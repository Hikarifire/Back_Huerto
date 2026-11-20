# Huerto Escolar API

API Spring Boot con Java 21 y MySQL 8.4. Integrantes: Giovanni Orellana y Vicente Vilches.

## Ejecutar con XAMPP

1. En el panel de XAMPP, inicia **MySQL**. Si vas a usar phpMyAdmin, inicia también Apache.
2. Abre `http://localhost/phpmyadmin`, selecciona **Importar** e importa `database/huerto_escolar.sql`. El script crea la base `huerto_escolar`.
3. Abre el proyecto en IntelliJ con JDK 21 y selecciona **Use Maven wrapper** en la configuración de Maven. En PowerShell puedes iniciar el servidor con:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

4. Comprueba que `http://localhost:8080/api/health` responde antes de abrir la app Android.

La configuración local predeterminada usa `localhost:3306`, el usuario `root` y una contraseña vacía. Si tu instalación difiere, ajusta `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` en las variables de entorno de IntelliJ o en la terminal. La URL debe apuntar a la base `huerto_escolar`.

Ejecuta `./mvnw test` o `.\mvnw.cmd test` para correr las pruebas.

## Cuentas de demostración

| Rol | Correo | Clave ficticia |
|---|---|---|
| Docente | `docente@huerto.cl` | `Docente123!` |
| Estudiante 1 | `estudiante1@huerto.cl` | `Estudiante123!` |
| Estudiante 2 | `estudiante2@huerto.cl` | `Estudiante123!` |
| Estudiante 3 | `estudiante3@huerto.cl` | `Estudiante123!` |

El registro público crea cuentas de estudiante. El perfil y los permisos se resuelven en Spring Boot. Para desactivar los usuarios y datos ficticios en una base nueva, configura `SEED_DEMO_DATA=false` antes del primer arranque.

## API e integración

Android usa Kotlin, Jetpack Compose, MVVM, StateFlow, Room y Retrofit. MySQL almacena los registros compartidos. Room guarda los borradores por usuario y tarea. Spring Boot expone la API propia y aplica las reglas de autorización. El proyecto incluye un servicio Spring Boot desplegable.

| Componente | Operaciones |
|---|---|
| Sesiones | `POST /api/auth/login`, `POST /api/auth/registro`, `GET /api/auth/me`, `POST /api/auth/logout` |
| Usuarios | `GET /api/usuarios/estudiantes` |
| Sectores, cultivos y tareas | `GET/POST /api/{recurso}`, `GET/PUT/DELETE /api/{recurso}/{id}` |
| Tareas y evidencias | `POST /api/tareas/{id}/iniciar`, `GET/POST /api/tareas/{id}/evidencias`, `POST /api/evidencias/{id}/revision`, `GET /api/evidencias/{id}/foto` |
| Reportes | `GET/POST /api/reportes`, `PATCH /api/reportes/{id}`, `GET /api/reportes/{id}/foto` |
| Sensores simulados | `GET /api/lecturas`, `POST /api/lecturas/simuladas` |
| Open-Meteo | `GET https://api.open-meteo.com/v1/forecast` con coordenadas fijas de San Bernardo, `current=temperature_2m,relative_humidity_2m,precipitation` y `timezone=America/Santiago` |

Las fotos y los reportes están sujetos a los permisos del recurso asociado. Una tarea con evidencias se conserva para mantener sus revisiones. Las bajas de sectores y cultivos requieren que estén libres de referencias.

Cliente Android en https://github.com/Hikarifire/Huerto_escolar.
