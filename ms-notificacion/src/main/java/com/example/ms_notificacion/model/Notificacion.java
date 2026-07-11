package com.example.ms_notificacion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Id del cliente que vive en ms-cliente (no hay relacion de BD real,
    // solo guardamos el numero de referencia)
    private Long clienteId;

    private String mensaje;

    private String tipo;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean leida = false;

    private LocalDateTime fechaEnvio;
}
