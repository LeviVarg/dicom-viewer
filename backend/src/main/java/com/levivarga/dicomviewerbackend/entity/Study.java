package com.levivarga.dicomviewerbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "studies", indexes = {
        @Index(name = "idx_studies_patient_id", columnList = "patient_id"),
        @Index(name = "idx_studies_study_date", columnList = "studyDate")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "study_instance_uid", length = 128, nullable = false, unique = true)
    private String studyInstanceUid;  // DICOM StudyInstanceUID (0020,000D)

    @Column(name = "study_id", length = 64)
    private String studyId;  // DICOM StudyID (0020,0010)

    @Column(name = "study_date", length = 8)
    private String studyDate;  // DICOM StudyDate (0008,0020) - YYYYMMDD

    @Column(name = "study_description", length = 255)
    private String studyDescription;  // DICOM StudyDescription (0008,1030)

    @Column(name = "accession_number", length = 64)
    private String accessionNumber;  // DICOM AccessionNumber (0008,0050)

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Series> seriesList = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper method to add a series
    public void addSeries(Series series) {
        seriesList.add(series);
        series.setStudy(this);
    }
}
