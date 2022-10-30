package com.nextlabs.destiny.console.enums;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class LogMarker {

    public static final Marker AUDIT = MarkerFactory.getMarker("Audit");
    public static final Marker AUTHENTICATION = MarkerFactory.getMarker("Authentication");
    public static final Marker SECURITY = MarkerFactory.getMarker("Security");
    public static final Marker SYSTEM = MarkerFactory.getMarker("System");

    private LogMarker() {
        super();
    }
}
