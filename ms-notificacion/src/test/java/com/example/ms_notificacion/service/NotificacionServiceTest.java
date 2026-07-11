package com.example.ms_notificacion.service;

import com.example.ms_notificacion.client.ClienteClient;
import com.example.ms_notificacion.dto.ClienteDTO;
import com.example.ms_notificacion.dto.NotificacionRequestDTO;
import com.example.ms_notificacion.dto.NotificacionResponseDTO;
import com.example.ms_notificacion.model.Notificacion;
import com.example.ms_notificacion.repository.NotificacionRepository;
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
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private ClienteClient clienteClient;

    @InjectMocks
    private NotificacionService notificacionService;

    private Notificacion notificacion;
    private NotificacionRequestDTO requestDTO;
    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        notificacion = new Notificacion();
        notificacion.setId(1L);
        notificacion.setClienteId(10L);
        notificacion.setMensaje("Su pedido ha sido enviado");
        notificacion.setTipo("EMAIL");
        notificacion.setLeida(false);
        notificacion.setFechaEnvio(LocalDateTime.now());

        requestDTO = new NotificacionRequestDTO();
        requestDTO.setClienteId(10L);
        requestDTO.setMensaje("Su pedido ha sido enviado");
        requestDTO.setTipo("EMAIL");

        clienteDTO = new ClienteDTO(10L, "Juan Perez", "juan@email.com", "123456789", "Calle Falsa 123");
    }

    @Test
    void listarNotificaciones_retornaListaConNombreDeCliente() {
        when(notificacionRepository.findAll()).thenReturn(List.of(notificacion));
        when(clienteClient.obtenerClientePorId(10L)).thenReturn(clienteDTO);

        List<NotificacionResponseDTO> resultado = notificacionService.listarNotificaciones();

        assertEquals(1, resultado.size());
        assertEquals("Juan Perez", resultado.get(0).getNombreCliente());
    }

    @Test
    void listarNotificaciones_listaVacia_retornaListaVacia() {
        when(notificacionRepository.findAll()).thenReturn(List.of());

        List<NotificacionResponseDTO> resultado = notificacionService.listarNotificaciones();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(clienteClient, never()).obtenerClientePorId(any());
    }

    @Test
    void listarNotificaciones_clienteNoDisponible_nombreNoDisponible() {
        when(notificacionRepository.findAll()).thenReturn(List.of(notificacion));
        when(clienteClient.obtenerClientePorId(10L)).thenReturn(null);

        List<NotificacionResponseDTO> resultado = notificacionService.listarNotificaciones();

        assertEquals(1, resultado.size());
        assertEquals("Cliente no disponible", resultado.get(0).getNombreCliente());
    }

    @Test
    void listarNotificacionesMultiplesItems_todosLosNombresCorrectos() {
        Notificacion notif2 = new Notificacion();
        notif2.setId(2L);
        notif2.setClienteId(20L);
        notif2.setMensaje("Su cita ha sido confirmada");
        notif2.setTipo("SMS");
        notif2.setLeida(false);
        notif2.setFechaEnvio(LocalDateTime.now());

        ClienteDTO cliente2 = new ClienteDTO(20L, "Maria Lopez", "maria@email.com", "987654321", "Av. Principal 456");

        when(notificacionRepository.findAll()).thenReturn(List.of(notificacion, notif2));
        when(clienteClient.obtenerClientePorId(10L)).thenReturn(clienteDTO);
        when(clienteClient.obtenerClientePorId(20L)).thenReturn(cliente2);

        List<NotificacionResponseDTO> resultado = notificacionService.listarNotificaciones();

        assertEquals(2, resultado.size());
        assertEquals("Juan Perez", resultado.get(0).getNombreCliente());
        assertEquals("Maria Lopez", resultado.get(1).getNombreCliente());
    }

    @Test
    void buscarPorId_notificacionExistente_retornaDTO() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(clienteClient.obtenerClientePorId(10L)).thenReturn(clienteDTO);

        NotificacionResponseDTO resultado = notificacionService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Su pedido ha sido enviado", resultado.getMensaje());
        assertEquals("EMAIL", resultado.getTipo());
    }

    @Test
    void buscarPorId_notificacionNoExistente_lanzaExcepcion() {
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> notificacionService.buscarPorId(99L));
    }

    @Test
    void buscarPorId_clienteNoDisponible_nombreNoDisponible() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(clienteClient.obtenerClientePorId(10L)).thenReturn(null);

        NotificacionResponseDTO resultado = notificacionService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Cliente no disponible", resultado.getNombreCliente());
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getClienteId());
        assertEquals("Su pedido ha sido enviado", resultado.getMensaje());
    }

    @Test
    void buscarPorId_notificacionExistente_retornaTodosLosCampos() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(clienteClient.obtenerClientePorId(10L)).thenReturn(clienteDTO);

        NotificacionResponseDTO resultado = notificacionService.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getClienteId());
        assertEquals("Juan Perez", resultado.getNombreCliente());
        assertEquals("Su pedido ha sido enviado", resultado.getMensaje());
        assertEquals("EMAIL", resultado.getTipo());
        assertFalse(resultado.getLeida());
        assertNotNull(resultado.getFechaEnvio());
    }

    @Test
    void crearNotificacion_clienteExiste_creaCorrectamente() {
        when(clienteClient.obtenerClientePorId(10L)).thenReturn(clienteDTO);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);

        NotificacionResponseDTO resultado = notificacionService.crearNotificacion(requestDTO);

        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getNombreCliente());
        assertFalse(resultado.getLeida());
        assertNotNull(resultado.getFechaEnvio());
        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void crearNotificacion_clienteNoExiste_lanzaExcepcion() {
        when(clienteClient.obtenerClientePorId(10L)).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> notificacionService.crearNotificacion(requestDTO));

        verify(notificacionRepository, never()).save(any());
    }

    @Test
    void crearNotificacion_clienteExiste_guardaCorrectamenteLosCampos() {
        Notificacion guardada = new Notificacion();
        guardada.setId(5L);
        guardada.setClienteId(10L);
        guardada.setMensaje("Su pedido ha sido enviado");
        guardada.setTipo("EMAIL");
        guardada.setLeida(false);
        guardada.setFechaEnvio(LocalDateTime.now());

        when(clienteClient.obtenerClientePorId(10L)).thenReturn(clienteDTO);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(guardada);

        NotificacionResponseDTO resultado = notificacionService.crearNotificacion(requestDTO);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
        assertEquals(10L, resultado.getClienteId());
        assertEquals("Juan Perez", resultado.getNombreCliente());

        verify(notificacionRepository).save(argThat(notif ->
                notif.getClienteId().equals(10L) &&
                notif.getMensaje().equals("Su pedido ha sido enviado") &&
                notif.getTipo().equals("EMAIL") &&
                !notif.getLeida() &&
                notif.getFechaEnvio() != null
        ));
    }

    @Test
    void marcarComoLeida_notificacionExistente_cambiaEstado() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);

        NotificacionResponseDTO resultado = notificacionService.marcarComoLeida(1L);

        assertNotNull(resultado);
        assertTrue(notificacion.getLeida());
        verify(notificacionRepository).save(notificacion);
    }

    @Test
    void marcarComoLeida_notificacionNoExistente_lanzaExcepcion() {
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> notificacionService.marcarComoLeida(99L));

        verify(notificacionRepository, never()).save(any());
    }

    @Test
    void marcarComoLeida_verificaQueLeidaSeActualiza() {
        Notificacion notifNoLeida = new Notificacion();
        notifNoLeida.setId(2L);
        notifNoLeida.setClienteId(10L);
        notifNoLeida.setMensaje("Recordatorio de cita");
        notifNoLeida.setTipo("SMS");
        notifNoLeida.setLeida(false);
        notifNoLeida.setFechaEnvio(LocalDateTime.now());

        when(notificacionRepository.findById(2L)).thenReturn(Optional.of(notifNoLeida));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notifNoLeida);

        notificacionService.marcarComoLeida(2L);

        assertTrue(notifNoLeida.getLeida());
        verify(notificacionRepository).save(notifNoLeida);
    }

    @Test
    void eliminarNotificacion_notificacionExistente_eliminaCorrectamente() {
        when(notificacionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(notificacionRepository).deleteById(1L);

        notificacionService.eliminarNotificacion(1L);

        verify(notificacionRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarNotificacion_notificacionNoExistente_lanzaExcepcion() {
        when(notificacionRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> notificacionService.eliminarNotificacion(99L));

        verify(notificacionRepository, never()).deleteById(any());
    }

    @Test
    void eliminarNotificacion_notificacionExistente_noLlamaABuscarPorId() {
        when(notificacionRepository.existsById(1L)).thenReturn(true);

        notificacionService.eliminarNotificacion(1L);

        verify(notificacionRepository).existsById(1L);
        verify(notificacionRepository).deleteById(1L);
        verify(notificacionRepository, never()).findById(any());
    }
}
