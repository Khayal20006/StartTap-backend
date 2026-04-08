package com.bmu1093a.quill.profile.model.dto;

import lombok.Data;

@Data
public class UpdateProfileRequestDto {
    private String firstName;
    private String lastName;
    private String username;
}
