package com.dynamease.serviceproviders;

public class SpInfoRetrievingException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SpInfoRetrievingException() {

    }

    public SpInfoRetrievingException(String message) {
        super(message);
    }

    public SpInfoRetrievingException(Throwable cause) {
        super(cause);
    }

    public SpInfoRetrievingException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpInfoRetrievingException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
