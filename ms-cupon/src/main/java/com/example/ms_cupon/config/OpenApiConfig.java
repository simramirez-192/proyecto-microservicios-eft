package com.example.ms_cupon.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cuponOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Cupones")
                        .description("Gestiona cupones de descuento para el sistema de microservicios.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Proyecto Microservicios")));
    }
}
