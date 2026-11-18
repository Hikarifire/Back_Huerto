package Duoc.Huerto.dto;

import java.time.LocalDateTime;

public record SectorResponse(Long id, String nombre, String ubicacion, String descripcion, boolean activo, LocalDateTime fechaCreacion) {}
