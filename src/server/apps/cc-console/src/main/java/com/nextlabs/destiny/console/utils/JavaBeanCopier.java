/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 27, 2016
 *
 */
package com.nextlabs.destiny.console.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SerializationUtils;

import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.model.BaseModel;

/**
 * Copy java beans with all the properties
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class JavaBeanCopier {

    /**
     * Add suffix to cloned object
     * 
     * @param label
     * @return
     */
    public static String clonedLabelSuffix(String label) {

        int copyCount = 0;
        if (label.matches("^.+?\\(\\d{0,4}\\)$")) {
            Pattern p = Pattern.compile("\\(\\d+\\)$");
            Matcher m = p.matcher(label);
            if (m.find()) {
                String value = m.group(0);
                value = value.replace("(", "").replace(")", "");
                copyCount = 1 + Integer.valueOf(value);
            }
            label = label.replaceAll("\\(\\d+\\)$",
                    "(" + String.valueOf(copyCount) + ")");
        } else {
            label = label.concat(" - (1)");
        }

        return label;
    }

    /**
     * Copy and remove all the ids
     * 
     * @param fromBean
     *            bean class
     * @return cloned version of given bean
     * @throws Exception
     */
    public static <T extends BaseModel> T copyAndRemoveIds(T fromBean,
            List<String> fieldNamesToSkip) throws ConsoleException {
        try {
            T newBean = copy(fromBean);
            resetIdsRecursively(newBean, fieldNamesToSkip);
            return newBean;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error occurred during object copy and removing ids,", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends BaseModel> void resetIdsRecursively(T bean,
            List<String> fieldNamesToSkip) throws IllegalAccessException {
        for (Field field : bean.getClass().getDeclaredFields()) {

            if (fieldNamesToSkip.contains(field.getName())) {
                continue;
            }

            if (Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);

                if (field.getName().equals("id")) {
                    field.set(bean, null);
                    continue;

                } else if (field.getType().equals(Set.class)) {
                    Set<T> items = (Set<T>) field.get(bean);
                    for (T item : items) {
                        resetIdsRecursively(item, fieldNamesToSkip);
                    }
                } else if (field.getType().equals(List.class)) {

                    List<T> items = (List<T>) field.get(bean);
                    for (T item : items) {
                        resetIdsRecursively(item, fieldNamesToSkip);
                    }
                }
            }
        }
    }

    /**
     * Returns a deeply cloned java bean.
     *
     * @param fromBean
     *            java bean to be cloned.
     * @return a new java bean cloned from fromBean.
     */
    public static <T extends BaseModel> T copy(T fromBean) {
        return SerializationUtils.clone(fromBean);
    }

}
