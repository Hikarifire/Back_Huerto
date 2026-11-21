-- Para una instalación existente con tipo_cuenta declarado como ENUM.
-- El cambio conserva los valores DOCENTE y ALUMNO ya almacenados.
USE huerto_escolar;
ALTER TABLE usuarios MODIFY COLUMN tipo_cuenta VARCHAR(20) NOT NULL;
