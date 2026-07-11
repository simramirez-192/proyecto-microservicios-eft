package com.example.ms_pedido.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PedidoResponseDTOTest {

    @Test
    void constructorSinArgs_creaInstanciaVacia() {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getClienteId());
        assertNull(dto.getNombreCliente());
        assertNull(dto.getProductoId());
        assertNull(dto.getNombreProducto());
        assertNull(dto.getCantidad());
        assertNull(dto.getTotal());
        assertNull(dto.getEstado());
    }

    @Test
    void constructorConArgs_creaInstanciaConValores() {
        PedidoResponseDTO dto = new PedidoResponseDTO(
                1L, 5L, "Ana Torres", 10L, "Mouse Gamer", 2, 31980.0, "PENDIENTE");

        assertEquals(1L, dto.getId());
        assertEquals(5L, dto.getClienteId());
        assertEquals("Ana Torres", dto.getNombreCliente());
        assertEquals(10L, dto.getProductoId());
        assertEquals("Mouse Gamer", dto.getNombreProducto());
        assertEquals(2, dto.getCantidad());
        assertEquals(31980.0, dto.getTotal());
        assertEquals("PENDIENTE", dto.getEstado());
    }

    @Test
    void settersYGetters_funcionanCorrectamente() {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(2L);
        dto.setClienteId(6L);
        dto.setNombreCliente("Carlos Lopez");
        dto.setProductoId(11L);
        dto.setNombreProducto("Teclado Mecanico");
        dto.setCantidad(1);
        dto.setTotal(24990.0);
        dto.setEstado("CONFIRMADO");

        assertEquals(2L, dto.getId());
        assertEquals(6L, dto.getClienteId());
        assertEquals("Carlos Lopez", dto.getNombreCliente());
        assertEquals(11L, dto.getProductoId());
        assertEquals("Teclado Mecanico", dto.getNombreProducto());
        assertEquals(1, dto.getCantidad());
        assertEquals(24990.0, dto.getTotal());
        assertEquals("CONFIRMADO", dto.getEstado());
    }

    @Test
    void equals_mismosValores_retornaTrue() {
        PedidoResponseDTO dto1 = new PedidoResponseDTO(
                1L, 5L, "Ana Torres", 10L, "Mouse Gamer", 2, 31980.0, "PENDIENTE");
        PedidoResponseDTO dto2 = new PedidoResponseDTO(
                1L, 5L, "Ana Torres", 10L, "Mouse Gamer", 2, 31980.0, "PENDIENTE");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void equals_diferentesValores_retornaFalse() {
        PedidoResponseDTO dto1 = new PedidoResponseDTO(
                1L, 5L, "Ana Torres", 10L, "Mouse Gamer", 2, 31980.0, "PENDIENTE");
        PedidoResponseDTO dto2 = new PedidoResponseDTO(
                2L, 5L, "Ana Torres", 10L, "Mouse Gamer", 2, 31980.0, "PENDIENTE");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void toString_contieneInfo() {
        PedidoResponseDTO dto = new PedidoResponseDTO(
                1L, 5L, "Ana Torres", 10L, "Mouse Gamer", 2, 31980.0, "PENDIENTE");
        String str = dto.toString();

        assertNotNull(str);
        assertTrue(str.contains("Ana Torres"));
        assertTrue(str.contains("Mouse Gamer"));
    }
}
