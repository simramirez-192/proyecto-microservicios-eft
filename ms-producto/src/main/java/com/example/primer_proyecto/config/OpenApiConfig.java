package com.example.primer_proyecto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Esta clase configura los datos generales que aparecen arriba de la
// pagina de Swagger (titulo, descripcion, version). La UI queda disponible
// automaticamente en: http://localhost:8080/swagger-ui/index.html
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Productos")
                        .description("Gestiona el catalogo de productos: creacion, consulta, actualizacion y eliminacion.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Proyecto Microservicios")));
    }
}
