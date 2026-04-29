package com.bmu1093a.quill.auth.service;

import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.auth.repository.UserRepository;
import com.bmu1093a.quill.common.exception.ResourceNotFoundException;
import com.bmu1093a.quill.common.exception.UnauthorizedActionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLookupServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserLookupServiceImpl userLookupService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    // ✅ 1. Happy path
    @Test
    void shouldReturnUser_whenAuthenticatedAndUserExists() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("realUser");
        when(authentication.getName()).thenReturn("test@mail.com");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = new User();
        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        User result = userLookupService.getCurrentUser();

        assertNotNull(result);
        verify(userRepository).findByEmail("test@mail.com");
    }

    // ❗ 2. No authentication
    @Test
    void shouldThrowUnauthorized_whenAuthenticationIsNull() {
        SecurityContextHolder.clearContext();

        assertThrows(UnauthorizedActionException.class, () -> {
            userLookupService.getCurrentUser();
        });

        verifyNoInteractions(userRepository);
    }

    // ❗ 3. Anonymous user
    @Test
    void shouldThrowUnauthorized_whenUserIsAnonymous() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThrows(UnauthorizedActionException.class, () -> {
            userLookupService.getCurrentUser();
        });

        verifyNoInteractions(userRepository);
    }

    // ❗ 4. User not found in DB
    @Test
    void shouldThrowNotFound_whenUserDoesNotExist() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("realUser");
        when(authentication.getName()).thenReturn("missing@mail.com");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail("missing@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userLookupService.getCurrentUser();
        });

        verify(userRepository).findByEmail("missing@mail.com");
    }
}