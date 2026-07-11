package com.example.ms_envio.service;

import com.example.ms_envio.client.PedidoClient;
import com.example.ms_envio.dto.EnvioRequestDTO;
import com.example.ms_envio.dto.EnvioResponseDTO;
import com.example.ms_envio.dto.PedidoDTO;
import com.example.ms_envio.model.Envio;
import com.example.ms_envio.repository.EnvioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private PedidoClient pedidoClient;

    @InjectMocks
    private EnvioService envioService;

    private Envio envio;
    private EnvioRequestDTO requestDTO;
    private PedidoDTO pedidoDTO;

    @BeforeEach
    void setUp() {
        envio = new Envio();
        envio.setId(1L);
        envio.setPedidoId(10L);
        envio.setDireccion("Calle Principal 123");
        envio.setEstado("PENDIENTE");
        envio.setFechaEnvio(LocalDateTime.now());

        requestDTO = new EnvioRequestDTO();
        requestDTO.setPedidoId(10L);
        requestDTO.setDireccion("Calle Principal 123");

        pedidoDTO = new PedidoDTO(10L, 1L, 5L, 2, 31980.0, "CONFIRMADO");
    }

    @Test
    void listarEnvios_retornaListaConNombreDePedido() {
        when(envioRepository.findAll()).thenReturn(List.of(envio));
        when(pedidoClient.obtenerPedidoPorId(10L)).thenReturn(pedidoDTO);

        List<EnvioResponseDTO> resultado = envioService.listarEnvios();

        assertEquals(1, resultado.size());
        assertEquals("Pedido #10", resultado.get(0).getNombrePedido());
    }

    @Test
    void buscarPorId_envioExistente_retornaDTO() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(pedidoClient.obtenerPedidoPorId(10L)).thenReturn(pedidoDTO);

        EnvioResponseDTO resultado = envioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Calle Principal 123", resultado.getDireccion());
    }

    @Test
    void buscarPorId_envioNoExistente_lanzaExcepcion() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> envioService.buscarPorId(99L));
    }

    @Test
    void crearEnvio_pedidoExiste_creaCorrectamente() {
        when(pedidoClient.obtenerPedidoPorId(10L)).thenReturn(pedidoDTO);
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);

        EnvioResponseDTO resultado = envioService.crearEnvio(requestDTO);

        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado());
        assertNotNull(resultado.getFechaEnvio());
        verify(envioRepository).save(any(Envio.class));
    }

    @Test
    void crearEnvio_pedidoNoExiste_lanzaExcepcion() {
        when(pedidoClient.obtenerPedidoPorId(10L)).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> envioService.crearEnvio(requestDTO));

        verify(envioRepository, never()).save(any());
    }

    @Test
    void actualizarEnvio_envioNoExistente_lanzaExcepcion() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> envioService.actualizarEnvio(99L, requestDTO));

        verify(envioRepository, never()).save(any());
    }

    @Test
    void eliminarEnvio_envioExistente_eliminaCorrectamente() {
        when(envioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(envioRepository).deleteById(1L);

        envioService.eliminarEnvio(1L);

        verify(envioRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarEnvio_envioNoExistente_lanzaExcepcion() {
        when(envioRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> envioService.eliminarEnvio(99L));
    }
}
