package com.tpgen.common;

/**
 * Generic exceptions that occurs in the processmanager
 *
 * @author Shalinda Ranasinghe
 * @version 1.0
 *
 */
public class FrameworkException extends Exception {

    private Throwable cause;

    public FrameworkException() {
        super();
    }

    public FrameworkException(String message) {
	    super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public FrameworkException(Throwable cause) {
        super();
        this.cause = cause;
    }

    /**
     * Cause of the exception
     * 
     * @return Cause
     */
    public Throwable getCause() {
        return cause;
    }
}

