package com.example.ms_pago.service;

import com.example.ms_pago.client.PedidoClient;
import com.example.ms_pago.dto.PagoRequestDTO;
import com.example.ms_pago.dto.PagoResponseDTO;
import com.example.ms_pago.dto.PedidoDTO;
import com.example.ms_pago.model.Pago;
import com.example.ms_pago.repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PedidoClient pedidoClient;

    @InjectMocks
    private PagoService pagoService;

    private Pago pago;
    private PagoRequestDTO requestDTO;
    private PedidoDTO pedidoDTO;

    @BeforeEach
    void setUp() {
        pago = new Pago();
        pago.setId(1L);
        pago.setPedidoId(7L);
        pago.setMonto(31980.0);
        pago.setMetodoPago("TARJETA");
        pago.setEstado("PAGADO");

        requestDTO = new PagoRequestDTO();
        requestDTO.setPedidoId(7L);
        requestDTO.setMetodoPago("TARJETA");

        pedidoDTO = new PedidoDTO(7L, 5L, "Ana Torres", 10L, "Mouse Gamer", 2, 31980.0, "PENDIENTE");
    }

    @Test
    void listarPagos_retornaListaDePagos() {
        when(pagoRepository.findAll()).thenReturn(List.of(pago));

        List<PagoResponseDTO> resultado = pagoService.listarPagos();

        assertEquals(1, resultado.size());
        assertEquals("TARJETA", resultado.get(0).getMetodoPago());
    }

    @Test
    void buscarPorId_pagoNoExistente_lanzaExcepcion() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> pagoService.buscarPorId(99L));
    }

    @Test
    void buscarPorId_pagoExistente_retornaDTO() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));

        PagoResponseDTO resultado = pagoService.buscarPorId(1L);

        assertEquals("PAGADO", resultado.getEstado());
    }

    @Test
    void crearPago_pedidoExiste_tomaElMontoDelPedido() {
        when(pedidoClient.obtenerPedidoPorId(7L)).thenReturn(pedidoDTO);
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        PagoResponseDTO resultado = pagoService.crearPago(requestDTO);

        assertNotNull(resultado);
        // El monto del pago debe ser igual al total del pedido consultado
        assertEquals(31980.0, resultado.getMonto());
        assertEquals("PAGADO", resultado.getEstado());
    }

    @Test
    void crearPago_pedidoNoExiste_lanzaExcepcion() {
        when(pedidoClient.obtenerPedidoPorId(7L)).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> pagoService.crearPago(requestDTO));

        verify(pagoRepository, never()).save(any());
    }

    @Test
    void actualizarPago_datosValidos_actualizaCorrectamente() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pedidoClient.obtenerPedidoPorId(7L)).thenReturn(pedidoDTO);
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        PagoResponseDTO resultado = pagoService.actualizarPago(1L, requestDTO);

        assertEquals(31980.0, resultado.getMonto());
        verify(pagoRepository).save(pago);
    }

    @Test
    void actualizarPago_pedidoNoExiste_lanzaExcepcion() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pedidoClient.obtenerPedidoPorId(7L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> pagoService.actualizarPago(1L, requestDTO));
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void eliminarPago_pagoExistente_eliminaCorrectamente() {
        when(pagoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pagoRepository).deleteById(1L);

        pagoService.eliminarPago(1L);

        verify(pagoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarPago_pagoNoExistente_lanzaExcepcion() {
        when(pagoRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> pagoService.eliminarPago(99L));
    }
}
