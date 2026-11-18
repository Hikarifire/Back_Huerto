package Duoc.Huerto.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LecturaResponse(Long id, BigDecimal humedad, BigDecimal temperatura, Long sectorId,
                              String sectorNombre, boolean simulada, LocalDateTime fechaRegistro) {}
