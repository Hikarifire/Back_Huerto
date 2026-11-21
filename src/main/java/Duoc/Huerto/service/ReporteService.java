package Duoc.Huerto.service;

import Duoc.Huerto.entity.*;
import Duoc.Huerto.exception.RecursoNoEncontradoException;
import Duoc.Huerto.exception.ReglaNegocioException;
import Duoc.Huerto.repository.*;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReporteService {
    public record Resumen(Long id, String descripcion, Reporte.Estado estado, Long sectorId, String sectorNombre,
                          Long cultivoId, String cultivoNombre, String autor, String seguimiento, String atendidoPor,
                          String fotoUrl, LocalDateTime creadoEn, LocalDateTime actualizadoEn) {}

    private final ReporteRepository reportes;
    private final SectorRepository sectores;
    private final CultivoRepository cultivos;
    private final UsuarioRepository usuarios;
    private final ArchivoStorageService storage;

    public ReporteService(ReporteRepository reportes, SectorRepository sectores, CultivoRepository cultivos,
                          UsuarioRepository usuarios, ArchivoStorageService storage) {
        this.reportes = reportes;
        this.sectores = sectores;
        this.cultivos = cultivos;
        this.usuarios = usuarios;
        this.storage = storage;
    }

    @Transactional(readOnly = true)
    public List<Resumen> listar(String correo) {
        Usuario usuario = usuario(correo);
        List<Reporte> visibles = usuario.getTipoCuenta().puedeGestionarHuerto()
            ? reportes.findAllByOrderByCreadoEnDesc()
            : reportes.findAllByAutorIdOrderByCreadoEnDesc(usuario.getId());
        return visibles.stream().map(this::mapear).toList();
    }

    @Transactional
    public Resumen crear(String correo, Long sectorId, Long cultivoId, String descripcion, MultipartFile foto) {
        if (descripcion == null || descripcion.isBlank() || descripcion.trim().length() > 1000) {
            throw new ReglaNegocioException("Describe el problema en 1 a 1000 caracteres.");
        }
        Reporte reporte = new Reporte();
        reporte.autor = usuario(correo);
        reporte.sector = sectores.findById(sectorId).orElseThrow(() -> new RecursoNoEncontradoException("Sector no encontrado."));
        if (cultivoId != null) {
            reporte.cultivo = cultivos.findById(cultivoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cultivo no encontrado."));
            if (!reporte.cultivo.getSector().getId().equals(sectorId)) {
                throw new ReglaNegocioException("El cultivo no está en el sector indicado.");
            }
        }
        reporte.descripcion = descripcion.trim();
        if (foto != null && !foto.isEmpty()) reporte.foto = storage.guardarImagen(foto);
        return mapear(reportes.save(reporte));
    }

    @Transactional
    public Resumen actualizar(Long id, String correo, Reporte.Estado estado, String seguimiento) {
        Usuario docente = usuario(correo);
        if (!docente.getTipoCuenta().puedeGestionarHuerto()) throw new AccessDeniedException("Solo docentes o administradores.");
        Reporte reporte = buscar(id);
        reporte.estado = estado;
        reporte.seguimiento = seguimiento.trim();
        reporte.atendidoPor = docente;
        reporte.actualizadoEn = LocalDateTime.now();
        return mapear(reporte);
    }

    @Transactional(readOnly = true)
    public Resource foto(Long id, String correo) {
        Reporte reporte = buscar(id);
        Usuario usuario = usuario(correo);
        if (reporte.foto == null || (!usuario.getTipoCuenta().puedeGestionarHuerto()
            && !reporte.autor.getId().equals(usuario.getId()))) {
            throw new RecursoNoEncontradoException("Fotografía no encontrada.");
        }
        return storage.abrirImagen(reporte.foto);
    }

    private Reporte buscar(Long id) {
        return reportes.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Reporte no encontrado."));
    }

    private Usuario usuario(String correo) {
        return usuarios.findByCorreoIgnoreCase(correo).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));
    }

    private Resumen mapear(Reporte r) {
        return new Resumen(r.id, r.descripcion, r.estado, r.sector.getId(), r.sector.getNombre(),
            r.cultivo == null ? null : r.cultivo.getId(), r.cultivo == null ? null : r.cultivo.getNombre(),
            r.autor.getNombre(), r.seguimiento, r.atendidoPor == null ? null : r.atendidoPor.getNombre(),
            r.foto == null ? null : "/api/reportes/" + r.id + "/foto", r.creadoEn, r.actualizadoEn);
    }
}
