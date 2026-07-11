package com.example.ms_opinion.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    @Test
    void opinionOpenAPI_returnsConfiguredAPI() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.opinionOpenAPI();

        assertNotNull(api);
        assertNotNull(api.getInfo());
        assertEquals("Microservicio de Opiniones", api.getInfo().getTitle());
        assertEquals("1.0.0", api.getInfo().getVersion());
        assertNotNull(api.getInfo().getDescription());
        assertNotNull(api.getInfo().getContact());
        assertEquals("Equipo Proyecto Microservicios", api.getInfo().getContact().getName());
    }

    @Test
    void opinionOpenAPI_descriptionContainsKeyword() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.opinionOpenAPI();

        assertTrue(api.getInfo().getDescription().contains("opiniones"));
        assertTrue(api.getInfo().getDescription().contains("clientes"));
        assertTrue(api.getInfo().getDescription().contains("productos"));
    }

    @Test
    void opinionOpenAPI_contactHasName() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.opinionOpenAPI();

        assertNotNull(api.getInfo().getContact());
        assertEquals("Equipo Proyecto Microservicios", api.getInfo().getContact().getName());
    }

    @Test
    void opinionOpenAPI_infoTitleAndVersionAreCorrect() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.opinionOpenAPI();

        assertEquals("Microservicio de Opiniones", api.getInfo().getTitle());
        assertEquals("1.0.0", api.getInfo().getVersion());
    }
}
