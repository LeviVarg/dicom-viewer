package com.levivarga.dicomviewerbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
    name = "patients",
    indexes = {
        @Index(name = "idx_patients_dicom_id", columnList = "patientDicomId"),
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_dicom_id", length = 64)
    private String patientDicomId;

    @Column(name = "patient_name", length = 255)
    private String patientName;

    @Column(name = "patient_birth_date", length = 32)
    private String patientBirthDate;

    @Column(name = "patient_sex", length = 32)
    private String patientSex;

    @OneToMany(
        mappedBy = "patient",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Study> studies = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper method to add a study
    public void addStudy(Study study) {
        studies.add(study);
        study.setPatient(this);
    }
}
