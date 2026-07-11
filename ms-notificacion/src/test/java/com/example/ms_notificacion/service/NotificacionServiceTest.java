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
    void marcarComoLeida_notificacionExistente_cambiaEstado() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);

        NotificacionResponseDTO resultado = notificacionService.marcarComoLeida(1L);

        assertNotNull(resultado);
        assertTrue(notificacion.getLeida());
        verify(notificacionRepository).save(notificacion);
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
    }
}
