package com.levivarga.dicomviewerbackend.service;

import com.levivarga.dicomviewerbackend.dto.DicomParseResultDto;
import com.levivarga.dicomviewerbackend.dto.DicomParseResultDto.ParsedInstanceDto;
import com.levivarga.dicomviewerbackend.dto.DicomParseResultDto.ParsedPatientDto;
import com.levivarga.dicomviewerbackend.dto.DicomParseResultDto.ParsedSeriesDto;
import com.levivarga.dicomviewerbackend.dto.DicomParseResultDto.ParsedStudyDto;
import com.levivarga.dicomviewerbackend.entity.Instance;
import com.levivarga.dicomviewerbackend.entity.Patient;
import com.levivarga.dicomviewerbackend.entity.Series;
import com.levivarga.dicomviewerbackend.entity.Study;
import com.levivarga.dicomviewerbackend.repository.InstanceRepository;
import com.levivarga.dicomviewerbackend.repository.PatientRepository;
import com.levivarga.dicomviewerbackend.repository.SeriesRepository;
import com.levivarga.dicomviewerbackend.repository.StudyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service responsible for persisting parsed DICOM data to the database.
 * Handles deduplication based on DICOM UIDs to prevent duplicate entries.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DicomPersistenceService {

    private final PatientRepository patientRepository;
    private final StudyRepository studyRepository;
    private final SeriesRepository seriesRepository;
    private final InstanceRepository instanceRepository;

    /**
     * Persists the parsed DICOM data from the Go microservice to the database.
     * Uses DICOM UIDs for deduplication - if an entity with the same UID exists,
     * it will be reused rather than creating a duplicate.
     *
     * @param parseResult The parsed DICOM data from the Go service
     * @return List of persisted Patient entities
     */
    @Transactional
    public List<Patient> persistDicomData(DicomParseResultDto parseResult) {
        if (parseResult == null || parseResult.getPatients() == null) {
            log.warn("Received null or empty parse result");
            return new ArrayList<>();
        }

        List<Patient> persistedPatients = new ArrayList<>();

        for (ParsedPatientDto parsedPatient : parseResult.getPatients()) {
            Patient patient = findOrCreatePatient(parsedPatient);
            persistStudies(patient, parsedPatient.getStudies());
            persistedPatients.add(patient);
        }

        log.info("Persisted {} patients to database", persistedPatients.size());
        return persistedPatients;
    }

    /**
     * Finds an existing patient or creates a new one.
     * Matches on DICOM Patient ID and Patient Name combination.
     */
    private Patient findOrCreatePatient(ParsedPatientDto parsedPatient) {
        // Try to find existing patient by DICOM ID and name
        Optional<Patient> existingPatient = patientRepository
                .findByPatientDicomIdAndPatientName(
                        parsedPatient.getPatientId(),
                        parsedPatient.getPatientName()
                );

        if (existingPatient.isPresent()) {
            log.debug("Found existing patient: {}", existingPatient.get().getId());
            return existingPatient.get();
        }

        // Create new patient
        Patient patient = Patient.builder()
                .patientDicomId(parsedPatient.getPatientId())
                .patientName(parsedPatient.getPatientName())
                .patientBirthDate(parsedPatient.getPatientBirthDate())
                .patientSex(parsedPatient.getPatientSex())
                .build();

        patient = patientRepository.save(patient);
        log.debug("Created new patient with ID: {}", patient.getId());
        return patient;
    }

    /**
     * Persists studies for a patient, handling deduplication by Study Instance UID.
     */
    private void persistStudies(Patient patient, List<ParsedStudyDto> parsedStudies) {
        if (parsedStudies == null) {
            return;
        }

        for (ParsedStudyDto parsedStudy : parsedStudies) {
            Study study = findOrCreateStudy(patient, parsedStudy);
            persistSeries(study, parsedStudy.getSeries());
        }
    }

    /**
     * Finds an existing study or creates a new one.
     * Study Instance UID is guaranteed unique in DICOM.
     */
    private Study findOrCreateStudy(Patient patient, ParsedStudyDto parsedStudy) {
        // Check if study already exists by Study Instance UID
        Optional<Study> existingStudy = studyRepository
                .findByStudyInstanceUid(parsedStudy.getStudyInstanceUid());

        if (existingStudy.isPresent()) {
            log.debug("Found existing study: {}", existingStudy.get().getId());
            return existingStudy.get();
        }

        // Create new study
        Study study = Study.builder()
                .patient(patient)
                .studyInstanceUid(parsedStudy.getStudyInstanceUid())
                .studyId(parsedStudy.getStudyId())
                .studyDate(parsedStudy.getStudyDate())
                .studyDescription(parsedStudy.getStudyDescription())
                .accessionNumber(parsedStudy.getAccessionNumber())
                .build();

        study = studyRepository.save(study);
        log.debug("Created new study with ID: {}", study.getId());

        // Add to patient's studies list
        patient.getStudies().add(study);

        return study;
    }

    /**
     * Persists series for a study, handling deduplication by Series Instance UID.
     */
    private void persistSeries(Study study, List<ParsedSeriesDto> parsedSeriesList) {
        if (parsedSeriesList == null) {
            return;
        }

        for (ParsedSeriesDto parsedSeries : parsedSeriesList) {
            Series series = findOrCreateSeries(study, parsedSeries);
            persistInstances(series, parsedSeries.getInstances());
        }
    }

    /**
     * Finds an existing series or creates a new one.
     * Series Instance UID is guaranteed unique in DICOM.
     */
    private Series findOrCreateSeries(Study study, ParsedSeriesDto parsedSeries) {
        // Check if series already exists by Series Instance UID
        Optional<Series> existingSeries = seriesRepository
                .findBySeriesInstanceUid(parsedSeries.getSeriesInstanceUid());

        if (existingSeries.isPresent()) {
            log.debug("Found existing series: {}", existingSeries.get().getId());
            return existingSeries.get();
        }

        // Create new series
        Series series = Series.builder()
                .study(study)
                .seriesInstanceUid(parsedSeries.getSeriesInstanceUid())
                .seriesNumber(parsedSeries.getSeriesNumber())
                .modality(parsedSeries.getModality())
                .seriesDescription(parsedSeries.getSeriesDescription())
                .protocolName(parsedSeries.getProtocolName())
                .bodyPartExamined(parsedSeries.getBodyPartExamined())
                .build();

        series = seriesRepository.save(series);
        log.debug("Created new series with ID: {}", series.getId());

        // Add to study's series list
        study.getSeriesList().add(series);

        return series;
    }

    /**
     * Persists instances for a series, handling deduplication by SOP Instance UID.
     */
    private void persistInstances(Series series, List<ParsedInstanceDto> parsedInstances) {
        if (parsedInstances == null) {
            return;
        }

        for (ParsedInstanceDto parsedInstance : parsedInstances) {
            findOrCreateInstance(series, parsedInstance);
        }
    }

    /**
     * Finds an existing instance or creates a new one.
     * SOP Instance UID is guaranteed unique in DICOM.
     */
    private Instance findOrCreateInstance(Series series, ParsedInstanceDto parsedInstance) {
        // Check if instance already exists by SOP Instance UID
        Optional<Instance> existingInstance = instanceRepository
                .findBySopInstanceUid(parsedInstance.getSopInstanceUid());

        if (existingInstance.isPresent()) {
            log.debug("Found existing instance: {}", existingInstance.get().getId());
            return existingInstance.get();
        }

        // Also check by file path to prevent duplicates from re-uploads
        Optional<Instance> existingByPath = instanceRepository
                .findByFilePath(parsedInstance.getFilePath());

        if (existingByPath.isPresent()) {
            log.debug("Found existing instance by file path: {}", existingByPath.get().getId());
            // Update the SOP Instance UID if it changed
            Instance instance = existingByPath.get();
            instance.setSopInstanceUid(parsedInstance.getSopInstanceUid());
            return instanceRepository.save(instance);
        }

        // Create new instance
        Instance instance = Instance.builder()
                .series(series)
                .sopInstanceUid(parsedInstance.getSopInstanceUid())
                .instanceNumber(parsedInstance.getInstanceNumber())
                .sopClassUid(parsedInstance.getSopClassUid())
                .transferSyntaxUid(parsedInstance.getTransferSyntaxUid())
                .filePath(parsedInstance.getFilePath())
                .build();

        instance = instanceRepository.save(instance);
        log.debug("Created new instance with ID: {}", instance.getId());

        // Add to series' instances list
        series.getInstances().add(instance);

        return instance;
    }

    /**
     * Gets statistics about the persisted DICOM data.
     *
     * @return A summary of counts for each entity type
     */
    public DicomDataStats getStats() {
        return DicomDataStats.builder()
                .patientCount(patientRepository.count())
                .studyCount(studyRepository.count())
                .seriesCount(seriesRepository.count())
                .instanceCount(instanceRepository.count())
                .build();
    }

    /**
     * Statistics about DICOM data in the database.
     */
    @lombok.Builder
    @lombok.Getter
    public static class DicomDataStats {
        private final long patientCount;
        private final long studyCount;
        private final long seriesCount;
        private final long instanceCount;
    }
}
