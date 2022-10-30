/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 22, 2016
 *
 */
package com.nextlabs.destiny.console.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.nextlabs.destiny.console.exceptions.ConsoleException;

/**
 *
 * MD5 Generator utility class
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public final class MD5Generator {

    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static String encode(String a) throws ConsoleException {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(a.getBytes());
            final byte[] digest = messageDigest.digest();
            final StringBuilder buf = new StringBuilder(digest.length * 2);
            for (int j = 0; j < digest.length; j++) {
                buf.append(HEX_DIGITS[(digest[j] >> 4) & 0x0f]);
                buf.append(HEX_DIGITS[digest[j] & 0x0f]);
            }

            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new ConsoleException(
                    "Error encountered in encoding the given string", e);
        }
    }

}
