package Duoc.Huerto.controller;

import Duoc.Huerto.dto.CultivoRequest;
import Duoc.Huerto.dto.CultivoResponse;
import Duoc.Huerto.service.CultivoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cultivos")
public class CultivoController {
    private final CultivoService service;
    public CultivoController(CultivoService service) { this.service = service; }
    @GetMapping public List<CultivoResponse> listar(@RequestParam(required = false) Long sectorId) { return service.listar(sectorId); }
    @GetMapping("/{id}") public CultivoResponse obtener(@PathVariable Long id) { return service.obtener(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
    public CultivoResponse crear(@Valid @RequestBody CultivoRequest request) { return service.crear(request); }
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
    public CultivoResponse actualizar(@PathVariable Long id, @Valid @RequestBody CultivoRequest request) { return service.actualizar(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
