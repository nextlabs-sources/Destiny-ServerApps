package com.nextlabs.destiny.console.services.impl;


import javax.persistence.criteria.Predicate;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.nextlabs.destiny.console.config.ReversibleTextEncryptor;
import com.nextlabs.destiny.console.enums.SysConfigScope;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.nextlabs.destiny.console.ConsoleApplication;
import com.nextlabs.destiny.console.dao.EntityAuditLogDao;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.StringFieldValue;
import com.nextlabs.destiny.console.dto.common.TextSearchValue;
import com.nextlabs.destiny.console.dto.config.SysConfigDTO;
import com.nextlabs.destiny.console.dto.config.SysConfigGroupDTO;
import com.nextlabs.destiny.console.dto.config.SysConfigValueDTO;
import com.nextlabs.destiny.console.enums.AuditAction;
import com.nextlabs.destiny.console.enums.AuditableEntity;
import com.nextlabs.destiny.console.exceptions.ConfigRefreshFailedException;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.configuration.SysConfig;
import com.nextlabs.destiny.console.repositories.SysConfigRepository;
import com.nextlabs.destiny.console.search.repositories.ApplicationUserSearchRepository;
import com.nextlabs.destiny.console.services.SysConfigService;

/**
 * Implementation of system configuration service.
 *
 * @author Sachindra Dasun
 */
