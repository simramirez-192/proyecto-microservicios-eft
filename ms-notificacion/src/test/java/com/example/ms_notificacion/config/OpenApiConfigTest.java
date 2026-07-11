package com.example.ms_notificacion.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    @Test
    void notificacionOpenAPI_returnsConfiguredAPI() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.notificacionOpenAPI();

        assertNotNull(api);
        assertNotNull(api.getInfo());
        assertEquals("Microservicio de Notificaciones", api.getInfo().getTitle());
        assertEquals("1.0.0", api.getInfo().getVersion());
        assertNotNull(api.getInfo().getDescription());
        assertNotNull(api.getInfo().getContact());
        assertEquals("Equipo Proyecto Microservicios", api.getInfo().getContact().getName());
    }

    @Test
    void notificacionOpenAPI_descriptionContainsNotificaciones() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.notificacionOpenAPI();

        assertTrue(api.getInfo().getDescription().contains("notificaciones"));
    }

    @Test
    void notificacionOpenAPI_descriptionContainsMsCliente() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.notificacionOpenAPI();

        assertTrue(api.getInfo().getDescription().contains("ms-cliente"));
    }
}
