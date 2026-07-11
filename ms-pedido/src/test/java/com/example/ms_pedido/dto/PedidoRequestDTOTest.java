package com.example.ms_pedido.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PedidoRequestDTOTest {

    @Test
    void constructorSinArgs_creaInstanciaVacia() {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        assertNotNull(dto);
        assertNull(dto.getClienteId());
        assertNull(dto.getProductoId());
        assertNull(dto.getCantidad());
    }

    @Test
    void settersYGetters_funcionanCorrectamente() {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(5L);
        dto.setProductoId(10L);
        dto.setCantidad(3);

        assertEquals(5L, dto.getClienteId());
        assertEquals(10L, dto.getProductoId());
        assertEquals(3, dto.getCantidad());
    }

    @Test
    void equals_mismosValores_retornaTrue() {
        PedidoRequestDTO dto1 = new PedidoRequestDTO();
        dto1.setClienteId(5L);
        dto1.setProductoId(10L);
        dto1.setCantidad(3);

        PedidoRequestDTO dto2 = new PedidoRequestDTO();
        dto2.setClienteId(5L);
        dto2.setProductoId(10L);
        dto2.setCantidad(3);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void equals_diferentesValores_retornaFalse() {
        PedidoRequestDTO dto1 = new PedidoRequestDTO();
        dto1.setClienteId(5L);

        PedidoRequestDTO dto2 = new PedidoRequestDTO();
        dto2.setClienteId(99L);

        assertNotEquals(dto1, dto2);
    }

    @Test
    void toString_contieneInfo() {
        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setClienteId(5L);
        assertNotNull(dto.toString());
    }
}
