package com.example.ms_pago.client;

import com.example.ms_pago.dto.PedidoDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class PedidoClient {

    private static final Logger logger = LoggerFactory.getLogger(PedidoClient.class);
    private final WebClient webClient;

    @Value("${ms.pedido.url}")
    private String pedidoUrl;

    public PedidoDTO obtenerPedidoPorId(Long pedidoId) {
        try {
            PedidoDTO pedido = webClient.get()
                    .uri(pedidoUrl + "/{id}", pedidoId)
                    .retrieve()
                    .bodyToMono(PedidoDTO.class)
                    .block();
            logger.info("Pedido obtenido exitosamente: id={}", pedidoId);
            return pedido;
        } catch (Exception e) {
            logger.warn("No se pudo obtener el pedido con id={}: {}", pedidoId, e.getMessage());
            return null;
        }
    }
}
