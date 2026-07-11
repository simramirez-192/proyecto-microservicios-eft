package com.example.ms_pedido.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Id del cliente que vive en ms-cliente (no hay relacion de BD real,
    // solo guardamos el numero de referencia)
    private Long clienteId;

    // Id del producto que vive en ms-producto
    private Long productoId;

    private Integer cantidad;

    private Double total;

    private String estado; // PENDIENTE, CONFIRMADO, CANCELADO
}
