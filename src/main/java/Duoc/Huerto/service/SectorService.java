package Duoc.Huerto.service;

import Duoc.Huerto.dto.SectorRequest;
import Duoc.Huerto.dto.SectorResponse;
import Duoc.Huerto.entity.Sector;
import Duoc.Huerto.exception.ReglaNegocioException;
import Duoc.Huerto.exception.RecursoNoEncontradoException;
import Duoc.Huerto.repository.CultivoRepository;
import Duoc.Huerto.repository.SectorRepository;
import Duoc.Huerto.repository.TareaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SectorService {
    private final SectorRepository sectores;
    private final CultivoRepository cultivos;
    private final TareaRepository tareas;
    public SectorService(SectorRepository sectores, CultivoRepository cultivos, TareaRepository tareas) {
        this.sectores = sectores; this.cultivos = cultivos; this.tareas = tareas;
    }
    @Transactional(readOnly = true)
    public List<SectorResponse> listar() { return sectores.findAll().stream().map(this::mapear).toList(); }
    @Transactional(readOnly = true)
    public SectorResponse obtener(Long id) { return mapear(buscar(id)); }
    @Transactional
    public SectorResponse crear(SectorRequest request) {
        if (sectores.existsByNombreIgnoreCase(request.nombre().trim())) throw new ReglaNegocioException("Ya existe un sector con ese nombre.");
        return mapear(sectores.save(new Sector(request.nombre().trim(), request.ubicacion(), request.descripcion())));
    }
    @Transactional
    public SectorResponse actualizar(Long id, SectorRequest request) {
        Sector sector = buscar(id);
        sector.setNombre(request.nombre().trim()); sector.setUbicacion(request.ubicacion()); sector.setDescripcion(request.descripcion());
        return mapear(sectores.save(sector));
    }
    @Transactional
    public void eliminar(Long id) {
        Sector sector = buscar(id);
        if (cultivos.existsBySectorId(id) || tareas.existsBySectorId(id)) throw new ReglaNegocioException("No se puede eliminar el sector porque tiene cultivos o tareas asociados.");
        sectores.delete(sector);
    }
    private Sector buscar(Long id) { return sectores.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Sector no encontrado.")); }
    private SectorResponse mapear(Sector s) { return new SectorResponse(s.getId(), s.getNombre(), s.getUbicacion(), s.getDescripcion(), s.isActivo(), s.getFechaCreacion()); }
}
