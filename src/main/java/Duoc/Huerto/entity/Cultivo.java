package Duoc.Huerto.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cultivos")
public class Cultivo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 100) private String nombre;
    @Column(length = 80) private String tipo;
    @Column(length = 500) private String descripcion;
    @Column(name = "fecha_siembra") private LocalDate fechaSiembra;
    @Column(name = "fecha_cosecha_estimada") private LocalDate fechaCosechaEstimada;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private EstadoCultivo estado = EstadoCultivo.SEMBRADO;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "sector_id", nullable = false) private Sector sector;
    public Cultivo() {}
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDate getFechaSiembra() { return fechaSiembra; }
    public void setFechaSiembra(LocalDate fechaSiembra) { this.fechaSiembra = fechaSiembra; }
    public LocalDate getFechaCosechaEstimada() { return fechaCosechaEstimada; }
    public void setFechaCosechaEstimada(LocalDate fechaCosechaEstimada) { this.fechaCosechaEstimada = fechaCosechaEstimada; }
    public EstadoCultivo getEstado() { return estado; }
    public void setEstado(EstadoCultivo estado) { this.estado = estado; }
    public Sector getSector() { return sector; }
    public void setSector(Sector sector) { this.sector = sector; }
}
