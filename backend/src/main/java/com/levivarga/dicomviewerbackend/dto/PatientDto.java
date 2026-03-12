package com.levivarga.dicomviewerbackend.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDto {

    private Long id;
    private String patientDicomId;

    @Override
    public String toString() {
        return "PatientDto{" +
                "id=" + id +
                ", patientDicomId='" + patientDicomId + '\'' +
                ", patientName='" + patientName + '\'' +
                ", patientBirthDate='" + patientBirthDate + '\'' +
                ", patientSex='" + patientSex + '\'' +
                ", studies=" + studies +
                '}';
    }

    private String patientName;
    private String patientBirthDate;
    private String patientSex;

    @Builder.Default
    private List<StudyDto> studies = new ArrayList<>();
}
