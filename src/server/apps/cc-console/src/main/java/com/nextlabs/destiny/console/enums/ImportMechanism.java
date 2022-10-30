package com.nextlabs.destiny.console.enums;

public enum ImportMechanism {

    PARTIAL,
    FULL;

    public static ImportMechanism getMechanism(String name) {
        for(ImportMechanism mechanism : values()) {
            if(mechanism.name().equals(name)) {
                return mechanism;
            }
        }
        return null;
    }
}
