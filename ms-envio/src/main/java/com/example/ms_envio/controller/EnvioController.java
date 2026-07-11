package com.example.ms_envio.controller;

import com.example.ms_envio.dto.EnvioRequestDTO;
import com.example.ms_envio.dto.EnvioResponseDTO;
import com.example.ms_envio.service.EnvioService;
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

@Tag(name = "Envios", description = "Gestiona los envios y entregas de pedidos (consulta datos en vivo a ms-pedido)")
@RestController
@RequestMapping("/api/envios")
@RequiredArgsConstructor
public class EnvioController {

    private final EnvioService envioService;

    @Operation(summary = "Listar todos los envios")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    // GET http://localhost:8086/api/envios
    @GetMapping
    public ResponseEntity<List<EnvioResponseDTO>> listarEnvios() {
        return ResponseEntity.ok(envioService.listarEnvios());
    }

    @Operation(summary = "Buscar un envio por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Envio encontrado"),
            @ApiResponse(responseCode = "404", description = "El envio no existe", content = @Content)
    })
    // GET http://localhost:8086/api/envios/1
    @GetMapping("/{id}")
    public ResponseEntity<EnvioResponseDTO> buscarPorId(
            @Parameter(description = "Id del envio", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(envioService.buscarPorId(id));
    }

    @Operation(summary = "Crear un envio",
            description = "Valida primero que el pedidoId exista en ms-pedido antes de crear el envio.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Envio creado correctamente"),
            @ApiResponse(responseCode = "400", description = "El pedido indicado no existe en ms-pedido", content = @Content)
    })
    // POST http://localhost:8086/api/envios
    @PostMapping
    public ResponseEntity<EnvioResponseDTO> crearEnvio(@Valid @RequestBody EnvioRequestDTO requestDTO) {
        EnvioResponseDTO creado = envioService.crearEnvio(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar un envio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Envio actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "El envio no existe", content = @Content)
    })
    // PUT http://localhost:8086/api/envios/1
    @PutMapping("/{id}")
    public ResponseEntity<EnvioResponseDTO> actualizarEnvio(
            @Parameter(description = "Id del envio", example = "1") @PathVariable Long id,
            @Valid @RequestBody EnvioRequestDTO requestDTO) {
        return ResponseEntity.ok(envioService.actualizarEnvio(id, requestDTO));
    }

    @Operation(summary = "Eliminar un envio")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Envio eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "El envio no existe", content = @Content)
    })
    // DELETE http://localhost:8086/api/envios/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEnvio(
            @Parameter(description = "Id del envio", example = "1") @PathVariable Long id) {
        envioService.eliminarEnvio(id);
        return ResponseEntity.noContent().build();
    }
}
