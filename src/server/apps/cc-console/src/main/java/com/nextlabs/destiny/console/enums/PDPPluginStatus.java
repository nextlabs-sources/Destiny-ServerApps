package com.nextlabs.destiny.console.enums;

/**
 * List of PIP plugin statuses
 *
 * @author Chok Shah Neng
 */
public enum PDPPluginStatus {
    DRAFT, DEPLOYED, INACTIVE, DELETED, UNKNOWN;

    public static PDPPluginStatus get(String status) {
        for (PDPPluginStatus s : PDPPluginStatus.values()) {
            if (s.name().equals(status)) {
                return s;
            }
        }

        return UNKNOWN;
    }
}
