package com.nextlabs.destiny.cc.installer.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nextlabs.destiny.cc.installer.annotations.CcServicesStopped;
import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.services.impl.WindowsServiceManagementServiceImpl;

/**
 * Validator to test if Control Center services are stopped.
 *
 * @author Sachindra Dasun
 */
public class CcServicesStoppedValidator implements ConstraintValidator<CcServicesStopped, CcProperties> {

    private static final Logger logger = LoggerFactory.getLogger(CcServicesStoppedValidator.class);

    @Override
    public boolean isValid(CcProperties ccProperties, ConstraintValidatorContext context) {
        if (SystemUtils.IS_OS_WINDOWS) {
            return isWindowsServiceStopped(WindowsServiceManagementServiceImpl.SERVICE_NAME_CC)
                    && isWindowsServiceStopped(WindowsServiceManagementServiceImpl.SERVICE_NAME_CC_ES);
        } else if (SystemUtils.IS_OS_LINUX) {
            return isLinuxServiceStopped(WindowsServiceManagementServiceImpl.SERVICE_NAME_CC)
                    && isLinuxServiceStopped(WindowsServiceManagementServiceImpl.SERVICE_NAME_CC_ES);
        }
        return true;
    }

    private boolean isWindowsServiceStopped(String serviceName) {
        List<String> arguments = new ArrayList<>();
        arguments.add("query");
        arguments.add(serviceName);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine("sc");
        arguments.forEach(commandLine::addArgument);
        logger.trace("Running command: {}", commandLine);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        defaultExecutor.setStreamHandler(new PumpStreamHandler(byteArrayOutputStream));
        try {
            defaultExecutor.execute(commandLine);
            String output = byteArrayOutputStream.toString();
            if (StringUtils.isNotEmpty(output)) {
                output = output.toUpperCase();
                return !output.contains("RUNNING") && !output.contains("START_PENDING")
                        && !output.contains("STOP_PENDING");
            }
        } catch (Exception e) {
            logger.debug("Error in checking for service status", e);
            String output = byteArrayOutputStream.toString();
            if (StringUtils.isNotEmpty(output)) {
                logger.info(output);
            }
        }
        return true;
    }

    private boolean isLinuxServiceStopped(String serviceName) {
        List<String> arguments = new ArrayList<>();
        arguments.add("status");
        arguments.add(String.format("%s.service", serviceName));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine("systemctl");
        arguments.forEach(commandLine::addArgument);
        logger.trace("Running command: {}", commandLine);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        defaultExecutor.setStreamHandler(new PumpStreamHandler(byteArrayOutputStream));
        try {
            defaultExecutor.execute(commandLine);
            String output = byteArrayOutputStream.toString();
            if (StringUtils.isNotEmpty(output)) {
                output = output.toUpperCase();
                return !output.contains("ACTIVE: ACTIVE") && !output.contains("ACTIVE: DEACTIVATING");
            }
        } catch (Exception e) {
            logger.debug("Error in checking for service status", e);
            String output = byteArrayOutputStream.toString();
            if (StringUtils.isNotEmpty(output)) {
                logger.debug(output);
            }
        }
        return true;
    }

}
