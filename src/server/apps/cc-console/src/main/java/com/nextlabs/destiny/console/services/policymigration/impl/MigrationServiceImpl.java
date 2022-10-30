package com.nextlabs.destiny.console.services.policymigration.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextlabs.destiny.console.dao.EntityAuditLogDao;
import com.nextlabs.destiny.console.dto.policymgmt.porting.Node;
import com.nextlabs.destiny.console.dto.policymgmt.porting.PolicyPortingDTO;
import com.nextlabs.destiny.console.dto.policymgmt.porting.PolicyTree;
import com.nextlabs.destiny.console.dto.policyworkflow.MigrationExportRequestDTO;
import com.nextlabs.destiny.console.dto.policyworkflow.MigrationImportRequestDTO;
import com.nextlabs.destiny.console.enums.*;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ServerException;
import com.nextlabs.destiny.console.model.remoteenv.RemoteEnvironment;
import com.nextlabs.destiny.console.repositories.RemoteEnvironmentRepository;
import com.nextlabs.destiny.console.services.authentication.OidcAuthenticationClientService;
import com.nextlabs.destiny.console.services.policy.PolicyPortingService;
import com.nextlabs.destiny.console.services.policymigration.MigrationService;
import com.nextlabs.destiny.console.utils.PolicyPortingUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MigrationServiceImpl implements MigrationService {

    public static final String POLICY_IMPORT_ENDPOINT = "/console/api/v1/migration/import";

    @Value("${server.name}")
    private String serverName;

    private OidcAuthenticationClientService oidcAuthenticationClientService;

    private RemoteEnvironmentRepository remoteEnvironmentRepository;

    private PolicyPortingService policyPortingService;

    private RestTemplate sslRestTemplate;

    private EntityAuditLogDao entityAuditLogDao;

    @PostConstruct
    public void initialize() {
        String regex = "^https://(.*?)(:.*)*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(serverName);
        matcher.find();
        this.serverName = matcher.group(1);
    }

    @Override
    public void executeExport(MigrationExportRequestDTO migrationExportRequestDTO) throws ConsoleException, ServerException {
        Optional<RemoteEnvironment> optionalRemoteEnvironment = remoteEnvironmentRepository.findByIdAndIsActiveTrue(migrationExportRequestDTO.getDestinationEnvId());
        if (optionalRemoteEnvironment.isPresent()) {
            RemoteEnvironment remoteEnvironment = optionalRemoteEnvironment.get();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(oidcAuthenticationClientService.getIdToken(remoteEnvironment.getId()));

            PolicyPortingDTO policyPortingDTO = policyPortingService.prepareDataToExport(migrationExportRequestDTO.getExportEntityDTOS());
            MigrationImportRequestDTO migrationImportRequestDTO = new MigrationImportRequestDTO(policyPortingDTO, this.serverName);
            migrationImportRequestDTO.setMechanism(migrationExportRequestDTO.getMechanism());
            migrationImportRequestDTO.setCleanup(migrationExportRequestDTO.isCleanup());
            HttpEntity<MigrationImportRequestDTO> request = new HttpEntity<>(migrationImportRequestDTO, headers);
            try {
                sslRestTemplate.postForEntity(
                        String.format("https://%s:%s%s", remoteEnvironment.getHost(), remoteEnvironment.getPort(), POLICY_IMPORT_ENDPOINT), request, String.class);
                entityAuditLogDao.addEntityAuditLog(AuditAction.MIGRATION_EXPORT,
                        AuditableEntity.POLICY.getCode(),
                        -1L, prepareAuditString(policyPortingDTO, this.serverName, remoteEnvironment.getHost()),
                        null);
            } catch (ResourceAccessException resourceAccessException) {
                if (resourceAccessException.getCause() instanceof SocketTimeoutException) {
                    throw new ServerException(String.format("Connection to host %s timed out.", remoteEnvironment.getHost()), resourceAccessException);
                }
                throw new ServerException(String.format("Unable to reach host %s.", remoteEnvironment.getHost()), resourceAccessException);
            } catch (HttpClientErrorException httpClientErrorException) {
                if (httpClientErrorException.getStatusCode() == HttpStatus.FORBIDDEN) {
                    throw new ServerException(String.format("Insufficient permission for %s user %s to accept the policies.",
                            remoteEnvironment.getHost(),
                            remoteEnvironment.getUsername()),
                            httpClientErrorException);
                }
                throw new ServerException("Error while migrating policies.", httpClientErrorException);
            } catch (RestClientException restClientException) {
                throw new ServerException("Error while migrating policies.", restClientException);
            }
        } else {
            throw new ConsoleException(
                    String.format("Error while migrating policy, remote environment with ID %s doesn't exist.",
                            migrationExportRequestDTO.getDestinationEnvId()));
        }
    }

    @Override
    public PolicyPortingDTO executeImport(MigrationImportRequestDTO migrationImportRequestDTO) throws ConsoleException {
        try {
            PolicyPortingDTO policyPortingDTO = policyPortingService.validateAndImport(getByteArray(migrationImportRequestDTO.getPayload()),
                                                    PolicyPortingUtil.DataTransportationMode.PLAIN.name(),
                                                    migrationImportRequestDTO.getMechanism());
            policyPortingDTO.setMechanism(migrationImportRequestDTO.getPayload().getMechanism());
            if(migrationImportRequestDTO.isCleanup()) {
                policyPortingService.cleanup(policyPortingDTO);
            }
            entityAuditLogDao.addEntityAuditLog(AuditAction.MIGRATION_IMPORT,
                    AuditableEntity.POLICY.getCode(),
                    -1L, null,
                    prepareAuditString(policyPortingDTO, migrationImportRequestDTO.getSourceHostname(), this.serverName));
            return policyPortingDTO;
        } catch (CircularReferenceException e) {
            throw new ConsoleException(
                    "Error encountered while executing migration import request,", e);
        }
    }

    private byte[] getByteArray(PolicyPortingDTO policyExport) throws ConsoleException {
        byte[] exportData;
        try {
            ObjectMapper mapper = new ObjectMapper();
            exportData = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(policyExport);
        } catch (IOException e){
            throw new ConsoleException(
                    "Error encountered while exporting policies,", e);
        }
        return exportData;
    }

    private String prepareAuditString(PolicyPortingDTO policyExport, String sourceHost, String destinationHost) {

        JSONObject audit = new JSONObject();
        audit.put("Source Host", sourceHost);
        audit.put("Destination Host", destinationHost);
        audit.put("Policies", populatePolicyTree(policyExport.getPolicyTree()));
        audit.put("Mechanism", policyExport.getMechanism());
        return audit.toString(2);
    }

    private List<Map<String, Object>> populatePolicyTree(PolicyTree tree) {
        List<Map<String, Object>> policyList = new LinkedList<>();
        // root level folders
        for (Node node : tree.getRoot().getChildren()) {
            if (!node.isFolder()) {
                continue;
            }
            for (Node policyNode : node.getChildren()) {
                Map<String, Object> policy = new LinkedHashMap<>();
                policy.put("Name", policyNode.getData().getName());
                policy.put("Full Name", policyNode.getData().getFullName());
                policy.put("Sub Policy List", populateSubPolicyTree(policyNode));
                policyList.add(policy);
            }
        }
        return policyList;
    }

    private List<Map<String, Object>> populateSubPolicyTree(Node node) {
        if (node.getChildren().isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> subPolicyList = new LinkedList<>();
        for (Node subNode : node.getChildren()){
            Map<String, Object> subPolicy = new LinkedHashMap<>();
            subPolicy.put("Name", subNode.getData().getName());
            subPolicy.put("Full Name", subNode.getData().getFullName());
            subPolicy.put("Sub Policy List", populateSubPolicyTree(subNode));
            subPolicyList.add(subPolicy);
        }
        return subPolicyList;
    }

    @Autowired
    public void setOidcAuthenticationClientService(OidcAuthenticationClientService oidcAuthenticationClientService) {
        this.oidcAuthenticationClientService = oidcAuthenticationClientService;
    }

    @Autowired
    public void setRemoteEnvironmentRepository(RemoteEnvironmentRepository remoteEnvironmentRepository) {
        this.remoteEnvironmentRepository = remoteEnvironmentRepository;
    }

    @Autowired
    public void setPolicyPortingService(PolicyPortingService policyPortingService) {
        this.policyPortingService = policyPortingService;
    }

    @Autowired
    public void setSslRestTemplate(RestTemplate sslRestTemplate) {
        this.sslRestTemplate = sslRestTemplate;
    }

    @Autowired
    public void setEntityAuditLogDao(EntityAuditLogDao entityAuditLogDao) {
        this.entityAuditLogDao = entityAuditLogDao;
    }
}
