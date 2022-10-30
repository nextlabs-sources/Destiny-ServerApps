/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on 6 Jun 2016
 *
 */
package com.nextlabs.destiny.console.enums;

/**
 * Enum to maintain the Enrollment Types
 *
 * @author aishwarya
 * @since 8.0
 */
public enum EnrollmentType {

    ACTIVE_DIRECTORY("com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.ad.ActiveDirectoryEnroller"),
    AZURE_ACTIVE_DIRECTORY("com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.AzureADEnroller"),
    LDIF("com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.ldif.LdifEnroller"),
    SHAREPOINT("com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.sharepoint.SharePointEnroller"),
    MULTI_DOMAIN("com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.mdom.DomainGroupEnroller"),
    SCIM("com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.scim.SCIMEnroller"),
    UNKNOWN("com.bluejungle.destiny.container.share.dictionary.enrollment.enroller.unknown");

    private final String name;

    EnrollmentType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static EnrollmentType fromName(String name) {
        EnrollmentType typeToReturn = null;
        for (EnrollmentType nextType : EnrollmentType.values()) {
            if (nextType.name.equals(name)) {
                typeToReturn = nextType;
            }
        }

        if (typeToReturn == null) {
            throw new IllegalArgumentException("No type for name, " + name);
        }

        return typeToReturn;
    }

}
