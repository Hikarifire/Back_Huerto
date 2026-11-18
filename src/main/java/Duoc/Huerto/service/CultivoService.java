package Duoc.Huerto.service;

import Duoc.Huerto.dto.CultivoRequest;
import Duoc.Huerto.dto.CultivoResponse;
import Duoc.Huerto.entity.Cultivo;
import Duoc.Huerto.entity.EstadoCultivo;
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
public class CultivoService {
    private final CultivoRepository cultivos;
    private final SectorRepository sectores;
    private final TareaRepository tareas;
    public CultivoService(CultivoRepository cultivos, SectorRepository sectores, TareaRepository tareas) { this.cultivos = cultivos; this.sectores = sectores; this.tareas = tareas; }
    @Transactional(readOnly = true)
    public List<CultivoResponse> listar(Long sectorId) {
        List<Cultivo> lista = sectorId == null ? cultivos.findAll() : cultivos.findAllBySectorIdOrderByNombreAsc(sectorId);
        return lista.stream().map(this::mapear).toList();
    }
    @Transactional(readOnly = true)
    public CultivoResponse obtener(Long id) { return mapear(buscar(id)); }
    @Transactional
    public CultivoResponse crear(CultivoRequest request) {
        Cultivo cultivo = new Cultivo(); aplicar(cultivo, request); return mapear(cultivos.save(cultivo));
    }
    @Transactional
    public CultivoResponse actualizar(Long id, CultivoRequest request) {
        Cultivo cultivo = buscar(id); aplicar(cultivo, request); return mapear(cultivos.save(cultivo));
    }
    @Transactional
    public void eliminar(Long id) {
        Cultivo cultivo = buscar(id);
        if (tareas.existsByCultivoId(id)) throw new ReglaNegocioException("No se puede eliminar el cultivo porque tiene tareas asociadas.");
        cultivos.delete(cultivo);
    }
    private void aplicar(Cultivo c, CultivoRequest r) {
        Sector sector = sectores.findById(r.sectorId()).orElseThrow(() -> new RecursoNoEncontradoException("Sector no encontrado."));
        if (r.fechaSiembra() != null && r.fechaCosechaEstimada() != null && r.fechaCosechaEstimada().isBefore(r.fechaSiembra())) throw new ReglaNegocioException("La fecha estimada de cosecha no puede ser anterior a la siembra.");
        c.setNombre(r.nombre().trim()); c.setTipo(r.tipo()); c.setDescripcion(r.descripcion()); c.setFechaSiembra(r.fechaSiembra());
        c.setFechaCosechaEstimada(r.fechaCosechaEstimada()); c.setEstado(r.estado() == null ? EstadoCultivo.SEMBRADO : r.estado()); c.setSector(sector);
    }
    private Cultivo buscar(Long id) { return cultivos.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Cultivo no encontrado.")); }
    private CultivoResponse mapear(Cultivo c) { return new CultivoResponse(c.getId(), c.getNombre(), c.getTipo(), c.getDescripcion(), c.getFechaSiembra(), c.getFechaCosechaEstimada(), c.getEstado(), c.getSector().getId(), c.getSector().getNombre()); }
}
