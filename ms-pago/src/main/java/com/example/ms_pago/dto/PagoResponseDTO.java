package com.example.ms_pago.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lo que la API devuelve al cliente (Postman)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {

    private Long id;
    private Long pedidoId;
    private Double monto;
    private String metodoPago;
    private String estado;
}
