package Duoc.Huerto.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lecturas_huerto")
public class LecturaHuerto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, precision = 5, scale = 2) private BigDecimal humedad;
    @Column(nullable = false, precision = 5, scale = 2) private BigDecimal temperatura;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "sector_id") private Sector sector;
    @Column(nullable = false) private boolean simulada = true;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "registrada_por_id") private Usuario registradaPor;
    @Column(name = "fecha_registro", nullable = false) private LocalDateTime fechaRegistro;
    @PrePersist void antesDeGuardar() { if (fechaRegistro == null) fechaRegistro = LocalDateTime.now(); }
    public LecturaHuerto() {}
    public Long getId() { return id; }
    public BigDecimal getHumedad() { return humedad; }
    public void setHumedad(BigDecimal humedad) { this.humedad = humedad; }
    public BigDecimal getTemperatura() { return temperatura; }
    public void setTemperatura(BigDecimal temperatura) { this.temperatura = temperatura; }
    public Sector getSector() { return sector; }
    public void setSector(Sector sector) { this.sector = sector; }
    public boolean isSimulada() { return simulada; }
    public void setSimulada(boolean simulada) { this.simulada = simulada; }
    public Usuario getRegistradaPor() { return registradaPor; }
    public void setRegistradaPor(Usuario registradaPor) { this.registradaPor = registradaPor; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
}
