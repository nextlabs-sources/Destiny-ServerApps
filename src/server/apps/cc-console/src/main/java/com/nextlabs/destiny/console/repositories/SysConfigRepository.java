package com.nextlabs.destiny.console.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.model.configuration.SysConfig;

/**
 * System configuration repository.
 *
 * @author Sachindra Dasun
 */
@Repository
public interface SysConfigRepository extends JpaRepository<SysConfig, Long>, JpaSpecificationExecutor<SysConfig> {

    Optional<SysConfig> findByApplicationAndConfigKey(String application, String configKey);

    List<SysConfig> findByMainGroupAndHiddenFalse(String mainGroup);
    
    List<SysConfig> findByMainGroupAndHiddenFalseAndAdvancedInOrderBySubGroupOrderAsc(String mainGroup, List<Boolean> includeAdvanced);

    List<SysConfig> findByMainGroupAndSubGroup(String mainGroup, String subGroup);

    Optional<SysConfig> findTopByMainGroupOrderByModifiedOnDesc(String mainGroup);

    List<SysConfig> findByApplicationInAndUiAndHiddenFalse(List<String> applications, boolean ui);
    
    List<SysConfig> findByApplicationInAndUi(List<String> applications, boolean ui);
    
    @Query("SELECT mainGroup FROM SysConfig WHERE hidden = false GROUP BY mainGroup ORDER BY  MIN(mainGroupOrder) ASC")
    List<String> findDistinctMainGroup();
}
