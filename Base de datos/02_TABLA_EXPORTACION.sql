-- ============================================================================
-- Tabla de exportaciones del archivo de intercambio (auditoría de exportación)
-- Ejecutar DESPUÉS de 01_TABLA_SESION.sql
-- ============================================================================

CREATE TABLE IF NOT EXISTS exportacion (
    id_exportacion          INT GENERATED ALWAYS AS IDENTITY,
    fecha_hora_generacion   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    periodo_inicio          DATE        NOT NULL,
    periodo_fin             DATE        NOT NULL,
    formato                 VARCHAR(20) NOT NULL DEFAULT 'CSV',
    correo_destino          VARCHAR(254) NULL,
    id_usuario              INT         NULL,
    CONSTRAINT pk_exportacion PRIMARY KEY (id_exportacion),
    CONSTRAINT fk_exportacion_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_exportacion_fecha ON exportacion (fecha_hora_generacion);
CREATE INDEX IF NOT EXISTS idx_exportacion_usuario ON exportacion (id_usuario);

COMMENT ON TABLE exportacion IS 'Registro de auditoría de cada exportación del archivo de intercambio generada por supervisores';
COMMENT ON COLUMN exportacion.id_exportacion IS 'Identificador único de la exportación';
COMMENT ON COLUMN exportacion.fecha_hora_generacion IS 'Fecha y hora exacta con zona horaria en la que se generó la exportación';
COMMENT ON COLUMN exportacion.periodo_inicio IS 'Fecha inicial del periodo de auditoría exportado';
COMMENT ON COLUMN exportacion.periodo_fin IS 'Fecha final del periodo de auditoría exportado';
COMMENT ON COLUMN exportacion.formato IS 'Formato del archivo exportado (ej. CSV)';
COMMENT ON COLUMN exportacion.correo_destino IS 'Correo destinatario si el reporte fue enviado; NULL si solo se descargó';
COMMENT ON COLUMN exportacion.id_usuario IS 'Usuario interno que solicitó y generó la exportación (nullable si el usuario es eliminado)';
