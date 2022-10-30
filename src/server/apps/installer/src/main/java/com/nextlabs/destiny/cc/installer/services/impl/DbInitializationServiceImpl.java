package com.nextlabs.destiny.cc.installer.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nextlabs.cc.common.enums.DbType;
import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.config.properties.Version;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import com.nextlabs.destiny.cc.installer.enums.Task;
import com.nextlabs.destiny.cc.installer.helpers.DbHelper;
import com.nextlabs.destiny.cc.installer.helpers.ParameterHelper;
import com.nextlabs.destiny.cc.installer.services.DbInitializationService;
import com.nextlabs.destiny.cc.installer.services.ProgressService;

/**
 * Service implementation for database initialization.
 *
 * @author Sachindra Dasun
 */
@Service
public class DbInitializationServiceImpl implements DbInitializationService {

    private static final Logger logger = LoggerFactory.getLogger(DbInitializationServiceImpl.class);

    @Autowired
    private CcProperties ccProperties;
    @Autowired
    private ProgressService progressService;
    private PasswordEncoder delegatingPasswordEncoder;

    /**
     * Initialize Control Center database.
     *
     * @throws IOException if an error occurred
     */
    @Override
    public void initialize() throws IOException {
        if (!ccProperties.isManagementServerInstance()) {
            return;
        }
        Version versionFromDb = ccProperties.getDb().getCcVersionFromDb();
        if (ccProperties.getDb().getCcVersionFromDb() == null ||
                versionFromDb.before(ccProperties.getVersion())) {
            Version previousVersion = versionFromDb == null ? ccProperties.getPreviousVersion() : versionFromDb;
            logger.info("Started initializing Control Center database");
            try {
                logger.info("Configure database initialization files");
                progressService.setCurrentTask(Task.CONFIGURE_DATABASE_INITIALIZATION);
                configure();

                logger.info("Start dictionary repository initialization");
                progressService.setCurrentTask(Task.INITIALIZE_DICTIONARY_REPOSITORY);
                initDatabase(Paths.get(ccProperties.getHome(), "tools", "dbInit", "dictionary", "dictionary.cfg"),
                        Paths.get(ccProperties.getHome(), "tools", "dbInit", "dictionary"), previousVersion);
                logger.info("Completed dictionary repository initialization");

                logger.info("Start management repository initialization");
                progressService.setCurrentTask(Task.INITIALIZE_MANAGEMENT_REPOSITORY);
                initDatabase(Paths.get(ccProperties.getHome(), "tools", "dbInit", "mgmt", "mgmt.cfg"),
                        Paths.get(ccProperties.getHome(), "tools", "dbInit", "mgmt"), previousVersion);
                logger.info("Completed management repository initialization");

                logger.info("Start PF repository initialization");
                progressService.setCurrentTask(Task.INITIALIZE_PF_REPOSITORY);
                initDatabase(Paths.get(ccProperties.getHome(), "tools", "dbInit", "pf", "pf.cfg"),
                        Paths.get(ccProperties.getHome(), "tools", "dbInit", "pf"), previousVersion);
                logger.info("Completed PF repository initialization");

                logger.info("Start activity repository initialization");
                progressService.setCurrentTask(Task.INITIALIZE_ACTIVITY_REPOSITORY);
                initDatabase(Paths.get(ccProperties.getHome(), "tools", "dbInit", "activity", "activity.cfg"),
                        Paths.get(ccProperties.getHome(), "tools", "dbInit", "activity"), previousVersion);
                logger.info("Completed activity repository initialization");

                if (ccProperties.getRunningMode() == RunningMode.INSTALLATION) {
                    logger.info("Start data initialization");
                    progressService.setCurrentTask(Task.INITIALIZE_DATA);
                    initData();
                    logger.info("Completed data initialization");
                }
                progressService.setCurrentTask(Task.CLEAN_DATABASE_INITIALIZATION_FILES);
            } finally {
                complete();
            }
            logger.info("Completed initializing Control Center database");
        }
    }

