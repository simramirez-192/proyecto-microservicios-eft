package com.example.ms_pedido.service;

import com.example.ms_pedido.client.ClienteClient;
import com.example.ms_pedido.client.ProductoClient;
import com.example.ms_pedido.dto.ClienteDTO;
import com.example.ms_pedido.dto.PedidoRequestDTO;
import com.example.ms_pedido.dto.PedidoResponseDTO;
import com.example.ms_pedido.dto.ProductoDTO;
import com.example.ms_pedido.model.Pedido;
import com.example.ms_pedido.repository.PedidoRepository;
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
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedido;
    private PedidoRequestDTO requestDTO;
    private ClienteDTO clienteDTO;
    private ProductoDTO productoDTO;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setClienteId(5L);
        pedido.setProductoId(10L);
        pedido.setCantidad(2);
        pedido.setTotal(31980.0);
        pedido.setEstado("PENDIENTE");

        requestDTO = new PedidoRequestDTO();
        requestDTO.setClienteId(5L);
        requestDTO.setProductoId(10L);
        requestDTO.setCantidad(2);

        clienteDTO = new ClienteDTO(5L, "Ana Torres", "ana@email.com", "+56911112222", "Av. Siempre Viva 123");
        productoDTO = new ProductoDTO(10L, "Mouse Gamer", "Mouse inalambrico", 15990.0, 50);
    }

    @Test
    void listarPedidos_retornaListaConNombres() {
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);

        List<PedidoResponseDTO> resultado = pedidoService.listarPedidos();

        assertEquals(1, resultado.size());
        assertEquals("Ana Torres", resultado.get(0).getNombreCliente());
        assertEquals("Mouse Gamer", resultado.get(0).getNombreProducto());
    }

    @Test
    void listarPedidos_listaVacia_retornaListaVacia() {
        when(pedidoRepository.findAll()).thenReturn(List.of());

        List<PedidoResponseDTO> resultado = pedidoService.listarPedidos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarPedidos_clienteNull_retornaNombreNoDisponible() {
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(null);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);

        List<PedidoResponseDTO> resultado = pedidoService.listarPedidos();

        assertEquals(1, resultado.size());
        assertEquals("Cliente no disponible", resultado.get(0).getNombreCliente());
    }

    @Test
    void listarPedidos_productoNull_retornaNombreNoDisponible() {
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(null);

        List<PedidoResponseDTO> resultado = pedidoService.listarPedidos();

        assertEquals(1, resultado.size());
        assertEquals("Producto no disponible", resultado.get(0).getNombreProducto());
    }

    @Test
    void buscarPorId_pedidoNoExistente_lanzaExcepcion() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pedidoService.buscarPorId(99L));
        assertTrue(ex.getMessage().contains("no encontrado"));
    }

    @Test
    void buscarPorId_pedidoExistente_retornaDTO() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);

        PedidoResponseDTO resultado = pedidoService.buscarPorId(1L);

        assertEquals("Ana Torres", resultado.getNombreCliente());
        assertEquals("Mouse Gamer", resultado.getNombreProducto());
    }

    @Test
    void buscarPorId_clienteNull_retornaNombreNoDisponible() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(null);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);

        PedidoResponseDTO resultado = pedidoService.buscarPorId(1L);

        assertEquals("Cliente no disponible", resultado.getNombreCliente());
        assertEquals("Mouse Gamer", resultado.getNombreProducto());
    }

    @Test
    void buscarPorId_productoNull_retornaNombreNoDisponible() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(null);

        PedidoResponseDTO resultado = pedidoService.buscarPorId(1L);

        assertEquals("Ana Torres", resultado.getNombreCliente());
        assertEquals("Producto no disponible", resultado.getNombreProducto());
    }

    @Test
    void crearPedido_clienteYProductoExisten_calculaTotalCorrectamente() {
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        PedidoResponseDTO resultado = pedidoService.crearPedido(requestDTO);

        assertNotNull(resultado);
        assertEquals(31980.0, resultado.getTotal());
        assertEquals("PENDIENTE", resultado.getEstado());
    }

    @Test
    void crearPedido_verificaQueSeConsultanClienteYProducto() {
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        pedidoService.crearPedido(requestDTO);

        verify(clienteClient).obtenerClientePorId(5L);
        verify(productoClient).obtenerProductoPorId(10L);
    }

    @Test
    void crearPedido_verificaEstadoPendiente() {
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            assertEquals("PENDIENTE", p.getEstado());
            return p;
        });

        PedidoResponseDTO resultado = pedidoService.crearPedido(requestDTO);

        assertEquals("PENDIENTE", resultado.getEstado());
    }

    @Test
    void crearPedido_clienteNoExiste_lanzaExcepcion() {
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pedidoService.crearPedido(requestDTO));
        assertTrue(ex.getMessage().contains("no existe"));

        verify(pedidoRepository, never()).save(any());
        verify(productoClient, never()).obtenerProductoPorId(any());
    }

    @Test
    void crearPedido_productoNoExiste_lanzaExcepcion() {
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pedidoService.crearPedido(requestDTO));
        assertTrue(ex.getMessage().contains("no existe"));

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void actualizarPedido_datosValidos_actualizaCorrectamente() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        PedidoResponseDTO resultado = pedidoService.actualizarPedido(1L, requestDTO);

        assertEquals(31980.0, resultado.getTotal());
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void actualizarPedido_pedidoNoEncontrado_lanzaExcepcion() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pedidoService.actualizarPedido(99L, requestDTO));
        assertTrue(ex.getMessage().contains("no encontrado"));
    }

    @Test
    void actualizarPedido_clienteNoExiste_lanzaExcepcion() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pedidoService.actualizarPedido(1L, requestDTO));
        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    void actualizarPedido_productoNoExiste_lanzaExcepcion() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pedidoService.actualizarPedido(1L, requestDTO));
        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    void eliminarPedido_pedidoExistente_eliminaCorrectamente() {
        when(pedidoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pedidoRepository).deleteById(1L);

        pedidoService.eliminarPedido(1L);

        verify(pedidoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarPedido_pedidoNoExistente_lanzaExcepcion() {
        when(pedidoRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pedidoService.eliminarPedido(99L));
        assertTrue(ex.getMessage().contains("no encontrado"));
    }

    @Test
    void crearPedido_totalCalculadoPrecioPorCantidad() {
        ProductoDTO productoCaro = new ProductoDTO(20L, "Monitor 4K", "UHD", 299990.0, 5);
        when(clienteClient.obtenerClientePorId(5L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(20L)).thenReturn(productoCaro);

        PedidoRequestDTO requestCarrto = new PedidoRequestDTO();
        requestCarrto.setClienteId(5L);
        requestCarrto.setProductoId(20L);
        requestCarrto.setCantidad(3);

        Pedido pedidoGuardado = new Pedido(2L, 5L, 20L, 3, 899970.0, "PENDIENTE");
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);

        PedidoResponseDTO resultado = pedidoService.crearPedido(requestCarrto);

        assertEquals(899970.0, resultado.getTotal());
    }
}
