package com.levivarga.dicomviewerbackend.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeriesDto {

    private Long id;
    private String seriesInstanceUid;
    private Integer seriesNumber;
    private String modality;
    private String seriesDescription;
    private String protocolName;
    private String bodyPartExamined;

    @Builder.Default
    private List<InstanceDto> instances = new ArrayList<>();

    @Override
    public String toString() {
        return "SeriesDto{" +
                "id=" + id +
                ", seriesInstanceUid='" + seriesInstanceUid + '\'' +
                ", seriesNumber=" + seriesNumber +
                ", modality='" + modality + '\'' +
                ", seriesDescription='" + seriesDescription + '\'' +
                ", protocolName='" + protocolName + '\'' +
                ", bodyPartExamined='" + bodyPartExamined + '\'' +
                ", instances=" + instances +
                '}';
    }
}
