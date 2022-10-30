package com.nextlabs.destiny.console.validators;

import com.nextlabs.destiny.console.dto.enrollment.EnrollmentDTO;
import com.nextlabs.destiny.console.dto.enrollment.EnrollmentPropertyDTO;
import com.nextlabs.destiny.console.services.MessageBundleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EnrollmentValidationHelper {

    MessageBundleService msgBundle;

    public boolean validateRequiredFields(EnrollmentDTO enrollmentDTO, Set<String> mandatoryProperties, ConstraintValidatorContext context, String errorCodeKey) {
        Map<String, String> propertyValues = enrollmentDTO.getValues().stream()
                .collect(Collectors.toMap(EnrollmentPropertyDTO::getName, EnrollmentPropertyDTO::getValue));
        List<String> invalidProperties = mandatoryProperties.stream()
                .filter(property -> !propertyValues.containsKey(property) || StringUtils.isEmpty(propertyValues.get(property)))
                .collect(Collectors.toList());
        if (!invalidProperties.isEmpty()) {
            context.disableDefaultConstraintViolation();
            invalidProperties.forEach(s ->
                    context.buildConstraintViolationWithTemplate(msgBundle.getText(errorCodeKey, s))
                            .addConstraintViolation());
            return false;
        }
        return true;
    }

    @Autowired
    public void setMsgBundle(MessageBundleService msgBundle) {
        this.msgBundle = msgBundle;
    }
}
