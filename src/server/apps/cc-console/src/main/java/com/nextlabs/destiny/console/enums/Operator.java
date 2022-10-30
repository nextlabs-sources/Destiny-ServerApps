/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 12, 2016
 *
 */
package com.nextlabs.destiny.console.enums;

/**
 * Component supporting operators
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public enum Operator {

    NOT, IN;

    public static Operator get(String name) {
        for (Operator op : Operator.values()) {
            if (op.name().equalsIgnoreCase(name))
                return op;
        }
        return null;
    }
}
