-- Additive migration for account recovery. Existing accounts keep a NULL email
-- until an administrator enters and saves their recovery address.
ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS email VARCHAR(254);

CREATE UNIQUE INDEX IF NOT EXISTS ux_usuario_email_lower
    ON usuario (LOWER(email))
    WHERE email IS NOT NULL;

CREATE TABLE IF NOT EXISTS password_reset_token (
    token_hash VARCHAR(64) PRIMARY KEY,
    id_usuario INTEGER NOT NULL REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS ix_password_reset_token_expiration
    ON password_reset_token (expires_at);
