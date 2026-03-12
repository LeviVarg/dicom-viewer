package com.levivarga.dicomviewerbackend.repository;

import com.levivarga.dicomviewerbackend.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Find a patient by their DICOM Patient ID.
     * Note: Multiple patients may have the same DICOM ID (e.g., anonymized files),
     * so this returns a list.
     */
    List<Patient> findByPatientDicomId(String patientDicomId);

    /**
     * Find a patient by name (case-insensitive, partial match).
     */
    List<Patient> findByPatientNameContainingIgnoreCase(String patientName);

    /**
     * Find patients by sex.
     */
    List<Patient> findByPatientSex(String patientSex);

    /**
     * Find patients by birth date.
     */
    List<Patient> findByPatientBirthDate(String patientBirthDate);

    /**
     * Check if a patient with the given DICOM ID and name already exists.
     * Useful for deduplication during import.
     */
    Optional<Patient> findByPatientDicomIdAndPatientName(String patientDicomId, String patientName);

    /**
     * Find all patients with their studies eagerly loaded.
     */
    @Query("SELECT DISTINCT p FROM Patient p LEFT JOIN FETCH p.studies")
    List<Patient> findAllWithStudies();

    /**
     * Find a patient by ID with studies eagerly loaded.
     */
    @Query("SELECT p FROM Patient p LEFT JOIN FETCH p.studies WHERE p.id = :id")
    Optional<Patient> findByIdWithStudies(@Param("id") Long id);

    /**
     * Search patients by name or DICOM ID (case-insensitive).
     */
    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.patientName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.patientDicomId) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Patient> searchByNameOrDicomId(@Param("searchTerm") String searchTerm);

    /**
     * Count the number of studies for a patient.
     */
    @Query("SELECT COUNT(s) FROM Study s WHERE s.patient.id = :patientId")
    Long countStudiesByPatientId(@Param("patientId") Long patientId);
}
