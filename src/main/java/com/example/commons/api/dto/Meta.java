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
public class Meta {

    private String version;

    private String server;

    private Long offset;

    private Long limit;

    private Long recordCount;

    private Long totalRecords;
    
}
