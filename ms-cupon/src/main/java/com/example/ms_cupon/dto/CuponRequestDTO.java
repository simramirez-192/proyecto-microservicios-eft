package com.example.ms_cupon.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CuponRequestDTO {

    @NotBlank(message = "El codigo es obligatorio")
    private String codigo;

    @NotNull(message = "El porcentaje de descuento es obligatorio")
    @Positive(message = "El porcentaje de descuento debe ser positivo")
    @Max(value = 100, message = "El porcentaje de descuento no puede superar 100")
    private Double porcentajeDescuento;
}
