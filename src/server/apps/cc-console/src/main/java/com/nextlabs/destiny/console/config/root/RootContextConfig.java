/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Oct 30, 2015
 *
 */
package com.nextlabs.destiny.console.config.root;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * <p>
 * Data-source specific configurations - create datasources, sets hibernate
 * configurations
 * 
 * This will read data from configurations.xml file and create datasources
 * 
 * - management.repository
 * 
 * </p>
 * 
 * @author Amila Silva
 * @since 8.0
 */
@Configuration
public class RootContextConfig {

    @Autowired
    @Qualifier("management.datasource")
    private DataSource mgmtDataSource;

    @Autowired
    @Qualifier("mgmt.entityManager")
    private EntityManagerFactory mgmtEntityManagerFactory;

    @Bean(name = "transactionManager")
    public PlatformTransactionManager mgmtTxManager() {
        return createJPSTxManager(
                mgmtDataSource, mgmtEntityManagerFactory);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    private JpaTransactionManager createJPSTxManager(DataSource dataSource,
            EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }
}
