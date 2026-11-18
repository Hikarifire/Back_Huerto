package Duoc.Huerto.service;

import Duoc.Huerto.dto.TareaRequest;
import Duoc.Huerto.dto.TareaResponse;
import Duoc.Huerto.entity.*;
import Duoc.Huerto.exception.ReglaNegocioException;
import Duoc.Huerto.exception.RecursoNoEncontradoException;
import Duoc.Huerto.repository.*;
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
        Tarea tarea = buscar(id);
        if (!tarea.getEstudiante().getId().equals(request.estudianteId())) {
            evidencias.deleteAllByTareaId(id);
            tarea.setEstado(EstadoTarea.PENDIENTE);
        }
        aplicar(tarea, request);
        return mapear(tareas.save(tarea));
    }

    @Transactional
    public void eliminar(Long id) {
        Tarea tarea = buscar(id);
        evidencias.deleteAllByTareaId(id);
        tareas.delete(tarea);
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
        tarea.setFechaLimite(request.fechaLimite()); tarea.setSector(sector); tarea.setCultivo(cultivo); tarea.setEstudiante(estudiante);
    }

    private void validarAcceso(Tarea tarea, Usuario usuario) {
        if (usuario.getTipoCuenta() == TipoCuenta.ALUMNO && !tarea.getEstudiante().getId().equals(usuario.getId())) {
            throw new ReglaNegocioException("No tienes permiso para consultar esta tarea.");
        }
    }
    private Usuario buscarUsuario(String correo) { return usuarios.findByCorreoIgnoreCase(correo).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado.")); }
    private Tarea buscar(Long id) { return tareas.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Tarea no encontrada.")); }
    private TareaResponse mapear(Tarea t) {
        return new TareaResponse(t.getId(), t.getTitulo(), t.getDescripcion(), t.getFechaLimite(), t.getEstado(),
            t.getSector().getId(), t.getSector().getNombre(), t.getCultivo() == null ? null : t.getCultivo().getId(),
            t.getCultivo() == null ? null : t.getCultivo().getNombre(), t.getEstudiante().getId(),
            t.getEstudiante().getNombre(), t.getCreadoPor().getId(), t.getFechaCreacion());
    }
}
