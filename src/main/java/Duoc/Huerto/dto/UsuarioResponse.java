package Duoc.Huerto.dto;

import Duoc.Huerto.entity.TipoCuenta;

public record UsuarioResponse(Long id, String nombre, String correo, TipoCuenta tipoCuenta) {}
