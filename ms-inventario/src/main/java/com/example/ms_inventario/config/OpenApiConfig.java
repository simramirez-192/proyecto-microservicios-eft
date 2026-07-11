package com.example.ms_inventario.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inventarioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Inventario")
                        .description("Gestiona el stock disponible de cada producto, consultando datos en tiempo real a ms-producto.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Proyecto Microservicios")));
    }
}
