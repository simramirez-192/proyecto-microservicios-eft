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

    @Test
    void listarProductos_multiplesProductos_retornaTodos() {
        Producto otro = new Producto();
        otro.setId(2L);
        otro.setNombre("Teclado Mecanico");
        otro.setDescripcion("RGB");
        otro.setPrecio(45000.0);
        otro.setStock(25);

        when(productoRepository.findAll()).thenReturn(List.of(producto, otro));

        List<ProductoResponseDTO> resultado = productoService.listarProductos();

        assertEquals(2, resultado.size());
        assertEquals("Mouse Gamer", resultado.get(0).getNombre());
        assertEquals("Teclado Mecanico", resultado.get(1).getNombre());
    }

    @Test
    void listarProductos_verificaCamposDelDTO() {
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        List<ProductoResponseDTO> resultado = productoService.listarProductos();

        assertEquals(1, resultado.size());
        ProductoResponseDTO dto = resultado.get(0);
        assertEquals(1L, dto.getId());
        assertEquals("Mouse Gamer", dto.getNombre());
        assertEquals("Mouse inalambrico", dto.getDescripcion());
        assertEquals(15990.0, dto.getPrecio());
        assertEquals(50, dto.getStock());
    }

    @Test
    void crearProducto_stockCero_creaCorrectamente() {
        requestDTO.setStock(0);

        Producto guardado = new Producto();
        guardado.setId(2L);
        guardado.setNombre("Mouse Gamer");
        guardado.setDescripcion("Mouse inalambrico");
        guardado.setPrecio(15990.0);
        guardado.setStock(0);

        when(productoRepository.save(any(Producto.class))).thenReturn(guardado);

        ProductoResponseDTO resultado = productoService.crearProducto(requestDTO);

        assertNotNull(resultado);
        assertEquals(0, resultado.getStock());
        assertEquals(2L, resultado.getId());
    }

    @Test
    void crearProducto_stockNegativo_creaCorrectamente() {
        requestDTO.setStock(-10);

        Producto guardado = new Producto();
        guardado.setId(3L);
        guardado.setNombre("Mouse Gamer");
        guardado.setDescripcion("Mouse inalambrico");
        guardado.setPrecio(15990.0);
        guardado.setStock(-10);

        when(productoRepository.save(any(Producto.class))).thenReturn(guardado);

        ProductoResponseDTO resultado = productoService.crearProducto(requestDTO);

        assertNotNull(resultado);
        assertEquals(-10, resultado.getStock());
    }

    @Test
    void crearProducto_precioDecimal_creaCorrectamente() {
        requestDTO.setPrecio(9999.99);

        Producto guardado = new Producto();
        guardado.setId(4L);
        guardado.setNombre("Mouse Gamer");
        guardado.setDescripcion("Mouse inalambrico");
        guardado.setPrecio(9999.99);
        guardado.setStock(50);

        when(productoRepository.save(any(Producto.class))).thenReturn(guardado);

        ProductoResponseDTO resultado = productoService.crearProducto(requestDTO);

        assertNotNull(resultado);
        assertEquals(9999.99, resultado.getPrecio());
    }

    @Test
    void crearProducto_verificaQueSeCopianTodosLosCampos() {
        requestDTO.setNombre("Teclado RGB");
        requestDTO.setDescripcion("Teclado mecanico");
        requestDTO.setPrecio(45000.0);
        requestDTO.setStock(30);

        when(productoRepository.save(argThat(p ->
                p.getNombre().equals("Teclado RGB") &&
                p.getDescripcion().equals("Teclado mecanico") &&
                p.getPrecio().equals(45000.0) &&
                p.getStock().equals(30)
        ))).thenReturn(producto);

        ProductoResponseDTO resultado = productoService.crearProducto(requestDTO);

        assertNotNull(resultado);
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void actualizarProducto_modificaTodosLosCampos() {
        Producto existente = new Producto();
        existente.setId(1L);
        existente.setNombre("Nombre Viejo");
        existente.setDescripcion("Desc Vieja");
        existente.setPrecio(1000.0);
        existente.setStock(10);

        ProductoRequestDTO requestActualizado = new ProductoRequestDTO();
        requestActualizado.setNombre("Nombre Nuevo");
        requestActualizado.setDescripcion("Desc Nueva");
        requestActualizado.setPrecio(20000.0);
        requestActualizado.setStock(100);

        Producto actualizado = new Producto();
        actualizado.setId(1L);
        actualizado.setNombre("Nombre Nuevo");
        actualizado.setDescripcion("Desc Nueva");
        actualizado.setPrecio(20000.0);
        actualizado.setStock(100);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(any(Producto.class))).thenReturn(actualizado);

        ProductoResponseDTO resultado = productoService.actualizarProducto(1L, requestActualizado);

        assertNotNull(resultado);
        assertEquals("Nombre Nuevo", resultado.getNombre());
        assertEquals("Desc Nueva", resultado.getDescripcion());
        assertEquals(20000.0, resultado.getPrecio());
        assertEquals(100, resultado.getStock());
    }

    @Test
    void actualizarProducto_verificaCamposModificados() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(argThat(p ->
                p.getNombre().equals("Mouse Gamer") &&
                p.getPrecio().equals(15990.0)
        ))).thenReturn(producto);

        ProductoResponseDTO resultado = productoService.actualizarProducto(1L, requestDTO);

        assertNotNull(resultado);
        verify(productoRepository).save(argThat(p ->
                p.getNombre().equals("Mouse Gamer") &&
                p.getDescripcion().equals("Mouse inalambrico") &&
                p.getPrecio().equals(15990.0) &&
                p.getStock().equals(50)
        ));
    }

    @Test
    void buscarPorId_verificaTodosLosCamposDelDTO() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        ProductoResponseDTO resultado = productoService.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals("Mouse Gamer", resultado.getNombre());
        assertEquals("Mouse inalambrico", resultado.getDescripcion());
        assertEquals(15990.0, resultado.getPrecio());
        assertEquals(50, resultado.getStock());
    }

    @Test
    void eliminarProducto_verificaQueSeLlamaDeleteByIdUnaVez() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        productoService.eliminarProducto(1L);

        verify(productoRepository, times(1)).deleteById(1L);
        verify(productoRepository, times(1)).existsById(1L);
    }
}
