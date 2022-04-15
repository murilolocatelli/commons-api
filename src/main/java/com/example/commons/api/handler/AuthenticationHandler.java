package com.example.commons.api.handler;

import static java.text.MessageFormat.format;

import com.example.commons.api.dto.ResponseError;
import com.example.commons.api.service.JsonService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationHandler implements AuthenticationEntryPoint {

    @Autowired
    private JsonService jsonService;

    @Override
    public void commence(
        HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        ResponseError responseError = ResponseError.builder()
            .developerMessage(format("Unauthorized: {0}", authException.getMessage()))
            .userMessage("You are not authorized to perform this operation")
            .build();

        response.getWriter().println(this.jsonService.toJsonString(responseError));
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

}
