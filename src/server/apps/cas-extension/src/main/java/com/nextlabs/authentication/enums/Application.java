package com.nextlabs.authentication.enums;

public enum Application {

    APPLICATION("application"),
    ADMINISTRATOR("administrator"),
    CAS("cas"),
    CONFIG_SERVICE("config-service"),
    CONSOLE("console"),
    DABS("dabs"),
    DAC("dac"),
    DCSF("dcsf"),
    DEM("dem"),
    DKMS("dkms"),
    DMS("dms"),
    DPS("dps"),
    REPORTER("reporter");

    private String code;

    Application(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
