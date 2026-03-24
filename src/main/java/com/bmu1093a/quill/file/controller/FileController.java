package com.bmu1093a.quill.file.controller;

import com.bmu1093a.quill.file.model.dto.FileUploadResponse;
import com.bmu1093a.quill.file.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "File", description = "CV file upload and delete operations")
public class FileController {

    private final FileUploadService fileUploadService;

    @Operation(summary = "Upload CV", description = "Uploads a PDF file to Cloudinary and saves record to DB")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file type or size"),
            @ApiResponse(responseCode = "500", description = "Upload failed")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(fileUploadService.uploadFile(file));
    }

    @Operation(summary = "Delete CV", description = "Soft deletes the file record in DB")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File deleted successfully"),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    @DeleteMapping
    public ResponseEntity<String> deleteFile(
            @RequestParam String publicId
    ) {
        fileUploadService.deleteFile(publicId);
        return ResponseEntity.ok("File deleted successfully");
    }
}