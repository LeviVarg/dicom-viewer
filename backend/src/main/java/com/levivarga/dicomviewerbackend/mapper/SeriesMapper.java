package com.levivarga.dicomviewerbackend.mapper;

import com.levivarga.dicomviewerbackend.dto.SeriesDto;
import com.levivarga.dicomviewerbackend.dto.SeriesSummaryDto;
import com.levivarga.dicomviewerbackend.entity.Series;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {InstanceMapper.class})
public interface SeriesMapper {

    SeriesDto toDto(Series series);

    List<SeriesDto> toDtoList(List<Series> seriesList);

    @Mapping(target = "studyId", source = "study.id")
    @Mapping(target = "instanceCount", expression = "java(series.getInstances() != null ? series.getInstances().size() : 0)")
    SeriesSummaryDto toSummaryDto(Series series);

    List<SeriesSummaryDto> toSummaryDtoList(List<Series> seriesList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "study", ignore = true)
    @Mapping(target = "instances", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Series toEntity(SeriesDto seriesDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "study", ignore = true)
    @Mapping(target = "instances", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(SeriesDto seriesDto, @MappingTarget Series series);
}
