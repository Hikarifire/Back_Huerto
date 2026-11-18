package Duoc.Huerto.repository;

import Duoc.Huerto.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectorRepository extends JpaRepository<Sector, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}
