package com.nextlabs.destiny.console.repositories;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nextlabs.destiny.console.model.policy.PolicyDevelopmentEntity;

/**
 * Policy development entity repository.
 *
 * @author Sachindra Dasun
 */
@Repository
public interface PolicyDevelopmentEntityRepository extends JpaRepository<PolicyDevelopmentEntity, Long> {

    List<PolicyDevelopmentEntity> findByFolderIdAndType(Long folderId, String type);

    List<PolicyDevelopmentEntity> findByTypeAndStatus(String type, String status);

    List<PolicyDevelopmentEntity> findByTypeAndStatusAndTitleContainingIgnoreCase(String type,
            String status, String text, Sort sort);

    List<PolicyDevelopmentEntity> findByTypeAndStatusAndTitleIgnoreCase(String type,
            String status, String text);

    List<PolicyDevelopmentEntity> findByTypeAndStatusInAndHidden(String type, List<String> status,
            char hidden);

}
