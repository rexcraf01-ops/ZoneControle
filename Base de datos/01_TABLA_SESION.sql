-- ============================================================================
-- Tabla de sesiones de usuario (autenticación en servidor)
-- Ejecutar DESPUÉS de ZoneControl_BD.sql
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS sesion (
    id_sesion UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_usuario INT NOT NULL REFERENCES usuario (id_usuario) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    expiracion TIMESTAMPTZ NOT NULL,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    direccion_ip INET,
    user_agent TEXT
);

CREATE INDEX IF NOT EXISTS idx_sesion_token_hash ON sesion (token_hash);
CREATE INDEX IF NOT EXISTS idx_sesion_id_usuario ON sesion (id_usuario);
CREATE INDEX IF NOT EXISTS idx_sesion_expiracion ON sesion (expiracion);

COMMENT ON TABLE sesion IS 'Sesiones activas de usuarios autenticados (control de acceso en servidor)';
COMMENT ON COLUMN sesion.id_sesion IS 'Identificador único de la sesión (UUID)';
COMMENT ON COLUMN sesion.id_usuario IS 'Usuario propietario de la sesión';
COMMENT ON COLUMN sesion.token_hash IS 'Hash criptográfico del token de sesión emitido';
COMMENT ON COLUMN sesion.expiracion IS 'Fecha y hora de expiración de la sesión';
COMMENT ON COLUMN sesion.creado_en IS 'Fecha y hora de creación de la sesión';
COMMENT ON COLUMN sesion.direccion_ip IS 'Dirección IP del cliente que inició la sesión';
COMMENT ON COLUMN sesion.user_agent IS 'Agente de usuario / navegador del cliente autenticado';