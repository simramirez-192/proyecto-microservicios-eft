package com.example.ms_pedido.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteDTOTest {

    @Test
    void constructorSinArgs_creaInstanciaVacia() {
        ClienteDTO dto = new ClienteDTO();
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getNombre());
        assertNull(dto.getEmail());
        assertNull(dto.getTelefono());
        assertNull(dto.getDireccion());
    }

    @Test
    void constructorConArgs_creaInstanciaConValores() {
        ClienteDTO dto = new ClienteDTO(1L, "Juan Perez", "juan@email.com", "+56912345678", "Calle Falsa 123");

        assertEquals(1L, dto.getId());
        assertEquals("Juan Perez", dto.getNombre());
        assertEquals("juan@email.com", dto.getEmail());
        assertEquals("+56912345678", dto.getTelefono());
        assertEquals("Calle Falsa 123", dto.getDireccion());
    }

    @Test
    void settersYGetters_funcionanCorrectamente() {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(2L);
        dto.setNombre("Maria Garcia");
        dto.setEmail("maria@email.com");
        dto.setTelefono("+56987654321");
        dto.setDireccion("Av. Principal 456");

        assertEquals(2L, dto.getId());
        assertEquals("Maria Garcia", dto.getNombre());
        assertEquals("maria@email.com", dto.getEmail());
        assertEquals("+56987654321", dto.getTelefono());
        assertEquals("Av. Principal 456", dto.getDireccion());
    }

    @Test
    void equals_mismosValores_retornaTrue() {
        ClienteDTO dto1 = new ClienteDTO(1L, "Juan Perez", "juan@email.com", "+56912345678", "Calle Falsa 123");
        ClienteDTO dto2 = new ClienteDTO(1L, "Juan Perez", "juan@email.com", "+56912345678", "Calle Falsa 123");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void equals_diferentesValores_retornaFalse() {
        ClienteDTO dto1 = new ClienteDTO(1L, "Juan Perez", "juan@email.com", "+56912345678", "Calle Falsa 123");
        ClienteDTO dto2 = new ClienteDTO(2L, "Otro Nombre", "otro@email.com", "+56900000000", "OtraDireccion");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void toString_contieneInfo() {
        ClienteDTO dto = new ClienteDTO(1L, "Juan Perez", "juan@email.com", "+56912345678", "Calle Falsa 123");
        String str = dto.toString();

        assertNotNull(str);
        assertTrue(str.contains("Juan Perez"));
        assertTrue(str.contains("juan@email.com"));
    }
}
