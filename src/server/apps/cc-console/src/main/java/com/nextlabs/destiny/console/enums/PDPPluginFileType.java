package com.nextlabs.destiny.console.enums;

public enum PDPPluginFileType {

    PROPERTIES, PRIMARY_JAR, THIRD_PARTY_JAR, OTHER;

    public static PDPPluginFileType get(String type) {
        for (PDPPluginFileType t : PDPPluginFileType.values()) {
            if (t.name().equals(type)) {
                return t;
            }
        }

        return PRIMARY_JAR;
    }
}
