/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Nov 12, 2015
 *
 */
package com.nextlabs.destiny.console.enums;

/**
 *
 * Enum to maintain policy status
 * 
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public enum PolicyEffect {

    allow("policy.mgmt.effect.allow"), deny("policy.mgmt.effect.deny");

    private String key;

    private PolicyEffect(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static PolicyEffect get(String name) {
        for (PolicyEffect effect : PolicyEffect.values()) {
            if (effect.name().equalsIgnoreCase(name))
                return effect;
        }
        return null;
    }

}
