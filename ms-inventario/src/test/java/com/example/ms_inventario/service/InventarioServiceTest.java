package com.example.ms_inventario.service;

import com.example.ms_inventario.client.ProductoClient;
import com.example.ms_inventario.dto.InventarioRequestDTO;
import com.example.ms_inventario.dto.InventarioResponseDTO;
import com.example.ms_inventario.dto.ProductoDTO;
import com.example.ms_inventario.model.Inventario;
import com.example.ms_inventario.repository.InventarioRepository;
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
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    // Este servicio depende de OTRO microservicio (ms-producto) a traves
    // de ProductoClient. En el test NO llamamos a ms-producto de verdad:
    // lo simulamos (mockeamos) para que el test sea rapido y no dependa
    // de que ms-producto este corriendo.
    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private InventarioService inventarioService;

    private Inventario inventario;
    private InventarioRequestDTO requestDTO;
    private ProductoDTO productoDTO;

    @BeforeEach
    void setUp() {
        inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProductoId(10L);
        inventario.setCantidadDisponible(30);

        requestDTO = new InventarioRequestDTO();
        requestDTO.setProductoId(10L);
        requestDTO.setCantidadDisponible(30);

        productoDTO = new ProductoDTO(10L, "Mouse Gamer", "Mouse inalambrico", 15990.0, 50);
    }

    @Test
    void listarInventario_retornaListaConNombreDeProducto() {
        when(inventarioRepository.findAll()).thenReturn(List.of(inventario));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);

        List<InventarioResponseDTO> resultado = inventarioService.listarInventario();

        assertEquals(1, resultado.size());
        assertEquals("Mouse Gamer", resultado.get(0).getNombreProducto());
    }

    @Test
    void buscarPorId_inventarioExistente_retornaDTO() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);

        InventarioResponseDTO resultado = inventarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(30, resultado.getCantidadDisponible());
    }

    @Test
    void buscarPorId_inventarioNoExistente_lanzaExcepcion() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> inventarioService.buscarPorId(99L));
    }

    @Test
    void crearInventario_productoExisteEnMsProducto_creaCorrectamente() {
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        InventarioResponseDTO resultado = inventarioService.crearInventario(requestDTO);

        assertNotNull(resultado);
        assertEquals("Mouse Gamer", resultado.getNombreProducto());
        verify(inventarioRepository).save(any(Inventario.class));
    }

    @Test
    void crearInventario_productoNoExisteEnMsProducto_lanzaExcepcion() {
        // Simulamos que ms-producto respondio "no existe" (null)
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> inventarioService.crearInventario(requestDTO));

        // Nunca deberia intentar guardar si el producto no existe
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    void actualizarInventario_inventarioNoExistente_lanzaExcepcion() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> inventarioService.actualizarInventario(99L, requestDTO));

        verify(inventarioRepository, never()).save(any());
    }

    @Test
    void actualizarInventario_datosValidos_actualizaCorrectamente() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        InventarioResponseDTO resultado = inventarioService.actualizarInventario(1L, requestDTO);

        assertEquals("Mouse Gamer", resultado.getNombreProducto());
        verify(inventarioRepository).save(inventario);
    }

    @Test
    void actualizarInventario_productoNoExiste_lanzaExcepcion() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> inventarioService.actualizarInventario(1L, requestDTO));
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    void eliminarInventario_inventarioExistente_eliminaCorrectamente() {
        when(inventarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(inventarioRepository).deleteById(1L);

        inventarioService.eliminarInventario(1L);

        verify(inventarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarInventario_inventarioNoExistente_lanzaExcepcion() {
        when(inventarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> inventarioService.eliminarInventario(99L));
    }

    @Test
    void listarInventario_listaVacia_retornaListaVacia() {
        when(inventarioRepository.findAll()).thenReturn(List.of());

        List<InventarioResponseDTO> resultado = inventarioService.listarInventario();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(productoClient, never()).obtenerProductoPorId(any());
    }

    @Test
    void listarInventario_productoNoDisponible_nombreNoDisponible() {
        when(inventarioRepository.findAll()).thenReturn(List.of(inventario));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(null);

        List<InventarioResponseDTO> resultado = inventarioService.listarInventario();

        assertEquals(1, resultado.size());
        assertEquals("Producto no disponible", resultado.get(0).getNombreProducto());
    }

    @Test
    void buscarPorId_productoNoDisponible_nombreNoDisponible() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(null);

        InventarioResponseDTO resultado = inventarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Producto no disponible", resultado.getNombreProducto());
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getProductoId());
        assertEquals(30, resultado.getCantidadDisponible());
    }

    @Test
    void crearInventario_productoExiste_guardaCorrectamenteLosCampos() {
        Inventario guardado = new Inventario();
        guardado.setId(5L);
        guardado.setProductoId(10L);
        guardado.setCantidadDisponible(50);

        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(guardado);

        InventarioResponseDTO resultado = inventarioService.crearInventario(requestDTO);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
        assertEquals(10L, resultado.getProductoId());
        assertEquals(50, resultado.getCantidadDisponible());
        assertEquals("Mouse Gamer", resultado.getNombreProducto());

        verify(inventarioRepository).save(argThat(inv ->
                inv.getProductoId().equals(10L) &&
                inv.getCantidadDisponible().equals(30)
        ));
    }

    @Test
    void actualizarInventario_datosValidos_verificaCamposActualizados() {
        Inventario actualizado = new Inventario();
        actualizado.setId(1L);
        actualizado.setProductoId(20L);
        actualizado.setCantidadDisponible(100);

        InventarioRequestDTO requestActualizado = new InventarioRequestDTO();
        requestActualizado.setProductoId(20L);
        requestActualizado.setCantidadDisponible(100);

        ProductoDTO otroProducto = new ProductoDTO(20L, "Teclado Mecanico", "RGB", 45000.0, 25);

        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        when(productoClient.obtenerProductoPorId(20L)).thenReturn(otroProducto);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(actualizado);

        InventarioResponseDTO resultado = inventarioService.actualizarInventario(1L, requestActualizado);

        assertEquals("Teclado Mecanico", resultado.getNombreProducto());
        assertEquals(100, resultado.getCantidadDisponible());
        verify(inventarioRepository).save(argThat(inv ->
                inv.getProductoId().equals(20L) &&
                inv.getCantidadDisponible().equals(100)
        ));
    }

    @Test
    void listarInventarioMultiplesItems_todosLosNombresCorrectos() {
        Inventario inv2 = new Inventario();
        inv2.setId(2L);
        inv2.setProductoId(20L);
        inv2.setCantidadDisponible(15);

        ProductoDTO prod2 = new ProductoDTO(20L, "Teclado Mecanico", "RGB", 45000.0, 25);

        when(inventarioRepository.findAll()).thenReturn(List.of(inventario, inv2));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);
        when(productoClient.obtenerProductoPorId(20L)).thenReturn(prod2);

        List<InventarioResponseDTO> resultado = inventarioService.listarInventario();

        assertEquals(2, resultado.size());
        assertEquals("Mouse Gamer", resultado.get(0).getNombreProducto());
        assertEquals("Teclado Mecanico", resultado.get(1).getNombreProducto());
    }
}
