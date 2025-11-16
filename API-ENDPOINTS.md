# API Endpoints - TPI Backend Microservicios

## Descripción General

Este documento describe los endpoints implementados para cumplir con los **11 requerimientos funcionales mínimos** especificados en el enunciado.

## Base URLs

- **API Gateway**: `http://localhost:8080`
- **Microservicio Solicitudes**: `http://localhost:8081`
- **Microservicio Operaciones**: `http://localhost:8082`

---

## 🔐 Autenticación

Todos los endpoints requieren autenticación JWT (excepto endpoints de test). 

**Header requerido:**
```
Authorization: Bearer <token>
```

**Roles disponibles:**
- `CLIENTE`: Para operaciones de clientes
- `OPERADOR`: Para operaciones administrativas de logística
- `TRANSPORTISTA`: Para operaciones de camioneros
- `ADMIN`: Acceso completo

---

## 📦 Microservicio Solicitudes

### Clientes

#### Listar todos los clientes
```http
GET /solicitudes/clientes
```

#### Obtener cliente por ID
```http
GET /solicitudes/clientes/{id}
```

#### Crear cliente
```http
POST /solicitudes/clientes
Content-Type: application/json

{
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "telefono": "+54911234567",
  "direccion": "Av. Corrientes 1234, CABA",
  "documento": "12345678"
}
```

---

### Contenedores

#### Listar todos los contenedores
```http
GET /solicitudes/contenedores
```

#### Crear contenedor
```http
POST /solicitudes/contenedores
Content-Type: application/json

{
  "identificacion": "CONT-001",
  "tipo": "20ft Standard",
  "peso": 5000.0,
  "volumen": 33.0,
  "estado": "disponible"
}
```

---

### Solicitudes

#### ✅ RF1: Registrar nueva solicitud completa (Cliente)
```http
POST /solicitudes/solicitudes/completa
Authorization: Bearer <token>
Content-Type: application/json

{
  "descripcion": "Transporte de contenedor a obra",
  "direccionOrigen": "Av. Libertador 5000, CABA",
  "direccionDestino": "Ruta 9 Km 100, Provincia de Buenos Aires",
  "latitudOrigen": -34.5731,
  "longitudOrigen": -58.4486,
  "latitudDestino": -34.9211,
  "longitudDestino": -57.9544,
  "cliente": {
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "telefono": "+54911234567",
    "direccion": "Av. Corrientes 1234",
    "documento": "12345678"
  },
  "contenedor": {
    "identificacion": "CONT-001",
    "tipo": "20ft Standard",
    "peso": 5000.0,
    "volumen": 33.0
  }
}
```

**Roles:** `CLIENTE`, `ADMIN`

---

#### ✅ RF2: Consultar estado del transporte (Cliente)
```http
GET /solicitudes/solicitudes/{id}/estado
```

**Respuesta:**
```json
{
  "solicitudId": 1,
  "estado": "programada",
  "contenedorId": 1,
  "contenedorIdentificacion": "CONT-001",
  "contenedorEstado": "en tránsito",
  "direccionOrigen": "...",
  "direccionDestino": "...",
  "costoEstimado": 15000.0,
  "tiempoEstimadoHoras": 24,
  "rutaId": 1
}
```

---

#### ✅ RF4: Asignar ruta a solicitud (Operador/Admin)
```http
PUT /solicitudes/solicitudes/{id}/asignar-ruta
Authorization: Bearer <token>
Content-Type: application/json

{
  "rutaId": 1,
  "costoEstimado": 15000.0,
  "tiempoEstimadoHoras": 24
}
```

**Roles:** `ADMIN`, `OPERADOR`

---

#### ✅ RF5: Consultar contenedores pendientes con filtros (Operador/Admin)
```http
GET /solicitudes/solicitudes/pendientes?estado=programada
```

**Roles:** `ADMIN`, `OPERADOR`

---

#### ✅ RF9: Finalizar solicitud con costo y tiempo real (Operador/Admin)
```http
PUT /solicitudes/solicitudes/{id}/finalizar
Authorization: Bearer <token>
Content-Type: application/json

{
  "costoFinal": 16500.0,
  "tiempoRealHoras": 26
}
```

**Roles:** `ADMIN`, `OPERADOR`

---

## 🚛 Microservicio Operaciones

### Camiones

#### ✅ RF10: Listar todos los camiones
```http
GET /operaciones/camiones
```

#### Obtener camión por dominio
```http
GET /operaciones/camiones/{dominio}
```

#### ✅ RF10: Crear camión (Operador/Admin)
```http
POST /operaciones/camiones
Authorization: Bearer <token>
Content-Type: application/json

{
  "dominio": "ABC123",
  "nombreTransportista": "Pedro Rodríguez",
  "telefono": "+54911234567",
  "capacidadPeso": 8000.0,
  "capacidadVolumen": 40.0,
  "disponible": true,
  "consumoPorKm": 0.35,
  "costoBasePorKm": 50.0
}
```

**Roles:** `ADMIN`, `OPERADOR`

