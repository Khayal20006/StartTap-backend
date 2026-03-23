package com.bmu1093a.quill.file.controller;

import com.bmu1093a.quill.file.model.dto.FileUploadResponse;
import com.bmu1093a.quill.file.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(fileUploadService.uploadFile(file));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<String> deleteFile(@PathVariable String publicId) throws IOException {
        fileUploadService.deleteFile(publicId);
        return ResponseEntity.ok("Fayl uğurla silindi");
    }
}