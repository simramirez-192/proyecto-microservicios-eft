package com.example.ms_notificacion.service;

import com.example.ms_notificacion.client.ClienteClient;
import com.example.ms_notificacion.dto.ClienteDTO;
import com.example.ms_notificacion.dto.NotificacionRequestDTO;
import com.example.ms_notificacion.dto.NotificacionResponseDTO;
import com.example.ms_notificacion.model.Notificacion;
import com.example.ms_notificacion.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionService.class);

    private final NotificacionRepository notificacionRepository;
    private final ClienteClient clienteClient;

    // GET - listar todas las notificaciones
    public List<NotificacionResponseDTO> listarNotificaciones() {
        logger.info("Listando todas las notificaciones");
        return notificacionRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    // GET - buscar una notificacion por id
    public NotificacionResponseDTO buscarPorId(Long id) {
        logger.info("Buscando notificacion con id: {}", id);
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Notificacion no encontrada con id: {}", id);
                    return new RuntimeException("Notificacion no encontrada con id: " + id);
                });
        return convertirAResponseDTO(notificacion);
    }

    // POST - crear una notificacion, validando que el cliente exista en ms-cliente
    public NotificacionResponseDTO crearNotificacion(NotificacionRequestDTO requestDTO) {
        logger.info("Consultando a ms-cliente por el cliente id: {}", requestDTO.getClienteId());
        ClienteDTO cliente = clienteClient.obtenerClientePorId(requestDTO.getClienteId());

        if (cliente == null) {
            logger.warn("ms-cliente no encontro el cliente con id: {}", requestDTO.getClienteId());
            throw new RuntimeException("El cliente con id " + requestDTO.getClienteId() + " no existe en ms-cliente");
        }

        Notificacion notificacion = new Notificacion();
        notificacion.setClienteId(requestDTO.getClienteId());
        notificacion.setMensaje(requestDTO.getMensaje());
        notificacion.setTipo(requestDTO.getTipo());
        notificacion.setLeida(false);
        notificacion.setFechaEnvio(LocalDateTime.now());

        Notificacion guardada = notificacionRepository.save(notificacion);
        logger.info("Notificacion creada con id: {} para cliente: {}", guardada.getId(), cliente.getNombre());
        return convertirAResponseDTO(guardada, cliente.getNombre());
    }

    // PATCH - marcar una notificacion como leida
    public NotificacionResponseDTO marcarComoLeida(Long id) {
        logger.info("Marcando notificacion como leida con id: {}", id);
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Notificacion no encontrada con id: {}", id);
                    return new RuntimeException("Notificacion no encontrada con id: " + id);
                });

        notificacion.setLeida(true);
        Notificacion actualizada = notificacionRepository.save(notificacion);
        logger.info("Notificacion marcada como leida con id: {}", id);
        return convertirAResponseDTO(actualizada);
    }

    // DELETE - eliminar una notificacion existente
    public void eliminarNotificacion(Long id) {
        if (!notificacionRepository.existsById(id)) {
            logger.warn("Intento de eliminar notificacion inexistente con id: {}", id);
            throw new RuntimeException("Notificacion no encontrada con id: " + id);
        }
        notificacionRepository.deleteById(id);
        logger.info("Notificacion eliminada con id: {}", id);
    }

    // Convierte la entidad en DTO, consultando el nombre del cliente en ms-cliente
    private NotificacionResponseDTO convertirAResponseDTO(Notificacion notificacion) {
        ClienteDTO cliente = clienteClient.obtenerClientePorId(notificacion.getClienteId());
        String nombreCliente = (cliente != null) ? cliente.getNombre() : "Cliente no disponible";
        return convertirAResponseDTO(notificacion, nombreCliente);
    }

    private NotificacionResponseDTO convertirAResponseDTO(Notificacion notificacion, String nombreCliente) {
        return new NotificacionResponseDTO(
                notificacion.getId(),
                notificacion.getClienteId(),
                nombreCliente,
                notificacion.getMensaje(),
                notificacion.getTipo(),
                notificacion.getLeida(),
                notificacion.getFechaEnvio()
        );
    }
}
