package com.nextlabs.destiny.console.services.authentication.impl;

import com.nextlabs.destiny.console.dto.authentication.OidcJwtToken;
import com.nextlabs.destiny.console.dto.policyworkflow.RemoteEnvironmentDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ServerException;
import com.nextlabs.destiny.console.model.remoteenv.RemoteEnvironment;
import com.nextlabs.destiny.console.repositories.RemoteEnvironmentRepository;
import com.nextlabs.destiny.console.services.authentication.OidcAuthenticationClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OidcAuthenticationClientServiceImpl implements OidcAuthenticationClientService {

    private static final String GRANT_TYPE_FIELD = "grant_type";
    private static final String GRANT_TYPE_VALUE = "password";
    private static final String CLIENT_ID_FIELD = "client_id";
    private static final String USERNAME_FIELD = "username";
    private static final String PASSWORD_FIELD = "password";
    private static final String OIDC_ENDPOINT = "/cas/oidc/accessToken";

    Map<Long, OidcJwtToken> jwtTokenMap;
    private RestTemplate sslRestTemplate;
    private final RemoteEnvironmentRepository remoteEnvironmentRepository;
    private TextEncryptor textEncryptor;

    public OidcAuthenticationClientServiceImpl(RemoteEnvironmentRepository remoteEnvironmentRepository) {
        this.remoteEnvironmentRepository = remoteEnvironmentRepository;
        this.jwtTokenMap = new ConcurrentHashMap<>();
    }

    @Override
    public String getIdToken(long targetEnvId) throws ConsoleException, ServerException {
        OidcJwtToken oidcJwtToken = jwtTokenMap.get(targetEnvId);
        if (oidcJwtToken != null && oidcJwtToken.isActive()) {
            return oidcJwtToken.getIdToken();
        } else {
            OidcJwtToken newOidcJwtToken = authenticate(targetEnvId);
            newOidcJwtToken.setIssuedAt(System.currentTimeMillis());
            jwtTokenMap.put(targetEnvId, newOidcJwtToken);
            return newOidcJwtToken.getIdToken();
        }
    }

    private OidcJwtToken authenticate(long targetEnvId) throws ConsoleException, ServerException {
        Optional<RemoteEnvironment> optionalRemoteEnvironment = remoteEnvironmentRepository.findByIdAndIsActiveTrue(targetEnvId);
        if (optionalRemoteEnvironment.isPresent()) {
            RemoteEnvironment remoteEnvironment = optionalRemoteEnvironment.get();
            return authenticate(RemoteEnvironmentDTO.getDTO(remoteEnvironment, true));
        } else {
            throw new ConsoleException(String.format("Target environment wih id %s not found", targetEnvId));
        }
    }

    @Override
    public OidcJwtToken authenticate(RemoteEnvironmentDTO remoteEnvironmentDTO) throws ServerException {
        String password;
        if(remoteEnvironmentDTO.getPassword() != null && remoteEnvironmentDTO.getPassword().startsWith("{cipher}")) {
            password = textEncryptor.decrypt(remoteEnvironmentDTO.getPassword());
        } else {
            password = remoteEnvironmentDTO.getPassword();
        }
        MultiValueMap<String, String> authMap = new LinkedMultiValueMap<>();
        authMap.add(GRANT_TYPE_FIELD, GRANT_TYPE_VALUE);
        authMap.add(USERNAME_FIELD, remoteEnvironmentDTO.getUsername());
        authMap.add(PASSWORD_FIELD, password);
        authMap.add(CLIENT_ID_FIELD, remoteEnvironmentDTO.getClientId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> authRequest = new HttpEntity<>(authMap, headers);
        try {
            return sslRestTemplate.postForObject(
                    String.format("https://%s:%s%s", remoteEnvironmentDTO.getHost(), remoteEnvironmentDTO.getPort(), OIDC_ENDPOINT),
                    authRequest, OidcJwtToken.class);
        } catch (ResourceAccessException resourceAccessException) {
            throw new ServerException(String.format("Unable to reach host %s", remoteEnvironmentDTO.getHost()), resourceAccessException);
        }
    }

    @Override
    public void removeEnvironmentIdToken(long targetEnvId) {
        jwtTokenMap.remove(targetEnvId);
    }

    @Autowired
    public void setSslRestTemplate(RestTemplate sslRestTemplate) {
        this.sslRestTemplate = sslRestTemplate;
    }

    @Autowired
    public void setTextEncryptor(TextEncryptor textEncryptor) {
        this.textEncryptor = textEncryptor;
    }
}
