package com.bmu1093a.quill.file.model.dto;

public record FileUploadResponse(
        String url,
        String publicId,
        String originalFileName
) {
    public FileUploadResponse {
        originalFileName = (originalFileName != null) ? originalFileName : "unknown";
    }
}