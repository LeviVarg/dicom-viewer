package com.levivarga.dicomviewerbackend.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;


@Configuration
public class RestTemplateConfig {

    /**
     * Creates a configured RestTemplate bean.
     * RestTemplateBuilder is auto-configured by Spring Boot.
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    /**
     * Creates reusable JSON headers.
     * Note: HttpHeaders is mutable, so be careful with shared instances.
     * For truly immutable headers, consider returning a new instance each time
     * or using a factory method instead.
     */
    @Bean
    public HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        return headers;
    }
}