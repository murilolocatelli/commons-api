package com.example.commons.api.filter.log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.commons.api.service.JsonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LogRequestFilter extends OncePerRequestFilter {

    //TODO: Review Log

    @Autowired
    private JsonService jsonService;

    private static final List<String> URI_EXCLUDE_LOG = Arrays.asList("/v2/api-docs", "/swagger", "/actuator");

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        if (URI_EXCLUDE_LOG.stream().anyMatch(m -> request.getRequestURI().startsWith(m))) {
            
            filterChain.doFilter(request, response);
            return;
        }

        long start = System.currentTimeMillis();

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(requestWrapper, responseWrapper);
        
        this.logger(requestWrapper, responseWrapper, this.getResponseTime(start));
    }

    private void logger(
        ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper,
        long responseTime) throws IOException {

        String basePath = requestWrapper.getServletPath();
        String message = requestWrapper.getMethod() + " " + basePath;

        responseWrapper.copyBodyToResponse();

        String requestHeaders = this.getRequestHeaders(requestWrapper);
        String responseHeaders = this.getResponseHeaders(responseWrapper);

        String requestParams = this.getRequestParams(requestWrapper);

        String requestBody = this.getRequestBody(requestWrapper);
        String responseBody = this.getResponseBody(responseWrapper);

        Map<String, Object> data = new HashMap<>();

        data.put("request.server", requestWrapper.getServerName());
        data.put("request.port", requestWrapper.getServerPort());
        data.put("request.method", requestWrapper.getMethod());
        data.put("request.basePath", basePath);
        data.put("request.params", requestParams);
        data.put("request.headers", requestHeaders);
        data.put("request.body", requestBody);
        data.put("request.url", requestWrapper.getRequestURL().toString());
        data.put("response.headers", responseHeaders);
        data.put("response.body", responseBody);
        data.put("response.status", responseWrapper.getStatus());
        data.put("response.time", responseTime);

        int status = responseWrapper.getStatus();

        if (status < 500) {
            log.info(message + data);
        } else {
            log.error(message + data);
        }
    }

    private long getResponseTime(long start) {
        long end = System.currentTimeMillis();
        return end - start;
    }

    private String getRequestParams(ContentCachingRequestWrapper requestWrapper) {
        return requestWrapper.getParameterMap().entrySet()
            .stream()
            .map(entry -> entry.getKey() + "=" + Arrays.asList(entry.getValue()))
            .collect(Collectors.joining(", "));
    }

    private String getRequestHeaders(ContentCachingRequestWrapper requestWrapper) {
        return Collections.list(requestWrapper.getHeaderNames())
            .stream()
            .map(t -> t + "=" + requestWrapper.getHeader(t))
            .collect(Collectors.joining(", "));
    }

    private String getResponseHeaders(ContentCachingResponseWrapper responseWrapper) {
        return responseWrapper.getHeaderNames()
            .stream()
            .map(t -> t + "=" + responseWrapper.getHeader(t))
            .collect(Collectors.joining(", "));
    }

    private String getRequestBody(ContentCachingRequestWrapper requestWrapper) {
        return this.jsonService.removeNewlineAndTabFromString(
            new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8));
    }

    private String getResponseBody(ContentCachingResponseWrapper responseWrapper) {
        return this.jsonService.removeNewlineAndTabFromString(
            new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8));
    }

}
