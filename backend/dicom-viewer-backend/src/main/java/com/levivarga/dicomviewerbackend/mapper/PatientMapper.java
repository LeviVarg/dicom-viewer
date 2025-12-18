package com.levivarga.dicomviewerbackend.mapper;

import com.levivarga.dicomviewerbackend.dto.PatientDto;
import com.levivarga.dicomviewerbackend.dto.PatientSummaryDto;
import com.levivarga.dicomviewerbackend.entity.Patient;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {StudyMapper.class})
public interface PatientMapper {

    PatientDto toDto(Patient patient);

    List<PatientDto> toDtoList(List<Patient> patients);

    @Mapping(target = "studyCount", expression = "java(patient.getStudies() != null ? patient.getStudies().size() : 0)")
    PatientSummaryDto toSummaryDto(Patient patient);

    List<PatientSummaryDto> toSummaryDtoList(List<Patient> patients);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studies", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Patient toEntity(PatientDto patientDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studies", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(PatientDto patientDto, @MappingTarget Patient patient);
}
