package com.levivarga.dicomviewerbackend.mapper;

import com.levivarga.dicomviewerbackend.dto.InstanceDto;
import com.levivarga.dicomviewerbackend.entity.Instance;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-12T13:21:49+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 24.0.2 (Oracle Corporation)"
)
@Component
public class InstanceMapperImpl implements InstanceMapper {

    @Override
    public InstanceDto toDto(Instance instance) {
        if ( instance == null ) {
            return null;
        }

        InstanceDto.InstanceDtoBuilder instanceDto = InstanceDto.builder();

        instanceDto.filePath( instance.getFilePath() );
        instanceDto.id( instance.getId() );
        instanceDto.instanceNumber( instance.getInstanceNumber() );
        instanceDto.sopClassUid( instance.getSopClassUid() );
        instanceDto.sopInstanceUid( instance.getSopInstanceUid() );
        instanceDto.transferSyntaxUid( instance.getTransferSyntaxUid() );

        return instanceDto.build();
    }

    @Override
    public List<InstanceDto> toDtoList(List<Instance> instances) {
        if ( instances == null ) {
            return null;
        }

        List<InstanceDto> list = new ArrayList<InstanceDto>( instances.size() );
        for ( Instance instance : instances ) {
            list.add( toDto( instance ) );
        }

        return list;
    }

    @Override
    public Instance toEntity(InstanceDto instanceDto) {
        if ( instanceDto == null ) {
            return null;
        }

        Instance.InstanceBuilder instance = Instance.builder();

        instance.filePath( instanceDto.getFilePath() );
        instance.instanceNumber( instanceDto.getInstanceNumber() );
        instance.sopClassUid( instanceDto.getSopClassUid() );
        instance.sopInstanceUid( instanceDto.getSopInstanceUid() );
        instance.transferSyntaxUid( instanceDto.getTransferSyntaxUid() );

        return instance.build();
    }

    @Override
    public void updateEntityFromDto(InstanceDto instanceDto, Instance instance) {
        if ( instanceDto == null ) {
            return;
        }

        if ( instanceDto.getFilePath() != null ) {
            instance.setFilePath( instanceDto.getFilePath() );
        }
        if ( instanceDto.getInstanceNumber() != null ) {
            instance.setInstanceNumber( instanceDto.getInstanceNumber() );
        }
        if ( instanceDto.getSopClassUid() != null ) {
            instance.setSopClassUid( instanceDto.getSopClassUid() );
        }
        if ( instanceDto.getSopInstanceUid() != null ) {
            instance.setSopInstanceUid( instanceDto.getSopInstanceUid() );
        }
        if ( instanceDto.getTransferSyntaxUid() != null ) {
            instance.setTransferSyntaxUid( instanceDto.getTransferSyntaxUid() );
        }
    }
}
