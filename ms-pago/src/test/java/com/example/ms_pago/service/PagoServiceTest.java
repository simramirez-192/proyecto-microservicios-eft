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

    @Test
    void listarPagos_listaVacia_retornaListaVacia() {
        when(pagoRepository.findAll()).thenReturn(List.of());

        List<PagoResponseDTO> resultado = pagoService.listarPagos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarPagos_multiplesPagos_retornaTodos() {
        Pago pago2 = new Pago();
        pago2.setId(2L);
        pago2.setPedidoId(8L);
        pago2.setMonto(15000.0);
        pago2.setMetodoPago("EFECTIVO");
        pago2.setEstado("PENDIENTE");

        when(pagoRepository.findAll()).thenReturn(List.of(pago, pago2));

        List<PagoResponseDTO> resultado = pagoService.listarPagos();

        assertEquals(2, resultado.size());
        assertEquals("TARJETA", resultado.get(0).getMetodoPago());
        assertEquals("EFECTIVO", resultado.get(1).getMetodoPago());
    }

    @Test
    void crearPago_pedidoExiste_verificaCamposGuardados() {
        Pago guardado = new Pago();
        guardado.setId(5L);
        guardado.setPedidoId(7L);
        guardado.setMonto(31980.0);
        guardado.setMetodoPago("TARJETA");
        guardado.setEstado("PAGADO");

        when(pedidoClient.obtenerPedidoPorId(7L)).thenReturn(pedidoDTO);
        when(pagoRepository.save(any(Pago.class))).thenReturn(guardado);

        PagoResponseDTO resultado = pagoService.crearPago(requestDTO);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
        assertEquals(7L, resultado.getPedidoId());
        assertEquals(31980.0, resultado.getMonto());
        assertEquals("TARJETA", resultado.getMetodoPago());
        assertEquals("PAGADO", resultado.getEstado());

        verify(pagoRepository).save(argThat(p ->
                p.getPedidoId().equals(7L) &&
                p.getMonto().equals(31980.0) &&
                p.getMetodoPago().equals("TARJETA") &&
                p.getEstado().equals("PAGADO")
        ));
    }

    @Test
    void actualizarPago_pagoNoExiste_lanzaExcepcion() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> pagoService.actualizarPago(99L, requestDTO));

        verify(pagoRepository, never()).save(any());
    }

    @Test
    void actualizarPago_datosValidos_verificaCamposActualizados() {
        Pago pagoActualizado = new Pago();
        pagoActualizado.setId(1L);
        pagoActualizado.setPedidoId(8L);
        pagoActualizado.setMonto(45000.0);
        pagoActualizado.setMetodoPago("EFECTIVO");
        pagoActualizado.setEstado("PAGADO");

        PedidoDTO otroPedido = new PedidoDTO(8L, 3L, "Carlos Ruiz", 20L, "Teclado RGB", 1, 45000.0, "CONFIRMADO");

        PagoRequestDTO requestActualizado = new PagoRequestDTO();
        requestActualizado.setPedidoId(8L);
        requestActualizado.setMetodoPago("EFECTIVO");

        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pedidoClient.obtenerPedidoPorId(8L)).thenReturn(otroPedido);
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoActualizado);

        PagoResponseDTO resultado = pagoService.actualizarPago(1L, requestActualizado);

        assertEquals(45000.0, resultado.getMonto());
        assertEquals("EFECTIVO", resultado.getMetodoPago());
        verify(pagoRepository).save(argThat(p ->
                p.getPedidoId().equals(8L) &&
                p.getMonto().equals(45000.0) &&
                p.getMetodoPago().equals("EFECTIVO")
        ));
    }

    @Test
    void crearPago_diferenteMetodoPago_guardaCorrectamente() {
        Pago guardado = new Pago();
        guardado.setId(10L);
        guardado.setPedidoId(3L);
        guardado.setMonto(5000.0);
        guardado.setMetodoPago("TRANSFERENCIA");
        guardado.setEstado("PAGADO");

        PedidoDTO pedidoPequeno = new PedidoDTO(3L, 1L, "Maria Lopez", 5L, "Cable HDMI", 1, 5000.0, "PENDIENTE");

        PagoRequestDTO requestTransferencia = new PagoRequestDTO();
        requestTransferencia.setPedidoId(3L);
        requestTransferencia.setMetodoPago("TRANSFERENCIA");

        when(pedidoClient.obtenerPedidoPorId(3L)).thenReturn(pedidoPequeno);
        when(pagoRepository.save(any(Pago.class))).thenReturn(guardado);

        PagoResponseDTO resultado = pagoService.crearPago(requestTransferencia);

        assertEquals("TRANSFERENCIA", resultado.getMetodoPago());
        assertEquals(5000.0, resultado.getMonto());
        assertEquals("PAGADO", resultado.getEstado());
    }

    @Test
    void buscarPorId_verificaTodosLosCamposDelDTO() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));

        PagoResponseDTO resultado = pagoService.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals(7L, resultado.getPedidoId());
        assertEquals(31980.0, resultado.getMonto());
        assertEquals("TARJETA", resultado.getMetodoPago());
        assertEquals("PAGADO", resultado.getEstado());
    }
}
