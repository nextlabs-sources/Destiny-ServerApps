/*
 * Created on May 17, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.inquirycenter.report.defaultimpl.helpers;

import java.util.StringTokenizer;

import com.bluejungle.destiny.types.basic.v1.StringList;

/**
 * This class assembles / disassemble a flat expression (in a string) to a
 * collection of String objects, and vice versa
 * 
 * @author ihanen
 */
public class ExpressionCutter {

    private static final String SEPARATOR = ",";

    private static final String SPACE = " ";

    /**
     * Converts a string list to a user readable expression.
     * 
     * @param stringList
     *            list of strings to convert
     * @return a human readable expression
     */
    public static String convertFromStringList(StringList stringList) {
        String result = null;
        if (stringList != null && stringList.getValues() != null) {
            String[] values = stringList.getValues();
            int length = values.length;
            if (length > 0) {
                result = values[0];
                for (int index = 1; index < length; index++) {
                    result += SEPARATOR + SPACE + values[index];
                }
            }
        }
        return result;
    }

    /**
     * Converts a string expression to a string list, based on the separator
     * character
     * 
     * @param expression
     *            expression to convert
     * @return a stringlist, with one item per element of the list.
     */
    public static StringList convertToStringList(String expression) {
        StringList result = null;
        if (expression != null) {
            result = new StringList();
            StringTokenizer tokenizer = new StringTokenizer(expression, SEPARATOR);
            int nbTokens = tokenizer.countTokens();
            String[] values = new String[nbTokens];
            for (int index = 0; index < nbTokens; index++) {
                String element = tokenizer.nextToken().trim();
                values[index] = element;
            }
            result.setValues(values);
        }
        return result;
    }
}