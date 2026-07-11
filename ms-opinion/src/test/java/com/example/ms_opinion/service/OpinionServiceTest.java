package com.example.ms_opinion.service;

import com.example.ms_opinion.client.ClienteClient;
import com.example.ms_opinion.client.ProductoClient;
import com.example.ms_opinion.dto.ClienteDTO;
import com.example.ms_opinion.dto.OpinionRequestDTO;
import com.example.ms_opinion.dto.OpinionResponseDTO;
import com.example.ms_opinion.dto.ProductoDTO;
import com.example.ms_opinion.model.Opinion;
import com.example.ms_opinion.repository.OpinionRepository;
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
class OpinionServiceTest {

    @Mock
    private OpinionRepository opinionRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private OpinionService opinionService;

    private Opinion opinion;
    private OpinionRequestDTO requestDTO;
    private ClienteDTO clienteDTO;
    private ProductoDTO productoDTO;

    @BeforeEach
    void setUp() {
        opinion = new Opinion();
        opinion.setId(1L);
        opinion.setClienteId(1L);
        opinion.setProductoId(10L);
        opinion.setPuntuacion(5);
        opinion.setComentario("Excelente producto");

        requestDTO = new OpinionRequestDTO();
        requestDTO.setClienteId(1L);
        requestDTO.setProductoId(10L);
        requestDTO.setPuntuacion(5);
        requestDTO.setComentario("Excelente producto");

        clienteDTO = new ClienteDTO(1L, "Juan Perez", "juan@mail.com", "123456789", "Calle Falsa 123");
        productoDTO = new ProductoDTO(10L, "Mouse Gamer", "Mouse inalambrico", 15990.0, 50);
    }

    @Test
    void listarOpiniones_retornaListaConNombres() {
        when(opinionRepository.findAll()).thenReturn(List.of(opinion));
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);

        List<OpinionResponseDTO> resultado = opinionService.listarOpiniones();

        assertEquals(1, resultado.size());
        assertEquals("Juan Perez", resultado.get(0).getNombreCliente());
        assertEquals("Mouse Gamer", resultado.get(0).getNombreProducto());
    }

    @Test
    void buscarPorId_opinionExistente_retornaDTO() {
        when(opinionRepository.findById(1L)).thenReturn(Optional.of(opinion));
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);

        OpinionResponseDTO resultado = opinionService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(5, resultado.getPuntuacion());
        assertEquals("Excelente producto", resultado.getComentario());
    }

    @Test
    void buscarPorId_opinionNoExistente_lanzaExcepcion() {
        when(opinionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> opinionService.buscarPorId(99L));
    }

    @Test
    void crearOpinion_clienteYProductoExisten_creaCorrectamente() {
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);
        when(opinionRepository.save(any(Opinion.class))).thenReturn(opinion);

        OpinionResponseDTO resultado = opinionService.crearOpinion(requestDTO);

        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getNombreCliente());
        assertEquals("Mouse Gamer", resultado.getNombreProducto());
        verify(opinionRepository).save(any(Opinion.class));
    }

    @Test
    void crearOpinion_clienteNoExiste_lanzaExcepcion() {
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> opinionService.crearOpinion(requestDTO));

        verify(opinionRepository, never()).save(any());
    }

    @Test
    void crearOpinion_productoNoExiste_lanzaExcepcion() {
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> opinionService.crearOpinion(requestDTO));

        verify(opinionRepository, never()).save(any());
    }

    @Test
    void eliminarOpinion_opinionExistente_eliminaCorrectamente() {
        when(opinionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(opinionRepository).deleteById(1L);

        opinionService.eliminarOpinion(1L);

        verify(opinionRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarOpinion_opinionNoExistente_lanzaExcepcion() {
        when(opinionRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> opinionService.eliminarOpinion(99L));
    }
}
