package com.musala.artemis.dronemanager.rest.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@RequiredArgsConstructor
@Getter
public class RestErrorResponse {
    @NotBlank
    @Schema(description = "Error timestamp")
    private final Date timestamp;
    @NotBlank
    @Schema(description = "HTTP status code")
    private final int status;
    @NotBlank
    @Schema(description = "Error title")
    private final String error;
    @NotBlank
    @Schema(description = "Detailed error message")
    private final String message;
}
