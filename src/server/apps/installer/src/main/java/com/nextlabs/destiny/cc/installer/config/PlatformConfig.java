package com.nextlabs.destiny.cc.installer.config;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nextlabs.destiny.cc.installer.services.ServiceManagementService;
import com.nextlabs.destiny.cc.installer.services.impl.LinuxServiceManagementServiceImpl;
import com.nextlabs.destiny.cc.installer.services.impl.WindowsServiceManagementServiceImpl;

/**
 * Configure Service management based on the platform.
 *
 * @author Sachindra Dasun
 */
@Configuration
public class PlatformConfig {

    @Bean
    public ServiceManagementService serviceManagementService() {
        if (SystemUtils.IS_OS_LINUX) {
            return new LinuxServiceManagementServiceImpl();
        } else {
            return new WindowsServiceManagementServiceImpl();
        }
    }

}
