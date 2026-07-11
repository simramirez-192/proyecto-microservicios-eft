package com.example.ms_cupon.controller;

import com.example.ms_cupon.dto.CuponRequestDTO;
import com.example.ms_cupon.dto.CuponResponseDTO;
import com.example.ms_cupon.service.CuponService;
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

@Tag(name = "Cupones", description = "Gestiona cupones de descuento")
@RestController
@RequestMapping("/api/cupones")
@RequiredArgsConstructor
public class CuponController {

    private final CuponService cuponService;

    @Operation(summary = "Listar todos los cupones")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<CuponResponseDTO>> listarCupones() {
        return ResponseEntity.ok(cuponService.listarCupones());
    }

    @Operation(summary = "Buscar un cupon por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cupon encontrado"),
            @ApiResponse(responseCode = "404", description = "El cupon no existe", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CuponResponseDTO> buscarPorId(
            @Parameter(description = "Id del cupon", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(cuponService.buscarPorId(id));
    }

    @Operation(summary = "Crear un cupon",
            description = "Crea un cupon de descuento. El codigo debe ser unico.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cupon creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Ya existe un cupon con ese codigo", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CuponResponseDTO> crearCupon(@Valid @RequestBody CuponRequestDTO requestDTO) {
        CuponResponseDTO creado = cuponService.crearCupon(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar un cupon")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cupon actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "El cupon no existe", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CuponResponseDTO> actualizarCupon(
            @Parameter(description = "Id del cupon", example = "1") @PathVariable Long id,
            @Valid @RequestBody CuponRequestDTO requestDTO) {
        return ResponseEntity.ok(cuponService.actualizarCupon(id, requestDTO));
    }

    @Operation(summary = "Eliminar un cupon")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cupon eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "El cupon no existe", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCupon(
            @Parameter(description = "Id del cupon", example = "1") @PathVariable Long id) {
        cuponService.eliminarCupon(id);
        return ResponseEntity.noContent().build();
    }
}
