package com.nextlabs.destiny.cc.installer.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.nextlabs.destiny.cc.installer.annotations.ValidDbConnection;
import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.config.properties.DbProperties;
import com.nextlabs.destiny.cc.installer.enums.Environment;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import com.nextlabs.destiny.cc.installer.helpers.DbHelper;
import com.nextlabs.destiny.cc.installer.services.impl.CertificateManagementServiceImpl;
import com.nextlabs.destiny.cc.installer.services.impl.InstallServiceImpl;

/**
 * Validator for Control Center database connection.
 *
 * @author Sachindra Dasun
 */
public class DbConnectionValidator implements ConstraintValidator<ValidDbConnection, CcProperties> {

    private static final String SQL_CREATE_TABLE = "CREATE TABLE %s (ID INT, NAME VARCHAR(20))";
    private static final String SQL_DROP_TEST = "DROP TABLE %s";
    private static final String SQL_INSERT_TEST = "INSERT INTO %s (ID, NAME) VALUES (1, 'test')";
    private static final Logger logger = LoggerFactory.getLogger(DbConnectionValidator.class);

    @Override
    public boolean isValid(CcProperties ccProperties, ConstraintValidatorContext context) {
        if (!Boolean.parseBoolean(System.getProperty("nextlabs.cc.is-management-server", "false"))) {
            return true;
        }
        DbProperties dbProperties = ccProperties.getDb();
        boolean cacertsCreated = false;
        try {
            if (SystemUtils.IS_OS_LINUX && ccProperties.getEnvironment() != Environment.CONTAINER) {
                new InstallServiceImpl(ccProperties)
                        .grantExecutePermission(Paths.get(ccProperties.getHome(), "java", "jre", "bin", "keytool"));
            }
            cacertsCreated = new CertificateManagementServiceImpl(ccProperties, null).createCacerts(ccProperties,
                    ccProperties.getSsl().getInstallerTruststore().getPassword(), false);
            FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
            fixedBackOffPolicy.setBackOffPeriod(dbProperties.getRetryBackOffPeriod());
            RetryTemplate retryTemplate = new RetryTemplate();
            retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
            retryTemplate.setRetryPolicy(new SimpleRetryPolicy(dbProperties.getRetryAttempts()));
            return retryTemplate.execute((RetryCallback<Boolean, Exception>) retryContext -> {
                logger.info("Database connection test started");
                JdbcTemplate jdbcTemplate = DbHelper.getJdbcTemplate(dbProperties);
                String tableName = String.format("DB_TEST_%s", RandomStringUtils.randomAlphabetic(6)
                        .toUpperCase());
                jdbcTemplate.execute(String.format(SQL_CREATE_TABLE, tableName));
                jdbcTemplate.execute(String.format(SQL_INSERT_TEST, tableName));
                jdbcTemplate.execute(String.format(SQL_DROP_TEST, tableName));
                logger.info("Database connection test successful");
                if (RunningMode.UPGRADE.name().equals(System.getProperty("nextlabs.cc.running-mode"))) {
                    try {
                        jdbcTemplate.execute("SELECT COUNT(*) FROM COMPONENT");
                    } catch (Exception e) {
                        logger.error("Error in checking for valid Control Center database", e);
                        if (context != null) {
                            context.disableDefaultConstraintViolation();
                            context.buildConstraintViolationWithTemplate("{db.validCcDb}")
                                    .addConstraintViolation();
                        }
                        return false;
                    }
                }
                return true;
            });
        } catch (Exception e) {
            logger.error("Error in database connection test", e);
        } finally {
            if(cacertsCreated) {
                Path certificatesDirectoryPath = Paths.get(ccProperties.getHome(), "server", "certificates");
                ccProperties.getSsl().getCaCerts().forEach(certificateProperties -> {
                    String fileName = StringUtils.isNotEmpty(certificateProperties.getAlias()) ?
                            String.format("%s.cer", certificateProperties.getAlias()) :
                            Paths.get(certificateProperties.getPath()).toFile().getName();
                    if (StringUtils.isNotEmpty(fileName)) {
                        FileUtils.deleteQuietly(certificatesDirectoryPath.resolve("cacerts").resolve(fileName).toFile());
                    }
                });
                FileUtils.deleteQuietly(certificatesDirectoryPath.resolve(CcProperties.CACERTS_FILE.toString()).toFile());
                FileUtils.deleteQuietly(Paths.get(ccProperties.getHome(), "java", "jre", "lib", "security", "cacerts").toFile());
            }
        }
        return false;
    }

}
