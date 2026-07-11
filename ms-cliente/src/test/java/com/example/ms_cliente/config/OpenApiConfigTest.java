package com.example.ms_cliente.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    @Test
    void clienteOpenAPI_returnsConfiguredAPI() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.clienteOpenAPI();

        assertNotNull(api);
        assertNotNull(api.getInfo());
        assertEquals("Microservicio de Clientes", api.getInfo().getTitle());
        assertEquals("1.0.0", api.getInfo().getVersion());
        assertNotNull(api.getInfo().getDescription());
        assertNotNull(api.getInfo().getContact());
        assertEquals("Equipo Proyecto Microservicios", api.getInfo().getContact().getName());
    }

    @Test
    void clienteOpenAPI_descriptionContainsClientes() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.clienteOpenAPI();

        assertTrue(api.getInfo().getDescription().toLowerCase().contains("clientes"));
    }

    @Test
    void clienteOpenAPI_contactNameIsNotBlank() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.clienteOpenAPI();

        assertNotNull(api.getInfo().getContact().getName());
        assertFalse(api.getInfo().getContact().getName().isBlank());
    }
}
