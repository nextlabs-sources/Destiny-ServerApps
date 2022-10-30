package com.nextlabs.authentication.models;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author kyu
 * @since 8.0
 */
public final class SysInfo {

    @JsonProperty("license_info")
    private LicenseInfo licenseInfo;
    @JsonProperty("pdp_info")
    private PdpInfo[] pdpInfo;

    public LicenseInfo getLicenseInfo() {
        return licenseInfo;
    }

    public void setLicenseInfo(LicenseInfo licenseInfo) {
        this.licenseInfo = licenseInfo;
    }

    public PdpInfo[] getPdpInfo() {
        return pdpInfo;
    }

    public void setPdpInfo(PdpInfo[] pdpInfo) {
        this.pdpInfo = pdpInfo;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SysInfo [licenseInfo=").append(licenseInfo)
                .append(", pdpInfo=").append(Arrays.toString(pdpInfo))
                .append("]");
        return builder.toString();
    }

    public class LicenseInfo {

        @JsonProperty("expiry_date")
        private String expiryDate;
        @JsonProperty("subcription_mode")
        private String subcriptionMode;

        public LicenseInfo() {
            super();
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        public String getSubcriptionMode() {
            return subcriptionMode;
        }

        public void setSubcriptionMode(String subcriptionMode) {
            this.subcriptionMode = subcriptionMode;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("LicenseInfo [expiryDate=").append(expiryDate)
                    .append(", subcriptionMode=").append(subcriptionMode)
                    .append("]");
            return builder.toString();
        }

    }

}
