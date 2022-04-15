package com.example.commons.api.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface JsonService {

    String toJsonString(Object object);

    <T> T toObject(String jsonString, Class<T> clazz);

    String removeNewlineAndTabFromString(String jsonString);

    ObjectNode toObjectNode(Object object);

}
