package com.example.ms_inventario.controller;

import com.example.ms_inventario.dto.InventarioRequestDTO;
import com.example.ms_inventario.dto.InventarioResponseDTO;
import com.example.ms_inventario.exception.GlobalExceptionHandler;
import com.example.ms_inventario.service.InventarioService;
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
class InventarioControllerTest {

    @Mock
    private InventarioService inventarioService;

    @InjectMocks
    private InventarioController inventarioController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private InventarioResponseDTO responseDTO;
    private InventarioRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(inventarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        responseDTO = new InventarioResponseDTO(1L, 10L, "Mouse Gamer", 30);
        requestDTO = new InventarioRequestDTO();
        requestDTO.setProductoId(10L);
        requestDTO.setCantidadDisponible(30);
    }

    @Test
    void listarInventario_retornaLista200() throws Exception {
        when(inventarioService.listarInventario()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreProducto").value("Mouse Gamer"))
                .andExpect(jsonPath("$[0].cantidadDisponible").value(30));

        verify(inventarioService).listarInventario();
    }

    @Test
    void listarInventario_listaVacia_retorna200() throws Exception {
        when(inventarioService.listarInventario()).thenReturn(List.of());

        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void buscarPorId_inventarioExistente_retorna200() throws Exception {
        when(inventarioService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/inventario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreProducto").value("Mouse Gamer"));

        verify(inventarioService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_inventarioNoExistente_retorna404() throws Exception {
        when(inventarioService.buscarPorId(99L))
                .thenThrow(new RuntimeException("Registro de inventario no encontrado con id: 99"));

        mockMvc.perform(get("/api/inventario/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Registro de inventario no encontrado con id: 99"));
    }

    @Test
    void crearInventario_datosValidos_retorna201() throws Exception {
        when(inventarioService.crearInventario(any(InventarioRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreProducto").value("Mouse Gamer"));

        verify(inventarioService).crearInventario(any(InventarioRequestDTO.class));
    }

    @Test
    void crearInventario_productoNoExiste_retorna404() throws Exception {
        when(inventarioService.crearInventario(any(InventarioRequestDTO.class)))
                .thenThrow(new RuntimeException("El producto con id 10 no existe en ms-producto"));

        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El producto con id 10 no existe en ms-producto"));
    }

    @Test
    void actualizarInventario_datosValidos_retorna200() throws Exception {
        when(inventarioService.actualizarInventario(eq(1L), any(InventarioRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/inventario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreProducto").value("Mouse Gamer"));

        verify(inventarioService).actualizarInventario(eq(1L), any(InventarioRequestDTO.class));
    }

    @Test
    void actualizarInventario_inventarioNoExiste_retorna404() throws Exception {
        when(inventarioService.actualizarInventario(eq(99L), any(InventarioRequestDTO.class)))
                .thenThrow(new RuntimeException("Registro de inventario no encontrado con id: 99"));

        mockMvc.perform(put("/api/inventario/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void eliminarInventario_inventarioExistente_retorna204() throws Exception {
        doNothing().when(inventarioService).eliminarInventario(1L);

        mockMvc.perform(delete("/api/inventario/1"))
                .andExpect(status().isNoContent());

        verify(inventarioService).eliminarInventario(1L);
    }

    @Test
    void eliminarInventario_inventarioNoExiste_retorna404() throws Exception {
        doThrow(new RuntimeException("Registro de inventario no encontrado con id: 99"))
                .when(inventarioService).eliminarInventario(99L);

        mockMvc.perform(delete("/api/inventario/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
