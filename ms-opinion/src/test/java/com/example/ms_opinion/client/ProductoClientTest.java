package com.example.ms_opinion.client;

import com.example.ms_opinion.dto.ProductoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoClientTest {

    @Mock
    private WebClient webClient;

    @InjectMocks
    private ProductoClient productoClient;

    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(productoClient, "productoUrl", "http://localhost:8082/api/productos");

        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);
    }

    @Test
    void obtenerProductoPorId_productoExistente_retornaProducto() {
        ProductoDTO productoEsperado = new ProductoDTO(10L, "Mouse Gamer", "Mouse inalambrico", 15990.0, 50);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductoDTO.class)).thenReturn(Mono.just(productoEsperado));

        ProductoDTO resultado = productoClient.obtenerProductoPorId(10L);

        assertNotNull(resultado);
        assertEquals("Mouse Gamer", resultado.getNombre());
        assertEquals(15990.0, resultado.getPrecio());
    }

    @Test
    void obtenerProductoPorId_productoNoExistente_retornaNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductoDTO.class)).thenReturn(Mono.empty());

        ProductoDTO resultado = productoClient.obtenerProductoPorId(99L);

        assertNull(resultado);
    }

    @Test
    void obtenerProductoPorId_excepcion_retornaNull() {
        when(webClient.get()).thenThrow(new RuntimeException("Conexion fallida"));

        ProductoDTO resultado = productoClient.obtenerProductoPorId(1L);

        assertNull(resultado);
    }

    @Test
    void obtenerProductoPorId_urlEsCorrecta() {
        ProductoDTO productoEsperado = new ProductoDTO(5L, "Teclado Mecanico", "RGB", 45000.0, 20);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("http://localhost:8082/api/productos/{id}", 5L)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductoDTO.class)).thenReturn(Mono.just(productoEsperado));

        ProductoDTO resultado = productoClient.obtenerProductoPorId(5L);

        assertNotNull(resultado);
        assertEquals("Teclado Mecanico", resultado.getNombre());
        verify(requestHeadersUriSpec).uri("http://localhost:8082/api/productos/{id}", 5L);
    }

    @Test
    void obtenerProductoPorId_WebClientError_retornaNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductoDTO.class))
                .thenReturn(Mono.error(new RuntimeException("500 Internal Server Error")));

        ProductoDTO resultado = productoClient.obtenerProductoPorId(1L);

        assertNull(resultado);
    }
}
