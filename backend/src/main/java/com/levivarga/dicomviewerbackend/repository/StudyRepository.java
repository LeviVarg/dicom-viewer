package com.levivarga.dicomviewerbackend.repository;

import com.levivarga.dicomviewerbackend.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    /**
     * Find a study by its DICOM Study Instance UID.
     * This is guaranteed to be unique in DICOM.
     */
    Optional<Study> findByStudyInstanceUid(String studyInstanceUid);

    /**
     * Check if a study with the given Study Instance UID exists.
     */
    boolean existsByStudyInstanceUid(String studyInstanceUid);

    /**
     * Find all studies for a specific patient.
     */
    List<Study> findByPatientId(Long patientId);

    /**
     * Find studies by date.
     */
    List<Study> findByStudyDate(String studyDate);

    /**
     * Find studies by date range.
     */
    @Query("SELECT s FROM Study s WHERE s.studyDate BETWEEN :startDate AND :endDate ORDER BY s.studyDate DESC")
    List<Study> findByStudyDateBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * Find studies by description (case-insensitive, partial match).
     */
    List<Study> findByStudyDescriptionContainingIgnoreCase(String studyDescription);

    /**
     * Find a study by accession number.
     */
    Optional<Study> findByAccessionNumber(String accessionNumber);

    /**
     * Find a study by ID with series eagerly loaded.
     */
    @Query("SELECT s FROM Study s LEFT JOIN FETCH s.seriesList WHERE s.id = :id")
    Optional<Study> findByIdWithSeries(@Param("id") Long id);

    /**
     * Find all studies for a patient with series eagerly loaded.
     */
    @Query("SELECT DISTINCT s FROM Study s LEFT JOIN FETCH s.seriesList WHERE s.patient.id = :patientId")
    List<Study> findByPatientIdWithSeries(@Param("patientId") Long patientId);

    /**
     * Search studies by description or accession number.
     */
    @Query("SELECT s FROM Study s WHERE " +
           "LOWER(s.studyDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.accessionNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Study> searchByDescriptionOrAccessionNumber(@Param("searchTerm") String searchTerm);

    /**
     * Count the number of series for a study.
     */
    @Query("SELECT COUNT(ser) FROM Series ser WHERE ser.study.id = :studyId")
    Long countSeriesByStudyId(@Param("studyId") Long studyId);

    /**
     * Find studies ordered by date (most recent first).
     */
    List<Study> findAllByOrderByStudyDateDesc();

    /**
     * Find studies for a patient ordered by date (most recent first).
     */
    List<Study> findByPatientIdOrderByStudyDateDesc(Long patientId);
}
