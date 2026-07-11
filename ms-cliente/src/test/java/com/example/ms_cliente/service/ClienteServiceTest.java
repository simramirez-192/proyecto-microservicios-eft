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
import static org.mockito.ArgumentMatchers.argThat;
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

    @Test
    void listarClientes_listaVacia_retornaListaVacia() {
        when(clienteRepository.findAll()).thenReturn(List.of());

        List<ClienteResponseDTO> resultado = clienteService.listarClientes();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarClientes_multiplesClientes_retornaTodos() {
        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        cliente2.setNombre("Bob Smith");
        cliente2.setEmail("bob@email.com");
        cliente2.setTelefono("+56933334444");
        cliente2.setDireccion("Calle Falsa 456");

        when(clienteRepository.findAll()).thenReturn(List.of(cliente, cliente2));

        List<ClienteResponseDTO> resultado = clienteService.listarClientes();

        assertEquals(2, resultado.size());
        assertEquals("Ana Torres", resultado.get(0).getNombre());
        assertEquals("Bob Smith", resultado.get(1).getNombre());
    }

    @Test
    void buscarPorId_verificaCamposDelDTO() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        ClienteResponseDTO resultado = clienteService.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals("Ana Torres", resultado.getNombre());
        assertEquals("ana@email.com", resultado.getEmail());
        assertEquals("+56911112222", resultado.getTelefono());
        assertEquals("Av. Siempre Viva 123", resultado.getDireccion());
    }

    @Test
    void crearCliente_verificaCamposEnEntidad() {
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        clienteService.crearCliente(requestDTO);

        verify(clienteRepository).save(argThat(c ->
                c.getNombre().equals("Ana Torres") &&
                c.getEmail().equals("ana@email.com") &&
                c.getTelefono().equals("+56911112222") &&
                c.getDireccion().equals("Av. Siempre Viva 123")
        ));
    }

    @Test
    void crearCliente_retornaIdCorrecto() {
        Cliente guardado = new Cliente();
        guardado.setId(5L);
        guardado.setNombre("Nuevo Cliente");
        guardado.setEmail("nuevo@email.com");

        when(clienteRepository.save(any(Cliente.class))).thenReturn(guardado);

        ClienteResponseDTO resultado = clienteService.crearCliente(requestDTO);

        assertEquals(5L, resultado.getId());
        assertEquals("Nuevo Cliente", resultado.getNombre());
    }

    @Test
    void actualizarCliente_verificaCamposActualizados() {
        Cliente actualizado = new Cliente();
        actualizado.setId(1L);
        actualizado.setNombre("Nombre Nuevo");
        actualizado.setEmail("nuevo@email.com");
        actualizado.setTelefono("+56999990000");
        actualizado.setDireccion("Nueva Direccion 789");

        ClienteRequestDTO requestActualizado = new ClienteRequestDTO();
        requestActualizado.setNombre("Nombre Nuevo");
        requestActualizado.setEmail("nuevo@email.com");
        requestActualizado.setTelefono("+56999990000");
        requestActualizado.setDireccion("Nueva Direccion 789");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(actualizado);

        ClienteResponseDTO resultado = clienteService.actualizarCliente(1L, requestActualizado);

        assertEquals("Nombre Nuevo", resultado.getNombre());
        assertEquals("nuevo@email.com", resultado.getEmail());
        assertEquals("+56999990000", resultado.getTelefono());
        assertEquals("Nueva Direccion 789", resultado.getDireccion());
    }

    @Test
    void actualizarCliente_guardaTodosLosCampos() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        clienteService.actualizarCliente(1L, requestDTO);

        verify(clienteRepository).save(argThat(c ->
                c.getNombre().equals("Ana Torres") &&
                c.getEmail().equals("ana@email.com") &&
                c.getTelefono().equals("+56911112222") &&
                c.getDireccion().equals("Av. Siempre Viva 123")
        ));
    }

    @Test
    void eliminarCliente_clienteExistente_noLanzaExcepcion() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(1L);

        assertDoesNotThrow(() -> clienteService.eliminarCliente(1L));
    }

    @Test
    void eliminarCliente_clienteNoExistente_mensajeContieneId() {
        when(clienteRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.eliminarCliente(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void buscarPorId_clienteNoExistente_mensajeContieneId() {
        when(clienteRepository.findById(42L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.buscarPorId(42L));

        assertTrue(ex.getMessage().contains("42"));
    }

    @Test
    void actualizarCliente_clienteNoExistente_mensajeContieneId() {
        when(clienteRepository.findById(77L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.actualizarCliente(77L, requestDTO));

        assertTrue(ex.getMessage().contains("77"));
    }

    @Test
    void listarClientes_verificaQueSeLlamaFindAll() {
        when(clienteRepository.findAll()).thenReturn(List.of());

        clienteService.listarClientes();

        verify(clienteRepository).findAll();
    }

    @Test
    void crearCliente_conDatosMinimos_retornaCliente() {
        ClienteRequestDTO minimo = new ClienteRequestDTO();
        minimo.setNombre("Solo Nombre");
        minimo.setEmail("solo@email.com");

        Cliente guardado = new Cliente();
        guardado.setId(10L);
        guardado.setNombre("Solo Nombre");
        guardado.setEmail("solo@email.com");

        when(clienteRepository.save(any(Cliente.class))).thenReturn(guardado);

        ClienteResponseDTO resultado = clienteService.crearCliente(minimo);

        assertNotNull(resultado);
        assertEquals("Solo Nombre", resultado.getNombre());
        assertEquals("solo@email.com", resultado.getEmail());
    }
}
