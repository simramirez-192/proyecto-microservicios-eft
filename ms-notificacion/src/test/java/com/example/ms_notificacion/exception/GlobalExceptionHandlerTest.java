package com.example.ms_notificacion.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testManejarRuntimeException_conMensajeNoEncontrado_retorna404() {
        RuntimeException ex = new RuntimeException("Registro no encontrado con id: 99");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRuntimeException(ex);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals(404, respuesta.getBody().get("status"));
        assertTrue(respuesta.getBody().get("mensaje").toString().contains("no encontrado"));
    }

    @Test
    void testManejarRuntimeException_conMensajeNoExiste_retorna404() {
        RuntimeException ex = new RuntimeException("El cliente con id 10 no existe en ms-cliente");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRuntimeException(ex);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals(404, respuesta.getBody().get("status"));
        assertTrue(respuesta.getBody().get("mensaje").toString().contains("no existe"));
    }

    @Test
    void testManejarRuntimeException_conMensajeErrorGenerico_retorna400() {
        RuntimeException ex = new RuntimeException("Error de conexion con la base de datos");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRuntimeException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(400, respuesta.getBody().get("status"));
    }

    @Test
    void testManejarRuntimeException_conMensajeNull_retorna400ConMensajeDefault() {
        RuntimeException ex = new RuntimeException((String) null);

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRuntimeException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(400, respuesta.getBody().get("status"));
        assertEquals("Error inesperado", respuesta.getBody().get("mensaje"));
    }

    @Test
    void testManejarRuntimeException_caseInsensitive_noEncontrado() {
        RuntimeException ex = new RuntimeException("No Encontrado Algo");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRuntimeException(ex);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testManejarValidacion_retorna400ConErrores() throws Exception {
        MethodParameter parameter = mock(MethodParameter.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError error1 = new FieldError("requestDTO", "clienteId", "El clienteId es obligatorio");
        FieldError error2 = new FieldError("requestDTO", "mensaje", "El mensaje es obligatorio");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

        MethodArgumentNotValidException validacionEx =
                new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarValidacion(validacionEx);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(400, respuesta.getBody().get("status"));

        Map<String, String> errores = (Map<String, String>) respuesta.getBody().get("errores");
        assertNotNull(errores);
        assertEquals(2, errores.size());
        assertEquals("El clienteId es obligatorio", errores.get("clienteId"));
        assertEquals("El mensaje es obligatorio", errores.get("mensaje"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testManejarValidacion_unSoloError_retorna400() throws Exception {
        MethodParameter parameter = mock(MethodParameter.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError error = new FieldError("requestDTO", "tipo", "El tipo es obligatorio");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(error));

        MethodArgumentNotValidException validacionEx =
                new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarValidacion(validacionEx);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());

        Map<String, String> errores = (Map<String, String>) respuesta.getBody().get("errores");
        assertNotNull(errores);
        assertEquals(1, errores.size());
        assertEquals("El tipo es obligatorio", errores.get("tipo"));
    }

    @Test
    void testManejarExcepcionGeneral_retorna500() {
        Exception ex = new NullPointerException("referencia nula inesperada");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarExcepcionGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertEquals(500, respuesta.getBody().get("status"));
        assertEquals("Ocurrio un error interno inesperado", respuesta.getBody().get("mensaje"));
    }

    @Test
    void testManejarExcepcionGeneral_otrosTiposExcepcion_retorna500() {
        Exception ex = new IllegalStateException("estado invalido");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarExcepcionGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertEquals(500, respuesta.getBody().get("status"));
    }

    @Test
    void testConstruirCuerpo_contieneTimestampYError() {
        RuntimeException ex = new RuntimeException("test");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRuntimeException(ex);

        assertNotNull(respuesta.getBody().get("timestamp"));
        assertNotNull(respuesta.getBody().get("error"));
    }

    @Test
    void testConstruirCuerpo_contieneCamposCorrectos() {
        RuntimeException ex = new RuntimeException("test");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRuntimeException(ex);

        assertEquals("Bad Request", respuesta.getBody().get("error"));
    }

    @Test
    void testConstruirCuerpo_para404_contieneReasonPhrase() {
        RuntimeException ex = new RuntimeException("no encontrado");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRuntimeException(ex);

        assertEquals("Not Found", respuesta.getBody().get("error"));
    }
}
