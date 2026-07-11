package com.example.primer_proyecto.repository;

import com.example.primer_proyecto.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // JpaRepository ya nos da gratis: save, findAll, findById, deleteById, etc.
}
