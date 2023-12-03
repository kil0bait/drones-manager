package com.musala.artemis.dronemanager.rest.annotation;

import com.musala.artemis.dronemanager.rest.model.RestErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "400", description = "Bad request",
                content = @Content(schema = @Schema(implementation = RestErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Conflict",
                content = @Content(schema = @Schema(implementation = RestErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = RestErrorResponse.class)))})
public @interface CreatedApiResponses {
}
