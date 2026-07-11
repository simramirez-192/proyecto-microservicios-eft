package com.example.ms_pedido.client;

import com.example.ms_pedido.dto.ClienteDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

// Esta clase es la unica responsable de "hablar" con ms-cliente.
// Si el dia de mañana ms-cliente cambia de URL, solo tocas este archivo.
@Component
@RequiredArgsConstructor
public class ClienteClient {

    private final RestTemplate restTemplate;

    @Value("${ms.cliente.url}")
    private String clienteUrl;

    // Llama a GET http://localhost:8082/api/clientes/{id}
    public ClienteDTO obtenerClientePorId(Long clienteId) {
        try {
            return restTemplate.getForObject(clienteUrl + "/" + clienteId, ClienteDTO.class);
        } catch (RestClientException e) {
            // Si ms-cliente no responde o el cliente no existe, devolvemos null
            return null;
        }
    }
}
