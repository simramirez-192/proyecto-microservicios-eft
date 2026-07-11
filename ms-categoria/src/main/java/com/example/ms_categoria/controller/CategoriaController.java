package com.example.ms_categoria.controller;

import com.example.ms_categoria.dto.CategoriaRequestDTO;
import com.example.ms_categoria.dto.CategoriaResponseDTO;
import com.example.ms_categoria.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Categorias", description = "Gestiona las categorias de productos")
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @Operation(summary = "Listar todas las categorias")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    // GET http://localhost:8085/api/categorias
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.listarCategorias());
    }

    @Operation(summary = "Buscar una categoria por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "La categoria no existe", content = @Content)
    })
    // GET http://localhost:8085/api/categorias/1
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(
            @Parameter(description = "Id de la categoria", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @Operation(summary = "Crear una categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content)
    })
    // POST http://localhost:8085/api/categorias
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(@Valid @RequestBody CategoriaRequestDTO requestDTO) {
        CategoriaResponseDTO creado = categoriaService.crearCategoria(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar una categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "La categoria no existe", content = @Content)
    })
    // PUT http://localhost:8085/api/categorias/1
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizarCategoria(
            @Parameter(description = "Id de la categoria", example = "1") @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO requestDTO) {
        return ResponseEntity.ok(categoriaService.actualizarCategoria(id, requestDTO));
    }

    @Operation(summary = "Eliminar una categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria eliminada correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "La categoria no existe", content = @Content)
    })
    // DELETE http://localhost:8085/api/categorias/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(
            @Parameter(description = "Id de la categoria", example = "1") @PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
