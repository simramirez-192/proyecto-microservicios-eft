package com.example.ms_envio.controller;

import com.example.ms_envio.dto.EnvioRequestDTO;
import com.example.ms_envio.dto.EnvioResponseDTO;
import com.example.ms_envio.exception.GlobalExceptionHandler;
import com.example.ms_envio.service.EnvioService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EnvioControllerTest {

    @Mock
    private EnvioService envioService;

    @InjectMocks
    private EnvioController envioController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private EnvioResponseDTO responseDTO;
    private EnvioRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(envioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        responseDTO = new EnvioResponseDTO(1L, 10L, "Pedido #10", "Calle Principal 123", "PENDIENTE", LocalDateTime.now());
        requestDTO = new EnvioRequestDTO();
        requestDTO.setPedidoId(10L);
        requestDTO.setDireccion("Calle Principal 123");
    }

    @Test
    void listarEnvios_retornaLista200() throws Exception {
        when(envioService.listarEnvios()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/envios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombrePedido").value("Pedido #10"))
                .andExpect(jsonPath("$[0].direccion").value("Calle Principal 123"));

        verify(envioService).listarEnvios();
    }

    @Test
    void listarEnvios_listaVacia_retorna200() throws Exception {
        when(envioService.listarEnvios()).thenReturn(List.of());

        mockMvc.perform(get("/api/envios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void buscarPorId_envioExistente_retorna200() throws Exception {
        when(envioService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/envios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombrePedido").value("Pedido #10"));

        verify(envioService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_envioNoExistente_retorna404() throws Exception {
        when(envioService.buscarPorId(99L))
                .thenThrow(new RuntimeException("Envio no encontrado con id: 99"));

        mockMvc.perform(get("/api/envios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Envio no encontrado con id: 99"));
    }

    @Test
    void crearEnvio_datosValidos_retorna201() throws Exception {
        when(envioService.crearEnvio(any(EnvioRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombrePedido").value("Pedido #10"));

        verify(envioService).crearEnvio(any(EnvioRequestDTO.class));
    }

    @Test
    void crearEnvio_pedidoNoExiste_retorna404() throws Exception {
        when(envioService.crearEnvio(any(EnvioRequestDTO.class)))
                .thenThrow(new RuntimeException("El pedido con id 10 no existe en ms-pedido"));

        mockMvc.perform(post("/api/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El pedido con id 10 no existe en ms-pedido"));
    }

    @Test
    void crearEnvio_datosInvalidos_retorna400() throws Exception {
        EnvioRequestDTO invalidDTO = new EnvioRequestDTO();

        mockMvc.perform(post("/api/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarEnvio_datosValidos_retorna200() throws Exception {
        when(envioService.actualizarEnvio(eq(1L), any(EnvioRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/envios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombrePedido").value("Pedido #10"));

        verify(envioService).actualizarEnvio(eq(1L), any(EnvioRequestDTO.class));
    }

    @Test
    void actualizarEnvio_envioNoExiste_retorna404() throws Exception {
        when(envioService.actualizarEnvio(eq(99L), any(EnvioRequestDTO.class)))
                .thenThrow(new RuntimeException("Envio no encontrado con id: 99"));

        mockMvc.perform(put("/api/envios/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void eliminarEnvio_envioExistente_retorna204() throws Exception {
        doNothing().when(envioService).eliminarEnvio(1L);

        mockMvc.perform(delete("/api/envios/1"))
                .andExpect(status().isNoContent());

        verify(envioService).eliminarEnvio(1L);
    }

    @Test
    void eliminarEnvio_envioNoExiste_retorna404() throws Exception {
        doThrow(new RuntimeException("Envio no encontrado con id: 99"))
                .when(envioService).eliminarEnvio(99L);

        mockMvc.perform(delete("/api/envios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
