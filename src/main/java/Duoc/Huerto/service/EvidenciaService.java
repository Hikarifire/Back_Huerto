package Duoc.Huerto.service;

import Duoc.Huerto.dto.EvidenciaResponse;
import Duoc.Huerto.entity.*;
import Duoc.Huerto.exception.ReglaNegocioException;
import Duoc.Huerto.exception.RecursoNoEncontradoException;
import Duoc.Huerto.repository.EvidenciaRepository;
import Duoc.Huerto.repository.TareaRepository;
import Duoc.Huerto.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EvidenciaService {
    private final EvidenciaRepository evidencias;
    private final TareaRepository tareas;
    private final UsuarioRepository usuarios;
    private final ArchivoStorageService storage;
    public EvidenciaService(EvidenciaRepository evidencias, TareaRepository tareas, UsuarioRepository usuarios, ArchivoStorageService storage) {
        this.evidencias = evidencias; this.tareas = tareas; this.usuarios = usuarios; this.storage = storage;
    }

    @Transactional
    public EvidenciaResponse entregar(Long tareaId, String correo, MultipartFile foto, String comentario) {
        Usuario alumno = buscarUsuario(correo);
        if (alumno.getTipoCuenta() != TipoCuenta.ALUMNO) throw new ReglaNegocioException("Solo los estudiantes pueden entregar evidencias.");
        Tarea tarea = tareas.findById(tareaId).orElseThrow(() -> new RecursoNoEncontradoException("Tarea no encontrada."));
        if (!tarea.getEstudiante().getId().equals(alumno.getId())) throw new ReglaNegocioException("Esta tarea no está asignada a tu cuenta.");
        if (comentario == null || comentario.isBlank()) throw new ReglaNegocioException("Debes escribir un comentario sobre la actividad.");
        if (tarea.getEstado() == EstadoTarea.EN_REVISION || tarea.getEstado() == EstadoTarea.APROBADA) throw new ReglaNegocioException("La tarea ya fue enviada o aprobada y no admite otra entrega.");
        String fotoUrl = storage.guardarImagen(foto);
        Evidencia evidencia = new Evidencia(); evidencia.setTarea(tarea); evidencia.setFotoUrl(fotoUrl);
        evidencia.setComentario(comentario.trim()); evidencia.setEstado(EstadoEvidencia.EN_REVISION);
        tarea.setEstado(EstadoTarea.EN_REVISION);
        tareas.save(tarea);
        return mapear(evidencias.save(evidencia));
    }

    @Transactional(readOnly = true)
    public List<EvidenciaResponse> listarDeTarea(Long tareaId, String correo) {
        Tarea tarea = tareas.findById(tareaId).orElseThrow(() -> new RecursoNoEncontradoException("Tarea no encontrada."));
        Usuario usuario = buscarUsuario(correo);
        if (usuario.getTipoCuenta() == TipoCuenta.ALUMNO && !tarea.getEstudiante().getId().equals(usuario.getId())) throw new ReglaNegocioException("No tienes permiso para ver estas evidencias.");
        return evidencias.findAllByTareaIdOrderByFechaRegistroDesc(tareaId).stream().map(this::mapear).toList();
    }

    @Transactional
    public EvidenciaResponse revisar(Long evidenciaId, String correoDocente, boolean aprobada, String observacion) {
        Usuario docente = buscarUsuario(correoDocente);
        if (docente.getTipoCuenta() != TipoCuenta.DOCENTE) throw new ReglaNegocioException("Solo un docente puede revisar evidencias.");
        Evidencia evidencia = evidencias.findById(evidenciaId).orElseThrow(() -> new RecursoNoEncontradoException("Evidencia no encontrada."));
        if (evidencia.getEstado() != EstadoEvidencia.EN_REVISION) throw new ReglaNegocioException("Esta evidencia ya fue revisada.");
        EstadoEvidencia nuevoEstado = aprobada ? EstadoEvidencia.APROBADA : EstadoEvidencia.RECHAZADA;
        evidencia.setEstado(nuevoEstado); evidencia.setObservacionRevision(observacion); evidencia.setRevisadoPor(docente); evidencia.setFechaRevision(LocalDateTime.now());
        evidencia.getTarea().setEstado(aprobada ? EstadoTarea.APROBADA : EstadoTarea.RECHAZADA);
        return mapear(evidencias.save(evidencia));
    }

    private Usuario buscarUsuario(String correo) { return usuarios.findByCorreoIgnoreCase(correo).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado.")); }
    private EvidenciaResponse mapear(Evidencia e) { return new EvidenciaResponse(e.getId(), e.getTarea().getId(), e.getFotoUrl(), e.getComentario(), e.getEstado(), e.getObservacionRevision(), e.getRevisadoPor() == null ? null : e.getRevisadoPor().getNombre(), e.getFechaRegistro(), e.getFechaRevision()); }
}
