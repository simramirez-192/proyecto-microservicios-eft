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

    @Test
    void listarEnvios_listaVacia_retornaListaVacia() {
        when(envioRepository.findAll()).thenReturn(List.of());

        List<EnvioResponseDTO> resultado = envioService.listarEnvios();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(pedidoClient, never()).obtenerPedidoPorId(any());
    }

    @Test
    void listarEnvios_pedidoNoDisponible_nombreNoDisponible() {
        when(envioRepository.findAll()).thenReturn(List.of(envio));
        when(pedidoClient.obtenerPedidoPorId(10L)).thenReturn(null);

        List<EnvioResponseDTO> resultado = envioService.listarEnvios();

        assertEquals(1, resultado.size());
        assertEquals("Pedido no disponible", resultado.get(0).getNombrePedido());
    }

    @Test
    void buscarPorId_pedidoNoDisponible_nombreNoDisponible() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(pedidoClient.obtenerPedidoPorId(10L)).thenReturn(null);

        EnvioResponseDTO resultado = envioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Pedido no disponible", resultado.getNombrePedido());
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getPedidoId());
        assertEquals("Calle Principal 123", resultado.getDireccion());
    }

    @Test
    void actualizarEnvio_datosValidos_actualizaCorrectamente() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(pedidoClient.obtenerPedidoPorId(10L)).thenReturn(pedidoDTO);
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);

        EnvioResponseDTO resultado = envioService.actualizarEnvio(1L, requestDTO);

        assertEquals("Pedido #10", resultado.getNombrePedido());
        verify(envioRepository).save(envio);
    }

    @Test
    void actualizarEnvio_pedidoNoExiste_lanzaExcepcion() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(pedidoClient.obtenerPedidoPorId(10L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> envioService.actualizarEnvio(1L, requestDTO));
        verify(envioRepository, never()).save(any());
    }

    @Test
    void crearEnvio_guardaCamposCorrectamente() {
        Envio guardado = new Envio();
        guardado.setId(5L);
        guardado.setPedidoId(10L);
        guardado.setDireccion("Calle Principal 123");
        guardado.setEstado("PENDIENTE");
        guardado.setFechaEnvio(LocalDateTime.now());

        when(pedidoClient.obtenerPedidoPorId(10L)).thenReturn(pedidoDTO);
        when(envioRepository.save(any(Envio.class))).thenReturn(guardado);

        EnvioResponseDTO resultado = envioService.crearEnvio(requestDTO);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
        assertEquals(10L, resultado.getPedidoId());
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals("Pedido #10", resultado.getNombrePedido());

        verify(envioRepository).save(argThat(env ->
                env.getPedidoId().equals(10L) &&
                env.getDireccion().equals("Calle Principal 123") &&
                env.getEstado().equals("PENDIENTE") &&
                env.getFechaEnvio() != null
        ));
    }

    @Test
    void actualizarEnvio_verificaCamposActualizados() {
        EnvioRequestDTO requestActualizado = new EnvioRequestDTO();
        requestActualizado.setPedidoId(20L);
        requestActualizado.setDireccion("Avenida Nueva 456");

        PedidoDTO otroPedido = new PedidoDTO(20L, 3L, 8L, 1, 25000.0, "ENVIADO");

        Envio actualizado = new Envio();
        actualizado.setId(1L);
        actualizado.setPedidoId(20L);
        actualizado.setDireccion("Avenida Nueva 456");
        actualizado.setEstado("PENDIENTE");
        actualizado.setFechaEnvio(LocalDateTime.now());

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(pedidoClient.obtenerPedidoPorId(20L)).thenReturn(otroPedido);
        when(envioRepository.save(any(Envio.class))).thenReturn(actualizado);

        EnvioResponseDTO resultado = envioService.actualizarEnvio(1L, requestActualizado);

        assertEquals("Pedido #20", resultado.getNombrePedido());
        assertEquals("Avenida Nueva 456", resultado.getDireccion());
        verify(envioRepository).save(argThat(e ->
                e.getPedidoId().equals(20L) &&
                e.getDireccion().equals("Avenida Nueva 456")
        ));
    }

    @Test
    void listarEnviosMultiplesItems_todosLosNombresCorrectos() {
        Envio envio2 = new Envio();
        envio2.setId(2L);
        envio2.setPedidoId(20L);
        envio2.setDireccion("Avenida Nueva 456");
        envio2.setEstado("ENVIADO");
        envio2.setFechaEnvio(LocalDateTime.now());

        PedidoDTO pedido2 = new PedidoDTO(20L, 3L, 8L, 1, 25000.0, "ENVIADO");

        when(envioRepository.findAll()).thenReturn(List.of(envio, envio2));
        when(pedidoClient.obtenerPedidoPorId(10L)).thenReturn(pedidoDTO);
        when(pedidoClient.obtenerPedidoPorId(20L)).thenReturn(pedido2);

        List<EnvioResponseDTO> resultado = envioService.listarEnvios();

        assertEquals(2, resultado.size());
        assertEquals("Pedido #10", resultado.get(0).getNombrePedido());
        assertEquals("Pedido #20", resultado.get(1).getNombrePedido());
    }

    @Test
    void buscarPorId_retornaTodosLosCampos() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(pedidoClient.obtenerPedidoPorId(10L)).thenReturn(pedidoDTO);

        EnvioResponseDTO resultado = envioService.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getPedidoId());
        assertEquals("Pedido #10", resultado.getNombrePedido());
        assertEquals("Calle Principal 123", resultado.getDireccion());
        assertEquals("PENDIENTE", resultado.getEstado());
        assertNotNull(resultado.getFechaEnvio());
    }
}
