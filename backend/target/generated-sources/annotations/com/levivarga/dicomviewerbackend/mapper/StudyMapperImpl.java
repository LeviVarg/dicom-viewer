package com.levivarga.dicomviewerbackend.mapper;

import com.levivarga.dicomviewerbackend.dto.StudyDto;
import com.levivarga.dicomviewerbackend.dto.StudySummaryDto;
import com.levivarga.dicomviewerbackend.entity.Patient;
import com.levivarga.dicomviewerbackend.entity.Study;
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
public class StudyMapperImpl implements StudyMapper {

    @Autowired
    private SeriesMapper seriesMapper;

    @Override
    public StudyDto toDto(Study study) {
        if ( study == null ) {
            return null;
        }

        StudyDto.StudyDtoBuilder studyDto = StudyDto.builder();

        studyDto.accessionNumber( study.getAccessionNumber() );
        studyDto.id( study.getId() );
        studyDto.seriesList( seriesMapper.toDtoList( study.getSeriesList() ) );
        studyDto.studyDate( study.getStudyDate() );
        studyDto.studyDescription( study.getStudyDescription() );
        studyDto.studyId( study.getStudyId() );
        studyDto.studyInstanceUid( study.getStudyInstanceUid() );

        return studyDto.build();
    }

    @Override
    public List<StudyDto> toDtoList(List<Study> studies) {
        if ( studies == null ) {
            return null;
        }

        List<StudyDto> list = new ArrayList<StudyDto>( studies.size() );
        for ( Study study : studies ) {
            list.add( toDto( study ) );
        }

        return list;
    }

    @Override
    public StudySummaryDto toSummaryDto(Study study) {
        if ( study == null ) {
            return null;
        }

        StudySummaryDto.StudySummaryDtoBuilder studySummaryDto = StudySummaryDto.builder();

        studySummaryDto.patientId( studyPatientId( study ) );
        studySummaryDto.accessionNumber( study.getAccessionNumber() );
        studySummaryDto.id( study.getId() );
        studySummaryDto.studyDate( study.getStudyDate() );
        studySummaryDto.studyDescription( study.getStudyDescription() );
        studySummaryDto.studyId( study.getStudyId() );
        studySummaryDto.studyInstanceUid( study.getStudyInstanceUid() );

        studySummaryDto.seriesCount( study.getSeriesList() != null ? study.getSeriesList().size() : 0 );

        return studySummaryDto.build();
    }

    @Override
    public List<StudySummaryDto> toSummaryDtoList(List<Study> studies) {
        if ( studies == null ) {
            return null;
        }

        List<StudySummaryDto> list = new ArrayList<StudySummaryDto>( studies.size() );
        for ( Study study : studies ) {
            list.add( toSummaryDto( study ) );
        }

        return list;
    }

    @Override
    public Study toEntity(StudyDto studyDto) {
        if ( studyDto == null ) {
            return null;
        }

        Study.StudyBuilder study = Study.builder();

        study.accessionNumber( studyDto.getAccessionNumber() );
        study.studyDate( studyDto.getStudyDate() );
        study.studyDescription( studyDto.getStudyDescription() );
        study.studyId( studyDto.getStudyId() );
        study.studyInstanceUid( studyDto.getStudyInstanceUid() );

        return study.build();
    }

    @Override
    public void updateEntityFromDto(StudyDto studyDto, Study study) {
        if ( studyDto == null ) {
            return;
        }

        if ( studyDto.getAccessionNumber() != null ) {
            study.setAccessionNumber( studyDto.getAccessionNumber() );
        }
        if ( studyDto.getStudyDate() != null ) {
            study.setStudyDate( studyDto.getStudyDate() );
        }
        if ( studyDto.getStudyDescription() != null ) {
            study.setStudyDescription( studyDto.getStudyDescription() );
        }
        if ( studyDto.getStudyId() != null ) {
            study.setStudyId( studyDto.getStudyId() );
        }
        if ( studyDto.getStudyInstanceUid() != null ) {
            study.setStudyInstanceUid( studyDto.getStudyInstanceUid() );
        }
    }

    private Long studyPatientId(Study study) {
        Patient patient = study.getPatient();
        if ( patient == null ) {
            return null;
        }
        return patient.getId();
    }
}
