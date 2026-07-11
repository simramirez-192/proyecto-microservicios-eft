package com.example.ms_inventario.service;

import com.example.ms_inventario.client.ProductoClient;
import com.example.ms_inventario.dto.InventarioRequestDTO;
import com.example.ms_inventario.dto.InventarioResponseDTO;
import com.example.ms_inventario.dto.ProductoDTO;
import com.example.ms_inventario.model.Inventario;
import com.example.ms_inventario.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private static final Logger logger = LoggerFactory.getLogger(InventarioService.class);

    private final InventarioRepository inventarioRepository;
    private final ProductoClient productoClient; // aqui esta la comunicacion entre microservicios

    // GET - listar todos los registros de inventario
    public List<InventarioResponseDTO> listarInventario() {
        logger.info("Listando todo el inventario");
        return inventarioRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    // GET - buscar un registro por id
    public InventarioResponseDTO buscarPorId(Long id) {
        logger.info("Buscando registro de inventario con id: {}", id);
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Registro de inventario no encontrado con id: {}", id);
                    return new RuntimeException("Registro de inventario no encontrado con id: " + id);
                });
        return convertirAResponseDTO(inventario);
    }

    // POST - crear un registro de inventario, validando que el producto exista en ms-producto
    public InventarioResponseDTO crearInventario(InventarioRequestDTO requestDTO) {
        logger.info("Consultando a ms-producto por el producto id: {}", requestDTO.getProductoId());
        ProductoDTO producto = productoClient.obtenerProductoPorId(requestDTO.getProductoId());

        if (producto == null) {
            logger.warn("ms-producto no encontro el producto con id: {}", requestDTO.getProductoId());
            throw new RuntimeException("El producto con id " + requestDTO.getProductoId() + " no existe en ms-producto");
        }

        Inventario inventario = new Inventario();
        inventario.setProductoId(requestDTO.getProductoId());
        inventario.setCantidadDisponible(requestDTO.getCantidadDisponible());

        Inventario guardado = inventarioRepository.save(inventario);
        logger.info("Registro de inventario creado con id: {} para producto: {}", guardado.getId(), producto.getNombre());
        return convertirAResponseDTO(guardado, producto.getNombre());
    }

    // PUT - actualizar la cantidad disponible de un registro existente
    public InventarioResponseDTO actualizarInventario(Long id, InventarioRequestDTO requestDTO) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Intento de actualizar registro de inventario inexistente con id: {}", id);
                    return new RuntimeException("Registro de inventario no encontrado con id: " + id);
                });

        ProductoDTO producto = productoClient.obtenerProductoPorId(requestDTO.getProductoId());
        if (producto == null) {
            logger.warn("ms-producto no encontro el producto con id: {}", requestDTO.getProductoId());
            throw new RuntimeException("El producto con id " + requestDTO.getProductoId() + " no existe en ms-producto");
        }

        inventario.setProductoId(requestDTO.getProductoId());
        inventario.setCantidadDisponible(requestDTO.getCantidadDisponible());

        Inventario actualizado = inventarioRepository.save(inventario);
        logger.info("Registro de inventario actualizado con id: {}", actualizado.getId());
        return convertirAResponseDTO(actualizado, producto.getNombre());
    }

    // DELETE - eliminar un registro de inventario existente
    public void eliminarInventario(Long id) {
        if (!inventarioRepository.existsById(id)) {
            logger.warn("Intento de eliminar registro de inventario inexistente con id: {}", id);
            throw new RuntimeException("Registro de inventario no encontrado con id: " + id);
        }
        inventarioRepository.deleteById(id);
        logger.info("Registro de inventario eliminado con id: {}", id);
    }

    // Convierte la entidad en DTO, consultando el nombre del producto en ms-producto
    private InventarioResponseDTO convertirAResponseDTO(Inventario inventario) {
        ProductoDTO producto = productoClient.obtenerProductoPorId(inventario.getProductoId());
        String nombreProducto = (producto != null) ? producto.getNombre() : "Producto no disponible";
        return convertirAResponseDTO(inventario, nombreProducto);
    }

    private InventarioResponseDTO convertirAResponseDTO(Inventario inventario, String nombreProducto) {
        return new InventarioResponseDTO(
                inventario.getId(),
                inventario.getProductoId(),
                nombreProducto,
                inventario.getCantidadDisponible()
        );
    }
}
