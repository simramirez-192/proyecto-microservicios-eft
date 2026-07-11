package com.example.primer_proyecto.service;

import com.example.primer_proyecto.dto.ProductoRequestDTO;
import com.example.primer_proyecto.dto.ProductoResponseDTO;
import com.example.primer_proyecto.model.Producto;
import com.example.primer_proyecto.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    // El Logger permite dejar registro de lo que pasa en cada operacion.
    // Usamos el nombre de la clase para que en la consola se vea de donde viene cada mensaje.
    private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;

    // GET - listar todos los productos
    public List<ProductoResponseDTO> listarProductos() {
        logger.info("Listando todos los productos");
        return productoRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    // GET - buscar un producto por id
    public ProductoResponseDTO buscarPorId(Long id) {
        logger.info("Buscando producto con id: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Producto no encontrado con id: {}", id);
                    return new RuntimeException("Producto no encontrado con id: " + id);
                });
        return convertirAResponseDTO(producto);
    }

    // POST - crear un producto nuevo
    public ProductoResponseDTO crearProducto(ProductoRequestDTO requestDTO) {
        Producto producto = new Producto();
        producto.setNombre(requestDTO.getNombre());
        producto.setDescripcion(requestDTO.getDescripcion());
        producto.setPrecio(requestDTO.getPrecio());
        producto.setStock(requestDTO.getStock());

        Producto guardado = productoRepository.save(producto);
        logger.info("Producto creado con id: {} - nombre: {}", guardado.getId(), guardado.getNombre());
        return convertirAResponseDTO(guardado);
    }

    // PUT - actualizar un producto existente
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO requestDTO) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Intento de actualizar producto inexistente con id: {}", id);
                    return new RuntimeException("Producto no encontrado con id: " + id);
                });

        producto.setNombre(requestDTO.getNombre());
        producto.setDescripcion(requestDTO.getDescripcion());
        producto.setPrecio(requestDTO.getPrecio());
        producto.setStock(requestDTO.getStock());

        Producto actualizado = productoRepository.save(producto);
        logger.info("Producto actualizado con id: {}", actualizado.getId());
        return convertirAResponseDTO(actualizado);
    }

    // DELETE - eliminar un producto existente
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            logger.warn("Intento de eliminar producto inexistente con id: {}", id);
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
        logger.info("Producto eliminado con id: {}", id);
    }

    // Método de apoyo: convierte la entidad Producto en un ProductoResponseDTO
    private ProductoResponseDTO convertirAResponseDTO(Producto producto) {
        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock()
        );
    }
}
