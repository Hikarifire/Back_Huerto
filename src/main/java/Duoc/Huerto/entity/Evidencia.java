package Duoc.Huerto.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evidencias")
public class Evidencia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "tarea_id", nullable = false) private Tarea tarea;
    @Column(name = "foto_url", nullable = false, length = 500) private String fotoUrl;
    @Column(nullable = false, length = 1000) private String comentario;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private EstadoEvidencia estado = EstadoEvidencia.EN_REVISION;
    @Column(name = "observacion_revision", length = 1000) private String observacionRevision;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "revisado_por_id") private Usuario revisadoPor;
    @Column(name = "fecha_registro", nullable = false) private LocalDateTime fechaRegistro;
    @Column(name = "fecha_revision") private LocalDateTime fechaRevision;
    @PrePersist void antesDeGuardar() { if (fechaRegistro == null) fechaRegistro = LocalDateTime.now(); }
    public Evidencia() {}
    public Long getId() { return id; }
    public Tarea getTarea() { return tarea; }
    public void setTarea(Tarea tarea) { this.tarea = tarea; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public EstadoEvidencia getEstado() { return estado; }
    public void setEstado(EstadoEvidencia estado) { this.estado = estado; }
    public String getObservacionRevision() { return observacionRevision; }
    public void setObservacionRevision(String observacionRevision) { this.observacionRevision = observacionRevision; }
    public Usuario getRevisadoPor() { return revisadoPor; }
    public void setRevisadoPor(Usuario revisadoPor) { this.revisadoPor = revisadoPor; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public LocalDateTime getFechaRevision() { return fechaRevision; }
    public void setFechaRevision(LocalDateTime fechaRevision) { this.fechaRevision = fechaRevision; }
}
