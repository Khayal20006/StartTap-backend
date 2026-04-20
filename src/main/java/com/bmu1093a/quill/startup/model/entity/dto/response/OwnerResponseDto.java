package com.bmu1093a.quill.startup.model.entity.dto.response;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class OwnerResponseDto {

    private Long id;

    private String firstname;

    private String lastname;

    private String email;

}
