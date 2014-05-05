package com.github.golimpio.atm.services;

public class AtmException extends Exception {

    private boolean internalError;

    public AtmException(String message) {
        this(message, false);
    }

    public AtmException(String message, boolean isInternalError) {
        super(message);
        internalError = isInternalError;
    }

    public boolean isInternalError() {
        return internalError;
    }
}
