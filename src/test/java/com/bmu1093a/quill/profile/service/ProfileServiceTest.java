package com.bmu1093a.quill.profile.service;

import com.bmu1093a.quill.common.exception.UserNotFoundException;
import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.auth.repository.UserRepository;
import com.bmu1093a.quill.common.exception.FileOperationException;
import com.bmu1093a.quill.file.model.entity.FileRecord;
import com.bmu1093a.quill.file.repo.FileRecordRepository;
import com.bmu1093a.quill.profile.model.dto.ProfileRequestDto;
import com.bmu1093a.quill.profile.model.dto.ProfileResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileRecordRepository fileRecordRepository;

    @InjectMocks
    private ProfileService profileService;

    private static final String EMAIL = "test@mail.com";

    private User user;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        EMAIL,
                        null,
                        java.util.List.of()
                )
        );

        user = User.builder()
                .id(1L)
                .email(EMAIL)
                .username("test")
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    // ---------------- HELPERS ----------------

    private FileRecord sampleCv() {
        return FileRecord.builder()
                .url("http://cv.com/file.pdf")
                .originalFileName("cv.pdf")
                .build();
    }

    // ---------------- GET PROFILE ----------------

    @Test
    void getProfile_success() {
        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(fileRecordRepository.findFirstByUserEmailAndDeletedFalseOrderByIdDesc(EMAIL))
                .thenReturn(Optional.of(sampleCv()));

        ProfileResponseDto result = profileService.getProfile();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(user.getEmail(), result.getEmail()),
                () -> assertEquals("cv.pdf", result.getCvFileName()),
                () -> assertEquals("http://cv.com/file.pdf", result.getCvUrl())
        );
    }

    // ---------------- UPDATE PROFILE ----------------

    @Test
    void updateProfile_success() {
        ProfileRequestDto request = new ProfileRequestDto();
        request.setFirstName("NewName");
        request.setLastName("NewLast");

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(fileRecordRepository.findFirstByUserEmailAndDeletedFalseOrderByIdDesc(EMAIL))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProfileResponseDto result = profileService.updateProfile(request);

        assertEquals("NewName", result.getFirstName());
        assertEquals("NewLast", result.getLastName());

        verify(userRepository, times(1)).save(any(User.class));
    }

    // ---------------- GET BY ID ----------------

    @Test
    void getProfileById_success() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(fileRecordRepository.findFirstByUserEmailAndDeletedFalseOrderByIdDesc(EMAIL))
                .thenReturn(Optional.of(sampleCv()));

        ProfileResponseDto result = profileService.getProfileById(1L);

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals("cv.pdf", result.getCvFileName());
    }

    // ---------------- EXCEPTION TESTS ----------------

    @Test
    void getProfile_shouldThrow_whenUserNotAuthenticated() {
        SecurityContextHolder.clearContext();

        assertThrows(FileOperationException.class,
                () -> profileService.getProfile());
    }

    @Test
    void getProfile_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> profileService.getProfile());
    }
}