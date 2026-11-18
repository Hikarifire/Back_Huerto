package Duoc.Huerto.repository;

import Duoc.Huerto.entity.TipoCuenta;
import Duoc.Huerto.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    boolean existsByCorreoIgnoreCase(String correo);
    List<Usuario> findAllByTipoCuentaAndActivoTrueOrderByNombreAsc(TipoCuenta tipoCuenta);
}
