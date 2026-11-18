package Duoc.Huerto.dto;

import java.time.LocalDateTime;

public record AuthResponse(String token, String tipoToken, LocalDateTime expiraEn, UsuarioResponse usuario) {}
