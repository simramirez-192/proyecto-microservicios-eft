package com.example.ms_pago.controller;

import com.example.ms_pago.dto.PagoRequestDTO;
import com.example.ms_pago.dto.PagoResponseDTO;
import com.example.ms_pago.exception.GlobalExceptionHandler;
import com.example.ms_pago.service.PagoService;
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
class PagoControllerTest {

    @Mock
    private PagoService pagoService;

    @InjectMocks
    private PagoController pagoController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private PagoResponseDTO responseDTO;
    private PagoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pagoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        responseDTO = new PagoResponseDTO(1L, 7L, 31980.0, "TARJETA", "PAGADO");
        requestDTO = new PagoRequestDTO();
        requestDTO.setPedidoId(7L);
        requestDTO.setMetodoPago("TARJETA");
    }

    @Test
    void listarPagos_retornaLista200() throws Exception {
        when(pagoService.listarPagos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].metodoPago").value("TARJETA"))
                .andExpect(jsonPath("$[0].monto").value(31980.0))
                .andExpect(jsonPath("$[0].estado").value("PAGADO"));

        verify(pagoService).listarPagos();
    }

    @Test
    void listarPagos_listaVacia_retorna200() throws Exception {
        when(pagoService.listarPagos()).thenReturn(List.of());

        mockMvc.perform(get("/api/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void buscarPorId_pagoExistente_retorna200() throws Exception {
        when(pagoService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.metodoPago").value("TARJETA"));

        verify(pagoService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_pagoNoExistente_retorna404() throws Exception {
        when(pagoService.buscarPorId(99L))
                .thenThrow(new RuntimeException("Pago no encontrado con id: 99"));

        mockMvc.perform(get("/api/pagos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Pago no encontrado con id: 99"));
    }

    @Test
    void crearPago_datosValidos_retorna201() throws Exception {
        when(pagoService.crearPago(any(PagoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.metodoPago").value("TARJETA"))
                .andExpect(jsonPath("$.estado").value("PAGADO"));

        verify(pagoService).crearPago(any(PagoRequestDTO.class));
    }

    @Test
    void crearPago_pedidoNoExiste_retorna404() throws Exception {
        when(pagoService.crearPago(any(PagoRequestDTO.class)))
                .thenThrow(new RuntimeException("El pedido con id 7 no existe en ms-pedido"));

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El pedido con id 7 no existe en ms-pedido"));
    }

    @Test
    void crearPago_datosInvalidos_retorna400() throws Exception {
        PagoRequestDTO requestInvalido = new PagoRequestDTO();
        requestInvalido.setPedidoId(null);
        requestInvalido.setMetodoPago("");

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarPago_datosValidos_retorna200() throws Exception {
        when(pagoService.actualizarPago(eq(1L), any(PagoRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/pagos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metodoPago").value("TARJETA"));

        verify(pagoService).actualizarPago(eq(1L), any(PagoRequestDTO.class));
    }

    @Test
    void actualizarPago_pagoNoExiste_retorna404() throws Exception {
        when(pagoService.actualizarPago(eq(99L), any(PagoRequestDTO.class)))
                .thenThrow(new RuntimeException("Pago no encontrado con id: 99"));

        mockMvc.perform(put("/api/pagos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void eliminarPago_pagoExistente_retorna204() throws Exception {
        doNothing().when(pagoService).eliminarPago(1L);

        mockMvc.perform(delete("/api/pagos/1"))
                .andExpect(status().isNoContent());

        verify(pagoService).eliminarPago(1L);
    }

    @Test
    void eliminarPago_pagoNoExiste_retorna404() throws Exception {
        doThrow(new RuntimeException("Pago no encontrado con id: 99"))
                .when(pagoService).eliminarPago(99L);

        mockMvc.perform(delete("/api/pagos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
