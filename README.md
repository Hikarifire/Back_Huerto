# Huerto Escolar API

Backend REST para la aplicación Android **Huerto Escolar**, construido con Java 21, Spring Boot, Spring Data JPA, Spring Security y MySQL.

## Funcionalidades

- Registro de cuentas de estudiante e inicio/cierre de sesión.
- Autenticación mediante token Bearer aleatorio, con vencimiento y revocación guardados en la base de datos.
- Contraseñas protegidas con BCrypt; nunca se devuelve el hash en las respuestas.
- Roles `DOCENTE` y `ALUMNO`.
- CRUD de sectores y cultivos, con restricciones para no borrar elementos relacionados.
- Creación, asignación, consulta y actualización de tareas.
- Los estudiantes solo consultan las tareas asignadas a su propia cuenta.
- Entrega de evidencia mediante fotografía multipart y comentario.
- Revisión docente de evidencias: aprobar o rechazar.
- Historial de lecturas simuladas de humedad y temperatura.
- Validación de entradas y respuestas de error consistentes.

## Requisitos

- JDK 21.
- MySQL 8 o compatible.
- Maven Wrapper incluido.

## Preparar la base de datos

1. Abre MySQL Workbench.
2. Ejecuta el archivo `database/huerto_escolar.sql` para crear la base de datos vacía `huerto_escolar`.
3. Revisa las credenciales de MySQL en `src/main/resources/application.properties` o define variables de entorno:

| Variable | Valor predeterminado |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/huerto_escolar?...` |
| `DB_USERNAME` | `root` |
| `DB_PASSWORD` | `root` |
| `PORT` | `8080` |
| `SEED_DEMO_DATA` | `true` |
| `TOKEN_EXPIRATION_HOURS` | `24` |
| `UPLOAD_DIR` | `uploads` |

4. Inicia la aplicación. Hibernate/JPA creará las tablas y sus relaciones a partir de las clases del paquete `entity` (`spring.jpa.hibernate.ddl-auto=update`).

> Cambia `DB_PASSWORD` por la contraseña real de tu MySQL. No uses las credenciales de ejemplo en un entorno publicado.

## Ejecutar

En Windows:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="TU_CLAVE_MYSQL"
.\mvnw.cmd spring-boot:run
```

En Linux/macOS:

```bash
export DB_USERNAME=root
export DB_PASSWORD='TU_CLAVE_MYSQL'
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080`. Verifica el servicio en `GET /api/health`.

### Conectar desde Android

- Emulador Android Studio: usa `http://10.0.2.2:8080` como URL base.
- Teléfono físico: usa `http://IP_LOCAL_DEL_PC:8080` y conecta ambos dispositivos a la misma red Wi-Fi.
- Para HTTP local de desarrollo, Android puede requerir habilitar tráfico cleartext en el manifiesto. En producción usa HTTPS.

## Cuentas de demostración

Se insertan una sola vez cuando `SEED_DEMO_DATA=true` y no existen ya en la base:

| Rol | Correo | Contraseña |
|---|---|---|
| Docente | `docente@huerto.cl` | `Docente123!` |
| Estudiante 1 | `estudiante1@huerto.cl` | `Estudiante123!` |
| Estudiante 2 | `estudiante2@huerto.cl` | `Estudiante123!` |
| Estudiante 3 | `estudiante3@huerto.cl` | `Estudiante123!` |

El registro público crea cuentas `ALUMNO`. Esto evita que cualquier persona pueda registrarse públicamente como docente. Las cuentas docentes se crean mediante datos de demostración o por un proceso administrativo controlado.

## Autenticación

1. Ejecuta `POST /api/auth/login` con correo y contraseña.
2. Copia el campo `token` de la respuesta.
3. En las demás solicitudes protegidas agrega el encabezado `Authorization: Bearer TU_TOKEN`.
4. Cierra la sesión con `POST /api/auth/logout` y el mismo encabezado.

Ejemplo de inicio de sesión:

```http
POST /api/auth/login
Content-Type: application/json

{
  "correo": "docente@huerto.cl",
  "password": "Docente123!"
}
```

Ejemplo de respuesta (estructura):

```json
{
  "token": "TOKEN_GENERADO",
  "tipoToken": "Bearer",
  "expiraEn": "2026-11-19T12:00:00",
  "usuario": {
    "id": 1,
    "nombre": "Docente de prueba",
    "correo": "docente@huerto.cl",
    "tipoCuenta": "DOCENTE"
  }
}
```