    private void configure() throws IOException {
        Path pfFilePath = Paths.get(ccProperties.getHome(), "tools", "dbInit", "pf", "pf.cfg");
        Files.copy(pfFilePath, Paths.get(ccProperties.getHome(), "tools", "dbInit", "pf", "pf-default.cfg"),
                StandardCopyOption.REPLACE_EXISTING);
        try (Stream<String> lines = Files.lines(pfFilePath)) {
            Files.write(pfFilePath, lines.map(line ->
                    line.replace("[BLUEJUNGLE_HOME]",
                            FilenameUtils.separatorsToUnix(Paths.get(ccProperties.getHome(), "tools",
                                    "dbInit", "pf").toString())))
                    .collect(Collectors.toList()));
        }
        Path mgmtFilePath = Paths.get(ccProperties.getHome(), "tools", "dbInit", "mgmt", "mgmt.cfg");
        Files.copy(mgmtFilePath, Paths.get(ccProperties.getHome(), "tools", "dbInit", "mgmt", "mgmt-default.cfg"),
                StandardCopyOption.REPLACE_EXISTING);
        try (Stream<String> lines = Files.lines(mgmtFilePath)) {
            Files.write(mgmtFilePath, lines.map(line -> {
                if (line.contains("[ADMINISTRATOR_PASSWORD]")) {
                    return line.replace("[ADMINISTRATOR_PASSWORD]",
                            ccProperties.getRunningMode() == RunningMode.UPGRADE ? "" :
                                    delegatingPasswordEncoder.encode(ccProperties.getAdminPassword()));
                }
                return line;
            }).collect(Collectors.toList()));
        }
        Path hostnameFolderPath = Paths.get(ParameterHelper.INIT_CC_HOME, "server", "logs",
                ccProperties.getHostname());
        boolean hostnameFolderCreated = hostnameFolderPath.toFile().mkdirs();
        if (hostnameFolderCreated) {
            logger.info("Folder created: {}", hostnameFolderPath);
        }
        Path loggingConfigurationFilePath = Paths.get(ccProperties.getHome(), "server", "configuration", "dbinit-logging.properties");
        Files.copy(loggingConfigurationFilePath, Paths.get(ccProperties.getHome(), "server", "configuration", "dbinit-logging-default.properties"),
                StandardCopyOption.REPLACE_EXISTING);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cc.home", FilenameUtils.separatorsToUnix(ParameterHelper.INIT_CC_HOME));
        parameters.put("server.hostname", ccProperties.getHostname());
        StrSubstitutor strSubstitutor = new StrSubstitutor(parameters);
        try (Stream<String> lines = Files.lines(loggingConfigurationFilePath)) {
            Files.write(loggingConfigurationFilePath, lines.map(strSubstitutor::replace)
                    .collect(Collectors.toList()));
        }
    }

    private void initDatabase(Path configFilePath, Path libraryPath, Version previousVersion) throws IOException {
        DbType dbType = ccProperties.getDb().getDbType();
        List<String> arguments = new ArrayList<>();
        arguments.add("-noverify");
        arguments.add(String.format("-Ddb.url=%s", ccProperties.getDb().getUrl()));
        arguments.add(String.format("-Ddb.username=%s", ccProperties.getDb().getUsername()));
        arguments.add(String.format("-Ddb.password=%s", ccProperties.getDb().getPassword()));
        arguments.add(String.format("-Ddb.driver=%s", dbType.getDriver()));
        arguments.add(String.format("-Ddb.dialect=%s", dbType.getHibernate2Dialect()));
        arguments.add(String.format("-Djava.util.logging.config.file=%s",
                Paths.get(ccProperties.getHome(), "server", "configuration", "dbinit-logging.properties")));
        arguments.add(String.format("-Dcc.home=%s", ccProperties.getHome()));
        arguments.add(String.format("-Dserver.hostname=%s", ccProperties.getHostname()));
        String jpdaOptsProcess = System.getenv("NEXTLABS_CC_PROCESS_JPDA_OPTS");
        if (StringUtils.isNotEmpty(jpdaOptsProcess)) {
            arguments.add(jpdaOptsProcess);
        }
        arguments.add("-jar");
        arguments.add(Paths.get(ccProperties.getHome(), "tools", "dbInit", "db-init.jar").toString());
        if (previousVersion == null) {
            arguments.add("-install");
        } else {
            arguments.add("-upgrade");
            arguments.add("-fromV");
            arguments.add(previousVersion.toString());
            arguments.add("-toV");
            arguments.add(ccProperties.getVersion().toString());
        }
        arguments.add("-config");
        arguments.add(configFilePath.toString());
        arguments.add("-connection");
        arguments.add(ccProperties.getHome());
        arguments.add("-libraryPath");
        arguments.add(libraryPath.toString());
        arguments.add("-quiet");

        dbInitTool(arguments);
    }

