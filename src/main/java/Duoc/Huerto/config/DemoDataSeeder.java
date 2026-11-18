package Duoc.Huerto.config;

import Duoc.Huerto.entity.*;
import Duoc.Huerto.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true", matchIfMissing = true)
public class DemoDataSeeder implements CommandLineRunner {
    private final UsuarioRepository usuarios;
    private final SectorRepository sectores;
    private final CultivoRepository cultivos;
    private final TareaRepository tareas;
    private final LecturaHuertoRepository lecturas;
    private final PasswordEncoder encoder;

    public DemoDataSeeder(UsuarioRepository usuarios, SectorRepository sectores, CultivoRepository cultivos,
                          TareaRepository tareas, LecturaHuertoRepository lecturas, PasswordEncoder encoder) {
        this.usuarios = usuarios; this.sectores = sectores; this.cultivos = cultivos; this.tareas = tareas; this.lecturas = lecturas; this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Usuario docente = crearUsuarioSiNoExiste("Docente de prueba", "docente@huerto.cl", "Docente123!", TipoCuenta.DOCENTE);
        Usuario alumno1 = crearUsuarioSiNoExiste("Estudiante Uno", "estudiante1@huerto.cl", "Estudiante123!", TipoCuenta.ALUMNO);
        crearUsuarioSiNoExiste("Estudiante Dos", "estudiante2@huerto.cl", "Estudiante123!", TipoCuenta.ALUMNO);
        crearUsuarioSiNoExiste("Estudiante Tres", "estudiante3@huerto.cl", "Estudiante123!", TipoCuenta.ALUMNO);

        Sector sector = sectores.findAll().stream().findFirst().orElseGet(() -> sectores.save(new Sector("Invernadero", "Patio escolar", "Sector de demostración del huerto.")));
        Cultivo cultivo = cultivos.findAll().stream().findFirst().orElseGet(() -> {
            Cultivo c = new Cultivo(); c.setNombre("Lechuga"); c.setTipo("Hortaliza"); c.setDescripcion("Cultivo de demostración.");
            c.setFechaSiembra(LocalDate.now()); c.setFechaCosechaEstimada(LocalDate.now().plusDays(60)); c.setEstado(EstadoCultivo.SEMBRADO); c.setSector(sector);
            return cultivos.save(c);
        });
        if (tareas.count() == 0) {
            Tarea tarea = new Tarea(); tarea.setTitulo("Revisar el crecimiento de la lechuga");
            tarea.setDescripcion("Observar las hojas, registrar cambios y adjuntar una fotografía.");
            tarea.setFechaLimite(LocalDate.now().plusDays(7)); tarea.setEstado(EstadoTarea.PENDIENTE);
            tarea.setSector(sector); tarea.setCultivo(cultivo); tarea.setEstudiante(alumno1); tarea.setCreadoPor(docente); tareas.save(tarea);
        }
        if (lecturas.count() == 0) {
            LecturaHuerto lectura = new LecturaHuerto(); lectura.setHumedad(new BigDecimal("62.00")); lectura.setTemperatura(new BigDecimal("21.50"));
            lectura.setSector(sector); lectura.setSimulada(true); lectura.setRegistradaPor(docente); lecturas.save(lectura);
        }
    }

    private Usuario crearUsuarioSiNoExiste(String nombre, String correo, String password, TipoCuenta tipo) {
        return usuarios.findByCorreoIgnoreCase(correo).orElseGet(() -> usuarios.save(new Usuario(nombre, correo, encoder.encode(password), tipo)));
    }
}
