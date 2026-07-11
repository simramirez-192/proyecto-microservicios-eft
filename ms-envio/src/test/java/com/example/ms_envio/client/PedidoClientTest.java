package com.example.ms_envio.client;

import com.example.ms_envio.dto.PedidoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private PedidoClient pedidoClient;

    @BeforeEach
    void setUp() {
        pedidoClient = new PedidoClient(webClient);
        ReflectionTestUtils.setField(pedidoClient, "pedidoUrl", "http://localhost:8085/api/pedidos");
    }

    @Test
    void obtenerPedidoPorId_pedidoExiste_retornaPedidoDTO() {
        PedidoDTO pedidoEsperado = new PedidoDTO(10L, 1L, 5L, 2, 31980.0, "CONFIRMADO");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PedidoDTO.class)).thenReturn(Mono.just(pedidoEsperado));

        PedidoDTO resultado = pedidoClient.obtenerPedidoPorId(10L);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals(1L, resultado.getClienteId());
        assertEquals("CONFIRMADO", resultado.getEstado());
    }

    @Test
    void obtenerPedidoPorId_pedidoNoExiste_retornaNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PedidoDTO.class)).thenReturn(Mono.empty());

        PedidoDTO resultado = pedidoClient.obtenerPedidoPorId(99L);

        assertNull(resultado);
    }

    @Test
    void obtenerPedidoPorId_errorEnConexion_retornaNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PedidoDTO.class))
                .thenReturn(Mono.error(new RuntimeException("Connection refused")));

        PedidoDTO resultado = pedidoClient.obtenerPedidoPorId(10L);

        assertNull(resultado);
    }

    @Test
    void obtenerPedidoPorId_verificaUriCorrecta() {
        PedidoDTO pedidoEsperado = new PedidoDTO(5L, 2L, 3L, 1, 15990.0, "PENDIENTE");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("http://localhost:8085/api/pedidos/{id}", 5L))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PedidoDTO.class)).thenReturn(Mono.just(pedidoEsperado));

        PedidoDTO resultado = pedidoClient.obtenerPedidoPorId(5L);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri("http://localhost:8085/api/pedidos/{id}", 5L);
    }
}
