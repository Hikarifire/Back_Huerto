package Duoc.Huerto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TareaRequest(
    @NotBlank @Size(max = 140) String titulo,
    @NotBlank @Size(max = 1000) String descripcion,
    @NotNull LocalDate fechaLimite,
    @NotNull Long sectorId,
    Long cultivoId,
    @NotNull Long estudianteId
) {}
