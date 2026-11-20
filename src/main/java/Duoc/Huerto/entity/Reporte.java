package Duoc.Huerto.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes_problema")
public class Reporte {
    public enum Estado { ABIERTO, EN_ATENCION, RESUELTO }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @Column(nullable = false, length = 1000) public String descripcion;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) public Estado estado = Estado.ABIERTO;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "sector_id") public Sector sector;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "cultivo_id") public Cultivo cultivo;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "autor_id") public Usuario autor;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "atendido_por_id") public Usuario atendidoPor;
    @Column(length = 1000) public String seguimiento;
    @Column(length = 500) public String foto;
    @Column(nullable = false) public LocalDateTime creadoEn;
    public LocalDateTime actualizadoEn;

    @PrePersist void registrarFecha() { creadoEn = LocalDateTime.now(); }
}
