package Duoc.Huerto.service;

import Duoc.Huerto.dto.CultivoRequest;
import Duoc.Huerto.entity.Sector;
import Duoc.Huerto.exception.ReglaNegocioException;
import Duoc.Huerto.repository.CultivoRepository;
import Duoc.Huerto.repository.SectorRepository;
import Duoc.Huerto.repository.TareaRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CultivoServiceTest {
    private final CultivoRepository cultivos = mock(CultivoRepository.class);
    private final SectorRepository sectores = mock(SectorRepository.class);
    private final TareaRepository tareas = mock(TareaRepository.class);
    private final CultivoService servicio = new CultivoService(cultivos, sectores, tareas);
    private final LocalDate siembra = LocalDate.of(2026, 11, 20);

    @Test
    void impideCosechaAnteriorALaSiembra() {
        when(sectores.findById(1L)).thenReturn(Optional.of(new Sector()));
        CultivoRequest request = new CultivoRequest("Tomate", null, null, siembra, siembra.minusDays(1), null, 3, null, 1L);
        assertThrows(ReglaNegocioException.class, () -> servicio.crear(request));
        verifyNoInteractions(cultivos);
    }

    @Test
    void impideTrasplantePosteriorALaCosecha() {
        when(sectores.findById(1L)).thenReturn(Optional.of(new Sector()));
        CultivoRequest request = new CultivoRequest("Tomate", null, null, siembra, siembra.plusDays(10),
            siembra.plusDays(11), 3, null, 1L);
        assertThrows(ReglaNegocioException.class, () -> servicio.crear(request));
        verifyNoInteractions(cultivos);
    }
}
