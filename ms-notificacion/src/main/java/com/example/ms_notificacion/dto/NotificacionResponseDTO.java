package com.example.ms_notificacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionResponseDTO {

    private Long id;
    private Long clienteId;
    private String nombreCliente;
    private String mensaje;
    private String tipo;
    private Boolean leida;
    private LocalDateTime fechaEnvio;
}
