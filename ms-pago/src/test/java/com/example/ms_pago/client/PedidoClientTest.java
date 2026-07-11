package com.example.ms_pago.client;

import com.example.ms_pago.dto.PedidoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
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

    @InjectMocks
    private PedidoClient pedidoClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pedidoClient, "pedidoUrl", "http://ms-pedido:8083/api/pedidos");
    }

    @Test
    void obtenerPedidoPorId_pedidoExiste_retornaPedidoDTO() {
        PedidoDTO pedidoEsperado = new PedidoDTO(1L, 5L, "Ana Torres", 10L, "Mouse Gamer", 2, 31980.0, "PENDIENTE");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PedidoDTO.class)).thenReturn(Mono.just(pedidoEsperado));

        PedidoDTO resultado = pedidoClient.obtenerPedidoPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Ana Torres", resultado.getNombreCliente());
        assertEquals(31980.0, resultado.getTotal());
        verify(webClient).get();
    }

    @Test
    void obtenerPedidoPorId_pedidoNoExiste_retornaNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PedidoDTO.class)).thenReturn(Mono.empty());

        PedidoDTO resultado = pedidoClient.obtenerPedidoPorId(99L);

        assertNull(resultado);
        verify(webClient).get();
    }

    @Test
    void obtenerPedidoPorId_errorDeRed_retornaNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PedidoDTO.class))
                .thenReturn(Mono.error(new RuntimeException("Connection refused")));

        PedidoDTO resultado = pedidoClient.obtenerPedidoPorId(1L);

        assertNull(resultado);
    }

    @Test
    void obtenerPedidoPorId_requestException_retornaNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PedidoDTO.class))
                .thenReturn(Mono.error(mock(WebClientRequestException.class)));

        PedidoDTO resultado = pedidoClient.obtenerPedidoPorId(1L);

        assertNull(resultado);
    }

    @Test
    void obtenerPedidoPorId_timeout_retornaNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PedidoDTO.class))
                .thenReturn(Mono.error(new RuntimeException("Request timeout")));

        PedidoDTO resultado = pedidoClient.obtenerPedidoPorId(1L);

        assertNull(resultado);
        verify(webClient).get();
    }
}
