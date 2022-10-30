/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 14, 2016
 *
 */
package com.nextlabs.destiny.console.services.policy.impl;

import com.bluejungle.framework.domain.IHasId;
import com.bluejungle.pf.destiny.lib.DTOUtils;
import com.bluejungle.pf.destiny.lib.LeafObjectType;
import com.bluejungle.pf.destiny.parser.PQLException;
import com.bluejungle.pf.domain.epicenter.common.SpecType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.nextlabs.destiny.console.config.properties.DataTransportationProperties;
import com.nextlabs.destiny.console.config.properties.KeyStoreProperties;
import com.nextlabs.destiny.console.config.properties.TrustStoreProperties;
import com.nextlabs.destiny.console.dao.EntityAuditLogDao;
import com.nextlabs.destiny.console.dao.policy.OperatorConfigDao;
import com.nextlabs.destiny.console.dao.policy.PolicyDevelopmentEntityDao;
import com.nextlabs.destiny.console.dao.policy.PolicyModelDao;
import com.nextlabs.destiny.console.dto.common.TagDTO;
import com.nextlabs.destiny.console.dto.comparable.ComparableComponent;
import com.nextlabs.destiny.console.dto.comparable.ComparablePolicy;
import com.nextlabs.destiny.console.dto.comparable.ComparablePolicyModel;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.ExportEntityDTO;
import com.nextlabs.destiny.console.dto.policymgmt.FolderDTO;
import com.nextlabs.destiny.console.dto.policymgmt.MemberCondition;
import com.nextlabs.destiny.console.dto.policymgmt.MemberDTO;
import com.nextlabs.destiny.console.dto.policymgmt.ObligationDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyComponent;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.SubComponentLite;
import com.nextlabs.destiny.console.dto.policymgmt.SubPolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.XacmlPolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.XacmlPolicyLite;
import com.nextlabs.destiny.console.dto.policymgmt.porting.EbinDTO;
import com.nextlabs.destiny.console.dto.policymgmt.porting.Node;
import com.nextlabs.destiny.console.dto.policymgmt.porting.PolicyExportOptionsDTO;
import com.nextlabs.destiny.console.dto.policymgmt.porting.PolicyPortingDTO;
import com.nextlabs.destiny.console.dto.policymgmt.porting.PolicyTree;
import com.nextlabs.destiny.console.enums.ActionType;
import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.enums.AuditableEntity;
import com.nextlabs.destiny.console.enums.DelegationModelShortName;
import com.nextlabs.destiny.console.enums.DevEntityType;
import com.nextlabs.destiny.console.enums.FolderType;
import com.nextlabs.destiny.console.enums.ImportMechanism;
import com.nextlabs.destiny.console.enums.Operator;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.enums.PolicyStatus;
import com.nextlabs.destiny.console.enums.Status;
import com.nextlabs.destiny.console.enums.TagType;
import com.nextlabs.destiny.console.exceptions.CircularReferenceException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.exceptions.ForbiddenException;
import com.nextlabs.destiny.console.exceptions.InvalidPolicyPortingRequestException;
import com.nextlabs.destiny.console.exceptions.NoDataFoundException;
import com.nextlabs.destiny.console.exceptions.ServerException;
import com.nextlabs.destiny.console.init.ConfigurationDataLoader;
import com.nextlabs.destiny.console.model.TagLabel;
import com.nextlabs.destiny.console.model.policy.ActionConfig;
import com.nextlabs.destiny.console.model.policy.AttributeConfig;
import com.nextlabs.destiny.console.model.policy.ObligationConfig;
import com.nextlabs.destiny.console.model.policy.OperatorConfig;
import com.nextlabs.destiny.console.model.policy.ParameterConfig;
import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;
import com.nextlabs.destiny.console.model.policy.PolicyModel;
import com.nextlabs.destiny.console.policy.pql.helpers.ComponentPQLHelper;
import com.nextlabs.destiny.console.repositories.PolicyDevelopmentEntityRepository;
import com.nextlabs.destiny.console.repositories.PolicyModelRepository;
import com.nextlabs.destiny.console.search.repositories.ComponentSearchRepository;
import com.nextlabs.destiny.console.search.repositories.PolicyModelSearchRepository;
import com.nextlabs.destiny.console.search.repositories.PolicySearchRepository;
import com.nextlabs.destiny.console.search.repositories.TagLabelSearchRepository;
import com.nextlabs.destiny.console.search.repositories.XacmlPolicySearchRepository;
import com.nextlabs.destiny.console.services.MemberService;
import com.nextlabs.destiny.console.services.MessageBundleService;
import com.nextlabs.destiny.console.services.TagLabelService;
import com.nextlabs.destiny.console.services.delegadmin.AccessControlService;
import com.nextlabs.destiny.console.services.policy.ComponentMgmtService;
import com.nextlabs.destiny.console.services.policy.ComponentMgmtService.CheckCircularRefs;
import com.nextlabs.destiny.console.services.policy.FolderService;
import com.nextlabs.destiny.console.services.policy.OperatorConfigService;
import com.nextlabs.destiny.console.services.policy.PolicyDevelopmentEntityMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyMgmtService;
import com.nextlabs.destiny.console.services.policy.PolicyModelService;
import com.nextlabs.destiny.console.services.policy.PolicyPortingService;
import com.nextlabs.destiny.console.utils.PolicyPortingUtil;
import com.nextlabs.destiny.console.utils.PolicyPortingUtil.DataTransportationMode;
import com.nextlabs.pf.destiny.formatter.PDFDomainObjectFormatter;
import com.nextlabs.pf.destiny.formatter.XACMLDomainObjectFormatter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 *
 * Implementation of the {@link PolicyPortingService}
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class PolicyPortingServiceImpl implements PolicyPortingService {

    private static final Logger log = LoggerFactory
            .getLogger(PolicyPortingServiceImpl.class);

    @Resource
    private PolicySearchRepository policySearchRepository;

    @Resource
    private PolicyModelSearchRepository policyModelSearchRepository;

    @Resource
    private ComponentSearchRepository componentSearchRepository;

    @Resource
    private TagLabelSearchRepository tagLabelSearchRepository;

    @Resource
    XacmlPolicySearchRepository xacmlPolicySearchRepository;

    @Autowired
    private PolicyDevelopmentEntityRepository policyDevelopmentEntityRepository;

    @Autowired
    private PolicyDevelopmentEntityDao policyDevelopmentEntityDao;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TagLabelService tagLabelService;

    @Autowired
    private ComponentMgmtService componentMgmtService;

    @Autowired
    private PolicyMgmtService policyMgmtService;

    @Autowired
    private PolicyModelService policyModelService;

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private OperatorConfigDao operatorConfigDao;

    @Autowired
    private OperatorConfigService operatorConfigService;

    @Autowired
    private ConfigurationDataLoader configDataLoader;

    @Autowired
    private DataTransportationProperties dataTransportationProperties;

    @Autowired
    private KeyStoreProperties keyStoreProperties;

    @Autowired
    private TrustStoreProperties trustStoreProperties;

    @Autowired
    private MessageBundleService msgBundle;

    @Autowired
    private PolicyModelDao policyModelDao;

    @Autowired
    private PolicyModelRepository policyModelRepository;

    @Autowired
   	private MemberService memberService;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EntityAuditLogDao entityAuditLogDao;

    @Autowired
    private PolicyDevelopmentEntityMgmtService devEntityMgmtService;

    @Override
    public PolicyPortingDTO validateAndImport(byte[] bytes)
    		throws ConsoleException, CircularReferenceException {
    	return validateAndImport(bytes, DataTransportationMode.PLAIN.name());
    }

    @Override
    public PolicyPortingDTO validateAndImport(byte[] bytes, String importMode)
            throws ConsoleException, CircularReferenceException {
        return validateAndImport(bytes, importMode, ImportMechanism.PARTIAL);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PolicyPortingDTO validateAndImport(byte[] bytes, String importMode, ImportMechanism importMechanism)
            throws ConsoleException, CircularReferenceException {
        try {
        	validatePolicyImportRequest(importMode);        	
        	ObjectMapper mapper = new ObjectMapper();
        	PolicyPortingDTO policyPort;
        	if (DataTransportationMode.SANDE == DataTransportationMode.valueOf(importMode)) {        		
        		String policyBundle = validateSignatureAndExtractPolicyBundle(bytes);
        		policyPort = mapper.readValue(policyBundle.getBytes(),
                        PolicyPortingDTO.class);
        	} else {
        		policyPort = mapper.readValue(bytes, PolicyPortingDTO.class);
        	}
            policyPort.setMechanism(importMechanism);
            log.info("Policy porting data received, {}", policyPort);

            Map<Long, Long> policyModelIdMapForImportedComps = importPolicyModels(policyPort);
            Set<Long> importedPolicyModelIds = new HashSet<>();
            policyModelIdMapForImportedComps.forEach((key, value) -> importedPolicyModelIds.add(value));

            Map<String, Long> componentFolders = new HashMap<>();
            Set<String> componentFoldersInLowerCase = new HashSet<>();
            if(CollectionUtils.isNotEmpty(policyPort.getComponentFolders())) {
                for (String folderPath : policyPort.getComponentFolders()) {
                    componentFolders.put(folderPath, folderService.createFolderPath(FolderType.COMPONENT, folderPath));
                    registerFolders(componentFoldersInLowerCase, folderPath);
                }
            }
            Map<Long, ComponentDTO> componentIdMap = new HashMap<>();
            importComponents(policyPort, policyModelIdMapForImportedComps, componentIdMap, componentFolders, componentFoldersInLowerCase);
            Set<Long> importedComponentIds = new HashSet<>();
            componentIdMap.forEach((key, value) -> importedComponentIds.add(value.getId()));

            Map<String, Long> policyFolders = new HashMap<>();
            Set<String> policyFoldersInLowerCase = new HashSet<>();
            if(CollectionUtils.isNotEmpty(policyPort.getPolicyFolders())) {
                for (String folderPath : policyPort.getPolicyFolders()) {
                    policyFolders.put(folderPath, folderService.createFolderPath(FolderType.POLICY, folderPath));
                    registerFolders(policyFoldersInLowerCase, folderPath);
                }
            }
            importPolicies(policyPort, componentIdMap, policyModelIdMapForImportedComps, policyFolders, policyFoldersInLowerCase);

            policyPort.setImportedPolicyIdSet(new HashSet<>(policyPort.getImportedPolicyIds()));
            policyPort.setImportedPolicyFolderSet(policyFoldersInLowerCase);
            policyPort.setImportedComponentIdSet(importedComponentIds);
            policyPort.setImportedComponentFolderSet(componentFoldersInLowerCase);
            policyPort.setImportedPolicyModelIdSet(importedPolicyModelIds);

            return policyPort;
        } catch (IOException e) {
            throw new ConsoleException(
                    "Error encountered while validate and importing data,", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void cleanup(PolicyPortingDTO policyPortingDTO)
            throws ConsoleException {
        removeNonImportingPolicies(policyPortingDTO.getImportedPolicyIdSet());
        removeNonImportingFolders(FolderType.POLICY, policyPortingDTO.getImportedPolicyFolderSet());
        removeNonImportingComponents(policyPortingDTO.getImportedComponentIdSet());
        removeNonImportingFolders(FolderType.COMPONENT, policyPortingDTO.getImportedComponentFolderSet());
        removeNonImportingPolicyModels(policyPortingDTO.getImportedPolicyModelIdSet());
        folderService.reIndexAllFolders();
    }

    public byte[] validateAndExport(List<PolicyLite> policyLites, String exportMode) throws ConsoleException, JsonProcessingException {
        validatePolicyExportRequest(exportMode);
        List<ExportEntityDTO> exportEntityDTOS = policyLites.stream()
                .map(ExportEntityDTO::new)
                .collect(Collectors.toList());
        exportEntityDTOS.addAll(folderService.all(FolderType.POLICY).stream()
                .filter(folderDTO -> folderDTO.getId() > -1)
                .map(ExportEntityDTO::new)
                .collect(Collectors.toList()));
        exportEntityDTOS.addAll(folderService.all(FolderType.COMPONENT).stream()
                .filter(folderDTO -> folderDTO.getId() > -1)
                .map(ExportEntityDTO::new)
                .collect(Collectors.toList()));
        PolicyPortingDTO policyExport = prepareDataToExport(exportEntityDTOS);
        ObjectMapper mapper = new ObjectMapper();
        byte[] exportData;
        if (DataTransportationMode.SANDE == DataTransportationMode.valueOf(exportMode)) {
            exportData = generateEncryptedExportData (mapper.writerWithDefaultPrettyPrinter().writeValueAsString(policyExport));
        } else {
            exportData = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(policyExport);
        }
        return exportData;
    }

    @Override
    public String exportAll(List<PolicyLite> policyLites, String exportMode)
            throws ConsoleException {
        log.info("Export all policies processing started...");
        try {
            byte[] exportData = validateAndExport(policyLites, exportMode);
            String fileName = msgBundle
                    .getText("policy.mgmt.export.file.all.prefix")
                    + System.currentTimeMillis() + getFileExtension(exportMode);
            String fileLocation = configDataLoader
                    .getPolicyExportsFileLocation() + File.separator + "Policy" + File.separator + fileName;
            File destDirectory = new File(configDataLoader.getPolicyExportsFileLocation() + File.separator + "Policy");
            if(!destDirectory.exists()) {
                destDirectory.mkdirs();
            }

            Files.write(Paths.get(fileLocation), exportData);
            log.info("All Policy data exported successfully to :{}", fileName);
            return fileName;
        } catch (IOException e) {
            throw new ConsoleException(
                    "Error encountered while writing to file", e);
        }
    }

    @Override
    public String exportAsFile(List<ExportEntityDTO> exportEntityDTOS, String exportMode)
    		throws ConsoleException {
        PolicyPortingDTO policyExport = prepareDataToExport(exportEntityDTOS);
        try {
        	validatePolicyExportRequest(exportMode);
        	
        	ObjectMapper mapper = new ObjectMapper();
            byte[] exportData;
			String fileExtension;
			if (DataTransportationMode.SANDE == DataTransportationMode.valueOf(exportMode)) {
				exportData = generateEncryptedExportData (mapper.writerWithDefaultPrettyPrinter().writeValueAsString(policyExport));				
				fileExtension = PolicyPortingUtil.FILE_EXTENSION_EBIN;
			} else {
				exportData = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(policyExport);
				fileExtension = PolicyPortingUtil.FILE_EXTENSION_BIN;
			}

            String fileName = msgBundle
                    .getText("policy.mgmt.export.file.prefix")
                    + System.currentTimeMillis() + fileExtension;
            String fileLocation = configDataLoader
                    .getPolicyExportsFileLocation() + File.separator + "Policy" + File.separator + fileName;
            File destDirectory = new File(configDataLoader.getPolicyExportsFileLocation() + File.separator + "Policy");
            if(!destDirectory.exists()) {
                destDirectory.mkdirs();
            }

            Files.write(Paths.get(fileLocation), exportData);
            log.info("Policy data exported successfully to :{}", fileName);
            return fileName;
        } catch (IOException e) {
            throw new ConsoleException(
                    "Error encountered while writing to file", e);
        }
    }

    @Override
    public List<String> exportXacmlPolicy(List<Long> ids) throws ConsoleException {
        List<String> exportPaths = new ArrayList<>();
        try {
            for (long id : ids) {
                XacmlPolicyDTO policy = new XacmlPolicyDTO();

                PolicyDevelopmentEntity devEntity = devEntityMgmtService.findById(id);
                Optional<XacmlPolicyLite> optionalXacmlPolicyLite = xacmlPolicySearchRepository.findById(id);
                if (devEntity == null || !optionalXacmlPolicyLite.isPresent()){
                    throw new NoDataFoundException(
                            msgBundle.getText("no.data.found.code"),
                            msgBundle.getText("no.data.found"));
                }
                XacmlPolicyLite xacmlPolicyLite = optionalXacmlPolicyLite.get();
                String type = xacmlPolicyLite.getDocumentType().equals("XACML POLICY")? "Policy_": "PolicySet_";
                String fileName = msgBundle
                        .getText("policy.xacml.mgmt.export.file.prefix") + type
                        + System.currentTimeMillis() + PolicyPortingUtil.XACML_FILE_EXTENSION;
                String fileLocation = configDataLoader
                        .getPolicyExportsFileLocation() + File.separator + "XacmlPolicy" + File.separator + fileName;
                File destDirectory = new File(configDataLoader.getPolicyExportsFileLocation() + File.separator + "XacmlPolicy");
                if (!destDirectory.exists()) {
                    destDirectory.mkdirs();
                }
                Files.write(Paths.get(fileLocation), devEntity.getPql().getBytes());
                exportPaths.add(fileName);

                policy.setPolicyName(devEntity.getTitle());
                policy.setDescription(devEntity.getDescription());

                entityAuditLogDao.addEntityAuditLog(AuditAction.EXPORT,
                        AuditableEntity.XACML_POLICY.getCode(),
                        devEntity.getId(), null,
                        policy.toAuditString());
            }
        } catch (IOException e) {
            throw new ConsoleException("Error encountered while writing to file", e);
        }
        return exportPaths;
    }

    @Override
    public PolicyExportOptionsDTO getExportOptions() throws ConsoleException {
    	try {
    		DataTransportationMode configuredMode = DataTransportationMode.valueOf(dataTransportationProperties.getMode());
    		PolicyExportOptionsDTO dto = new PolicyExportOptionsDTO();
    		dto.setSandeEnabled(DataTransportationMode.SANDE == configuredMode);
    		dto.setPlainTextEnabled(DataTransportationMode.PLAIN == configuredMode || dataTransportationProperties.isAllowPlainTextExport());
    		return dto;
    	} catch (Exception e) {
    		throw new ConsoleException(
                    "Error encountered while getting policy export options,", e);
    	}
    }

    @Override
    public String getFileExtension(String exportMode) {
        if (DataTransportationMode.SANDE == DataTransportationMode.valueOf(exportMode)) {
            return PolicyPortingUtil.FILE_EXTENSION_EBIN;
        } else {
            return PolicyPortingUtil.FILE_EXTENSION_BIN;
        }
    }

    /**
     * if a policy should be exported. return true if this policy is a root policy or none of its successor policy is not exported
     * 
     * @param policyLite
     * @param policyIdSet
     * @return
     */
    private boolean shouldExportPolicy(PolicyLite policyLite, Set<Long> policyIdSet) {
        while (policyLite != null && policyLite.isHasParent()) {
            // if any of its successor policy is exported, skip current one
            if (policyIdSet.contains(policyLite.getParentPolicy().getId()))
                return false;
            else
                policyLite = policySearchRepository.findById(policyLite.getParentPolicy().getId()).orElse(null);
        }
        return policyLite != null;
    }

    public PolicyPortingDTO prepareDataToExport(List<ExportEntityDTO> exportEntityDTOS)
            throws ConsoleException {
        Set<Long> policyIdsToExport = new TreeSet<>();
        Set<Long> componentIdsToExport = new TreeSet<>();
        Set<Long> policyModelIdsToExport = new TreeSet<>();
        Set<String> policyFolders = new HashSet<>();
        Set<String> componentFolders = new HashSet<>();

        PolicyPortingDTO policyExportData = new PolicyPortingDTO();

        Set<Long> policyIdSet = exportEntityDTOS.stream()
                .filter(exportEntityDTO -> DevEntityType.POLICY.equals(exportEntityDTO.getEntityType()))
                .map(ExportEntityDTO::getId)
                .collect(Collectors.toSet());
        exportEntityDTOS.stream()
                .filter(exportEntityDTO -> DevEntityType.FOLDER.equals(exportEntityDTO.getEntityType()))
                .forEach(exportEntityDTO -> {
                    List<FolderDTO> folderDTOS = folderService.findAllSubFolders(exportEntityDTO.getId());
                    for (FolderDTO folderDTO : folderDTOS) {

                        try {
                            accessControlService.authorizeByTags(ActionType.VIEW,
                                    FolderType.POLICY.equals(folderDTO.getType()) ?
                                            DelegationModelShortName.POLICY_FOLDER_ACCESS_TAGS :
                                            DelegationModelShortName.COMPONENT_FOLDER_ACCESS_TAGS,
                                    folderDTO,
                                    false);
                        } catch (ConsoleException e) {
                            continue;
                        }
                        if (FolderType.POLICY.equals(folderDTO.getType())) {
                            policyFolders.add(folderDTO.getFolderPath());
                            policyIdSet.addAll(policyDevelopmentEntityRepository
                                    .findByFolderIdAndType(folderDTO.getId(), DevEntityType.POLICY.getKey()).stream()
                                    .map(PolicyDevelopmentEntity::getId)
                                    .collect(Collectors.toList()));
                        } else {
                            componentFolders.add(folderDTO.getFolderPath());
                        }
                    }
                });
        policyExportData.getPolicyFolders().addAll(policyFolders);
        policyExportData.getComponentFolders().addAll(componentFolders);

        for (Long id : policyIdSet) {
            PolicyLite policyLite = policySearchRepository.findById(id).orElse(null);
            if (shouldExportPolicy(policyLite, policyIdSet)) {
                PolicyDTO policyDTO = policyMgmtService.findActiveById(id);
                boolean unauthorized = false;
                try {
                    accessControlService.authorizeByTags(ActionType.VIEW,
                            DelegationModelShortName.POLICY_ACCESS_TAGS,
                            policyDTO,
                            false);
                } catch (ForbiddenException e) {
                    unauthorized = true;
                }
                if (unauthorized || policyExportData.getPolicyTree().checkNodeExists(new Node(policyDTO))) {
                    continue;
                }
                preparePolicy(policyDTO);
                Node rootPolicyNode = policyExportData.getPolicyTree().addNode(policyDTO);
                policyIdsToExport.add(id);
                addSubPolicyRecursively(rootPolicyNode, policyExportData,
                        policyIdsToExport, policyLite);
            }
        }

        // Select component and sub-components and related policy models
        for (Long id : policyIdsToExport) {
            PolicyDTO policyDTO = policyMgmtService.findById(id);

            addComponentIds(componentIdsToExport, policyDTO.getSubjectComponents());
            addComponentIds(componentIdsToExport, policyDTO.getToSubjectComponents());
            addComponentIds(componentIdsToExport, policyDTO.getActionComponents());
            addComponentIds(componentIdsToExport, policyDTO.getFromResourceComponents());
            addComponentIds(componentIdsToExport, policyDTO.getToResourceComponents());
        }

        Map<Long, List<Long>> componentSubCompMap = policyExportData.getComponentToSubCompMap();
        Set<Long> componentIdsToExportSet = new TreeSet<>();
        for (Long id : componentIdsToExport) {
            addSubComponentRecursively(componentSubCompMap,
                    componentIdsToExportSet, policyModelIdsToExport, id);
        }
        componentIdsToExport.addAll(componentIdsToExportSet);

        for (Long id : policyModelIdsToExport) {
            PolicyModel policyModel = policyModelService.findActivePolicyModelById(id);
            if (policyModel == null) {
                log.info("No active model found for Id :{}", id);
                continue;
            }
            policyExportData.getPolicyModels().add(policyModel);
        }

        for (Long id : componentIdsToExport) {
            ComponentDTO componentDTO = componentMgmtService.findActiveById(id);
            if (componentDTO == null) {
                log.info("No active component found for Id :{}", id);
                continue;
            }
            prepareComponent(componentDTO);
            policyExportData.getComponents().add(componentDTO);
        }

        log.info(
                "Policy export data created successfully, [ Export data summary :{}]",
                policyExportData);
        return policyExportData;
    }

    private void prepareComponent(ComponentDTO componentDTO) {
        componentDTO.setFolderId(null);
    }

    private void addSubComponentRecursively(
            Map<Long, List<Long>> compToSubCompMap,
            Set<Long> componentIdsToExport, Set<Long> policyModelIdsToExport,
            Long id) {
        componentSearchRepository.findById(id)
                .ifPresent(componentLite -> {
                    policyModelIdsToExport.add(componentLite.getModelId());
                    List<Long> subComps = compToSubCompMap.get(id);
                    if (subComps == null) {
                        subComps = new ArrayList<>();
                        compToSubCompMap.put(id, subComps);
                    }
                    // add sub components
                    for (SubComponentLite subComponent : componentLite.getSubComponents()) {
                        componentIdsToExport.add(subComponent.getId());
                        subComps.add(subComponent.getId());
                        addSubComponentRecursively(compToSubCompMap, componentIdsToExport,
                                policyModelIdsToExport, subComponent.getId());
                    }
                });
    }

    private void addComponentIds(Set<Long> componentIdsToExport,
                                 List<PolicyComponent> components) {
        for (PolicyComponent component : components) {
            for (ComponentDTO componentDTO : component.getComponents()) {
                componentIdsToExport.add(componentDTO.getId());
            }
        }
    }

    private void addSubPolicyRecursively(Node parentNode,
            PolicyPortingDTO policyExportData, Set<Long> policyIdsToExport,
            PolicyLite policyLite) throws ConsoleException {
        for (SubPolicyLite child : policyLite.getSubPolicies()) {
            policyIdsToExport.add(child.getId());
            policyLite = policySearchRepository.findById(child.getId()).orElse(null);
            PolicyDTO policyDTO = policyMgmtService
                    .findActiveById(child.getId());
            policyDTO.setFullName(
                    parentNode.getPath() + "/" + policyDTO.getName());
            preparePolicy(policyDTO);
            Node subParentNode = policyExportData.getPolicyTree().addNode(policyDTO);
            addSubPolicyRecursively(subParentNode, policyExportData,
                                    policyIdsToExport, policyLite);
        }
    }

    private void importPolicies(PolicyPortingDTO policyPort, Map<Long, ComponentDTO> componentIdMap,
                                Map<Long, Long> policyModelIdMap, Map<String, Long> policyFolders, Set<String> policyFoldersInLowerCase)
            throws ConsoleException {
        PolicyTree tree = policyPort.getPolicyTree();

        for (Node node : tree.getRoot().getChildren()) {
            if (!node.isFolder()) {
                continue;
            }

            for (Node policyNode : node.getChildren()) {
                PolicyDTO policy = policyNode.getData();
                List<PolicyLite> policyLiteList = policyMgmtService.getPolicyByName(policy.getName());

                policy.setId(null);
                policy.getSubPolicyRefs().clear();
                
                Set<TagDTO> tags = policy.getTags();
                createTagIfNotExists(tags);
                policy.setTags(tags);
                
                updateComponentRefs(componentIdMap, policy.getActionComponents());
                updateComponentRefs(componentIdMap, policy.getSubjectComponents());
                updateComponentRefs(componentIdMap, policy.getToSubjectComponents());
                updateComponentRefs(componentIdMap, policy.getFromResourceComponents());
                updateComponentRefs(componentIdMap, policy.getToResourceComponents());
                
                for (ObligationDTO obligationDTO : policy.getAllowObligations()){
                	Long importingModelId = obligationDTO.getPolicyModelId();
                	if (!(importingModelId.compareTo(0L) == 0)){
                		obligationDTO.setPolicyModelId(policyModelIdMap.get(importingModelId));
                	}
                }
                for (ObligationDTO obligationDTO : policy.getDenyObligations()){
                	Long importingModelId = obligationDTO.getPolicyModelId();
                	if (!(importingModelId.compareTo(0L) == 0)){
                		obligationDTO.setPolicyModelId(policyModelIdMap.get(importingModelId));
                	}
                }
                policy.setStatus(PolicyStatus.DRAFT.name());
                if(policy.getFolderPath() != null && !policyFolders.containsKey(policy.getFolderPath())) {
                    policyFolders.put(policy.getFolderPath(), folderService.createFolderPath(FolderType.POLICY, policy.getFolderPath()));
                    registerFolders(policyFoldersInLowerCase, policy.getFolderPath());
                }
                policy.setFolderId(policyFolders.get(policy.getFolderPath()));
                if(!policyLiteList.isEmpty()) {
                    PolicyDTO databaseRecord = policyMgmtService.findById(policyLiteList.get(0).getId());
                    if(new ComparablePolicy(policy, policyNode).equals(new ComparablePolicy(databaseRecord, null))) {
                        policyPort.getImportedPolicyIds().add(databaseRecord.getId());
                        addSubPolicy(policyPort, componentIdMap, policyNode, databaseRecord, policyModelIdMap);
                        continue;
                    } else {
                        PolicyLite policyLite = policyLiteList.get(0);
                        policy.setId(policyLite.getId());
                        policy.setFullName(null);
                        policy.setVersion(-1);
                        policy.setReIndexNow(false);
                        policyMgmtService.modify(policy);
                    }
                } else {
                    policy.setReIndexNow(false);
            	    policy = policyMgmtService.save(policy);
                }
  
                policyPort.getImportedPolicyIds().add(policy.getId());
                addSubPolicy(policyPort, componentIdMap, policyNode, policy, policyModelIdMap);
            }
        }
    }

    private void registerFolders(Set folders, String folderPath) {
        List<String> folderNamesInPath = Arrays.stream(folderPath.toLowerCase().split("/"))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());

        for(int i = 0; i < folderNamesInPath.size(); i++) {
            folders.add(String.join("/", folderNamesInPath.subList(0, i + 1)));
        }
    }

    private void removeNonImportingPolicies(Set<Long> importingPolicyIds)
            throws ConsoleException {
        List<PolicyDevelopmentEntity> activePolicies = policyDevelopmentEntityDao
                .findActiveRecordsByType(DevEntityType.POLICY.getKey());

        for(PolicyDevelopmentEntity policy : activePolicies) {
            if(!importingPolicyIds.contains(policy.getId())) {
                policyMgmtService.remove(ImmutableList.of(policy.getId()), true);
                if(log.isDebugEnabled()) {
                    log.debug("Removed non-importing policy with id {}", policy.getId());
                }
            }
        }
    }

    private void importComponents(PolicyPortingDTO policyPort, Map<Long, Long> policyModelIdMap,
                                  Map<Long, ComponentDTO> componentIdMap, Map<String, Long> componentFolders, Set<String> componentFoldersInLowerCase)
            throws ConsoleException, CircularReferenceException {
        Set<Long> unmodifiedComponentIds = new HashSet<>();
        for (ComponentDTO component : policyPort.getComponents()) {
            List<ComponentLite> compLiteList = componentMgmtService.
                    getComponentsByNameAndGroup(component.getName(), component.getType());
            boolean saveImportComponent;
            ComparableComponent comparableImportComponent = new ComparableComponent(component);

            Long importingComponentId = component.getId();
            Long policyModelId = component.getPolicyModel().getId();
            Long localPolicyModelId = policyModelIdMap.get(policyModelId);
            component.getPolicyModel().setId(localPolicyModelId);

            component.setId(null);
            component.setStatus(PolicyStatus.DRAFT.name());
            component.setVersion(-1);
            
            Set<TagDTO> tags = component.getTags();
            createTagIfNotExists(tags);
            component.setTags(tags);

			boolean componentWithSameNameAndModelFound = false;
			if (!compLiteList.isEmpty()) {
			    for(ComponentLite comp : compLiteList) {
			        if(!comp.getModelId().equals(localPolicyModelId)) {
			            continue;
			        }
			        componentWithSameNameAndModelFound = true;
                    saveImportComponent = !new ComparableComponent(componentMgmtService.findById(comp.getId()))
                            .equals(comparableImportComponent);

    				component.setId(comp.getId());
    				component.setReIndexAllNow(false);
                    if(saveImportComponent) {
                        component = componentMgmtService.modify(component, CheckCircularRefs.NO);
                    } else {
                        unmodifiedComponentIds.add(component.getId());
                    }
			    }
			}
            if(component.getFolderPath() != null && !componentFolders.containsKey(component.getFolderPath())) {
                componentFolders.put(component.getFolderPath(), folderService.createFolderPath(FolderType.COMPONENT, component.getFolderPath()));
                registerFolders(componentFoldersInLowerCase, component.getFolderPath());
            }
			component.setFolderId(componentFolders.get(component.getFolderPath()));
			if(!componentWithSameNameAndModelFound) {
			    component.setReIndexAllNow(false);
				component = componentMgmtService.save(component, CheckCircularRefs.NO);
			}
            componentIdMap.put(importingComponentId, component);
        }

        // update members and components
        for (ComponentDTO component : policyPort.getComponents()) {
            if(unmodifiedComponentIds.contains(component.getId())) {
                continue;
            }

            for (MemberCondition memberCondition : component.getMemberConditions()) {
                for (MemberDTO member : memberCondition.getMembers()) {
                    if (ComponentPQLHelper.MEMBER_GROUP.equals(member.getType())) {
                        List<MemberDTO> memberFound = memberService.findMemberByUniqueName(LeafObjectType.forName(member.getMemberType()), member.getUniqueName());
                        if(memberFound == null || memberFound.isEmpty()) {
                            member.setId(-member.getId());
                            policyPort.setNonBlockingError(true);
                        } else {
                            member.setId(memberFound.get(0).getId());
                        }
                    } else {
                        ComponentDTO persistedComponent = componentIdMap.get(member.getId());
                        if(persistedComponent == null) {
                            member.setId(-member.getId());
                            policyPort.setNonBlockingError(true);
                        } else {
                            member.setId(persistedComponent.getId());
                        }
                    }
                }
            }

            // Map sub-components found in policies exported from CC versions before 8.7 to a member condition
            List<ComponentDTO> subComponents = component.getSubComponents();
            if (!subComponents.isEmpty()) {
                MemberCondition memberCondition = new MemberCondition();
                memberCondition.setOperator(Operator.IN);
                subComponents.forEach(subComponent -> {
                    ComponentDTO persistedComp = componentIdMap.get(subComponent.getId());
                    if (persistedComp == null) {
                        memberCondition.getMembers().add(new MemberDTO(-subComponent.getId(), subComponent.getType()));
                        policyPort.setNonBlockingError(true);
                    } else {
                        memberCondition.getMembers().add(new MemberDTO(persistedComp.getId(), persistedComp.getType()));
                    }
                });
                component.getMemberConditions().add(memberCondition);
                subComponents.clear();
            }
            component.setReIndexAllNow(false);
            componentMgmtService.modify(component, CheckCircularRefs.NO);
        }
    }

    private void removeNonImportingComponents(Set<Long> importingComponentIds)
            throws ConsoleException {
        List<PolicyDevelopmentEntity> activeComponents = policyDevelopmentEntityDao
                .findActiveRecordsByType(DevEntityType.COMPONENT.getKey());

        for(PolicyDevelopmentEntity component : activeComponents) {
            if(!importingComponentIds.contains(component.getId())) {
                componentMgmtService.remove(component.getId());
                if(log.isDebugEnabled()) {
                    log.debug("Removed non-importing component with id {}", component.getId());
                }
            }
        }
    }

	private Map<Long, Long> importPolicyModels(PolicyPortingDTO policyPort)
			throws ConsoleException, CircularReferenceException {
        Map<Long, Long> policyModelIdMapForImportedComps = new HashMap<>();
		for (PolicyModel importingPolicyModel : policyPort.getPolicyModels()) {
			Long importingPolicyModelId = importingPolicyModel.getId();
			List<TagLabel> tags = new ArrayList<>(importingPolicyModel.getTags());
			createTagIfNotExists(tags);
			importingPolicyModel.setTags(new HashSet<>(tags));

			List<PolicyModel> localPolicyModels = policyModelRepository.findByShortNameAndTypeAndStatus(
                    importingPolicyModel.getShortName(), importingPolicyModel.getType(), Status.ACTIVE);
            boolean saveImportModel = true;
            if(localPolicyModels.isEmpty()) {
                importingPolicyModel.setId(null);
                importingPolicyModel.setVersion(0);
                importingPolicyModel.getActions().forEach(action
                        -> action.setId(null));
                populatePolicyModel(importingPolicyModel);
            } else {
                PolicyModel localPolicyModel = localPolicyModels.get(0);
                importingPolicyModel.setId(localPolicyModel.getId());

                Map<String, AttributeConfig> nonCustomAttributes = new HashMap<>();
                if (SpecType.USER.getName().equals(importingPolicyModel.getShortName())
                        || SpecType.HOST.getName().equals(importingPolicyModel.getShortName())
                        || SpecType.APPLICATION.getName().equals(importingPolicyModel.getShortName())) {
                    policyModelService.loadExtraSubjectAttributes(importingPolicyModel.getShortName())
                            .forEach(extraAttribute -> nonCustomAttributes.put(extraAttribute.getShortName(), extraAttribute));
                }

                importingPolicyModel.getAttributes().removeIf(attributeConfig
                        -> nonCustomAttributes.containsKey(attributeConfig.getShortName()));

                saveImportModel = !new ComparablePolicyModel(localPolicyModel).equals(new ComparablePolicyModel(importingPolicyModel));

                if(saveImportModel) {
                    importingPolicyModel.setVersion(-1);
                    updatePolicyModel(importingPolicyModel, localPolicyModel, policyPort.getMechanism());
                }
            }
            if(saveImportModel) {
                importingPolicyModel = policyModelService.save(importingPolicyModel, false);
            }
			policyModelIdMapForImportedComps.put(importingPolicyModelId, importingPolicyModel.getId());
		}

        return policyModelIdMapForImportedComps;
	}

    private void removeNonImportingPolicyModels(Set<Long> importingPolicyModelIds)
            throws ConsoleException {
        List<PolicyModel> policyModelsToRemove = policyModelRepository.findByTypeAndStatusAndIdNotIn(PolicyModelType.RESOURCE,
                Status.ACTIVE, new ArrayList<>(importingPolicyModelIds));

        for(PolicyModel policyModel : policyModelsToRemove) {
            policyModelService.remove(policyModel.getId());
            if(log.isDebugEnabled()) {
                log.debug("Removed non-importing policy model with id {}", policyModel.getId());
            }
        }
    }

    private void removeNonImportingFolders(FolderType folderType, Set<String> importingFolders)
            throws ConsoleException {
        List<FolderDTO> folders = folderService.findByType(folderType);
        List<Long> folderIdsToDelete = new ArrayList<>();
        for(FolderDTO folder : folders) {
            if(!importingFolders.contains(folder.getFolderPath().toLowerCase())) {
                folderIdsToDelete.add(folder.getId());
            }
        }
        if(!folderIdsToDelete.isEmpty()) {
            folderService.delete(folderIdsToDelete);
        }
    }

    private void populatePolicyModel(PolicyModel importingPolicyModel)
            throws ConsoleException {
        Set<AttributeConfig> attributeConfigs = new TreeSet<>();
        for (AttributeConfig attribute : importingPolicyModel.getAttributes()) {
            AttributeConfig attrib = new AttributeConfig();
            attrib.setName(attribute.getName());
            attrib.setShortName(attribute.getShortName());
            for (OperatorConfig operator : attribute.getOperatorConfigs()) {
                OperatorConfig operConfig = operatorConfigService.
                        findByKeyAndDataType(operator.getKey(),
                                operator.getDataType());

                attrib.getOperatorConfigs().add(operConfig);
            }
            attrib.setDataType(attribute.getDataType());
            attrib.setRegExPattern(attribute.getRegExPattern());
            attrib.setSortOrder(attribute.getSortOrder());
            attributeConfigs.add(attrib);
        }
        importingPolicyModel.setAttributes(attributeConfigs);

        Set<ActionConfig> actionConfigs = new TreeSet<>();
        for (ActionConfig action : importingPolicyModel.getActions()) {
            ActionConfig newAction = new ActionConfig();
            newAction.setName(action.getName());
            newAction.setShortName(action.getShortName());
            newAction.setShortCode(action.getShortCode());
            newAction.setSortOrder(action.getSortOrder());
            actionConfigs.add(newAction);
        }
        importingPolicyModel.setActions(actionConfigs);

        Set<ObligationConfig> obligationConfigs = new TreeSet<>();
        for (ObligationConfig obligation : importingPolicyModel.getObligations()) {
            ObligationConfig newObligation = new ObligationConfig();
            newObligation.setName(obligation.getName());
            newObligation.setShortName(obligation.getShortName());
            newObligation.setRunAt(obligation.getRunAt());

            for (ParameterConfig param : obligation.getParameters()) {
                ParameterConfig newParam = new ParameterConfig();
                newParam.setType(param.getType());
                newParam.setName(param.getName());
                newParam.setShortName(param.getShortName());
                newParam.setMandatory(param.isMandatory());
                newParam.setHidden(param.isHidden());
                newParam.setEditable(param.isEditable());
                newParam.setDefaultValue(param.getDefaultValue());
                newParam.setListValues(param.getListValues());
                newParam.setSortOrder(param.getSortOrder());
                newObligation.getParameters().add(newParam);
            }
            newObligation.setSortOrder(obligation.getSortOrder());
            obligationConfigs.add(newObligation);
        }
        importingPolicyModel.setObligations(obligationConfigs);
    }

    private void updatePolicyModel(PolicyModel importingPolicyModel, PolicyModel existingPolicyModel, ImportMechanism importMechanism)
            throws ConsoleException {
        if(ImportMechanism.PARTIAL.equals(importMechanism)) {
            Map<String, AttributeConfig> importingAttributeMap = new HashMap<>();
            importingPolicyModel.getAttributes().forEach(attribute
                    -> importingAttributeMap.put(attribute.getShortName(), attribute));
            for(AttributeConfig attributeConfig : existingPolicyModel.getAttributes()) {
                if(!importingAttributeMap.containsKey(attributeConfig.getShortName())) {
                    AttributeConfig attribute = new AttributeConfig();
                    attribute.setName(attributeConfig.getName());
                    attribute.setShortName(attributeConfig.getShortName());
                    for (OperatorConfig operator : attributeConfig.getOperatorConfigs()) {
                        OperatorConfig operatorConfig = operatorConfigService.
                                findByKeyAndDataType(operator.getKey(),
                                        operator.getDataType());

                        attribute.getOperatorConfigs().add(operatorConfig);
                    }
                    attribute.setDataType(attributeConfig.getDataType());
                    attribute.setRegExPattern(attributeConfig.getRegExPattern());
                    attribute.setSortOrder(attributeConfig.getSortOrder());
                    importingPolicyModel.getAttributes().add(attribute);
                } else {
                    AttributeConfig attribute = importingAttributeMap.get(attributeConfig.getShortName());
                    if(attribute.getDataType().equals(attributeConfig.getDataType())) {
                        for (OperatorConfig operator : attributeConfig.getOperatorConfigs()) {
                            OperatorConfig operatorConfig = operatorConfigService.
                                    findByKeyAndDataType(operator.getKey(),
                                            operator.getDataType());
                            attribute.getOperatorConfigs().add(operatorConfig);
                        }
                    }
                }
            }

            Set<String> importingActionSet = new TreeSet<>();
            importingPolicyModel.getActions().forEach(action
                    -> importingActionSet.add(action.getShortName()));
            existingPolicyModel.getActions().forEach(actionConfig ->  {
                if(!importingActionSet.contains(actionConfig.getShortName())) {
                    ActionConfig action = new ActionConfig();
                    action.setName(actionConfig.getName());
                    action.setShortName(actionConfig.getShortName());
                    action.setShortCode(actionConfig.getShortCode());
                    action.setSortOrder(actionConfig.getSortOrder());
                    importingPolicyModel.getActions().add(action);
                }
            });

            Map<String, ObligationConfig> importingObligationMap = new HashMap<>();
            importingPolicyModel.getObligations().forEach(obligation
                    -> importingObligationMap.put(obligation.getShortName(), obligation));
            existingPolicyModel.getObligations().forEach(obligationConfig -> {
                if(!importingObligationMap.containsKey(obligationConfig.getShortName())) {
                    ObligationConfig obligation = new ObligationConfig();
                    obligation.setName(obligationConfig.getName());
                    obligation.setShortName(obligationConfig.getShortName());
                    obligation.setRunAt(obligationConfig.getRunAt());
                    obligationConfig.getParameters().forEach(parameterConfig -> {
                        ParameterConfig parameter = new ParameterConfig();
                        parameter.setType(parameterConfig.getType());
                        parameter.setName(parameterConfig.getName());
                        parameter.setShortName(parameterConfig.getShortName());
                        parameter.setMandatory(parameterConfig.isMandatory());
                        parameter.setHidden(parameterConfig.isHidden());
                        parameter.setEditable(parameterConfig.isEditable());
                        parameter.setDefaultValue(parameterConfig.getDefaultValue());
                        parameter.setListValues(parameterConfig.getListValues());
                        parameter.setSortOrder(parameterConfig.getSortOrder());
                        obligation.getParameters().add(parameter);
                    });
                    obligation.setSortOrder(obligationConfig.getSortOrder());
                    importingPolicyModel.getObligations().add(obligation);
                } else {
                    Set<String> importingParameterSet = new TreeSet<>();
                    ObligationConfig importingObligation = importingObligationMap.get(obligationConfig.getShortName());
                    importingObligation.getParameters().forEach(parameterConfig
                            -> importingParameterSet.add(parameterConfig.getShortName()));
                    obligationConfig.getParameters().forEach(parameterConfig -> {
                        if(!importingParameterSet.contains(parameterConfig.getShortName())) {
                            ParameterConfig parameter = new ParameterConfig();
                            parameter.setType(parameterConfig.getType());
                            parameter.setName(parameterConfig.getName());
                            parameter.setShortName(parameterConfig.getShortName());
                            parameter.setMandatory(parameterConfig.isMandatory());
                            parameter.setHidden(parameterConfig.isHidden());
                            parameter.setEditable(parameterConfig.isEditable());
                            parameter.setDefaultValue(parameterConfig.getDefaultValue());
                            parameter.setListValues(parameterConfig.getListValues());
                            parameter.setSortOrder(parameterConfig.getSortOrder());
                            importingObligation.getParameters().add(parameter);
                        }
                    });
                }
            });
        }
    }

    private void addSubPolicy(PolicyPortingDTO policyPort, Map<Long, ComponentDTO> componentIdMap,
            Node policyNode, PolicyDTO policy, Map<Long, Long> policyModelIdMap) 
            		throws ConsoleException {

        for (Node subPolicyNode : policyNode.getChildren()) {
            PolicyDTO subPolicy = subPolicyNode.getData();
            List<PolicyLite> subPolicyLiteList = policyMgmtService.getPolicyByName(subPolicy.getName());

            subPolicy.setParentId(policy.getId());
            subPolicy.setId(null);
            subPolicy.setFolderId(policy.getFolderId());
            subPolicy.getSubPolicyRefs().clear();
            
            //set sub-policy tags while importing
            Set<TagDTO> tags = subPolicy.getTags();
            createTagIfNotExists(tags);
            subPolicy.setTags(tags);
            
            updateComponentRefs(componentIdMap, subPolicy.getActionComponents());
            updateComponentRefs(componentIdMap, subPolicy.getSubjectComponents());
            updateComponentRefs(componentIdMap, subPolicy.getToSubjectComponents());
            updateComponentRefs(componentIdMap, subPolicy.getFromResourceComponents());
            updateComponentRefs(componentIdMap, subPolicy.getToResourceComponents());

            for (ObligationDTO obligationDTO : subPolicy.getAllowObligations()){
            	Long oldModelId = obligationDTO.getPolicyModelId();
            	if (!(oldModelId.compareTo(0L) == 0)){
            		obligationDTO.setPolicyModelId(
            				policyModelIdMap.get(oldModelId));
            	}
            }
            for (ObligationDTO obligationDTO : subPolicy.getDenyObligations()){
            	Long oldModelId = obligationDTO.getPolicyModelId();
            	if (!(oldModelId.compareTo(0L) == 0)){
            		obligationDTO.setPolicyModelId(
            				policyModelIdMap.get(oldModelId));
            	}
            }
            //set sub-policy status to DRAFT before saving
            subPolicy.setStatus(PolicyStatus.DRAFT.name());
            
            // check policy name is exists do update the existing policy
            List<PolicyLite> policyLiteList = policyMgmtService.getPolicyByName(subPolicy.getName());
            if (!policyLiteList.isEmpty()) {
                PolicyDTO subPolicyDTO = policyMgmtService.findById(subPolicyLiteList.get(0).getId());
                if(new ComparablePolicy(subPolicy, subPolicyNode).equals(new ComparablePolicy(subPolicyDTO, null))) {
                    policyPort.getImportedPolicyIds().add(subPolicyDTO.getId());
                    addSubPolicy(policyPort, componentIdMap, subPolicyNode, subPolicyDTO, policyModelIdMap);
                    continue;
                }

                PolicyLite policyLite = policyLiteList.get(0);
                subPolicy.setId(policyLite.getId());
                subPolicy.setFullName(null);
                subPolicy.setVersion(-1);
                subPolicy.setReIndexNow(false);
                policyMgmtService.modify(subPolicy);
            } else {
                subPolicy.setReIndexNow(false);
            	subPolicy = policyMgmtService.addSubPolicy(subPolicy);
            }

            policyPort.getImportedPolicyIds().add(subPolicy.getId());
            addSubPolicy(policyPort, componentIdMap, subPolicyNode, subPolicy, policyModelIdMap);
        }
    }

    private void updateComponentRefs(Map<Long, ComponentDTO> componentIdMap,
            List<PolicyComponent> policyCompList) {

        for (PolicyComponent policyComp : policyCompList) {
            List<ComponentDTO> compList = policyComp.getComponents();
            for (int i = 0; i < compList.size(); i++) {
                Long oldCompId = compList.get(i).getId();
                ComponentDTO persistedComp = componentIdMap.get(oldCompId);
                if (persistedComp != null) {
                    compList.set(i, persistedComp);
                }
            }
        }
    }

    private void createTagIfNotExists(List<TagLabel> tags)
            throws ConsoleException {
        PageRequest page = PageRequest.of(0, 20);
        for (int i = 0; i < tags.size(); i++) {
            TagLabel tag = tags.get(i);
            Page<TagLabel> tagsFromRepo = tagLabelSearchRepository
                    .findByKeyAndTypeAndStatusAndHiddenOrderByLabelAsc(
                            tag.getKey(), tag.getType(), Status.ACTIVE, false,
                            page);
            if (tagsFromRepo.getContent().isEmpty()) {
                TagLabel newTag = tagLabelService.saveTag(new TagLabel(tag.getKey(),
                        tag.getLabel(), tag.getType(), Status.ACTIVE));
                tags.set(i, newTag);
            } else {
                TagLabel foundTag = tagsFromRepo.getContent().get(0);
                tags.set(i, foundTag);
            }
        }
    }
    
    private void createTagIfNotExists(Set<TagDTO> tags)
            throws ConsoleException {
        PageRequest page = PageRequest.of(0, 20);
        for (TagDTO tag : tags) {
            Page<TagLabel> tagsFromRepo = tagLabelSearchRepository
                    .findByKeyAndTypeAndStatusAndHiddenOrderByLabelAsc(
                            tag.getKey(), TagType.getType(tag.getType()), Status.ACTIVE, false,
                            page);
            if (tagsFromRepo.getContent().isEmpty()) {
            	TagLabel tagLbl = tagLabelService.saveTag(new TagLabel(tag.getKey(),
                        tag.getLabel(), TagType.getType(tag.getType()), Status.ACTIVE));
            	tag.setId(tagLbl.getId());
            } else {
            	TagLabel tagLbl = tagsFromRepo.getContent().get(0);
            	tag.setId(tagLbl.getId());
            }
        }
    }

    public static PolicyPortingDTO deserialize(byte[] bytes)
            throws ConsoleException {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream is = new ObjectInputStream(bis);
            return (PolicyPortingDTO) is.readObject();
        } catch (Exception e) {
            throw new ConsoleException("Error encountered while deserializing,",
                    e);
        }
    }

    private void preparePolicy(PolicyDTO policy) {
        policy.setManualDeploy(false);
        policy.getDeploymentTargets().clear();
        policy.setFolderId(null);
    }
    
	private void validatePolicyExportRequest(String desiredMode) {
		if (!EnumUtils.isValidEnum(DataTransportationMode.class, desiredMode)) {
			throw new InvalidPolicyPortingRequestException(msgBundle.getText("invalid.policy.export.format.code"),
					msgBundle.getText("invalid.policy.export.format",
							msgBundle.getText("policy.mgmt.data.transportation.mode.plain") + " / "
									+ msgBundle.getText("policy.mgmt.data.transportation.mode.sande")));
		}
		DataTransportationMode configuredMode = DataTransportationMode.valueOf(dataTransportationProperties.getMode());
		if (DataTransportationMode.SANDE == configuredMode) {
			if (DataTransportationMode.PLAIN == DataTransportationMode.valueOf(desiredMode)
					&& !dataTransportationProperties.isAllowPlainTextExport()) {
				throw new InvalidPolicyPortingRequestException(msgBundle.getText("invalid.policy.export.format.code"),
						msgBundle.getText("invalid.policy.export.format", msgBundle.getText("policy.mgmt.data.transportation.mode.sande")));
			}
		} else if (DataTransportationMode.PLAIN == configuredMode) {
			if (DataTransportationMode.SANDE == DataTransportationMode.valueOf(desiredMode)) {
				throw new InvalidPolicyPortingRequestException(msgBundle.getText("invalid.policy.export.format.code"),
						msgBundle.getText("invalid.policy.export.format", msgBundle.getText("policy.mgmt.data.transportation.mode.plain")));
			}
		}
	}
	
	private void validatePolicyImportRequest(String desiredMode) {		
		if (!EnumUtils.isValidEnum(DataTransportationMode.class, desiredMode)) {
			throw new InvalidPolicyPortingRequestException(msgBundle.getText("invalid.policy.import.format.code"),
					msgBundle.getText("invalid.policy.import.format",
							msgBundle.getText("policy.mgmt.data.transportation.mode.plain") + " / "
									+ msgBundle.getText("policy.mgmt.data.transportation.mode.sande")));
		}
		if (DataTransportationMode.PLAIN == DataTransportationMode.valueOf(desiredMode)
                && DataTransportationMode.PLAIN != DataTransportationMode.valueOf(dataTransportationProperties.getMode())
			    && !dataTransportationProperties.isAllowPlainTextImport()) {
            throw new InvalidPolicyPortingRequestException(msgBundle.getText("invalid.policy.import.format.code"),
                    msgBundle.getText("invalid.policy.import.format", msgBundle.getText("policy.mgmt.data.transportation.mode.sande")));
		}
		if (DataTransportationMode.SANDE == DataTransportationMode.valueOf(desiredMode)
			    && DataTransportationMode.SANDE != DataTransportationMode.valueOf(dataTransportationProperties.getMode())) {
            throw new InvalidPolicyPortingRequestException(msgBundle.getText("invalid.policy.import.format.code"),
                    msgBundle.getText("invalid.policy.import.format", msgBundle.getText("policy.mgmt.data.transportation.mode.plain")));
		}
	}

    private byte[] generateEncryptedExportData(String policyBundle) throws ConsoleException {
        try {
            String hostname = request.getServerName().toLowerCase();
            HttpSession httpSession = request.getSession(false);
            String sessionId = httpSession == null ? "" : httpSession.getId() + "-";
            String keystoreFileInfoIdentifier = sessionId + System.currentTimeMillis();
            String keystoreFile = dataTransportationProperties.getKeyStoreFile();
            String keystoreType = keyStoreProperties.getType();
            String keystorePassword = keyStoreProperties.getPassword();
            String signatureAlgorithm = PolicyPortingUtil.SIGNATURE_ALGORITHM;
            String digitalSignature = PolicyPortingUtil.createDigitalSignature(hostname, keystoreFileInfoIdentifier,
                    keystoreFile, keystoreType, keystorePassword, signatureAlgorithm);
            String secretKey = dataTransportationProperties.getSharedKey();
            return Base64.encodeBase64(PolicyPortingUtil.generateEbinContent(policyBundle, secretKey, hostname,
                    digitalSignature));
        } catch (Exception e) {
            throw new ConsoleException("Error encountered while signing & encrypting policy bundle,", e);
        }
    }

    private String validateSignatureAndExtractPolicyBundle(byte[] ebinContent)
            throws ConsoleException {
        try {
            HttpSession httpSession = request.getSession(false);
            String sessionId = httpSession == null ? "" : httpSession.getId() + "-";
            String keystoreFileInfoIdentifier = sessionId + System.currentTimeMillis();
            String trustStoreFile = dataTransportationProperties.getTrustStoreFile();
            String trustStoreType = trustStoreProperties.getType();
            String trustStorePassword = trustStoreProperties.getPassword();
            String secretKey = dataTransportationProperties.getSharedKey();

            if (Base64.isBase64(ebinContent)) {
                ebinContent = Base64.decodeBase64(ebinContent);
            }
            EbinDTO dto = PolicyPortingUtil.extractEbinContent(ebinContent, secretKey);
            if (!dto.getAlias().matches(PolicyPortingUtil.HOSTNAME_REGEX)) {
                throw new InvalidPolicyPortingRequestException(msgBundle.getText("invalid.shared.key.code"),
                        msgBundle.getText("invalid.shared.key"));
            }

            String signatureKeyAlias = dto.getAlias();
            String digitalSignature = dto.getSignature();
            String signatureAlgorithm = PolicyPortingUtil.SIGNATURE_ALGORITHM;

            boolean isValidSignatureKeyAlias = PolicyPortingUtil.containsPublicKey(signatureKeyAlias, keystoreFileInfoIdentifier,
                    trustStoreFile, trustStoreType, trustStorePassword);
            if (!isValidSignatureKeyAlias) {
                throw new InvalidPolicyPortingRequestException(msgBundle.getText("invalid.signature.key.alias.code"),
                        msgBundle.getText("invalid.signature.key.alias", signatureKeyAlias, signatureKeyAlias));
            }

            boolean isValidSignature = PolicyPortingUtil.verifyDigitalSignature(signatureKeyAlias, digitalSignature, keystoreFileInfoIdentifier,
                    trustStoreFile, trustStoreType, trustStorePassword, signatureAlgorithm);
            if (!isValidSignature) {
                throw new InvalidPolicyPortingRequestException(msgBundle.getText("digital.signature.not.match.code"),
                        msgBundle.getText("digital.signature.not.match"));
            }

            return dto.getPolicyBundle();
        } catch (InvalidPolicyPortingRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new ConsoleException("Error encountered while validating signature and extracting policy bundle from ebin,", e);
        }
    }

    @Override
    public String generatePDF(List<ExportEntityDTO> exportEntityDTOS)
            throws ConsoleException, ServerException {
        String fileName;
        try {            
            PDFDomainObjectFormatter pdfFormatter = new PDFDomainObjectFormatter();
            pdfFormatter.formatDef(getCollectionForExport(exportEntityDTOS));
            fileName = generateFile(pdfFormatter.getPDF(), ".pdf");
        } catch (IOException e) {
            throw new ServerException("Error encountered while writing to file", e);
        } catch (PQLException e) {
            throw new ConsoleException("Error processing the policies,", e);
        } 
        return fileName;
    }

    @Override
    public String generateXACML(List<ExportEntityDTO> exportEntityDTOS)
            throws ConsoleException, ServerException {
        String fileName;
        try {
            XACMLDomainObjectFormatter xacmlFormatter = new XACMLDomainObjectFormatter();
            xacmlFormatter.formatDef(getCollectionForExport(exportEntityDTOS));
            fileName = generateFile(xacmlFormatter.getXACML().getBytes(), ".xml");
        } catch (IOException e) {
            throw new ServerException("Error encountered while writing to file", e);
        } catch (PQLException e) {
            throw new ConsoleException("Error processing the policies,", e);
        }
        return fileName;
    }

    /**
     * Convert the exported list to a Collection<IHadId>, a format which the DomainObjectFormatter
     * expects.
     */
    public Collection<IHasId> getCollectionForExport(List<ExportEntityDTO> exportEntityDTOS)
            throws ConsoleException, PQLException {

        Set<Long> exportSet = new TreeSet<>();
        Set<Long> policyExportSet = getPolicyIds(exportEntityDTOS);
        exportSet.addAll(policyExportSet);
        exportSet.addAll(getComponentIds(policyExportSet));

        if (exportSet.isEmpty()) {
            throw new InvalidPolicyPortingRequestException(
                    msgBundle.getText("export.no.policies.code"),
                    msgBundle.getText("export.no.policies"));
        }

        PolicyDevelopmentEntity pe;
        Collection<IHasId> objs = null;
        for (Long id : exportSet) {
            pe = policyDevelopmentEntityRepository.findById(id).get();
            if (objs == null) {
                objs = DTOUtils.makeCollectionOfSpecs(pe.getPql());
            } else {
                objs.addAll(DTOUtils.makeCollectionOfSpecs(pe.getPql()));
            }
        }
        return objs;
    }

    /**
     * Saves a policy export file from the given data and of the given file extension
     * 
     * @return fileName the name of the file created.
     */
    public String generateFile(byte[] exportData, String fileExtension) throws IOException {

        String fileName = msgBundle.getText("policy.mgmt.export.file.prefix")
                + System.currentTimeMillis() + fileExtension;

        Path path = Paths.get(configDataLoader.getPolicyExportsFileLocation(), "Policy", fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, exportData);
        log.info("Policy data file generated successfully to :{}", fileName);

        return fileName;
    }

    public Set<Long> getPolicyIds(List<ExportEntityDTO> exportEntityDTOS) {
        Set<Long> policyIdSet = exportEntityDTOS.stream().filter(
                exportEntityDTO -> DevEntityType.POLICY.equals(exportEntityDTO.getEntityType()))
                .map(ExportEntityDTO::getId).collect(Collectors.toSet());

        exportEntityDTOS.stream().filter(
                exportEntityDTO -> DevEntityType.FOLDER.equals(exportEntityDTO.getEntityType()))
                .forEach(exportEntityDTO -> {
                    List<FolderDTO> folderDTOS =
                            folderService.findAllSubFolders(exportEntityDTO.getId());
                    for (FolderDTO folderDTO : folderDTOS) {

                        try {
                            accessControlService.authorizeByTags(ActionType.VIEW,
                                    FolderType.POLICY.equals(folderDTO.getType())
                                            ? DelegationModelShortName.POLICY_FOLDER_ACCESS_TAGS
                                            : DelegationModelShortName.COMPONENT_FOLDER_ACCESS_TAGS,
                                    folderDTO, false);
                        } catch (ConsoleException e) {
                            continue;
                        }
                        if (FolderType.POLICY.equals(folderDTO.getType())) {
                            policyIdSet.addAll(policyDevelopmentEntityRepository
                                    .findByFolderIdAndType(folderDTO.getId(),
                                            DevEntityType.POLICY.getKey())
                                    .stream().map(PolicyDevelopmentEntity::getId)
                                    .collect(Collectors.toList()));
                        }
                    }
                });
        return policyIdSet;
    }

    public Set<Long> getComponentIds(Set<Long> policyIds) throws ConsoleException {
        Set<Long> componentIds = new TreeSet<>();
        // Select component and sub components and related policy models
        for (Long id : policyIds) {
            PolicyDTO policyDTO = policyMgmtService.findById(id);

            addComponentIds(componentIds, policyDTO.getSubjectComponents());
            addComponentIds(componentIds, policyDTO.getToSubjectComponents());
            addComponentIds(componentIds, policyDTO.getActionComponents());
            addComponentIds(componentIds, policyDTO.getFromResourceComponents());
            addComponentIds(componentIds, policyDTO.getToResourceComponents());

        }

        return componentIds;
    }
}
