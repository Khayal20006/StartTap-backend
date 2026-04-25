package com.bmu1093a.quill.profile.model.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ProfileResponseDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String cvUrl;
    private String cvFileName;
    private String phoneNumber;
    private String linkedinUrl;
    private String githubUrl;
}
