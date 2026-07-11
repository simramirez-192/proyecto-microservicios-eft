package com.example.ms_inventario.client;

import com.example.ms_inventario.dto.ProductoDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ProductoClient {

    private static final Logger logger = LoggerFactory.getLogger(ProductoClient.class);
    private final WebClient webClient;

    @Value("${ms.producto.url}")
    private String productoUrl;

    public ProductoDTO obtenerProductoPorId(Long productoId) {
        try {
            ProductoDTO producto = webClient.get()
                    .uri(productoUrl + "/{id}", productoId)
                    .retrieve()
                    .bodyToMono(ProductoDTO.class)
                    .block();
            logger.info("Producto obtenido exitosamente: id={}", productoId);
            return producto;
        } catch (Exception e) {
            logger.warn("No se pudo obtener el producto con id={}: {}", productoId, e.getMessage());
            return null;
        }
    }
}
