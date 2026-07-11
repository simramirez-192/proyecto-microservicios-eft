package com.example.primer_proyecto.service;

import com.example.primer_proyecto.dto.ProductoRequestDTO;
import com.example.primer_proyecto.dto.ProductoResponseDTO;
import com.example.primer_proyecto.model.Producto;
import com.example.primer_proyecto.repository.ProductoRepository;
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

// @ExtendWith(MockitoExtension.class) le dice a JUnit que active Mockito
// en esta clase, para poder usar @Mock y @InjectMocks
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    // @Mock crea una version "falsa" del repositorio: no toca la base de datos real,
    // nosotros le decimos que devolver en cada metodo con when(...)
    @Mock
    private ProductoRepository productoRepository;

    // @InjectMocks crea una instancia REAL de ProductoService, pero inyectandole
    // el productoRepository de arriba (el falso)
    @InjectMocks
    private ProductoService productoService;

    private Producto producto;
    private ProductoRequestDTO requestDTO;

    // @BeforeEach se ejecuta ANTES de cada @Test, para dejar datos de prueba listos
    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Mouse Gamer");
        producto.setDescripcion("Mouse inalambrico");
        producto.setPrecio(15990.0);
        producto.setStock(50);

        requestDTO = new ProductoRequestDTO();
        requestDTO.setNombre("Mouse Gamer");
        requestDTO.setDescripcion("Mouse inalambrico");
        requestDTO.setPrecio(15990.0);
        requestDTO.setStock(50);
    }

    @Test
    void listarProductos_retornaListaDeProductos() {
        // ARRANGE: cuando llamen a findAll(), que devuelva esta lista
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        // ACT: ejecutamos el metodo real que queremos probar
        List<ProductoResponseDTO> resultado = productoService.listarProductos();

        // ASSERT: verificamos que el resultado sea el esperado
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Mouse Gamer", resultado.get(0).getNombre());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void listarProductos_listaVaciaRetornaListaVacia() {
        when(productoRepository.findAll()).thenReturn(List.of());

        List<ProductoResponseDTO> resultado = productoService.listarProductos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorId_productoExistente_retornaDTO() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        ProductoResponseDTO resultado = productoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Mouse Gamer", resultado.getNombre());
        assertEquals(15990.0, resultado.getPrecio());
    }

    @Test
    void buscarPorId_productoNoExistente_lanzaExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // assertThrows verifica que efectivamente se lance la excepcion esperada
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productoService.buscarPorId(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void crearProducto_datosValidos_creaYRetornaProducto() {
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoResponseDTO resultado = productoService.crearProducto(requestDTO);

        assertNotNull(resultado);
        assertEquals("Mouse Gamer", resultado.getNombre());
        assertEquals(50, resultado.getStock());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void actualizarProducto_productoExistente_actualizaYRetorna() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoResponseDTO resultado = productoService.actualizarProducto(1L, requestDTO);

        assertNotNull(resultado);
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void actualizarProducto_productoNoExistente_lanzaExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> productoService.actualizarProducto(99L, requestDTO));

        verify(productoRepository, never()).save(any());
    }

    @Test
    void eliminarProducto_productoExistente_eliminaCorrectamente() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);

        productoService.eliminarProducto(1L);

        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarProducto_productoNoExistente_lanzaExcepcion() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> productoService.eliminarProducto(99L));

        verify(productoRepository, never()).deleteById(any());
    }
}
