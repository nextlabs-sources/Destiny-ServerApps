/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 13, 2015
 *
 */
package com.nextlabs.destiny.console.enums;

import static com.nextlabs.destiny.console.enums.DateTimeUnit.DAYS;
import static com.nextlabs.destiny.console.enums.DateTimeUnit.MONTHS;
import static com.nextlabs.destiny.console.enums.DateTimeUnit.YEARS;

/**
 *
 * System wide Date options provided by this enum
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public enum DateOption {
   
    PAST_7_DAYS(7 , DAYS, "policy.mgmt.search.date.past7"),
    PAST_30_DAYS(30 , DAYS, "policy.mgmt.search.date.past30"), 
    PAST_3_MONTHS(3 , MONTHS, "policy.mgmt.search.date.past3M"), 
    PAST_6_MONTHS(6 , MONTHS, "policy.mgmt.search.date.past6M"),
    PAST_1_YEAR(1 , YEARS, "policy.mgmt.search.date.past1Y"),
    CUSTOM("policy.mgmt.search.date.custom");

    private int value;
    private DateTimeUnit unit;
    private String key;

    private DateOption(String key) {
        this.key = key;
    }

    private DateOption(int value, DateTimeUnit unit, String key) {
        this.value = value;
        this.unit = unit;
        this.key = key;
    }

    public long getValue() {
        return value;
    }
    
    public DateTimeUnit getUnit() {
        return unit;
    }

    public String getKey() {
        return key;
    }
    
    /**
     * Get DateOption enum value for given name.
     * 
     * @param name
     * @return {@link DateOption}
     */
    public static DateOption get(String name) {
        for (DateOption dOpts : DateOption.values()) {
            if (dOpts.name().equalsIgnoreCase(name)) {
                return dOpts;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
