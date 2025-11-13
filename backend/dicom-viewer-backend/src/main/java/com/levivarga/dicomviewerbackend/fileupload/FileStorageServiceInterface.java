package com.levivarga.dicomviewerbackend.fileupload;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageServiceInterface {
    /**
     * Initializes the storage location.
     * This should be called on application startup.
     */
    void init();


    /**
     * Stores a single file to the designated storage location.
     *
     * @param file The MultipartFile to be stored.
     * @throws StorageException if the file cannot be stored.
     */
    void store(MultipartFile file) throws StorageException;
}
