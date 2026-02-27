package com.banking.app.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

        private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    private List<FieldError> fieldErrors;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }

}
