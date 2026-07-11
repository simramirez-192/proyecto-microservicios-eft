package com.example.ms_pedido.client;

import com.example.ms_pedido.dto.ClienteDTO;
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
class ClienteClientTest {

    @Mock
    private WebClient webClient;

    @InjectMocks
    private ClienteClient clienteClient;

    @SuppressWarnings("unchecked")
    private final WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    private final WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
    private final WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        clienteDTO = new ClienteDTO(1L, "Juan Perez", "juan@email.com", "+56912345678", "Calle Falsa 123");
    }

    @Test
    void obtenerClientePorId_clienteExistente_retornaCliente() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ClienteDTO.class)).thenReturn(Mono.just(clienteDTO));

        ClienteDTO resultado = clienteClient.obtenerClientePorId(1L);

        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getNombre());
        assertEquals("juan@email.com", resultado.getEmail());
        assertEquals("+56912345678", resultado.getTelefono());
        assertEquals("Calle Falsa 123", resultado.getDireccion());
    }

    @Test
    void obtenerClientePorId_clienteNoExistente_retornaNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ClienteDTO.class)).thenReturn(Mono.empty());

        ClienteDTO resultado = clienteClient.obtenerClientePorId(99L);

        assertNull(resultado);
    }

    @Test
    void obtenerClientePorId_errorDeRed_retornaNull() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ClienteDTO.class))
                .thenReturn(Mono.error(new RuntimeException("Connection refused")));

        ClienteDTO resultado = clienteClient.obtenerClientePorId(1L);

        assertNull(resultado);
    }
}
