# Modelo de datos

- Un usuario tiene varias sesiones. Los tokens se almacenan mediante un hash, con expiración y revocación.
- Un sector tiene varios cultivos, tareas, reportes y lecturas. Los cultivos pueden asociarse a tareas y reportes.
- Una tarea pertenece a un sector, un estudiante asignado y un docente creador. Sus estados son Pendiente, En desarrollo, En revisión, Aprobada y Rechazada.
- Cada entrega de evidencia conserva una foto, un comentario, el resultado de revisión y la identidad del docente revisor. Una tarea rechazada puede volver a iniciarse y generar otra entrega.
- Los reportes guardan autor, sector, cultivo opcional y su último seguimiento docente. Un autor estudiante ve sus propios reportes.
- Las lecturas de sensores guardan temperatura, humedad de suelo y humedad ambiental simuladas.
- Room conserva los borradores de evidencia con clave compuesta por usuario y tarea. MySQL guarda los datos compartidos.
