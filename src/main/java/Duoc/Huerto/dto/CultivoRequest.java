package Duoc.Huerto.dto;

import Duoc.Huerto.entity.EstadoCultivo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CultivoRequest(
    @NotBlank @Size(max = 100) String nombre,
    @Size(max = 80) String tipo,
    @Size(max = 500) String descripcion,
    LocalDate fechaSiembra,
    LocalDate fechaCosechaEstimada,
    EstadoCultivo estado,
    @NotNull Long sectorId
) {}
