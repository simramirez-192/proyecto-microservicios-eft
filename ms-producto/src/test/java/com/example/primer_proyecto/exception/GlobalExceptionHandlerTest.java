package com.example.primer_proyecto.exception;

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
    void manejarRuntimeException_conMensajeNoEncontrado_retorna404() {
        RuntimeException ex = new RuntimeException("Producto no encontrado con id: 99");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRuntimeException(ex);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals(404, respuesta.getBody().get("status"));
        assertTrue(respuesta.getBody().get("mensaje").toString().contains("no encontrado"));
    }

    @Test
    void manejarRuntimeException_conMensajeNoExiste_retorna404() {
        RuntimeException ex = new RuntimeException("El producto no existe en el catalogo");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRuntimeException(ex);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals(404, respuesta.getBody().get("status"));
        assertTrue(respuesta.getBody().get("mensaje").toString().contains("no existe"));
    }

    @Test
    void manejarRuntimeException_conMensajeDeOtroTipo_retorna400() {
        RuntimeException ex = new RuntimeException("Error de conexion con la base de datos");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRuntimeException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(400, respuesta.getBody().get("status"));
    }

    @Test
    void manejarRuntimeException_conMensajeNull_retorna400ConErrorInesperado() {
        RuntimeException ex = new RuntimeException((String) null);

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRuntimeException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(400, respuesta.getBody().get("status"));
        assertEquals("Error inesperado", respuesta.getBody().get("mensaje"));
    }

    @Test
    void manejarRuntimeException_mensajeCaseInsensitive_retorna404() {
        RuntimeException ex = new RuntimeException("PRODUCTO No Encontrado Con Id: 5");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRuntimeException(ex);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertEquals(404, respuesta.getBody().get("status"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void manejarValidacion_conDosErrores_retorna400ConMapaDeErrores() throws Exception {
        MethodParameter parameter = mock(MethodParameter.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError error1 = new FieldError("productoRequestDTO", "nombre", "El nombre es obligatorio");
        FieldError error2 = new FieldError("productoRequestDTO", "precio", "El precio es obligatorio");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

        MethodArgumentNotValidException validacionEx =
                new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarValidacion(validacionEx);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(400, respuesta.getBody().get("status"));
        assertEquals("Error de validacion en los datos enviados", respuesta.getBody().get("mensaje"));

        Map<String, String> errores = (Map<String, String>) respuesta.getBody().get("errores");
        assertNotNull(errores);
        assertEquals(2, errores.size());
        assertEquals("El nombre es obligatorio", errores.get("nombre"));
        assertEquals("El precio es obligatorio", errores.get("precio"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void manejarValidacion_conUnSoloError_retorna400ConUnError() throws Exception {
        MethodParameter parameter = mock(MethodParameter.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError error = new FieldError("productoRequestDTO", "stock", "El stock no puede ser negativo");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(error));

        MethodArgumentNotValidException validacionEx =
                new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarValidacion(validacionEx);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());

        Map<String, String> errores = (Map<String, String>) respuesta.getBody().get("errores");
        assertNotNull(errores);
        assertEquals(1, errores.size());
        assertEquals("El stock no puede ser negativo", errores.get("stock"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void manejarValidacion_sinErrores_retorna400ConMapaVacio() throws Exception {
        MethodParameter parameter = mock(MethodParameter.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        MethodArgumentNotValidException validacionEx =
                new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarValidacion(validacionEx);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        Map<String, String> errores = (Map<String, String>) respuesta.getBody().get("errores");
        assertNotNull(errores);
        assertTrue(errores.isEmpty());
    }

    @Test
    void manejarExcepcionGeneral_retorna500() {
        Exception ex = new NullPointerException("referencia nula inesperada");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarExcepcionGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertEquals(500, respuesta.getBody().get("status"));
        assertEquals("Internal Server Error", respuesta.getBody().get("error"));
        assertEquals("Ocurrio un error interno inesperado", respuesta.getBody().get("mensaje"));
    }

    @Test
    void manejarExcepcionGeneral_conExceptionGenerica_retorna500() {
        Exception ex = new IllegalStateException("estado invalido");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarExcepcionGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertEquals(500, respuesta.getBody().get("status"));
    }

    @Test
    void construirCuerpo_contieneTimestampYError() {
        RuntimeException ex = new RuntimeException("test");

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRuntimeException(ex);

        assertNotNull(respuesta.getBody().get("timestamp"));
        assertNotNull(respuesta.getBody().get("error"));
        assertNotNull(respuesta.getBody().get("status"));
        assertNotNull(respuesta.getBody().get("mensaje"));
    }

    @Test
    void manejarRuntimeException_exceptionSinMensaje_retorna400ConErrorInesperado() {
        RuntimeException ex = new RuntimeException();

        ResponseEntity<Map<String, Object>> respuesta = handler.manejarRuntimeException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals("Error inesperado", respuesta.getBody().get("mensaje"));
    }
}
