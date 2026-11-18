# Modelo de datos de Huerto Escolar

## Relaciones principales

- Un `Usuario` puede tener varias `SesionUsuario`.
- Un `Sector` puede contener varios `Cultivo`.
- Un `Sector` puede tener varias `Tarea`.
- Un `Cultivo` puede estar asociado a varias `Tarea` (la asociación en la tarea es opcional).
- Un `Usuario` con rol `ALUMNO` puede tener varias tareas asignadas.
- Un `Usuario` con rol `DOCENTE` puede crear varias tareas.
- Una `Tarea` puede tener varias `Evidencia` (por ejemplo, una nueva entrega después de un rechazo).
- Un `Usuario` docente puede revisar varias evidencias.
- Un `Sector` puede tener varias `LecturaHuerto`; las lecturas también registran quién las ingresó.

## Tablas

| Tabla | Propósito |
|---|---|
| `usuarios` | Nombre, correo único, hash BCrypt, rol y estado de la cuenta. |
| `sesiones_usuario` | Hash del token de acceso, usuario asociado, vencimiento y revocación. |
| `sectores` | Zonas físicas del huerto. |
| `cultivos` | Plantaciones y fechas, vinculadas a un sector. |
| `tareas` | Actividades, fechas, estado, sector, cultivo opcional, estudiante y docente creador. |
| `evidencias` | Ruta de imagen, comentario y resultado de revisión docente. |
| `lecturas_huerto` | Humedad y temperatura; en esta versión se identifican como simuladas. |

## Enumeraciones

- `TipoCuenta`: `DOCENTE`, `ALUMNO`.
- `EstadoCultivo`: `SEMBRADO`, `EN_CRECIMIENTO`, `LISTO_PARA_COSECHA`, `COSECHADO`.
- `EstadoTarea`: `PENDIENTE`, `EN_REVISION`, `APROBADA`, `RECHAZADA`.
- `EstadoEvidencia`: `EN_REVISION`, `APROBADA`, `RECHAZADA`.

Las enumeraciones se almacenan como texto (`EnumType.STRING`) para que la base de datos sea legible y no dependa del orden ordinal del enum.
