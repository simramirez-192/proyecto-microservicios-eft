package com.example.ms_inventario.controller;

import com.example.ms_inventario.dto.InventarioRequestDTO;
import com.example.ms_inventario.dto.InventarioResponseDTO;
import com.example.ms_inventario.service.InventarioService;
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

@Tag(name = "Inventario", description = "Gestiona el stock disponible de cada producto (consulta datos en vivo a ms-producto)")
@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @Operation(summary = "Listar todo el inventario")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    // GET http://localhost:8081/api/inventario
    @GetMapping
    public ResponseEntity<List<InventarioResponseDTO>> listarInventario() {
        return ResponseEntity.ok(inventarioService.listarInventario());
    }

    @Operation(summary = "Buscar un registro de inventario por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro encontrado"),
            @ApiResponse(responseCode = "404", description = "El registro no existe", content = @Content)
    })
    // GET http://localhost:8081/api/inventario/1
    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponseDTO> buscarPorId(
            @Parameter(description = "Id del registro de inventario", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.buscarPorId(id));
    }

    @Operation(summary = "Crear un registro de inventario",
            description = "Valida primero que el productoId exista en ms-producto antes de crear el registro.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro creado correctamente"),
            @ApiResponse(responseCode = "400", description = "El producto indicado no existe en ms-producto", content = @Content)
    })
    // POST http://localhost:8081/api/inventario
    @PostMapping
    public ResponseEntity<InventarioResponseDTO> crearInventario(@Valid @RequestBody InventarioRequestDTO requestDTO) {
        InventarioResponseDTO creado = inventarioService.crearInventario(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar un registro de inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "El registro no existe", content = @Content)
    })
    // PUT http://localhost:8081/api/inventario/1
    @PutMapping("/{id}")
    public ResponseEntity<InventarioResponseDTO> actualizarInventario(
            @Parameter(description = "Id del registro de inventario", example = "1") @PathVariable Long id,
            @Valid @RequestBody InventarioRequestDTO requestDTO) {
        return ResponseEntity.ok(inventarioService.actualizarInventario(id, requestDTO));
    }

    @Operation(summary = "Eliminar un registro de inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Registro eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "El registro no existe", content = @Content)
    })
    // DELETE http://localhost:8081/api/inventario/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInventario(
            @Parameter(description = "Id del registro de inventario", example = "1") @PathVariable Long id) {
        inventarioService.eliminarInventario(id);
        return ResponseEntity.noContent().build();
    }
}
