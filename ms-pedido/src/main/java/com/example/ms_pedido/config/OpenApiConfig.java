package com.example.ms_pedido.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pedidoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Pedidos")
                        .description("Gestiona los pedidos realizados por los clientes, consultando datos en tiempo real a ms-cliente y ms-producto.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Proyecto Microservicios")));
    }
}
