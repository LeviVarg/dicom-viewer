package com.levivarga.dicomviewerbackend.mapper;

import com.levivarga.dicomviewerbackend.dto.SeriesDto;
import com.levivarga.dicomviewerbackend.dto.SeriesSummaryDto;
import com.levivarga.dicomviewerbackend.entity.Series;
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
public class SeriesMapperImpl implements SeriesMapper {

    @Autowired
    private InstanceMapper instanceMapper;

    @Override
    public SeriesDto toDto(Series series) {
        if ( series == null ) {
            return null;
        }

        SeriesDto.SeriesDtoBuilder seriesDto = SeriesDto.builder();

        seriesDto.bodyPartExamined( series.getBodyPartExamined() );
        seriesDto.id( series.getId() );
        seriesDto.instances( instanceMapper.toDtoList( series.getInstances() ) );
        seriesDto.modality( series.getModality() );
        seriesDto.protocolName( series.getProtocolName() );
        seriesDto.seriesDescription( series.getSeriesDescription() );
        seriesDto.seriesInstanceUid( series.getSeriesInstanceUid() );
        seriesDto.seriesNumber( series.getSeriesNumber() );

        return seriesDto.build();
    }

    @Override
    public List<SeriesDto> toDtoList(List<Series> seriesList) {
        if ( seriesList == null ) {
            return null;
        }

        List<SeriesDto> list = new ArrayList<SeriesDto>( seriesList.size() );
        for ( Series series : seriesList ) {
            list.add( toDto( series ) );
        }

        return list;
    }

    @Override
    public SeriesSummaryDto toSummaryDto(Series series) {
        if ( series == null ) {
            return null;
        }

        SeriesSummaryDto.SeriesSummaryDtoBuilder seriesSummaryDto = SeriesSummaryDto.builder();

        seriesSummaryDto.studyId( seriesStudyId( series ) );
        seriesSummaryDto.bodyPartExamined( series.getBodyPartExamined() );
        seriesSummaryDto.id( series.getId() );
        seriesSummaryDto.modality( series.getModality() );
        seriesSummaryDto.protocolName( series.getProtocolName() );
        seriesSummaryDto.seriesDescription( series.getSeriesDescription() );
        seriesSummaryDto.seriesInstanceUid( series.getSeriesInstanceUid() );
        seriesSummaryDto.seriesNumber( series.getSeriesNumber() );

        seriesSummaryDto.instanceCount( series.getInstances() != null ? series.getInstances().size() : 0 );

        return seriesSummaryDto.build();
    }

    @Override
    public List<SeriesSummaryDto> toSummaryDtoList(List<Series> seriesList) {
        if ( seriesList == null ) {
            return null;
        }

        List<SeriesSummaryDto> list = new ArrayList<SeriesSummaryDto>( seriesList.size() );
        for ( Series series : seriesList ) {
            list.add( toSummaryDto( series ) );
        }

        return list;
    }

    @Override
    public Series toEntity(SeriesDto seriesDto) {
        if ( seriesDto == null ) {
            return null;
        }

        Series.SeriesBuilder series = Series.builder();

        series.bodyPartExamined( seriesDto.getBodyPartExamined() );
        series.modality( seriesDto.getModality() );
        series.protocolName( seriesDto.getProtocolName() );
        series.seriesDescription( seriesDto.getSeriesDescription() );
        series.seriesInstanceUid( seriesDto.getSeriesInstanceUid() );
        series.seriesNumber( seriesDto.getSeriesNumber() );

        return series.build();
    }

    @Override
    public void updateEntityFromDto(SeriesDto seriesDto, Series series) {
        if ( seriesDto == null ) {
            return;
        }

        if ( seriesDto.getBodyPartExamined() != null ) {
            series.setBodyPartExamined( seriesDto.getBodyPartExamined() );
        }
        if ( seriesDto.getModality() != null ) {
            series.setModality( seriesDto.getModality() );
        }
        if ( seriesDto.getProtocolName() != null ) {
            series.setProtocolName( seriesDto.getProtocolName() );
        }
        if ( seriesDto.getSeriesDescription() != null ) {
            series.setSeriesDescription( seriesDto.getSeriesDescription() );
        }
        if ( seriesDto.getSeriesInstanceUid() != null ) {
            series.setSeriesInstanceUid( seriesDto.getSeriesInstanceUid() );
        }
        if ( seriesDto.getSeriesNumber() != null ) {
            series.setSeriesNumber( seriesDto.getSeriesNumber() );
        }
    }

    private Long seriesStudyId(Series series) {
        Study study = series.getStudy();
        if ( study == null ) {
            return null;
        }
        return study.getId();
    }
}
