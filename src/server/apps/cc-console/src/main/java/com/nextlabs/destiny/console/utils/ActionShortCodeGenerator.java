/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Mar 21, 2016
 *
 */
package com.nextlabs.destiny.console.utils;

/**
 *
 * This class generates the short_code sequence for PM_ACTION_CONFIG table
 *
 * @author aishwarya
 * @since 8.0
 *
 */
public class ActionShortCodeGenerator {

    private static String nextVal;

    /**
     * Get next value of the sequence, given the current value
     * 
     * @param currentVal
     * @return
     */
    public static String getSeqNextVal(String currentVal) {

        if (currentVal == null) {
            currentVal = "a0";
        }
        char[] seqArray = currentVal.toCharArray();

        if (seqArray.length != 2) {
            return null;
        }

        char first = seqArray[0];
        char second = seqArray[1];

        if (second == 'z') {
            first = getNextChar(first);
            second = '0';
        } else if (second >= '0' && second < '9') {
            second++;
        } else if (second == '9') {
            second = 'a';
        } else {
            second = getNextChar(second);
        }

        seqArray[0] = first;
        seqArray[1] = second;

        nextVal = new String(seqArray);
        return nextVal;
    }

    private static char getNextChar(char charVal) {
        if (charVal >= 'a' && charVal < 'z') {
            charVal++;
        }
        return charVal;
    }

}
