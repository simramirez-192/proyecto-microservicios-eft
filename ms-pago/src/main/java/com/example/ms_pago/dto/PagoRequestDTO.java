package com.example.ms_pago.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Lo que el cliente (Postman) envia en un POST o PUT
@Data
public class PagoRequestDTO {

    @NotNull(message = "El pedidoId es obligatorio")
    private Long pedidoId;

    @NotBlank(message = "El metodo de pago es obligatorio")
    private String metodoPago;
}
