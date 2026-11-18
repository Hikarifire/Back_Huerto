package Duoc.Huerto.service;

import Duoc.Huerto.dto.*;
import Duoc.Huerto.entity.TipoCuenta;
import Duoc.Huerto.entity.Usuario;
import Duoc.Huerto.exception.ReglaNegocioException;
import Duoc.Huerto.exception.CredencialesInvalidasException;
import Duoc.Huerto.exception.RecursoNoEncontradoException;
import Duoc.Huerto.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.usuarios = usuarios; this.passwordEncoder = passwordEncoder; this.tokenService = tokenService;
    }

    @Transactional
    public AuthResponse registrar(RegistroRequest request) {
        String correo = request.correo().trim().toLowerCase();
        if (usuarios.existsByCorreoIgnoreCase(correo)) throw new ReglaNegocioException("Ya existe una cuenta registrada con ese correo.");
        Usuario usuario = new Usuario(request.nombre().trim(), correo, passwordEncoder.encode(request.password()), TipoCuenta.ALUMNO);
        usuario = usuarios.save(usuario);
        return respuesta(usuario);
    }

    @Transactional
    public AuthResponse iniciarSesion(LoginRequest request) {
        Usuario usuario = usuarios.findByCorreoIgnoreCase(request.correo().trim())
            .filter(Usuario::isActivo)
            .orElseThrow(() -> new CredencialesInvalidasException("Correo o contraseña incorrectos."));
        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos.");
        }
        return respuesta(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPerfil(String correo) {
        Usuario usuario = usuarios.findByCorreoIgnoreCase(correo)
            .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario autenticado."));
        return mapear(usuario);
    }

    private AuthResponse respuesta(Usuario usuario) {
        TokenService.TokenCreado token = tokenService.crear(usuario);
        return new AuthResponse(token.valor(), "Bearer", token.expiraEn(), mapear(usuario));
    }

    public static UsuarioResponse mapear(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getCorreo(), usuario.getTipoCuenta());
    }
}
