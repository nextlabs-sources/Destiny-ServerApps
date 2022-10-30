/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Oct 30, 2015
 *
 */
package com.nextlabs.destiny.console.config.root;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.nextlabs.destiny.console.config.properties.ComboPooledDataSourceProperties;
import com.nextlabs.destiny.console.init.ConfigurationDataLoader;

/**
 * <p>
 * Data-source specific configurations - create datasources, sets hibernate
 * configurations
 *
 *
 * </p>
 *
 * @author Amila Silva
 * @since 8.0
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("com.nextlabs.destiny.console.repositories")
public class DataSourceConfiguration {

    private static final Logger log = LoggerFactory
            .getLogger(DataSourceConfiguration.class);

    @Autowired
    private ConfigurationDataLoader configDataLoader;

    private ComboPooledDataSourceProperties comboPooledDataSourceProperties;

    public DataSourceConfiguration(ComboPooledDataSourceProperties comboPooledDataSourceProperties) {
        this.comboPooledDataSourceProperties = comboPooledDataSourceProperties;
    }

    @Bean(name = "management.datasource", destroyMethod = "close")
    public ComboPooledDataSource mgmtDataSource() {
        try {
            Map<String, String> dbConfig = configDataLoader
                    .getDatabaseConfigs();
            String driverClass = dbConfig.get("db.driver");
            String jdbcUrl = dbConfig.get("db.url");
            String user = dbConfig.get("db.username");
            String password = dbConfig.get("db.password");

            return createDatasource(driverClass, jdbcUrl, user, password);
        } catch (Exception e) {
            log.error("Error encountered in management datasource creation", e);
            return null;
        }
    }

    @Bean(name = {"mgmt.entityManager", "entityManagerFactory"})
    public LocalContainerEntityManagerFactoryBean mgmtEntityManagerFactory() {

        Map<String, String> dbConfig = configDataLoader.getDatabaseConfigs();

        String dialect = dbConfig.get("db.hibernate.dialect");
        String showSQL = dbConfig.get("db.hibernate.show.sql");
        String hiberante2ddlAuto = dbConfig.get("db.hibernate.ddl.auto");

        return createEntityManagerFactory(
                mgmtDataSource(), dialect, MGMT_UNIT, hiberante2ddlAuto,
                showSQL, "com.nextlabs.destiny.console.model");
    }

    private LocalContainerEntityManagerFactoryBean createEntityManagerFactory(
            DataSource dataSource, String dialect, String persistenceUnitName,
            String hiberante2ddlAuto, String showSQL,
            String... packagesToScan) {

        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan(packagesToScan);
        entityManagerFactoryBean
                .setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
        entityManagerFactoryBean
                .setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPersistenceUnitName(persistenceUnitName);

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.hbm2ddl.auto", hiberante2ddlAuto);
        jpaProperties.put("hibernate.show_sql", showSQL);
        jpaProperties.put("hibernate.format_sql", "true");
        jpaProperties.put("hibernate.use_sql_comments", "true");
        jpaProperties.put("hibernate.dialect", dialect);
        jpaProperties.put("hibernate.id.new_generator_mappings", false);
        entityManagerFactoryBean.setJpaPropertyMap(jpaProperties);
        return entityManagerFactoryBean;
    }

    private ComboPooledDataSource createDatasource(String driverClass, String jdbcUrl, String user, String password)
                    throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(driverClass);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setMaxIdleTime(5000);
        customizeComboPooledDataSource(dataSource);
        return dataSource;
    }

    private void customizeComboPooledDataSource(ComboPooledDataSource dataSource) {
        for (Map.Entry<String, String> entry : comboPooledDataSourceProperties.getComboPooledDataSource().entrySet()) {
            try {
                BeanUtils.setProperty(dataSource, entry.getKey(), entry.getValue());
            } catch (Exception e) {
                log.error(String.format("Error in setting ComboPooledDataSource property %s to %s", entry.getKey(),
                        entry.getValue()), e);
            }
        }
    }

}
