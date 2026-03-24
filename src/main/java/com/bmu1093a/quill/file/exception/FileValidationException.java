package com.bmu1093a.quill.file.exception;

import lombok.Getter;

@Getter
public class FileValidationException extends RuntimeException {

    private final String code;

    public FileValidationException(String code, String message) {
        super(message);
        this.code = code;
    }

}