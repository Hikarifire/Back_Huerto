package Duoc.Huerto.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tareas")
public class Tarea {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 140) private String titulo;
    @Column(nullable = false, length = 1000) private String descripcion;
    @Column(name = "fecha_limite", nullable = false) private LocalDate fechaLimite;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private EstadoTarea estado = EstadoTarea.PENDIENTE;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "sector_id", nullable = false) private Sector sector;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "cultivo_id") private Cultivo cultivo;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "estudiante_id", nullable = false) private Usuario estudiante;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "creado_por_id", nullable = false) private Usuario creadoPor;
    @Column(name = "fecha_creacion", nullable = false) private LocalDateTime fechaCreacion;
    @Column(name = "fecha_actualizacion", nullable = false) private LocalDateTime fechaActualizacion;
    @PrePersist void antesDeGuardar() { LocalDateTime ahora = LocalDateTime.now(); if (fechaCreacion == null) fechaCreacion = ahora; fechaActualizacion = ahora; }
    @PreUpdate void antesDeActualizar() { fechaActualizacion = LocalDateTime.now(); }
    public Tarea() {}
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDate getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDate fechaLimite) { this.fechaLimite = fechaLimite; }
    public EstadoTarea getEstado() { return estado; }
    public void setEstado(EstadoTarea estado) { this.estado = estado; }
    public Sector getSector() { return sector; }
    public void setSector(Sector sector) { this.sector = sector; }
    public Cultivo getCultivo() { return cultivo; }
    public void setCultivo(Cultivo cultivo) { this.cultivo = cultivo; }
    public Usuario getEstudiante() { return estudiante; }
    public void setEstudiante(Usuario estudiante) { this.estudiante = estudiante; }
    public Usuario getCreadoPor() { return creadoPor; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setCreadoPor(Usuario creadoPor) { this.creadoPor = creadoPor; }
}
