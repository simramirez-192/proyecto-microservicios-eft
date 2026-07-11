package com.example.ms_opinion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpinionResponseDTO {

    private Long id;
    private Long clienteId;
    private String nombreCliente;
    private Long productoId;
    private String nombreProducto;
    private Integer puntuacion;
    private String comentario;
}
