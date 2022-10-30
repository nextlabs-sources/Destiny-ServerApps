package com.bluejungle.destiny.webui.framework.user;

public class PasswordHistoryException extends Exception {

		
    /**
     * Constructor
     * 
     */
    public PasswordHistoryException() {
        super();
    }

    /**
     * Constructor
     * @param arg0
     */
    public PasswordHistoryException(String arg0) {
        super(arg0);
    }

    /**
     * Constructor
     * @param arg0
     */
    public PasswordHistoryException(Throwable arg0) {
        super(arg0);
    }

    /**
     * Constructor
     * @param arg0
     * @param arg1
     */
    public PasswordHistoryException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
