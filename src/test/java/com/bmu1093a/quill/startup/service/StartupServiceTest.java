package com.bmu1093a.quill.startup.service;

import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.common.exception.ResourceNotFoundException;
import com.bmu1093a.quill.common.exception.UnauthorizedActionException;
import com.bmu1093a.quill.startup.mapper.StartupMapper;
import com.bmu1093a.quill.startup.model.dto.request.StartupRequestDto;
import com.bmu1093a.quill.startup.model.dto.request.StartupUpdateRequestDto;
import com.bmu1093a.quill.startup.model.dto.respone.StartupResponseDto;
import com.bmu1093a.quill.startup.model.entity.Startup;
import com.bmu1093a.quill.startup.repository.StartupRepository;
import com.bmu1093a.quill.vacancy.service.UserLookupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private User owner;
    private User otherUser;
    private Startup startup;
    private StartupResponseDto startupResponseDto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@test.com");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@test.com");

        startup = new Startup();
        startup.setId(10L);
        startup.setName("Test Startup");
        startup.setOwner(owner);

        startupResponseDto = new StartupResponseDto();
        startupResponseDto.setId(10L);
        startupResponseDto.setName("Test Startup");
    }

    // ── getAllStartups ────────────────────────────────────────────────────────

    @Test
    void getAllStartups_shouldReturnAllStartups() {
        when(startupRepository.findAll()).thenReturn(List.of(startup));
        when(startupMapper.toDto(startup)).thenReturn(startupResponseDto);

        List<StartupResponseDto> result = startupService.getAllStartups();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Startup");
    }

    @Test
    void getAllStartups_shouldReturnEmptyList_whenNoStartups() {
        when(startupRepository.findAll()).thenReturn(List.of());

        List<StartupResponseDto> result = startupService.getAllStartups();

        assertThat(result).isEmpty();
    }

    // ── getStartupById ────────────────────────────────────────────────────────

    @Test
    void getStartupById_shouldReturnDto_withIsOwnerTrue_whenCurrentUserIsOwner() {
        when(startupRepository.findById(10L)).thenReturn(Optional.of(startup));
        when(startupMapper.toDto(startup)).thenReturn(startupResponseDto);
        when(userLookupService.getCurrentUser()).thenReturn(owner);

        StartupResponseDto result = startupService.getStartupById(10L);

        assertThat(result.getIsOwner()).isTrue();
    }

    @Test
    void getStartupById_shouldReturnDto_withIsOwnerFalse_whenCurrentUserIsNotOwner() {
        when(startupRepository.findById(10L)).thenReturn(Optional.of(startup));
        when(startupMapper.toDto(startup)).thenReturn(startupResponseDto);
        when(userLookupService.getCurrentUser()).thenReturn(otherUser);

        StartupResponseDto result = startupService.getStartupById(10L);

        assertThat(result.getIsOwner()).isFalse();
    }

    @Test
    void getStartupById_shouldReturnDto_withIsOwnerFalse_whenNotAuthenticated() {
        when(startupRepository.findById(10L)).thenReturn(Optional.of(startup));
        when(startupMapper.toDto(startup)).thenReturn(startupResponseDto);
        when(userLookupService.getCurrentUser()).thenThrow(new UnauthorizedActionException("Authentication required"));

        StartupResponseDto result = startupService.getStartupById(10L);

        assertThat(result.getIsOwner()).isFalse();
    }

    @Test
    void getStartupById_shouldThrow_whenStartupNotFound() {
        when(startupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> startupService.getStartupById(99L));
    }

    // ── createStartup ─────────────────────────────────────────────────────────

    @Test
    void createStartup_shouldSaveAndReturnDto() {
        StartupRequestDto requestDto = new StartupRequestDto();
        when(startupMapper.toStartup(requestDto)).thenReturn(startup);
        when(userLookupService.getCurrentUser()).thenReturn(owner);
        when(startupRepository.save(startup)).thenReturn(startup);
        when(startupMapper.toDto(startup)).thenReturn(startupResponseDto);

        StartupResponseDto result = startupService.createStartup(requestDto);

        assertThat(result).isNotNull();
        assertThat(startup.getOwner()).isEqualTo(owner);
        verify(startupRepository).save(startup);
    }

    @Test
    void createStartup_shouldThrow_whenNotAuthenticated() {
        StartupRequestDto requestDto = new StartupRequestDto();
        when(startupMapper.toStartup(requestDto)).thenReturn(startup);
        when(userLookupService.getCurrentUser()).thenThrow(new UnauthorizedActionException("Authentication required"));

        assertThrows(UnauthorizedActionException.class,
                () -> startupService.createStartup(requestDto));

        verify(startupRepository, never()).save(any());
    }

    // ── updateStartup ─────────────────────────────────────────────────────────

    @Test
    void updateStartup_shouldUpdate_whenCurrentUserIsOwner() {
        StartupUpdateRequestDto updateDto = new StartupUpdateRequestDto();
        updateDto.setName("Updated Name");
        updateDto.setTagline("New tagline");
        updateDto.setDescription("New desc");
        updateDto.setIsActive(true);

        when(startupRepository.findById(10L)).thenReturn(Optional.of(startup));
        when(userLookupService.getCurrentUser()).thenReturn(owner);
        when(startupRepository.save(startup)).thenReturn(startup);
        when(startupMapper.toDto(startup)).thenReturn(startupResponseDto);

        StartupResponseDto result = startupService.updateStartup(10L, updateDto);

        assertThat(result).isNotNull();
        verify(startupRepository).save(startup);
    }

    @Test
    void updateStartup_shouldThrow_whenCurrentUserIsNotOwner() {
        StartupUpdateRequestDto updateDto = new StartupUpdateRequestDto();

        when(startupRepository.findById(10L)).thenReturn(Optional.of(startup));
        when(userLookupService.getCurrentUser()).thenReturn(otherUser);

        assertThrows(UnauthorizedActionException.class,
                () -> startupService.updateStartup(10L, updateDto));

        verify(startupRepository, never()).save(any());
    }

    @Test
    void updateStartup_shouldThrow_whenStartupNotFound() {
        when(startupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> startupService.updateStartup(99L, new StartupUpdateRequestDto()));
    }

    // ── getMyStartups ─────────────────────────────────────────────────────────

    @Test
    void getMyStartups_shouldReturnCurrentUserStartups() {
        when(userLookupService.getCurrentUser()).thenReturn(owner);
        when(startupRepository.findByOwner(owner)).thenReturn(List.of(startup));
        when(startupMapper.toDto(startup)).thenReturn(startupResponseDto);

        List<StartupResponseDto> result = startupService.getMyStartups();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Startup");
    }

    @Test
    void getMyStartups_shouldThrow_whenNotAuthenticated() {
        when(userLookupService.getCurrentUser()).thenThrow(new UnauthorizedActionException("Authentication required"));

        assertThrows(UnauthorizedActionException.class,
                () -> startupService.getMyStartups());
    }
}