@Service
public class SysConfigServiceImpl implements SysConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SysConfigServiceImpl.class);
    private static final String CONFIG_REFRESH_ENDPOINT = "/config/refresh?applications=";
    private static final String LOGGER_REFRESH_ENDPOINT = "/logger-config/refresh?applications=";
    private static final String SECURE_STORE_REFRESH_ENDPOINT = "/secure-store/refresh?applications=";

    @Autowired
    private SysConfigRepository sysConfigRepository;

    @Autowired
    private ApplicationUserSearchRepository applicationUserSearchRepository;

    @Autowired
    private ReversibleEncryptor reversibleEncryptor;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Value("${spring.cloud.config.uri}")
    private String configServiceUri;

    @Value("${spring.cloud.config.username}")
    private String configClientUsername;

    @Value("${spring.cloud.config.password}")
    private String configClientPassword;
    
    @Autowired
    private EntityAuditLogDao entityAuditLogDao;

    @Override
    public Map<String, List<SysConfigDTO>> findByMainGroup(String mainGroup, boolean includeAdvanced) {
        Map<String, List<SysConfigDTO>> sysConfigDTOS = new LinkedHashMap<>();
        sysConfigRepository
                .findByMainGroupAndHiddenFalseAndAdvancedInOrderBySubGroupOrderAsc(mainGroup,
                        includeAdvanced ? Arrays.asList(true, false)
                                : Collections.singletonList(false))
                .stream().map(SysConfigDTO::new).forEach(sysConfig -> {
            if (sysConfig.isEncrypted()) {
            	if(StringUtils.isEmpty(sysConfig.getValue())) {
            		sysConfig.setValueEmpty(true);
            	}
                sysConfig.setValue(null);
            	sysConfig.setDefaultValue(null);
            } else if (StringUtils.isNotEmpty(sysConfig.getValueFormat())
                    && StringUtils.isNotEmpty(sysConfig.getValue())) {
                try {
                    String valueFormat = sysConfig.getValueFormat();
                    String value = sysConfig.getValue()
                            .replace(valueFormat.substring(0, valueFormat.indexOf("%s")), "")
                            .replace(valueFormat.substring(valueFormat.indexOf("%s") + 2), "");
                    sysConfig.setValue(value);
                } catch (Exception e) {
                    LOGGER.error("Invalid configuration value for: " + sysConfig.getConfigKey(), e);
                }
            }

            sysConfigDTOS.computeIfAbsent(sysConfig.getSubGroup(), key -> new ArrayList<>()).add(sysConfig);

        });
        return sysConfigDTOS;
    }

    /**
     * Find system configuration records by main group and sub group value
     *
     * @param mainGroup Main group value
     * @param subGroup Sub group value
     * @return Collection of system configuration for the given main group and sub group
     */
    @Override
    public List<SysConfig> findByMainGroupAndSubGroup(String mainGroup, String subGroup) {
        return sysConfigRepository.findByMainGroupAndSubGroup(mainGroup, subGroup);
    }

    @Override
    public void saveAll(List<SysConfig> sysConfigs) {
        sysConfigRepository.saveAll(sysConfigs);
    }

    @Override
    public void deleteAll(List<SysConfig> sysConfigs) {
        sysConfigRepository.deleteAll(sysConfigs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Set<String> updateValue(List<SysConfigValueDTO> sysConfigValueDTOS) {
        return updateValue(sysConfigValueDTOS, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Set<String> updateValue(List<SysConfigValueDTO> sysConfigValueDTOS, boolean checkUIEditable) {
        Set<String> updatedApplications = new HashSet<>();
        sysConfigValueDTOS.forEach(sysConfigValueDTO ->
            sysConfigRepository.findByApplicationAndConfigKey(sysConfigValueDTO.getApplication(),
                sysConfigValueDTO.getConfigKey()).ifPresent(sysConfig -> {
                    if(!sysConfig.isRequired() || StringUtils.isNotEmpty(sysConfig.getValue())) {
                        if(checkUIEditable) {
                            if (!sysConfig.isHidden() && !sysConfig.isReadOnly()) {
                                updatedApplications.add(updateValue(sysConfig, sysConfigValueDTO));
                            }
                        } else {
                            updatedApplications.add(updateValue(sysConfig, sysConfigValueDTO));
                        }
                    }
                })
        );
        return updatedApplications;
    }

    private String updateValue(SysConfig sysConfig, SysConfigValueDTO sysConfigValueDTO) {
        SysConfigDTO oldConfig = SysConfigDTO.getDTO(sysConfig);
        String value = sysConfigValueDTO.getValue();
        if (value == null) {
            value = "";
        }
        if ((sysConfig.isRequired() || StringUtils.isNotEmpty(value))
                && StringUtils.isNotBlank(sysConfig.getPattern()) && !value.matches(sysConfig.getPattern()))
            throw new ValidationException(String.format("New %s value doesn't match allowed pattern",
                    sysConfig.getConfigKey()));
        if (StringUtils.isNotEmpty(value) && sysConfig.isEncrypted()) {
            value = reversibleEncryptor.encrypt(value);
        }
        if (StringUtils.isNotEmpty(sysConfig.getValueFormat())
                && (!sysConfig.isEncrypted() || StringUtils.isNotEmpty(value))) {
            value = String.format(sysConfig.getValueFormat(), value);
        }

        sysConfig.setValue(value);
        SysConfigDTO newConfig = SysConfigDTO.getDTO(sysConfig);
        sysConfigRepository.save(sysConfig);
        try {
            entityAuditLogDao.addEntityAuditLog(AuditAction.UPDATE,
                            AuditableEntity.SYSTEM_CONFIGURATION.getCode(),
                            sysConfig.getId(), oldConfig.toAuditString(), newConfig.toAuditString());
        } catch (ConsoleException e) {
            LOGGER.error("Error in saving audit log for {}", newConfig);
        }

        return sysConfig.getApplication();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetByMainGroup(String mainGroup) {
        sysConfigRepository.findByMainGroupAndHiddenFalse(mainGroup).forEach(sysConfig -> {
            if (!sysConfig.isReadOnly() && !sysConfig.isHidden()) {
                sysConfig.setValue(sysConfig.getDefaultValue());
                sysConfigRepository.save(sysConfig);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SysConfigGroupDTO> search(SearchCriteria searchCriteria) {
        Map<String, SysConfigGroupDTO> sysConfigDTOMap = new HashMap<>();
        Specification<SysConfig> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.<Boolean>get(SysConfig.ATTRIBUTE_HIDDEN), false));
            if (searchCriteria != null) {
                searchCriteria.getFields().forEach(searchField -> {
                    if (searchField.getValue() != null) {
                        switch (searchField.getField()) {
                            case SysConfig.ATTRIBUTE_MAIN_GROUP: {
                                StringFieldValue stringFieldValue = (StringFieldValue) searchField.getValue();
                                List<String> values = stringFieldValue.getValue() != null ? (List<String>) stringFieldValue.getValue()
                                        : new ArrayList<>();
                                if (!values.isEmpty()) {
                                    predicates.add(root.<String>get(SysConfig.ATTRIBUTE_MAIN_GROUP).in(values));
                                }
                                break;
                            }
                            case SysConfig.TEXT: {
                                TextSearchValue textSearchValue = (TextSearchValue) searchField.getValue();
                                String value = textSearchValue.getValue();
                                String likeValue = String.format("%%%S%%", value);
                                predicates.add(builder.or(
                                        builder.like(builder.upper(root.get(SysConfig.ATTRIBUTE_CONFIG_KEY)), likeValue),
                                        builder.like(builder.upper(root.get(SysConfig.ATTRIBUTE_MAIN_GROUP)), likeValue),
                                        builder.like(builder.upper(root.get(SysConfig.ATTRIBUTE_SUB_GROUP)), likeValue),
                                        builder.like(builder.upper(root.get(SysConfig.ATTRIBUTE_DESCRIPTION)), likeValue)
                                ));
                                break;
                            }
                            case SysConfig.ATTRIBUTE_ADVANCED: {
                                StringFieldValue stringFieldValue = (StringFieldValue) searchField.getValue();
                                boolean includeAdvanced = stringFieldValue.getValue() != null
                                                && Boolean.valueOf((String) stringFieldValue.getValue());

                                if (!includeAdvanced) {
                                    predicates.add(builder.equal(root.<Boolean>get(SysConfig.ATTRIBUTE_ADVANCED), false));
                                }
                                break;
                            }
                            default:
                                LOGGER.info("Incorrect field: {}", searchField.getField());

                        }
                    }
                });
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
        sysConfigRepository.findAll(specification)
                .forEach(sysConfig -> sysConfigDTOMap.putIfAbsent(sysConfig.getMainGroup(),
                        new SysConfigGroupDTO(sysConfig.getMainGroup(), sysConfig.getMainGroupOrder())));
        List<SysConfigGroupDTO> sysConfigGroupDTOS = new ArrayList<>(sysConfigDTOMap.values());
        sysConfigGroupDTOS.forEach(sysConfigGroupDTO -> sysConfigRepository.findTopByMainGroupOrderByModifiedOnDesc(sysConfigGroupDTO.getGroup())
                .ifPresent(sysConfig -> {
                    sysConfigGroupDTO.setLastModifiedOn(sysConfig.getModifiedOn());
                    applicationUserSearchRepository.findById(sysConfig.getModifiedBy())
                            .ifPresent(modifiedBy -> sysConfigGroupDTO.setLastModifiedByName(modifiedBy.getDisplayName()));

                }));

        return sysConfigGroupDTOS.stream()
                .sorted(Comparator.comparing(SysConfigGroupDTO::getGroupOrder))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> findUiConfigs() {
        return sysConfigRepository.findByApplicationInAndUi(Arrays.asList(ConsoleApplication.DEFAULT_APPLICATION_NAME,
                ConsoleApplication.APPLICATION_NAME),
                true).stream()
                .collect(Collectors.toMap(SysConfig::getConfigKey, sysConfig -> {
                    String value = sysConfig.getValue();
                    if (StringUtils.isNotEmpty(value)) {
                        try {
                            if (sysConfig.isEncrypted()
                                    || value.startsWith(ReversibleTextEncryptor.ENCRYPTED_VALUE_PREFIX)) {
                                value = reversibleEncryptor.decrypt(value);
                            }
                        } catch (Exception e) {
                            LOGGER.error("Invalid configuration value for: " + sysConfig.getConfigKey(), e);
                        }
                    }
                    return value;
                }));
    }

    @Override
    public void sendConfigRefreshRequest(Set<String> applications) {
        sendRefreshRequest(CONFIG_REFRESH_ENDPOINT, applications);
    }

    @Override
    public void sendLoggerRefreshRequest() {
        Set<String> applications = new HashSet<>();
        applications.add(ConsoleApplication.DEFAULT_APPLICATION_NAME);
        sendRefreshRequest(LOGGER_REFRESH_ENDPOINT, applications);
    }

    @Override
    public void sendSecureStoreRefreshRequest() {
        Set<String> applications = new HashSet<>();
        applications.add(SysConfigScope.DABS.getCode());
        sendRefreshRequest(SECURE_STORE_REFRESH_ENDPOINT, applications);
    }

    private void sendRefreshRequest(String refreshEndPoint, Set<String> applications) {
        if (!applications.isEmpty()) {
            String url = configServiceUri + refreshEndPoint + String.join("&applications=", applications);
            LOGGER.info("Sending configuration refresh request by calling {}", url);
            ResponseEntity<String> responseEntity = restTemplateBuilder
                    .basicAuthentication(configClientUsername, configClientPassword)
                    .build()
                    .getForEntity(url, String.class);
            if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                LOGGER.info("Configuration refresh request sent by calling: {}", url);
            } else {
                LOGGER.warn("Configuration refresh request using {} has been failed", url);
                throw new ConfigRefreshFailedException(url);
            }
        }
    }

    @Override
    public List<String> findMainGroups() {
        return sysConfigRepository.findDistinctMainGroup();
    }

    public Optional<SysConfig> findByApplicationAndConfigKey(String application, String configKey) {
        return sysConfigRepository.findByApplicationAndConfigKey(application, configKey);
    }
}
