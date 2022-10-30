package com.nextlabs.destiny.cc.installer.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestHelper {

    public static void init() throws IOException {
        System.setProperty("nextlabs.cc.home", getCcHome());
    }

    public static String getCcHome() throws IOException {
        return new File(String.format("build/control-center-%s", getCcVersion())).getAbsolutePath();
    }

    public static String getCcVersion() throws IOException {
        try (InputStream inputStream = new FileInputStream("../gradle.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            String version = properties.getProperty("version", "")
                    .replace("-SNAPSHOT", "");
            return version + (version.split(".").length < 4 ? ".0" : "");
        }
    }

}
