package Duoc.Huerto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SectorRequest(
    @NotBlank @Size(max = 100) String nombre,
    @NotBlank @Size(max = 150) String ubicacion,
    @Size(max = 500) String descripcion, Boolean activo
) {}
