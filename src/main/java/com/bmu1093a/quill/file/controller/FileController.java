package com.bmu1093a.quill.file.controller;

import com.bmu1093a.quill.file.exception.FileValidationException;
import com.bmu1093a.quill.file.model.dto.FileUploadResponse;
import com.bmu1093a.quill.file.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
             FileUploadResponse response = fileUploadService.uploadFile(file);
            return ResponseEntity.ok(response);
        } catch (FileValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Fayl yüklənərkən xəta baş verdi");
        }
    }
}