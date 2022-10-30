/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 16, 2015
 *
 */
package com.nextlabs.destiny.console.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Test configurations
 *
 * @author Amila Silva
 * @since 8.0
 */
@Configuration
@EnableTransactionManagement
public class TestConfiguration {
    //
    //    @Bean(initMethod = "init")
    //    public TestDataInitializer initTestData() {
    //        return new TestDataInitializer();
    //    }
    //
    //    @Bean(name = "keymanagement.datasource")
    //    public DataSource kmgmtDatasource() {
    //        DriverManagerDataSource dataSource = new DriverManagerDataSource();
    //        dataSource.setDriverClass(org.hsqldb.jdbcDriver.class.getName());
    //        dataSource.setJdbcUrl("jdbc:hsqldb:mem:test_console");
    //        dataSource.setUser("sa");
    //        dataSource.setPassword("");
    //
    //        return dataSource;
    //    }
    //
    //    @Bean(name = "activity.entityManager")
    //    public LocalContainerEntityManagerFactoryBean actManagerFactory() {
    //
    //        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = createEntMgrFactory(
    //                actDataSource(), ACTIVITY_UNIT);
    //        return entityManagerFactoryBean;
    //    }
    //
    //    private LocalContainerEntityManagerFactoryBean createEntMgrFactory(
    //            DataSource datasource, String persistenceUnitName) {
    //        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
    //        entityManagerFactoryBean.setDataSource(datasource);
    //        entityManagerFactoryBean.setPackagesToScan(
    //                new String[]{"com.nextlabs.destiny.console.model"});
    //        entityManagerFactoryBean
    //                .setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
    //        entityManagerFactoryBean
    //                .setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    //        entityManagerFactoryBean.setPersistenceUnitName(persistenceUnitName);
    //
    //        Map<String, Object> jpaProperties = new HashMap<String, Object>();
    //        jpaProperties.put("hibernate.hbm2ddl.auto", "create");
    //        jpaProperties.put("hibernate.show_sql", "true");
    //        jpaProperties.put("hibernate.format_sql", "true");
    //        jpaProperties.put("hibernate.use_sql_comments", "true");
    //        entityManagerFactoryBean.setJpaPropertyMap(jpaProperties);
    //        return entityManagerFactoryBean;
    //    }
    //
    //    @Bean(name = "activity.datasource")
    //    public DataSource actDataSource() {
    //        DriverManagerDataSource dataSource = new DriverManagerDataSource();
    //        dataSource.setDriverClass(org.hsqldb.jdbcDriver.class.getName());
    //        dataSource.setJdbcUrl("jdbc:hsqldb:mem:test_console");
    //        dataSource.setUser("sa");
    //        dataSource.setPassword("");
    //
    //        return dataSource;
    //    }
    //
    //    @Bean(name = "pf.entityManager")
    //    public LocalContainerEntityManagerFactoryBean pfManagerFactory() {
    //
    //        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = createEntMgrFactory(
    //                pfDataSource(), PF_UNIT);
    //        return entityManagerFactoryBean;
    //    }
    //
    //    @Bean(name = "policyframework.datasource")
    //    public DataSource pfDataSource() {
    //        DriverManagerDataSource dataSource = new DriverManagerDataSource();
    //        dataSource.setDriverClass(org.hsqldb.jdbcDriver.class.getName());
    //        dataSource.setJdbcUrl("jdbc:hsqldb:mem:test_console");
    //        dataSource.setUser("sa");
    //        dataSource.setPassword("");
    //
    //        return dataSource;
    //    }
    //
    //    @Bean(name = "dictionary.entityManager")
    //    public LocalContainerEntityManagerFactoryBean dicyManagerFactory() {
    //
    //        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = createEntMgrFactory(
    //                dicDataSource(), DICTIONARY_UNIT);
    //        return entityManagerFactoryBean;
    //    }
    //
    //    @Bean(name = "dictionary.datasource")
    //    public DataSource dicDataSource() {
    //        DriverManagerDataSource dataSource = new DriverManagerDataSource();
    //        dataSource.setDriverClass(org.hsqldb.jdbcDriver.class.getName());
    //        dataSource.setJdbcUrl("jdbc:hsqldb:mem:test_console");
    //        dataSource.setUser("sa");
    //        dataSource.setPassword("");
    //
    //        return dataSource;
    //    }
    //
    //    @Bean(name = "keymanagement.entityManager")
    //    public LocalContainerEntityManagerFactoryBean keyManagerFactory() {
    //
    //        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = createEntMgrFactory(
    //                mgmtDataSource(), KEYMGMT_UNIT);
    //        return entityManagerFactoryBean;
    //    }

}
