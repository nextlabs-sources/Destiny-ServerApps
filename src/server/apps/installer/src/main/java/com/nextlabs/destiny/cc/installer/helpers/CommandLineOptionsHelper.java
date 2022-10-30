package com.nextlabs.destiny.cc.installer.helpers;

import java.io.IOException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.nextlabs.destiny.cc.installer.enums.CommandOption;

/**
 * This class handles configuring CLI application.
 *
 * @author Sachindra Dasun
 */
public class CommandLineOptionsHelper {

    private static final OptionParser optionParser = new OptionParser();
    private static OptionSet optionSet;

    private CommandLineOptionsHelper() {
    }

    public static void printHelp() throws IOException {
        optionParser.printHelpOn(System.out);
    }

    public static void parse(String[] args) {
        createOptionParser();
        optionSet = optionParser.parse(args == null ? new String[]{} : args);
    }

    private static void createOptionParser() {
        optionParser.accepts(CommandOption.HELP.getOption(), CommandOption.HELP.getDescription())
                .forHelp();
        optionParser.accepts(CommandOption.REMOVE_SERVICES.getOption(), CommandOption.REMOVE_SERVICES.getDescription());
        optionParser.accepts(CommandOption.STOP_SERVICES.getOption(), CommandOption.STOP_SERVICES.getDescription());
        optionParser.accepts(CommandOption.RUN.getOption(), CommandOption.RUN.getDescription());
        optionParser.accepts(CommandOption.START.getOption(), CommandOption.START.getDescription());
        optionParser.accepts(CommandOption.NO_ES.getOption(), CommandOption.NO_ES.getDescription());
        optionParser.accepts(CommandOption.UI.getOption(), CommandOption.UI.getDescription());
        optionParser.accepts(CommandOption.CONFIGURE_INSTALLER.getOption(), CommandOption.CONFIGURE_INSTALLER.getDescription());
        optionParser.accepts(CommandOption.WITH_VERSION_CHECK.getOption(), CommandOption.WITH_VERSION_CHECK.getDescription());
        optionParser.accepts(CommandOption.UNINSTALL.getOption(), CommandOption.UNINSTALL.getDescription());
    }

    public static OptionSet getOptionSet() {
        return optionSet;
    }

    public static boolean isEmpty() {
        return optionSet.asMap().isEmpty();
    }

    public static boolean isCommand() {
        return has(CommandOption.HELP) ||
                has(CommandOption.REMOVE_SERVICES) ||
                has(CommandOption.STOP_SERVICES) ||
                has(CommandOption.CONFIGURE_INSTALLER);
    }

    public static boolean has(CommandOption option) {
        return optionSet.has(option.getOption());
    }

}
