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
}
