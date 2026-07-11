package com.example.ms_notificacion.controller;

import com.example.ms_notificacion.dto.NotificacionRequestDTO;
import com.example.ms_notificacion.dto.NotificacionResponseDTO;
import com.example.ms_notificacion.service.NotificacionService;
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

@Tag(name = "Notificaciones", description = "Gestiona el envio y seguimiento de notificaciones a clientes (consulta datos en vivo a ms-cliente)")
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @Operation(summary = "Listar todas las notificaciones")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    // GET http://localhost:8089/api/notificaciones
    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listarNotificaciones() {
        return ResponseEntity.ok(notificacionService.listarNotificaciones());
    }

    @Operation(summary = "Buscar una notificacion por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificacion encontrada"),
            @ApiResponse(responseCode = "404", description = "La notificacion no existe", content = @Content)
    })
    // GET http://localhost:8089/api/notificaciones/1
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> buscarPorId(
            @Parameter(description = "Id de la notificacion", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.buscarPorId(id));
    }

    @Operation(summary = "Crear una notificacion",
            description = "Valida primero que el clienteId exista en ms-cliente antes de crear la notificacion.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Notificacion creada correctamente"),
            @ApiResponse(responseCode = "400", description = "El cliente indicado no existe en ms-cliente", content = @Content)
    })
    // POST http://localhost:8089/api/notificaciones
    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crearNotificacion(@Valid @RequestBody NotificacionRequestDTO requestDTO) {
        NotificacionResponseDTO creada = notificacionService.crearNotificacion(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @Operation(summary = "Marcar una notificacion como leida")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificacion marcada como leida correctamente"),
            @ApiResponse(responseCode = "404", description = "La notificacion no existe", content = @Content)
    })
    // PATCH http://localhost:8089/api/notificaciones/1/leer
    @PatchMapping("/{id}/leer")
    public ResponseEntity<NotificacionResponseDTO> marcarComoLeida(
            @Parameter(description = "Id de la notificacion", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarComoLeida(id));
    }

    @Operation(summary = "Eliminar una notificacion")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Notificacion eliminada correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "La notificacion no existe", content = @Content)
    })
    // DELETE http://localhost:8089/api/notificaciones/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNotificacion(
            @Parameter(description = "Id de la notificacion", example = "1") @PathVariable Long id) {
        notificacionService.eliminarNotificacion(id);
        return ResponseEntity.noContent().build();
    }
}
