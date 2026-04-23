package com.bmu1093a.quill.profile.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileRequestDto {

    @Schema(example = "Khayal")
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^\\p{L}+(?:[-']\\p{L}+)*$", message = "First name contains invalid characters")
    private String firstName;

    @Schema(example = "Sharifov")
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^\\p{L}+(?:[-']\\p{L}+)*$", message = "Last name contains invalid characters")
    private String lastName;

    @Schema(example = "khayal123")
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    @Schema(example = "+994501234567")
    @Pattern(regexp = "^(\\+994|0)(50|51|55|70|77|99|10)[0-9]{7}$",
            message = "Telefon nömrəsi düzgün formatda deyil (məs: +994501234567)")
    private String phoneNumber;

    @Schema(example = "https://linkedin.com/in/khayal")
    @Pattern(regexp = "^https?://[\\w\\-]+(\\.[\\w\\-]+)+(/.*)?$",
            message = "LinkedIn linki düzgün URL formatında olmalıdır")
    private String linkedinUrl;

    @Schema(example = "https://github.com/khayal")
    @Pattern(regexp = "^https?://[\\w\\-]+(\\.[\\w\\-]+)+(/.*)?$",
            message = "Github linki düzgün URL formatında olmalıdır")
    private String githubUrl;
}