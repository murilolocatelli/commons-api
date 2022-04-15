package com.example.commons.api.controller;

import com.example.commons.api.dto.Meta;
import com.example.commons.api.dto.ResponseMeta;
import com.example.commons.api.exception.EntityNotFoundException;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    @Value("${application.version}")
    private String applicationVersion;

    private String entityName;

    protected BaseController(Class<?> entity) {
        this.entityName = entity.getSimpleName();
    }

    protected ResponseEntity<ResponseMeta> buildResponse(HttpStatus httpStatus, Object result) {

        return this.buildResponse(httpStatus, result, null);
    }

    protected ResponseEntity<ResponseMeta> buildResponse(HttpStatus httpStatus, Object result, Pageable pageable) {

        if (httpStatus == null) {
            throw new IllegalArgumentException();
        }

        if (httpStatus.equals(HttpStatus.NO_CONTENT)) {
            return new ResponseEntity<>(httpStatus);
        }

        if (result == null) {
            throw new EntityNotFoundException(entityName);
        }

        Collection<?> resultList;

        Long totalRecords = null;

        if (result instanceof Page) {

            Page<?> page = (Page<?>) result;

            resultList = page.getContent();

            totalRecords = page.getTotalElements();

        } else if (result instanceof Collection) {

            resultList = (Collection<?>) result;

        } else {

            resultList = Collections.singletonList(result);
        }

        if (resultList.isEmpty()) {
            throw new EntityNotFoundException(entityName);
        }

        Long offset = Optional.ofNullable(pageable)
            .map(Pageable::getOffset)
            .orElse(null);

        Long limit = Optional.ofNullable(pageable)
            .map(Pageable::getPageSize)
            .map(Integer::longValue)
            .orElse(null);

        Meta meta = Meta.builder()
            .version(this.getApplicationVersion())
            .server(this.getServer())
            .offset(offset)
            .limit(limit)
            .recordCount((long) resultList.size())
            .totalRecords(totalRecords)
            .build();

        ResponseMeta responseMeta = ResponseMeta.builder()
            .meta(meta)
            .records(resultList)
            .build();

        return new ResponseEntity<>(responseMeta, httpStatus);
    }

    private String getApplicationVersion() {
        return Optional.ofNullable(this.applicationVersion)
            .map(t -> t.replace("-SNAPSHOT", ""))
            .orElse(this.applicationVersion);
    }

    private String getServer() {
        try {
            return InetAddress.getLocalHost().toString();
        } catch (Exception e) {
            return "unknown";
        }
    }

}
