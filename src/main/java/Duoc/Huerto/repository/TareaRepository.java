package Duoc.Huerto.repository;

import Duoc.Huerto.entity.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    List<Tarea> findAllByEstudianteIdOrderByFechaLimiteAsc(Long estudianteId);
    List<Tarea> findAllByOrderByFechaLimiteAsc();
    boolean existsBySectorId(Long sectorId);
    boolean existsByCultivoId(Long cultivoId);
}
