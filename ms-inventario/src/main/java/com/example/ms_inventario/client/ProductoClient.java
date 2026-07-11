package com.example.ms_inventario.client;

import com.example.ms_inventario.dto.ProductoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

// Esta clase es la unica responsable de "hablar" con ms-producto.
// Si el dia de mañana ms-producto cambia de URL, solo tocas este archivo.
@Component
@RequiredArgsConstructor
public class ProductoClient {

    private final RestTemplate restTemplate;

    @Value("${ms.producto.url}")
    private String productoUrl;

    // Llama a GET http://localhost:8080/api/productos/{id}
    public ProductoDTO obtenerProductoPorId(Long productoId) {
        try {
            return restTemplate.getForObject(productoUrl + "/" + productoId, ProductoDTO.class);
        } catch (RestClientException e) {
            // Si ms-producto no responde o el producto no existe, devolvemos null
            return null;
        }
    }
}
