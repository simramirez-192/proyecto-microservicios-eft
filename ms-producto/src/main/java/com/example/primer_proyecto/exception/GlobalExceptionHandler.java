package com.example.primer_proyecto.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice intercepta las excepciones que se lancen en
// CUALQUIER controller de este microservicio, en un solo lugar centralizado,
// en vez de tener que poner try/catch repetido en cada metodo.
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Captura las RuntimeException que lanzamos nosotros mismos en los Service
    // (por ejemplo: "Producto no encontrado con id: 99")
    // Las distinguimos por el mensaje: si menciona "no existe" o "no encontrado"
    // respondemos 404, en caso contrario 400 (peticion invalida / regla de negocio).
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> manejarRuntimeException(RuntimeException ex) {
        String mensaje = ex.getMessage() != null ? ex.getMessage() : "Error inesperado";
        boolean esNoEncontrado = mensaje.toLowerCase().contains("no encontrado")
                || mensaje.toLowerCase().contains("no existe");

        HttpStatus status = esNoEncontrado ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;

        logger.warn("Excepcion controlada [{}]: {}", status.value(), mensaje);

        return ResponseEntity.status(status).body(construirCuerpo(status, mensaje));
    }

    // Captura los errores de @Valid (por ejemplo @NotBlank, @Email, etc.)
    // y devuelve un mensaje claro con TODOS los campos que fallaron, en vez
    // del error crudo de Spring.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));

        logger.warn("Error de validacion: {}", errores);

        Map<String, Object> cuerpo = construirCuerpo(HttpStatus.BAD_REQUEST, "Error de validacion en los datos enviados");
        cuerpo.put("errores", errores);

        return ResponseEntity.badRequest().body(cuerpo);
    }

    // Red de seguridad: cualquier otra excepcion no prevista cae aqui,
    // para que NUNCA se devuelva un error 500 sin explicacion al cliente.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarExcepcionGeneral(Exception ex) {
        logger.error("Error no controlado: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(construirCuerpo(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error interno inesperado"));
    }

    private Map<String, Object> construirCuerpo(HttpStatus status, String mensaje) {
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", status.value());
        cuerpo.put("error", status.getReasonPhrase());
        cuerpo.put("mensaje", mensaje);
        return cuerpo;
    }
}
