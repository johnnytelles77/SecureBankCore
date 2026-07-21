package com.johnny.securebank.dto;

import java.util.Map;

public class ValidationErrorResponseDTO extends ErrorResponseDTO {

    private Map<String, String> errors;

    public ValidationErrorResponseDTO(
            int status,
            Map<String, String> errors
    ) {
        super(status, "Validation failed");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
