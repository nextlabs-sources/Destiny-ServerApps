package com.nextlabs.destiny.cc.installer.services.impl;

import static com.nextlabs.destiny.cc.installer.services.impl.WindowsServiceManagementServiceImpl.SERVICE_DESCRIPTION_CC;
import static com.nextlabs.destiny.cc.installer.services.impl.WindowsServiceManagementServiceImpl.SERVICE_DESCRIPTION_CC_ES;
import static com.nextlabs.destiny.cc.installer.services.impl.WindowsServiceManagementServiceImpl.SERVICE_DISPLAY_NAME_CC;
import static com.nextlabs.destiny.cc.installer.services.impl.WindowsServiceManagementServiceImpl.SERVICE_DISPLAY_NAME_CC_ES;
import static com.nextlabs.destiny.cc.installer.services.impl.WindowsServiceManagementServiceImpl.SERVICE_NAME_CC;
import static com.nextlabs.destiny.cc.installer.services.impl.WindowsServiceManagementServiceImpl.SERVICE_NAME_CC_ES;
import static com.nextlabs.destiny.cc.installer.services.impl.WindowsServiceManagementServiceImpl.SERVICE_NAME_CC_PV;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.environment.EnvironmentUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import com.nextlabs.destiny.cc.installer.enums.Task;
import com.nextlabs.destiny.cc.installer.services.ProgressService;
import com.nextlabs.destiny.cc.installer.services.ServiceManagementService;

/**
 * Service implementation for Linux service management.
 *
 * @author Sachindra Dasun
 */
public class LinuxServiceManagementServiceImpl implements ServiceManagementService {

    private static final Logger logger = LoggerFactory.getLogger(LinuxServiceManagementServiceImpl.class);
    @Autowired
    private CcProperties ccProperties;
    @Autowired
    private ProgressService progressService;

    /**
     * Stop running Control Center services.
     *
     * @throws IOException if an error occured
     */
    @Override
    public void stopCCServices() throws IOException {
        controlService(SERVICE_NAME_CC, false);
        if (ccProperties.isManagementServerInstance()) {
            controlService(SERVICE_NAME_CC_ES, false);
            stopPolicyValidatorService();
        }
        logger.info("Stopeed Control Center services");
    }

    /**
     * Delete Control Center services.
     *
     * @throws IOException if an error occurred
     */
    @Override
    public void deleteCCServices() throws IOException {
        boolean ccServiceDeleted = deleteCCService();
        if (ccServiceDeleted) {
            logger.info("Deleted Control Center service");
        }
        if (ccProperties.isManagementServerInstance()) {
            boolean ccEsServiceDeleted = deleteElasticsearchService();
            if (ccEsServiceDeleted) {
                logger.info("Deleted Control Center Elasticsearch service");
            }
            deletePolicyValidatorService();
            logger.info("Deleted Control Center Policy Validator service");
        }
        // Delete CC services created in init.d before 8.7
        Path ccServiceFilePath = Paths.get("/etc", "init.d",
                WindowsServiceManagementServiceImpl.SERVICE_NAME_CC);
        if (ccServiceFilePath.toFile().exists()) {
            Files.delete(ccServiceFilePath);
        }
        Path elasticsearchServiceFilePath = Paths.get("/etc", "init.d",
                WindowsServiceManagementServiceImpl.SERVICE_NAME_CC_ES);
        if (elasticsearchServiceFilePath.toFile().exists()) {
            Files.delete(elasticsearchServiceFilePath);
        }
    }

    /**
     * Create Control Center services.
     *
     * @throws IOException if an error occurred
     */
    @Override
    public void createCCServices() throws IOException {
        deleteCCServices();

        if (ccProperties.isManagementServerInstance()) {
            progressService.setCurrentTask(Task.CREATE_CONTROL_CENTER_ELASTICSEARCH_SERVICE);
            createElasticsearchService();
            logger.info("Completed creating Control Center Elasticsearch service");
            progressService.setCurrentTask(Task.CREATE_CONTROL_CENTER_POLICY_VALIDATOR_SERVICE);
            createPolicyValidatorService();
            logger.info("Completed creating Control Center Policy Validator service");
        }

        progressService.setCurrentTask(Task.CREATE_CONTROL_CENTER_SERVICE);
        createCCService();
        logger.info("Completed creating Control Center service");
    }

