package com.example.ms_pago.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Id del pedido que vive en ms-pedido (no hay relacion de BD real,
    // solo guardamos el numero de referencia)
    private Long pedidoId;

    private Double monto;

    private String metodoPago; // TARJETA, EFECTIVO, TRANSFERENCIA

    private String estado; // PENDIENTE, PAGADO, RECHAZADO
}
