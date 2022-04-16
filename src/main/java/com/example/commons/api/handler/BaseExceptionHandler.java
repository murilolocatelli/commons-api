package com.example.commons.api.handler;

import static com.example.commons.api.util.StringUtils.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.example.commons.api.dto.ResponseError;
import com.example.commons.api.exception.EntityAlreadyExistsException;
import com.example.commons.api.exception.EntityNotFoundException;
import com.example.commons.api.exception.InvalidDateRangeException;
import com.example.commons.api.exception.MalformedRequestException;
import com.example.commons.api.exception.MissingParameterException;
import com.example.commons.api.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class BaseExceptionHandler {

    private static final String ZONED_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String LOCAL_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final String LOCAL_DATE_PATTERN = "yyyy-MM-dd";
    private static final String INVALID_FIELD = "Invalid field {0} - it must be filled with a valid date in pattern {1}";
    private static final String INVALID_PARAMETER = "Invalid parameter {0} - it must be filled with a valid date in pattern {1}";

    //TODO: Review ExceptionHandler

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ResponseError> exception(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ResponseError> errorMessages = new ArrayList<>();

        fieldErrors.forEach(fieldError ->
            errorMessages.add(this.getResponseError(fieldError)));

        return errorMessages;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ResponseError> exception(HttpMessageNotReadableException ex) {
        List<ResponseError> errorMessages = new ArrayList<>();
        Throwable cause = ex.getCause();

        if (cause instanceof JsonMappingException jsonMappingException) {
            String field = this.getField(jsonMappingException);
            String message = ex.getMessage();

            errorMessages.add(this.getResponseError(message, field));
        } else {
            errorMessages.add(this.getMalformedRequestResponseError());
        }

        return errorMessages;
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ResponseError> exception(BindException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ResponseError> errorMessages = new ArrayList<>();

        fieldErrors.forEach(fieldError -> {
            if ("typeMismatch".equalsIgnoreCase(fieldError.getCode())) {
                String message = fieldError.getDefaultMessage();

                errorMessages.add(this.getResponseError(message, fieldError.getField()));
            } else {
                errorMessages.add(this.getResponseError(fieldError));
            }
        });

        return errorMessages;
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ResponseError> exception(InvalidDateRangeException ex) {
        return this.createResponseErrors(
            "Invalid date range", "Invalid date range - start date must be lesser or equal than end date");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ResponseError> exception(MethodArgumentTypeMismatchException ex) {
        return Collections.singletonList(this.getMalformedRequestResponseError());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public List<ResponseError> exception(NoHandlerFoundException ex) {
        return this.createResponseErrors("Resource not found", "Resource not found");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public List<ResponseError> exception(HttpRequestMethodNotSupportedException ex) {
        return this.createResponseErrors("Method not allowed", "Method not allowed");
    }

    @ResponseBody
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<ResponseError> exception(EntityNotFoundException ex) {
        return this.createResponseErrors(
            format("{0} not found", ex.getParameters()),
            format("You attempted to get a {0}, but did not find any", ex.getParameters()));
    }

    @ResponseBody
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public List<ResponseError> exception(UnauthorizedException ex) {
        return this.createResponseErrors(
            format("Unauthorized - {0}", ex.getParameters()), "You are not authorized to perform this operation");
    }

    @ResponseBody
    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public List<ResponseError> exception(EntityAlreadyExistsException ex) {
        return this.createResponseErrors(
            format("{0} already exists", ex.getParameters()),
            format("You attempted to create {0}, but already exists", ex.getParameters()));
    }

    @ResponseBody
    @ExceptionHandler(MissingParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ResponseError> exception(MissingParameterException ex) {
        return Collections.singletonList(this.getMissingParameterResponseError(ex.getParameters()));
    }

    @ResponseBody
    @ExceptionHandler(MalformedRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ResponseError> exception(MalformedRequestException ex) {
        return Collections.singletonList(this.getMalformedRequestResponseError());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public List<ResponseError> exception(Exception ex) {
        String developerMessage = "Internal server error {0}";
        String userMessage = "Was encountered an error when processing your request. We apologize for the inconvenience";

        return this.createResponseErrors(
            format(developerMessage, ex.toString()), format(userMessage, ex.toString()));
    }

    private ResponseError getResponseError(String message, String fieldName) {
        if (message == null) {
            message = "";
        }
        
        if (message.contains("JSON parse error: Cannot deserialize value of type `short`")
            || message.contains("JSON parse error: Cannot deserialize value of type `integer`")
            || message.contains("JSON parse error: Cannot deserialize value of type `long`")
            || message.contains("java.lang.Short")
            || message.contains("java.lang.Integer")
            || message.contains("java.lang.Long")) {

            String developerMessage =
                format("Invalid parameter {0} - it must be filled with a valid integer number", fieldName);

            String userMessage =
                format("Invalid field {0} - it must be filled with a valid integer number", fieldName);

            return ResponseError.builder()
                .developerMessage(developerMessage)
                .userMessage(userMessage)
                .build();

        } else if (message.contains("JSON parse error: Cannot deserialize value of type `double`")
            || message.contains("JSON parse error: Cannot deserialize value of type `fload`")
            || message.contains("java.lang.Double")
            || message.contains("java.lang.Float")) {

            String developerMessage =
                format("Invalid parameter {0} - it must be filled with a valid number", fieldName);

            String userMessage =
                format("Invalid field {0} - it must be filled with a valid number", fieldName);

            return ResponseError.builder()
                .developerMessage(developerMessage)
                .userMessage(userMessage)
                .build();

        } else if (message.contains("java.lang.Boolean")) {
            String developerMessage =
                format("Invalid parameter {0} - it must be filled with a valid boolean (true or false)", fieldName);

            String userMessage =
                format("Invalid field {0} - it must be filled with a true or false", fieldName);

            return ResponseError.builder()
                .developerMessage(developerMessage)
                .userMessage(userMessage)
                .build();

        } else if (message.contains("java.time.ZonedDateTime")) {
            String developerMessage =
                format(INVALID_PARAMETER,
                    fieldName, ZONED_DATE_TIME_PATTERN);

            String userMessage =
                format(INVALID_FIELD,
                    fieldName, ZONED_DATE_TIME_PATTERN);

            return ResponseError.builder()
                .developerMessage(developerMessage)
                .userMessage(userMessage)
                .build();

        } else if (message.contains("java.time.LocalDateTime")) {
            String developerMessage =
                format(INVALID_PARAMETER,
                    fieldName, LOCAL_DATE_TIME_PATTERN);

            String userMessage =
                format(INVALID_FIELD,
                    fieldName, LOCAL_DATE_TIME_PATTERN);

            return ResponseError.builder()
                .developerMessage(developerMessage)
                .userMessage(userMessage)
                .build();

        } else if (message.contains("java.time.LocalDate")) {
            String developerMessage =
                format(INVALID_PARAMETER,
                    fieldName, LOCAL_DATE_PATTERN);

            String userMessage =
                format(INVALID_FIELD,
                    fieldName, LOCAL_DATE_PATTERN);

            return ResponseError.builder()
                .developerMessage(developerMessage)
                .userMessage(userMessage)
                .build();

        } else {
            return this.getMalformedRequestResponseError();
        }
    }

    private ResponseError getResponseError(FieldError fieldError) {
        String fieldName = fieldError.getField();

        if ("NotNull".equalsIgnoreCase(fieldError.getCode())
            || "NotBlank".equalsIgnoreCase(fieldError.getCode())) {

            return this.getMissingParameterResponseError(new String[] {fieldName});

        } else if ("Min".equalsIgnoreCase(fieldError.getCode())) {
            String developerMessage =
                format("Invalid parameter {0} - it must be filled with a value greater than or equal to {1}",
                    fieldName, this.getValue(fieldError).toString());

            String userMessage =
                format("Invalid field {0} - it must be filled with a value greater than or equal to {1}",
                fieldName, this.getValue(fieldError).toString());

            return ResponseError.builder()
                .developerMessage(developerMessage)
                .userMessage(userMessage)
                .build();

        } else if ("Max".equalsIgnoreCase(fieldError.getCode())) {
            String developerMessage =
                format("Invalid parameter {0} - it must be filled with a value less than or equal to {1}",
                fieldName, this.getValue(fieldError).toString());

            String userMessage =
                format("Invalid field {0} - it must be filled with a value less than or equal to {1}",
                fieldName, this.getValue(fieldError).toString());

            return ResponseError.builder()
                .developerMessage(developerMessage)
                .userMessage(userMessage)
                .build();

        } else {
            return this.getMalformedRequestResponseError();
        }
    }

    private ResponseError getMalformedRequestResponseError() {
        return ResponseError.builder()
            .developerMessage("Malformed request")
            .userMessage("Malformed request")
            .build();
    }

    private ResponseError getMissingParameterResponseError(String[] parameters) {
        return ResponseError.builder()
            .developerMessage(format("Missing parameter {0}", parameters))
            .userMessage(format("Field {0} is required and can not be empty", parameters))
            .build();
    }

    private Long getValue(FieldError fieldError) {
        return (Long) Optional.ofNullable(fieldError.getArguments())
            .filter(t -> t.length > 1)
            .map(t -> t[1])
            .orElse("");
    }

    private String getField(JsonMappingException jsonMappingException) {
        return jsonMappingException.getPath().stream()
            .map(t -> t.getFieldName() != null ? t.getFieldName() : "[" + t.getIndex() + "]")
            .reduce((t, u) -> {
                if (u.contains("[")) {
                    return t + u;
                } else {
                    return t + "." + u;
                }
            })
            .orElse("");
    }

    private List<ResponseError> createResponseErrors(String developerMessage, String userMessage) {
        return Collections.singletonList(ResponseError.builder()
            .developerMessage(developerMessage)
            .userMessage(userMessage)
            .build());
    }

}
