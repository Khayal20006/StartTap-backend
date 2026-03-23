package com.bmu1093a.quill.file.service;

import com.bmu1093a.quill.file.exception.FileValidationException;
import com.bmu1093a.quill.file.model.dto.FileUploadResponse;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryFileUploadService implements FileUploadService {

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private final Cloudinary cloudinary;

    public CloudinaryFileUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public FileUploadResponse uploadFile(MultipartFile file) throws IOException {
        validate(file);

        Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "quill/cv",
                        "resource_type", "auto",
                        "use_filename", true,
                        "unique_filename", true
                )
        );

        return new FileUploadResponse(
                uploadResult.get("secure_url").toString(),
                uploadResult.get("public_id").toString(),
                file.getOriginalFilename()
        );
    }

    @Override
    public void deleteFile(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileValidationException("Fayl boşdur");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new FileValidationException("Yalnız PDF və DOCX fayllar qəbul edilir");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileValidationException("Fayl həcmi 5MB-dan çox ola bilməz");
        }
    }
}