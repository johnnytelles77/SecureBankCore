package com.johnny.securebank.exception;

import com.johnny.securebank.dto.ErrorResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void handleUnexpectedException_shouldReturnInternalServerError() {

        Exception exception = new RuntimeException("Database connection failed");

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleUnexpectedException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred.", response.getBody().getError());
    }
}