package com.nextlabs.destiny.cc.installer.services;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.json.JSONException;
import org.json.simple.parser.ParseException;

import com.nextlabs.destiny.cc.installer.enums.Component;

/**
 * Handle tomcat configuration.
 *
 * @author Sachindra Dasun
 */
public interface InstanceConfigurationService {

    void configure() throws IOException, InvocationTargetException, IllegalAccessException, JSONException;

    void createServerXmlFile(String fileName, Set<Component> components) throws IOException;

    void configureSetEnvFile() throws IOException;

    void grantRestrictedPortAccessPermission(String fileName) throws IOException;

    void changeFolderOwnership(String folder) throws IOException;

    void changeFolderPermission(String folder) throws IOException;

    void runCommand(String command) throws IOException;

}
