package Duoc.Huerto.repository;

import Duoc.Huerto.entity.LecturaHuerto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LecturaHuertoRepository extends JpaRepository<LecturaHuerto, Long> {
    List<LecturaHuerto> findTop30ByOrderByFechaRegistroDesc();
}
