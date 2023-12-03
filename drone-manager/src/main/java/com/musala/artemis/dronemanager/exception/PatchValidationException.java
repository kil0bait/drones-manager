package com.musala.artemis.dronemanager.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Errors;

@Getter
@RequiredArgsConstructor
public class PatchValidationException extends RuntimeException {
    private final transient Errors errors;
}
