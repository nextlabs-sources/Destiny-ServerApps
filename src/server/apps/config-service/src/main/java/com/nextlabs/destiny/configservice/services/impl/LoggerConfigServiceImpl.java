package com.nextlabs.destiny.configservice.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.configservice.entities.LoggerConfig;
import com.nextlabs.destiny.configservice.repositories.LoggerConfigRepository;
import com.nextlabs.destiny.configservice.services.LoggerConfigService;
import com.nextlabs.destiny.logmanager.LogManagerClient;
import com.nextlabs.destiny.logmanager.enums.LoggerConfigType;

/**
 * Logger configuration service implementation.
 *
 * @author Sachindra Dasun
 */
@Service
public class LoggerConfigServiceImpl implements LoggerConfigService {

    @Value("${logger.manager.enabled:true}")
    private boolean loggerManagerEnabled;

    @Autowired
    private LoggerConfigRepository loggerConfigRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initLoggers() {
        if (loggerManagerEnabled) {
            LogManagerClient.configure(get());
        }
    }

    @Override
    public List<String> get() {
        List<LoggerConfig> loggerConfigs = new ArrayList<>();
        loggerConfigs.addAll(loggerConfigRepository.findByTypeOrderByIdDesc(LoggerConfigType.DEFAULT));
        loggerConfigs.addAll(loggerConfigRepository.findByTypeOrderByIdDesc(LoggerConfigType.CUSTOM));
        return loggerConfigs.stream()
                .map(LoggerConfig::getConfig)
                .filter(StringUtils::isNotEmpty).collect(Collectors.toList());
    }

}
