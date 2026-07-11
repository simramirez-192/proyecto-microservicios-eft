package com.example.ms_pago.service;

import com.example.ms_pago.client.PedidoClient;
import com.example.ms_pago.dto.PagoRequestDTO;
import com.example.ms_pago.dto.PagoResponseDTO;
import com.example.ms_pago.dto.PedidoDTO;
import com.example.ms_pago.model.Pago;
import com.example.ms_pago.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {

    private static final Logger logger = LoggerFactory.getLogger(PagoService.class);

    private final PagoRepository pagoRepository;
    private final PedidoClient pedidoClient; // comunicacion con ms-pedido

    // GET - listar todos los pagos
    public List<PagoResponseDTO> listarPagos() {
        logger.info("Listando todos los pagos");
        return pagoRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    // GET - buscar un pago por id
    public PagoResponseDTO buscarPorId(Long id) {
        logger.info("Buscando pago con id: {}", id);
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pago no encontrado con id: {}", id);
                    return new RuntimeException("Pago no encontrado con id: " + id);
                });
        return convertirAResponseDTO(pago);
    }

    // POST - crear un pago, validando que el pedido exista en ms-pedido
    public PagoResponseDTO crearPago(PagoRequestDTO requestDTO) {
        logger.info("Creando pago - consultando pedido id: {}", requestDTO.getPedidoId());
        PedidoDTO pedido = pedidoClient.obtenerPedidoPorId(requestDTO.getPedidoId());

        if (pedido == null) {
            logger.warn("ms-pedido no encontro el pedido con id: {}", requestDTO.getPedidoId());
            throw new RuntimeException("El pedido con id " + requestDTO.getPedidoId() + " no existe en ms-pedido");
        }

        Pago pago = new Pago();
        pago.setPedidoId(requestDTO.getPedidoId());
        pago.setMonto(pedido.getTotal());
        pago.setMetodoPago(requestDTO.getMetodoPago());
        pago.setEstado("PAGADO");

        Pago guardado = pagoRepository.save(pago);
        logger.info("Pago creado con id: {} - monto: {}", guardado.getId(), guardado.getMonto());
        return convertirAResponseDTO(guardado);
    }

    // PUT - actualizar un pago existente
    public PagoResponseDTO actualizarPago(Long id, PagoRequestDTO requestDTO) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Intento de actualizar pago inexistente con id: {}", id);
                    return new RuntimeException("Pago no encontrado con id: " + id);
                });

        PedidoDTO pedido = pedidoClient.obtenerPedidoPorId(requestDTO.getPedidoId());
        if (pedido == null) {
            logger.warn("ms-pedido no encontro el pedido con id: {}", requestDTO.getPedidoId());
            throw new RuntimeException("El pedido con id " + requestDTO.getPedidoId() + " no existe en ms-pedido");
        }

        pago.setPedidoId(requestDTO.getPedidoId());
        pago.setMonto(pedido.getTotal());
        pago.setMetodoPago(requestDTO.getMetodoPago());

        Pago actualizado = pagoRepository.save(pago);
        logger.info("Pago actualizado con id: {}", actualizado.getId());
        return convertirAResponseDTO(actualizado);
    }

    // DELETE - eliminar un pago existente
    public void eliminarPago(Long id) {
        if (!pagoRepository.existsById(id)) {
            logger.warn("Intento de eliminar pago inexistente con id: {}", id);
            throw new RuntimeException("Pago no encontrado con id: " + id);
        }
        pagoRepository.deleteById(id);
        logger.info("Pago eliminado con id: {}", id);
    }

    // Convierte la entidad en DTO
    private PagoResponseDTO convertirAResponseDTO(Pago pago) {
        return new PagoResponseDTO(
                pago.getId(),
                pago.getPedidoId(),
                pago.getMonto(),
                pago.getMetodoPago(),
                pago.getEstado()
        );
    }
}
