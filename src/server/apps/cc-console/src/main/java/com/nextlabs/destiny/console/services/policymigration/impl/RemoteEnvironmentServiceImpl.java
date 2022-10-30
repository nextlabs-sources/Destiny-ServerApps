package com.nextlabs.destiny.console.services.policymigration.impl;

import com.nextlabs.destiny.console.dao.EntityAuditLogDao;
import com.nextlabs.destiny.console.dto.policyworkflow.RemoteEnvironmentDTO;
import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.enums.AuditableEntity;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ServerException;
import com.nextlabs.destiny.console.model.remoteenv.RemoteEnvironment;
import com.nextlabs.destiny.console.repositories.RemoteEnvironmentRepository;
import com.nextlabs.destiny.console.search.repositories.RemoteEnvironmentSearchRepository;
import com.nextlabs.destiny.console.services.AuditLogService;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.SSLManagerService;
import com.nextlabs.destiny.console.services.authentication.OidcAuthenticationClientService;
import com.nextlabs.destiny.console.services.policymigration.RemoteEnvironmentSearchService;
import com.nextlabs.destiny.console.services.policymigration.RemoteEnvironmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLHandshakeException;
import java.util.List;
import java.util.Optional;

import static com.nextlabs.destiny.console.enums.AuditLogComponent.REMOTE_ENV;

