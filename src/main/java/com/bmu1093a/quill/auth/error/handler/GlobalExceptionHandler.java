package com.bmu1093a.quill.auth.error.handler;

import com.bmu1093a.quill.auth.error.UserNotFoundException;
import com.bmu1093a.quill.file.exception.FileOperationException;
import com.bmu1093a.quill.file.exception.FileValidationException;
import com.bmu1093a.quill.file.model.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException e, WebRequest request) {
        return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<ErrorResponse> handleFileValidation(FileValidationException e) {
        log.warn("File validation failed [{}]: {}", e.getCode(), e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(FileOperationException.class)
    public ResponseEntity<ErrorResponse> handleFileOperation(FileOperationException e) {
        log.error("File operation failed [{}]: {}", e.getCode(), e.getMessage());
        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIO(IOException e) {
        log.error("Unexpected IO error", e);
        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponse("IO_ERROR", "File operation failed"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponse("INTERNAL_ERROR", "Unexpected error occurred"));
    }


    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", status.value());

        return new ResponseEntity<>(response, status);
    }

}
