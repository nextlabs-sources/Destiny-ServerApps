package com.nextlabs.destiny.console.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import com.nextlabs.destiny.console.annotations.ValidEnrollment;
import com.nextlabs.destiny.console.dto.enrollment.EnrollmentDTO;
import com.nextlabs.destiny.console.dto.enrollment.EnrollmentPropertyDTO;
import com.nextlabs.destiny.console.enums.EnrollmentType;
import com.nextlabs.destiny.console.model.dictionary.Enrollment;
import com.nextlabs.destiny.console.repositories.dictionary.EnrollmentRepository;
import com.nextlabs.destiny.console.services.tool.impl.EnrollmentValidationServiceFactory;
import com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants;

/**
 * Validator for enrollment.
 *
 * @author Sachindra Dasun
 */
@Component
public class EnrollmentValidator implements ConstraintValidator<ValidEnrollment, EnrollmentDTO> {

    private final EnrollmentValidationServiceFactory enrollmentFactory;

    private EnrollmentRepository enrollmentRepository;

    private TextEncryptor textEncryptor;

    @Autowired
    public EnrollmentValidator(EnrollmentValidationServiceFactory enrollmentFactory) {
        this.enrollmentFactory = enrollmentFactory;
    }

    @Override
    public boolean isValid(EnrollmentDTO enrollmentDTO, ConstraintValidatorContext context) {
        if (enrollmentDTO.getType() == EnrollmentType.ACTIVE_DIRECTORY || enrollmentDTO.getType() == EnrollmentType.SHAREPOINT
                || enrollmentDTO.getType() == EnrollmentType.AZURE_ACTIVE_DIRECTORY) {
            String passwordFieldKey = getPasswordFiledKey(enrollmentDTO);
            if (enrollmentDTO.getId() != null
                    && EnrollmentPropertyDTO.ENROLLMENT_DISPLAY_VALUE.equals(enrollmentDTO.getEnrollmentPropertyValue(passwordFieldKey))) {
                Enrollment existingEnrollment = enrollmentRepository
                        .findById(enrollmentDTO.getId())
                        .orElseThrow(NoSuchElementException::new);
                String savedPassword = existingEnrollment.getEnrollmentPropertyValue(passwordFieldKey);
                enrollmentDTO.getEnrollmentProperty(passwordFieldKey)
                        .ifPresent(passwordPropertyDTO -> passwordPropertyDTO.setValue(textEncryptor.decrypt(savedPassword)));
            }
        }
        return enrollmentFactory
                .getValidationService(enrollmentDTO.getType())
                .map(validationService -> validationService.isValid(enrollmentDTO, context))
                .orElse(true);
    }

    private String getPasswordFiledKey(EnrollmentDTO enrollmentDTO) {
        if (enrollmentDTO.getType() == EnrollmentType.ACTIVE_DIRECTORY || enrollmentDTO.getType() == EnrollmentType.SHAREPOINT) {
            return EnrollmentConstants.PASSWORD_PROPERTY;
        } else if (enrollmentDTO.getType() == EnrollmentType.AZURE_ACTIVE_DIRECTORY) {
            return EnrollmentConstants.APPLICATION_KEY;
        } else {
            throw new RuntimeException(String.format("Validation not implemented for enrollment type %s", enrollmentDTO.getType()));
        }
    }

    @Autowired
    public void setEnrollmentRepository(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Autowired
    public void setTextEncryptor(TextEncryptor textEncryptor) {
        this.textEncryptor = textEncryptor;
    }

}
