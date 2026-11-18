package Duoc.Huerto.repository;

import Duoc.Huerto.entity.Cultivo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CultivoRepository extends JpaRepository<Cultivo, Long> {
    List<Cultivo> findAllBySectorIdOrderByNombreAsc(Long sectorId);
    boolean existsBySectorId(Long sectorId);
}
