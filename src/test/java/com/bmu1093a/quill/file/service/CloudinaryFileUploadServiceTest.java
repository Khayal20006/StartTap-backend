package com.bmu1093a.quill.file.service;

import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.auth.repository.UserRepository;
import com.bmu1093a.quill.common.exception.FileOperationException;
import com.bmu1093a.quill.common.exception.FileValidationException;
import com.bmu1093a.quill.file.model.entity.FileRecord;
import com.bmu1093a.quill.file.repo.FileRecordRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CloudinaryFileUploadServiceTest {

    @Mock
    Cloudinary cloudinary;

    @Mock
    FileRecordRepository fileRecordRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    Uploader uploader;

    @InjectMocks
    CloudinaryFileUploadService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(cloudinary.uploader()).thenReturn(uploader);

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("user");
        when(auth.getName()).thenReturn("test@mail.com");

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ---------------- uploadFile ----------------

    @Test
    void uploadFile_success() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "content".getBytes()
        );

        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        Map<String, Object> cloudinaryResult = Map.of(
                "secure_url", "http://url",
                "public_id", "id123"
        );

        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(cloudinaryResult);

        when(fileRecordRepository.save(any(FileRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.uploadFile(file);

        assertNotNull(result);
        assertEquals("http://url", result.url());
    }

    @Test
    void uploadFile_emptyFile_shouldThrow() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                new byte[0]
        );

        assertThrows(FileValidationException.class,
                () -> service.uploadFile(file));
    }

    @Test
    void uploadFile_invalidType_shouldThrow() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.exe",
                "application/octet-stream",
                "bad".getBytes()
        );

        assertThrows(FileValidationException.class,
                () -> service.uploadFile(file));
    }

    @Test
    void uploadFile_userNotFound_shouldThrow() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "content".getBytes()
        );

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(FileOperationException.class,
                () -> service.uploadFile(file));
    }

    @Test
    void uploadFile_cloudinaryFails_shouldThrow() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "content".getBytes()
        );

        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        when(uploader.upload(any(), anyMap()))
                .thenThrow(new RuntimeException("fail"));

        assertThrows(FileOperationException.class,
                () -> service.uploadFile(file));
    }

    // ---------------- deleteFile ----------------

    @Test
    void deleteFile_success() {
        User user = new User();
        user.setEmail("test@mail.com");

        FileRecord fileRecord = new FileRecord();
        fileRecord.setPublicId("id123");
        fileRecord.setUser(user);

        when(fileRecordRepository.findByPublicIdAndDeletedFalse("id123"))
                .thenReturn(Optional.of(fileRecord));

        assertDoesNotThrow(() -> service.deleteFile("id123"));
    }

    @Test
    void deleteFile_accessDenied() {
        User owner = new User();
        owner.setEmail("other@mail.com");

        FileRecord fileRecord = new FileRecord();
        fileRecord.setUser(owner);

        when(fileRecordRepository.findByPublicIdAndDeletedFalse("id123"))
                .thenReturn(Optional.of(fileRecord));

        assertThrows(FileOperationException.class,
                () -> service.deleteFile("id123"));
    }

    @Test
    void deleteFile_notFound() {
        when(fileRecordRepository.findByPublicIdAndDeletedFalse(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(FileOperationException.class,
                () -> service.deleteFile("id123"));
    }

    // ---------------- getLastUploadedCv ----------------

    @Test
    void getLastUploadedCv_success() {
        FileRecord fileRecord = new FileRecord();
        fileRecord.setUrl("url");
        fileRecord.setPublicId("id");
        fileRecord.setOriginalFileName("cv.pdf");

        when(fileRecordRepository.findFirstByUserEmailAndDeletedFalseOrderByIdDesc(anyString()))
                .thenReturn(Optional.of(fileRecord));

        var result = service.getLastUploadedCv();

        assertEquals("url", result.url());
    }

    @Test
    void getLastUploadedCv_notFound() {
        when(fileRecordRepository.findFirstByUserEmailAndDeletedFalseOrderByIdDesc(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(FileOperationException.class,
                () -> service.getLastUploadedCv());
    }
}