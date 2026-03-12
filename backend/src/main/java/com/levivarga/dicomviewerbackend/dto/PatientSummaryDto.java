package com.levivarga.dicomviewerbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientSummaryDto {

    private Long id;
    private String patientDicomId;
    private String patientName;
    private String patientBirthDate;
    private String patientSex;
    private Integer studyCount;

    @Override
    public String toString() {
        return "PatientSummaryDto{" +
                "id=" + id +
                ", patientDicomId='" + patientDicomId + '\'' +
                ", patientName='" + patientName + '\'' +
                ", patientBirthDate='" + patientBirthDate + '\'' +
                ", patientSex='" + patientSex + '\'' +
                ", studyCount=" + studyCount +
                '}';
    }
}
