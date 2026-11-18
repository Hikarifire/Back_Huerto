package Duoc.Huerto.dto;

import Duoc.Huerto.entity.EstadoTarea;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TareaResponse(Long id, String titulo, String descripcion, LocalDate fechaLimite, EstadoTarea estado,
                            Long sectorId, String sectorNombre, Long cultivoId, String cultivoNombre,
                            Long estudianteId, String estudianteNombre, Long creadoPorId, LocalDateTime fechaCreacion) {}
