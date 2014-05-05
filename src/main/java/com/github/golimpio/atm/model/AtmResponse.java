package com.github.golimpio.atm.model;

public class AtmResponse {
    private String message;

    public AtmResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
