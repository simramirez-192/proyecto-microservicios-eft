package com.example.ms_cupon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuponResponseDTO {

    private Long id;
    private String codigo;
    private Double porcentajeDescuento;
    private Boolean activo;
}
