package com.nextlabs.authentication.util;

import static java.lang.System.currentTimeMillis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map.Entry;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.nextlabs.authentication.models.SysInfo;

/**
 * Utility class to read the server license details
 *
 * @author Amila Silva
 * @since 8.0
 */
public final class LicenseCheckerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseCheckerUtil.class);

    private static final String EXTENDED_LICENSE_FILE_NAME = "sys_info.dat";
    private static final String LICENSE_JARCHECKER_CLASS_NAME = "com.wald.license.checker.JarChecker";
    private static final String LICENSE_CLASSLOADER_CLASS_NAME = "com.wald.license.checker.LicenseClassLoader";
    private static final String LICENSE_JARCHECKER_GETPROPERTIES_METHOD_NAME = "getProperties";
    private static final String LICENSE_JARCHECKER_CHECK_METHOD_NAME = "check";
    private static final String LICENSE_JARCHECKER_SETCLASSLOADER_METHOD_NAME = "setClassLoader";
    private static final String LICENSE_JARCHECKER_SETJARFILENAME_METHOD_NAME = "setJarFileName";
    private static final String FILE_PROTOCOL = "file:///";
    private static final long LICENSE_NEVER_EXPIRE = Long.MAX_VALUE;
    private static long licenseExpireTime = 0;
    private static Properties licenseProperties = null;
    private static String licenseFolderLocation = null;

    private LicenseCheckerUtil() {
    }

    public static void init(String licenseFolderPath) {
        licenseFolderLocation = licenseFolderPath;
    }

    public static boolean isValidLicense() {
        try {
            if (licenseProperties == null) {
                loadLicenseProperties();
            }
            if (licenseProperties != null) {
                return licenseExpireTime >= currentTimeMillis();
            }
        } catch (Exception e) {
            LOGGER.warn("Exception while validating license", e);
        }
        return false;
    }

    private static synchronized void loadLicenseProperties() throws NoSuchMethodException, IOException,
            InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException,
            ParseException {
        if (licenseProperties == null) {
            licenseProperties = loadLicenseFileDetails(licenseFolderLocation);
            if (null != licenseProperties) {
                for (Entry<Object, Object> entry : licenseProperties.entrySet()) {
                    licenseProperties.setProperty(((String) entry.getKey()).toLowerCase(), (String) entry.getValue());
                }
                String expirationDate = licenseProperties.getProperty("expiration");
                LOGGER.info("license expires on [{}], -1 means never expire", expirationDate);
                if (expirationDate.equals("-1")) {
                    licenseExpireTime = LICENSE_NEVER_EXPIRE;
                } else {
                    // 12/01/2016 convert to timestamp
                    licenseExpireTime = new SimpleDateFormat("MM/dd/yyyy").parse(expirationDate).getTime();
                }
            }
        }
    }

    private static Properties loadLicenseFileDetails(String licenseFolderLocation) throws IOException,
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException,
            IllegalAccessException {
        File extendedLicenseFile = new File(licenseFolderLocation, EXTENDED_LICENSE_FILE_NAME);
        if (extendedLicenseFile.exists() && extendedLicenseFile.isFile()) {
            LOGGER.info("{} found, load license information from this file.", EXTENDED_LICENSE_FILE_NAME);
            return loadExtendedLicense(extendedLicenseFile);
        } else {
            LOGGER.info("{} not found, load license information from license.dat file.", EXTENDED_LICENSE_FILE_NAME);
            return loadedLicense(licenseFolderLocation);
        }
    }

    private static Properties loadedLicense(String licenseFolderLocation) throws MalformedURLException,
            ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        licenseFolderLocation = licenseFolderLocation + File.separator;
        String licenseJarFileLocation = licenseFolderLocation + "license.jar";
        URL jarLocation = new URL(FILE_PROTOCOL + licenseJarFileLocation);
        URL dataFileParentFolderLocation = new URL(FILE_PROTOCOL + licenseFolderLocation);
        URL[] classLoaderURLs = {dataFileParentFolderLocation, jarLocation};
        Properties properties = null;
        try (URLClassLoader licenseLocationClassLoader = new URLClassLoader(classLoaderURLs,
                LicenseCheckerUtil.class.getClassLoader())) {
            Class<?> licenseClassLoaderClass = licenseLocationClassLoader.loadClass(LICENSE_CLASSLOADER_CLASS_NAME);
            Constructor<?> parentClassLoaderConstructor = licenseClassLoaderClass.getConstructor(ClassLoader.class);
            Object licenseClassLoader = parentClassLoaderConstructor.newInstance(licenseLocationClassLoader);

            Class<?> jarCheckerClass = licenseLocationClassLoader.loadClass(LICENSE_JARCHECKER_CLASS_NAME);
            Object jarCheckerInstance = jarCheckerClass.getConstructor().newInstance();
            Method setJarFileMethod = jarCheckerClass.getMethod(LICENSE_JARCHECKER_SETJARFILENAME_METHOD_NAME,
                    java.lang.String.class);
            setJarFileMethod.invoke(jarCheckerInstance, licenseJarFileLocation);

            Class<?> setClassLoaderMethodParams = licenseClassLoader.getClass();
            Method setClassLoaderMethod = jarCheckerClass.getMethod(LICENSE_JARCHECKER_SETCLASSLOADER_METHOD_NAME,
                    setClassLoaderMethodParams);
            setClassLoaderMethod.invoke(jarCheckerInstance, licenseClassLoader);

            Method checkMethod = jarCheckerClass.getMethod(LICENSE_JARCHECKER_CHECK_METHOD_NAME);
            checkMethod.invoke(jarCheckerInstance);

            Method getPropertiesMethod = jarCheckerClass.getMethod(LICENSE_JARCHECKER_GETPROPERTIES_METHOD_NAME);
            properties = (Properties) getPropertiesMethod.invoke(jarCheckerInstance);
        } catch (IOException e) {
            LOGGER.error("Error in class loader", e);
        }
        return properties;
    }

    private static Properties loadExtendedLicense(File extendedLicenseFile) throws IOException {
        String encryptedLicenseInfo = IOUtils
                .toString(new FileInputStream(extendedLicenseFile), Charset.defaultCharset()).trim();
        ReversibleEncryptor encryptor = new ReversibleEncryptor();
        String decryptedLicenseInfo = encryptor.decrypt(encryptedLicenseInfo);
        Properties properties = new Properties();
        ObjectMapper mapper = new ObjectMapper();
        SysInfo sysInfo = mapper.readValue(decryptedLicenseInfo, SysInfo.class);
        properties.setProperty("expiration", sysInfo.getLicenseInfo().getExpiryDate());
        return properties;
    }

}
