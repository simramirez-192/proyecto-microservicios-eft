package com.example.ms_categoria.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI categoriaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Categorias")
                        .description("Gestiona las categorias de productos del sistema.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Proyecto Microservicios")));
    }
}
