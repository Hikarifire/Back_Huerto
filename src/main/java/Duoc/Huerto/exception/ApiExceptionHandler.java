package Duoc.Huerto.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ApiError> credenciales(CredencialesInvalidasException ex, HttpServletRequest req) {
        return respuesta(HttpStatus.UNAUTHORIZED, ex.getMessage(), req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> accesoDenegado(AccessDeniedException ex, HttpServletRequest req) {
        return respuesta(HttpStatus.FORBIDDEN, "No tienes permisos para realizar esta operación.", req);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiError> noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest req) {
        return respuesta(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ApiError> conflicto(ReglaNegocioException ex, HttpServletRequest req) {
        return respuesta(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> datosInvalidos(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage).distinct().collect(Collectors.joining("; "));
        return respuesta(HttpStatus.BAD_REQUEST, mensaje.isBlank() ? "Datos inválidos." : mensaje, req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> integridad(DataIntegrityViolationException ex, HttpServletRequest req) {
        return respuesta(HttpStatus.CONFLICT, "La operación no se puede completar porque existen datos relacionados o duplicados.", req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> argumento(IllegalArgumentException ex, HttpServletRequest req) {
        return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    private ResponseEntity<ApiError> respuesta(HttpStatus status, String mensaje, HttpServletRequest req) {
        return ResponseEntity.status(status).body(new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensaje, req.getRequestURI()));
    }
}
