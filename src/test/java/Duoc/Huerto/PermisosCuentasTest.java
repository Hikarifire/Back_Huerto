package Duoc.Huerto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.datasource.url=jdbc:h2:mem:huerto_roles;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
    "app.seed-demo-data=true", "HUERTO_ADMIN_PASSWORD=ClaveAdminSoloTest2026!"
})
class PermisosCuentasTest {
    @LocalServerPort
    private int puerto;

    private final HttpClient cliente = HttpClient.newHttpClient();
    private final JsonMapper json = new JsonMapper();

    @Test
    void soloAdminCreaCuentasDePersonalYAdminConservaFuncionesDocentes() throws Exception {
        String admin = iniciarSesion("admin@huerto.cl", "ClaveAdminSoloTest2026!");
        String docente = iniciarSesion("docente@huerto.cl", "Docente123!");
        String estudiante = iniciarSesion("estudiante1@huerto.cl", "Estudiante123!");

        String alta = """
            {"nombre":"Docente nuevo","correo":"nuevo-docente@huerto.cl","password":"ClaveDocenteTest2026!"}
            """;
        llamar("POST", "/api/usuarios/cuentas/DOCENTE", null, alta, 401);
        llamar("POST", "/api/usuarios/cuentas/DOCENTE", docente, alta, 403);
        llamar("POST", "/api/usuarios/cuentas/DOCENTE", estudiante, alta, 403);
        llamar("POST", "/api/usuarios/cuentas/ALUMNO", docente, alta, 403);
        llamar("POST", "/api/usuarios/cuentas/ADMIN", admin, alta, 400);
        JsonNode nuevoDocente = llamar("POST", "/api/usuarios/cuentas/DOCENTE", admin, alta, 201);
        assertEquals("DOCENTE", nuevoDocente.path("tipoCuenta").asText());
        llamar("POST", "/api/usuarios/cuentas/DOCENTE", admin, alta, 409);
        String docenteNuevo = iniciarSesion("nuevo-docente@huerto.cl", "ClaveDocenteTest2026!");
        llamar("GET", "/api/usuarios/estudiantes", docenteNuevo, null, 200);

        JsonNode alumnoNuevo = llamar("POST", "/api/usuarios/cuentas/ALUMNO", admin, """
            {"nombre":"Alumno nuevo","correo":"nuevo-alumno@huerto.cl","password":"ClaveAlumnoTest2026!"}
            """, 201);
        assertEquals("ALUMNO", alumnoNuevo.path("tipoCuenta").asText());
        String alumnoNuevoToken = iniciarSesion("nuevo-alumno@huerto.cl", "ClaveAlumnoTest2026!");
        long alumnoId = alumnoNuevo.path("id").asLong();

        JsonNode altaPublica = llamar("POST", "/api/auth/registro", null, """
            {"nombre":"Alumno público","correo":"alumno-publico@huerto.cl","password":"ClavePublicaTest2026!"}
            """, 201);
        assertEquals("ALUMNO", altaPublica.path("usuario").path("tipoCuenta").asText());

        JsonNode alumnos = llamar("GET", "/api/usuarios/estudiantes", admin, null, 200);
        assertTrue(alumnos.size() >= 4);
        JsonNode sector = llamar("POST", "/api/sectores", admin, """
            {"nombre":"Sector del administrador","ubicacion":"Patio"}
            """, 201);
        long sectorId = sector.path("id").asLong();
        JsonNode tarea = llamar("POST", "/api/tareas", admin, """
            {"titulo":"Revisar semillero","descripcion":"Contar plantas","fechaLimite":"2030-01-15",
             "sectorId":%d,"estudianteId":%d,"tipo":"OBSERVACION"}
            """.formatted(sectorId, alumnoId), 201);
        long tareaId = tarea.path("id").asLong();
        JsonNode tareasGlobales = llamar("GET", "/api/tareas", admin, null, 200);
        assertTrue(tareasGlobales.size() >= 2);
        llamar("GET", "/api/tareas/" + tareaId + "/evidencias", admin, null, 200);
        llamar("POST", "/api/tareas/" + tareaId + "/iniciar", alumnoNuevoToken, null, 200);
        byte[] png = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+aR1sAAAAASUVORK5CYII=");
        JsonNode evidencia = enviarFoto("/api/tareas/" + tareaId + "/evidencias", alumnoNuevoToken,
            Map.of("comentario", "Semillero revisado"), png);
        String evidenciaId = evidencia.path("id").asText();
        verificarFoto("/api/evidencias/" + evidenciaId + "/foto", admin, png);
        JsonNode revision = llamar("POST", "/api/evidencias/" + evidenciaId + "/revision", admin, """
            {"aprobada":true,"observacion":"Revisión del administrador"}
            """, 200);
        assertEquals("APROBADA", revision.path("estado").asText());

        JsonNode reporte = enviarFoto("/api/reportes", alumnoNuevoToken,
            Map.of("sectorId", Long.toString(sectorId), "descripcion", "Falta tierra"), png);
        String reporteId = reporte.path("id").asText();
        assertTrue(llamar("GET", "/api/reportes", admin, null, 200).size() >= 1);
        verificarFoto("/api/reportes/" + reporteId + "/foto", admin, png);
        JsonNode seguimiento = llamar("PATCH", "/api/reportes/" + reporteId, admin, """
            {"estado":"RESUELTO","seguimiento":"Se agregó tierra"}
            """, 200);
        assertEquals("RESUELTO", seguimiento.path("estado").asText());
        llamar("POST", "/api/lecturas/simuladas", admin, """
            {"humedad":52,"temperatura":21,"humedadAmbiental":49}
            """, 201);
    }

