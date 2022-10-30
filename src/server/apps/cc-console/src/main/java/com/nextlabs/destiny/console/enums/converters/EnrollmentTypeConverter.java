package com.nextlabs.destiny.console.enums.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.nextlabs.destiny.console.enums.EnrollmentType;

/**
 * Convert the enrollment type stored in the database to enrollment type enum.
 *
 * @author Sachindra Dasun
 */
@Converter
public class EnrollmentTypeConverter implements AttributeConverter<EnrollmentType, String> {

    @Override
    public String convertToDatabaseColumn(EnrollmentType enrollmentType) {
        return enrollmentType.getName();
    }

    @Override
    public EnrollmentType convertToEntityAttribute(String name) {
        return EnrollmentType.fromName(name);
    }

}
