package com.example.ms_notificacion.client;

import com.example.ms_notificacion.dto.ClienteDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ClienteClient {

    private static final Logger logger = LoggerFactory.getLogger(ClienteClient.class);
    private final WebClient webClient;

    @Value("${ms.cliente.url}")
    private String clienteUrl;

    public ClienteDTO obtenerClientePorId(Long clienteId) {
        try {
            ClienteDTO cliente = webClient.get()
                    .uri(clienteUrl + "/{id}", clienteId)
                    .retrieve()
                    .bodyToMono(ClienteDTO.class)
                    .block();
            logger.info("Cliente obtenido exitosamente: id={}", clienteId);
            return cliente;
        } catch (Exception e) {
            logger.warn("No se pudo obtener el cliente con id={}: {}", clienteId, e.getMessage());
            return null;
        }
    }
}
