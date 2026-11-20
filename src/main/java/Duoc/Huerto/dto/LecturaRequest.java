package Duoc.Huerto.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record LecturaRequest(
    @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal humedad,
    @NotNull @DecimalMin("-20.0") @DecimalMax("60.0") BigDecimal temperatura,
    Long sectorId, @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal humedadAmbiental
) {}
