package com.example.ms_cliente.service;

import com.example.ms_cliente.dto.ClienteRequestDTO;
import com.example.ms_cliente.dto.ClienteResponseDTO;
import com.example.ms_cliente.model.Cliente;
import com.example.ms_cliente.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;
    private ClienteRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Ana Torres");
        cliente.setEmail("ana@email.com");
        cliente.setTelefono("+56911112222");
        cliente.setDireccion("Av. Siempre Viva 123");

        requestDTO = new ClienteRequestDTO();
        requestDTO.setNombre("Ana Torres");
        requestDTO.setEmail("ana@email.com");
        requestDTO.setTelefono("+56911112222");
        requestDTO.setDireccion("Av. Siempre Viva 123");
    }

    @Test
    void listarClientes_retornaListaDeClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        List<ClienteResponseDTO> resultado = clienteService.listarClientes();

        assertEquals(1, resultado.size());
        assertEquals("Ana Torres", resultado.get(0).getNombre());
    }

    @Test
    void buscarPorId_clienteExistente_retornaDTO() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        ClienteResponseDTO resultado = clienteService.buscarPorId(1L);

        assertEquals("ana@email.com", resultado.getEmail());
    }

    @Test
    void buscarPorId_clienteNoExistente_lanzaExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> clienteService.buscarPorId(99L));
    }

    @Test
    void crearCliente_datosValidos_creaYRetornaCliente() {
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        ClienteResponseDTO resultado = clienteService.crearCliente(requestDTO);

        assertNotNull(resultado);
        assertEquals("Ana Torres", resultado.getNombre());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void actualizarCliente_clienteExistente_actualizaYRetorna() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        ClienteResponseDTO resultado = clienteService.actualizarCliente(1L, requestDTO);

        assertNotNull(resultado);
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void actualizarCliente_clienteNoExistente_lanzaExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> clienteService.actualizarCliente(99L, requestDTO));

        verify(clienteRepository, never()).save(any());
    }

    @Test
    void eliminarCliente_clienteExistente_eliminaCorrectamente() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(1L);

        clienteService.eliminarCliente(1L);

        verify(clienteRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarCliente_clienteNoExistente_lanzaExcepcion() {
        when(clienteRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> clienteService.eliminarCliente(99L));
    }
}
