package com.nextlabs.authentication.config.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Container to store all data source properties.
 *
 * @author Sachindra Dasun
 */
@Configuration
@ConfigurationProperties(prefix = "db")
public class ComboPooledDataSourceProperties {

    private Map<String, String> comboPooledDataSource = new HashMap<>();

    public Map<String, String> getComboPooledDataSource() {
        return comboPooledDataSource;
    }

    public void setComboPooledDataSource(Map<String, String> comboPooledDataSource) {
        this.comboPooledDataSource = comboPooledDataSource;
    }

}
