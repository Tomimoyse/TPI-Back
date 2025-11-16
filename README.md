# TPI Microservicios - Backend de Aplicaciones 2025

## 📋 Requerimientos Funcionales Implementados

Este proyecto implementa los **11 requerimientos funcionales mínimos** especificados en el `enunciado.md`:

✅ **RF1**: Registrar nueva solicitud de transporte con cliente y contenedor
✅ **RF2**: Consultar estado del transporte de un contenedor
✅ **RF3**: Consultar rutas tentativas con tramos sugeridos, tiempo y costo estimados
✅ **RF4**: Asignar ruta con todos sus tramos a la solicitud
✅ **RF5**: Consultar contenedores pendientes de entrega con filtros
✅ **RF6**: Asignar camión a un tramo de traslado
✅ **RF7**: Determinar inicio o fin de un tramo de traslado (Transportista)
✅ **RF8**: Calcular costo total de la entrega
✅ **RF9**: Registrar cálculo de tiempo real y costo real al finalizar
✅ **RF10**: Registrar y actualizar depósitos, camiones y tarifas
✅ **RF11**: Validar que un camión no supere su capacidad máxima

📚 **Ver documentación completa de endpoints:** [API-ENDPOINTS.md](./API-ENDPOINTS.md)

## Estructura General
- El sistema está dividido en tres proyectos principales: `api-gateway`, `microservicio-operaciones` y `microservicio-solicitudes`.
- Cada microservicio es un proyecto Spring Boot independiente, con su propio `pom.xml`, controladores, modelos, repositorios y configuración.
- El gateway utiliza Spring Cloud Gateway para enrutar solicitudes a los microservicios.

## Decisiones Técnicas
- **Separación de responsabilidades:**
  - `microservicio-solicitudes`: gestiona clientes, contenedores y solicitudes.
  - `microservicio-operaciones`: gestiona camiones, tramos, rutas, depósitos, tarifas y lógica de asignación/validación.
- **Persistencia:**
  - Se utiliza PostgreSQL como base de datos en producción (configurable en `docker-compose.yml`).
  - Para desarrollo, se puede usar H2.
- **Seguridad:**
  - Keycloak se utiliza como proveedor de identidad y autorización.
  - Todos los endpoints están protegidos por JWT; algunos requieren roles específicos (ejemplo: solo ADMIN puede eliminar clientes).
- **Documentación:**
  - Swagger/OpenAPI está habilitado en ambos microservicios para facilitar el testing y la integración.
- **API Gateway:**
  - Centraliza la entrada al sistema y valida tokens JWT antes de enrutar.
- **Google Maps Directions API:**
  - Se integra para calcular distancias reales entre ubicaciones al crear tramos.
- **Dockerización:**
  - Cada microservicio y el gateway tienen su propio Dockerfile.
  - `docker-compose.yml` orquesta todo el sistema, incluyendo Keycloak y la base de datos.

## Desafíos y Soluciones
- **Integración de Keycloak:**
  - Se configuró para que tanto el gateway como los microservicios validen tokens JWT.
  - Se habilitó acceso público solo a Swagger y endpoints de documentación.
  - Se implementó protección por rol usando anotaciones `@PreAuthorize`.
- **Consumo de Google Maps API:**
  - Se manejó la obtención y parseo de la distancia en kilómetros.
  - Se parametrizó la API key en el archivo de configuración.
- **Comunicación entre microservicios:**
  - El gateway enruta por path y reenvía el token JWT.
- **Pruebas y Colección Postman:**
  - Se creó una colección de pruebas con ejemplos de login, uso de endpoints protegidos y operaciones clave.
- **Persistencia y relaciones:**
  - Se definieron relaciones JPA (ej: tramos en rutas, solicitudes con cliente y contenedor).
- **Logs:**
  - Se agregaron logs en operaciones clave para facilitar la auditoría y el debugging.

## Información extra

### Guía rápida de despliegue

1. Clona el repositorio y navega a la carpeta raíz.
2. Asegúrate de tener Docker y Docker Compose instalados.
3. Configura la variable de entorno `GOOGLE_MAPS_API_KEY` con tu clave de Google Maps.
4. Ejecuta:
   ```sh
   docker-compose up --build
   ```
5. Accede a los servicios:
   - Gateway: http://localhost:8080
   - Keycloak: http://localhost:8084
   - Swagger Operaciones: http://localhost:8082/swagger-ui.html
   - Swagger Solicitudes: http://localhost:8081/swagger-ui.html

### Guía de uso de la API

- Autenticación: Obtén un token JWT desde Keycloak (endpoint `/realms/tu-realm/protocol/openid-connect/token`).
- Usa el token en el header `Authorization: Bearer <token>` para acceder a los endpoints protegidos.
- Endpoints principales:
  - CRUD de clientes, contenedores, solicitudes, tramos, rutas, depósitos, tarifas.
  - Asignación de tramos a rutas.
  - Cálculo de distancia entre ciudades (Google Maps).
- Consulta la documentación Swagger en cada microservicio para ver todos los endpoints disponibles.

### Configuración de Keycloak

1. Ingresa a http://localhost:8084 como admin (admin/admin).
2. Crea un nuevo realm (ej: `tpi-realm`).
3. Crea clientes para cada microservicio y el gateway (tipo: confidential).
4. Crea roles (ej: `ADMIN`, `USER`) y asígnalos a los usuarios.
5. Usa el cliente y usuario para obtener el token JWT desde Postman o Thunder Client.

### Estructura de carpetas

- `api-gateway/`: Gateway de entrada, enruta y valida JWT.
- `microservicio-operaciones/`: Lógica de operaciones, rutas, tramos, etc.
- `microservicio-solicitudes/`: Lógica de clientes, contenedores, solicitudes.
- `docker-compose.yml`: Orquestación de servicios.
- `TPI-Microservicios.postman_collection.json`: Colección de pruebas.

### Pruebas automáticas

- Ejecuta los tests con:
  ```sh
  mvn test
  ```
- Hay ejemplos de tests unitarios para servicios clave.

### Monitoreo y métricas

- Accede a `/actuator/health` y `/actuator/metrics` en cada microservicio para ver el estado y métricas básicas.
- (Opcional) Integra Prometheus/Grafana para monitoreo avanzado.

### Consideraciones de seguridad

- Cambia las contraseñas por defecto en Keycloak y la base de datos para producción.
- Expón solo los puertos necesarios.
- Usa HTTPS en producción.

### Contacto y soporte

- Equipo TPI - 2025
- Para soporte, abre un issue en el repositorio o contacta al responsable del equipo.

---

**Autor:** Equipo TPI - 2025
