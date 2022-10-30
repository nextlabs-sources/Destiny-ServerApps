package com.nextlabs.destiny.cc.installer.enums;

/**
 * Control Center installation types.
 *
 * @author Sachindra Dasun
 */
public enum InstanceType {
    INSTALLER, COMPLETE, MANAGEMENT_SERVER, ICENET;

    public String getComponents() {
        switch (this) {
            case COMPLETE: {
                return "administrator, cas, config_service, console, dabs, dac, dcsf, dem, dkms, dms, dps, reporter";
            }
            case ICENET: {
                return "dabs, dkms";
            }
            case INSTALLER: {
                return "installer";
            }
            case MANAGEMENT_SERVER: {
                return "administrator, cas, config_service, console, dac, dcsf, dem, dkms, dms, dps, reporter";
            }
        }
        return "";
    }
}
