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
    void listarOpiniones_listaVacia_retornaListaVacia() {
        when(opinionRepository.findAll()).thenReturn(List.of());

        List<OpinionResponseDTO> resultado = opinionService.listarOpiniones();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarOpiniones_multiplesOpiniones_retornaTodas() {
        Opinion opinion2 = new Opinion();
        opinion2.setId(2L);
        opinion2.setClienteId(2L);
        opinion2.setProductoId(20L);
        opinion2.setPuntuacion(3);
        opinion2.setComentario("Regular");

        when(opinionRepository.findAll()).thenReturn(List.of(opinion, opinion2));
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);
        when(clienteClient.obtenerClientePorId(2L)).thenReturn(new ClienteDTO(2L, "Maria Lopez", "maria@mail.com", "987654321", "Av. Siempre Viva"));
        when(productoClient.obtenerProductoPorId(20L)).thenReturn(new ProductoDTO(20L, "Teclado", "Mecanico", 45000.0, 10));

        List<OpinionResponseDTO> resultado = opinionService.listarOpiniones();

        assertEquals(2, resultado.size());
    }

    @Test
    void listarOpiniones_clienteNull_muestraNoDisponible() {
        when(opinionRepository.findAll()).thenReturn(List.of(opinion));
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(null);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);

        List<OpinionResponseDTO> resultado = opinionService.listarOpiniones();

        assertEquals(1, resultado.size());
        assertEquals("Cliente no disponible", resultado.get(0).getNombreCliente());
    }

    @Test
    void listarOpiniones_productoNull_muestraNoDisponible() {
        when(opinionRepository.findAll()).thenReturn(List.of(opinion));
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(null);

        List<OpinionResponseDTO> resultado = opinionService.listarOpiniones();

        assertEquals(1, resultado.size());
        assertEquals("Producto no disponible", resultado.get(0).getNombreProducto());
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
    void buscarPorId_opinionExistente_retornaIdsCorrectos() {
        when(opinionRepository.findById(1L)).thenReturn(Optional.of(opinion));
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);

        OpinionResponseDTO resultado = opinionService.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals(1L, resultado.getClienteId());
        assertEquals(10L, resultado.getProductoId());
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
    void crearOpinion_verificaDatosEnOpinionGuardada() {
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);
        when(opinionRepository.save(any(Opinion.class))).thenAnswer(invocation -> {
            Opinion op = invocation.getArgument(0);
            op.setId(1L);
            return op;
        });

        opinionService.crearOpinion(requestDTO);

        verify(opinionRepository).save(argThat(op ->
                op.getClienteId().equals(1L) &&
                op.getProductoId().equals(10L) &&
                op.getPuntuacion().equals(5) &&
                op.getComentario().equals("Excelente producto")
        ));
    }

    @Test
    void actualizarOpinion_datosValidos_retornaDTO() {
        when(opinionRepository.findById(1L)).thenReturn(Optional.of(opinion));
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(productoDTO);
        when(opinionRepository.save(any(Opinion.class))).thenReturn(opinion);

        OpinionResponseDTO resultado = opinionService.actualizarOpinion(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getNombreCliente());
        verify(opinionRepository).save(any(Opinion.class));
    }

    @Test
    void actualizarOpinion_opinionNoExistente_lanzaExcepcion() {
        when(opinionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> opinionService.actualizarOpinion(99L, requestDTO));

        verify(opinionRepository, never()).save(any());
    }

    @Test
    void actualizarOpinion_clienteNoExiste_lanzaExcepcion() {
        when(opinionRepository.findById(1L)).thenReturn(Optional.of(opinion));
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> opinionService.actualizarOpinion(1L, requestDTO));

        verify(opinionRepository, never()).save(any());
    }

    @Test
    void actualizarOpinion_productoNoExiste_lanzaExcepcion() {
        when(opinionRepository.findById(1L)).thenReturn(Optional.of(opinion));
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(clienteDTO);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> opinionService.actualizarOpinion(1L, requestDTO));

        verify(opinionRepository, never()).save(any());
    }

    @Test
    void actualizarOpinion_modificaCamposCorrectamente() {
        OpinionRequestDTO nuevoRequest = new OpinionRequestDTO();
        nuevoRequest.setClienteId(2L);
        nuevoRequest.setProductoId(20L);
        nuevoRequest.setPuntuacion(3);
        nuevoRequest.setComentario(" producto mejoro");

        when(opinionRepository.findById(1L)).thenReturn(Optional.of(opinion));
        when(clienteClient.obtenerClientePorId(2L)).thenReturn(new ClienteDTO(2L, "Maria", "maria@mail.com", "111", "Dir"));
        when(productoClient.obtenerProductoPorId(20L)).thenReturn(new ProductoDTO(20L, "Teclado", "Mec", 45000.0, 10));
        when(opinionRepository.save(any(Opinion.class))).thenAnswer(inv -> inv.getArgument(0));

        OpinionResponseDTO resultado = opinionService.actualizarOpinion(1L, nuevoRequest);

        assertNotNull(resultado);
        verify(opinionRepository).save(argThat(op ->
                op.getClienteId().equals(2L) &&
                op.getProductoId().equals(20L) &&
                op.getPuntuacion().equals(3) &&
                op.getComentario().equals(" producto mejoro")
        ));
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

        verify(opinionRepository, never()).deleteById(any());
    }
}
