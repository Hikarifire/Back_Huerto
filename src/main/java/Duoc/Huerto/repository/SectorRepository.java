package Duoc.Huerto.repository;

import Duoc.Huerto.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectorRepository extends JpaRepository<Sector, Long> {
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
    boolean existsByNombreIgnoreCase(String nombre);
}
