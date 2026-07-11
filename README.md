# Proyecto Microservicios

## Descripción del contexto / dominio

Sistema de gestión de una tienda basado en arquitectura de microservicios. Permite administrar el catálogo de
productos, el stock disponible (inventario), los clientes, los pedidos que realizan (asociando cliente + producto)
y los pagos generados a partir de esos pedidos.

## Estudiantes

- Nombre Apellido 1
- Nombre Apellido 2
- (completar con los integrantes del equipo)

## Microservicios implementados

| Microservicio  | Puerto | Descripción                                                                 |
|-----------------|--------|-------------------------------------------------------------------------------|
| `ms-producto`   | 8080   | CRUD del catálogo de productos.                                              |
| `ms-inventario` | 8081   | CRUD de stock disponible. Consume `ms-producto` para validar el producto.    |
| `ms-cliente`    | 8082   | CRUD de clientes.                                                             |
| `ms-pedido`     | 8083   | CRUD de pedidos. Consume `ms-cliente` y `ms-producto` para armar el pedido.  |
| `ms-pago`       | 8084   | CRUD de pagos. Consume `ms-pedido` para obtener el monto a pagar.            |
| `ms-gateway`    | 8090   | API Gateway (Spring Cloud Gateway). Punto de entrada único a todo el sistema. |

Cada microservicio sigue el patrón **CSR (Controller – Service – Repository/Model)**, con DTOs de entrada/salida,
validaciones con Bean Validation, manejo centralizado de errores (`@RestControllerAdvice`) y logs con SLF4J.

## Rutas principales del Gateway

Todas las peticiones pueden hacerse directamente a cada microservicio, o de forma centralizada a través del Gateway
en el puerto **8090**:

- `http://localhost:8090/api/productos`
- `http://localhost:8090/api/inventario`
- `http://localhost:8090/api/clientes`
- `http://localhost:8090/api/pedidos`
- `http://localhost:8090/api/pagos`

## Documentación Swagger / OpenAPI

Cada microservicio expone su propia documentación interactiva (no aplica a `ms-gateway`, que no tiene endpoints
propios):

- ms-producto: `http://localhost:8080/swagger-ui/index.html`
- ms-inventario: `http://localhost:8081/swagger-ui/index.html`
- ms-cliente: `http://localhost:8082/swagger-ui/index.html`
- ms-pedido: `http://localhost:8083/swagger-ui/index.html`
- ms-pago: `http://localhost:8084/swagger-ui/index.html`

## Instrucciones de ejecución

### Local (con Laragon/MySQL corriendo en el equipo)

Cada microservicio es un proyecto Maven independiente. Para levantar uno:

```bash
cd ms-producto
mvn spring-boot:run
```

Repetir para cada microservicio (`ms-inventario`, `ms-cliente`, `ms-pedido`, `ms-pago`, `ms-gateway`), en ese orden,
para que las dependencias entre servicios funcionen correctamente al consultarlos.

### Con Docker

Desde la raíz del proyecto (donde está `docker-compose.yml`):

```bash
docker compose up --build
```

Esto levanta MySQL (con las 5 bases de datos creadas automáticamente) y los 6 microservicios en una misma red.

Para apagar todo:

```bash
docker compose down
```

## Pruebas unitarias

Cada microservicio tiene sus pruebas unitarias en `src/test/java`, usando **JUnit 5 + Mockito**, mockeando el
`Repository` (y los `Client` en los microservicios que consumen a otros).

Para ejecutarlas:

```bash
cd ms-producto
mvn test
```

Repetir en cada carpeta de microservicio.
