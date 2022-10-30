package com.nextlabs.authentication.enums;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class LogMarker {

    public static final Marker SYSTEM = MarkerFactory.getMarker("System");
    public static final Marker AUTHENTICATION = MarkerFactory.getMarker("Authentication");

    private LogMarker() {
        super();
    }

}
