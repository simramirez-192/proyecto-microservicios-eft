package com.example.ms_pedido.controller;

import com.example.ms_pedido.dto.PedidoRequestDTO;
import com.example.ms_pedido.dto.PedidoResponseDTO;
import com.example.ms_pedido.service.PedidoService;
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

@Tag(name = "Pedidos", description = "Gestiona los pedidos de los clientes (consulta datos en vivo a ms-cliente y ms-producto)")
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @Operation(summary = "Listar todos los pedidos")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    // GET http://localhost:8083/api/pedidos
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarPedidos());
    }

    @Operation(summary = "Buscar un pedido por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "El pedido no existe", content = @Content)
    })
    // GET http://localhost:8083/api/pedidos/1
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(
            @Parameter(description = "Id del pedido", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @Operation(summary = "Crear un nuevo pedido",
            description = "Valida que el cliente exista en ms-cliente y el producto en ms-producto, y calcula el total automaticamente (precio x cantidad).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido creado correctamente",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = "{\"clienteId\":1,\"productoId\":1,\"cantidad\":2}"))),
            @ApiResponse(responseCode = "400", description = "El cliente o el producto indicado no existe", content = @Content)
    })
    // POST http://localhost:8083/api/pedidos
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crearPedido(@Valid @RequestBody PedidoRequestDTO requestDTO) {
        PedidoResponseDTO creado = pedidoService.crearPedido(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar un pedido existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "El pedido no existe", content = @Content)
    })
    // PUT http://localhost:8083/api/pedidos/1
    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> actualizarPedido(
            @Parameter(description = "Id del pedido", example = "1") @PathVariable Long id,
            @Valid @RequestBody PedidoRequestDTO requestDTO) {
        return ResponseEntity.ok(pedidoService.actualizarPedido(id, requestDTO));
    }

    @Operation(summary = "Eliminar un pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "El pedido no existe", content = @Content)
    })
    // DELETE http://localhost:8083/api/pedidos/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(
            @Parameter(description = "Id del pedido", example = "1") @PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }
}
