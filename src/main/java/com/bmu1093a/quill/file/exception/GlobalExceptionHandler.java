package com.bmu1093a.quill.file.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.io.IOException;
import com.bmu1093a.quill.file.model.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
}