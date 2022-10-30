package com.nextlabs.authentication.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nextlabs.authentication.enums.DevelopmentEntityType;
import com.nextlabs.authentication.models.DevelopmentEntity;

/**
 * Development entity repository.
 *
 * @author Sachindra Dasun
 */
@Repository
public interface DevelopmentEntityRepository extends JpaRepository<DevelopmentEntity, Long> {

    List<DevelopmentEntity> findByHiddenAndStatusInAndType(char hidden, List<String> status,
                                                           DevelopmentEntityType developmentEntityType);

}
