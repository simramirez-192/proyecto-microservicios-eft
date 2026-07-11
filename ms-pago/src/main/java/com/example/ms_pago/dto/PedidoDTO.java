package com.example.ms_pago.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Esta clase NO es nuestra entidad. Solo sirve para "traducir"
// el JSON que responde ms-pedido cuando lo consultamos.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    private Long id;
    private Long clienteId;
    private String nombreCliente;
    private Long productoId;
    private String nombreProducto;
    private Integer cantidad;
    private Double total;
    private String estado;
}
