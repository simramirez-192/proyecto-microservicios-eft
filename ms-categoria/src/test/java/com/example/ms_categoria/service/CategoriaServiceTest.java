package com.example.ms_categoria.service;

import com.example.ms_categoria.dto.CategoriaRequestDTO;
import com.example.ms_categoria.dto.CategoriaResponseDTO;
import com.example.ms_categoria.model.Categoria;
import com.example.ms_categoria.repository.CategoriaRepository;
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
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private CategoriaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electronica");
        categoria.setDescripcion("Productos electronicos");

        requestDTO = new CategoriaRequestDTO();
        requestDTO.setNombre("Electronica");
        requestDTO.setDescripcion("Productos electronicos");
    }

    @Test
    void listarCategorias_retornaListaDeCategorias() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        List<CategoriaResponseDTO> resultado = categoriaService.listarCategorias();

        assertEquals(1, resultado.size());
        assertEquals("Electronica", resultado.get(0).getNombre());
    }

    @Test
    void buscarPorId_categoriaExistente_retornaDTO() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        CategoriaResponseDTO resultado = categoriaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Electronica", resultado.getNombre());
        assertEquals("Productos electronicos", resultado.getDescripcion());
    }

    @Test
    void buscarPorId_categoriaNoExistente_lanzaExcepcion() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> categoriaService.buscarPorId(99L));
    }

    @Test
    void crearCategoria_datosValidos_creaYRetornaCategoria() {
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaResponseDTO resultado = categoriaService.crearCategoria(requestDTO);

        assertNotNull(resultado);
        assertEquals("Electronica", resultado.getNombre());
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void actualizarCategoria_categoriaExistente_actualizaYRetorna() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaResponseDTO resultado = categoriaService.actualizarCategoria(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals("Electronica", resultado.getNombre());
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void actualizarCategoria_categoriaNoExistente_lanzaExcepcion() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> categoriaService.actualizarCategoria(99L, requestDTO));

        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void eliminarCategoria_categoriaExistente_eliminaCorrectamente() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoriaRepository).deleteById(1L);

        categoriaService.eliminarCategoria(1L);

        verify(categoriaRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarCategoria_categoriaNoExistente_lanzaExcepcion() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> categoriaService.eliminarCategoria(99L));
    }

    @Test
    void listarCategorias_listaVacia_retornaListaVacia() {
        when(categoriaRepository.findAll()).thenReturn(List.of());

        List<CategoriaResponseDTO> resultado = categoriaService.listarCategorias();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarCategoriasMultiples_retornaTodosLosItems() {
        Categoria cat2 = new Categoria();
        cat2.setId(2L);
        cat2.setNombre("Ropa");
        cat2.setDescripcion("Productos de vestir");

        when(categoriaRepository.findAll()).thenReturn(List.of(categoria, cat2));

        List<CategoriaResponseDTO> resultado = categoriaService.listarCategorias();

        assertEquals(2, resultado.size());
        assertEquals("Electronica", resultado.get(0).getNombre());
        assertEquals("Ropa", resultado.get(1).getNombre());
    }

    @Test
    void crearCategoria_verificaCamposGuardados() {
        Categoria guardada = new Categoria();
        guardada.setId(5L);
        guardada.setNombre("Deportes");
        guardada.setDescripcion("Articulos deportivos");

        when(categoriaRepository.save(any(Categoria.class))).thenReturn(guardada);

        CategoriaResponseDTO resultado = categoriaService.crearCategoria(requestDTO);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
        assertEquals("Deportes", resultado.getNombre());
        assertEquals("Articulos deportivos", resultado.getDescripcion());

        verify(categoriaRepository).save(argThat(cat ->
                cat.getNombre().equals("Electronica") &&
                cat.getDescripcion().equals("Productos electronicos")
        ));
    }

    @Test
    void actualizarCategoria_verificaCamposActualizados() {
        Categoria actualizada = new Categoria();
        actualizada.setId(1L);
        actualizada.setNombre("Hogar");
        actualizada.setDescripcion("Articulos del hogar");

        CategoriaRequestDTO requestActualizado = new CategoriaRequestDTO();
        requestActualizado.setNombre("Hogar");
        requestActualizado.setDescripcion("Articulos del hogar");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(actualizada);

        CategoriaResponseDTO resultado = categoriaService.actualizarCategoria(1L, requestActualizado);

        assertEquals("Hogar", resultado.getNombre());
        assertEquals("Articulos del hogar", resultado.getDescripcion());
        verify(categoriaRepository).save(argThat(cat ->
                cat.getNombre().equals("Hogar") &&
                cat.getDescripcion().equals("Articulos del hogar")
        ));
    }

    @Test
    void buscarPorId_retornaIdCorrecto() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        CategoriaResponseDTO resultado = categoriaService.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
    }

    @Test
    void eliminarCategoria_categoriaNoExiste_lanzaExcepcionConMensaje() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoriaService.eliminarCategoria(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void buscarPorId_categoriaNoExistente_lanzaExcepcionConMensaje() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoriaService.buscarPorId(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void actualizarCategoria_categoriaNoExiste_lanzaExcepcionConMensaje() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoriaService.actualizarCategoria(99L, requestDTO));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void listarCategorias_verificarQueSeLlamaAFindAll() {
        when(categoriaRepository.findAll()).thenReturn(List.of());

        categoriaService.listarCategorias();

        verify(categoriaRepository, times(1)).findAll();
    }
}
