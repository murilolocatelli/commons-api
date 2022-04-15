package com.example.commons.api.exception;

public class MissingParameterException extends BaseException {

    public MissingParameterException(String... parameters) {
        super(parameters);
    }

}
