package com.example.primer_proyecto.controller;

import com.example.primer_proyecto.dto.ProductoRequestDTO;
import com.example.primer_proyecto.dto.ProductoResponseDTO;
import com.example.primer_proyecto.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @Tag agrupa todos los endpoints de este controller bajo un mismo titulo en Swagger
@Tag(name = "Productos", description = "Operaciones CRUD sobre el catalogo de productos")
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @Operation(summary = "Listar todos los productos", description = "Retorna el catalogo completo de productos registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    })
    // GET http://localhost:8080/api/productos
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarProductos() {
        return ResponseEntity.ok(productoService.listarProductos());
    }

    @Operation(summary = "Buscar un producto por id", description = "Retorna los datos de un producto especifico segun su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "El producto no existe", content = @Content)
    })
    // GET http://localhost:8080/api/productos/1
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> buscarPorId(
            @Parameter(description = "Id del producto a buscar", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @Operation(summary = "Crear un nuevo producto", description = "Registra un producto nuevo en el catalogo.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado correctamente",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = "{\"id\":1,\"nombre\":\"Mouse Gamer\",\"descripcion\":\"Mouse inalambrico\",\"precio\":15990.0,\"stock\":50}"))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos en la solicitud", content = @Content)
    })
    // POST http://localhost:8080/api/productos
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(@Valid @RequestBody ProductoRequestDTO requestDTO) {
        ProductoResponseDTO creado = productoService.crearProducto(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar un producto existente", description = "Modifica los datos de un producto ya registrado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "El producto no existe", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos invalidos en la solicitud", content = @Content)
    })
    // PUT http://localhost:8080/api/productos/1
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @Parameter(description = "Id del producto a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO requestDTO) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, requestDTO));
    }

    @Operation(summary = "Eliminar un producto", description = "Elimina un producto del catalogo segun su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "El producto no existe", content = @Content)
    })
    // DELETE http://localhost:8080/api/productos/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
            @Parameter(description = "Id del producto a eliminar", example = "1") @PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
