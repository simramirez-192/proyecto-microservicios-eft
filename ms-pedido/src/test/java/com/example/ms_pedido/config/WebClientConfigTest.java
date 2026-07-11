package com.example.ms_pedido.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

class WebClientConfigTest {

    @Test
    void webClient_returnsNonNullInstance() {
        WebClientConfig config = new WebClientConfig();
        WebClient webClient = config.webClient();

        assertNotNull(webClient);
    }
}
