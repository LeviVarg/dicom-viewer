package com.levivarga.dicomviewerbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySummaryDto {

    private Long id;
    private Long patientId;
    private String studyInstanceUid;
    private String studyId;
    private String studyDate;
    private String studyDescription;
    private String accessionNumber;
    private Integer seriesCount;

    @Override
    public String toString() {
        return "StudySummaryDto{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", studyInstanceUid='" + studyInstanceUid + '\'' +
                ", studyId='" + studyId + '\'' +
                ", studyDate='" + studyDate + '\'' +
                ", studyDescription='" + studyDescription + '\'' +
                ", accessionNumber='" + accessionNumber + '\'' +
                ", seriesCount=" + seriesCount +
                '}';
    }
}
