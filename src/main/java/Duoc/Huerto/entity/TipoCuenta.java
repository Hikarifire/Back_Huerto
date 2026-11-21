package Duoc.Huerto.entity;

public enum TipoCuenta {
    DOCENTE,
    ALUMNO,
    ADMIN;

    public boolean puedeGestionarHuerto() {
        return this == DOCENTE || this == ADMIN;
    }
}
