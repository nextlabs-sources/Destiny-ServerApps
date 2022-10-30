package com.nextlabs.destiny.cc.installer;

import javax.validation.ConstraintViolationException;
import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

import com.nextlabs.destiny.cc.installer.advices.ValidationExceptionHandler;
import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.enums.CommandOption;
import com.nextlabs.destiny.cc.installer.exceptions.InstallerException;
import com.nextlabs.destiny.cc.installer.helpers.CommandLineOptionsHelper;
import com.nextlabs.destiny.cc.installer.helpers.ParameterHelper;
import com.nextlabs.destiny.cc.installer.services.InstallService;
import com.nextlabs.destiny.cc.installer.services.InstallerConfigurationService;
import com.nextlabs.destiny.cc.installer.services.ServiceManagementService;
import com.nextlabs.destiny.cc.installer.services.UninstallService;

/**
 * Control Center installer application.
 *
 * @author Sachindra Dasun
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class
})
@PropertySource(value = "classpath:installation-default.properties", name = "cc_installer_default_properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${nextlabs.cc.previous-home}/server/configuration/application.properties", name = "cc_previous_application_properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${nextlabs.cc.previous-home}/server/configuration/bootstrap.properties", name = "cc_previous_bootstrap_properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${NEXTLABS_CC_HOME}/access-key.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${NEXTLABS_CC_HOME}/server/configuration/application.properties", name = "cc_application_properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${NEXTLABS_CC_HOME}/server/configuration/bootstrap.properties", name = "cc_bootstrap_properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:${NEXTLABS_CC_HOME}/installation.properties", name = "cc_installation_properties", ignoreResourceNotFound = true)
@EnableAsync
@ConfigurationPropertiesScan
public class InstallerApplication extends SpringBootServletInitializer implements CommandLineRunner {

    private static final Marker INSTALLER_CONSOLE_MARKER = MarkerFactory.getMarker("INSTALLER_CONSOLE");
    @Autowired
    private CcProperties ccProperties;
    @Autowired
    private InstallService installService;
    @Autowired
    private InstallerConfigurationService installerCOnfigurationService;
    @Autowired
    private ServiceManagementService serviceManagementService;
    @Autowired
    private UninstallService uninstallService;

    @Override
    public void run(String... args) throws Exception {
        if (ArrayUtils.isNotEmpty(args)) {
            if (CommandLineOptionsHelper.has(CommandOption.REMOVE_SERVICES)) {
                removeServices();
            } else if (CommandLineOptionsHelper.has(CommandOption.STOP_SERVICES)) {
                stopServices();
            } else if (CommandLineOptionsHelper.has(CommandOption.CONFIGURE_INSTALLER)) {
                installerCOnfigurationService.configureInstaller();
            } else if (CommandLineOptionsHelper.has(CommandOption.RUN)
                    || CommandLineOptionsHelper.has(CommandOption.START)) {
                install();
            } else if (CommandLineOptionsHelper.has(CommandOption.UNINSTALL)) {
                uninstallService.uninstall();
            } else {
                CommandLineOptionsHelper.printHelp();
            }
        }
    }

    private void removeServices() throws IOException {
        serviceManagementService.deleteCCServices();
        LoggerFactory.getLogger(InstallerApplication.class)
                .info(INSTALLER_CONSOLE_MARKER, "Control Center services have been deleted.");
    }

    private void stopServices() throws IOException {
        serviceManagementService.stopCCServices();
        LoggerFactory.getLogger(InstallerApplication.class)
                .info(INSTALLER_CONSOLE_MARKER, "Control Center services have been stopped.");
    }

    private void install() throws Exception {
        installService.install(ccProperties).get();
        installService.createCcStartFiles();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        try {
            System.setProperty("nextlabs.cc.web-installer", "true");
            CommandLineOptionsHelper.parse(null);
            ParameterHelper.setInitParameters();
        } catch (Exception e) {
            throw new InstallerException(e);
        }
        builder.bannerMode(Banner.Mode.OFF);
        return super.configure(builder);
    }

    public static void main(String[] args) {
        try {
            CommandLineOptionsHelper.parse(args);
            ParameterHelper.setInitParameters();
            SpringApplication application = new SpringApplication(InstallerApplication.class);
            application.setAddCommandLineProperties(false);
            application.setWebApplicationType(WebApplicationType.NONE);
            application.run(args).close();
        } catch (Exception e) {
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            if (rootCause instanceof ConstraintViolationException) {
                ValidationExceptionHandler.getErrors((ConstraintViolationException) rootCause).forEach((field, errors) -> {
                    StringBuilder message = new StringBuilder()
                            .append(field.replace("install.ccProperties", "cc.nextlabs"));
                    errors.forEach(error -> message.append(System.lineSeparator()).append("\t").append(error));
                    LoggerFactory.getLogger(InstallerApplication.class)
                            .info(INSTALLER_CONSOLE_MARKER, message.toString());
                });
            } else {
                Logger logger = LoggerFactory.getLogger(InstallerApplication.class);
                logger.error("Installer error", e);
                logger.info(INSTALLER_CONSOLE_MARKER,
                        "An error has occurred. Please refer to installer.log for more details.");
                logger.info(INSTALLER_CONSOLE_MARKER, rootCause == null ? e.getMessage() : rootCause.getMessage());
            }
        }
    }

}