---

#### Listar camiones disponibles
```http
GET /operaciones/camiones/disponibles
```

#### Listar camiones aptos para un contenedor
```http
GET /operaciones/camiones/aptos?peso=5000&volumen=33
```

#### ✅ RF11: Validar capacidad de camión
```http
GET /operaciones/camiones/{dominio}/puede-transportar?peso=5000&volumen=33
```

**Respuesta:** `true` o `false`

---

### Depósitos

#### ✅ RF10: Listar depósitos
```http
GET /operaciones/depositos
```

#### ✅ RF10: Crear depósito (Operador/Admin)
```http
POST /operaciones/depositos
Authorization: Bearer <token>
Content-Type: application/json

{
  "nombre": "Depósito Central",
  "direccion": "Av. General Paz 1000, CABA",
  "latitud": -34.6037,
  "longitud": -58.3816,
  "costoEstadiaDiaria": 500.0
}
```

**Roles:** `ADMIN`, `OPERADOR`

---

### Tarifas

#### ✅ RF10: Listar tarifas
```http
GET /operaciones/tarifas
```

#### ✅ RF10: Crear tarifa (Operador/Admin)
```http
POST /operaciones/tarifas
Authorization: Bearer <token>
Content-Type: application/json

{
  "descripcion": "Tarifa contenedor 20ft",
  "costoBaseKm": 100.0,
  "valorLitroCombustible": 150.0,
  "pesoMinimo": 0.0,
  "pesoMaximo": 8000.0,
  "volumenMinimo": 0.0,
  "volumenMaximo": 35.0,
  "cargoGestionFijo": 1000.0
}
```

**Roles:** `ADMIN`, `OPERADOR`

---

### Rutas

#### ✅ RF3: Calcular ruta tentativa con tramos y costos estimados (Operador/Admin)
```http
POST /operaciones/rutas/calcular-tentativa
Authorization: Bearer <token>
Content-Type: application/json

{
  "solicitudId": 1,
  "latitudOrigen": -34.5731,
  "longitudOrigen": -58.4486,
  "direccionOrigen": "Av. Libertador 5000, CABA",
  "latitudDestino": -34.9211,
  "longitudDestino": -57.9544,
  "direccionDestino": "Ruta 9 Km 100, Provincia",
  "peso": 5000.0,
  "volumen": 33.0,
  "depositoIds": [1, 2]
}
```

**Respuesta:**
```json
{
  "id": 1,
  "nombre": "Ruta para Solicitud 1",
  "solicitudId": 1,
  "cantidadTramos": 3,
  "cantidadDepositos": 2,
  "distanciaTotal": 450.5,
  "costoEstimado": 25000.0,
  "tiempoEstimadoHoras": 8,
  "tramos": [
    {
      "id": 1,
      "origen": "Av. Libertador 5000, CABA",
      "destino": "Depósito Central",
      "tipoTramo": "origen-deposito",
      "estado": "estimado",
      "distancia": 150.0,
      "costoAproximado": 8000.0
    }
  ]
}
```

**Roles:** `ADMIN`, `OPERADOR`

---

#### Calcular costo y tiempo real de una ruta
```http
PUT /operaciones/rutas/{id}/calcular-real
Authorization: Bearer <token>
```

**Roles:** `ADMIN`, `OPERADOR`

---

### Tramos

#### Listar todos los tramos
```http
GET /operaciones/tramos
```

#### Obtener tramo por ID
```http
GET /operaciones/tramos/{id}
```

#### Crear tramo (Operador/Admin)
```http
POST /operaciones/tramos
Authorization: Bearer <token>
Content-Type: application/json

{
  "origen": "Av. Libertador 5000",
  "destino": "Ruta 9 Km 100",
  "latitudOrigen": -34.5731,
  "longitudOrigen": -58.4486,
  "latitudDestino": -34.9211,
  "longitudDestino": -57.9544,
  "tipoTramo": "origen-destino",
  "estado": "estimado"
}
```

**Roles:** `ADMIN`, `OPERADOR`

---

#### ✅ RF6: Asignar camión a tramo (Operador/Admin)
```http
PUT /operaciones/tramos/{id}/asignar-camion
Authorization: Bearer <token>
Content-Type: application/json

{
  "camionDominio": "ABC123",
  "peso": 5000.0,
  "volumen": 33.0
}
```

**Descripción:** Asigna un camión al tramo y valida que tenga capacidad suficiente (RF11).

**Roles:** `ADMIN`, `OPERADOR`

---

#### ✅ RF7: Iniciar tramo (Transportista)
```http
PUT /operaciones/tramos/{id}/iniciar
Authorization: Bearer <token>
```

**Descripción:** El transportista registra el inicio del viaje. El tramo debe estar en estado "asignado".

**Roles:** `TRANSPORTISTA`, `ADMIN`

---

#### ✅ RF7: Finalizar tramo (Transportista)
```http
PUT /operaciones/tramos/{id}/finalizar
Authorization: Bearer <token>
```

