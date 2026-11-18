package Duoc.Huerto.controller;

import Duoc.Huerto.dto.TareaRequest;
import Duoc.Huerto.dto.TareaResponse;
import Duoc.Huerto.service.TareaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {
    private final TareaService service;
    public TareaController(TareaService service) { this.service = service; }
    @GetMapping public List<TareaResponse> listar(Principal principal) { return service.listarPara(principal.getName()); }
    @GetMapping("/{id}") public TareaResponse obtener(@PathVariable Long id, Principal principal) { return service.obtener(id, principal.getName()); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasRole('DOCENTE')")
    public TareaResponse crear(@Valid @RequestBody TareaRequest request, Principal principal) { return service.crear(request, principal.getName()); }
    @PutMapping("/{id}") @PreAuthorize("hasRole('DOCENTE')")
    public TareaResponse actualizar(@PathVariable Long id, @Valid @RequestBody TareaRequest request) { return service.actualizar(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @PreAuthorize("hasRole('DOCENTE')")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
