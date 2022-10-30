package com.nextlabs.destiny.cc.installer.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nextlabs.destiny.cc.installer.annotations.ValidCaCertsFolder;
import com.nextlabs.destiny.cc.installer.config.properties.CertificateProperties;
import com.nextlabs.destiny.cc.installer.config.properties.SslProperties;
import com.nextlabs.destiny.cc.installer.helpers.ParameterHelper;

/**
 * Validator for SSL certificates in cacerts folder.
 *
 * @author Sachindra Dasun
 */
public class CaCertsFolderValidator implements ConstraintValidator<ValidCaCertsFolder, SslProperties> {

    private static final Logger logger = LoggerFactory.getLogger(CaCertsFolderValidator.class);

    @Override
    public boolean isValid(SslProperties sslProperties, ConstraintValidatorContext context) {
        Path cacertsFolderPath = Paths.get(ParameterHelper.INIT_CC_HOME, "server", "certificates", "cacerts");
        if (cacertsFolderPath.toFile().exists()) {
            try {
                CertificateValidator certificateValidator = new CertificateValidator();
                try (Stream<Path> pathStream = Files.walk(cacertsFolderPath)) {
                    return pathStream.filter(path -> path.toFile().isFile() && !path.toString().endsWith(".jks"))
                            .map(Path::toString)
                            .allMatch(path -> certificateValidator.isValid(new CertificateProperties(path), null));
                }
            } catch (Exception e) {
                logger.error("Error in validating certificates in cacerts folder", e);
                return false;
            }
        }
        return true;
    }

}
