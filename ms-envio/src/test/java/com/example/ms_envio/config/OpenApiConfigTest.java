package com.example.ms_envio.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    @Test
    void envioOpenAPI_returnsConfiguredAPI() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.envioOpenAPI();

        assertNotNull(api);
        assertNotNull(api.getInfo());
        assertEquals("Microservicio de Envios", api.getInfo().getTitle());
        assertEquals("1.0.0", api.getInfo().getVersion());
        assertNotNull(api.getInfo().getDescription());
        assertNotNull(api.getInfo().getContact());
        assertEquals("Equipo Proyecto Microservicios", api.getInfo().getContact().getName());
    }
}
