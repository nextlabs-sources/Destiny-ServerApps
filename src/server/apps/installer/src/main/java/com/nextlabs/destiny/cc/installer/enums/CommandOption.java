package com.nextlabs.destiny.cc.installer.enums;

/**
 * Options supported by starter command line application.
 *
 * @author Sachindra Dasun
 */
public enum CommandOption {

    HELP("help", "Print help"),
    REMOVE_SERVICES("remove-services", "Remove Control Center services"),
    STOP_SERVICES("stop", "Stop Control Center Services"),
    RUN("run", "Run Control Center"),
    START("start", "Start Control Center services"),
    NO_ES("no-es", "Do not start Control Center Elasticsearch"),
    CONFIGURE_INSTALLER("configure-installer", "Configure control center installer"),
    UI("ui", "Run web based installer"),
    WITH_VERSION_CHECK("with-version-check", "Check existing Control Center version before running the installer"),
    UNINSTALL("uninstall", "Uninstall Control Center");

    private final String description;
    private final String option;

    CommandOption(String option, String description) {
        this.option = option;
        this.description = description;
    }

    public String getOption() {
        return option;
    }

    public String getDescription() {
        return description;
    }

}
