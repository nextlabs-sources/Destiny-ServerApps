/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jun 28, 2016
 *
 */
package com.nextlabs.destiny.console.enums;

/**
 * Enum to hold all the environment related configurations
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public enum EnvironmentConfig {

    INSTALL_MODE_ENV_PARAM("console.install.mode"),
    INSTALL_MODE_LEGACY_CODE("OPL"),
    INSTALL_MODE_CONSOLE_CODE("OPN"),
    INSTALL_MODE_SAAS_CODE("SAAS");
    
    private String label;

    private EnvironmentConfig(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    
    public static EnvironmentConfig get(String label) {
        for (EnvironmentConfig config : EnvironmentConfig.values()) {
            if (config.getLabel().equalsIgnoreCase(label)) {
                return config;
            }
        }
        return EnvironmentConfig.INSTALL_MODE_CONSOLE_CODE;
    }
    
   
}
