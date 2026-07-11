package com.example.ms_opinion.service;

import com.example.ms_opinion.client.ClienteClient;
import com.example.ms_opinion.client.ProductoClient;
import com.example.ms_opinion.dto.ClienteDTO;
import com.example.ms_opinion.dto.OpinionRequestDTO;
import com.example.ms_opinion.dto.OpinionResponseDTO;
import com.example.ms_opinion.dto.ProductoDTO;
import com.example.ms_opinion.model.Opinion;
import com.example.ms_opinion.repository.OpinionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpinionService {

    private static final Logger logger = LoggerFactory.getLogger(OpinionService.class);

    private final OpinionRepository opinionRepository;
    private final ClienteClient clienteClient;
    private final ProductoClient productoClient;

    public List<OpinionResponseDTO> listarOpiniones() {
        logger.info("Listando todas las opiniones");
        return opinionRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public OpinionResponseDTO buscarPorId(Long id) {
        logger.info("Buscando opinion con id: {}", id);
        Opinion opinion = opinionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Opinion no encontrada con id: {}", id);
                    return new RuntimeException("Opinion no encontrada con id: " + id);
                });
        return convertirAResponseDTO(opinion);
    }

    public OpinionResponseDTO crearOpinion(OpinionRequestDTO requestDTO) {
        logger.info("Validando que el cliente id={} exista en ms-cliente", requestDTO.getClienteId());
        ClienteDTO cliente = clienteClient.obtenerClientePorId(requestDTO.getClienteId());
        if (cliente == null) {
            logger.warn("ms-cliente no encontro el cliente con id: {}", requestDTO.getClienteId());
            throw new RuntimeException("El cliente con id " + requestDTO.getClienteId() + " no existe en ms-cliente");
        }

        logger.info("Validando que el producto id={} exista en ms-producto", requestDTO.getProductoId());
        ProductoDTO producto = productoClient.obtenerProductoPorId(requestDTO.getProductoId());
        if (producto == null) {
            logger.warn("ms-producto no encontro el producto con id: {}", requestDTO.getProductoId());
            throw new RuntimeException("El producto con id " + requestDTO.getProductoId() + " no existe en ms-producto");
        }

        Opinion opinion = new Opinion();
        opinion.setClienteId(requestDTO.getClienteId());
        opinion.setProductoId(requestDTO.getProductoId());
        opinion.setPuntuacion(requestDTO.getPuntuacion());
        opinion.setComentario(requestDTO.getComentario());

        Opinion guardada = opinionRepository.save(opinion);
        logger.info("Opinion creada con id: {} para cliente: {} sobre producto: {}", guardada.getId(), cliente.getNombre(), producto.getNombre());
        return convertirAResponseDTO(guardada, cliente.getNombre(), producto.getNombre());
    }

    public OpinionResponseDTO actualizarOpinion(Long id, OpinionRequestDTO requestDTO) {
        Opinion opinion = opinionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Intento de actualizar opinion inexistente con id: {}", id);
                    return new RuntimeException("Opinion no encontrada con id: " + id);
                });

        logger.info("Validando que el cliente id={} exista en ms-cliente", requestDTO.getClienteId());
        ClienteDTO cliente = clienteClient.obtenerClientePorId(requestDTO.getClienteId());
        if (cliente == null) {
            logger.warn("ms-cliente no encontro el cliente con id: {}", requestDTO.getClienteId());
            throw new RuntimeException("El cliente con id " + requestDTO.getClienteId() + " no existe en ms-cliente");
        }

        logger.info("Validando que el producto id={} exista en ms-producto", requestDTO.getProductoId());
        ProductoDTO producto = productoClient.obtenerProductoPorId(requestDTO.getProductoId());
        if (producto == null) {
            logger.warn("ms-producto no encontro el producto con id: {}", requestDTO.getProductoId());
            throw new RuntimeException("El producto con id " + requestDTO.getProductoId() + " no existe en ms-producto");
        }

        opinion.setClienteId(requestDTO.getClienteId());
        opinion.setProductoId(requestDTO.getProductoId());
        opinion.setPuntuacion(requestDTO.getPuntuacion());
        opinion.setComentario(requestDTO.getComentario());

        Opinion actualizada = opinionRepository.save(opinion);
        logger.info("Opinion actualizada con id: {}", actualizada.getId());
        return convertirAResponseDTO(actualizada, cliente.getNombre(), producto.getNombre());
    }

    public void eliminarOpinion(Long id) {
        if (!opinionRepository.existsById(id)) {
            logger.warn("Intento de eliminar opinion inexistente con id: {}", id);
            throw new RuntimeException("Opinion no encontrada con id: " + id);
        }
        opinionRepository.deleteById(id);
        logger.info("Opinion eliminada con id: {}", id);
    }

    private OpinionResponseDTO convertirAResponseDTO(Opinion opinion) {
        ClienteDTO cliente = clienteClient.obtenerClientePorId(opinion.getClienteId());
        String nombreCliente = (cliente != null) ? cliente.getNombre() : "Cliente no disponible";

        ProductoDTO producto = productoClient.obtenerProductoPorId(opinion.getProductoId());
        String nombreProducto = (producto != null) ? producto.getNombre() : "Producto no disponible";

        return convertirAResponseDTO(opinion, nombreCliente, nombreProducto);
    }

    private OpinionResponseDTO convertirAResponseDTO(Opinion opinion, String nombreCliente, String nombreProducto) {
        return new OpinionResponseDTO(
                opinion.getId(),
                opinion.getClienteId(),
                nombreCliente,
                opinion.getProductoId(),
                nombreProducto,
                opinion.getPuntuacion(),
                opinion.getComentario()
        );
    }
}
