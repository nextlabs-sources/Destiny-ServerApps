package com.nextlabs.destiny.cc.installer.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.cert.CertificateFactory;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nextlabs.destiny.cc.installer.annotations.ValidCertificate;
import com.nextlabs.destiny.cc.installer.config.properties.CertificateProperties;
import com.nextlabs.destiny.cc.installer.exceptions.InstallerException;

/**
 * Validator for SSL certificates.
 *
 * @author Sachindra Dasun
 */
public class CertificateValidator implements ConstraintValidator<ValidCertificate, CertificateProperties> {

    private static final Logger logger = LoggerFactory.getLogger(CertificateValidator.class);

    @Override
    public boolean isValid(CertificateProperties certificate, ConstraintValidatorContext context) {
        try (InputStream certificateInputStream = getCertificateInputStream(certificate, context)) {
            CertificateFactory.getInstance("X.509").generateCertificate(certificateInputStream);
        } catch (Exception e) {
            logger.error("Error in validating SSL certificate", e);
            return false;
        }
        return true;
    }

    private InputStream getCertificateInputStream(CertificateProperties certificate, ConstraintValidatorContext context)
            throws FileNotFoundException {
        InputStream certificateInputStream = null;
        if (StringUtils.isNotEmpty(certificate.getContent())) {
            if (StringUtils.isEmpty(certificate.getAlias())) {
                if (context != null) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("{ssl.notEmptyAlias}")
                            .addConstraintViolation();
                }
                throw new InstallerException("Certificate Alas is required");
            }
            certificateInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(certificate.getContent().replaceAll("[\\r\\n]", "").trim()));
        } else if (StringUtils.isNotEmpty(certificate.getPath())) {
            File certificateFile = Paths.get(certificate.getPath()).toFile();
            if (certificateFile.exists()) {
                certificateInputStream = new FileInputStream(certificateFile);
            } else {
                if (context != null) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("{ssl.existingCertificate}")
                            .addConstraintViolation();
                }
                throw new InstallerException("Valid certificate path is required");
            }
        }
        return certificateInputStream;
    }

}
