package com.bmu1093a.quill.startup.model.dto.request;

import com.bmu1093a.quill.startup.model.entity.enumeration.StartupCategory;
import com.bmu1093a.quill.startup.model.entity.enumeration.StartupStage;
import lombok.Getter;

@Getter
public class StartupRequestDto {

    private String name;

    private String tagline;

    private String description;

    private StartupCategory category;   // SaaS, FinTech, etc.

    private StartupStage stage;      // idea, pre-seed, seed, etc.

    private String website;


}
