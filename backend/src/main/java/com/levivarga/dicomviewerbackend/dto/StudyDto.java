package com.levivarga.dicomviewerbackend.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyDto {

    private Long id;
    private String studyInstanceUid;
    private String studyId;
    private String studyDate;
    private String studyDescription;
    private String accessionNumber;

    @Builder.Default
    private List<SeriesDto> seriesList = new ArrayList<>();

    @Override
    public String toString() {
        return "StudyDto{" +
                "id=" + id +
                ", studyInstanceUid='" + studyInstanceUid + '\'' +
                ", studyId='" + studyId + '\'' +
                ", studyDate='" + studyDate + '\'' +
                ", studyDescription='" + studyDescription + '\'' +
                ", accessionNumber='" + accessionNumber + '\'' +
                ", seriesList=" + seriesList +
                '}';
    }
}
