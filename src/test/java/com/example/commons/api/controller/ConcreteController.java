package com.example.commons.api.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.commons.api.dto.ResponseMeta;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ConcreteController extends BaseController {

    public ConcreteController() {
        super(ConcreteController.class);
    }

    public ResponseEntity<ResponseMeta> buildResponseHttpStatusNull() {
        return super.buildResponse(null, null);
    }

    public ResponseEntity<ResponseMeta> buildResponseResultNull() {
        return super.buildResponse(HttpStatus.OK, null);
    }

    public ResponseEntity<ResponseMeta> buildResponseResultObject() {
        return super.buildResponse(HttpStatus.OK, new Object());
    }

    public ResponseEntity<ResponseMeta> buildResponseResultList() {
        List<String> list = new ArrayList<>();

        return super.buildResponse(HttpStatus.OK, list);
    }

}
