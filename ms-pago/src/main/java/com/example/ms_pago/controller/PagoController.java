package com.example.ms_pago.controller;

import com.example.ms_pago.dto.PagoRequestDTO;
import com.example.ms_pago.dto.PagoResponseDTO;
import com.example.ms_pago.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pagos", description = "Gestiona los pagos asociados a un pedido (consulta datos en vivo a ms-pedido)")
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @Operation(summary = "Listar todos los pagos")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    // GET http://localhost:8084/api/pagos
    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> listarPagos() {
        return ResponseEntity.ok(pagoService.listarPagos());
    }

    @Operation(summary = "Buscar un pago por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "El pago no existe", content = @Content)
    })
    // GET http://localhost:8084/api/pagos/1
    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> buscarPorId(
            @Parameter(description = "Id del pago", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(pagoService.buscarPorId(id));
    }

    @Operation(summary = "Crear un nuevo pago",
            description = "Valida que el pedido exista en ms-pedido y toma el monto directamente del total del pedido.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pago creado correctamente",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = "{\"pedidoId\":1,\"metodoPago\":\"TARJETA\"}"))),
            @ApiResponse(responseCode = "400", description = "El pedido indicado no existe", content = @Content)
    })
    // POST http://localhost:8084/api/pagos
    @PostMapping
    public ResponseEntity<PagoResponseDTO> crearPago(@Valid @RequestBody PagoRequestDTO requestDTO) {
        PagoResponseDTO creado = pagoService.crearPago(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar un pago existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "El pago no existe", content = @Content)
    })
    // PUT http://localhost:8084/api/pagos/1
    @PutMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> actualizarPago(
            @Parameter(description = "Id del pago", example = "1") @PathVariable Long id,
            @Valid @RequestBody PagoRequestDTO requestDTO) {
        return ResponseEntity.ok(pagoService.actualizarPago(id, requestDTO));
    }

    @Operation(summary = "Eliminar un pago")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pago eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "El pago no existe", content = @Content)
    })
    // DELETE http://localhost:8084/api/pagos/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPago(
            @Parameter(description = "Id del pago", example = "1") @PathVariable Long id) {
        pagoService.eliminarPago(id);
        return ResponseEntity.noContent().build();
    }
}
