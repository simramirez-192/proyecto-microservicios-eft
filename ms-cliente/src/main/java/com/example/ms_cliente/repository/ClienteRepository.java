package com.example.ms_cliente.repository;

import com.example.ms_cliente.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // JpaRepository ya nos da gratis: save, findAll, findById, deleteById, etc.
}
