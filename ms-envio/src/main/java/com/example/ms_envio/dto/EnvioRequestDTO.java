package com.example.ms_envio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnvioRequestDTO {

    @NotNull(message = "El pedidoId es obligatorio")
    private Long pedidoId;

    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;
}
