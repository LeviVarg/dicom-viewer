package com.levivarga.dicomviewerbackend.repository;

import com.levivarga.dicomviewerbackend.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {

    /**
     * Find a series by its DICOM Series Instance UID.
     * This is guaranteed to be unique in DICOM.
     */
    Optional<Series> findBySeriesInstanceUid(String seriesInstanceUid);

    /**
     * Check if a series with the given Series Instance UID exists.
     */
    boolean existsBySeriesInstanceUid(String seriesInstanceUid);

    /**
     * Find all series for a specific study.
     */
    List<Series> findByStudyId(Long studyId);

    /**
     * Find series by modality.
     */
    List<Series> findByModality(String modality);

    /**
     * Find series by modality for a specific study.
     */
    List<Series> findByStudyIdAndModality(Long studyId, String modality);

    /**
     * Find series by description (case-insensitive, partial match).
     */
    List<Series> findBySeriesDescriptionContainingIgnoreCase(String seriesDescription);

    /**
     * Find series by body part examined.
     */
    List<Series> findByBodyPartExamined(String bodyPartExamined);

    /**
     * Find series by protocol name.
     */
    List<Series> findByProtocolName(String protocolName);

    /**
     * Find a series by ID with instances eagerly loaded.
     */
    @Query("SELECT s FROM Series s LEFT JOIN FETCH s.instances WHERE s.id = :id")
    Optional<Series> findByIdWithInstances(@Param("id") Long id);

    /**
     * Find all series for a study with instances eagerly loaded.
     */
    @Query("SELECT DISTINCT s FROM Series s LEFT JOIN FETCH s.instances WHERE s.study.id = :studyId")
    List<Series> findByStudyIdWithInstances(@Param("studyId") Long studyId);

    /**
     * Find series ordered by series number.
     */
    List<Series> findByStudyIdOrderBySeriesNumberAsc(Long studyId);

    /**
     * Search series by description or protocol name.
     */
    @Query("SELECT s FROM Series s WHERE " +
           "LOWER(s.seriesDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.protocolName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Series> searchByDescriptionOrProtocol(@Param("searchTerm") String searchTerm);

    /**
     * Count the number of instances for a series.
     */
    @Query("SELECT COUNT(i) FROM Instance i WHERE i.series.id = :seriesId")
    Long countInstancesBySeriesId(@Param("seriesId") Long seriesId);

    /**
     * Get distinct modalities in the database.
     */
    @Query("SELECT DISTINCT s.modality FROM Series s WHERE s.modality IS NOT NULL ORDER BY s.modality")
    List<String> findDistinctModalities();

    /**
     * Get distinct body parts examined in the database.
     */
    @Query("SELECT DISTINCT s.bodyPartExamined FROM Series s WHERE s.bodyPartExamined IS NOT NULL ORDER BY s.bodyPartExamined")
    List<String> findDistinctBodyParts();
}
