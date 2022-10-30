/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 13, 2015
 *
 */
package com.nextlabs.destiny.console.enums;

/**
 *
 * Date Time measurring units
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public enum DateTimeUnit {

    SECONDS, MINUTES, HOURS, DAYS, WEEKS, MONTHS, YEARS;
    
    /**
     * Get DateTimeUnit enum value for given name.
     * 
     * @param name
     * @return {@link DateTimeUnit}
     */
    public static DateTimeUnit get(String name) {
        for (DateTimeUnit dt : DateTimeUnit.values()) {
            if (dt.name().equalsIgnoreCase(name)) {
                return dt;
            }
        }
        return null;
    }
}
