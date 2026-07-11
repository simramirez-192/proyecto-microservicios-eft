package com.example.ms_categoria.controller;

import com.example.ms_categoria.dto.CategoriaRequestDTO;
import com.example.ms_categoria.dto.CategoriaResponseDTO;
import com.example.ms_categoria.exception.GlobalExceptionHandler;
import com.example.ms_categoria.service.CategoriaService;
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
class CategoriaControllerTest {

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private CategoriaController categoriaController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private CategoriaResponseDTO responseDTO;
    private CategoriaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoriaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        responseDTO = new CategoriaResponseDTO(1L, "Electronica", "Productos electronicos");
        requestDTO = new CategoriaRequestDTO();
        requestDTO.setNombre("Electronica");
        requestDTO.setDescripcion("Productos electronicos");
    }

    @Test
    void listarCategorias_retornaLista200() throws Exception {
        when(categoriaService.listarCategorias()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Electronica"))
                .andExpect(jsonPath("$[0].descripcion").value("Productos electronicos"));

        verify(categoriaService).listarCategorias();
    }

    @Test
    void listarCategorias_listaVacia_retorna200() throws Exception {
        when(categoriaService.listarCategorias()).thenReturn(List.of());

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void buscarPorId_categoriaExistente_retorna200() throws Exception {
        when(categoriaService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Electronica"));

        verify(categoriaService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_categoriaNoExistente_retorna404() throws Exception {
        when(categoriaService.buscarPorId(99L))
                .thenThrow(new RuntimeException("La categoria con id 99 no existe"));

        mockMvc.perform(get("/api/categorias/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("La categoria con id 99 no existe"));
    }

    @Test
    void buscarPorId_errorDeNegocio_retorna400() throws Exception {
        when(categoriaService.buscarPorId(99L))
                .thenThrow(new RuntimeException("Error interno del servicio"));

        mockMvc.perform(get("/api/categorias/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void crearCategoria_datosValidos_retorna201() throws Exception {
        when(categoriaService.crearCategoria(any(CategoriaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Electronica"));

        verify(categoriaService).crearCategoria(any(CategoriaRequestDTO.class));
    }

    @Test
    void crearCategoria_nombreEnBlanco_retorna400() throws Exception {
        requestDTO.setNombre("");

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearCategoria_nombreNull_retorna400() throws Exception {
        requestDTO.setNombre(null);

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarCategoria_datosValidos_retorna200() throws Exception {
        when(categoriaService.actualizarCategoria(eq(1L), any(CategoriaRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Electronica"));

        verify(categoriaService).actualizarCategoria(eq(1L), any(CategoriaRequestDTO.class));
    }

    @Test
    void actualizarCategoria_categoriaNoExiste_retorna404() throws Exception {
        when(categoriaService.actualizarCategoria(eq(99L), any(CategoriaRequestDTO.class)))
                .thenThrow(new RuntimeException("La categoria con id 99 no existe"));

        mockMvc.perform(put("/api/categorias/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void eliminarCategoria_categoriaExistente_retorna204() throws Exception {
        doNothing().when(categoriaService).eliminarCategoria(1L);

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isNoContent());

        verify(categoriaService).eliminarCategoria(1L);
    }

    @Test
    void eliminarCategoria_categoriaNoExiste_retorna404() throws Exception {
        doThrow(new RuntimeException("La categoria con id 99 no existe"))
                .when(categoriaService).eliminarCategoria(99L);

        mockMvc.perform(delete("/api/categorias/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void listarCategoriasMultiples_retornaTodosLosItems() throws Exception {
        CategoriaResponseDTO cat2 = new CategoriaResponseDTO(2L, "Ropa", "Productos de vestir");

        when(categoriaService.listarCategorias()).thenReturn(List.of(responseDTO, cat2));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Electronica"))
                .andExpect(jsonPath("$[1].nombre").value("Ropa"));
    }
}
