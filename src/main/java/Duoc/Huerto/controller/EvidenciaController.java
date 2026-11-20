package Duoc.Huerto.controller;

import Duoc.Huerto.dto.EvidenciaResponse;
import Duoc.Huerto.dto.RevisionEvidenciaRequest;
import Duoc.Huerto.service.EvidenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.List;

@RestController
public class EvidenciaController {
    private final EvidenciaService service;
    public EvidenciaController(EvidenciaService service) { this.service = service; }

    @PostMapping(value = "/api/tareas/{tareaId}/evidencias", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ALUMNO')")
    public EvidenciaResponse entregar(@PathVariable Long tareaId, @RequestPart("foto") MultipartFile foto,
                                      @RequestPart("comentario") String comentario, Principal principal) {
        return service.entregar(tareaId, principal.getName(), foto, comentario);
    }

    @GetMapping("/api/tareas/{tareaId}/evidencias")
    public List<EvidenciaResponse> listar(@PathVariable Long tareaId, Principal principal) { return service.listarDeTarea(tareaId, principal.getName()); }

    @GetMapping("/api/evidencias/{id}/foto")
    public ResponseEntity<Resource> foto(@PathVariable Long id, Principal principal) {
        Resource imagen = service.obtenerFoto(id, principal.getName());
        return ResponseEntity.ok().contentType(MediaTypeFactory.getMediaType(imagen)
            .orElse(MediaType.APPLICATION_OCTET_STREAM)).body(imagen);
    }

    @PostMapping("/api/evidencias/{id}/revision")
    @PreAuthorize("hasRole('DOCENTE')")
    public EvidenciaResponse revisar(@PathVariable Long id, @Valid @RequestBody RevisionEvidenciaRequest request, Principal principal) {
        return service.revisar(id, principal.getName(), request.aprobada(), request.observacion());
    }
}
