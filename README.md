# Proyecto Microservicios

## Stack Tecnologico

- **Java 21** + **Spring Boot 4.1.0**
- **Spring Cloud Gateway** (API Gateway)
- **Spring WebFlux** (WebClient para comunicacion entre servicios)
- **Spring Data JPA + Hibernate** (persistencia)
- **MySQL 8** (base de datos)
- **Lombok** (reduccion de boilerplate)
- **SpringDoc OpenAPI** (documentacion Swagger)
- **JUnit 5 + Mockito** (pruebas unitarias)
- **JaCoCo** (cobertura de codigo)
- **Docker + Docker Compose** (despliegue)

## Descripcion del contexto / dominio

Sistema de gestion de una tienda basado en arquitectura de microservicios. Permite administrar el catalogo de
productos, el stock disponible (inventario), los clientes, los pedidos que realizan (asociando cliente + producto)
y los pagos generados a partir de esos pedidos.

## Estudiantes

- **Simon Ramirez**
- **Jorge Delgado**

## Gestion del proyecto

- **Tablero Trello:** [COMPLETAR CON EL LINK DEL TABLERO]

## Microservicios implementados

| Microservicio  | Puerto | Descripcion                                                               |
|-----------------|--------|-------------------------------------------------------------------------------|
| `ms-producto`   | 8080   | CRUD del catalogo de productos.                                              |
| `ms-inventario` | 8081   | CRUD de stock disponible. Consume `ms-producto` para validar el producto.    |
| `ms-cliente`    | 8082   | CRUD de clientes.                                                             |
| `ms-pedido`     | 8083   | CRUD de pedidos. Consume `ms-cliente` y `ms-producto` para armar el pedido.  |
| `ms-pago`       | 8084   | CRUD de pagos. Consume `ms-pedido` para obtener el monto a pagar.            |
| `ms-categoria`  | 8085   | CRUD de categorias de productos.                                              |
| `ms-envio`      | 8086   | CRUD de envios. Consume `ms-pedido` para validar el pedido.                  |
| `ms-cupon`      | 8087   | CRUD de cupones de descuento.                                                 |
| `ms-opinion`    | 8088   | CRUD de opiniones. Consume `ms-cliente` y `ms-producto` para validar.        |
| `ms-notificacion` | 8089 | CRUD de notificaciones. Consume `ms-cliente` para validar el cliente.       |
| `ms-gateway`    | 8090   | API Gateway (Spring Cloud Gateway). Punto de entrada unico a todo el sistema. |

Cada microservicio sigue el patron **CSR (Controller - Service - Repository/Model)**, con DTOs de entrada/salida,
validaciones con Bean Validation, manejo centralizado de errores (`@RestControllerAdvice`) y logs con SLF4J.

## Arquitectura y comunicacion entre servicios

```
                    ┌─────────────┐
                    │ ms-gateway  │
                    │   :8090     │
                    └──────┬──────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
┌───────────────┐  ┌───────────────┐  ┌───────────────┐
│ ms-producto   │  │ ms-cliente    │  │ ms-inventario  │
│    :8080      │  │    :8082      │  │    :8081       │
└───────────────┘  └───────────────┘  └───────┬───────┘
                                              │
        ┌─────────────────────────────────────┘
        │ WebClient
        ▼
┌───────────────┐
│ ms-producto   │
│    :8080      │
└───────────────┘

┌───────────────┐        ┌───────────────┐
│ ms-pedido     │◄──────►│ ms-cliente    │
│    :8083      │        │    :8082      │
└───────┬───────┘        └───────────────┘
        │
        │ WebClient
        ▼
┌───────────────┐
│ ms-producto   │
│    :8080      │
└───────────────┘

┌───────────────┐
│ ms-pago       │
│    :8084      │
└───────┬───────┘
        │ WebClient
        ▼
┌───────────────┐
│ ms-pedido     │
│    :8083      │
└───────────────┘
```

**Comunicacion entre servicios:** WebClient (Spring WebFlux)

| Servicio consumidor | Servicio consumido | Metodo |
|---------------------|-------------------|--------|
| ms-inventario | ms-producto | GET /api/productos/{id} |
| ms-pedido | ms-producto | GET /api/productos/{id} |
| ms-pedido | ms-cliente | GET /api/clientes/{id} |
| ms-pago | ms-pedido | GET /api/pedidos/{id} |
| ms-envio | ms-pedido | GET /api/pedidos/{id} |
| ms-opinion | ms-cliente | GET /api/clientes/{id} |
| ms-opinion | ms-producto | GET /api/productos/{id} |
| ms-notificacion | ms-cliente | GET /api/clientes/{id} |

## Rutas principales del Gateway

Todas las peticiones pueden hacerse directamente a cada microservicio, o de forma centralizada a traves del Gateway
en el puerto **8090**:

- `http://localhost:8090/api/productos`
- `http://localhost:8090/api/inventario`
- `http://localhost:8090/api/clientes`
- `http://localhost:8090/api/pedidos`
- `http://localhost:8090/api/pagos`
- `http://localhost:8090/api/categorias`
- `http://localhost:8090/api/envios`
- `http://localhost:8090/api/cupones`
- `http://localhost:8090/api/opiniones`
- `http://localhost:8090/api/notificaciones`

## Documentacion Swagger / OpenAPI

Cada microservicio expone su propia documentacion interactiva (no aplica a `ms-gateway`, que no tiene endpoints
propios):

