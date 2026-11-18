package Duoc.Huerto.service;

import Duoc.Huerto.dto.UsuarioResponse;
import Duoc.Huerto.entity.TipoCuenta;
import Duoc.Huerto.entity.Usuario;
import Duoc.Huerto.exception.RecursoNoEncontradoException;
import Duoc.Huerto.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarios;
    public UsuarioService(UsuarioRepository usuarios) { this.usuarios = usuarios; }

    @Transactional(readOnly = true)
    public Usuario buscarPorCorreo(String correo) {
        return usuarios.findByCorreoIgnoreCase(correo)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarEstudiantes() {
        return usuarios.findAllByTipoCuentaAndActivoTrueOrderByNombreAsc(TipoCuenta.ALUMNO)
            .stream().map(AuthService::mapear).toList();
    }
}
