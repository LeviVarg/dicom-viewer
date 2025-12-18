package com.levivarga.dicomviewerbackend.mapper;

import com.levivarga.dicomviewerbackend.dto.StudyDto;
import com.levivarga.dicomviewerbackend.dto.StudySummaryDto;
import com.levivarga.dicomviewerbackend.entity.Study;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SeriesMapper.class})
public interface StudyMapper {

    StudyDto toDto(Study study);

    List<StudyDto> toDtoList(List<Study> studies);

    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "seriesCount", expression = "java(study.getSeriesList() != null ? study.getSeriesList().size() : 0)")
    StudySummaryDto toSummaryDto(Study study);

    List<StudySummaryDto> toSummaryDtoList(List<Study> studies);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "seriesList", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Study toEntity(StudyDto studyDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "seriesList", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(StudyDto studyDto, @MappingTarget Study study);
}
