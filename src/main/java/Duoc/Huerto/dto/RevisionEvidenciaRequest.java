package Duoc.Huerto.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RevisionEvidenciaRequest(@NotNull Boolean aprobada, @Size(max = 1000) String observacion) {}
