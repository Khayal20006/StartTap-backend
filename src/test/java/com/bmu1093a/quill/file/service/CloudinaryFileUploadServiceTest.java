package com.bmu1093a.quill.file.service;

import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.auth.repository.UserRepository;
import com.bmu1093a.quill.file.exception.FileOperationException;
import com.bmu1093a.quill.file.exception.FileValidationException;
import com.bmu1093a.quill.file.model.dto.FileUploadResponse;
import com.bmu1093a.quill.file.model.entity.FileRecord;
import com.bmu1093a.quill.file.repo.FileRecordRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryFileUploadServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private FileRecordRepository fileRecordRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private CloudinaryFileUploadService service;

    private static final String EMAIL = "test@mail.com";

    private User user;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        // 🔥 FIX: Proper Spring Security authentication (IMPORTANT)
        Authentication auth = new UsernamePasswordAuthenticationToken(
                EMAIL,
                null,
                List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        user = User.builder()
                .id(1L)
                .email(EMAIL)
                .username("test")
                .build();
    }

    // ---------------- HELPERS ----------------

    private void mockCloudinary() {
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    private MockMultipartFile validFile() {
        return new MockMultipartFile(
                "file",
                "cv.pdf",
                "application/pdf",
                "dummy content".getBytes()
        );
    }

    // ---------------- UPLOAD TESTS ----------------

    @Test
    void uploadFile_success() throws Exception {
        mockCloudinary();

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(Map.of(
                        "secure_url", "http://cloudinary.com/file.pdf",
                        "public_id", "cv_123"
                ));

        when(fileRecordRepository.save(any(FileRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FileUploadResponse response = service.uploadFile(validFile());

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals("http://cloudinary.com/file.pdf", response.url()),
                () -> assertEquals("cv_123", response.publicId())
        );

        verify(fileRecordRepository, times(1)).save(any(FileRecord.class));
    }

    @Test
    void uploadFile_shouldThrow_whenEmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "",
                "application/pdf",
                new byte[0]
        );

        assertThrows(FileValidationException.class,
                () -> service.uploadFile(file));

        verifyNoInteractions(userRepository);
    }

    @Test
    void uploadFile_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.empty());

        assertThrows(FileOperationException.class,
                () -> service.uploadFile(validFile()));
    }

    // ---------------- DELETE TESTS ----------------

    @Test
    void deleteFile_success() {
        mockCloudinary();

        FileRecord fileRecord = FileRecord.builder()
                .publicId("cv_123")
                .deleted(false)
                .user(user)
                .build();

        when(fileRecordRepository.findByPublicIdAndDeletedFalse("cv_123"))
                .thenReturn(Optional.of(fileRecord));

        when(fileRecordRepository.save(any(FileRecord.class)))
                .thenReturn(fileRecord);

        assertDoesNotThrow(() -> service.deleteFile("cv_123"));

        verify(fileRecordRepository, times(2)).save(any(FileRecord.class));
    }

    @Test
    void deleteFile_shouldThrow_whenNotOwner() {
        User otherUser = User.builder()
                .email("other@mail.com")
                .build();

        FileRecord fileRecord = FileRecord.builder()
                .publicId("cv_123")
                .deleted(false)
                .user(otherUser)
                .build();

        when(fileRecordRepository.findByPublicIdAndDeletedFalse("cv_123"))
                .thenReturn(Optional.of(fileRecord));

        assertThrows(FileOperationException.class,
                () -> service.deleteFile("cv_123"));
    }
}