package Duoc.Huerto.controller;

import Duoc.Huerto.entity.Reporte;
import Duoc.Huerto.service.ReporteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    public record Seguimiento(@NotNull Reporte.Estado estado, @NotBlank @Size(max = 1000) String seguimiento) {}
    private final ReporteService service;

    public ReporteController(ReporteService service) { this.service = service; }

    @GetMapping
    public List<ReporteService.Resumen> listar(Principal principal) {
        return service.listar(principal.getName());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ReporteService.Resumen crear(@RequestParam Long sectorId, @RequestParam(required = false) Long cultivoId,
                                         @RequestParam String descripcion,
                                         @RequestPart(required = false) MultipartFile foto, Principal principal) {
        return service.crear(principal.getName(), sectorId, cultivoId, descripcion, foto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCENTE','ADMIN')")
    public ReporteService.Resumen actualizar(@PathVariable Long id, @Valid @RequestBody Seguimiento request,
                                             Principal principal) {
        return service.actualizar(id, principal.getName(), request.estado(), request.seguimiento());
    }

    @GetMapping("/{id}/foto")
    public ResponseEntity<Resource> foto(@PathVariable Long id, Principal principal) {
        Resource archivo = service.foto(id, principal.getName());
        return ResponseEntity.ok().contentType(MediaTypeFactory.getMediaType(archivo)
            .orElse(MediaType.APPLICATION_OCTET_STREAM)).body(archivo);
    }
}