    private JsonNode enviarFoto(String ruta, String token, Map<String, String> campos, byte[] png)
            throws IOException, InterruptedException {
        String borde = "HuertoEnvioPrueba";
        var cuerpo = new ByteArrayOutputStream();
        for (var campo : campos.entrySet()) {
            cuerpo.writeBytes(("--" + borde + "\r\nContent-Disposition: form-data; name=\"" + campo.getKey()
                + "\"\r\nContent-Type: text/plain; charset=UTF-8\r\n\r\n" + campo.getValue() + "\r\n")
                .getBytes(StandardCharsets.UTF_8));
        }
        cuerpo.writeBytes(("--" + borde + "\r\nContent-Disposition: form-data; name=\"foto\"; filename=\"evidencia.png\"" +
            "\r\nContent-Type: image/png\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        cuerpo.writeBytes(png);
        cuerpo.writeBytes(("\r\n--" + borde + "--\r\n").getBytes(StandardCharsets.UTF_8));
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + puerto + ruta))
            .header("Authorization", "Bearer " + token)
            .header("Content-Type", "multipart/form-data; boundary=" + borde)
            .POST(HttpRequest.BodyPublishers.ofByteArray(cuerpo.toByteArray())).build();
        var respuesta = cliente.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, respuesta.statusCode(), ruta);
        return json.readTree(respuesta.body());
    }

    private void verificarFoto(String ruta, String token, byte[] png) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + puerto + ruta))
            .header("Authorization", "Bearer " + token)
            .GET().build();
        var respuesta = cliente.send(request, HttpResponse.BodyHandlers.ofByteArray());
        assertEquals(200, respuesta.statusCode(), ruta);
        assertArrayEquals(png, respuesta.body());
    }

    private String iniciarSesion(String correo, String clave) throws Exception {
        JsonNode respuesta = llamar("POST", "/api/auth/login", null,
            """
            {"correo":"%s","password":"%s"}
            """.formatted(correo, clave), 200);
        return respuesta.path("token").asText();
    }

    private JsonNode llamar(String metodo, String ruta, String token, String cuerpo, int codigo)
            throws IOException, InterruptedException {
        var builder = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + puerto + ruta));
        if (token != null) builder.header("Authorization", "Bearer " + token);
        if (cuerpo != null) builder.header("Content-Type", "application/json");
        var request = builder.method(metodo, cuerpo == null ? HttpRequest.BodyPublishers.noBody()
            : HttpRequest.BodyPublishers.ofString(cuerpo)).build();
        var respuesta = cliente.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(codigo, respuesta.statusCode(), metodo + " " + ruta);
        return respuesta.body().isBlank() ? null : json.readTree(respuesta.body());
    }
}
