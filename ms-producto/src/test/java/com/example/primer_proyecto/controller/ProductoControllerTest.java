package com.example.primer_proyecto.controller;

import com.example.primer_proyecto.dto.ProductoRequestDTO;
import com.example.primer_proyecto.dto.ProductoResponseDTO;
import com.example.primer_proyecto.exception.GlobalExceptionHandler;
import com.example.primer_proyecto.service.ProductoService;
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
class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ProductoResponseDTO responseDTO;
    private ProductoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        responseDTO = new ProductoResponseDTO(1L, "Mouse Gamer", "Mouse inalambrico", 15990.0, 50, 1L);
        requestDTO = new ProductoRequestDTO();
        requestDTO.setNombre("Mouse Gamer");
        requestDTO.setDescripcion("Mouse inalambrico");
        requestDTO.setPrecio(15990.0);
        requestDTO.setStock(50);
        requestDTO.setCategoriaId(1L);
    }

    @Test
    void listarProductos_conProductos_retorna200() throws Exception {
        when(productoService.listarProductos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Mouse Gamer"))
                .andExpect(jsonPath("$[0].precio").value(15990.0))
                .andExpect(jsonPath("$[0].stock").value(50));

        verify(productoService).listarProductos();
    }

    @Test
    void listarProductos_listaVacia_retorna200() throws Exception {
        when(productoService.listarProductos()).thenReturn(List.of());

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void listarProductos_multiplesProductos_retorna200() throws Exception {
        ProductoResponseDTO otro = new ProductoResponseDTO(2L, "Teclado Mecanico", "RGB", 45000.0, 25, 1L);
        when(productoService.listarProductos()).thenReturn(List.of(responseDTO, otro));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Mouse Gamer"))
                .andExpect(jsonPath("$[1].nombre").value("Teclado Mecanico"));
    }

    @Test
    void buscarPorId_productoExistente_retorna200() throws Exception {
        when(productoService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Mouse Gamer"));

        verify(productoService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_productoNoExistente_retorna404() throws Exception {
        when(productoService.buscarPorId(99L))
                .thenThrow(new RuntimeException("Producto no encontrado con id: 99"));

        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Producto no encontrado con id: 99"));
    }

    @Test
    void crearProducto_datosValidos_retorna201() throws Exception {
        when(productoService.crearProducto(any(ProductoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Mouse Gamer"))
                .andExpect(jsonPath("$.precio").value(15990.0));

        verify(productoService).crearProducto(any(ProductoRequestDTO.class));
    }

    @Test
    void crearProducto_camposObligatoriosVacios_retorna400() throws Exception {
        ProductoRequestDTO dtoVacio = new ProductoRequestDTO();
        dtoVacio.setNombre("");
        dtoVacio.setPrecio(null);
        dtoVacio.setStock(null);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoVacio)))
                .andExpect(status().isBadRequest());

        verify(productoService, never()).crearProducto(any());
    }

    @Test
    void crearProducto_nombreEnBlanco_retorna400() throws Exception {
        requestDTO.setNombre("   ");

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(productoService, never()).crearProducto(any());
    }

    @Test
    void crearProducto_precioNegativo_retorna400() throws Exception {
        requestDTO.setPrecio(-100.0);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(productoService, never()).crearProducto(any());
    }

    @Test
    void crearProducto_stockNegativo_retorna400() throws Exception {
        requestDTO.setStock(-5);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(retorno -> {
                    org.junit.jupiter.api.Assertions.assertEquals(
                            org.springframework.http.HttpStatus.BAD_REQUEST.value(),
                            retorno.getResponse().getStatus()
                    );
                });

        verify(productoService, never()).crearProducto(any());
    }

    @Test
    void actualizarProducto_datosValidos_retorna200() throws Exception {
        when(productoService.actualizarProducto(eq(1L), any(ProductoRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Mouse Gamer"));

        verify(productoService).actualizarProducto(eq(1L), any(ProductoRequestDTO.class));
    }

    @Test
    void actualizarProducto_productoNoExiste_retorna404() throws Exception {
        when(productoService.actualizarProducto(eq(99L), any(ProductoRequestDTO.class)))
                .thenThrow(new RuntimeException("Producto no encontrado con id: 99"));

        mockMvc.perform(put("/api/productos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void actualizarProducto_datosInvalidos_retorna400() throws Exception {
        ProductoRequestDTO dtoInvalido = new ProductoRequestDTO();
        dtoInvalido.setNombre("");
        dtoInvalido.setPrecio(null);
        dtoInvalido.setStock(null);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());

        verify(productoService, never()).actualizarProducto(any(), any());
    }

    @Test
    void eliminarProducto_productoExistente_retorna204() throws Exception {
        doNothing().when(productoService).eliminarProducto(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());

        verify(productoService).eliminarProducto(1L);
    }

    @Test
    void eliminarProducto_productoNoExiste_retorna404() throws Exception {
        doThrow(new RuntimeException("Producto no encontrado con id: 99"))
                .when(productoService).eliminarProducto(99L);

        mockMvc.perform(delete("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
