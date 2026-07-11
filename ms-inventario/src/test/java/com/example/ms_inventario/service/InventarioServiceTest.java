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
}
