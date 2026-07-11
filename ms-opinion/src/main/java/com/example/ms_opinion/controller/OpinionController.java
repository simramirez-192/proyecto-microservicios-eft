package com.example.ms_opinion.controller;

import com.example.ms_opinion.dto.OpinionRequestDTO;
import com.example.ms_opinion.dto.OpinionResponseDTO;
import com.example.ms_opinion.service.OpinionService;
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

@Tag(name = "Opiniones", description = "Gestiona las opiniones y calificaciones de los clientes sobre los productos")
@RestController
@RequestMapping("/api/opiniones")
@RequiredArgsConstructor
public class OpinionController {

    private final OpinionService opinionService;

    @Operation(summary = "Listar todas las opiniones")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<OpinionResponseDTO>> listarOpiniones() {
        return ResponseEntity.ok(opinionService.listarOpiniones());
    }

    @Operation(summary = "Buscar una opinion por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Opinion encontrada"),
            @ApiResponse(responseCode = "404", description = "La opinion no existe", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<OpinionResponseDTO> buscarPorId(
            @Parameter(description = "Id de la opinion", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(opinionService.buscarPorId(id));
    }

    @Operation(summary = "Crear una opinion",
            description = "Valida primero que el clienteId y productoId existan en sus respectivos microservicios antes de crear la opinion.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Opinion creada correctamente"),
            @ApiResponse(responseCode = "400", description = "El cliente o producto indicado no existe", content = @Content)
    })
    @PostMapping
    public ResponseEntity<OpinionResponseDTO> crearOpinion(@Valid @RequestBody OpinionRequestDTO requestDTO) {
        OpinionResponseDTO creada = opinionService.crearOpinion(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @Operation(summary = "Actualizar una opinion")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Opinion actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "La opinion no existe", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<OpinionResponseDTO> actualizarOpinion(
            @Parameter(description = "Id de la opinion", example = "1") @PathVariable Long id,
            @Valid @RequestBody OpinionRequestDTO requestDTO) {
        return ResponseEntity.ok(opinionService.actualizarOpinion(id, requestDTO));
    }

    @Operation(summary = "Eliminar una opinion")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Opinion eliminada correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "La opinion no existe", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOpinion(
            @Parameter(description = "Id de la opinion", example = "1") @PathVariable Long id) {
        opinionService.eliminarOpinion(id);
        return ResponseEntity.noContent().build();
    }
}
