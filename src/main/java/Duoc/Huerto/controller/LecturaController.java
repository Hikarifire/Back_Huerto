package Duoc.Huerto.controller;

import Duoc.Huerto.dto.LecturaRequest;
import Duoc.Huerto.dto.LecturaResponse;
import Duoc.Huerto.service.LecturaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/lecturas")
public class LecturaController {
    private final LecturaService service;
    public LecturaController(LecturaService service) { this.service = service; }
    @GetMapping public List<LecturaResponse> listar() { return service.listar(); }
    @PostMapping("/simuladas") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
    public LecturaResponse crearSimulada(@Valid @RequestBody LecturaRequest request, Principal principal) { return service.crearSimulada(request, principal.getName()); }
}
