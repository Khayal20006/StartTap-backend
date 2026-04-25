package com.bmu1093a.quill.startup.service;

import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.startup.mapper.StartupMapper;
import com.bmu1093a.quill.startup.model.dto.request.StartupRequestDto;
import com.bmu1093a.quill.startup.model.dto.request.StartupUpdateRequestDto;
import com.bmu1093a.quill.startup.model.dto.respone.StartupResponseDto;
import com.bmu1093a.quill.startup.model.entity.Startup;
import com.bmu1093a.quill.startup.repository.StartupRepository;
import com.bmu1093a.quill.vacancy.service.UserLookupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartupServiceTest {

    @Mock
    private StartupRepository startupRepository;

    @Mock
    private UserLookupService userLookupService;

    @Mock
    private StartupMapper startupMapper;

    @InjectMocks
    private StartupService startupService;

    private User user(Long id) {
        return User.builder().id(id).build();
    }

    private Startup startup(Long id, User owner) {
        Startup s = new Startup();
        s.setId(id);
        s.setOwner(owner);
        return s;
    }

    private StartupResponseDto dto() {
        return new StartupResponseDto();
    }

    // ---------------- GET ALL ----------------

    @Test
    void getAllStartups_success() {
        Startup s1 = new Startup();
        Startup s2 = new Startup();

        when(startupRepository.findAll())
                .thenReturn(List.of(s1, s2));

        when(startupMapper.toDto(any()))
                .thenReturn(new StartupResponseDto());

        List<StartupResponseDto> result = startupService.getAllStartups();

        assertEquals(2, result.size());
        verify(startupRepository).findAll();
    }

    // ---------------- GET BY ID ----------------

    @Test
    void getStartupById_owner_true() {
        User owner = user(1L);
        Startup startup = startup(10L, owner);

        when(startupRepository.findById(10L))
                .thenReturn(Optional.of(startup));

        when(startupMapper.toDto(startup))
                .thenReturn(dto());

        when(userLookupService.getCurrentUser())
                .thenReturn(owner);

        StartupResponseDto result = startupService.getStartupById(10L);

        assertNotNull(result);
        assertTrue(result.getIsOwner());
    }

    @Test
    void getStartupById_owner_false() {
        User owner = user(1L);
        User other = user(2L);

        Startup startup = startup(10L, owner);

        when(startupRepository.findById(10L))
                .thenReturn(Optional.of(startup));

        when(startupMapper.toDto(startup))
                .thenReturn(dto());

        when(userLookupService.getCurrentUser())
                .thenReturn(other);

        StartupResponseDto result = startupService.getStartupById(10L);

        assertFalse(result.getIsOwner());
    }

    @Test
    void getStartupById_shouldThrow_whenNotFound() {
        when(startupRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> startupService.getStartupById(10L));
    }

    // ---------------- CREATE ----------------

    @Test
    void createStartup_success() {
        StartupRequestDto request = new StartupRequestDto();

        Startup startup = new Startup();

        when(startupMapper.toStartup(request))
                .thenReturn(startup);

        when(userLookupService.getCurrentUser())
                .thenReturn(user(1L));

        when(startupRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(startupMapper.toDto(any()))
                .thenReturn(new StartupResponseDto());

        StartupResponseDto result = startupService.createStartup(request);

        assertNotNull(result);
        verify(startupRepository).save(any(Startup.class));
    }

    // ---------------- UPDATE ----------------

    @Test
    void updateStartup_success() {
        Startup existing = new Startup();

        StartupUpdateRequestDto request = new StartupUpdateRequestDto();
        request.setName("New Name");

        when(startupRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(startupRepository.save(existing))
                .thenReturn(existing);

        when(startupMapper.toDto(existing))
                .thenReturn(new StartupResponseDto());

        StartupResponseDto result = startupService.updateStartup(1L, request);

        assertNotNull(result);
        assertEquals("New Name", existing.getName());
    }

    // ---------------- MY STARTUPS ----------------

    @Test
    void getMyStartups_success() {
        User user = user(1L);

        Startup s = startup(1L, user);

        when(userLookupService.getCurrentUser())
                .thenReturn(user);

        when(startupRepository.findByOwner(user))
                .thenReturn(List.of(s));

        when(startupMapper.toDto(any()))
                .thenReturn(new StartupResponseDto());

        List<StartupResponseDto> result = startupService.getMyStartups();

        assertEquals(1, result.size());
    }
}