package com.levivarga.dicomviewerbackend.mapper;

import com.levivarga.dicomviewerbackend.dto.PatientDto;
import com.levivarga.dicomviewerbackend.dto.PatientSummaryDto;
import com.levivarga.dicomviewerbackend.entity.Patient;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-12T13:21:49+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 24.0.2 (Oracle Corporation)"
)
@Component
public class PatientMapperImpl implements PatientMapper {

    @Autowired
    private StudyMapper studyMapper;

    @Override
    public PatientDto toDto(Patient patient) {
        if ( patient == null ) {
            return null;
        }

        PatientDto.PatientDtoBuilder patientDto = PatientDto.builder();

        patientDto.id( patient.getId() );
        patientDto.patientBirthDate( patient.getPatientBirthDate() );
        patientDto.patientDicomId( patient.getPatientDicomId() );
        patientDto.patientName( patient.getPatientName() );
        patientDto.patientSex( patient.getPatientSex() );
        patientDto.studies( studyMapper.toDtoList( patient.getStudies() ) );

        return patientDto.build();
    }

    @Override
    public List<PatientDto> toDtoList(List<Patient> patients) {
        if ( patients == null ) {
            return null;
        }

        List<PatientDto> list = new ArrayList<PatientDto>( patients.size() );
        for ( Patient patient : patients ) {
            list.add( toDto( patient ) );
        }

        return list;
    }

    @Override
    public PatientSummaryDto toSummaryDto(Patient patient) {
        if ( patient == null ) {
            return null;
        }

        PatientSummaryDto.PatientSummaryDtoBuilder patientSummaryDto = PatientSummaryDto.builder();

        patientSummaryDto.id( patient.getId() );
        patientSummaryDto.patientBirthDate( patient.getPatientBirthDate() );
        patientSummaryDto.patientDicomId( patient.getPatientDicomId() );
        patientSummaryDto.patientName( patient.getPatientName() );
        patientSummaryDto.patientSex( patient.getPatientSex() );

        patientSummaryDto.studyCount( patient.getStudies() != null ? patient.getStudies().size() : 0 );

        return patientSummaryDto.build();
    }

    @Override
    public List<PatientSummaryDto> toSummaryDtoList(List<Patient> patients) {
        if ( patients == null ) {
            return null;
        }

        List<PatientSummaryDto> list = new ArrayList<PatientSummaryDto>( patients.size() );
        for ( Patient patient : patients ) {
            list.add( toSummaryDto( patient ) );
        }

        return list;
    }

    @Override
    public Patient toEntity(PatientDto patientDto) {
        if ( patientDto == null ) {
            return null;
        }

        Patient.PatientBuilder patient = Patient.builder();

        patient.patientBirthDate( patientDto.getPatientBirthDate() );
        patient.patientDicomId( patientDto.getPatientDicomId() );
        patient.patientName( patientDto.getPatientName() );
        patient.patientSex( patientDto.getPatientSex() );

        return patient.build();
    }

    @Override
    public void updateEntityFromDto(PatientDto patientDto, Patient patient) {
        if ( patientDto == null ) {
            return;
        }

        if ( patientDto.getPatientBirthDate() != null ) {
            patient.setPatientBirthDate( patientDto.getPatientBirthDate() );
        }
        if ( patientDto.getPatientDicomId() != null ) {
            patient.setPatientDicomId( patientDto.getPatientDicomId() );
        }
        if ( patientDto.getPatientName() != null ) {
            patient.setPatientName( patientDto.getPatientName() );
        }
        if ( patientDto.getPatientSex() != null ) {
            patient.setPatientSex( patientDto.getPatientSex() );
        }
    }
}
