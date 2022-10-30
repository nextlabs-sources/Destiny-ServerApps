package com.nextlabs.destiny.cc.installer.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * List of tasked  used to notify the progress.
 *
 * @author Sachindra Dasun
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Task {

    VALIDATE("Validating prerequisites", "Validating prerequisites", 1),
    COPY_INSTALLATION("Copying installation", "Copying installation", 2),
    CREATE_DCC_CERTIFICATE("Creating dcc certificate", "Creating certificates", 12),
    CREATE_LEAGACY_DCC_CERTIFICATE("Creating legacy dcc certificate", "Creating certificates", 14),
    CREATE_AGENT_CERTIFICATE("Creating agent certificate", "Creating certificates", 16),
    CREATE_LEGACY_AGENT_CERTIFICATE("Creating legacy agent certificate", "Creating certificates", 18),
    CREATE_APPLICATION_CERTIFICATE("Creating application certificate", "Creating certificates", 20),
    CREATE_WEB_CERTIFICATE("Creating web certificate", "Creating certificates", 22),
    CREATE_DIGITAL_SIGNATURE_CERTIFICATE("Creating digital signature certificate", "Creating certificates", 24),
    CREATE_TRUST_STORES("Creating trust stores", "Creating certificates", 26),
    CONFIGURE_DATABASE_INITIALIZATION("Configuring database initialization", "Initializing database", 28),
    INITIALIZE_DICTIONARY_REPOSITORY("Initializing dictionary repository", "Initializing database", 35),
    INITIALIZE_MANAGEMENT_REPOSITORY("Initializing management repository", "Initializing database", 40),
    INITIALIZE_PF_REPOSITORY("Initializing pf repository", "Initializing database", 45),
    INITIALIZE_ACTIVITY_REPOSITORY("Initializing activity repository", "Initializing database", 55),
    INITIALIZE_DATA("Initializing data", "Initializing database", 60),
    CLEAN_DATABASE_INITIALIZATION_FILES("Cleaning database initialization files", "Initializing database", 65),
    CONFIGURE_CONTROL_CENTER("Configuring Control Center", "Configuring Control Center", 70),
    MIGRATE_DATA("Migrating data", "Configuring Control Center", 75),
    CREATE_CONTROL_CENTER_ELASTICSEARCH_SERVICE("Creating Control Center Elasticsearch service", "Configuring Services", 82),
    CREATE_CONTROL_CENTER_SERVICE("Creating Control Center service", "Configuring Services", 90),
    CREATE_CONTROL_CENTER_POLICY_VALIDATOR_SERVICE("Creating Control Center Policy Validator service", "Configuring Services", 92),
    CONFIGURE_CONTROL_CENTER_SERVER("Configuring Control Center server", "Configuring Services", 95),

    REMOVE_SERVICES("Removing Control Center services", "Configuring Services", 1),
    REMOVE_CONFIGURATIONS("Removing Control Center configurations", "Configuring Services", 30),
    REVOKE_GRANTED_PERMISSIONS("Revoking granted permissions", "Configuring Services", 60),
    COMPLETE("Complete", "Complete", 100);


    private final String group;
    private final int progress;
    private final String taskDescription;

    Task(String taskDescription, String group, int progress) {
        this.taskDescription = taskDescription;
        this.progress = progress;
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    public int getProgress() {
        return progress;
    }

    public String getTaskDescription() {
        return taskDescription;
    }
}
