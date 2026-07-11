package com.example.ms_pago.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    @Test
    void pagoOpenAPI_returnsConfiguredAPI() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.pagoOpenAPI();

        assertNotNull(api);
        assertNotNull(api.getInfo());
        assertEquals("Microservicio de Pagos", api.getInfo().getTitle());
        assertEquals("1.0.0", api.getInfo().getVersion());
        assertNotNull(api.getInfo().getDescription());
        assertNotNull(api.getInfo().getContact());
        assertEquals("Equipo Proyecto Microservicios", api.getInfo().getContact().getName());
    }

    @Test
    void pagoOpenAPI_descriptionContainsKeyWords() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.pagoOpenAPI();

        assertTrue(api.getInfo().getDescription().contains("pagos"));
        assertTrue(api.getInfo().getDescription().contains("pedido"));
    }

    @Test
    void pagoOpenAPI_contactNameIsNotNull() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.pagoOpenAPI();

        assertNotNull(api.getInfo().getContact().getName());
        assertFalse(api.getInfo().getContact().getName().isEmpty());
    }
}
