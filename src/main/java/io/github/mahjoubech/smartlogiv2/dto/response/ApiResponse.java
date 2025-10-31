package io.github.mahjoubech.smartlogiv2.dto.response;

import lombok.Data;

@Data
public class ApiResponse {
    private final int status;
    private final String message;
}
