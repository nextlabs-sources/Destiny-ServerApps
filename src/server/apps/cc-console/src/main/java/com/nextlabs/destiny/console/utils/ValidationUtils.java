/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 5, 2015
 *
 */
package com.nextlabs.destiny.console.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nextlabs.destiny.console.exceptions.InvalidInputParamException;
import com.nextlabs.destiny.console.exceptions.InvalidPasswordException;
import com.nextlabs.destiny.console.services.MessageBundleService;

/**
 *
 * Service Parameter validation utility
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Component
public class ValidationUtils {

    @Autowired
    private MessageBundleService msgBundle;
    
    private Set<String> reservedKeywords;
    private Set<String> reservedAttributeKeywords;
    
    @PostConstruct
    public void setupKeywords() {
        reservedKeywords = new HashSet<>();
        String keywordsList = msgBundle.getText("system.reserved.keywords.list");

        if(keywordsList != null) {
            String[] keywords = keywordsList.trim().split("\\s*,\\s*");

            for (String keyword : keywords) {
                reservedKeywords.add(keyword.toLowerCase());
            }
        }

        reservedAttributeKeywords = new HashSet<>();
        String attributeKeywordsList = msgBundle.getText("system.reserved.keywords.attribute.list");

        if (attributeKeywordsList != null) {
            String[] keywords = attributeKeywordsList.trim().split("\\s*,\\s*");

            for (String keyword : keywords) {
                reservedAttributeKeywords.add(keyword.toLowerCase());
            }
        }

    }

    /**
     * Assert for value should not be null or zero
     * 
     * @param value
     * @param fieldName
     */
    public void assertNotZero(Long value, String fieldName) {
        if (value == null || value < 0) {
            throw new InvalidInputParamException(
                    msgBundle.getText("invalid.input.field.zero.code"),
                    msgBundle.getText("invalid.input.field.zero", fieldName));
        }
    }

    /**
     * Assert for value should not be null or zero
     *
     * @param value
     * @param fieldName
     */
    public void assertNotZero(Integer value, String fieldName) {
        if (value == null || value < 0) {
            throw new InvalidInputParamException(
                            msgBundle.getText("invalid.input.field.zero.code"),
                            msgBundle.getText("invalid.input.field.zero", fieldName));
        }
    }

    /**
     * Assert for value should not be empty or blank
     * 
     * @param value
     * @param fieldName
     */
    public void assertNotBlank(String value, String fieldName) {
        if (StringUtils.isBlank(value)) {
            throw new InvalidInputParamException(
                    msgBundle.getText("invalid.input.field.blank.code"),
                    msgBundle.getText("invalid.input.field.blank", fieldName));
        }
    }

    /**
     * 
     * Assert for object null
     * 
     * @param obj
     * @param fieldName
     */
    public void assertNotNull(Object obj, String fieldName) {
        if (obj == null) {
            throw new InvalidInputParamException(
                    msgBundle.getText("invalid.input.field.blank.code"),
                    msgBundle.getText("invalid.input.field.blank", fieldName));
        }
    }

    /**
     * Assert for object not null
     *
     * @param obj
     * @param fieldName
     */
    public void assertNull(Object obj, String fieldName) {
        if (obj != null) {
            throw new InvalidInputParamException(
                    msgBundle.getText("invalid.input.field.not.blank.code"),
                    msgBundle.getText("invalid.input.field.not.blank", fieldName));
        }
    }

    /**
     * 
     * Assert for minimum length
     * 
     * @param value
     * @param length
     */
    public void assertMinimumLength(String value, int length,
            String fieldName) {
        if (value.length() < length) {
            throw new InvalidInputParamException(
                    msgBundle.getText("invalid.input.field.minlen.code"),
                    msgBundle.getText("invalid.input.field.minlen", fieldName,
                            String.valueOf(length)));
        }
    }

    /**
     * Assert for list's empty check
     *
     * @param c
     * @param fieldName
     */
    @SuppressWarnings("rawtypes")
    public void assertCollectionEmpty(Collection c, String fieldName) {
        if (c == null || c.isEmpty()) {
            throw new InvalidInputParamException(
                    msgBundle.getText("invalid.input.empty.collection.code"),
                    msgBundle.getText("invalid.input.empty.collection",
                            fieldName));
        }
    }

    /**
     * Assert for regular expression match
     * 
     * @param value
     * @param fieldName
     */
    public void assertMatches(String value, Pattern regex, String fieldName,
            String expectedFormat) {
        if (!regex.matcher(value).matches()) {
            throw new InvalidInputParamException(
                    msgBundle.getText("invalid.input.field.pattern.code"),
                    msgBundle.getText("invalid.input.field.pattern", fieldName,
                            expectedFormat));
        }
    }

    /**
     * Assert for two string value are exactly match
     *
     * @param value1
     * @param value2
     */
    public void assertMatches(String value1, String value2) {
        if (!value1.equals(value2)) {
            throw new InvalidInputParamException(
                    msgBundle.getText("invalid.input.field.value.not.match.code"),
                    msgBundle.getText("invalid.input.field.value.not.match", value1, value2));
        }
    }

    public void assertWithin(int value1, Set<Integer> ranges, String fieldName) {
        if (!ranges.contains(value1)) {
            throw new InvalidInputParamException(
                    msgBundle.getText("invalid.input.field.value.not.within.code"),
                    msgBundle.getText("invalid.input.field.value.not.within", Integer.toString(value1),
                            Arrays.toString(ranges.toArray()), fieldName));
        }
    }

    /**
     * Assert given two parameters are unequal
     *
     * @param field1Name
     * @param field2Name
     * @param field1Value
     * @param field2Value
     */
    public void assertNotSame(String field1Name, String field2Name,
            String field1Value, String field2Value) {
        if (field1Value.equals(field2Value)) {
            throw new InvalidPasswordException(
                    msgBundle.getText("field.values.same.code"),
                    msgBundle.getText("field.values.same",field1Name,field2Name));
        }
    }
    
    /**
     * Assert that password is valid
     * 
     * @param password
     */
    public void assertValidPassword(String password) {
        PasswordValidator passwordValidator = new PasswordValidator();
        if (!passwordValidator.validatePassword(password)) {
            throw new InvalidPasswordException(
                    msgBundle.getText("invalid.password.code"),
                    msgBundle.getText("invalid.password.message"));
        }
    }
    
    /**
     * Assert that field value is not a reserved keyword
     * 
     * @param fieldName
     * @param fieldValue
     */
	public void assertNotKeyword(String fieldName, String fieldValue) {
		if (reservedKeywords.contains(fieldValue.toLowerCase())) {
			throw new InvalidInputParamException(
					msgBundle.getText("user.input.reserved.keyword.code"),
					msgBundle.getText("user.input.reserved.keyword", fieldName, fieldValue));
		}
	}

	/**
	 *  Assert that field value does not contain reserved keyword
	 *  
	 * @param fieldName
	 * @param fieldValue
	 */
	public void assertNotContainsKeyword(String fieldName, String fieldValue) {
        if (reservedKeywords.contains(fieldName)) {
            throw new InvalidInputParamException(
                    msgBundle.getText("user.input.reserved.keyword.code"),
                    msgBundle.getText("user.input.reserved.keyword",
                            fieldName, fieldValue));
        }
	}
    
	/**
	 * Assert the 2 fields are not both null
	 * 
	 * @param obj1
	 * @param obj2
	 * @param field1
	 * @param field2
	 */
	public void assertNotBothNull(Object obj1, Object obj2, String field1, String field2) {
	    if (obj1 == null && obj2 == null) {
            throw new InvalidInputParamException(
                    msgBundle.getText("invalid.input.field.blank.code"),
                    msgBundle.getText("invalid.input.field.blank.two", field1, field2));
        }
	}
	
	/**
     * Assert the 2 fields are not both null or blank
     * 
     * @param obj1
     * @param obj2
     * @param field1
     * @param field2
     */
    public void assertNotBothNull(Object obj1, Collection<?> obj2, String field1, String field2) {
        if (obj1 == null && (obj2 == null || obj2.isEmpty())) {
            throw new InvalidInputParamException(
                    msgBundle.getText("invalid.input.field.blank.code"),
                    msgBundle.getText("invalid.input.field.blank.two", field1, field2));
        }
    }

    public void assertNotAttributeKeyword(String fieldName, String fieldValue) {
        if (reservedAttributeKeywords.contains(fieldValue.toLowerCase())) {
            throw new InvalidInputParamException(
                    msgBundle.getText("user.input.reserved.keyword.code"),
                    msgBundle.getText("user.input.reserved.keyword", fieldName, fieldValue));
        }
    }
}