@Service
public class RemoteEnvironmentServiceImpl implements RemoteEnvironmentService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteEnvironmentServiceImpl.class);

    private final RemoteEnvironmentRepository remoteEnvironmentRepository;

    private RestTemplate sslRestTemplate;

    private OidcAuthenticationClientService oidcAuthenticationClientService;

    private SSLManagerService sslManagerService;

    private TextEncryptor textEncryptor;

    private RemoteEnvironmentSearchService remoteEnvironmentSearchService;

    private RemoteEnvironmentSearchRepository remoteEnvironmentSearchRepository;

    private AuditLogService auditService;

    private EntityAuditLogDao entityAuditLogDao;

    private MessageBundleService msgBundle;

    private static final String CSRF_TOKEN_ENDPOINT = "/cas/security/csrfToken";

    @Autowired
    public RemoteEnvironmentServiceImpl(RemoteEnvironmentRepository remoteEnvironmentRepository) {
        this.remoteEnvironmentRepository = remoteEnvironmentRepository;
    }

    @Override
    public RemoteEnvironmentDTO findById(Long id) {
        Optional<RemoteEnvironment> optionalRemoteEnvironments = remoteEnvironmentRepository.findById(id);
        return optionalRemoteEnvironments.map(env ->  RemoteEnvironmentDTO.getDTO(env, false)).orElse(null);
    }

    @Override
    public RemoteEnvironmentDTO findActiveById(Long id) {
        Optional<RemoteEnvironment> optionalRemoteEnvironment = remoteEnvironmentRepository.findByIdAndIsActiveTrue(id);
        return optionalRemoteEnvironment.map(env ->  RemoteEnvironmentDTO.getDTO(env, false)).orElse(null);
    }

    @Override
    public void validateConnection(RemoteEnvironmentDTO remoteEnvironmentDTO) throws ConsoleException, ServerException {
        sslRestTemplate.setRequestFactory(sslManagerService.getRequestFactory());
        try {
            sslRestTemplate.getForEntity(String.format("https://%s:%s%s", remoteEnvironmentDTO.getHost(),
                    remoteEnvironmentDTO.getPort(), CSRF_TOKEN_ENDPOINT),
                    String.class);
        } catch (RestClientException e) {
            if (e.getCause() instanceof SSLHandshakeException) {
                throw new ConsoleException(msgBundle.getText("server.remote.env.ssl.error"), e);
            }
            throw new ConsoleException(msgBundle.getText("server.remote.env.connection.error"), e);
        }
        try {
            oidcAuthenticationClientService.authenticate(remoteEnvironmentDTO);
        } catch (RestClientException e) {
            throw new ConsoleException(msgBundle.getText("server.remote.env.auth.error"), e);
        }
    }

    @Override
    public RemoteEnvironment create(RemoteEnvironmentDTO remoteEnvironmentDTO) throws ConsoleException {
        Optional<RemoteEnvironment> activeRemoteEnvByHost = remoteEnvironmentRepository.findByHostAndIsActiveTrue(remoteEnvironmentDTO.getHost());
        if (activeRemoteEnvByHost.isPresent()) {
            throw new ConsoleException(String.format("Remote environment with host %s already exists.", remoteEnvironmentDTO.getHost()));
        }
        Optional<RemoteEnvironment> activeRemoteEnvByName = remoteEnvironmentRepository.findByNameAndIsActiveTrue(remoteEnvironmentDTO.getName());
        if (activeRemoteEnvByName.isPresent()) {
            throw new ConsoleException(String.format("Remote environment with name %s already exists.", remoteEnvironmentDTO.getName()));
        }
        RemoteEnvironment remoteEnvironment = new RemoteEnvironment();
        remoteEnvironment.setIsActive(true);
        encryptPassword(remoteEnvironmentDTO);
        remoteEnvironment = remoteEnvironmentRepository.save(RemoteEnvironmentDTO.setEntityValues(remoteEnvironmentDTO, remoteEnvironment));

        remoteEnvironmentSearchService.reIndexRemoteEnvironment(remoteEnvironment);
        auditService.save(REMOTE_ENV.name(), "audit.new.remote.env",
                remoteEnvironment.getName());

        entityAuditLogDao.addEntityAuditLog(AuditAction.CREATE, AuditableEntity.ENVIRONMENT_CONFIGURATION.getCode(),
                remoteEnvironment.getId(), null, RemoteEnvironmentDTO.getDTO(remoteEnvironment, false).toAuditString());

        logger.info("New remote environment saved successfully, [Env Id :{}]",
                remoteEnvironment.getId());
        return remoteEnvironment;
    }

    @Override
    public RemoteEnvironment modify(RemoteEnvironmentDTO remoteEnvironmentDTO) throws ConsoleException {
        Optional<RemoteEnvironment> optionalRemoteEnvironment = remoteEnvironmentRepository.findByIdAndIsActiveTrue(remoteEnvironmentDTO.getId());
        RemoteEnvironment remoteEnvironment = optionalRemoteEnvironment.orElseThrow(() ->
                new ConsoleException(String.format("Remote Environment by ID %s not found.", remoteEnvironmentDTO.getId())));

        if (!remoteEnvironment.getHost().equalsIgnoreCase(remoteEnvironmentDTO.getHost())) {
            Optional<RemoteEnvironment> activeRemoteEnvByHost = remoteEnvironmentRepository.findByHostAndIsActiveTrue(remoteEnvironmentDTO.getHost());
            if (activeRemoteEnvByHost.isPresent()) {
                throw new ConsoleException(String.format("Remote environment with host %s already exists.", remoteEnvironmentDTO.getHost()));
            }
        }

        if (!remoteEnvironment.getName().equalsIgnoreCase(remoteEnvironmentDTO.getName())) {
            Optional<RemoteEnvironment> activeRemoteEnvByName = remoteEnvironmentRepository.findByNameAndIsActiveTrue(remoteEnvironmentDTO.getName());
            if (activeRemoteEnvByName.isPresent()) {
                throw new ConsoleException(String.format("Remote environment with name %s already exists.", remoteEnvironmentDTO.getName()));
            }
        }
        encryptPassword(remoteEnvironmentDTO);
        String snapshot = RemoteEnvironmentDTO.getDTO(remoteEnvironment, false).toAuditString();
        remoteEnvironment =  remoteEnvironmentRepository.save(RemoteEnvironmentDTO.setEntityValues(remoteEnvironmentDTO, remoteEnvironment));
        oidcAuthenticationClientService.removeEnvironmentIdToken(remoteEnvironment.getId());
        remoteEnvironmentSearchService.reIndexRemoteEnvironment(remoteEnvironment);
        auditService.save(REMOTE_ENV.name(), "audit.modify.remote.env",
                remoteEnvironment.getName());

        entityAuditLogDao.addEntityAuditLog(AuditAction.UPDATE, AuditableEntity.ENVIRONMENT_CONFIGURATION.getCode(),
                remoteEnvironment.getId(), snapshot, RemoteEnvironmentDTO.getDTO(remoteEnvironment, false).toAuditString());
        logger.info("Remote environment modified successfully, [Env Id :{}]",
                remoteEnvironment.getId());
        return remoteEnvironment;
    }

    @Override
    public void delete(List<Long> ids) throws ConsoleException {
        for (long id: ids) {
            Optional<RemoteEnvironment> optionalRemoteEnvironment = remoteEnvironmentRepository.findByIdAndIsActiveTrue(id);
            RemoteEnvironment remoteEnvironment = optionalRemoteEnvironment.orElseThrow(() -> new ConsoleException(String.format("Remote Environment by ID %s not found.", id)));

            String snapshot = RemoteEnvironmentDTO.getDTO(remoteEnvironment, false).toAuditString();
            remoteEnvironmentRepository.setAsInactive(id);
            oidcAuthenticationClientService.removeEnvironmentIdToken(id);
            remoteEnvironmentSearchRepository.deleteById(id);
            auditService.save(REMOTE_ENV.name(), "audit.delete.remote.env",
                    remoteEnvironment.getName());

            entityAuditLogDao.addEntityAuditLog(AuditAction.DELETE, AuditableEntity.ENVIRONMENT_CONFIGURATION.getCode(),
                    id, snapshot, null);

            logger.info("Remote environment removed successfully, [Env Id :{}]",
                    remoteEnvironment.getId());
        }
    }

    private void encryptPassword(RemoteEnvironmentDTO remoteEnvironmentDTO) {
        remoteEnvironmentDTO.setPassword(textEncryptor.encrypt(remoteEnvironmentDTO.getPassword()));
    }

    @Autowired
    public void setSslRestTemplate(RestTemplate sslRestTemplate) {
        this.sslRestTemplate = sslRestTemplate;
    }

    @Autowired
    public void setOidcAuthenticationClientService(OidcAuthenticationClientService oidcAuthenticationClientService) {
        this.oidcAuthenticationClientService = oidcAuthenticationClientService;
    }

    @Autowired
    public void setSslManagerService(SSLManagerService sslManagerService) {
        this.sslManagerService = sslManagerService;
    }

    @Autowired
    public void setReversibleEncryptor(TextEncryptor textEncryptor) {
        this.textEncryptor = textEncryptor;
    }

    @Autowired
    public void setRemoteEnvironmentSearchService(RemoteEnvironmentSearchService remoteEnvironmentSearchService) {
        this.remoteEnvironmentSearchService = remoteEnvironmentSearchService;
    }

    @Autowired
    public void setRemoteEnvironmentSearchRepository(RemoteEnvironmentSearchRepository remoteEnvironmentSearchRepository) {
        this.remoteEnvironmentSearchRepository = remoteEnvironmentSearchRepository;
    }

    @Autowired
    public void setAuditService(AuditLogService auditService) {
        this.auditService = auditService;
    }

    @Autowired
    public void setEntityAuditLogDao(EntityAuditLogDao entityAuditLogDao) {
        this.entityAuditLogDao = entityAuditLogDao;
    }

    @Autowired
    public void setMsgBundle(MessageBundleService msgBundle) {
        this.msgBundle = msgBundle;
    }
}
