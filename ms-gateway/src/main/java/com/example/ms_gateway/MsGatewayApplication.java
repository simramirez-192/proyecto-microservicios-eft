package com.example.ms_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Este microservicio NO tiene controller, service, ni repository como los demas.
// Su unico trabajo es RECIBIR las peticiones y REDIRIGIRLAS al microservicio
// correcto, segun las reglas que definimos en application.yml.
@SpringBootApplication
public class MsGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsGatewayApplication.class, args);
	}

}
