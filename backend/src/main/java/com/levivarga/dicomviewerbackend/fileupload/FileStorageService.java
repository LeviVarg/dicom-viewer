package com.levivarga.dicomviewerbackend.fileupload;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService implements FileStorageServiceInterface {

    // Define the root location for storing files.
    // TODO: This should ideally be configurable in application.properties
    private final Path rootLocation;

    public FileStorageService(
        @Value("${dicom.upload.path:/data/dicom-uploads}") String uploadPath
    ) {
        this.rootLocation = Paths.get(uploadPath);
    }

    @Override
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
            System.out.println(
                "Storage directory initialized: " +
                    rootLocation.toAbsolutePath()
            );
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    /**
     * Generates a unique filename by prepending a timestamp to prevent file collisions.
     *
     * @param originalFilename The original filename from the uploaded file.
     * @return A unique filename with timestamp prefix.
     */
    private String generateUniqueFilename(String originalFilename) {
        // Format: yyyy-MM-dd_HH-mm-ss-SSS_originalFilename
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd_HH-mm-ss-SSS"
        );
        String timestamp = now.format(formatter);
        return timestamp + "_" + originalFilename;
    }

    /**
     * Stores a file.
     *
     * @param file The file to store.
     * @return
     */
    @Override
    public String store(MultipartFile file) throws StorageException {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }

            // Generate a unique filename with timestamp prefix
            String uniqueFilename = generateUniqueFilename(
                Objects.requireNonNull(file.getOriginalFilename())
            );

            // Resolve the final path for the file
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(uniqueFilename)
                )
                .normalize()
                .toAbsolutePath();

            // Security check: Ensure the file is stored within the rootLocation
            if (
                !destinationFile
                    .getParent()
                    .equals(this.rootLocation.toAbsolutePath())
            ) {
                throw new StorageException(
                    "Cannot store file outside current directory."
                );
            }

            // Copy the file's input stream to the target path
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(
                    inputStream,
                    destinationFile,
                    StandardCopyOption.REPLACE_EXISTING
                );
                System.out.println(
                    "Successfully stored file: " + file.getOriginalFilename()
                );
            }

            return destinationFile.toString();
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }
}
