package com.nextlabs.destiny.cc.installer.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.environment.EnvironmentUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import com.nextlabs.destiny.cc.installer.enums.Task;
import com.nextlabs.destiny.cc.installer.services.ProgressService;
import com.nextlabs.destiny.cc.installer.services.ServiceManagementService;

/**
 * Service implementation for Windows service management.
 *
 * @author Sachindra Dasun
 */
public class WindowsServiceManagementServiceImpl implements ServiceManagementService {

    public static final String SERVICE_DESCRIPTION_CC = "Controls NextLabs Control Center";
    public static final String SERVICE_DESCRIPTION_CC_ES = "Indexes NextLabs Control Center Data";
    public static final String SERVICE_DISPLAY_NAME_CC = "Control Center Policy Server";
    public static final String SERVICE_DISPLAY_NAME_CC_ES = "Control Center Data Index Engine";
    public static final String SERVICE_NAME_CC = "CompliantEnterpriseServer";
    public static final String SERVICE_NAME_CC_ES = "ControlCenterES";
    public static final String SERVICE_NAME_CC_PV = "controlcenterpolicyvalidator";
    private static final Logger logger = LoggerFactory.getLogger(WindowsServiceManagementServiceImpl.class);
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
        stopCCService();
        if (ccProperties.isManagementServerInstance()) {
            stopElasticsearchService();
            stopPolicyValidatorService();
        }
        logger.info("Stopped Control Center services");
    }

    /**
     * Delete Control Center services.
     *
     * @throws IOException if an error occurred
     */
    @Override
    public void deleteCCServices() throws IOException {
        deleteCCService();
        logger.info("Deleted Control Center service");
        if (ccProperties.isManagementServerInstance()) {
            deleteElasticsearchService();
            logger.info("Deleted Control Center Elasticsearch service");
            killPolicyValidatorService();
            deletePolicyValidatorService();
            logger.info("Deleted Control Center Policy Validator service");
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
        Path executablePath = Paths.get(ccProperties.getHome(), "server", "data", "search-index", "bin",
                "elasticsearch-service.bat");
        if (!executablePath.toFile().exists()) {
            return;
        }
        List<String> arguments = new ArrayList<>();
        arguments.add("install");
        arguments.add("ControlCenterES");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine(executablePath.toString());
        arguments.forEach(commandLine::addArgument);
        logger.trace("Running command: {}", commandLine);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        defaultExecutor.setStreamHandler(new PumpStreamHandler(byteArrayOutputStream));
        try {
            Map<String, String> environment = EnvironmentUtils.getProcEnvironment();
            environment.put("ES_JAVA_HOME", Paths.get(ccProperties.getHome(), "java", "jre").toString());
            environment.put("ES_HOME", Paths.get(ccProperties.getHome(), "server", "data", "search-index").toString());
            environment.put("ES_START_TYPE", "auto");
            environment.put("SERVICE_DISPLAY_NAME", SERVICE_DISPLAY_NAME_CC_ES);
            environment.put("SERVICE_DESCRIPTION", SERVICE_DESCRIPTION_CC_ES);
            defaultExecutor.execute(commandLine, environment);
        } finally {
            String output = byteArrayOutputStream.toString();
            if (StringUtils.isNotEmpty(output)) {
                logger.info(output);
            }
        }
    }

    private void createCCService() throws IOException {
        List<String> arguments = new ArrayList<>();
        arguments.add("//IS//CompliantEnterpriseServer");
        arguments.add("--DisplayName");
        arguments.add(SERVICE_DISPLAY_NAME_CC);
        arguments.add("--Description");
        arguments.add(SERVICE_DESCRIPTION_CC);
        arguments.add("--Install");
        arguments.add(String.format("%s/server/tomcat/bin/control-center.exe", ccProperties.getHome()));
        arguments.add("--Classpath");
        arguments.add(String.format("%s/server/tomcat/bin/bootstrap.jar;%s/server/tomcat/bin/tomcat-juli.jar;%s/server/tomcat/shared/lib/nxl-filehandler.jar",
                ccProperties.getHome(), ccProperties.getHome(), ccProperties.getHome()));
        arguments.add("--Jvm");
        arguments.add(String.format("%s/java/jre/bin/server/jvm.dll", ccProperties.getHome()));
        arguments.add("++JvmOptions");
        arguments.add(String.format("-Dcatalina.base=%s/server/tomcat", ccProperties.getHome()));
        arguments.add("++JvmOptions");
        arguments.add(String.format("-Dcatalina.home=%s/server/tomcat", ccProperties.getHome()));
        arguments.add("++JvmOptions");
        arguments.add(String.format("-Djava.io.tmpdir=%s/server/tomcat/temp", ccProperties.getHome()));
        arguments.add("++JvmOptions");
        arguments.add(String.format("-Dlog4j.configurationFile=%s/server/configuration/log4j2.xml", ccProperties.getHome()));
        arguments.add("++JvmOptions");
        arguments.add(String.format("-Dlogging.config=file:%s/server/configuration/log4j2.xml", ccProperties.getHome()));
        arguments.add("++JvmOptions");
        arguments.add("-Dorg.springframework.boot.logging.LoggingSystem=none");
        arguments.add("++JvmOptions");
        arguments.add("-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Jdk14Logger");
        arguments.add("++JvmOptions");
        arguments.add(String.format("-Dserver.config.path=%s/server/configuration", ccProperties.getHome()));
        arguments.add("++JvmOptions");
        arguments.add(String.format("-Dcc.home=%s", ccProperties.getHome()));
        arguments.add("++JvmOptions");
        arguments.add(String.format("-Dspring.cloud.bootstrap.location=%s/server/configuration/bootstrap.properties", ccProperties.getHome()));
        arguments.add("++JvmOptions");
        arguments.add(String.format("-Dserver.hostname=%s", ccProperties.getHostname()));
        arguments.add("++JvmOptions");
        arguments.add("-Djdk.tls.rejectClientInitiatedRenegotiation=true");
        arguments.add("++JvmOptions");
        arguments.add("-Xverify:none");
        arguments.add("++JvmOptions");
        arguments.add("-Djava.locale.providers=COMPAT,CLDR");
        arguments.add("++JvmOptions");
        arguments.add("-Dsun.lang.ClassLoader.allowArraySyntax=true");
        arguments.add("++JvmOptions");
        arguments.add(String.format("-Xms%s", ccProperties.getJava().getXms()));
        arguments.add("++JvmOptions");
        arguments.add(String.format("-Xmx%s", ccProperties.getJava().getXmx()));
        arguments.add("++JvmOptions");
        arguments.add("-Dconsole.install.mode=OPN");
        arguments.add("--JvmMx");
        arguments.add("2048");
        arguments.add("--JvmMs");
        arguments.add("1024");
        arguments.add("--LogPath");
        arguments.add(String.format("%s/server/logs/", ccProperties.getHome()));
        arguments.add("--ServiceUser");
        arguments.add("LocalSystem");
        arguments.add("--Startup");
        arguments.add("auto");
        arguments.add("--StartMode");
        arguments.add("jvm");
        arguments.add("--StartClass");
        arguments.add("org.apache.catalina.startup.Bootstrap");
        arguments.add("++StartParams");
        arguments.add("-config;../configuration/server.xml;start");
        arguments.add("--StopMode");
        arguments.add("jvm");
        arguments.add("--StopClass");
        arguments.add("org.apache.catalina.startup.Bootstrap");
        arguments.add("++StopParams");
        arguments.add("-config;../configuration/server.xml;stop");
        if (ccProperties.isManagementServerInstance()) {
            arguments.add("++DependsOn");
            arguments.add("ControlCenterES");
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine(Paths.get(ccProperties.getHome(), "server", "tomcat", "bin",
                "control-center.exe").toString());
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
    }

    private void deleteCCService() throws IOException {
        List<String> arguments = new ArrayList<>();
        arguments.add("//DS//CompliantEnterpriseServer");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine(Paths.get(ccProperties.getHome(), "server", "tomcat", "bin",
                "control-center.exe").toString());
        arguments.forEach(commandLine::addArgument);
        logger.trace("Running command: {}", commandLine);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        defaultExecutor.setStreamHandler(new PumpStreamHandler(byteArrayOutputStream));
        try {
            defaultExecutor.execute(commandLine);
        } catch (IOException e) {
            if (RunningMode.INSTALLATION != ccProperties.getRunningMode()) {
                logger.info("Error in deleting Control Center service", e);
                if (!e.getMessage().contains("Exit value: 9")) {
                    throw e;
                }
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

    private void deleteElasticsearchService() throws IOException {
        Path executablePath = Paths.get(ccProperties.getHome(), "server", "data",
                "search-index", "bin", "elasticsearch-service.bat");
        if (!executablePath.toFile().exists()) {
            return;
        }
        List<String> arguments = new ArrayList<>();
        arguments.add("remove");
        arguments.add("ControlCenterES");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine(executablePath.toString());
        arguments.forEach(commandLine::addArgument);
        logger.trace("Running command: {}", commandLine);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        defaultExecutor.setStreamHandler(new PumpStreamHandler(byteArrayOutputStream));
        try {
            defaultExecutor.execute(commandLine);
        } catch (IOException e) {
            if (RunningMode.INSTALLATION != ccProperties.getRunningMode()) {
                logger.info("Error in deleting Control Center Elasticsearch service", e);
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

    private void stopCCService() throws IOException {
        List<String> arguments = new ArrayList<>();
        arguments.add("//SS//CompliantEnterpriseServer");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine(Paths.get(ccProperties.getHome(), "server", "tomcat", "bin",
                "control-center.exe").toString());
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
    }

    private void stopElasticsearchService() throws IOException {
        Path executablePath = Paths.get(ccProperties.getHome(), "server", "data", "search-index", "bin",
                "elasticsearch-service.bat");
        if (!executablePath.toFile().exists()) {
            return;
        }
        List<String> arguments = new ArrayList<>();
        arguments.add("stop");
        arguments.add("ControlCenterES");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine(executablePath.toString());
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
    }

    private void createPolicyValidatorService() throws IOException {
        Path policyValidatorDirectory = Paths.get(ccProperties.getHome(), "tools", "policy-validator");
        Path executablePath = policyValidatorDirectory.resolve("node.exe");
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
    }

    private void stopPolicyValidatorService() throws IOException {
        Path policyValidatorDirectory = Paths.get(ccProperties.getHome(), "tools", "policy-validator");
        Path executablePath = policyValidatorDirectory.resolve("node.exe");
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

    private void killPolicyValidatorService() {
        List<String> arguments = new ArrayList<>();
        arguments.add("/f");
        arguments.add("/im");
        arguments.add("node.exe");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine("taskkill");
        arguments.forEach(commandLine::addArgument);
        logger.trace("Running command: {}", commandLine);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        defaultExecutor.setStreamHandler(new PumpStreamHandler(byteArrayOutputStream));
        try {
            defaultExecutor.execute(commandLine);
        } catch (IOException e) {
            if (RunningMode.INSTALLATION != ccProperties.getRunningMode()) {
                logger.warn("Error in trying to kill Control Center Policy Validator service: {}", e.getMessage());
            }
        } finally {
            if (RunningMode.INSTALLATION != ccProperties.getRunningMode()) {
                String output = byteArrayOutputStream.toString();
                if (StringUtils.isNotEmpty(output)) {
                    logger.debug(output);
                }
            }
        }
    }

    private void deletePolicyValidatorService() throws IOException {
        Path policyValidatorDirectory = Paths.get(ccProperties.getHome(), "tools", "policy-validator");
        Path executablePath = policyValidatorDirectory.resolve("node.exe");
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
