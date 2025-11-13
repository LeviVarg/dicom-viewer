package com.levivarga.dicomviewerbackend.fileupload;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileStorageService implements FileStorageServiceInterface {

    // Define the root location for storing files.
    // TODO: This should ideally be configurable in application.properties
    private final Path rootLocation = Paths.get("dicom-uploads");

    @Override
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
            System.out.println("Storage directory initialized: " + rootLocation.toAbsolutePath());
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    /**
     * Stores a file.
     *
     * @param file The file to store.
     */
    @Override
    public void store(MultipartFile file) throws StorageException {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }

            // Resolve the final path for the file
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(Objects.requireNonNull(file.getOriginalFilename()))
            ).normalize().toAbsolutePath();

            // Security check: Ensure the file is stored within the rootLocation
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store file outside current directory.");
            }

            // Copy the file's input stream to the target path
            // REPLACE_EXISTING ensures that if a file with the same name is uploaded,
            // it will be overwritten.
            // TODO: rewrite this to be structural
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Successfully stored file: " + file.getOriginalFilename());
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }
}
