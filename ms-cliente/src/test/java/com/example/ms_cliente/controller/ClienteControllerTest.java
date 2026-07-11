package com.example.ms_cliente.controller;

import com.example.ms_cliente.dto.ClienteRequestDTO;
import com.example.ms_cliente.dto.ClienteResponseDTO;
import com.example.ms_cliente.exception.GlobalExceptionHandler;
import com.example.ms_cliente.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ClienteResponseDTO responseDTO;
    private ClienteRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        responseDTO = new ClienteResponseDTO(1L, "Ana Torres", "ana@email.com", "+56911112222", "Av. Siempre Viva 123");
        requestDTO = new ClienteRequestDTO();
        requestDTO.setNombre("Ana Torres");
        requestDTO.setEmail("ana@email.com");
        requestDTO.setTelefono("+56911112222");
        requestDTO.setDireccion("Av. Siempre Viva 123");
    }

    @Test
    void listarClientes_retornaLista200() throws Exception {
        when(clienteService.listarClientes()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Ana Torres"))
                .andExpect(jsonPath("$[0].email").value("ana@email.com"));

        verify(clienteService).listarClientes();
    }

    @Test
    void listarClientes_listaVacia_retorna200() throws Exception {
        when(clienteService.listarClientes()).thenReturn(List.of());

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void buscarPorId_clienteExistente_retorna200() throws Exception {
        when(clienteService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ana Torres"));

        verify(clienteService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_clienteNoExistente_retorna404() throws Exception {
        when(clienteService.buscarPorId(99L))
                .thenThrow(new RuntimeException("Cliente no encontrado con id: 99"));

        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Cliente no encontrado con id: 99"));
    }

    @Test
    void crearCliente_datosValidos_retorna201() throws Exception {
        when(clienteService.crearCliente(any(ClienteRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Ana Torres"));

        verify(clienteService).crearCliente(any(ClienteRequestDTO.class));
    }

    @Test
    void crearCliente_datosInvalidos_retorna400() throws Exception {
        ClienteRequestDTO invalido = new ClienteRequestDTO();
        invalido.setNombre("");
        invalido.setEmail("no-es-email");

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());

        verify(clienteService, never()).crearCliente(any());
    }

    @Test
    void crearCliente_nombreBlanco_retorna400() throws Exception {
        ClienteRequestDTO sinNombre = new ClienteRequestDTO();
        sinNombre.setNombre("");
        sinNombre.setEmail("test@email.com");

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sinNombre)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearCliente_emailInvalido_retorna400() throws Exception {
        ClienteRequestDTO emailInvalido = new ClienteRequestDTO();
        emailInvalido.setNombre("Test");
        emailInvalido.setEmail("email-invalido");

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarCliente_datosValidos_retorna200() throws Exception {
        when(clienteService.actualizarCliente(eq(1L), any(ClienteRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana Torres"));

        verify(clienteService).actualizarCliente(eq(1L), any(ClienteRequestDTO.class));
    }

    @Test
    void actualizarCliente_clienteNoExiste_retorna404() throws Exception {
        when(clienteService.actualizarCliente(eq(99L), any(ClienteRequestDTO.class)))
                .thenThrow(new RuntimeException("Cliente no encontrado con id: 99"));

        mockMvc.perform(put("/api/clientes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void eliminarCliente_clienteExistente_retorna204() throws Exception {
        doNothing().when(clienteService).eliminarCliente(1L);

        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent());

        verify(clienteService).eliminarCliente(1L);
    }

    @Test
    void eliminarCliente_clienteNoExiste_retorna404() throws Exception {
        doThrow(new RuntimeException("Cliente no encontrado con id: 99"))
                .when(clienteService).eliminarCliente(99L);

        mockMvc.perform(delete("/api/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void buscarPorId_clienteNoEncontrado_retornaMensajeCorrecto() throws Exception {
        when(clienteService.buscarPorId(50L))
                .thenThrow(new RuntimeException("Cliente no encontrado con id: 50"));

        mockMvc.perform(get("/api/clientes/50"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Cliente no encontrado con id: 50"))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void listarClientes_multiplesClientes_retornaTodos() throws Exception {
        ClienteResponseDTO cliente2 = new ClienteResponseDTO(2L, "Bob Smith", "bob@email.com", "+56933334444", "Calle Falsa 456");
        when(clienteService.listarClientes()).thenReturn(List.of(responseDTO, cliente2));

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Ana Torres"))
                .andExpect(jsonPath("$[1].nombre").value("Bob Smith"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
