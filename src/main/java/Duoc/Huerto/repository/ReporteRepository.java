package Duoc.Huerto.repository;

import Duoc.Huerto.entity.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findAllByOrderByCreadoEnDesc();
    List<Reporte> findAllByAutorIdOrderByCreadoEnDesc(Long autorId);
}