    private void initData() {
        initCompProfileTable();
    }

    private void complete() throws IOException {
        Path mgmtCfgFilePath = Paths.get(ccProperties.getHome(), "tools", "dbInit", "mgmt", "mgmt.cfg");
        boolean deleted = Files.deleteIfExists(mgmtCfgFilePath);
        if (deleted) {
            logger.debug("Deleted: {}", mgmtCfgFilePath);
        }
        Path pfDefaultConfigFilePath = Paths.get(ccProperties.getHome(), "tools", "dbInit", "pf", "pf-default.cfg");
        if (pfDefaultConfigFilePath.toFile().exists()) {
            Files.copy(pfDefaultConfigFilePath,
                    Paths.get(ccProperties.getHome(), "tools", "dbInit", "pf", "pf.cfg"),
                    StandardCopyOption.REPLACE_EXISTING);
            Files.delete(pfDefaultConfigFilePath);
        }
        Path mgmtDefaultConfigFilePath = Paths.get(ccProperties.getHome(), "tools", "dbInit", "mgmt", "mgmt-default.cfg");
        if (mgmtDefaultConfigFilePath.toFile().exists()) {
            Files.copy(mgmtDefaultConfigFilePath,
                    Paths.get(ccProperties.getHome(), "tools", "dbInit", "mgmt", "mgmt.cfg"),
                    StandardCopyOption.REPLACE_EXISTING);
            Files.delete(mgmtDefaultConfigFilePath);
        }
        Path defaultLoggingConfigurationFilePath = Paths.get(ccProperties.getHome(), "server", "configuration", "dbinit-logging-default.properties");
        if (defaultLoggingConfigurationFilePath.toFile().exists()) {
            Files.copy(defaultLoggingConfigurationFilePath,
                    Paths.get(ccProperties.getHome(), "server", "configuration", "dbinit-logging.properties"),
                    StandardCopyOption.REPLACE_EXISTING);
            Files.delete(defaultLoggingConfigurationFilePath);
        }
    }

    private void dbInitTool(List<String> arguments) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CommandLine commandLine = new CommandLine(Paths.get(ccProperties.getHome(), "java", "jre", "bin", "java")
                .toString());
        arguments.forEach(argument -> commandLine.addArgument(argument, false));
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

    private void initCompProfileTable() {
        JdbcTemplate jdbcTemplate = DbHelper.getJdbcTemplate();
        String dabsLocation = ccProperties.getCommProfile().getDabsLocation();
        if (StringUtils.isNotEmpty(dabsLocation)) {
            jdbcTemplate.update("UPDATE COMM_PROFILE SET DABS_LOCATION = ?", dabsLocation);
        }
        int heartBeatFreqTime = ccProperties.getCommProfile().getHeartBeatFreqTime();
        if (heartBeatFreqTime > 0) {
            jdbcTemplate.update("UPDATE COMM_PROFILE SET HEART_BEAT_FREQ_TIME = ?", heartBeatFreqTime);
        }
        String heartBeatFreqTimeUnit = ccProperties.getCommProfile().getHeartBeatFreqTimeUnit();
        if (StringUtils.isNotEmpty(heartBeatFreqTimeUnit)) {
            jdbcTemplate.update("UPDATE COMM_PROFILE SET HEART_BEAT_FREQ_TIME_UNIT = ?", heartBeatFreqTimeUnit);
        }
        if (ccProperties.getCommProfile().isPushEnabled()) {
            jdbcTemplate.update("UPDATE COMM_PROFILE SET PUSH_ENABLED = ?", true);
        }
    }

    @Autowired
    public void setDelegatingPasswordEncoder(PasswordEncoder delegatingPasswordEncoder) {
        this.delegatingPasswordEncoder = delegatingPasswordEncoder;
    }
}
