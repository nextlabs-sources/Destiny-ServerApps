/*
 * Created on Mar 14, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.renderers;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * This is a date utility class to perform various date manipulation / lookup
 * for the renderer classes.
 * 
 * @author ihanen
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/renderers/DateUtil.java#1 $
 */

public class DateUtil {

    /**
     * Returns the index of the day within a week (Monday is 0 - Sunday is 6)
     * 
     * @param day
     *            calendar day code.
     * @return the index of the day within a week (Monday is 0 - Sunday is 6)
     */
    public static int mapCalendarDayToCommonDay(int day) {
        switch (day) {
        case Calendar.TUESDAY:
            return 1;
        case Calendar.WEDNESDAY:
            return 2;
        case Calendar.THURSDAY:
            return 3;
        case Calendar.FRIDAY:
            return 4;
        case Calendar.SATURDAY:
            return 5;
        case Calendar.SUNDAY:
            return 6;
        default:
            return 0;
        }
    }

    /**
     * Maps each week day to the symbols based on the current locale
     * 
     * @param symbols
     *            list of symbols for the current locale.
     * @return an array of Strings corresponding to the week days in the current
     *         locale
     */
    public static String[] mapWeekdays(DateFormatSymbols symbols) {
        String[] weekdays = new String[7];
        String[] localeWeekdays = symbols.getShortWeekdays();
        weekdays[0] = localeWeekdays[Calendar.MONDAY];
        weekdays[1] = localeWeekdays[Calendar.TUESDAY];
        weekdays[2] = localeWeekdays[Calendar.WEDNESDAY];
        weekdays[3] = localeWeekdays[Calendar.THURSDAY];
        weekdays[4] = localeWeekdays[Calendar.FRIDAY];
        weekdays[5] = localeWeekdays[Calendar.SATURDAY];
        weekdays[6] = localeWeekdays[Calendar.SUNDAY];
        return weekdays;
    }
}