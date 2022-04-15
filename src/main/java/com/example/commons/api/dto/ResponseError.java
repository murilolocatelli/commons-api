package com.example.commons.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class ResponseError {

    private String developerMessage;

    private String userMessage;

}
