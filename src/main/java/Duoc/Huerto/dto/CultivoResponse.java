package Duoc.Huerto.dto;

import Duoc.Huerto.entity.EstadoCultivo;
import java.time.LocalDate;

public record CultivoResponse(Long id, String nombre, String tipo, String descripcion, LocalDate fechaSiembra,
                              LocalDate fechaCosechaEstimada, EstadoCultivo estado, Long sectorId, String sectorNombre,
                              LocalDate fechaTrasplante, Integer frecuenciaRiegoDias) {}
