package com.bmu1093a.quill.job.mapper;

import com.bmu1093a.quill.job.model.dto.response.JobResponseDto;
import com.bmu1093a.quill.job.model.entity.Job;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface JobMapper {

    JobMapper INSTANCE = Mappers.getMapper(JobMapper.class);

    @Mapping(source = "employer" ,target = "employer")
    JobResponseDto toJobResponseDto(Job job);
}
