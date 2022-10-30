package com.nextlabs.destiny.console.dto.enrollment;

import com.nextlabs.destiny.console.dto.common.BaseDTO;
import com.nextlabs.destiny.console.enums.EnrollmentPropertyType;
import com.nextlabs.destiny.console.model.dictionary.EnrollmentProperty;
import com.nextlabs.destiny.console.utils.enrollment.EnrollmentConstants;

/**
 * DTO for Enrollment Property.
 *
 * @author Sachindra Dasun
 */
public class EnrollmentPropertyDTO extends BaseDTO {

    private static final long serialVersionUID = 3695632558856101719L;

    public static final String ENROLLMENT_DISPLAY_VALUE = "_ENROLLMENT_DISPLAY_VALUE_";
    private String name;
    private EnrollmentPropertyType type;
    private String value;

    public EnrollmentPropertyDTO() {
    }

    public EnrollmentPropertyDTO(EnrollmentProperty enrollmentProperty) {
        this.id = enrollmentProperty.getId();
        this.name = enrollmentProperty.getName();
        this.type = enrollmentProperty.getType();
        this.value = EnrollmentConstants.PASSWORD.equals(name) || EnrollmentConstants.APPLICATION_KEY.equals(name) ?
                ENROLLMENT_DISPLAY_VALUE : enrollmentProperty.getValue();
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnrollmentPropertyType getType() {
        return type;
    }

    public void setType(EnrollmentPropertyType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