- ms-producto: `http://localhost:8080/swagger-ui/index.html`
- ms-inventario: `http://localhost:8081/swagger-ui/index.html`
- ms-cliente: `http://localhost:8082/swagger-ui/index.html`
- ms-pedido: `http://localhost:8083/swagger-ui/index.html`
- ms-pago: `http://localhost:8084/swagger-ui/index.html`
- ms-categoria: `http://localhost:8085/swagger-ui/index.html`
- ms-envio: `http://localhost:8086/swagger-ui/index.html`
- ms-cupon: `http://localhost:8087/swagger-ui/index.html`
- ms-opinion: `http://localhost:8088/swagger-ui/index.html`
- ms-notificacion: `http://localhost:8089/swagger-ui/index.html`

## Endpoints principales

### ms-producto (puerto 8080)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/productos | Listar todos los productos |
| GET | /api/productos/{id} | Obtener producto por ID |
| POST | /api/productos | Crear nuevo producto |
| PUT | /api/productos/{id} | Actualizar producto |
| DELETE | /api/productos/{id} | Eliminar producto |

### ms-inventario (puerto 8081)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/inventario | Listar todo el inventario |
| GET | /api/inventario/{id} | Obtener inventario por ID |
| POST | /api/inventario | Crear registro de inventario |
| PUT | /api/inventario/{id} | Actualizar inventario |
| DELETE | /api/inventario/{id} | Eliminar inventario |

### ms-cliente (puerto 8082)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/clientes | Listar todos los clientes |
| GET | /api/clientes/{id} | Obtener cliente por ID |
| POST | /api/clientes | Crear nuevo cliente |
| PUT | /api/clientes/{id} | Actualizar cliente |
| DELETE | /api/clientes/{id} | Eliminar cliente |

### ms-pedido (puerto 8083)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/pedidos | Listar todos los pedidos |
| GET | /api/pedidos/{id} | Obtener pedido por ID |
| POST | /api/pedidos | Crear nuevo pedido |
| PUT | /api/pedidos/{id} | Actualizar pedido |
| DELETE | /api/pedidos/{id} | Eliminar pedido |

### ms-pago (puerto 8084)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/pagos | Listar todos los pagos |
| GET | /api/pagos/{id} | Obtener pago por ID |
| POST | /api/pagos | Crear nuevo pago |
| PUT | /api/pagos/{id} | Actualizar pago |
| DELETE | /api/pagos/{id} | Eliminar pago |

### ms-categoria (puerto 8085)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/categorias | Listar todas las categorias |
| GET | /api/categorias/{id} | Obtener categoria por ID |
| POST | /api/categorias | Crear nueva categoria |
| PUT | /api/categorias/{id} | Actualizar categoria |
| DELETE | /api/categorias/{id} | Eliminar categoria |

### ms-envio (puerto 8086)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/envios | Listar todos los envios |
| GET | /api/envios/{id} | Obtener envio por ID |
| POST | /api/envios | Crear nuevo envio |
| PUT | /api/envios/{id} | Actualizar envio |
| DELETE | /api/envios/{id} | Eliminar envio |

### ms-cupon (puerto 8087)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/cupones | Listar todos los cupones |
| GET | /api/cupones/{id} | Obtener cupon por ID |
| POST | /api/cupones | Crear nuevo cupon |
| PUT | /api/cupones/{id} | Actualizar cupon |
| DELETE | /api/cupones/{id} | Eliminar cupon |

### ms-opinion (puerto 8088)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/opiniones | Listar todas las opiniones |
| GET | /api/opiniones/{id} | Obtener opinion por ID |
| POST | /api/opiniones | Crear nueva opinion |
| PUT | /api/opiniones/{id} | Actualizar opinion |
| DELETE | /api/opiniones/{id} | Eliminar opinion |

### ms-notificacion (puerto 8089)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/notificaciones | Listar todas las notificaciones |
| GET | /api/notificaciones/{id} | Obtener notificacion por ID |
| POST | /api/notificaciones | Crear nueva notificacion |
| PUT | /api/notificaciones/{id} | Actualizar notificacion |
| DELETE | /api/notificaciones/{id} | Eliminar notificacion |
| PATCH | /api/notificaciones/{id}/leer | Marcar como leida |

## Instrucciones de ejecucion

### Local (con MySQL corriendo en el equipo)

Cada microservicio es un proyecto Maven independiente. Para levantar uno:

```bash
cd ms-producto
mvn spring-boot:run
```

Repetir para cada microservicio (`ms-inventario`, `ms-cliente`, `ms-pedido`, `ms-pago`, `ms-gateway`), en ese orden,
para que las dependencias entre servicios funcionen correctamente al consultarlos.

### Con Docker

Desde la raiz del proyecto (donde esta `docker-compose.yml`):

```bash
docker compose up --build
```

Esto levanta MySQL (con las 11 bases de datos creadas automaticamente) y los 11 microservicios en una misma red.

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

### Cobertura de codigo (JaCoCo)

Para generar el reporte de cobertura:

```bash
cd ms-producto
mvn test jacoco:report
```

El reporte se genera en `target/site/jacoco/index.html`. Se requiere minimo **80% de cobertura**.

## Estructura del proyecto (patron CSR)

```
ms-producto/
├── src/main/java/com/example/ms_producto/
│   ├── controller/          # Endpoints REST
│   │   └── ProductoController.java
│   ├── service/             # Logica de negocio
│   │   ├── ProductoService.java
│   │   └── ProductoServiceImpl.java
│   ├── repository/          # Acceso a datos
│   │   └── ProductoRepository.java
│   ├── model/               # Entidades JPA
│   │   └── Producto.java
│   ├── dto/                 # DTOs de entrada/salida
│   │   ├── ProductoRequestDTO.java
│   │   └── ProductoResponseDTO.java
│   ├── exception/           # Manejo centralizado de errores
│   │   └── GlobalExceptionHandler.java
│   └── MsProductoApplication.java
├── src/test/java/           # Pruebas unitarias
│   └── ProductoServiceTest.java
├── pom.xml
├── Dockerfile
└── application.yml
```
