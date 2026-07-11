package com.example.ms_pedido.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductoDTOTest {

    @Test
    void constructorSinArgs_creaInstanciaVacia() {
        ProductoDTO dto = new ProductoDTO();
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getNombre());
        assertNull(dto.getDescripcion());
        assertNull(dto.getPrecio());
        assertNull(dto.getStock());
    }

    @Test
    void constructorConArgs_creaInstanciaConValores() {
        ProductoDTO dto = new ProductoDTO(1L, "Mouse Gamer", "Mouse inalambrico", 15990.0, 50);

        assertEquals(1L, dto.getId());
        assertEquals("Mouse Gamer", dto.getNombre());
        assertEquals("Mouse inalambrico", dto.getDescripcion());
        assertEquals(15990.0, dto.getPrecio());
        assertEquals(50, dto.getStock());
    }

    @Test
    void settersYGetters_funcionanCorrectamente() {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(2L);
        dto.setNombre("Teclado Mecanico");
        dto.setDescripcion("Switches Cherry MX");
        dto.setPrecio(49990.0);
        dto.setStock(25);

        assertEquals(2L, dto.getId());
        assertEquals("Teclado Mecanico", dto.getNombre());
        assertEquals("Switches Cherry MX", dto.getDescripcion());
        assertEquals(49990.0, dto.getPrecio());
        assertEquals(25, dto.getStock());
    }

    @Test
    void equals_mismosValores_retornaTrue() {
        ProductoDTO dto1 = new ProductoDTO(1L, "Mouse Gamer", "Mouse inalambrico", 15990.0, 50);
        ProductoDTO dto2 = new ProductoDTO(1L, "Mouse Gamer", "Mouse inalambrico", 15990.0, 50);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void equals_diferentesValores_retornaFalse() {
        ProductoDTO dto1 = new ProductoDTO(1L, "Mouse Gamer", "Mouse inalambrico", 15990.0, 50);
        ProductoDTO dto2 = new ProductoDTO(2L, "Monitor", "4K UHD", 299990.0, 10);

        assertNotEquals(dto1, dto2);
    }

    @Test
    void toString_contieneInfo() {
        ProductoDTO dto = new ProductoDTO(1L, "Mouse Gamer", "Mouse inalambrico", 15990.0, 50);
        String str = dto.toString();

        assertNotNull(str);
        assertTrue(str.contains("Mouse Gamer"));
        assertTrue(str.contains("15990"));
    }
}
