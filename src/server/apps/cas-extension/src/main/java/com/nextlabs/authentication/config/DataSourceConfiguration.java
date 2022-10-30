package com.nextlabs.authentication.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.nextlabs.authentication.config.properties.ComboPooledDataSourceProperties;
import com.nextlabs.cc.common.enums.DbType;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

/**
 * Data source configuration.
 *
 * @author Sachindra Dasun
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.nextlabs.authentication.repositories")
public class DataSourceConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfiguration.class);

    @Value("${db.url}")
    private String jdbcUrl;

    @Value("${db.username}")
    private String user;

    @Value("${db.password}")
    private String password;

    @Value("${spring.jpa.show-sql:false}")
    private boolean showSQL;

    private ComboPooledDataSourceProperties comboPooledDataSourceProperties;

    public static final String CAS_PERSISTENCE_UNIT = "casPersistenceUnit";

    public DataSourceConfiguration(ComboPooledDataSourceProperties comboPooledDataSourceProperties) {
        this.comboPooledDataSourceProperties = comboPooledDataSourceProperties;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws PropertyVetoException {

        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPackagesToScan("com.nextlabs.authentication.models");
        entityManagerFactoryBean.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPersistenceUnitName(CAS_PERSISTENCE_UNIT);

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.hbm2ddl.auto", "none");
        jpaProperties.put("hibernate.show_sql", showSQL);
        jpaProperties.put("hibernate.format_sql", "true");
        jpaProperties.put("hibernate.use_sql_comments", "true");
        jpaProperties.put("hibernate.dialect", DbType.fromJdbcUrl(jdbcUrl).getHibernateDialect());
        jpaProperties.put("hibernate.id.new_generator_mappings", false);
        entityManagerFactoryBean.setJpaPropertyMap(jpaProperties);
        return entityManagerFactoryBean;
    }

    @Bean
    public DataSource dataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(DbType.fromJdbcUrl(jdbcUrl).getDriver());
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        customizeComboPooledDataSource(dataSource);
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    private void customizeComboPooledDataSource(ComboPooledDataSource dataSource) {
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
