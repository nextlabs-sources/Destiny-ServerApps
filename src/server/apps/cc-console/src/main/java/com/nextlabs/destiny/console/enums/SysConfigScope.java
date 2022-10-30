package com.nextlabs.destiny.console.enums;

public enum SysConfigScope {

    ADMINISTRATOR("administrator"),
    APPLICATION("application"),
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

    SysConfigScope(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
