package Duoc.Huerto.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sectores")
public class Sector {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 100) private String nombre;
    @Column(length = 150) private String ubicacion;
    @Column(length = 500) private String descripcion;
    @Column(nullable = false) private boolean activo = true;
    @Column(name = "fecha_creacion", nullable = false) private LocalDateTime fechaCreacion;
    @PrePersist void antesDeGuardar() { if (fechaCreacion == null) fechaCreacion = LocalDateTime.now(); }
    public Sector() {}
    public Sector(String nombre, String ubicacion, String descripcion) { this.nombre = nombre; this.ubicacion = ubicacion; this.descripcion = descripcion; }
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
