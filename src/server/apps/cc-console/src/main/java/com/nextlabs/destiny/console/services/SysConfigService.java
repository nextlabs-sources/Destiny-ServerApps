package com.nextlabs.destiny.console.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.config.SysConfigDTO;
import com.nextlabs.destiny.console.dto.config.SysConfigGroupDTO;
import com.nextlabs.destiny.console.dto.config.SysConfigValueDTO;
import com.nextlabs.destiny.console.model.configuration.SysConfig;

/**
 * Service to obtain and update system configurations.
 *
 * @author Sachindra Dasun
 */
public interface SysConfigService {

    Map<String, List<SysConfigDTO>> findByMainGroup(String mainGroup, boolean includeAdvanced);

    List<SysConfig> findByMainGroupAndSubGroup(String mainGroup, String subGroup);

    /**
     * Save list of system configurations
     *
     * @param sysConfigs Collection of system configuration
     */
    void saveAll(List<SysConfig> sysConfigs);

    /**
     * Delete list of system configurations
     *
     * @param sysConfigs Collection of system configuration
     */
    void deleteAll(List<SysConfig> sysConfigs);

    Set<String> updateValue(List<SysConfigValueDTO> sysConfigValueDTOS);

    Set<String> updateValue(List<SysConfigValueDTO> sysConfigValueDTOS, boolean checkUIEditable);

    void resetByMainGroup(String mainGroup);

    List<SysConfigGroupDTO> search(SearchCriteria searchCriteria);

    Map<String, String> findUiConfigs();

    void sendConfigRefreshRequest(Set<String> applications);

    void sendLoggerRefreshRequest();

    void sendSecureStoreRefreshRequest();

	List<String> findMainGroups();

    Optional<SysConfig> findByApplicationAndConfigKey(String application, String configKey);
}
