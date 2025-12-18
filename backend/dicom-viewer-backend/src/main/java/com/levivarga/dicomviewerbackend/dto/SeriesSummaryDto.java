package com.levivarga.dicomviewerbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeriesSummaryDto {

    private Long id;
    private Long studyId;
    private String seriesInstanceUid;
    private Integer seriesNumber;
    private String modality;
    private String seriesDescription;
    private String protocolName;
    private String bodyPartExamined;
    private Integer instanceCount;

    @Override
    public String toString() {
        return "SeriesSummaryDto{" +
                "id=" + id +
                ", studyId=" + studyId +
                ", seriesInstanceUid='" + seriesInstanceUid + '\'' +
                ", seriesNumber=" + seriesNumber +
                ", modality='" + modality + '\'' +
                ", seriesDescription='" + seriesDescription + '\'' +
                ", protocolName='" + protocolName + '\'' +
                ", bodyPartExamined='" + bodyPartExamined + '\'' +
                ", instanceCount=" + instanceCount +
                '}';
    }
}
