package Duoc.Huerto.controller;

import Duoc.Huerto.dto.RegistroRequest;
import Duoc.Huerto.dto.UsuarioResponse;
import Duoc.Huerto.entity.TipoCuenta;
import Duoc.Huerto.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import Duoc.Huerto.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final AuthService authService;

    public UsuarioController(UsuarioService usuarioService, AuthService authService) {
        this.usuarioService = usuarioService;
        this.authService = authService;
    }

    @GetMapping("/estudiantes")
    @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
    public List<UsuarioResponse> estudiantes() { return usuarioService.listarEstudiantes(); }

    @PostMapping("/cuentas/{rol}")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crearCuenta(@PathVariable("rol") TipoCuenta rol,
                                        @Valid @RequestBody RegistroRequest request) {
        return authService.crearCuenta(request, rol);
    }
}
