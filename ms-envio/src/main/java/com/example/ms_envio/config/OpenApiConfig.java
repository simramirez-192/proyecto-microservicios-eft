package com.example.ms_envio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI envioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Envios")
                        .description("Gestiona los envios y entregas de pedidos, consultando datos en tiempo real a ms-pedido.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Proyecto Microservicios")));
    }
}
