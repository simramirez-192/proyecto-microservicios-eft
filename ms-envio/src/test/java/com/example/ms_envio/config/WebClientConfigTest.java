package com.example.ms_envio.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

class WebClientConfigTest {

    @Test
    void webClientBean_returnsNonNullWebClient() {
        WebClientConfig config = new WebClientConfig();
        WebClient client = config.webClient();

        assertNotNull(client);
    }
}
