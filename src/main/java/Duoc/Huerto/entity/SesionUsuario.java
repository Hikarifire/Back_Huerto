package Duoc.Huerto.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sesiones_usuario")
public class SesionUsuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "token_hash", nullable = false, length = 64, unique = true)
    private String tokenHash;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;
    @Column(nullable = false)
    private boolean revocada = false;
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    @PrePersist void antesDeGuardar() { if (fechaCreacion == null) fechaCreacion = LocalDateTime.now(); }
    public SesionUsuario() {}
    public SesionUsuario(String tokenHash, Usuario usuario, LocalDateTime fechaExpiracion) { this.tokenHash = tokenHash; this.usuario = usuario; this.fechaExpiracion = fechaExpiracion; }
    public Long getId() { return id; }
    public String getTokenHash() { return tokenHash; }
    public Usuario getUsuario() { return usuario; }
    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
    public boolean isRevocada() { return revocada; }
    public void setRevocada(boolean revocada) { this.revocada = revocada; }
}
