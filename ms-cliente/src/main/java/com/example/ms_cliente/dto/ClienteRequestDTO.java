package com.example.ms_cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Este DTO representa los datos que el cliente (Postman) envía
// cuando hace un POST o un PUT.
@Data
public class ClienteRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato valido")
    private String email;

    private String telefono;

    private String direccion;
}
