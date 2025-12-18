package com.levivarga.dicomviewerbackend.fileparse;

import com.levivarga.dicomviewerbackend.dto.DicomParseResultDto;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class FileParseService implements FileParseServiceInterface {

    private final RestTemplate restTemplate;
    private final HttpHeaders jsonHeaders;
    private final String goServiceUrl;

    public FileParseService(
        RestTemplate restTemplate,
        HttpHeaders jsonHeaders,
        @Value("${go.service.url}") String goServiceUrl
    ) {
        this.restTemplate = restTemplate;
        this.jsonHeaders = jsonHeaders;
        this.goServiceUrl = goServiceUrl;
    }

    /**
     * Sends file paths to the Go microservice for DICOM parsing.
     *
     * @param filePaths List of absolute file paths to the uploaded DICOM files
     * @return The parsed DICOM data from the Go service
     */
    @Override
    public DicomParseResultDto parseDicomFile(List<String> filePaths) {
        String url = goServiceUrl + "/api/dicom/parse-batch";

        // Create request body matching ParseRequest struct in Go
        Map<String, Object> requestBody = Map.of("filePaths", filePaths);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(
            requestBody,
            jsonHeaders
        );

        try {
            ResponseEntity<DicomParseResultDto> response =
                restTemplate.postForEntity(
                    url,
                    reuest,
                    DicomParseResultDto.class
                );
            return response.getBody();
        } catch (RestClientException e) {
            throw new RuntimeException(
                "Failed to call Go DICOM processing service: " + e.getMessage(),
                e
            );
        }
    }
}
