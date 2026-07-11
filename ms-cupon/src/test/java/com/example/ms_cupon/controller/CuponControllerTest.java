package com.example.ms_cupon.controller;

import com.example.ms_cupon.dto.CuponRequestDTO;
import com.example.ms_cupon.dto.CuponResponseDTO;
import com.example.ms_cupon.exception.GlobalExceptionHandler;
import com.example.ms_cupon.service.CuponService;
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
class CuponControllerTest {

    @Mock
    private CuponService cuponService;

    @InjectMocks
    private CuponController cuponController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private CuponResponseDTO responseDTO;
    private CuponRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cuponController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        responseDTO = new CuponResponseDTO(1L, "DESCUENTO10", 10.0, true);
        requestDTO = new CuponRequestDTO();
        requestDTO.setCodigo("DESCUENTO10");
        requestDTO.setPorcentajeDescuento(10.0);
    }

    @Test
    void listarCupones_retornaLista200() throws Exception {
        when(cuponService.listarCupones()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/cupones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("DESCUENTO10"))
                .andExpect(jsonPath("$[0].porcentajeDescuento").value(10.0));

        verify(cuponService).listarCupones();
    }

    @Test
    void listarCupones_listaVacia_retorna200() throws Exception {
        when(cuponService.listarCupones()).thenReturn(List.of());

        mockMvc.perform(get("/api/cupones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void buscarPorId_cuponExistente_retorna200() throws Exception {
        when(cuponService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/cupones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigo").value("DESCUENTO10"));

        verify(cuponService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_cuponNoExistente_retorna404() throws Exception {
        when(cuponService.buscarPorId(99L))
                .thenThrow(new RuntimeException("Cupon no encontrado con id: 99"));

        mockMvc.perform(get("/api/cupones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Cupon no encontrado con id: 99"));
    }

    @Test
    void crearCupon_datosValidos_retorna201() throws Exception {
        when(cuponService.crearCupon(any(CuponRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/cupones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("DESCUENTO10"));

        verify(cuponService).crearCupon(any(CuponRequestDTO.class));
    }

    @Test
    void crearCupon_codigoDuplicado_retorna400() throws Exception {
        when(cuponService.crearCupon(any(CuponRequestDTO.class)))
                .thenThrow(new RuntimeException("Ya existe un cupon con el codigo: DESCUENTO10"));

        mockMvc.perform(post("/api/cupones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un cupon con el codigo: DESCUENTO10"));
    }

    @Test
    void crearCupon_datosInvalidos_retorna400() throws Exception {
        CuponRequestDTO dtoInvalido = new CuponRequestDTO();
        dtoInvalido.setCodigo("");
        dtoInvalido.setPorcentajeDescuento(null);

        mockMvc.perform(post("/api/cupones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarCupon_datosValidos_retorna200() throws Exception {
        when(cuponService.actualizarCupon(eq(1L), any(CuponRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/cupones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("DESCUENTO10"));

        verify(cuponService).actualizarCupon(eq(1L), any(CuponRequestDTO.class));
    }

    @Test
    void actualizarCupon_cuponNoExiste_retorna404() throws Exception {
        when(cuponService.actualizarCupon(eq(99L), any(CuponRequestDTO.class)))
                .thenThrow(new RuntimeException("Cupon no encontrado con id: 99"));

        mockMvc.perform(put("/api/cupones/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void eliminarCupon_cuponExistente_retorna204() throws Exception {
        doNothing().when(cuponService).eliminarCupon(1L);

        mockMvc.perform(delete("/api/cupones/1"))
                .andExpect(status().isNoContent());

        verify(cuponService).eliminarCupon(1L);
    }

    @Test
    void eliminarCupon_cuponNoExiste_retorna404() throws Exception {
        doThrow(new RuntimeException("Cupon no encontrado con id: 99"))
                .when(cuponService).eliminarCupon(99L);

        mockMvc.perform(delete("/api/cupones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
