package com.example.commons.api.service.impl;

import com.example.commons.api.service.JsonService;
import com.example.commons.api.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JsonServiceImpl implements JsonService {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String toJsonString(Object object) {
        return Optional.ofNullable(object)
            .map(t -> {
                try {
                    return objectMapper.writeValueAsString(object);
                } catch (JsonProcessingException e) {
                    return null;
                }
            })
            .orElse(null);
    }

    @Override
    public <T> T toObject(String jsonString, Class<T> clazz) {
        return Optional.ofNullable(jsonString)
            .filter(t -> !StringUtils.isEmptyTrim(t))
            .map(t -> {
                try {
                    return objectMapper.readValue(jsonString, clazz);
                } catch (IOException e) {
                    return null;
                }
            })
            .orElse(null);
    }

    @Override
    public String removeNewlineAndTabFromString(String jsonString) {
        return Optional.ofNullable(jsonString)
            .filter(t -> !StringUtils.isEmptyTrim(t))
            .map(t -> jsonString.replaceAll("\"", "").replaceAll("\n", ""))
            .orElse(null);
    }

    @Override
    public ObjectNode toObjectNode(Object object) {
        return (ObjectNode) Optional.ofNullable(object)
            .map(t -> {
                try {
                    return objectMapper.readTree(toJsonString(t));
                } catch (IOException e) {
                    return null;
                }
            })
            .orElse(null);
    }

}
