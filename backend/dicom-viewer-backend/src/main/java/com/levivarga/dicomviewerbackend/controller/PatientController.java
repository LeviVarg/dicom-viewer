package com.levivarga.dicomviewerbackend.controller;

import com.levivarga.dicomviewerbackend.dto.PatientDto;
import com.levivarga.dicomviewerbackend.entity.Instance;
import com.levivarga.dicomviewerbackend.entity.Patient;
import com.levivarga.dicomviewerbackend.mapper.PatientMapper;
import com.levivarga.dicomviewerbackend.repository.InstanceRepository;
import com.levivarga.dicomviewerbackend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private PatientMapper patientMapper;

    /**
     * Get all patients with their full hierarchy (studies, series, instances)
     */
    @GetMapping
    public ResponseEntity<List<PatientDto>> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        List<PatientDto> patientDtos = patientMapper.toDtoList(patients);
        return ResponseEntity.ok(patientDtos);
    }

    /**
     * Get a single patient by ID with full hierarchy
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable Long id) {
        Optional<Patient> patientOpt = patientRepository.findById(id);
        if (patientOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        PatientDto patientDto = patientMapper.toDto(patientOpt.get());
        return ResponseEntity.ok(patientDto);
    }

    /**
     * Serve a DICOM file by instance ID.
     * This endpoint is used by Cornerstone.js wadouri loader to fetch DICOM files.
     */
    @GetMapping("/instances/{instanceId}/file")
    public ResponseEntity<Resource> getDicomFile(@PathVariable Long instanceId) {
        Optional<Instance> instanceOpt = instanceRepository.findById(instanceId);

        if (instanceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Instance instance = instanceOpt.get();
        String filePath = instance.getFilePath();

        File file = new File(filePath);
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        Resource resource = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getName());
        // Add CORS headers for Cornerstone to access the file
        headers.add("Access-Control-Expose-Headers", "Content-Length");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .body(resource);
    }

    /**
     * Serve a DICOM file by SOP Instance UID.
     * Alternative endpoint using DICOM's natural identifier.
     */
    @GetMapping("/instances/by-uid/{sopInstanceUid}/file")
    public ResponseEntity<Resource> getDicomFileBySopUid(@PathVariable String sopInstanceUid) {
        Optional<Instance> instanceOpt = instanceRepository.findBySopInstanceUid(sopInstanceUid);

        if (instanceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Instance instance = instanceOpt.get();
        String filePath = instance.getFilePath();

        File file = new File(filePath);
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        Resource resource = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getName());
        headers.add("Access-Control-Expose-Headers", "Content-Length");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .body(resource);
    }
}
