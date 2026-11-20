package Duoc.Huerto.service;

import Duoc.Huerto.dto.TareaRequest;
import Duoc.Huerto.dto.TareaResponse;
import Duoc.Huerto.entity.*;
import Duoc.Huerto.exception.ReglaNegocioException;
import Duoc.Huerto.exception.RecursoNoEncontradoException;
import Duoc.Huerto.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TareaService {
    private final TareaRepository tareas;
    private final UsuarioRepository usuarios;
    private final SectorRepository sectores;
    private final CultivoRepository cultivos;
    private final EvidenciaRepository evidencias;
    public TareaService(TareaRepository tareas, UsuarioRepository usuarios, SectorRepository sectores,
                        CultivoRepository cultivos, EvidenciaRepository evidencias) {
        this.tareas = tareas; this.usuarios = usuarios; this.sectores = sectores; this.cultivos = cultivos; this.evidencias = evidencias;
    }

    @Transactional(readOnly = true)
    public List<TareaResponse> listarPara(String correo) {
        Usuario usuario = buscarUsuario(correo);
        List<Tarea> lista = usuario.getTipoCuenta() == TipoCuenta.DOCENTE
            ? tareas.findAllByOrderByFechaLimiteAsc()
            : tareas.findAllByEstudianteIdOrderByFechaLimiteAsc(usuario.getId());
        return lista.stream().map(this::mapear).toList();
    }

    @Transactional(readOnly = true)
    public TareaResponse obtener(Long id, String correo) {
        Tarea tarea = buscar(id); Usuario usuario = buscarUsuario(correo); validarAcceso(tarea, usuario);
        return mapear(tarea);
    }

    @Transactional
    public TareaResponse crear(TareaRequest request, String correoDocente) {
        Usuario docente = buscarUsuario(correoDocente);
        if (docente.getTipoCuenta() != TipoCuenta.DOCENTE) throw new ReglaNegocioException("Solo un docente puede crear tareas.");
        Tarea tarea = new Tarea(); tarea.setCreadoPor(docente); aplicar(tarea, request);
        return mapear(tareas.save(tarea));
    }

    @Transactional
    public TareaResponse actualizar(Long id, TareaRequest request) {
        Tarea tarea = buscarParaActualizar(id);
        if (tarea.getEstado() == EstadoTarea.EN_REVISION || tarea.getEstado() == EstadoTarea.APROBADA) {
            throw new ReglaNegocioException("La tarea está en revisión o ya fue validada.");
        }
        if (!tarea.getEstudiante().getId().equals(request.estudianteId())) {
            if (evidencias.existsByTareaId(id)) throw new ReglaNegocioException("La tarea ya tiene historial de entregas.");
            tarea.setEstado(EstadoTarea.PENDIENTE);
        }
        aplicar(tarea, request);
        return mapear(tareas.save(tarea));
    }

    @Transactional
    public void eliminar(Long id) {
        Tarea tarea = buscarParaActualizar(id);
        if (evidencias.existsByTareaId(id)) throw new ReglaNegocioException("La tarea tiene evidencias que deben conservarse.");
        tareas.delete(tarea);
    }

    @Transactional
    public TareaResponse iniciar(Long id, String correo) {
        Tarea tarea = buscarParaActualizar(id);
        Usuario alumno = buscarUsuario(correo);
        if (alumno.getTipoCuenta() != TipoCuenta.ALUMNO) throw new AccessDeniedException("Solo estudiantes.");
        validarAcceso(tarea, alumno);
        if (tarea.getEstado() != EstadoTarea.PENDIENTE && tarea.getEstado() != EstadoTarea.RECHAZADA) {
            throw new ReglaNegocioException("La tarea ya está iniciada o fue entregada.");
        }
        tarea.setEstado(EstadoTarea.EN_DESARROLLO);
        return mapear(tarea);
    }

    private Tarea buscarParaActualizar(Long id) {
        return tareas.buscarParaActualizar(id).orElseThrow(() -> new RecursoNoEncontradoException("Tarea no encontrada."));
    }

    private void aplicar(Tarea tarea, TareaRequest request) {
        Sector sector = sectores.findById(request.sectorId()).orElseThrow(() -> new RecursoNoEncontradoException("Sector no encontrado."));
        Usuario estudiante = usuarios.findById(request.estudianteId()).orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado."));
        if (estudiante.getTipoCuenta() != TipoCuenta.ALUMNO || !estudiante.isActivo()) throw new ReglaNegocioException("La tarea debe asignarse a un estudiante activo.");
        Cultivo cultivo = null;
        if (request.cultivoId() != null) {
            cultivo = cultivos.findById(request.cultivoId()).orElseThrow(() -> new RecursoNoEncontradoException("Cultivo no encontrado."));
            if (!cultivo.getSector().getId().equals(sector.getId())) throw new ReglaNegocioException("El cultivo seleccionado no pertenece al sector indicado.");
        }
        tarea.setTitulo(request.titulo().trim()); tarea.setDescripcion(request.descripcion().trim());
        tarea.setTipo(request.tipo() == null ? TipoTarea.OBSERVACION : request.tipo());
        tarea.setFechaLimite(request.fechaLimite()); tarea.setSector(sector); tarea.setCultivo(cultivo); tarea.setEstudiante(estudiante);
    }

    private void validarAcceso(Tarea tarea, Usuario usuario) {
        if (usuario.getTipoCuenta() == TipoCuenta.ALUMNO && !tarea.getEstudiante().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("No tienes permiso para consultar esta tarea.");
        }
    }
    private Usuario buscarUsuario(String correo) { return usuarios.findByCorreoIgnoreCase(correo).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado.")); }
    private Tarea buscar(Long id) { return tareas.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Tarea no encontrada.")); }
    private TareaResponse mapear(Tarea t) {
        return new TareaResponse(t.getId(), t.getTitulo(), t.getDescripcion(), t.getFechaLimite(), t.getEstado(),
            t.getSector().getId(), t.getSector().getNombre(), t.getCultivo() == null ? null : t.getCultivo().getId(),
            t.getCultivo() == null ? null : t.getCultivo().getNombre(), t.getEstudiante().getId(),
            t.getEstudiante().getNombre(), t.getCreadoPor().getId(), t.getFechaCreacion(), t.getTipo());
    }
}
