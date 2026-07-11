package com.example.ms_cliente.service;

import com.example.ms_cliente.dto.ClienteRequestDTO;
import com.example.ms_cliente.dto.ClienteResponseDTO;
import com.example.ms_cliente.model.Cliente;
import com.example.ms_cliente.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepository clienteRepository;

    // GET - listar todos los clientes
    public List<ClienteResponseDTO> listarClientes() {
        logger.info("Listando todos los clientes");
        return clienteRepository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    // GET - buscar un cliente por id
    public ClienteResponseDTO buscarPorId(Long id) {
        logger.info("Buscando cliente con id: {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Cliente no encontrado con id: {}", id);
                    return new RuntimeException("Cliente no encontrado con id: " + id);
                });
        return convertirAResponseDTO(cliente);
    }

    // POST - crear un cliente nuevo
    public ClienteResponseDTO crearCliente(ClienteRequestDTO requestDTO) {
        Cliente cliente = new Cliente();
        cliente.setNombre(requestDTO.getNombre());
        cliente.setEmail(requestDTO.getEmail());
        cliente.setTelefono(requestDTO.getTelefono());
        cliente.setDireccion(requestDTO.getDireccion());

        Cliente guardado = clienteRepository.save(cliente);
        logger.info("Cliente creado con id: {} - nombre: {}", guardado.getId(), guardado.getNombre());
        return convertirAResponseDTO(guardado);
    }

    // PUT - actualizar un cliente existente
    public ClienteResponseDTO actualizarCliente(Long id, ClienteRequestDTO requestDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Intento de actualizar cliente inexistente con id: {}", id);
                    return new RuntimeException("Cliente no encontrado con id: " + id);
                });

        cliente.setNombre(requestDTO.getNombre());
        cliente.setEmail(requestDTO.getEmail());
        cliente.setTelefono(requestDTO.getTelefono());
        cliente.setDireccion(requestDTO.getDireccion());

        Cliente actualizado = clienteRepository.save(cliente);
        logger.info("Cliente actualizado con id: {}", actualizado.getId());
        return convertirAResponseDTO(actualizado);
    }

    // DELETE - eliminar un cliente existente
    public void eliminarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            logger.warn("Intento de eliminar cliente inexistente con id: {}", id);
            throw new RuntimeException("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
        logger.info("Cliente eliminado con id: {}", id);
    }

    // Método de apoyo: convierte la entidad Cliente en un ClienteResponseDTO
    private ClienteResponseDTO convertirAResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getDireccion()
        );
    }
}
