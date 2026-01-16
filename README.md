# Soccer Fees Management

Solución modular en Kotlin para administrar pagos mensuales de un grupo de aficionados de soccer. Cada módulo (Ktor, Spring, Micronaut, Quarkus) corre de forma independiente con su propia base de datos PostgreSQL y vista React.

## Estructura

```
soccer-fees-management/
├── common/
│   ├── domain/
│   └── db/init.sql
├── ktor-app/
├── spring-app/
├── micronaut-app/
└── quarkus-app/
```

## Reglas de negocio

- Pago mensual de socio: S/ 15.
- Invitado por lunes: S/ 5.
- Alquiler de cancha por lunes: S/ 40.
- Se registran ingresos (pagos) y egresos (gastos) por mes.

## Ejecutar con Docker

Cada módulo tiene su propio `docker-compose.yml` y se ejecuta de forma independiente.

### Nota para usuarios de Podman (sin Docker)
La build está preparada para Maven. Si quieres compilar todo desde Podman:
```
podman run --rm -v "$PWD":/app -w /app maven:3.9.8-eclipse-temurin-17 mvn -DskipTests package
```

### Ktor
```
cd ktor-app
podman compose up --build
```
- Frontend: `http://localhost:3000`
- API: `http://localhost:8080/api`

### Spring
```
cd spring-app
podman compose up --build
```
- Frontend: `http://localhost:3001`
- API: `http://localhost:8081/api`

### Micronaut
```
cd micronaut-app
podman compose up --build
```
- Frontend: `http://localhost:3002`
- API: `http://localhost:8082/api`

### Quarkus
```
cd quarkus-app
podman compose up --build
```
- Frontend: `http://localhost:3003`
- API: `http://localhost:8083/api`

> Nota: la base de datos se inicializa con `common/db/init.sql` en cada módulo.

## Endpoints principales

- `GET /api/health`
- `GET /api/players`
- `POST /api/players`
- `GET /api/payments?year=YYYY&month=MM`
- `POST /api/payments`
- `POST /api/expenses`
- `GET /api/summary?year=YYYY&month=MM`

## Cómo funciona Ktor en este proyecto

Ktor es un framework **ligero y explícito**: tú registras los plugins y defines las rutas en código Kotlin. No hay anotaciones tipo Spring ni auto‑configuración pesada. Aquí se ve el flujo principal:

- **Arranque del servidor**: `ktor-app/src/main/kotlin/com/soccerfees/ktor/Application.kt` usa `embeddedServer(Netty, port)` y llama a `module()`.
- **Plugins**: en `module()` se instala `ContentNegotiation` con Jackson y `StatusPages` para manejar errores de validación.
- **Routing**: las rutas están declaradas con `routing { route("/api") { ... } }`. Cada endpoint usa `call.receive<T>()` para leer JSON y `call.respond(...)` para responder.
- **Capa de datos**: `DatabaseFactory` crea un `HikariDataSource` leyendo variables de entorno. `PaymentsRepository` usa SQL directo (JDBC) para CRUD y reportes.

### Equivalencias con Spring/Quarkus

- **Controllers**: en Ktor no hay `@RestController`. Se usan funciones y rutas declarativas.
- **Inyección de dependencias**: en este ejemplo no hay DI; se instancia el repositorio manualmente dentro de `module()`.
- **Configuración**: en lugar de `application.yml`, Ktor lee variables de entorno directamente (se puede integrar con `application.conf` si quieres).
- **Serialización**: Ktor usa un plugin (`ContentNegotiation`) para JSON; en Spring es auto‑configurado por Jackson.

Si quieres, puedo agregar una versión con DI (Koin) y `application.conf` para que tus alumnos vean una estructura más “enterprise”.

## Datos de ejemplo

Crear socio:
```json
{
  "name": "Juan Pérez",
  "joinedAt": "2025-06-01"
}
```

Registrar pago:
```json
{
  "playerId": "...",
  "paidAt": "2025-06-10",
  "amount": 15.00,
  "type": "MEMBER_FEE"
}
```

Registrar invitado:
```json
{
  "playerId": null,
  "paidAt": "2025-06-10",
  "amount": 5.00,
  "type": "GUEST_FEE"
}
```

Registrar gasto:
```json
{
  "spentAt": "2025-06-10",
  "amount": 40.00,
  "description": "Alquiler de cancha"
}
```
