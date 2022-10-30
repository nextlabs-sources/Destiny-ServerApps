/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 *
 */
package com.nextlabs.destiny.console.enums;


import java.util.Set;

public enum ElementType {
    APPLICATION(Set.of("appFingerPrint", "displayName", "systemReference", "uniqueName")),
    CLIENT_INFO(Set.of()),
    CONTACT(Set.of("displayname", "firstName", "lastName", "mail", "principalName")),
    HOST(Set.of("dnsName", "unixId", "windowsSid")),
    SITE(Set.of()),
    USER(Set.of("displayname", "firstName", "lastName", "mail", "principalName", "unixId", "windowsSid"));

    private Set<String> preSeededAttributes;

    ElementType(Set<String> preSeededAttributes) {
        this.preSeededAttributes = preSeededAttributes;
    }

    public Set<String> getPreSeededAttributes() {
        return preSeededAttributes;
    }
}
