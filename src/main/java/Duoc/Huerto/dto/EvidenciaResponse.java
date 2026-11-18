package Duoc.Huerto.dto;

import Duoc.Huerto.entity.EstadoEvidencia;
import java.time.LocalDateTime;

public record EvidenciaResponse(Long id, Long tareaId, String fotoUrl, String comentario, EstadoEvidencia estado,
                                String observacionRevision, String revisadoPor, LocalDateTime fechaRegistro,
                                LocalDateTime fechaRevision) {}
