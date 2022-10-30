package com.nextlabs.destiny.configservice.config.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties required to initialize ComboPooledDataSource. Properties can be configured using the prefix
 * db.comboPooledDataSource.*
 *
 * @author Sachindra Dasun
 */
@Component
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
