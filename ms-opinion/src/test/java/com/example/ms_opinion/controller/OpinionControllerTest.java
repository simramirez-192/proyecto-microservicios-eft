package com.example.ms_opinion.controller;

import com.example.ms_opinion.dto.OpinionRequestDTO;
import com.example.ms_opinion.dto.OpinionResponseDTO;
import com.example.ms_opinion.exception.GlobalExceptionHandler;
import com.example.ms_opinion.service.OpinionService;
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
class OpinionControllerTest {

    @Mock
    private OpinionService opinionService;

    @InjectMocks
    private OpinionController opinionController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private OpinionResponseDTO responseDTO;
    private OpinionRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(opinionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        responseDTO = new OpinionResponseDTO(1L, 1L, "Juan Perez", 10L, "Mouse Gamer", 5, "Excelente producto");
        requestDTO = new OpinionRequestDTO();
        requestDTO.setClienteId(1L);
        requestDTO.setProductoId(10L);
        requestDTO.setPuntuacion(5);
        requestDTO.setComentario("Excelente producto");
    }

    @Test
    void listarOpiniones_retornaLista200() throws Exception {
        when(opinionService.listarOpiniones()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/opiniones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCliente").value("Juan Perez"))
                .andExpect(jsonPath("$[0].nombreProducto").value("Mouse Gamer"))
                .andExpect(jsonPath("$[0].puntuacion").value(5));

        verify(opinionService).listarOpiniones();
    }

    @Test
    void listarOpiniones_listaVacia_retorna200() throws Exception {
        when(opinionService.listarOpiniones()).thenReturn(List.of());

        mockMvc.perform(get("/api/opiniones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void buscarPorId_opinionExistente_retorna200() throws Exception {
        when(opinionService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/opiniones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreCliente").value("Juan Perez"));

        verify(opinionService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_opinionNoExistente_retorna400() throws Exception {
        when(opinionService.buscarPorId(99L))
                .thenThrow(new RuntimeException("Opinion no encontrada con id: 99"));

        mockMvc.perform(get("/api/opiniones/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value("Opinion no encontrada con id: 99"));
    }

    @Test
    void crearOpinion_datosValidos_retorna201() throws Exception {
        when(opinionService.crearOpinion(any(OpinionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/opiniones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreCliente").value("Juan Perez"));

        verify(opinionService).crearOpinion(any(OpinionRequestDTO.class));
    }

    @Test
    void crearOpinion_clienteNoExiste_retorna404() throws Exception {
        when(opinionService.crearOpinion(any(OpinionRequestDTO.class)))
                .thenThrow(new RuntimeException("El cliente con id 1 no existe en ms-cliente"));

        mockMvc.perform(post("/api/opiniones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El cliente con id 1 no existe en ms-cliente"));
    }

    @Test
    void crearOpinion_datosInvalidos_retorna400() throws Exception {
        OpinionRequestDTO invalido = new OpinionRequestDTO();
        invalido.setClienteId(null);

        mockMvc.perform(post("/api/opiniones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarOpinion_datosValidos_retorna200() throws Exception {
        when(opinionService.actualizarOpinion(eq(1L), any(OpinionRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/opiniones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCliente").value("Juan Perez"));

        verify(opinionService).actualizarOpinion(eq(1L), any(OpinionRequestDTO.class));
    }

    @Test
    void actualizarOpinion_opinionNoExiste_retorna400() throws Exception {
        when(opinionService.actualizarOpinion(eq(99L), any(OpinionRequestDTO.class)))
                .thenThrow(new RuntimeException("Opinion no encontrada con id: 99"));

        mockMvc.perform(put("/api/opiniones/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void eliminarOpinion_opinionExistente_retorna204() throws Exception {
        doNothing().when(opinionService).eliminarOpinion(1L);

        mockMvc.perform(delete("/api/opiniones/1"))
                .andExpect(status().isNoContent());

        verify(opinionService).eliminarOpinion(1L);
    }

    @Test
    void eliminarOpinion_opinionNoExiste_retorna400() throws Exception {
        doThrow(new RuntimeException("Opinion no encontrada con id: 99"))
                .when(opinionService).eliminarOpinion(99L);

        mockMvc.perform(delete("/api/opiniones/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
