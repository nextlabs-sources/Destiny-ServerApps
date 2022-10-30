package com.nextlabs.authentication.exceptions;

/**
 * This exception is thrown when a reference by ID cannot be resolved.
 *
 * @author Sachindra Dasun
 */
public class ReferenceResolutionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * This field represents a referenced ID which is unresolved.
     */
    private final Long unresolvedId;

    public ReferenceResolutionException(Long unresolvedId) {
        super("Unresolved ID: " + unresolvedId);
        this.unresolvedId = unresolvedId;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    public Long getUnresolvedId() {
        return unresolvedId;
    }

}
