package com.example.ms_pedido.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Esta clase NO es nuestra entidad. Solo sirve para "traducir"
// el JSON que responde ms-cliente cuando lo consultamos.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
}