    private void createElasticsearchService() throws IOException {
        String serviceFileContent = new StrSubstitutor(getServiceProperties()).replace(
                "[Unit]\n" +
                        "Description=${service.es.description}\n" +
                        "After=syslog.target network.target\n\n" +
                        "[Service]\n" +
                        "Type=forking\n" +
                        "Restart=always\n" +
                        "RestartSec=10\n" +
                        "User=${cc.user}\n" +
                        "Group=${cc.user-group}\n" +
                        "TasksMax=1024\n\n" +
                        "Environment=\"ES_HOME=${cc.home}/server/data/search-index\"\n" +
                        "Environment=\"ES_JAVA_HOME=${cc.home}/java/jre\"\n" +
                        "ExecStart=${cc.home}/server/data/search-index/bin/elasticsearch -d -p ${cc.home}/server/data/search-index/data/controlcenteres.pid\n");
        Files.write(Paths.get("/etc", "systemd", "system", "ControlCenterES.service"), serviceFileContent.getBytes());
        configureService(SERVICE_NAME_CC_ES, true);
    }

    private void createCCService() throws IOException {
        String serviceFileContent = new StrSubstitutor(getServiceProperties()).replace(
                "[Unit]\n" +
                        "Description=${service.cc.description}\n" +
                        (ccProperties.isManagementServerInstance() ? "Requires=ControlCenterES.service\n" : "") +
                        "After=syslog.target network.target\n\n" +
                        "[Service]\n" +
                        "Type=forking\n" +
                        "Restart=always\n" +
                        "RestartSec=10\n" +
                        "User=${cc.user}\n" +
                        "Group=${cc.user-group}\n" +
                        "UMask=0007\n" +
                        "TasksMax=2048\n\n" +
                        "Environment=\"NEXTLABS_CC_HOME=${cc.home}\"\n\n" +
                        "ExecStart=${cc.home}/server/tomcat/bin/startup.sh -config ${cc.home.modified}/server/configuration/server.xml\n\n" +
                        "ExecStop=${cc.home}/server/tomcat/bin/shutdown.sh -force\n\n" +
                        "[Install]\n" +
                        "WantedBy=multi-user.target\n");
        Files.write(Paths.get("/etc", "systemd", "system", "CompliantEnterpriseServer.service"), serviceFileContent.getBytes());
        configureService(SERVICE_NAME_CC, true);
    }

    private boolean deleteCCService() throws IOException {
        Path serviceFilePath = Paths.get("/etc", "systemd", "system",
                String.format("%s.service", SERVICE_NAME_CC));
        if (serviceFilePath.toFile().exists()) {
            controlService(SERVICE_NAME_CC, false);
            configureService(SERVICE_NAME_CC, false);
            Files.delete(serviceFilePath);
            return true;
        }
        return false;
    }

    private boolean deleteElasticsearchService() throws IOException {
        Path serviceFilePath = Paths.get("/etc", "systemd", "system",
                String.format("%s.service", SERVICE_NAME_CC_ES));
        if (serviceFilePath.toFile().exists()) {
            controlService(SERVICE_NAME_CC_ES, false);
            configureService(SERVICE_NAME_CC_ES, false);
            Files.delete(serviceFilePath);
            return true;
        }
        return false;
    }

    private Properties getServiceProperties() {
        Properties properties = new Properties();
        properties.put("cc.home", ccProperties.getHome());
        properties.put("cc.home.modified", ccProperties.getHome().replace(" ", "\\ "));
        properties.put("cc.user", ccProperties.getUser().getName());
        properties.put("cc.user-group", ccProperties.getUser().getGroup());
        properties.put("service.cc.display-name", SERVICE_DISPLAY_NAME_CC);
        properties.put("service.es.display-name", SERVICE_DISPLAY_NAME_CC_ES);
        properties.put("service.cc.description", SERVICE_DESCRIPTION_CC);
        properties.put("service.es.description", SERVICE_DESCRIPTION_CC_ES);
        return properties;
    }

    private void configureService(String serviceName, boolean enable) throws IOException {
        List<String> arguments = new ArrayList<>();
        arguments.add(enable ? "enable" : "disable");
        arguments.add(String.format("%s.service", serviceName));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine("systemctl");
        arguments.forEach(commandLine::addArgument);
        logger.trace("Running command: {}", commandLine);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        defaultExecutor.setStreamHandler(new PumpStreamHandler(byteArrayOutputStream));
        try {
            defaultExecutor.execute(commandLine);
        } finally {
            String output = byteArrayOutputStream.toString();
            if (StringUtils.isNotEmpty(output)) {
                logger.info(output);
            }
        }
        logger.info("{} the service {}", enable ? "Enabled" : "Disabled", serviceName);
    }

