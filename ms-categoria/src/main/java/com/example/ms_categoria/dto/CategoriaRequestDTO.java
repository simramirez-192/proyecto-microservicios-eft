package com.example.ms_categoria.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Lo que el cliente (Postman) envia en un POST o PUT
@Data
public class CategoriaRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;
}
