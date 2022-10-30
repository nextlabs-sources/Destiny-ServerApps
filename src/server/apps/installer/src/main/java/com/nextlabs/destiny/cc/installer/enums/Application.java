package com.nextlabs.destiny.cc.installer.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Applications available in Control Center platform.
 *
 * @author Sachindra Dasun
 */
public enum Application {

    APPLICATION("application", null),
    ADMINISTRATOR("administrator", "mgmt_context_param.properties"),
    CAS("cas", "cas.properties"),
    CONFIG_SERVICE("config-service", null),
    CONSOLE("console", "cc-console-app.properties"),
    DMS("dms", "configuration.xml"),
    REPORTER("reporter", "reporter_context_param.properties");

    private static final String APPLICATION_NAME_ADMINISTRATOR = "administrator";
    private static final String APPLICATION_NAME_APPLICATION = "application";
    private static final String APPLICATION_NAME_CAS = "cas";
    private static final String APPLICATION_NAME_CONSOLE = "console";
    private static final String APPLICATION_NAME_DMS = "dms";
    private static final String APPLICATION_NAME_REPORTER = "reporter";
    private final String applicationName;
    private final String configFileName;
    private final Map<String, String> keyMappings = new HashMap<>();
    private final Map<String, UnaryOperator<String>> valueModifiers = new HashMap<>();
    private final Map<String, UnaryOperator<String>> existingValueModifiers = new HashMap<>();

    Application(String applicationName, String configFileName) {
        this.applicationName = applicationName;
        this.configFileName = configFileName;
        initKeyMappings();
        initValueModifiers();
        initExistingValueModifiers();
    }

    private void initExistingValueModifiers() {
        if (APPLICATION_NAME_APPLICATION.equals(applicationName)) {
            existingValueModifiers.put("server.name",
                    value -> value == null ? null : value.replace(":443", ""));
        }
    }

    private void initKeyMappings() {
        if (APPLICATION_NAME_APPLICATION.equals(applicationName)) {
            initKeyMappingsForApplication();
        } else if (APPLICATION_NAME_ADMINISTRATOR.equals(applicationName)) {
            initKeyMappingsForAdministrator();
        } else if (APPLICATION_NAME_CAS.equals(applicationName)) {
            initKeyMappingsForCas();
        } else if (APPLICATION_NAME_CONSOLE.equals(applicationName)) {
            initKeyMappingsForConsole();
        } else if (APPLICATION_NAME_DMS.equals(applicationName)) {
            initKeyMappingsForDms();
        } else if (APPLICATION_NAME_REPORTER.equals(applicationName)) {
            initKeyMappingsForReporter();
        }
    }

    private void initValueModifiers() {
        if (APPLICATION_NAME_CONSOLE.equals(applicationName)) {
            valueModifiers.put("app.service.security", value -> value.replace("j_spring_cas_security_check", "login/cas"));
        } else if (APPLICATION_NAME_DMS.equals(applicationName)) {
            valueModifiers.put("dac.sync.delete.after.sync", value -> "false".equalsIgnoreCase(value) ? "false" : "true");
            valueModifiers.put("reporter.show.sharepoint", value -> "0".equals(value) ? "false" : "true");
        }
    }

    private void initKeyMappingsForApplication() {
        keyMappings.put("spring.cloud.config.username", "application.config.client.username");
        keyMappings.put("spring.cloud.config.password", "application.config.client.password");
        keyMappings.put("spring.cloud.config.uri", null);
        keyMappings.put("spring.cloud.config.fail-fast", null);
    }

    private void initKeyMappingsForAdministrator() {
        keyMappings.put("ComponentName", "administrator.component.name");
        keyMappings.put("DMSLocation", "administrator.dms.location");
        keyMappings.put("InstallHome", "administrator.install.home");
        keyMappings.put("Location", "administrator.location");
    }

