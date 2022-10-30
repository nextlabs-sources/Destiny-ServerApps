package com.nextlabs.destiny.webui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator {
	private static final String PREFIX_FORMAT = "yyMMDDHHmmss";
    private static final char PADDING_CHAR = '0';
    private static final byte LENGTH = 0x6;
    private static final int INITIAL_VALUE = 1;
	private static final int MAX_SEQUENCE = 999999;
	
	private static AtomicInteger sequence = new AtomicInteger(INITIAL_VALUE);
	
	public static Long generate() 
		throws RuntimeException {
		String result = toDateString(new Date(), PREFIX_FORMAT);
		
		if(result == null) {
			throw new RuntimeException("Invalid date format prefix.");
		}
		
		result += leftPad(String.valueOf(sequence.getAndIncrement()), PADDING_CHAR, LENGTH);
		
		if(sequence.get() > MAX_SEQUENCE) {
			sequence.set(INITIAL_VALUE);
		}
		
		return Long.parseLong(result);
	}

    private static String toDateString(Date date, String format) {
        try {
            return (new SimpleDateFormat(format).format(date));
        } catch (Exception err) {
            return null;
        }
    }

    private static String leftPad(String value, char paddingChar, byte length) {
        StringBuilder result = new StringBuilder();
        int padLength = length - value.length();

        while (padLength > 0) {
            result.append(paddingChar);
            padLength--;
        }

        return result.append(value).toString();
    }
}
