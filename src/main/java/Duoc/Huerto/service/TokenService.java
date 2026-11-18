package Duoc.Huerto.service;

import Duoc.Huerto.entity.SesionUsuario;
import Duoc.Huerto.entity.Usuario;
import Duoc.Huerto.repository.SesionUsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class TokenService {
    private final SesionUsuarioRepository sesiones;
    private final SecureRandom random = new SecureRandom();
    private final long horasExpiracion;

    public TokenService(SesionUsuarioRepository sesiones,
                        @Value("${app.security.token-expiration-hours:24}") long horasExpiracion) {
        this.sesiones = sesiones;
        this.horasExpiracion = horasExpiracion;
    }

    @Transactional
    public TokenCreado crear(Usuario usuario) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        LocalDateTime expiracion = LocalDateTime.now().plusHours(horasExpiracion);
        sesiones.save(new SesionUsuario(hash(token), usuario, expiracion));
        return new TokenCreado(token, expiracion);
    }

    @Transactional
    public Optional<Usuario> validar(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        return sesiones.findByTokenHashAndRevocadaFalse(hash(token)).filter(sesion -> {
            if (sesion.getFechaExpiracion().isBefore(LocalDateTime.now()) || !sesion.getUsuario().isActivo()) {
                sesion.setRevocada(true);
                return false;
            }
            return true;
        }).map(SesionUsuario::getUsuario);
    }

    @Transactional
    public void revocar(String token) {
        if (token == null || token.isBlank()) return;
        sesiones.findByTokenHashAndRevocadaFalse(hash(token)).ifPresent(sesion -> sesion.setRevocada(true));
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (Exception ex) {
            throw new IllegalStateException("No fue posible procesar el token de sesión.", ex);
        }
    }

    public record TokenCreado(String valor, LocalDateTime expiraEn) {}
}
