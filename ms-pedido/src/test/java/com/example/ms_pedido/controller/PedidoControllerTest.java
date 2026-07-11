package com.example.ms_pedido.controller;

import com.example.ms_pedido.dto.PedidoRequestDTO;
import com.example.ms_pedido.dto.PedidoResponseDTO;
import com.example.ms_pedido.exception.GlobalExceptionHandler;
import com.example.ms_pedido.service.PedidoService;
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
class PedidoControllerTest {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private PedidoResponseDTO responseDTO;
    private PedidoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pedidoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        responseDTO = new PedidoResponseDTO(1L, 5L, "Ana Torres", 10L, "Mouse Gamer", 2, 31980.0, "PENDIENTE");
        requestDTO = new PedidoRequestDTO();
        requestDTO.setClienteId(5L);
        requestDTO.setProductoId(10L);
        requestDTO.setCantidad(2);
    }

    @Test
    void listarPedidos_retornaLista200() throws Exception {
        when(pedidoService.listarPedidos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCliente").value("Ana Torres"))
                .andExpect(jsonPath("$[0].nombreProducto").value("Mouse Gamer"))
                .andExpect(jsonPath("$[0].total").value(31980.0));

        verify(pedidoService).listarPedidos();
    }

    @Test
    void listarPedidos_listaVacia_retorna200() throws Exception {
        when(pedidoService.listarPedidos()).thenReturn(List.of());

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void listarPedidos_multiplesPedidos_retornaTodos() throws Exception {
        PedidoResponseDTO segundo = new PedidoResponseDTO(2L, 6L, "Carlos Lopez", 11L, "Teclado Mecanico", 1, 24990.0, "CONFIRMADO");
        when(pedidoService.listarPedidos()).thenReturn(List.of(responseDTO, segundo));

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].nombreCliente").value("Carlos Lopez"));
    }

    @Test
    void buscarPorId_pedidoExistente_retorna200() throws Exception {
        when(pedidoService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreCliente").value("Ana Torres"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(pedidoService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_pedidoNoExistente_retorna404() throws Exception {
        when(pedidoService.buscarPorId(99L))
                .thenThrow(new RuntimeException("Pedido no encontrado con id: 99"));

        mockMvc.perform(get("/api/pedidos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Pedido no encontrado con id: 99"));
    }

    @Test
    void crearPedido_datosValidos_retorna201() throws Exception {
        when(pedidoService.crearPedido(any(PedidoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreCliente").value("Ana Torres"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.total").value(31980.0));

        verify(pedidoService).crearPedido(any(PedidoRequestDTO.class));
    }

    @Test
    void crearPedido_clienteNoExiste_retorna404() throws Exception {
        when(pedidoService.crearPedido(any(PedidoRequestDTO.class)))
                .thenThrow(new RuntimeException("El cliente con id 5 no existe en ms-cliente"));

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El cliente con id 5 no existe en ms-cliente"));
    }

    @Test
    void crearPedido_productoNoExiste_retorna404() throws Exception {
        when(pedidoService.crearPedido(any(PedidoRequestDTO.class)))
                .thenThrow(new RuntimeException("El producto con id 10 no existe en ms-producto"));

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El producto con id 10 no existe en ms-producto"));
    }

    @Test
    void actualizarPedido_datosValidos_retorna200() throws Exception {
        when(pedidoService.actualizarPedido(eq(1L), any(PedidoRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/pedidos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCliente").value("Ana Torres"))
                .andExpect(jsonPath("$.total").value(31980.0));

        verify(pedidoService).actualizarPedido(eq(1L), any(PedidoRequestDTO.class));
    }

    @Test
    void actualizarPedido_pedidoNoExiste_retorna404() throws Exception {
        when(pedidoService.actualizarPedido(eq(99L), any(PedidoRequestDTO.class)))
                .thenThrow(new RuntimeException("Pedido no encontrado con id: 99"));

        mockMvc.perform(put("/api/pedidos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void actualizarPedido_clienteNoExiste_retorna404() throws Exception {
        when(pedidoService.actualizarPedido(eq(1L), any(PedidoRequestDTO.class)))
                .thenThrow(new RuntimeException("El cliente con id 5 no existe en ms-cliente"));

        mockMvc.perform(put("/api/pedidos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El cliente con id 5 no existe en ms-cliente"));
    }

    @Test
    void eliminarPedido_pedidoExistente_retorna204() throws Exception {
        doNothing().when(pedidoService).eliminarPedido(1L);

        mockMvc.perform(delete("/api/pedidos/1"))
                .andExpect(status().isNoContent());

        verify(pedidoService).eliminarPedido(1L);
    }

    @Test
    void eliminarPedido_pedidoNoExiste_retorna404() throws Exception {
        doThrow(new RuntimeException("Pedido no encontrado con id: 99"))
                .when(pedidoService).eliminarPedido(99L);

        mockMvc.perform(delete("/api/pedidos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
