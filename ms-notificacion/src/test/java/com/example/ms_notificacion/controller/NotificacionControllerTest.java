package com.example.ms_notificacion.controller;

import com.example.ms_notificacion.dto.NotificacionRequestDTO;
import com.example.ms_notificacion.dto.NotificacionResponseDTO;
import com.example.ms_notificacion.exception.GlobalExceptionHandler;
import com.example.ms_notificacion.service.NotificacionService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificacionControllerTest {

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private NotificacionController notificacionController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private NotificacionResponseDTO responseDTO;
    private NotificacionRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificacionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        responseDTO = new NotificacionResponseDTO(1L, 10L, "Juan Perez",
                "Su pedido ha sido enviado", "EMAIL", false, LocalDateTime.now());
        requestDTO = new NotificacionRequestDTO();
        requestDTO.setClienteId(10L);
        requestDTO.setMensaje("Su pedido ha sido enviado");
        requestDTO.setTipo("EMAIL");
    }

    @Test
    void listarNotificaciones_retornaLista200() throws Exception {
        when(notificacionService.listarNotificaciones()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mensaje").value("Su pedido ha sido enviado"))
                .andExpect(jsonPath("$[0].nombreCliente").value("Juan Perez"));

        verify(notificacionService).listarNotificaciones();
    }

    @Test
    void listarNotificaciones_listaVacia_retorna200() throws Exception {
        when(notificacionService.listarNotificaciones()).thenReturn(List.of());

        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void buscarPorId_notificacionExistente_retorna200() throws Exception {
        when(notificacionService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/notificaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreCliente").value("Juan Perez"));

        verify(notificacionService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_notificacionNoExistente_retorna400() throws Exception {
        when(notificacionService.buscarPorId(99L))
                .thenThrow(new RuntimeException("Notificacion no encontrada con id: 99"));

        mockMvc.perform(get("/api/notificaciones/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value("Notificacion no encontrada con id: 99"));
    }

    @Test
    void crearNotificacion_datosValidos_retorna201() throws Exception {
        when(notificacionService.crearNotificacion(any(NotificacionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreCliente").value("Juan Perez"));

        verify(notificacionService).crearNotificacion(any(NotificacionRequestDTO.class));
    }

    @Test
    void crearNotificacion_clienteNoExiste_retorna404() throws Exception {
        when(notificacionService.crearNotificacion(any(NotificacionRequestDTO.class)))
                .thenThrow(new RuntimeException("El cliente con id 10 no existe en ms-cliente"));

        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El cliente con id 10 no existe en ms-cliente"));
    }

    @Test
    void crearNotificacion_camposInvalidos_retorna400() throws Exception {
        NotificacionRequestDTO dtoInvalido = new NotificacionRequestDTO();

        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void marcarComoLeida_notificacionExistente_retorna200() throws Exception {
        NotificacionResponseDTO leida = new NotificacionResponseDTO(1L, 10L, "Juan Perez",
                "Su pedido ha sido enviado", "EMAIL", true, LocalDateTime.now());
        when(notificacionService.marcarComoLeida(1L)).thenReturn(leida);

        mockMvc.perform(patch("/api/notificaciones/1/leer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leida").value(true));

        verify(notificacionService).marcarComoLeida(1L);
    }

    @Test
    void marcarComoLeida_notificacionNoExistente_retorna400() throws Exception {
        when(notificacionService.marcarComoLeida(99L))
                .thenThrow(new RuntimeException("Notificacion no encontrada con id: 99"));

        mockMvc.perform(patch("/api/notificaciones/99/leer"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void eliminarNotificacion_notificacionExistente_retorna204() throws Exception {
        doNothing().when(notificacionService).eliminarNotificacion(1L);

        mockMvc.perform(delete("/api/notificaciones/1"))
                .andExpect(status().isNoContent());

        verify(notificacionService).eliminarNotificacion(1L);
    }

    @Test
    void eliminarNotificacion_notificacionNoExistente_retorna400() throws Exception {
        doThrow(new RuntimeException("Notificacion no encontrada con id: 99"))
                .when(notificacionService).eliminarNotificacion(99L);

        mockMvc.perform(delete("/api/notificaciones/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
