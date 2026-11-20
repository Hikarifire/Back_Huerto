package Duoc.Huerto.repository;

import Duoc.Huerto.entity.Tarea;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Tarea t where t.id = :id")
    Optional<Tarea> buscarParaActualizar(@Param("id") Long id);
    List<Tarea> findAllByEstudianteIdOrderByFechaLimiteAsc(Long estudianteId);
    List<Tarea> findAllByOrderByFechaLimiteAsc();
    boolean existsBySectorId(Long sectorId);
    boolean existsByCultivoId(Long cultivoId);
}
