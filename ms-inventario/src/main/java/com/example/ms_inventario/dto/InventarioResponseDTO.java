package com.example.ms_inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lo que la API devuelve al cliente (Postman)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioResponseDTO {

    private Long id;
    private Long productoId;
    private String nombreProducto; // dato que viene de ms-producto
    private Integer cantidadDisponible;
}
