package com.levivarga.dicomviewerbackend.mapper;

import com.levivarga.dicomviewerbackend.dto.InstanceDto;
import com.levivarga.dicomviewerbackend.entity.Instance;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InstanceMapper {

    InstanceDto toDto(Instance instance);

    List<InstanceDto> toDtoList(List<Instance> instances);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "series", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Instance toEntity(InstanceDto instanceDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "series", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(InstanceDto instanceDto, @MappingTarget Instance instance);
}
