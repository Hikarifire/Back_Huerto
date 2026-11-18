package Duoc.Huerto.controller;

import Duoc.Huerto.dto.*;
import Duoc.Huerto.service.AuthService;
import Duoc.Huerto.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;
    public AuthController(AuthService authService, TokenService tokenService) { this.authService = authService; this.tokenService = tokenService; }

    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) { return authService.iniciarSesion(request); }
    @GetMapping("/me")
    public UsuarioResponse perfil(Principal principal) { return authService.obtenerPerfil(principal.getName()); }
    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) tokenService.revocar(authorization.substring(7).trim());
        return Map.of("mensaje", "Sesión cerrada correctamente.");
    }
}
