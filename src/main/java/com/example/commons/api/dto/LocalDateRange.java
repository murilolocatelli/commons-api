package com.example.commons.api.dto;

import java.time.LocalDate;

import com.example.commons.api.exception.InvalidDateRangeException;
import com.example.commons.api.exception.MissingParameterException;

import org.springframework.format.annotation.DateTimeFormat;

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
public class LocalDateRange {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate start;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate end;

    public void validate() {
        if (start == null && end == null) {
            return;
        }

        if (start != null && end == null) {
            throw new MissingParameterException("end");
        }

        if (end != null && start == null) {
            throw new MissingParameterException("start");
        }

        if (start.isAfter(end)) {
            throw new InvalidDateRangeException();
        }
    }

}
