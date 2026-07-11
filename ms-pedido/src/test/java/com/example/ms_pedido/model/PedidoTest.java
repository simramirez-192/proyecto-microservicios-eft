package com.example.ms_pedido.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    @Test
    void constructorSinArgs_creaInstanciaVacia() {
        Pedido pedido = new Pedido();
        assertNotNull(pedido);
        assertNull(pedido.getId());
        assertNull(pedido.getClienteId());
        assertNull(pedido.getProductoId());
        assertNull(pedido.getCantidad());
        assertNull(pedido.getTotal());
        assertNull(pedido.getEstado());
    }

    @Test
    void constructorConArgs_creaInstanciaConValores() {
        Pedido pedido = new Pedido(1L, 5L, 10L, 2, 31980.0, "PENDIENTE");

        assertEquals(1L, pedido.getId());
        assertEquals(5L, pedido.getClienteId());
        assertEquals(10L, pedido.getProductoId());
        assertEquals(2, pedido.getCantidad());
        assertEquals(31980.0, pedido.getTotal());
        assertEquals("PENDIENTE", pedido.getEstado());
    }

    @Test
    void settersYGetters_funcionanCorrectamente() {
        Pedido pedido = new Pedido();
        pedido.setId(2L);
        pedido.setClienteId(3L);
        pedido.setProductoId(4L);
        pedido.setCantidad(5);
        pedido.setTotal(50000.0);
        pedido.setEstado("CONFIRMADO");

        assertEquals(2L, pedido.getId());
        assertEquals(3L, pedido.getClienteId());
        assertEquals(4L, pedido.getProductoId());
        assertEquals(5, pedido.getCantidad());
        assertEquals(50000.0, pedido.getTotal());
        assertEquals("CONFIRMADO", pedido.getEstado());
    }

    @Test
    void equals_mismosValores_retornaTrue() {
        Pedido p1 = new Pedido(1L, 5L, 10L, 2, 31980.0, "PENDIENTE");
        Pedido p2 = new Pedido(1L, 5L, 10L, 2, 31980.0, "PENDIENTE");

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void equals_diferentesValores_retornaFalse() {
        Pedido p1 = new Pedido(1L, 5L, 10L, 2, 31980.0, "PENDIENTE");
        Pedido p2 = new Pedido(2L, 5L, 10L, 2, 31980.0, "PENDIENTE");

        assertNotEquals(p1, p2);
    }

    @Test
    void equals_mismoObjeto_retornaTrue() {
        Pedido p1 = new Pedido(1L, 5L, 10L, 2, 31980.0, "PENDIENTE");

        assertEquals(p1, p1);
    }

    @Test
    void equals_null_retornaFalse() {
        Pedido p1 = new Pedido(1L, 5L, 10L, 2, 31980.0, "PENDIENTE");

        assertNotEquals(null, p1);
    }

    @Test
    void equals_otroTipo_retornaFalse() {
        Pedido p1 = new Pedido(1L, 5L, 10L, 2, 31980.0, "PENDIENTE");

        assertNotEquals("string", p1);
    }

    @Test
    void toString_contieneValores() {
        Pedido pedido = new Pedido(1L, 5L, 10L, 2, 31980.0, "PENDIENTE");
        String str = pedido.toString();

        assertNotNull(str);
        assertTrue(str.contains("PENDIENTE"));
        assertTrue(str.contains("5"));
    }
}
