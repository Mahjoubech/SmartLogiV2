package io.github.mahjoubech.smartlogiv2.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ApiResponseError {
    private final int status;
    private final String message;
    private final List<String> errors;
}
