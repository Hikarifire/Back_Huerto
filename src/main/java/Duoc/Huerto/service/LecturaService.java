package Duoc.Huerto.service;

import Duoc.Huerto.dto.LecturaRequest;
import Duoc.Huerto.dto.LecturaResponse;
import Duoc.Huerto.entity.LecturaHuerto;
import Duoc.Huerto.entity.Sector;
import Duoc.Huerto.entity.Usuario;
import Duoc.Huerto.exception.RecursoNoEncontradoException;
import Duoc.Huerto.repository.LecturaHuertoRepository;
import Duoc.Huerto.repository.SectorRepository;
import Duoc.Huerto.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class LecturaService {
    private final LecturaHuertoRepository lecturas;
    private final SectorRepository sectores;
    private final UsuarioRepository usuarios;
    public LecturaService(LecturaHuertoRepository lecturas, SectorRepository sectores, UsuarioRepository usuarios) { this.lecturas = lecturas; this.sectores = sectores; this.usuarios = usuarios; }
    @Transactional(readOnly = true)
    public List<LecturaResponse> listar() { return lecturas.findTop30ByOrderByFechaRegistroDesc().stream().map(this::mapear).toList(); }
    @Transactional
    public LecturaResponse crearSimulada(LecturaRequest request, String correo) {
        Usuario usuario = usuarios.findByCorreoIgnoreCase(correo).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));
        LecturaHuerto lectura = new LecturaHuerto(); lectura.setHumedad(request.humedad()); lectura.setTemperatura(request.temperatura());
        if (request.sectorId() != null) {
            Sector sector = sectores.findById(request.sectorId()).orElseThrow(() -> new RecursoNoEncontradoException("Sector no encontrado."));
            lectura.setSector(sector);
        }
        lectura.setSimulada(true); lectura.setRegistradaPor(usuario);
        return mapear(lecturas.save(lectura));
    }
    private LecturaResponse mapear(LecturaHuerto l) { return new LecturaResponse(l.getId(), l.getHumedad(), l.getTemperatura(), l.getSector() == null ? null : l.getSector().getId(), l.getSector() == null ? null : l.getSector().getNombre(), l.isSimulada(), l.getFechaRegistro()); }
}
