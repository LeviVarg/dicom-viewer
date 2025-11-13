package com.levivarga.dicomviewerbackend.fileupload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dicom")
@CrossOrigin(origins = "*")
public class FileStorageController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<?> store(MultipartFile[] files) {
        if(files == null || files.length == 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "No files selected for upload."
            ));        }

        try {
            Arrays.stream(files).forEach(file -> fileStorageService.store(file));

            List<String> fileNames =  Arrays.stream(files)
                    .map(MultipartFile::getOriginalFilename)
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of(
                            "success", true,
                            "message", "Uploaded " + files.length + " files successfully",
                            "filenames", fileNames
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to upload files: " + e.getMessage()
                    ));}
    }
}