**Descripción:** El transportista registra el fin del viaje. Calcula el costo real y libera el camión.

**Roles:** `TRANSPORTISTA`, `ADMIN`

---

#### Calcular distancia entre dos puntos
```http
GET /operaciones/tramos/distancia?origen=Buenos Aires&destino=La Plata
```

#### Calcular distancia por coordenadas
```http
GET /operaciones/tramos/distancia-coordenadas?lat1=-34.5731&lon1=-58.4486&lat2=-34.9211&lon2=-57.9544
```

---

## 🧮 Resumen de Requerimientos Funcionales

| RF | Descripción | Endpoint | Método | Rol |
|----|-------------|----------|--------|-----|
| 1 | Registrar solicitud completa | `/solicitudes/solicitudes/completa` | POST | CLIENTE, ADMIN |
| 2 | Consultar estado de transporte | `/solicitudes/solicitudes/{id}/estado` | GET | Todos |
| 3 | Consultar rutas tentativas | `/operaciones/rutas/calcular-tentativa` | POST | OPERADOR, ADMIN |
| 4 | Asignar ruta a solicitud | `/solicitudes/solicitudes/{id}/asignar-ruta` | PUT | OPERADOR, ADMIN |
| 5 | Consultar contenedores pendientes | `/solicitudes/solicitudes/pendientes` | GET | OPERADOR, ADMIN |
| 6 | Asignar camión a tramo | `/operaciones/tramos/{id}/asignar-camion` | PUT | OPERADOR, ADMIN |
| 7 | Iniciar/Finalizar tramo | `/operaciones/tramos/{id}/iniciar` o `/finalizar` | PUT | TRANSPORTISTA, ADMIN |
| 8 | Cálculo de costo total | Servicio `CostoService` | - | - |
| 9 | Registrar costo y tiempo real | `/solicitudes/solicitudes/{id}/finalizar` | PUT | OPERADOR, ADMIN |
| 10 | CRUD depósitos, camiones, tarifas | Varios endpoints | CRUD | OPERADOR, ADMIN |
| 11 | Validar capacidad de camión | `/operaciones/camiones/{dominio}/puede-transportar` | GET | Todos |

---

## 📊 Estados de Solicitud

- `borrador`: Solicitud creada pero sin ruta asignada
- `programada`: Solicitud con ruta asignada
- `en tránsito`: Al menos un tramo iniciado
- `entregada`: Todos los tramos completados

---

## 📊 Estados de Tramo

- `estimado`: Tramo calculado en ruta tentativa
- `asignado`: Tramo con camión asignado
- `iniciado`: Tramo en proceso (transportista viajando)
- `finalizado`: Tramo completado

---

## 🔄 Flujo de Trabajo Típico

1. **Cliente** crea solicitud completa (RF1)
2. **Operador** consulta rutas tentativas con diferentes opciones de depósitos (RF3)
3. **Operador** selecciona una ruta y la asigna a la solicitud (RF4)
4. **Operador** asigna camiones a cada tramo de la ruta (RF6)
5. **Transportista** inicia el primer tramo (RF7)
6. **Transportista** finaliza el primer tramo y continúa con los siguientes (RF7)
7. **Operador** finaliza la solicitud registrando costos y tiempos reales (RF9)
8. **Cliente** puede consultar el estado en cualquier momento (RF2)

---

## 🔧 Configuración

### Variables de Entorno

```env
# Google Maps API Key (opcional, usa Haversine si no está configurada)
GOOGLE_MAPS_API_KEY=tu_api_key_aqui

# Keycloak (para autenticación)
KEYCLOAK_REALM=tpi-realm
KEYCLOAK_CLIENT_ID=tpi-client
KEYCLOAK_CLIENT_SECRET=secret
```

### Cálculo de Costos

El sistema calcula costos basándose en:
- **Costo base por km** del camión
- **Consumo de combustible** del camión × distancia × valor del litro
- **Cargo de gestión fijo** por tramo
- **Costo de estadía** en depósitos (días × costo diario)

---

## 🧪 Testing

Se recomienda usar la colección de Postman incluida en:
```
TPI-Microservicios.postman_collection.json
```

O usar herramientas como Bruno, Thunder Client, o el archivo:
```
api-tests.http
```

---

## 📝 Notas Importantes

1. **Validación de Capacidad**: El sistema valida automáticamente que el camión tenga capacidad suficiente al asignar (RF11).

2. **Cálculo de Distancias**: Utiliza Google Maps API si está configurada, caso contrario usa la fórmula de Haversine como fallback.

3. **Disponibilidad de Camiones**: Los camiones se marcan como "no disponibles" al asignarlos a un tramo y se liberan al finalizar.

4. **Cliente Existente**: Si un cliente con el mismo email ya existe, se reutiliza en lugar de crear uno nuevo.

5. **Swagger**: Documentación interactiva disponible en:
   - Solicitudes: http://localhost:8081/swagger-ui.html
   - Operaciones: http://localhost:8082/swagger-ui.html
