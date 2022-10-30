package com.nextlabs.destiny.console.utils;

public class PrincipalUtil {
    private static final String EXT_MARKER = "#EXT#";
    private static final Character AT = '@';
    private static final Character UNDERSCORE = '_';

    private PrincipalUtil() {
        super();
    }

    public static String extractUID(String uid) {
        if(uid.indexOf(EXT_MARKER) != -1) {
            String beforeEXT = uid.substring(0, uid.indexOf(EXT_MARKER));
            int lastUnderscore = beforeEXT.lastIndexOf(UNDERSCORE);
            StringBuilder originalUID = new StringBuilder(beforeEXT.substring(0, lastUnderscore));
            originalUID.append(AT).append(beforeEXT.substring(lastUnderscore + 1));

            return originalUID.toString();
        }

        return uid;
    }
}
