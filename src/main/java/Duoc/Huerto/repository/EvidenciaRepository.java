package Duoc.Huerto.repository;

import Duoc.Huerto.entity.Evidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EvidenciaRepository extends JpaRepository<Evidencia, Long> {
    List<Evidencia> findAllByTareaIdOrderByFechaRegistroDesc(Long tareaId);
    Optional<Evidencia> findByIdAndTareaEstudianteId(Long id, Long estudianteId);
    boolean existsByTareaId(Long tareaId);
}
