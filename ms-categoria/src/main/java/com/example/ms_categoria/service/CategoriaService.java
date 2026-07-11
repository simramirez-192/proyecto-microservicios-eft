package com.example.ms_categoria.service;

import com.example.ms_categoria.dto.CategoriaRequestDTO;
import com.example.ms_categoria.dto.CategoriaResponseDTO;
import com.example.ms_categoria.model.Categoria;
import com.example.ms_categoria.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaService.class);

    private final CategoriaRepository categoriaRepository;

    // GET - listar todas las categorias
    public List<CategoriaResponseDTO> listarCategorias() {
        logger.info("Listando todas las categorias");
        return categoriaRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    // GET - buscar una categoria por id
    public CategoriaResponseDTO buscarPorId(Long id) {
        logger.info("Buscando categoria con id: {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Categoria no encontrada con id: {}", id);
                    return new RuntimeException("Categoria no encontrada con id: " + id);
                });
        return convertirAResponseDTO(categoria);
    }

    // POST - crear una categoria
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO requestDTO) {
        Categoria categoria = new Categoria();
        categoria.setNombre(requestDTO.getNombre());
        categoria.setDescripcion(requestDTO.getDescripcion());

        Categoria guardada = categoriaRepository.save(categoria);
        logger.info("Categoria creada con id: {} y nombre: {}", guardada.getId(), guardada.getNombre());
        return convertirAResponseDTO(guardada);
    }

    // PUT - actualizar una categoria existente
    public CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO requestDTO) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Intento de actualizar categoria inexistente con id: {}", id);
                    return new RuntimeException("Categoria no encontrada con id: " + id);
                });

        categoria.setNombre(requestDTO.getNombre());
        categoria.setDescripcion(requestDTO.getDescripcion());

        Categoria actualizada = categoriaRepository.save(categoria);
        logger.info("Categoria actualizada con id: {}", actualizada.getId());
        return convertirAResponseDTO(actualizada);
    }

    // DELETE - eliminar una categoria existente
    public void eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            logger.warn("Intento de eliminar categoria inexistente con id: {}", id);
            throw new RuntimeException("Categoria no encontrada con id: " + id);
        }
        categoriaRepository.deleteById(id);
        logger.info("Categoria eliminada con id: {}", id);
    }

    private CategoriaResponseDTO convertirAResponseDTO(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }
}
