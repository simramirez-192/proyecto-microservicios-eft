package com.example.ms_pedido.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lo que la API devuelve al cliente (Postman)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {

    private Long id;
    private Long clienteId;
    private String nombreCliente;   // dato que viene de ms-cliente
    private Long productoId;
    private String nombreProducto;  // dato que viene de ms-producto
    private Integer cantidad;
    private Double total;
    private String estado;
}
