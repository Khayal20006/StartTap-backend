package com.bmu1093a.quill.file.service;

import com.bmu1093a.quill.file.exception.FileOperationException;
import com.bmu1093a.quill.file.exception.FileValidationException;
import com.bmu1093a.quill.file.model.dto.FileUploadResponse;
import com.bmu1093a.quill.file.model.entity.FileRecord;
import com.bmu1093a.quill.file.repo.FileRecordRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryFileUploadService implements FileUploadService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryFileUploadService.class);

    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/x-tika-ooxml",
            "application/x-tika-msoffice",
            "application/zip"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private final Cloudinary cloudinary;
    private final Tika tika;
    private final FileRecordRepository fileRecordRepository;

    public CloudinaryFileUploadService(Cloudinary cloudinary, FileRecordRepository fileRecordRepository) {
        this.cloudinary = cloudinary;
        this.tika = new Tika();
        this.fileRecordRepository = fileRecordRepository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public FileUploadResponse uploadFile(MultipartFile file) throws IOException {
        validate(file);

        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            throw new FileValidationException("INVALID_FILE_NAME", "File name cannot be null");
        }

        String extension = "";
        int lastIndex = originalName.lastIndexOf(".");
        if (lastIndex != -1) {
            extension = originalName.substring(lastIndex);
        }

        String publicIdWithExtension = "file_" + UUID.randomUUID().toString().substring(0, 8) + extension;

        log.info("Uploading file: '{}'", originalName);

        Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "quill/cv",
                        "resource_type", "raw",
                        "public_id", publicIdWithExtension,
                        "use_filename", true,
                        "unique_filename", false
                )
        );

        String secureUrl = String.valueOf(uploadResult.get("secure_url"));
        String publicId = String.valueOf(uploadResult.get("public_id"));

        FileRecord record = FileRecord.builder()
                .url(secureUrl)
                .publicId(publicId)
                .originalFileName(originalName)
                .deleted(false)
                .build();

        fileRecordRepository.save(record);

        return new FileUploadResponse(secureUrl, publicId, originalName);
    }

    @Override
    public void deleteFile(String publicId) {
        FileRecord record = fileRecordRepository
                .findByPublicIdAndDeletedFalse(publicId)
                .orElseThrow(() -> new FileOperationException("FILE_NOT_FOUND", "File not found: " + publicId));

        record.setDeleted(true);
        fileRecordRepository.save(record);
    }

    private void validate(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new FileValidationException("FILE_EMPTY", "File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileValidationException("FILE_TOO_LARGE", "File size exceeds 5MB limit");
        }

        String detectedType = tika.detect(file.getInputStream());
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";

        boolean hasValidExtension = fileName.endsWith(".pdf") || fileName.endsWith(".docx");
        boolean hasValidMime = ALLOWED_MIME_TYPES.contains(detectedType);

        if (!hasValidMime && !hasValidExtension) {
            throw new FileValidationException("INVALID_FILE_TYPE", "Only PDF and DOCX files are accepted");
        }
    }
}