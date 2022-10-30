package com.nextlabs.destiny.cc.installer.validators;

import static java.lang.System.currentTimeMillis;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nextlabs.destiny.cc.installer.annotations.ValidLicense;
import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;
import com.nextlabs.destiny.cc.installer.enums.RunningMode;
import com.nextlabs.destiny.cc.installer.exceptions.InstallerException;
import com.nextlabs.destiny.cc.installer.helpers.ParameterHelper;

/**
 * Validator for Control Center license.
 *
 * @author Sachindra Dasun
 */
public class LicenseValidator implements ConstraintValidator<ValidLicense, CcProperties> {

    private static final Logger logger = LoggerFactory.getLogger(LicenseValidator.class);

    @Override
    public boolean isValid(CcProperties ccProperties, ConstraintValidatorContext context) {
        return isValid(ccProperties.getLicenseFilePath(), ccProperties.getLicense());
    }

    public boolean isValid(String licenseFileLocation, String license) {
        if (!Boolean.parseBoolean(System.getProperty("nextlabs.cc.is-management-server", "false"))) {
            return true;
        }
        long licenseExpireTime = 0;
        Path licenseFilePath = Paths.get(ParameterHelper.INIT_CC_HOME, "server", "license", "license.dat");
        copyLicenseValidator();
        boolean licenseFileCopied = copyLicenseFile(licenseFileLocation, license, licenseFilePath);
        if (licenseFilePath.toFile().exists()) {
            try {
                PrintStream systemError = System.err;
                System.setErr(new PrintStream(new OutputStream() {
                    @Override
                    public void write(int b) {
                        // Error output is not required for license validation
                    }
                }));
                Properties licenseProperties = getLicenseProperties(licenseFilePath.getParent());
                System.setErr(systemError);
                if (licenseProperties != null) {
                    for (Map.Entry<Object, Object> entry : licenseProperties.entrySet()) {
                        licenseProperties.setProperty(entry.getKey().toString().toLowerCase(), entry.getValue().toString());
                    }
                    String expirationDate = licenseProperties.getProperty("expiration");
                    logger.info("License expires on [{}], -1 means never expire", expirationDate);
                    licenseExpireTime = expirationDate.equals("-1") ? Long.MAX_VALUE
                            : new SimpleDateFormat("MM/dd/yyyy").parse(expirationDate).getTime();
                }
            } catch (Exception e) {
                logger.error("Error in validating license", e);
            }
        }
        if (licenseExpireTime >= currentTimeMillis()) {
            return true;
        }
        if (licenseFileCopied) {
            try {
                Files.deleteIfExists(licenseFilePath);
            } catch (IOException e) {
                logger.error("Error in deleting invalid license file", e);
            }
        }
        return false;
    }

    private void copyLicenseValidator() {
        Path licenseValidatorPath = Paths.get(ParameterHelper.INIT_CC_HOME, "server", "license-validator", "license.jar");
        Path licenseValidatorInLicenseFolderPath = Paths.get(ParameterHelper.INIT_CC_HOME, "server", "license", "license.jar");
        if (!licenseValidatorInLicenseFolderPath.toFile().exists()) {
            try {
                Files.copy(licenseValidatorPath, licenseValidatorInLicenseFolderPath);
            } catch (IOException e) {
                logger.error("Error in copying license validator", e);
                throw new InstallerException("Error in copying license");
            }
        }
    }

    private boolean copyLicenseFile(String path, String license, Path licenseFilePath) {
        try {
            if (licenseFilePath.toFile().exists()) {
                return false;
            } else if (StringUtils.isNotEmpty(license)) {
                Files.write(licenseFilePath, Base64.getDecoder()
                        .decode(license.replaceAll("\\r|\\n", "").trim()));
                return true;
            } else if (StringUtils.isNotEmpty(path)) {
                if (RunningMode.UPGRADE.name().equals(System.getProperty("nextlabs.cc.running-mode"))
                        && StringUtils.isEmpty(path)) {
                    path = Paths.get(System.getProperty("nextlabs.cc.previous-home"), "server", "license", "license.dat")
                            .toString();
                }
                File providedLicenseFile = Paths.get(path).toFile();
                if (!providedLicenseFile.equals(licenseFilePath.toFile())) {
                    FileUtils.copyFile(providedLicenseFile, licenseFilePath.toFile());
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("Error while copying license file", e);
        }
        return false;
    }

    private static Properties getLicenseProperties(Path licenseFileFolder) throws IOException, ClassNotFoundException,
            IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Path licenseJarPath = licenseFileFolder.resolve("license.jar");
        try (URLClassLoader licenseLocationClassLoader = new URLClassLoader(new URL[]{licenseFileFolder.toUri().toURL(),
                licenseJarPath.toUri().toURL()},
                LicenseValidator.class.getClassLoader())) {
            Class<?> licenseClassLoaderClass = licenseLocationClassLoader
                    .loadClass("com.wald.license.checker.LicenseClassLoader");
            Constructor<?> parentClassLoaderConstructor = licenseClassLoaderClass.getConstructor(ClassLoader.class);
            Object licenseClassLoader = parentClassLoaderConstructor.newInstance(licenseLocationClassLoader);

            Class<?> jarCheckerClass = licenseLocationClassLoader.loadClass("com.wald.license.checker.JarChecker");
            Object jarCheckerInstance = jarCheckerClass.newInstance();
            Method setJarFileMethod = jarCheckerClass.getMethod("setJarFileName",
                    String.class);
            setJarFileMethod.invoke(jarCheckerInstance, licenseJarPath.toString());

            Class<?> setClassLoaderMethodParams = licenseClassLoader.getClass();
            Method setClassLoaderMethod = jarCheckerClass.getMethod("setClassLoader",
                    setClassLoaderMethodParams);
            setClassLoaderMethod.invoke(jarCheckerInstance, licenseClassLoader);

            Method checkMethod = jarCheckerClass.getMethod("check");
            checkMethod.invoke(jarCheckerInstance);
            Method getPropertiesMethod = jarCheckerClass.getMethod("getProperties");
            return (Properties) getPropertiesMethod.invoke(jarCheckerInstance);
        }
    }

}
