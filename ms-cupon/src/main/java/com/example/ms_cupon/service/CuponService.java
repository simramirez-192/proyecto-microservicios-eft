package com.example.ms_cupon.service;

import com.example.ms_cupon.dto.CuponRequestDTO;
import com.example.ms_cupon.dto.CuponResponseDTO;
import com.example.ms_cupon.model.Cupon;
import com.example.ms_cupon.repository.CuponRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuponService {

    private static final Logger logger = LoggerFactory.getLogger(CuponService.class);

    private final CuponRepository cuponRepository;

    public List<CuponResponseDTO> listarCupones() {
        logger.info("Listando todos los cupones");
        return cuponRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public CuponResponseDTO buscarPorId(Long id) {
        logger.info("Buscando cupon con id: {}", id);
        Cupon cupon = cuponRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Cupon no encontrado con id: {}", id);
                    return new RuntimeException("Cupon no encontrado con id: " + id);
                });
        return convertirAResponseDTO(cupon);
    }

    public CuponResponseDTO crearCupon(CuponRequestDTO requestDTO) {
        logger.info("Creando cupon con codigo: {}", requestDTO.getCodigo());

        if (cuponRepository.findByCodigo(requestDTO.getCodigo()).isPresent()) {
            logger.warn("Ya existe un cupon con el codigo: {}", requestDTO.getCodigo());
            throw new RuntimeException("Ya existe un cupon con el codigo: " + requestDTO.getCodigo());
        }

        Cupon cupon = new Cupon();
        cupon.setCodigo(requestDTO.getCodigo());
        cupon.setPorcentajeDescuento(requestDTO.getPorcentajeDescuento());
        cupon.setActivo(true);

        Cupon guardado = cuponRepository.save(cupon);
        logger.info("Cupon creado con id: {} y codigo: {}", guardado.getId(), guardado.getCodigo());
        return convertirAResponseDTO(guardado);
    }

    public CuponResponseDTO actualizarCupon(Long id, CuponRequestDTO requestDTO) {
        logger.info("Actualizando cupon con id: {}", id);
        Cupon cupon = cuponRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Intento de actualizar cupon inexistente con id: {}", id);
                    return new RuntimeException("Cupon no encontrado con id: " + id);
                });

        cupon.setCodigo(requestDTO.getCodigo());
        cupon.setPorcentajeDescuento(requestDTO.getPorcentajeDescuento());

        Cupon actualizado = cuponRepository.save(cupon);
        logger.info("Cupon actualizado con id: {}", actualizado.getId());
        return convertirAResponseDTO(actualizado);
    }

    public void eliminarCupon(Long id) {
        if (!cuponRepository.existsById(id)) {
            logger.warn("Intento de eliminar cupon inexistente con id: {}", id);
            throw new RuntimeException("Cupon no encontrado con id: " + id);
        }
        cuponRepository.deleteById(id);
        logger.info("Cupon eliminado con id: {}", id);
    }

    private CuponResponseDTO convertirAResponseDTO(Cupon cupon) {
        return new CuponResponseDTO(
                cupon.getId(),
                cupon.getCodigo(),
                cupon.getPorcentajeDescuento(),
                cupon.getActivo()
        );
    }
}
