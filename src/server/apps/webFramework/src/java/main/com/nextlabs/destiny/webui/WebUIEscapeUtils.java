package com.nextlabs.destiny.webui;

import java.io.UnsupportedEncodingException;


public final class WebUIEscapeUtils {

    private WebUIEscapeUtils() {
    }
    
    public static String escapeURL(String urlString) {
        if (urlString == null) {
            return null;
        }
        try {
            urlString = new String(urlString.getBytes(), "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        
        StringBuilder sb = new StringBuilder();
        for(char c : urlString.toCharArray()){
            if (c == ' ') {
                sb.append("%20");
            } else if (c == '\"') {
                sb.append("%22");
            } else if (!Character.isWhitespace(c) && !Character.isISOControl(c)) {
                sb.append(c);
            }
        }
        
        return sb.toString();

    }
    
}
