-- Guarda el destinatario en el historial de reportes enviados.
-- Seguro de ejecutar repetidamente y compatible con exportaciones previas.
ALTER TABLE exportacion
    ADD COLUMN IF NOT EXISTS correo_destino VARCHAR(254);

COMMENT ON COLUMN exportacion.correo_destino IS
    'Correo destinatario si el reporte fue enviado; NULL si solo se descargó';
