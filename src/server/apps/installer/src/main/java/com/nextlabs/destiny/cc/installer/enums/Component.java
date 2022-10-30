package com.nextlabs.destiny.cc.installer.enums;

/**
 * Components available in Control Center platform.
 *
 * @author Sachindra Dasun
 */
public enum Component {
    ADMINISTRATOR("mgmtConsole.war"),
    APP_HOME("app-home.war"),
    CAS("cas.war"),
    CONFIG_SERVICE("config-service.war"),
    CONSOLE("control-center-console.war"),
    DABS("dabs.war"),
    DAC("dac.war"),
    DCSF("dcsf.war"),
    DEM("dem.war"),
    DKMS("dkms.war"),
    DMS("dms.war"),
    DPC("dpc.war"),
    DPS("dps.war"),
    SERVICE_MANAGER("service-manager.war"),
    INSTALLER("installer.war"),
    POLICY_CONTROLLER_MANAGER("policy-controller-manager.war"),
    REPORTER("inquiryCenter.war");

    private String applicationFileName;

    Component(String applicationFileName) {
        this.applicationFileName = applicationFileName;
    }

    public String getApplicationFileName() {
        return applicationFileName;
    }
}
