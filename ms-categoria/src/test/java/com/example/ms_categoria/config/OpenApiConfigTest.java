package com.example.ms_categoria.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    @Test
    void categoriaOpenAPI_returnsConfiguredAPI() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.categoriaOpenAPI();

        assertNotNull(api);
        assertNotNull(api.getInfo());
        assertEquals("Microservicio de Categorias", api.getInfo().getTitle());
        assertEquals("1.0.0", api.getInfo().getVersion());
        assertNotNull(api.getInfo().getDescription());
        assertNotNull(api.getInfo().getContact());
        assertEquals("Equipo Proyecto Microservicios", api.getInfo().getContact().getName());
    }

    @Test
    void categoriaOpenAPI_descriptionIsNotEmpty() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.categoriaOpenAPI();

        assertFalse(api.getInfo().getDescription().isEmpty());
        assertTrue(api.getInfo().getDescription().contains("categorias"));
    }

    @Test
    void categoriaOpenAPI_contactHasName() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.categoriaOpenAPI();

        assertNotNull(api.getInfo().getContact());
        assertNotNull(api.getInfo().getContact().getName());
        assertFalse(api.getInfo().getContact().getName().isEmpty());
    }
}
