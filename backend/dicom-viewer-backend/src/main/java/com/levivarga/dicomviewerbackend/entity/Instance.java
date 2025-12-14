package com.levivarga.dicomviewerbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "instances", indexes = {
        @Index(name = "idx_instances_series_id", columnList = "series_id"),
        @Index(name = "idx_instances_file_path", columnList = "filePath")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id", nullable = false)
    private Series series;

    @Column(name = "sop_instance_uid", length = 128, nullable = false, unique = true)
    private String sopInstanceUid;  // DICOM SOPInstanceUID (0008,0018)

    @Column(name = "instance_number")
    private Integer instanceNumber;  // DICOM InstanceNumber (0020,0013)

    @Column(name = "sop_class_uid", length = 128)
    private String sopClassUid;  // DICOM SOPClassUID (0008,0016)

    @Column(name = "transfer_syntax_uid", length = 128)
    private String transferSyntaxUid;  // DICOM TransferSyntaxUID (0002,0010)

    @Column(name = "file_path", length = 512, nullable = false)
    private String filePath;  // Path to the DICOM file on disk

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