    private void initKeyMappingsForCas() {
        keyMappings.put("database.pool.minSize", "cas.db.comboPooledDataSource.minPoolSize");
        keyMappings.put("database.pool.maxSize", "cas.db.comboPooledDataSource.maxPoolSize");
        keyMappings.put("database.pool.maxIdleTime", "cas.db.comboPooledDataSource.maxIdleTime");
        keyMappings.put("database.pool.acquireIncrement", "cas.db.comboPooledDataSource.acquireIncrement");
        keyMappings.put("database.pool.idleConnectionTestPeriod", "cas.db.comboPooledDataSource.idleConnectionTestPeriod");
        keyMappings.put("database.pool.acquireRetryAttempts", "cas.db.comboPooledDataSource.acquireRetryAttempts");
        keyMappings.put("database.pool.acquireRetryDelay", "cas.db.comboPooledDataSource.acquireRetryDelay");
        keyMappings.put("failed.login.attempts", "cas.failed.login.attempts");
        keyMappings.put("ldaps.keyStore.file", "cas.ldaps.keyStoreFile");
        keyMappings.put("ldaps.trustStore.file", "cas.ldaps.trustStoreFile");
    }

    private void initKeyMappingsForConsole() {
        keyMappings.put("app.service.home", null);
        keyMappings.put("app.service.security", null);
        keyMappings.put("cas.service.login", null);
        keyMappings.put("cas.service.logout", null);
        keyMappings.put("cas.service.url", null);
        keyMappings.put("help.content.dir.path", null);
        keyMappings.put("application.version", null);
        keyMappings.put("db.driver", null);
        keyMappings.put("db.hibernate.ddl.auto", null);
        keyMappings.put("db.hibernate.dialect", null);
        keyMappings.put("db.max.poolsize", "console.db.comboPooledDataSource.maxPoolSize");
        keyMappings.put("db.password", null);
        keyMappings.put("db.url", null);
        keyMappings.put("db.username", null);
        keyMappings.put("data.transportation.keystore.file", "console.data.transportation.keyStoreFile");
        keyMappings.put("data.transportation.allow.plain.text.export", "console.data.transportation.allowPlainTextExport");
        keyMappings.put("data.transportation.allow.plain.text.import", "console.data.transportation.allowPlainTextImport");
        keyMappings.put("data.transportation.shared.key", "console.data.transportation.sharedKey");
    }

