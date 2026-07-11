package com.example.ms_envio.service;

import com.example.ms_envio.client.PedidoClient;
import com.example.ms_envio.dto.EnvioRequestDTO;
import com.example.ms_envio.dto.EnvioResponseDTO;
import com.example.ms_envio.dto.PedidoDTO;
import com.example.ms_envio.model.Envio;
import com.example.ms_envio.repository.EnvioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnvioService {

    private static final Logger logger = LoggerFactory.getLogger(EnvioService.class);

    private final EnvioRepository envioRepository;
    private final PedidoClient pedidoClient;

    public List<EnvioResponseDTO> listarEnvios() {
        logger.info("Listando todos los envios");
        return envioRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public EnvioResponseDTO buscarPorId(Long id) {
        logger.info("Buscando envio con id: {}", id);
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Envio no encontrado con id: {}", id);
                    return new RuntimeException("Envio no encontrado con id: " + id);
                });
        return convertirAResponseDTO(envio);
    }

    public EnvioResponseDTO crearEnvio(EnvioRequestDTO requestDTO) {
        logger.info("Consultando a ms-pedido por el pedido id: {}", requestDTO.getPedidoId());
        PedidoDTO pedido = pedidoClient.obtenerPedidoPorId(requestDTO.getPedidoId());

        if (pedido == null) {
            logger.warn("ms-pedido no encontro el pedido con id: {}", requestDTO.getPedidoId());
            throw new RuntimeException("El pedido con id " + requestDTO.getPedidoId() + " no existe en ms-pedido");
        }

        Envio envio = new Envio();
        envio.setPedidoId(requestDTO.getPedidoId());
        envio.setDireccion(requestDTO.getDireccion());
        envio.setEstado("PENDIENTE");
        envio.setFechaEnvio(LocalDateTime.now());

        Envio guardado = envioRepository.save(envio);
        logger.info("Envio creado con id: {} para pedido: {}", guardado.getId(), pedido.getId());
        return convertirAResponseDTO(guardado, "Pedido #" + pedido.getId());
    }

    public EnvioResponseDTO actualizarEnvio(Long id, EnvioRequestDTO requestDTO) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Intento de actualizar envio inexistente con id: {}", id);
                    return new RuntimeException("Envio no encontrado con id: " + id);
                });

        PedidoDTO pedido = pedidoClient.obtenerPedidoPorId(requestDTO.getPedidoId());
        if (pedido == null) {
            logger.warn("ms-pedido no encontro el pedido con id: {}", requestDTO.getPedidoId());
            throw new RuntimeException("El pedido con id " + requestDTO.getPedidoId() + " no existe en ms-pedido");
        }

        envio.setPedidoId(requestDTO.getPedidoId());
        envio.setDireccion(requestDTO.getDireccion());

        Envio actualizado = envioRepository.save(envio);
        logger.info("Envio actualizado con id: {}", actualizado.getId());
        return convertirAResponseDTO(actualizado, "Pedido #" + pedido.getId());
    }

    public void eliminarEnvio(Long id) {
        if (!envioRepository.existsById(id)) {
            logger.warn("Intento de eliminar envio inexistente con id: {}", id);
            throw new RuntimeException("Envio no encontrado con id: " + id);
        }
        envioRepository.deleteById(id);
        logger.info("Envio eliminado con id: {}", id);
    }

    private EnvioResponseDTO convertirAResponseDTO(Envio envio) {
        PedidoDTO pedido = pedidoClient.obtenerPedidoPorId(envio.getPedidoId());
        String nombrePedido = (pedido != null) ? "Pedido #" + pedido.getId() : "Pedido no disponible";
        return convertirAResponseDTO(envio, nombrePedido);
    }

    private EnvioResponseDTO convertirAResponseDTO(Envio envio, String nombrePedido) {
        return new EnvioResponseDTO(
                envio.getId(),
                envio.getPedidoId(),
                nombrePedido,
                envio.getDireccion(),
                envio.getEstado(),
                envio.getFechaEnvio()
        );
    }
}