    private void controlService(String serviceName, boolean start) throws IOException {
        List<String> arguments = new ArrayList<>();
        arguments.add(start ? "start" : "stop");
        arguments.add(String.format("%s.service", serviceName));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine("systemctl");
        arguments.forEach(commandLine::addArgument);
        logger.trace("Running command: {}", commandLine);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        defaultExecutor.setStreamHandler(new PumpStreamHandler(byteArrayOutputStream));
        try {
            defaultExecutor.execute(commandLine);
        } finally {
            String output = byteArrayOutputStream.toString();
            if (StringUtils.isNotEmpty(output)) {
                logger.info(output);
            }
        }
        logger.info("{} the service {}", start ? "Started" : "Stopped", serviceName);
    }

    private void createPolicyValidatorService() throws IOException {
        Path policyValidatorDirectory = Paths.get(ccProperties.getHome(), "tools", "policy-validator");
        Path executablePath = policyValidatorDirectory.resolve("node");
        if (!executablePath.toFile().exists()) {
            return;
        }
        List<String> arguments = new ArrayList<>();
        arguments.add("installService.js");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine(executablePath.toString());
        arguments.forEach(commandLine::addArgument);
        logger.trace("Running command: {}", commandLine);
        DefaultExecutor defaultExecutor = new DefaultExecutor();

        defaultExecutor.setWorkingDirectory(policyValidatorDirectory.toFile());
        defaultExecutor.setStreamHandler(new PumpStreamHandler(byteArrayOutputStream));
        Map<String, String> environment = EnvironmentUtils.getProcEnvironment();
        environment.put("CC_HOME", ccProperties.getHome());
        try {
            defaultExecutor.execute(commandLine, environment);
        } finally {
            String output = byteArrayOutputStream.toString();
            if (StringUtils.isNotEmpty(output)) {
                logger.info(output);
            }
        }
        configureService(SERVICE_NAME_CC_PV, true);
    }

    private void stopPolicyValidatorService() throws IOException {
        Path policyValidatorDirectory = Paths.get(ccProperties.getHome(), "tools", "policy-validator");
        Path executablePath = policyValidatorDirectory.resolve("node");
        if (!executablePath.toFile().exists()) {
            return;
        }
        List<String> arguments = new ArrayList<>();
        arguments.add("stopService.js");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine(executablePath.toString());
        arguments.forEach(commandLine::addArgument);
        logger.trace("Running command: {}", commandLine);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        defaultExecutor.setWorkingDirectory(policyValidatorDirectory.toFile());
        defaultExecutor.setStreamHandler(new PumpStreamHandler(byteArrayOutputStream));
        try {
            defaultExecutor.execute(commandLine);
        } finally {
            String output = byteArrayOutputStream.toString();
            if (StringUtils.isNotEmpty(output)) {
                logger.info(output);
            }
        }
    }

    private void deletePolicyValidatorService() throws IOException {
        Path policyValidatorDirectory = Paths.get(ccProperties.getHome(), "tools", "policy-validator");
        Path executablePath = policyValidatorDirectory.resolve("node");
        if (!executablePath.toFile().exists()) {
            return;
        }
        List<String> arguments = new ArrayList<>();
        arguments.add("uninstallService.js");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine(executablePath.toString());
        arguments.forEach(commandLine::addArgument);
        logger.trace("Running command: {}", commandLine);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        defaultExecutor.setWorkingDirectory(policyValidatorDirectory.toFile());
        defaultExecutor.setStreamHandler(new PumpStreamHandler(byteArrayOutputStream));
        try {
            defaultExecutor.execute(commandLine);
        } catch (IOException e) {
            if (RunningMode.INSTALLATION != ccProperties.getRunningMode()) {
                logger.info("Error in deleting Control Center Policy Validator service", e);
                throw e;
            }
        } finally {
            if (RunningMode.INSTALLATION != ccProperties.getRunningMode()) {
                String output = byteArrayOutputStream.toString();
                if (StringUtils.isNotEmpty(output)) {
                    logger.info(output);
                }
            }
        }
    }

}
