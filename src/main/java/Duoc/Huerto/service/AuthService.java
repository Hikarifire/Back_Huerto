package Duoc.Huerto.service;

import Duoc.Huerto.dto.*;
import Duoc.Huerto.entity.TipoCuenta;
import Duoc.Huerto.entity.Usuario;
import Duoc.Huerto.exception.ReglaNegocioException;
import Duoc.Huerto.exception.CredencialesInvalidasException;
import Duoc.Huerto.exception.RecursoNoEncontradoException;
import Duoc.Huerto.repository.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.security.access.prepost.PreAuthorize;
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
        return respuesta(crearUsuario(request, TipoCuenta.ALUMNO));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioResponse crearCuenta(RegistroRequest request, TipoCuenta rol) {
        if (rol == TipoCuenta.ADMIN) {
            throw new IllegalArgumentException("Selecciona el rol docente o estudiante.");
        }
        return mapear(crearUsuario(request, rol));
    }

    private Usuario crearUsuario(RegistroRequest request, TipoCuenta rol) {
        String correo = request.correo().trim().toLowerCase(Locale.ROOT);
        if (usuarios.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioException("Ya existe una cuenta registrada con ese correo.");
        }
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("La contraseña supera el límite de 72 bytes.");
        }
        Usuario usuario = new Usuario(request.nombre().trim(), correo,
            passwordEncoder.encode(request.password()), rol);
        return usuarios.save(usuario);
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
