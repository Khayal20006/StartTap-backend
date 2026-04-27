package com.bmu1093a.quill.common.exception;

import lombok.Getter;

@Getter
public class FileOperationException extends RuntimeException {

    private final String code;

    public FileOperationException(String code, String message) {
        super(message);
        this.code = code;
    }

}