package com.nextlabs.destiny.cc.installer.helpers;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

/**
 * Utility for accessing Windows Registry.
 *
 * @author Sachindra Dasun
 */
public class RegistryHelper {

    public static final WinReg.HKEY HKEY_CURRENT_USER = WinReg.HKEY_CURRENT_USER;
    public static final WinReg.HKEY HKEY_LOCAL_MACHINE = WinReg.HKEY_LOCAL_MACHINE;

    public static String readString(WinReg.HKEY hkey, String key, String valueName) {
        if (hkey != HKEY_CURRENT_USER && hkey != HKEY_LOCAL_MACHINE) {
            throw new IllegalArgumentException("hkey=" + hkey);
        }

        if (Advapi32Util.registryValueExists(hkey, key, valueName)) {
            return Advapi32Util.registryGetStringValue(hkey, key, valueName);
        } else {
            return null;
        }
    }

    public static void createKey(WinReg.HKEY hkey, String key) {
        if (hkey != HKEY_CURRENT_USER && hkey != HKEY_LOCAL_MACHINE) {
            throw new IllegalArgumentException("hkey=" + hkey);
        }

        if (!Advapi32Util.registryKeyExists(hkey, key)) {
            if (!Advapi32Util.registryCreateKey(hkey, key)) {
                throw new IllegalArgumentException("rc=0  key=" + key);
            }
            Advapi32Util.registryCloseKey(hkey);
        }
    }

    public static void writeStringValue(WinReg.HKEY hkey, String key, String valueName, String value) {
        if (hkey != HKEY_CURRENT_USER && hkey != HKEY_LOCAL_MACHINE) {
            throw new IllegalArgumentException("hkey=" + hkey);
        }

        Advapi32Util.registrySetStringValue(hkey, key, valueName, value);
        Advapi32Util.registryCloseKey(hkey);
    }

    public static void deleteKey(WinReg.HKEY hkey, String key) {
        if (hkey != HKEY_CURRENT_USER && hkey != HKEY_LOCAL_MACHINE) {
            throw new IllegalArgumentException("hkey=" + hkey);
        }

        if (Advapi32Util.registryKeyExists(hkey, key)) {
            Advapi32Util.registryDeleteKey(hkey, key);
        }
    }

    public static void deleteValue(WinReg.HKEY hkey, String key, String value) {
        if (hkey != HKEY_CURRENT_USER && hkey != HKEY_LOCAL_MACHINE) {
            throw new IllegalArgumentException("hkey=" + hkey);
        }

        if (Advapi32Util.registryValueExists(hkey, key, value)) {
            Advapi32Util.registryDeleteValue(hkey, key, value);
        }
    }

}