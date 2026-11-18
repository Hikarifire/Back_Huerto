package Duoc.Huerto.repository;

import Duoc.Huerto.entity.SesionUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SesionUsuarioRepository extends JpaRepository<SesionUsuario, Long> {
    Optional<SesionUsuario> findByTokenHashAndRevocadaFalse(String tokenHash);
}
