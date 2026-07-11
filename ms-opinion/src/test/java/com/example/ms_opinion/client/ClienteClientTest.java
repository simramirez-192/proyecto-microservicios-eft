package com.example.ms_opinion.client;

import com.example.ms_opinion.dto.ClienteDTO;
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
class ClienteClientTest {

    @Mock
    private WebClient webClient;

    @InjectMocks
    private ClienteClient clienteClient;

    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(clienteClient, "clienteUrl", "http://localhost:8081/api/clientes");

        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);
    }

    @Test
    void obtenerClientePorId_clienteExistente_retornaCliente() {
        ClienteDTO clienteEsperado = new ClienteDTO(1L, "Juan Perez", "juan@mail.com", "123456789", "Calle 123");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ClienteDTO.class)).thenReturn(Mono.just(clienteEsperado));

        ClienteDTO resultado = clienteClient.obtenerClientePorId(1L);

        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getNombre());
        assertEquals("juan@mail.com", resultado.getEmail());
    }

    @Test
    void obtenerClientePorId_clienteNoExistente_retornaNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ClienteDTO.class)).thenReturn(Mono.empty());

        ClienteDTO resultado = clienteClient.obtenerClientePorId(99L);

        assertNull(resultado);
    }

    @Test
    void obtenerClientePorId_excepcion_retornaNull() {
        when(webClient.get()).thenThrow(new RuntimeException("Conexion fallida"));

        ClienteDTO resultado = clienteClient.obtenerClientePorId(1L);

        assertNull(resultado);
    }

    @Test
    void obtenerClientePorId_urlEsCorrecta() {
        ClienteDTO clienteEsperado = new ClienteDTO(1L, "Ana", "ana@mail.com", "99999", "Av. Principal");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("http://localhost:8081/api/clientes/{id}", 1L)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ClienteDTO.class)).thenReturn(Mono.just(clienteEsperado));

        ClienteDTO resultado = clienteClient.obtenerClientePorId(1L);

        assertNotNull(resultado);
        assertEquals("Ana", resultado.getNombre());
        verify(requestHeadersUriSpec).uri("http://localhost:8081/api/clientes/{id}", 1L);
    }

    @Test
    void obtenerClientePorId_WebClientError_retornaNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ClienteDTO.class))
                .thenReturn(Mono.error(new RuntimeException("404 Not Found")));

        ClienteDTO resultado = clienteClient.obtenerClientePorId(1L);

        assertNull(resultado);
    }
}
