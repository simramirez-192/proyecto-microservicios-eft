package com.example.primer_proyecto.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    @Test
    void productoOpenAPI_returnsConfiguredAPI() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.productoOpenAPI();

        assertNotNull(api);
        assertNotNull(api.getInfo());
        assertEquals("Microservicio de Productos", api.getInfo().getTitle());
        assertEquals("1.0.0", api.getInfo().getVersion());
        assertNotNull(api.getInfo().getDescription());
        assertNotNull(api.getInfo().getContact());
        assertEquals("Equipo Proyecto Microservicios", api.getInfo().getContact().getName());
    }

    @Test
    void productoOpenAPI_infoTitle_noEsNulo() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.productoOpenAPI();

        assertNotNull(api.getInfo().getTitle());
        assertFalse(api.getInfo().getTitle().isEmpty());
    }

    @Test
    void productoOpenAPI_infoVersion_esCorrecta() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.productoOpenAPI();

        assertEquals("1.0.0", api.getInfo().getVersion());
    }

    @Test
    void productoOpenAPI_contactName_esCorrecto() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.productoOpenAPI();

        assertNotNull(api.getInfo().getContact());
        assertEquals("Equipo Proyecto Microservicios", api.getInfo().getContact().getName());
    }

    @Test
    void productoOpenAPI_descriptionContieneProductos() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.productoOpenAPI();

        assertTrue(api.getInfo().getDescription().toLowerCase().contains("producto"));
    }
}
