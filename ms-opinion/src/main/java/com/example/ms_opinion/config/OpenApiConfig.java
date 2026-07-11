package com.example.ms_opinion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI opinionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Opiniones")
                        .description("Gestiona las opiniones y calificaciones de los clientes sobre los productos, consultando datos en tiempo real a ms-cliente y ms-producto.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Proyecto Microservicios")));
    }
}
