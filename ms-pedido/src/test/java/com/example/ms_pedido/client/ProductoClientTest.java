package com.example.ms_pedido.client;

import com.example.ms_pedido.dto.ProductoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoClientTest {

    @Mock
    private WebClient webClient;

    @InjectMocks
    private ProductoClient productoClient;

    @SuppressWarnings("unchecked")
    private final WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    private final WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
    private final WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    private ProductoDTO productoDTO;

    @BeforeEach
    void setUp() {
        productoDTO = new ProductoDTO(1L, "Mouse Gamer", "Mouse inalambrico", 15990.0, 50);
    }

    @Test
    void obtenerProductoPorId_productoExistente_retornaProducto() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductoDTO.class)).thenReturn(Mono.just(productoDTO));

        ProductoDTO resultado = productoClient.obtenerProductoPorId(1L);

        assertNotNull(resultado);
        assertEquals("Mouse Gamer", resultado.getNombre());
        assertEquals("Mouse inalambrico", resultado.getDescripcion());
        assertEquals(15990.0, resultado.getPrecio());
        assertEquals(50, resultado.getStock());
    }

    @Test
    void obtenerProductoPorId_productoNoExiste_retornaNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductoDTO.class)).thenReturn(Mono.empty());

        ProductoDTO resultado = productoClient.obtenerProductoPorId(99L);

        assertNull(resultado);
    }

    @Test
    void obtenerProductoPorId_errorDeRed_retornaNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductoDTO.class))
                .thenReturn(Mono.error(new RuntimeException("Timeout")));

        ProductoDTO resultado = productoClient.obtenerProductoPorId(1L);

        assertNull(resultado);
    }
}
