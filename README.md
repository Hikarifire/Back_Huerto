# Huerto Escolar — Backend

API REST en Spring Boot para gestionar un huerto escolar. La aplicación Android consume esta API y los datos se guardan en la base `huerto_escolar`.

**Integrantes** · Giovanni Orellana y Vicente Vilches

## Tecnologías

Java 21, Spring Boot, Spring Security, Spring Data JPA y MySQL/MariaDB de XAMPP.

## Ejecutar con XAMPP

1. Inicia **MySQL** y **Apache** desde el panel de XAMPP.
2. Si vas a preparar una base nueva, abre `http://localhost/phpmyadmin/` e importa `database/huerto_escolar.sql`. El script crea la base y sus tablas.
3. Si ya tienes una base de una versión anterior y `usuarios.tipo_cuenta` figura como `ENUM`, ejecuta `database/actualizar_rol_admin.sql` sobre la base existente para habilitar el rol `ADMIN`.
4. Abre el backend en IntelliJ, selecciona **JDK 21** y usa **Maven Wrapper**.
5. Ejecuta `HuertoApplication`. También puedes iniciarlo desde PowerShell en la raíz del proyecto:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

6. Abre `http://localhost:8080/api/health`. Si responde con `"estado": "ok"`, puedes abrir la aplicación Android.

La configuración local usa `localhost:3306`, el usuario `root` y una contraseña vacía. Si tu instalación de XAMPP usa otros valores, configura `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` en las variables de entorno de IntelliJ.

## Roles

| Rol | Permisos |
|---|---|
| `ADMIN` | Gestiona el huerto con los mismos permisos que un docente. Además, puede crear cuentas de docentes y estudiantes. |
| `DOCENTE` | Gestiona sectores, cultivos y tareas. Asigna responsables, revisa evidencias, atiende reportes y registra lecturas simuladas. |
| `ALUMNO` | Consulta sus tareas asignadas, las inicia, envía evidencias y registra reportes. |

Las rutas protegidas usan un token Bearer. Spring Boot valida los permisos de cada cuenta y guarda las contraseñas como hashes BCrypt.

## Cuentas para la demostración local

Al arrancar con `SEED_DEMO_DATA=true`, el backend crea las cuentas que falten. Las cuentas existentes conservan su contraseña.

| Rol | Correo | Clave de prueba |
|---|---|---|
| Administrador | `admin@huerto.cl` | `AdminDemo123!` |
| Docente | `docente@huerto.cl` | `Docente123!` |
| Estudiante 1 | `estudiante1@huerto.cl` | `Estudiante123!` |
| Estudiante 2 | `estudiante2@huerto.cl` | `Estudiante123!` |
| Estudiante 3 | `estudiante3@huerto.cl` | `Estudiante123!` |

Estas credenciales son para la demostración local. Para crear el administrador inicial con una contraseña propia, define `HUERTO_ADMIN_PASSWORD` antes de que se cree la cuenta.

## Endpoints principales

| Función | Endpoints |
|---|---|
| Estado de la API | `GET /api/health` |
| Sesión y registro público | `POST /api/auth/login`, `POST /api/auth/registro`, `GET /api/auth/me`, `POST /api/auth/logout` |
| Alta de cuentas | `POST /api/usuarios/cuentas/DOCENTE`, `POST /api/usuarios/cuentas/ALUMNO` |
| Estudiantes disponibles | `GET /api/usuarios/estudiantes` |
| Sectores, cultivos y tareas | Listado y alta en `/api/sectores`, `/api/cultivos` y `/api/tareas`. Consulta, edición y eliminación por `/{id}` según las reglas del recurso. |
| Ejecución y evidencia de tareas | `POST /api/tareas/{id}/iniciar`, `GET/POST /api/tareas/{id}/evidencias`, `POST /api/evidencias/{id}/revision`, `GET /api/evidencias/{id}/foto` |
| Reportes | `GET/POST /api/reportes`, `PATCH /api/reportes/{id}`, `GET /api/reportes/{id}/foto` |
| Lecturas simuladas | `GET /api/lecturas`, `POST /api/lecturas/simuladas` |

El alta de cuentas por `/api/usuarios/cuentas` requiere rol `ADMIN`. El formulario público de registro crea cuentas con rol `ALUMNO`.

## Pruebas

```powershell
.\mvnw.cmd test
```

Las pruebas automatizadas usan H2. `PermisosCuentasTest` comprueba que el administrador pueda crear cuentas, gestionar tareas y revisar evidencias, y que un docente o estudiante reciba un rechazo al intentar dar de alta docentes.

## Repositorio Android

[Huerto_escolar](https://github.com/Hikarifire/Huerto_escolar)
