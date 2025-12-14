package com.levivarga.dicomviewerbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "series", indexes = {
        @Index(name = "idx_series_study_id", columnList = "study_id"),
        @Index(name = "idx_series_modality", columnList = "modality")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @Column(name = "series_instance_uid", length = 128, nullable = false, unique = true)
    private String seriesInstanceUid;  // DICOM SeriesInstanceUID (0020,000E)

    @Column(name = "series_number")
    private Integer seriesNumber;  // DICOM SeriesNumber (0020,0011)

    @Column(name = "modality", length = 16)
    private String modality;  // DICOM Modality (0008,0060) - CT, MR, US, etc.

    @Column(name = "series_description", length = 255)
    private String seriesDescription;  // DICOM SeriesDescription (0008,103E)

    @Column(name = "protocol_name", length = 255)
    private String protocolName;  // DICOM ProtocolName (0018,1030)

    @Column(name = "body_part_examined", length = 64)
    private String bodyPartExamined;  // DICOM BodyPartExamined (0018,0015)

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Instance> instances = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper method to add an instance
    public void addInstance(Instance instance) {
        instances.add(instance);
        instance.setSeries(this);
    }
}
