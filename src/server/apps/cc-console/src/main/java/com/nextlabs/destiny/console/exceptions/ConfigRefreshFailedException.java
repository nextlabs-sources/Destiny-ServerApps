package com.nextlabs.destiny.console.exceptions;

/**
 * Thrown when a configuration refresh request or logger configuration refresh request is failed.
 *
 * @author Sachindra Dasun
 */
public class ConfigRefreshFailedException extends RuntimeException {
    private static final long serialVersionUID = 6703489444597409193L;

    public ConfigRefreshFailedException() {
    }

    public ConfigRefreshFailedException(String url) {
        super(String.format("Configuration refresh request using %s has been failed", url));
    }

}
