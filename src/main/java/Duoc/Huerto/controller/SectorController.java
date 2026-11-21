package Duoc.Huerto.controller;

import Duoc.Huerto.dto.SectorRequest;
import Duoc.Huerto.dto.SectorResponse;
import Duoc.Huerto.service.SectorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sectores")
public class SectorController {
    private final SectorService service;
    public SectorController(SectorService service) { this.service = service; }
    @GetMapping public List<SectorResponse> listar() { return service.listar(); }
    @GetMapping("/{id}") public SectorResponse obtener(@PathVariable Long id) { return service.obtener(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
    public SectorResponse crear(@Valid @RequestBody SectorRequest request) { return service.crear(request); }
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
    public SectorResponse actualizar(@PathVariable Long id, @Valid @RequestBody SectorRequest request) { return service.actualizar(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
