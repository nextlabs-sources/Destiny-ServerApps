package com.nextlabs.destiny.console.repositories;

import com.nextlabs.destiny.console.model.remoteenv.RemoteEnvironment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RemoteEnvironmentRepository extends JpaRepository<RemoteEnvironment, Long> {

    Optional<List<RemoteEnvironment>> findByIsActiveTrue();

    Optional<RemoteEnvironment> findByIdAndIsActiveTrue(Long id);

    Optional<RemoteEnvironment> findByHostAndIsActiveTrue(String host);

    Optional<RemoteEnvironment> findByNameAndIsActiveTrue(String name);

    @Transactional
    @Modifying
    @Query("UPDATE RemoteEnvironment r SET r.isActive = false WHERE r.isActive = true AND r.id = :id")
    void setAsInactive(@Param("id") Long id);
}
