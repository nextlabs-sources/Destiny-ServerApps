/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 24, 2016
 *
 */
package com.nextlabs.destiny.console.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Utility class to read the server license details
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class ServerLicenseUtil {

    private static final Logger log = LoggerFactory
            .getLogger(ServerLicenseUtil.class);
    private static final String LICENSE_JARCHECKER_CLASS_NAME = "com.wald.license.checker.JarChecker";
    private static final String LICENSE_CLASSLOADER_CLASS_NAME = "com.wald.license.checker.LicenseClassLoader";
    private static final String LICENSE_JARCHECKER_GETPROPERTIES_METHOD_NAME = "getProperties";
    private static final String LICENSE_JARCHECKER_CHECK_METHOD_NAME = "check";
    private static final String LICENSE_JARCHECKER_SETCLASSLOADER_METHOD_NAME = "setClassLoader";
    private static final String LICENSE_JARCHECKER_SETJARFILENAME_METHOD_NAME = "setJarFileName";
    private static final String FILE_PROTOCOL = "file:///";

    public static Properties readLicense(String licenseFolderLocation) {
        URLClassLoader urlClassLoader = null;
        try {
            licenseFolderLocation = licenseFolderLocation + File.separator;
            String licenseJarFileLocation = licenseFolderLocation
                    + "license.jar";
            URL jarLocation = new URL(FILE_PROTOCOL + licenseJarFileLocation);
            URL dataFileParentFolderLocation = new URL(
                    FILE_PROTOCOL + licenseFolderLocation);
            URL[] classLoaderURLs = { dataFileParentFolderLocation,
                    jarLocation };
            urlClassLoader = new URLClassLoader(classLoaderURLs, ServerLicenseUtil.class.getClassLoader());
            ClassLoader licenseLocationClassLoader = urlClassLoader;

            Class<?> licenseClassLoaderClass = licenseLocationClassLoader
                    .loadClass(LICENSE_CLASSLOADER_CLASS_NAME);
            Constructor<?> parentClassLoaderConstructor = licenseClassLoaderClass
                    .getConstructor(ClassLoader.class);
            Object licenseClassLoader = parentClassLoaderConstructor
                    .newInstance(licenseLocationClassLoader);

            Class<?> jarCheckerClass = licenseLocationClassLoader
                    .loadClass(LICENSE_JARCHECKER_CLASS_NAME);
            Object jarCheckerInstance = jarCheckerClass.newInstance();
            Method setJarFileMethod = jarCheckerClass.getMethod(
                    LICENSE_JARCHECKER_SETJARFILENAME_METHOD_NAME,
                    java.lang.String.class);
            setJarFileMethod.invoke(jarCheckerInstance, licenseJarFileLocation);

            Class<?> setClassLoaderMethodParams = licenseClassLoader.getClass();
            Method setClassLoaderMethod = jarCheckerClass.getMethod(
                    LICENSE_JARCHECKER_SETCLASSLOADER_METHOD_NAME,
                    setClassLoaderMethodParams);
            setClassLoaderMethod.invoke(jarCheckerInstance, licenseClassLoader);

            Method checkMethod = jarCheckerClass
                    .getMethod(LICENSE_JARCHECKER_CHECK_METHOD_NAME);
            checkMethod.invoke(jarCheckerInstance);

            Method getPropertiesMethod = jarCheckerClass
                    .getMethod(LICENSE_JARCHECKER_GETPROPERTIES_METHOD_NAME);
            return (Properties) getPropertiesMethod.invoke(jarCheckerInstance);
        } catch (Exception e) {
            log.error("Error encountered in reading the license information,",
                    e);
        } finally {
            if(urlClassLoader != null) {
                try {
                    urlClassLoader.close();
                } catch(IOException e) {
                    // Silent
                } finally {
                    urlClassLoader = null;
                }
            }
        }
        return null;
    }
}
