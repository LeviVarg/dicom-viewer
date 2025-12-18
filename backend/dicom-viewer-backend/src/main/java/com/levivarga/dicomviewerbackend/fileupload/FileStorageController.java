package com.levivarga.dicomviewerbackend.fileupload;

import com.levivarga.dicomviewerbackend.dto.DicomParseResultDto;
import com.levivarga.dicomviewerbackend.dto.PatientSummaryDto;
import com.levivarga.dicomviewerbackend.entity.Patient;
import com.levivarga.dicomviewerbackend.fileparse.FileParseService;
import com.levivarga.dicomviewerbackend.mapper.PatientMapper;
import com.levivarga.dicomviewerbackend.service.DicomPersistenceService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/dicom")
@CrossOrigin(origins = "*")
public class FileStorageController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileParseService fileParseService;

    @Autowired
    private DicomPersistenceService dicomPersistenceService;

    @Autowired
    private PatientMapper patientMapper;

    @PostMapping("/upload")
    public ResponseEntity<?> store(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body(
                Map.of(
                    "success",
                    false,
                    "message",
                    "No files selected for upload."
                )
            );
        }

        try {
            // Store files to disk and collect their paths
            List<String> filePaths = Arrays.stream(files)
                .map(file -> fileStorageService.store(file))
                .toList();

            List<String> fileNames = Arrays.stream(files)
                .map(MultipartFile::getOriginalFilename)
                .toList();

            // Send file paths to Go microservice for parsing
            DicomParseResultDto processingResult =
                fileParseService.parseDicomFile(filePaths);

            // Persist parsed DICOM data to the database
            List<Patient> persistedPatients =
                dicomPersistenceService.persistDicomData(processingResult);

            // Convert to summary DTOs for response
            List<PatientSummaryDto> patientSummaries =
                patientMapper.toSummaryDtoList(persistedPatients);

            // Get current database stats
            DicomPersistenceService.DicomDataStats stats =
                dicomPersistenceService.getStats();

            return ResponseEntity.status(HttpStatus.OK).body(
                Map.of(
                    "success",
                    true,
                    "message",
                    "Uploaded and processed " +
                        files.length +
                        " files successfully",
                    "filenames",
                    fileNames,
                    "patientsCreated",
                    patientSummaries,
                    "stats",
                    Map.of(
                        "totalPatients",
                        stats.getPatientCount(),
                        "totalStudies",
                        stats.getStudyCount(),
                        "totalSeries",
                        stats.getSeriesCount(),
                        "totalInstances",
                        stats.getInstanceCount()
                    )
                )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
                Map.of(
                    "success",
                    false,
                    "message",
                    "Failed to upload files: " + e.getMessage()
                )
            );
        }
    }
}
