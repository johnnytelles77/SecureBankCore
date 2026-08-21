package com.johnny.securebank.exception;

import com.johnny.securebank.dto.ErrorResponseDTO;
import com.johnny.securebank.dto.ValidationErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.Map;
import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFoundException(UserNotFoundException e) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );
   }

   private ResponseEntity<ErrorResponseDTO> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                status.value(),
                message
        );
        return ResponseEntity.status(status).body(errorResponseDTO);
   }

   @ExceptionHandler(AccountNotFoundException.class)
   public ResponseEntity<ErrorResponseDTO> handleAccountNotFoundException(AccountNotFoundException e) {
       return buildErrorResponse(
               HttpStatus.NOT_FOUND,
               e.getMessage()
       );
   }

   @ExceptionHandler(DuplicateEmailException.class)
   public ResponseEntity<ErrorResponseDTO> handleDuplicateEmailException(DuplicateEmailException e) {
       return buildErrorResponse(
               HttpStatus.CONFLICT,
               e.getMessage()
       );
   }

   @ExceptionHandler(DuplicateAccountException.class)
   public ResponseEntity<ErrorResponseDTO> handleDuplicateAccountException(DuplicateAccountException e) {
       return buildErrorResponse(
               HttpStatus.CONFLICT,
               e.getMessage()
       );
   }

   @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(MethodArgumentNotValidException e)
   {
       Map<String, String> errors = new HashMap<>();
       for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
           errors.put(
                   fieldError.getField(),
                   fieldError.getDefaultMessage());
       }
       ValidationErrorResponseDTO response = new ValidationErrorResponseDTO(
               HttpStatus.BAD_REQUEST.value(),
               errors
       );
       return ResponseEntity
               .status(HttpStatus.BAD_REQUEST)
               .body(response);
   }
}