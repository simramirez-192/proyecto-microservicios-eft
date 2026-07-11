package com.example.ms_cupon.exception;

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

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> manejarRuntimeException(RuntimeException ex) {
        String mensaje = ex.getMessage() != null ? ex.getMessage() : "Error inesperado";
        boolean esNoEncontrado = mensaje.toLowerCase().contains("no encontrado")
                || mensaje.toLowerCase().contains("no existe");

        HttpStatus status = esNoEncontrado ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;

        logger.warn("Excepcion controlada [{}]: {}", status.value(), mensaje);

        return ResponseEntity.status(status).body(construirCuerpo(status, mensaje));
    }

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