## Endpoints principales

Todos los endpoints, excepto `GET /api/health`, `POST /api/auth/registro` y `POST /api/auth/login`, requieren token Bearer. Las imágenes de `/uploads/evidencias/**` también requieren autenticación. Las operaciones de escritura administrativa requieren rol docente.

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| GET | `/api/health` | Público | Estado del backend |
| POST | `/api/auth/registro` | Público | Registrar estudiante |
| POST | `/api/auth/login` | Público | Iniciar sesión |
| GET | `/api/auth/me` | Autenticado | Perfil actual |
| POST | `/api/auth/logout` | Autenticado | Revocar token |
| GET | `/api/usuarios/estudiantes` | Docente | Listar estudiantes para asignar tareas |
| GET/POST | `/api/sectores` | Lectura: ambos; escritura: docente | Listar o crear sectores |
| GET/PUT/DELETE | `/api/sectores/{id}` | Lectura: ambos; escritura: docente | Consultar, editar o eliminar sector |
| GET/POST | `/api/cultivos` | Lectura: ambos; escritura: docente | Listar o crear cultivos |
| GET/PUT/DELETE | `/api/cultivos/{id}` | Lectura: ambos; escritura: docente | Consultar, editar o eliminar cultivo |
| GET | `/api/tareas` | Ambos | Docente ve todas; estudiante solo las asignadas |
| POST | `/api/tareas` | Docente | Crear y asignar tarea |
| PUT/DELETE | `/api/tareas/{id}` | Docente | Actualizar o eliminar tarea |
| POST | `/api/tareas/{id}/evidencias` | Estudiante asignado | Subir foto y comentario (`multipart/form-data`: `foto`, `comentario`) |
| GET | `/api/tareas/{id}/evidencias` | Docente o estudiante asignado | Consultar evidencias |
| POST | `/api/evidencias/{id}/revision` | Docente | Aprobar o rechazar evidencia |
| GET | `/api/lecturas` | Autenticado | Últimas lecturas |
| POST | `/api/lecturas/simuladas` | Docente | Guardar una lectura demostrativa |

### Ejemplo: crear un sector

```http
POST /api/sectores
Authorization: Bearer TU_TOKEN
Content-Type: application/json

{
  "nombre": "Invernadero norte",
  "ubicacion": "Patio principal",
  "descripcion": "Sector de hortalizas"
}
```

### Ejemplo: crear un cultivo

```json
{
  "nombre": "Tomate",
  "tipo": "Hortaliza",
  "descripcion": "Tomate cherry",
  "fechaSiembra": "2026-11-18",
  "fechaCosechaEstimada": "2027-02-18",
  "estado": "SEMBRADO",
  "sectorId": 1
}
```

### Ejemplo: crear una tarea

```json
{
  "titulo": "Regar los tomates",
  "descripcion": "Revisar la humedad y regar si hace falta.",
  "fechaLimite": "2026-11-25",
  "sectorId": 1,
  "cultivoId": 1,
  "estudianteId": 2
}
```

### Ejemplo: revisar evidencia

```json
{
  "aprobada": false,
  "observacion": "La fotografía no permite comprobar la actividad. Adjunta otra imagen."
}
```

## Modelo de datos

- `usuarios`: perfiles, rol y hash de contraseña.
- `sesiones_usuario`: hash de tokens, vencimiento y revocación.
- `sectores`: espacios del huerto.
- `cultivos`: plantaciones asociadas a un sector.
- `tareas`: actividades asociadas a sector, cultivo opcional, estudiante y docente creador.
- `evidencias`: fotografía, comentario y revisión docente.
- `lecturas_huerto`: humedad y temperatura; en esta versión son simuladas.

Las relaciones y restricciones se definen en las entidades JPA del paquete `Duoc.Huerto.entity`.

## Almacenamiento de fotografías

Las imágenes se guardan en `uploads/evidencias/` y la base almacena su ruta relativa. El límite es 5 MB por imagen y se aceptan JPG, PNG y WEBP. La ruta de las imágenes requiere autenticación Bearer; el cliente Android debe enviar el token al cargar la fotografía. En un servidor publicado se recomienda mantener el almacenamiento fuera de la carpeta pública de la aplicación y respaldar los archivos.

## Pruebas

Se incluye un perfil de pruebas H2 (`application-test.properties`) para no depender de MySQL al ejecutar las pruebas automatizadas. Ejecuta:

```bash
./mvnw test
```

En Windows utiliza `mvnw.cmd test`.
