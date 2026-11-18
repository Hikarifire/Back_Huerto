package Duoc.Huerto.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    @GetMapping public Map<String, Object> estado() { return Map.of("estado", "ok", "aplicacion", "Huerto Escolar API", "fecha", LocalDateTime.now()); }
}
