package com.nextlabs.destiny.cc.installer.exceptions;

public class InstallerException extends RuntimeException {

    private static final long serialVersionUID = 5059639498087812763L;

    public InstallerException() {
        super();
    }

    public InstallerException(String message) {
        super(message);
    }

    public InstallerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InstallerException(Throwable cause) {
        super(cause);
    }

    public InstallerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
