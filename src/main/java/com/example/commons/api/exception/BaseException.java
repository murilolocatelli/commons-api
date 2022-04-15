package com.example.commons.api.exception;

public class BaseException extends RuntimeException {

    private String[] parameters;

    public BaseException(String... parameters) {
        this.parameters = parameters;
    }

    public String[] getParameters() {
        return this.parameters.clone();
    }

}
