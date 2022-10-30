package com.nextlabs.destiny.configservice.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nextlabs.destiny.configservice.entities.LoggerConfig;
import com.nextlabs.destiny.logmanager.enums.LoggerConfigType;

/**
 * Logger configuration repository.
 *
 * @author Sachindra Dasun
 */
public interface LoggerConfigRepository extends JpaRepository<LoggerConfig, Long> {

    List<LoggerConfig> findByTypeOrderByIdDesc(LoggerConfigType type);

}
