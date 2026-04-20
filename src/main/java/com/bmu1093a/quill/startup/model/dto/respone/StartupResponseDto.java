package com.bmu1093a.quill.startup.model.dto.respone;

import com.bmu1093a.quill.startup.model.entity.dto.response.OwnerResponseDto;
import com.bmu1093a.quill.startup.model.entity.enumeration.StartupCategory;
import com.bmu1093a.quill.startup.model.entity.enumeration.StartupStage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StartupResponseDto {

    private Long id;

    private String name;

    private String tagline;

    private String description;

    private StartupCategory category;

    private StartupStage stage;

    private String website;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private OwnerResponseDto owner;


}
