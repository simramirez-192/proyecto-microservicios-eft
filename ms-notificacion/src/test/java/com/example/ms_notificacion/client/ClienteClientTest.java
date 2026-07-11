package com.example.ms_notificacion.client;

import com.example.ms_notificacion.dto.ClienteDTO;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteClientTest {

    @Mock
    private WebClient webClient;

    @InjectMocks
    private ClienteClient clienteClient;

    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(clienteClient, "clienteUrl", "http://localhost:8082/api/clientes");

        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);
    }

    @Test
    void obtenerClientePorId_clienteExiste_retornaClienteDTO() {
        ClienteDTO clienteEsperado = new ClienteDTO(10L, "Juan Perez", "juan@email.com", "123456789", "Calle Falsa 123");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq(10L))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ClienteDTO.class)).thenReturn(Mono.just(clienteEsperado));

        ClienteDTO resultado = clienteClient.obtenerClientePorId(10L);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("Juan Perez", resultado.getNombre());
        assertEquals("juan@email.com", resultado.getEmail());
        verify(webClient).get();
    }

    @Test
    void obtenerClientePorId_clienteNoExiste_retornaNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq(99L))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ClienteDTO.class)).thenReturn(Mono.empty());

        ClienteDTO resultado = clienteClient.obtenerClientePorId(99L);

        assertNull(resultado);
    }

    @Test
    void obtenerClientePorId_excepcionEnWebClient_retornaNull() {
        when(webClient.get()).thenThrow(new RuntimeException("Connection refused"));

        ClienteDTO resultado = clienteClient.obtenerClientePorId(10L);

        assertNull(resultado);
    }

    @Test
    void obtenerClientePorId_clienteIdDiferente_consultaCorrecta() {
        ClienteDTO clienteEsperado = new ClienteDTO(5L, "Maria Lopez", "maria@email.com", "987654321", "Av. Principal 456");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq(5L))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ClienteDTO.class)).thenReturn(Mono.just(clienteEsperado));

        ClienteDTO resultado = clienteClient.obtenerClientePorId(5L);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
        assertEquals("Maria Lopez", resultado.getNombre());
    }
}