    private void initKeyMappingsForDms() {
        keyMappings.put("/DestinyConfiguration/MessageHandlers/MessageHandler/Properties/Property[1]/Value/text()", "application.spring.mail.host");
        keyMappings.put("/DestinyConfiguration/MessageHandlers/MessageHandler/Properties/Property[2]/Value/text()", "application.spring.mail.port");
        keyMappings.put("/DestinyConfiguration/MessageHandlers/MessageHandler/Properties/Property[3]/Value/text()", "application.spring.mail.username");
        keyMappings.put("/DestinyConfiguration/MessageHandlers/MessageHandler/Properties/Property[4]/Value/text()", "application.spring.mail.password");
        keyMappings.put("/DestinyConfiguration/MessageHandlers/MessageHandler/Properties/Property[5]/Value/text()", "application.spring.mail.properties.mail.smtp.from");
        keyMappings.put("/DestinyConfiguration/MessageHandlers/MessageHandler/Properties/Property[6]/Value/text()", "application.cc.mail.default.to");

        keyMappings.put("/DestinyConfiguration/DABS/FileSystemLogConfiguration/QueueManagerUploadSize/text()", "dms.dabs.log.upload.size");
        keyMappings.put("/DestinyConfiguration/DABS/FileSystemLogConfiguration/ThreadPoolMaximumSize/text()", "dms.dabs.log.thread.count");
        keyMappings.put("/DestinyConfiguration/DAC/ActivityJournalSettingConfiguration/ArchiveOperation/AutoArchive/text()", "dms.dac.archive.auto.archive");
        keyMappings.put("/DestinyConfiguration/DAC/ActivityJournalSettingConfiguration/ArchiveOperation/DaysOfDataToKeep/text()", "dms.dac.archive.days.to.keep");
        keyMappings.put("/DestinyConfiguration/DAC/ActivityJournalSettingConfiguration/ArchiveOperation/TimeOfDay/text()", "dms.dac.archive.time.of.day");
        keyMappings.put("/DestinyConfiguration/DAC/ActivityJournalSettingConfiguration/ArchiveOperation/TimeoutInMinutes/text()", "dms.dac.archive.timeout.minutes");
        keyMappings.put("/DestinyConfiguration/DAC/ActivityJournalSettingConfiguration/IndexesRebuildOperation/AutoRebuildIndexes/text()", "dms.dac.index.rebuild.auto.rebuild");
        keyMappings.put("/DestinyConfiguration/DAC/ActivityJournalSettingConfiguration/IndexesRebuildOperation/TimeOfDay/text()", "dms.dac.index.rebuild.time.of.day");
        keyMappings.put("/DestinyConfiguration/DAC/ActivityJournalSettingConfiguration/IndexesRebuildOperation/TimeoutInMinutes/text()", "dms.dac.index.rebuild.timeout.minutes");
        keyMappings.put("/DestinyConfiguration/DAC/ActivityJournalSettingConfiguration/SyncOperation/DeleteAfterSync/text()", "dms.dac.sync.delete.after.sync");
        keyMappings.put("/DestinyConfiguration/DAC/ActivityJournalSettingConfiguration/SyncOperation/TimeInterval/text()", "dms.dac.sync.time.interval");
        keyMappings.put("/DestinyConfiguration/DAC/ActivityJournalSettingConfiguration/SyncOperation/TimeOfDay/text()", "dms.dac.sync.time.of.day");
        keyMappings.put("/DestinyConfiguration/DAC/ActivityJournalSettingConfiguration/SyncOperation/TimeoutInMinutes/text()", "dms.dac.sync.timeout.minutes");
        keyMappings.put("/DestinyConfiguration/DAC/Properties/Property[1]/Value/text()", "dms.dac.number.of.extended.attributes");
        keyMappings.put("/DestinyConfiguration/GenericComponents/GenericComponent[1]/Properties/Property[2]/Value/text()", "dms.dms.db.dialect");
        keyMappings.put("/DestinyConfiguration/Reporter/Properties/Property[2]/Value/text()", "dms.reporter.monitor.execution.interval");
        keyMappings.put("/DestinyConfiguration/Repositories/ConnectionPools/ConnectionPool[1]/MaxPoolSize/text()", "dms.repositories.pf.connection.maxpoolsize");
        keyMappings.put("/DestinyConfiguration/Repositories/ConnectionPools/ConnectionPool[2]/MaxPoolSize/text()", "dms.repositories.dictionary.connection.maxpoolsize");
        keyMappings.put("/DestinyConfiguration/Repositories/ConnectionPools/ConnectionPool[3]/MaxPoolSize/text()", "dms.repositories.activity.connection.maxpoolsize");
        keyMappings.put("/DestinyConfiguration/Repositories/ConnectionPools/ConnectionPool[4]/MaxPoolSize/text()", "dms.repositories.management.connection.maxpoolsize");
        keyMappings.put("/DestinyConfiguration/Repositories/Repository[1]/Properties/Property[1]/Value/text()", "dms.repositories.management.hibernate.dialect");
        keyMappings.put("/DestinyConfiguration/Repositories/Repository[2]/Properties/Property[1]/Value/text()", "dms.repositories.activity.hibernate.dialect");
        keyMappings.put("/DestinyConfiguration/Repositories/Repository[3]/Properties/Property[1]/Value/text()", "dms.repositories.pf.hibernate.dialect");
        keyMappings.put("/DestinyConfiguration/Repositories/Repository[4]/Properties/Property[1]/Value/text()", "dms.repositories.dictionary.hibernate.dialect");

        keyMappings.put("/DestinyConfiguration/Reporter/ShowSharePointReports/text()", "reporter.show.sharepoint");
        keyMappings.put("/DestinyConfiguration/Reporter/Properties/Property[1]/Value/text()", "reporter.use.past.data.for.monitoring");
    }

    private void initKeyMappingsForReporter() {
        keyMappings.put("reporter.ComponentName", "component.name");
        keyMappings.put("reporter.DACLocation", "dac.location");
        keyMappings.put("reporter.DMSLocation", "dms.location");
        keyMappings.put("reporter.InstallHome", "install.home");
        keyMappings.put("reporter.Location", "location");
    }

    public Map<String, String> getKeyMappings() {
        return keyMappings;
    }

    public Map<String, UnaryOperator<String>> getValueModifiers() {
        return valueModifiers;
    }

    public Map<String, UnaryOperator<String>> getExistingValueModifiers() {
        return existingValueModifiers;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getConfigFileName() {
        return configFileName;
    }
}
