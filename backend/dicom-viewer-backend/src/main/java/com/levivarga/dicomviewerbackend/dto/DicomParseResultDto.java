package com.levivarga.dicomviewerbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO to match the ParseResponse from the Go microservice.
 * Maps to the JSON structure returned by /api/dicom/parse-batch
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DicomParseResultDto {

    @Builder.Default
    private List<ParsedPatientDto> patients = new ArrayList<>();

    private String error;

    /**
     * Matches the Patient struct from Go service
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParsedPatientDto {

        @JsonProperty("patientUID")
        private String patientUid;

        @JsonProperty("patientID")
        private String patientId;

        private String patientName;
        private String patientBirthDate;
        private String patientSex;

        @Override
        public String toString() {
            return "ParsedPatientDto{" +
                    "patientUid='" + patientUid + '\'' +
                    ", patientId='" + patientId + '\'' +
                    ", patientName='" + patientName + '\'' +
                    ", patientBirthDate='" + patientBirthDate + '\'' +
                    ", patientSex='" + patientSex + '\'' +
                    ", studies=" + studies +
                    '}';
        }

        @Builder.Default
        private List<ParsedStudyDto> studies = new ArrayList<>();
    }

    /**
     * Matches the Study struct from Go service
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParsedStudyDto {

        @Override
        public String toString() {
            return "ParsedStudyDto{" +
                    "studyInstanceUid='" + studyInstanceUid + '\'' +
                    ", patientUid='" + patientUid + '\'' +
                    ", studyId='" + studyId + '\'' +
                    ", studyDate='" + studyDate + '\'' +
                    ", studyDescription='" + studyDescription + '\'' +
                    ", accessionNumber='" + accessionNumber + '\'' +
                    ", series=" + series +
                    '}';
        }

        @JsonProperty("studyInstanceUID")
        private String studyInstanceUid;

        @JsonProperty("patientUID")
        private String patientUid;

        @JsonProperty("studyID")
        private String studyId;

        private String studyDate;
        private String studyDescription;
        private String accessionNumber;

        @Builder.Default
        private List<ParsedSeriesDto> series = new ArrayList<>();
    }

    /**
     * Matches the Series struct from Go service
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParsedSeriesDto {

        @JsonProperty("seriesInstanceUID")
        private String seriesInstanceUid;

        @Override
        public String toString() {
            return "ParsedSeriesDto{" +
                    "seriesInstanceUid='" + seriesInstanceUid + '\'' +
                    ", studyInstanceUid='" + studyInstanceUid + '\'' +
                    ", seriesNumber=" + seriesNumber +
                    ", modality='" + modality + '\'' +
                    ", seriesDescription='" + seriesDescription + '\'' +
                    ", protocolName='" + protocolName + '\'' +
                    ", bodyPartExamined='" + bodyPartExamined + '\'' +
                    ", instances=" + instances +
                    '}';
        }

        @JsonProperty("studyInstanceUID")
        private String studyInstanceUid;

        private Integer seriesNumber;
        private String modality;
        private String seriesDescription;
        private String protocolName;
        private String bodyPartExamined;

        @Builder.Default
        private List<ParsedInstanceDto> instances = new ArrayList<>();
    }

    /**
     * Matches the Instance struct from Go service
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParsedInstanceDto {

        @Override
        public String toString() {
            return "ParsedInstanceDto{" +
                    "sopInstanceUid='" + sopInstanceUid + '\'' +
                    ", seriesInstanceUid='" + seriesInstanceUid + '\'' +
                    ", instanceNumber=" + instanceNumber +
                    ", sopClassUid='" + sopClassUid + '\'' +
                    ", transferSyntaxUid='" + transferSyntaxUid + '\'' +
                    ", filePath='" + filePath + '\'' +
                    '}';
        }

        @JsonProperty("sopInstanceUID")
        private String sopInstanceUid;

        @JsonProperty("seriesInstanceUID")
        private String seriesInstanceUid;

        @JsonProperty("instanceUID")
        private Integer instanceNumber;

        @JsonProperty("sopclassUID")
        private String sopClassUid;

        @JsonProperty("transferSyntaxUID")
        private String transferSyntaxUid;

        private String filePath;
    }

    @Override
    public String toString() {
        return "DicomParseResultDto{" +
                "patients=" + patients +
                ", error='" + error + '\'' +
                '}';
    }
}
