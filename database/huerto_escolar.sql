-- Esquema MySQL para Huerto Escolar.
-- Importar desde phpMyAdmin de XAMPP antes de iniciar Spring Boot.
-- Las entidades JPA mantienen este esquema con spring.jpa.hibernate.ddl-auto=update.
CREATE DATABASE IF NOT EXISTS huerto_escolar
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE huerto_escolar;

CREATE TABLE IF NOT EXISTS usuarios (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  correo VARCHAR(160) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  tipo_cuenta VARCHAR(20) NOT NULL,
  activo BIT(1) NOT NULL DEFAULT b'1',
  fecha_creacion DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_usuarios_correo UNIQUE (correo)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sesiones_usuario (
  id BIGINT NOT NULL AUTO_INCREMENT,
  token_hash VARCHAR(64) NOT NULL,
  usuario_id BIGINT NOT NULL,
  fecha_expiracion DATETIME(6) NOT NULL,
  revocada BIT(1) NOT NULL DEFAULT b'0',
  fecha_creacion DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_sesion_token UNIQUE (token_hash),
  CONSTRAINT fk_sesion_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
  INDEX idx_sesion_usuario (usuario_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sectores (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  ubicacion VARCHAR(150),
  descripcion VARCHAR(500),
  activo BIT(1) NOT NULL DEFAULT b'1',
  fecha_creacion DATETIME(6) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cultivos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  tipo VARCHAR(80),
  descripcion VARCHAR(500),
  fecha_siembra DATE,
  fecha_cosecha_estimada DATE,
  estado VARCHAR(30) NOT NULL,
  sector_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_cultivo_sector FOREIGN KEY (sector_id) REFERENCES sectores(id),
  INDEX idx_cultivo_sector (sector_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tareas (
  id BIGINT NOT NULL AUTO_INCREMENT,
  titulo VARCHAR(140) NOT NULL,
  descripcion VARCHAR(1000) NOT NULL,
  fecha_limite DATE NOT NULL,
  estado VARCHAR(30) NOT NULL,
  sector_id BIGINT NOT NULL,
  cultivo_id BIGINT,
  estudiante_id BIGINT NOT NULL,
  creado_por_id BIGINT NOT NULL,
  fecha_creacion DATETIME(6) NOT NULL,
  fecha_actualizacion DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_tarea_sector FOREIGN KEY (sector_id) REFERENCES sectores(id),
  CONSTRAINT fk_tarea_cultivo FOREIGN KEY (cultivo_id) REFERENCES cultivos(id),
  CONSTRAINT fk_tarea_estudiante FOREIGN KEY (estudiante_id) REFERENCES usuarios(id),
  CONSTRAINT fk_tarea_creador FOREIGN KEY (creado_por_id) REFERENCES usuarios(id),
  INDEX idx_tarea_estudiante (estudiante_id),
  INDEX idx_tarea_sector (sector_id),
  INDEX idx_tarea_cultivo (cultivo_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS evidencias (
  id BIGINT NOT NULL AUTO_INCREMENT,
  tarea_id BIGINT NOT NULL,
  foto_url VARCHAR(500) NOT NULL,
  comentario VARCHAR(1000) NOT NULL,
  estado VARCHAR(30) NOT NULL,
  observacion_revision VARCHAR(1000),
  revisado_por_id BIGINT,
  fecha_registro DATETIME(6) NOT NULL,
  fecha_revision DATETIME(6),
  PRIMARY KEY (id),
  CONSTRAINT fk_evidencia_tarea FOREIGN KEY (tarea_id) REFERENCES tareas(id),
  CONSTRAINT fk_evidencia_revisor FOREIGN KEY (revisado_por_id) REFERENCES usuarios(id),
  INDEX idx_evidencia_tarea (tarea_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS lecturas_huerto (
  id BIGINT NOT NULL AUTO_INCREMENT,
  humedad DECIMAL(5,2) NOT NULL,
  temperatura DECIMAL(5,2) NOT NULL,
  sector_id BIGINT,
  simulada BIT(1) NOT NULL DEFAULT b'1',
  registrada_por_id BIGINT,
  fecha_registro DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_lectura_sector FOREIGN KEY (sector_id) REFERENCES sectores(id),
  CONSTRAINT fk_lectura_usuario FOREIGN KEY (registrada_por_id) REFERENCES usuarios(id),
  INDEX idx_lectura_fecha (fecha_registro)
) ENGINE=InnoDB;

-- El servidor añade automáticamente las columnas y tablas de la versión actual
-- al iniciar con MySQL. Las tablas anteriores se conservan en bases existentes.
