package com.example.ms_pago.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // Este bean es el "telefono" que usamos para llamar a otros microservicios
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
