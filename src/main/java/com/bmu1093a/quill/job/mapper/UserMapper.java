package com.bmu1093a.quill.job.mapper;

import com.bmu1093a.quill.auth.model.entity.User;
import com.bmu1093a.quill.job.model.dto.response.EmployeeResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    EmployeeResponseDto toEmployeeResponseDto(User user);
}
