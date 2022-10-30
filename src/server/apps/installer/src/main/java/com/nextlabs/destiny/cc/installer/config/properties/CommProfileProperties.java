package com.nextlabs.destiny.cc.installer.config.properties;


import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * Properties available for comm profile configuration.
 *
 * @author Sachindra Dasun
 */
public class CommProfileProperties {

    private String dabsLocation;
    @Min(0)
    private int heartBeatFreqTime;
    @Pattern(regexp = "milliseconds|seconds|minutes|hours|days")
    private String heartBeatFreqTimeUnit;
    private boolean pushEnabled;

    public String getDabsLocation() {
        return dabsLocation;
    }

    public void setDabsLocation(String dabsLocation) {
        this.dabsLocation = dabsLocation;
    }

    public int getHeartBeatFreqTime() {
        return heartBeatFreqTime;
    }

    public void setHeartBeatFreqTime(int heartBeatFreqTime) {
        this.heartBeatFreqTime = heartBeatFreqTime;
    }

    public String getHeartBeatFreqTimeUnit() {
        return heartBeatFreqTimeUnit;
    }

    public void setHeartBeatFreqTimeUnit(String heartBeatFreqTimeUnit) {
        this.heartBeatFreqTimeUnit = heartBeatFreqTimeUnit;
    }

    public boolean isPushEnabled() {
        return pushEnabled;
    }

    public void setPushEnabled(boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

}
