package com.example.ms_envio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvioResponseDTO {

    private Long id;
    private Long pedidoId;
    private String nombrePedido;
    private String direccion;
    private String estado;
    private LocalDateTime fechaEnvio;
}
