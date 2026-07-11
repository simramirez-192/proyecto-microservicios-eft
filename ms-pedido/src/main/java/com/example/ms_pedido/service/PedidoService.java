package com.example.ms_pedido.service;

import com.example.ms_pedido.client.ClienteClient;
import com.example.ms_pedido.client.ProductoClient;
import com.example.ms_pedido.dto.ClienteDTO;
import com.example.ms_pedido.dto.PedidoRequestDTO;
import com.example.ms_pedido.dto.PedidoResponseDTO;
import com.example.ms_pedido.dto.ProductoDTO;
import com.example.ms_pedido.model.Pedido;
import com.example.ms_pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepository;
    private final ClienteClient clienteClient;   // comunicacion con ms-cliente
    private final ProductoClient productoClient; // comunicacion con ms-producto

    // GET - listar todos los pedidos
    public List<PedidoResponseDTO> listarPedidos() {
        logger.info("Listando todos los pedidos");
        return pedidoRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    // GET - buscar un pedido por id
    public PedidoResponseDTO buscarPorId(Long id) {
        logger.info("Buscando pedido con id: {}", id);
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pedido no encontrado con id: {}", id);
                    return new RuntimeException("Pedido no encontrado con id: " + id);
                });
        return convertirAResponseDTO(pedido);
    }

    // POST - crear un pedido, validando que el cliente y el producto existan
    public PedidoResponseDTO crearPedido(PedidoRequestDTO requestDTO) {
        logger.info("Creando pedido - consultando cliente id: {} y producto id: {}",
                requestDTO.getClienteId(), requestDTO.getProductoId());

        ClienteDTO cliente = clienteClient.obtenerClientePorId(requestDTO.getClienteId());
        if (cliente == null) {
            logger.warn("ms-cliente no encontro el cliente con id: {}", requestDTO.getClienteId());
            throw new RuntimeException("El cliente con id " + requestDTO.getClienteId() + " no existe en ms-cliente");
        }

        ProductoDTO producto = productoClient.obtenerProductoPorId(requestDTO.getProductoId());
        if (producto == null) {
            logger.warn("ms-producto no encontro el producto con id: {}", requestDTO.getProductoId());
            throw new RuntimeException("El producto con id " + requestDTO.getProductoId() + " no existe en ms-producto");
        }

        Pedido pedido = new Pedido();
        pedido.setClienteId(requestDTO.getClienteId());
        pedido.setProductoId(requestDTO.getProductoId());
        pedido.setCantidad(requestDTO.getCantidad());
        pedido.setTotal(producto.getPrecio() * requestDTO.getCantidad());
        pedido.setEstado("PENDIENTE");

        Pedido guardado = pedidoRepository.save(pedido);
        logger.info("Pedido creado con id: {} - total: {}", guardado.getId(), guardado.getTotal());
        return convertirAResponseDTO(guardado, cliente.getNombre(), producto.getNombre());
    }

    // PUT - actualizar un pedido existente
    public PedidoResponseDTO actualizarPedido(Long id, PedidoRequestDTO requestDTO) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con id: " + id));

        ClienteDTO cliente = clienteClient.obtenerClientePorId(requestDTO.getClienteId());
        if (cliente == null) {
            throw new RuntimeException("El cliente con id " + requestDTO.getClienteId() + " no existe en ms-cliente");
        }

        ProductoDTO producto = productoClient.obtenerProductoPorId(requestDTO.getProductoId());
        if (producto == null) {
            throw new RuntimeException("El producto con id " + requestDTO.getProductoId() + " no existe en ms-producto");
        }

        pedido.setClienteId(requestDTO.getClienteId());
        pedido.setProductoId(requestDTO.getProductoId());
        pedido.setCantidad(requestDTO.getCantidad());
        pedido.setTotal(producto.getPrecio() * requestDTO.getCantidad());

        Pedido actualizado = pedidoRepository.save(pedido);
        return convertirAResponseDTO(actualizado, cliente.getNombre(), producto.getNombre());
    }

    // DELETE - eliminar un pedido existente
    public void eliminarPedido(Long id) {
        if (!pedidoRepository.existsById(id)) {
            logger.warn("Intento de eliminar pedido inexistente con id: {}", id);
            throw new RuntimeException("Pedido no encontrado con id: " + id);
        }
        pedidoRepository.deleteById(id);
        logger.info("Pedido eliminado con id: {}", id);
    }

    // Convierte la entidad en DTO, consultando el nombre del cliente y del producto
    private PedidoResponseDTO convertirAResponseDTO(Pedido pedido) {
        ClienteDTO cliente = clienteClient.obtenerClientePorId(pedido.getClienteId());
        ProductoDTO producto = productoClient.obtenerProductoPorId(pedido.getProductoId());

        String nombreCliente = (cliente != null) ? cliente.getNombre() : "Cliente no disponible";
        String nombreProducto = (producto != null) ? producto.getNombre() : "Producto no disponible";

        return convertirAResponseDTO(pedido, nombreCliente, nombreProducto);
    }

    private PedidoResponseDTO convertirAResponseDTO(Pedido pedido, String nombreCliente, String nombreProducto) {
        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getClienteId(),
                nombreCliente,
                pedido.getProductoId(),
                nombreProducto,
                pedido.getCantidad(),
                pedido.getTotal(),
                pedido.getEstado()
        );
    }
}
