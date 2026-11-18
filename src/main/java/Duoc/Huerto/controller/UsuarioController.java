package Duoc.Huerto.controller;

import Duoc.Huerto.dto.UsuarioResponse;
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
    public UsuarioController(UsuarioService usuarioService) { this.usuarioService = usuarioService; }
    @GetMapping("/estudiantes")
    @PreAuthorize("hasRole('DOCENTE')")
    public List<UsuarioResponse> estudiantes() { return usuarioService.listarEstudiantes(); }
}
