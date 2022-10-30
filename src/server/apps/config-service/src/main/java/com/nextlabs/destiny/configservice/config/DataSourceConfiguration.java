package com.nextlabs.destiny.configservice.config;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Map;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nextlabs.cc.common.enums.DbType;
import com.nextlabs.destiny.configservice.config.properties.ComboPooledDataSourceProperties;

/**
 * Data source configuration.
 *
 * @author Sachindra Dasun
 */
@Configuration
public class DataSourceConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfiguration.class);

    @Value("${db.url}")
    private String jdbcUrl;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String encryptedPassword;

    private ComboPooledDataSourceProperties comboPooledDataSourceProperties;

    public DataSourceConfiguration(ComboPooledDataSourceProperties comboPooledDataSourceProperties) {
        this.comboPooledDataSourceProperties = comboPooledDataSourceProperties;
    }

    @Bean
    public DataSource dataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(DbType.fromJdbcUrl(jdbcUrl).getDriver());
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUser(username);
        dataSource.setPassword(StringUtils.isEmpty(encryptedPassword) ? null :
                ReversibleTextEncryptor.decryptIfEncrypted(encryptedPassword));
        customizeDataSource(dataSource);
        return dataSource;
    }

    private void customizeDataSource(ComboPooledDataSource dataSource) {
        for (Map.Entry<String, String> entry : comboPooledDataSourceProperties.getComboPooledDataSource().entrySet()) {
            try {
                BeanUtils.setProperty(dataSource, entry.getKey(), entry.getValue());
            } catch (Exception e) {
                LOGGER.error(String.format("Error in setting ComboPooledDataSource property %s to %s", entry.getKey(),
                        entry.getValue()), e);
            }
        }
    }

}
