package com.nextlabs.destiny.console.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextlabs.destiny.console.model.LoggerConfig;
import com.nextlabs.destiny.logmanager.enums.LoggerConfigType;

/**
 * Logger configuration repository.
 *
 * @author Sachindra Dasun
 */
public interface LoggerConfigRepository extends JpaRepository<LoggerConfig, Long> {

    List<LoggerConfig> findByTypeOrderByIdDesc(LoggerConfigType type);

    Optional<LoggerConfig> findTopByTypeOrderByIdDesc(LoggerConfigType type);

}
