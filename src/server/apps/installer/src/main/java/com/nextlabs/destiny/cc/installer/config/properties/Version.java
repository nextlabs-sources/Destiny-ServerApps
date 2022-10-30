package com.nextlabs.destiny.cc.installer.config.properties;

import javax.validation.constraints.NotEmpty;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Control Center version.
 *
 * @author Sachindra Dasun
 */
public class Version implements Comparable<Version> {

    public static final String VERSION_DELIMITER = "\\.";
    public static final Version V_8_7_0_0 = new Version("8.7.0.0");
    public static final Version V_9_0_0_0 = new Version("9.0.0.0");
    public static final Version V_2020_04 = new Version("2020.04");
    public static final Version V_2021_03 = new Version("2021.03");
    public static final Version V_2022_01 = new Version("2022.01");
    public static final Version V_2022_03 = new Version("2022.03");
    @NotEmpty(message = "{version.noEmpty}")
    private String ccVersion;

    public Version() {
    }

    public Version(String ccVersion) {
        // The version can include the build number which is separated from the version using "-".
        this.ccVersion = ccVersion.split("-")[0].trim();
    }

    @Override
    public int hashCode() {
        return Objects.hash(ccVersion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return compareTo(version) == 0;
    }

    @Override
    public String toString() {
        return ccVersion;
    }

    @Override
    public int compareTo(Version o) {
        String[] thisVersion = this.ccVersion.split(VERSION_DELIMITER);
        String[] otherVersion = o.ccVersion.split(VERSION_DELIMITER);
        for (int i = 0; i < Math.max(thisVersion.length, otherVersion.length); i++) {
            String thisVersionComponent = i < thisVersion.length ? thisVersion[i] : "0";
            String otherVersionComponent = i < otherVersion.length ? otherVersion[i] : "0";
            int result = new BigInteger(thisVersionComponent).compareTo(new BigInteger(otherVersionComponent));
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    public boolean before(Version version) {
        return compareTo(version) < 0;
    }

    public String getCcVersion() {
        return ccVersion;
    }

    public void setCcVersion(String ccVersion) {
        // The version can include the build number which is separated from the version using "-".
        this.ccVersion = ccVersion.split("-")[0].trim();
    }

}
