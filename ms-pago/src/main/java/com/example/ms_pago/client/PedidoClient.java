package com.example.ms_pago.client;

import com.example.ms_pago.dto.PedidoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

// Esta clase es la unica responsable de "hablar" con ms-pedido.
// Si el dia de mañana ms-pedido cambia de URL, solo tocas este archivo.
@Component
@RequiredArgsConstructor
public class PedidoClient {

    private final RestTemplate restTemplate;

    @Value("${ms.pedido.url}")
    private String pedidoUrl;

    // Llama a GET http://localhost:8083/api/pedidos/{id}
    public PedidoDTO obtenerPedidoPorId(Long pedidoId) {
        try {
            return restTemplate.getForObject(pedidoUrl + "/" + pedidoId, PedidoDTO.class);
        } catch (RestClientException e) {
            // Si ms-pedido no responde o el pedido no existe, devolvemos null
            return null;
        }
    }
}
