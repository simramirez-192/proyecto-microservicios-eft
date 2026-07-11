package com.example.ms_gateway;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GatewayRouteConfigTest {

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadYaml() {
        try {
            Yaml yaml = new Yaml();
            File file = new File("src/main/resources/application.yml");
            if (!file.exists()) {
                file = new File("../src/main/resources/application.yml");
            }
            InputStream is = new FileInputStream(file);
            return yaml.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Error cargando application.yml", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getRoutes() {
        Map<String, Object> yaml = loadYaml();
        Map<String, Object> spring = (Map<String, Object>) yaml.get("spring");
        Map<String, Object> cloud = (Map<String, Object>) spring.get("cloud");
        Map<String, Object> gateway = (Map<String, Object>) cloud.get("gateway");
        return (List<Map<String, Object>>) gateway.get("routes");
    }

    @Test
    void gatewayConfigura10Rutas() {
        List<Map<String, Object>> routes = getRoutes();
        assertEquals(10, routes.size(), "El gateway debe tener exactamente 10 rutas");
    }

    @Test
    void rutaMsProducto_configuradaCorrectamente() {
        List<Map<String, Object>> routes = getRoutes();
        Map<String, Object> route = routes.stream()
                .filter(r -> "ms-producto".equals(r.get("id")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ruta ms-producto no encontrada"));

        String uri = (String) route.get("uri");
        assertTrue(uri.contains("MS_PRODUCTO_URL"), "Debe usar variable de entorno MS_PRODUCTO_URL");
        assertTrue(uri.contains("8080"), "Debe tener fallback al puerto 8080");
    }

    @Test
    void rutaMsInventario_configuradaCorrectamente() {
        List<Map<String, Object>> routes = getRoutes();
        Map<String, Object> route = routes.stream()
                .filter(r -> "ms-inventario".equals(r.get("id")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ruta ms-inventario no encontrada"));

        String uri = (String) route.get("uri");
        assertTrue(uri.contains("MS_INVENTARIO_URL"));
        assertTrue(uri.contains("8081"));
    }

    @Test
    void rutaMsCliente_configuradaCorrectamente() {
        List<Map<String, Object>> routes = getRoutes();
        Map<String, Object> route = routes.stream()
                .filter(r -> "ms-cliente".equals(r.get("id")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ruta ms-cliente no encontrada"));

        String uri = (String) route.get("uri");
        assertTrue(uri.contains("MS_CLIENTE_URL"));
        assertTrue(uri.contains("8082"));
    }

    @Test
    void rutaMsPedido_configuradaCorrectamente() {
        List<Map<String, Object>> routes = getRoutes();
        Map<String, Object> route = routes.stream()
                .filter(r -> "ms-pedido".equals(r.get("id")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ruta ms-pedido no encontrada"));

        String uri = (String) route.get("uri");
        assertTrue(uri.contains("MS_PEDIDO_URL"));
        assertTrue(uri.contains("8083"));
    }

    @Test
    void rutaMsPago_configuradaCorrectamente() {
        List<Map<String, Object>> routes = getRoutes();
        Map<String, Object> route = routes.stream()
                .filter(r -> "ms-pago".equals(r.get("id")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ruta ms-pago no encontrada"));

        String uri = (String) route.get("uri");
        assertTrue(uri.contains("MS_PAGO_URL"));
        assertTrue(uri.contains("8084"));
    }

    @Test
    void rutaMsCategoria_configuradaCorrectamente() {
        List<Map<String, Object>> routes = getRoutes();
        Map<String, Object> route = routes.stream()
                .filter(r -> "ms-categoria".equals(r.get("id")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ruta ms-categoria no encontrada"));

        String uri = (String) route.get("uri");
        assertTrue(uri.contains("MS_CATEGORIA_URL"));
        assertTrue(uri.contains("8085"));
    }

    @Test
    void rutaMsEnvio_configuradaCorrectamente() {
        List<Map<String, Object>> routes = getRoutes();
        Map<String, Object> route = routes.stream()
                .filter(r -> "ms-envio".equals(r.get("id")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ruta ms-envio no encontrada"));

        String uri = (String) route.get("uri");
        assertTrue(uri.contains("MS_ENVIO_URL"));
        assertTrue(uri.contains("8086"));
    }

    @Test
    void rutaMsCupon_configuradaCorrectamente() {
        List<Map<String, Object>> routes = getRoutes();
        Map<String, Object> route = routes.stream()
                .filter(r -> "ms-cupon".equals(r.get("id")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ruta ms-cupon no encontrada"));

        String uri = (String) route.get("uri");
        assertTrue(uri.contains("MS_CUPON_URL"));
        assertTrue(uri.contains("8087"));
    }

    @Test
    void rutaMsOpinion_configuradaCorrectamente() {
        List<Map<String, Object>> routes = getRoutes();
        Map<String, Object> route = routes.stream()
                .filter(r -> "ms-opinion".equals(r.get("id")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ruta ms-opinion no encontrada"));

        String uri = (String) route.get("uri");
        assertTrue(uri.contains("MS_OPINION_URL"));
        assertTrue(uri.contains("8088"));
    }

    @Test
    void rutaMsNotificacion_configuradaCorrectamente() {
        List<Map<String, Object>> routes = getRoutes();
        Map<String, Object> route = routes.stream()
                .filter(r -> "ms-notificacion".equals(r.get("id")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ruta ms-notificacion no encontrada"));

        String uri = (String) route.get("uri");
        assertTrue(uri.contains("MS_NOTIFICACION_URL"));
        assertTrue(uri.contains("8089"));
    }

    @Test
    void todasLasRutasTienenPredicadoPath() {
        List<Map<String, Object>> routes = getRoutes();
        for (Map<String, Object> route : routes) {
            String id = (String) route.get("id");
            List<String> predicates = (List<String>) route.get("predicates");
            assertNotNull(predicates, "Ruta " + id + " debe tener predicates");
            assertFalse(predicates.isEmpty(), "Ruta " + id + " debe tener al menos un predicate");
            assertTrue(predicates.stream().anyMatch(p -> p.startsWith("Path=")),
                    "Ruta " + id + " debe tener un predicate Path");
        }
    }

    @Test
    void puertoDinamico_configuradoEnGateway() {
        Map<String, Object> yaml = loadYaml();
        Map<String, Object> server = (Map<String, Object>) yaml.get("server");
        Object port = server.get("port");
        assertNotNull(port);
        assertTrue(port.toString().contains("${PORT:8090}"), "El puerto del gateway debe ser dinamico ${PORT:8090}");
    }
}
