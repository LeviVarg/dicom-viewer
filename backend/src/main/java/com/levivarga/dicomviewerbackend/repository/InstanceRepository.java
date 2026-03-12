package com.levivarga.dicomviewerbackend.repository;

import com.levivarga.dicomviewerbackend.entity.Instance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstanceRepository extends JpaRepository<Instance, Long> {

    /**
     * Find an instance by its DICOM SOP Instance UID.
     * This is guaranteed to be unique in DICOM.
     */
    Optional<Instance> findBySopInstanceUid(String sopInstanceUid);

    /**
     * Check if an instance with the given SOP Instance UID exists.
     */
    boolean existsBySopInstanceUid(String sopInstanceUid);

    /**
     * Find all instances for a specific series.
     */
    List<Instance> findBySeriesId(Long seriesId);

    /**
     * Find instances ordered by instance number.
     */
    List<Instance> findBySeriesIdOrderByInstanceNumberAsc(Long seriesId);

    /**
     * Find an instance by its file path.
     */
    Optional<Instance> findByFilePath(String filePath);

    /**
     * Check if an instance with the given file path exists.
     */
    boolean existsByFilePath(String filePath);

    /**
     * Find instances by SOP Class UID (type of DICOM object).
     */
    List<Instance> findBySopClassUid(String sopClassUid);

    /**
     * Find instances by transfer syntax UID.
     */
    List<Instance> findByTransferSyntaxUid(String transferSyntaxUid);

    /**
     * Find instances for a series by SOP Class UID.
     */
    List<Instance> findBySeriesIdAndSopClassUid(Long seriesId, String sopClassUid);

    /**
     * Count the number of instances for a series.
     */
    Long countBySeriesId(Long seriesId);

    /**
     * Get the first instance of a series (by instance number).
     * Useful for getting a representative image.
     */
    Optional<Instance> findFirstBySeriesIdOrderByInstanceNumberAsc(Long seriesId);

    /**
     * Get the middle instance of a series.
     * Useful for thumbnails/previews.
     */
    @Query(value = "SELECT * FROM instances WHERE series_id = :seriesId " +
                   "ORDER BY instance_number ASC " +
                   "LIMIT 1 OFFSET (SELECT COUNT(*) / 2 FROM instances WHERE series_id = :seriesId)",
           nativeQuery = true)
    Optional<Instance> findMiddleInstanceBySeriesId(@Param("seriesId") Long seriesId);

    /**
     * Find all instances for a study (across all series).
     */
    @Query("SELECT i FROM Instance i WHERE i.series.study.id = :studyId ORDER BY i.series.seriesNumber, i.instanceNumber")
    List<Instance> findByStudyId(@Param("studyId") Long studyId);

    /**
     * Find all instances for a patient (across all studies and series).
     */
    @Query("SELECT i FROM Instance i WHERE i.series.study.patient.id = :patientId")
    List<Instance> findByPatientId(@Param("patientId") Long patientId);

    /**
     * Count total instances in the database.
     */
    @Query("SELECT COUNT(i) FROM Instance i")
    Long countTotalInstances();

    /**
     * Get distinct SOP Class UIDs in the database.
     */
    @Query("SELECT DISTINCT i.sopClassUid FROM Instance i WHERE i.sopClassUid IS NOT NULL ORDER BY i.sopClassUid")
    List<String> findDistinctSopClassUids();

    /**
     * Get distinct transfer syntax UIDs in the database.
     */
    @Query("SELECT DISTINCT i.transferSyntaxUid FROM Instance i WHERE i.transferSyntaxUid IS NOT NULL ORDER BY i.transferSyntaxUid")
    List<String> findDistinctTransferSyntaxUids();

    /**
     * Delete all instances by series ID.
     */
    void deleteBySeriesId(Long seriesId);

    /**
     * Find instances with file paths containing a specific pattern.
     * Useful for finding instances in a specific directory.
     */
    List<Instance> findByFilePathContaining(String pathPattern);
}
