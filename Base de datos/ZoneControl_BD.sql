-- ============================================================================
-- Zona Control - Script de creación de base de datos (PostgreSQL)
-- ADVERTENCIA: El DROP TABLE elimina datos. No usar en producción sin respaldo.
-- ============================================================================

DROP TABLE IF EXISTS exportacion, sesion, intento_ingreso, usuario, personal,
                     resultado_ingreso, estado_autorizacion, departamento, rol CASCADE;

-- Catálogos
CREATE TABLE IF NOT EXISTS rol (
    id_rol   INT GENERATED ALWAYS AS IDENTITY,
    nombre   VARCHAR(50) NOT NULL,
    CONSTRAINT pk_rol PRIMARY KEY (id_rol),
    CONSTRAINT uq_rol_nombre UNIQUE (nombre)
);

CREATE TABLE IF NOT EXISTS departamento (
    id_departamento INT GENERATED ALWAYS AS IDENTITY,
    nombre          VARCHAR(100) NOT NULL,
    direccion       VARCHAR(200) NOT NULL DEFAULT 'Sin dirección registrada',
    CONSTRAINT pk_departamento PRIMARY KEY (id_departamento),
    CONSTRAINT uq_departamento_nombre UNIQUE (nombre)
);

CREATE TABLE IF NOT EXISTS estado_autorizacion (
    id_estado_autorizacion INT GENERATED ALWAYS AS IDENTITY,
    nombre                 VARCHAR(30) NOT NULL,
    CONSTRAINT pk_estado_autorizacion PRIMARY KEY (id_estado_autorizacion),
    CONSTRAINT uq_estado_autorizacion_nombre UNIQUE (nombre),
    CONSTRAINT chk_estado_autorizacion_nombre
        CHECK (nombre IN ('Otorgada', 'Revocada', 'Suspendida'))
);

CREATE TABLE IF NOT EXISTS resultado_ingreso (
    id_resultado_ingreso INT GENERATED ALWAYS AS IDENTITY,
    nombre               VARCHAR(30) NOT NULL,
    CONSTRAINT pk_resultado_ingreso PRIMARY KEY (id_resultado_ingreso),
    CONSTRAINT uq_resultado_ingreso_nombre UNIQUE (nombre),
    CONSTRAINT chk_resultado_ingreso_nombre
        CHECK (nombre IN ('Autorizado', 'Denegado', 'No registrado'))
);

-- Tabla transaccional: Personal
CREATE TABLE IF NOT EXISTS personal (
    id_personal            INT GENERATED ALWAYS AS IDENTITY,
    numero_identificacion  VARCHAR(20)  NOT NULL,
    nombres                VARCHAR(100) NOT NULL,
    apellidos              VARCHAR(100) NOT NULL,
    id_departamento        INT          NOT NULL,
    id_estado_autorizacion INT          NOT NULL,
    CONSTRAINT pk_personal PRIMARY KEY (id_personal),
    CONSTRAINT uq_personal_identificacion UNIQUE (numero_identificacion),
    CONSTRAINT fk_personal_departamento
        FOREIGN KEY (id_departamento) REFERENCES departamento (id_departamento)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_personal_estado
        FOREIGN KEY (id_estado_autorizacion) REFERENCES estado_autorizacion (id_estado_autorizacion)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_personal_apellidos_nombres ON personal (apellidos, nombres);
CREATE INDEX IF NOT EXISTS idx_personal_departamento      ON personal (id_departamento);
CREATE INDEX IF NOT EXISTS idx_personal_estado            ON personal (id_estado_autorizacion);

-- Tabla transaccional: Usuario del sistema
-- id_personal es UNIQUE y nullable: relación 1:1 opcional (un usuario puede o no
-- vincularse a un registro de personal; un personal solo puede tener un usuario).
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario      INT GENERATED ALWAYS AS IDENTITY,
    nombre_usuario  VARCHAR(50)  NOT NULL,
    contrasena_hash VARCHAR(255) NOT NULL,
    id_rol          INT          NOT NULL,
    id_personal     INT          NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_nombre UNIQUE (nombre_usuario),
    CONSTRAINT uq_usuario_personal UNIQUE (id_personal),
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol) REFERENCES rol (id_rol)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_usuario_personal
        FOREIGN KEY (id_personal) REFERENCES personal (id_personal)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_usuario_rol ON usuario (id_rol);

-- Tabla transaccional: Intento de ingreso (auditoría de accesos)
CREATE TABLE IF NOT EXISTS intento_ingreso (
    id_intento                      INT GENERATED ALWAYS AS IDENTITY,
    numero_identificacion_ingresado VARCHAR(20) NOT NULL,
    id_personal                     INT         NULL,
    id_resultado_ingreso            INT         NOT NULL,
    fecha_hora                      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_intento_ingreso PRIMARY KEY (id_intento),
    CONSTRAINT fk_intento_personal
        FOREIGN KEY (id_personal) REFERENCES personal (id_personal)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_intento_resultado
        FOREIGN KEY (id_resultado_ingreso) REFERENCES resultado_ingreso (id_resultado_ingreso)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_intento_personal_fecha ON intento_ingreso (id_personal, fecha_hora);
CREATE INDEX IF NOT EXISTS idx_intento_fecha          ON intento_ingreso (fecha_hora);
CREATE INDEX IF NOT EXISTS idx_intento_identificacion ON intento_ingreso (numero_identificacion_ingresado);
CREATE INDEX IF NOT EXISTS idx_intento_resultado      ON intento_ingreso (id_resultado_ingreso);

-- Datos semilla: catálogos
INSERT INTO rol (nombre) VALUES
    ('Administrador del sistema'),
    ('Gestor de personal'),
    ('Supervisor/Auditor')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO estado_autorizacion (nombre) VALUES
    ('Otorgada'),
    ('Revocada'),
    ('Suspendida')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO resultado_ingreso (nombre) VALUES
    ('Autorizado'),
    ('Denegado'),
    ('No registrado')
ON CONFLICT (nombre) DO NOTHING;

-- Datos semilla: departamentos (con columna direccion agregada)
INSERT INTO departamento (nombre, direccion) VALUES
    ('Sistemas', 'Av. Laboratorio 123, Piso 2, Edificio Central'),
    ('Seguridad', 'Av. Laboratorio 456, Piso 1, Bloque B'),
    ('Administración', 'Av. Laboratorio 789, Piso 3, Edificio Central'),
    ('Operaciones', 'Por definir'),
    ('Recursos Humanos', 'Por definir'),
    ('Tecnología', 'Por definir'),
    ('Finanzas', 'Por definir'),
    ('Logística', 'Por definir')
ON CONFLICT (nombre) DO NOTHING;

-- Datos semilla: personal (5 registros con estados variados: Otorgada, Revocada, Suspendida)
INSERT INTO personal (numero_identificacion, nombres, apellidos, id_departamento, id_estado_autorizacion) VALUES
    ('12345678', 'Carlos', 'Mendoza Ríos',
        (SELECT id_departamento FROM departamento WHERE nombre = 'Sistemas'),
        (SELECT id_estado_autorizacion FROM estado_autorizacion WHERE nombre = 'Otorgada')),
    ('87654321', 'María', 'García López',
        (SELECT id_departamento FROM departamento WHERE nombre = 'Seguridad'),
        (SELECT id_estado_autorizacion FROM estado_autorizacion WHERE nombre = 'Otorgada')),
    ('11223344', 'Ana', 'Martínez Gómez',
        (SELECT id_departamento FROM departamento WHERE nombre = 'Administración'),
        (SELECT id_estado_autorizacion FROM estado_autorizacion WHERE nombre = 'Revocada')),
    ('44332211', 'Jorge', 'Sánchez Prado',
        (SELECT id_departamento FROM departamento WHERE nombre = 'Sistemas'),
        (SELECT id_estado_autorizacion FROM estado_autorizacion WHERE nombre = 'Suspendida')),
    ('55667788', 'Lucía', 'Fernández Castro',
        (SELECT id_departamento FROM departamento WHERE nombre = 'Seguridad'),
        (SELECT id_estado_autorizacion FROM estado_autorizacion WHERE nombre = 'Otorgada'))
ON CONFLICT (numero_identificacion) DO NOTHING;

-- Datos semilla: usuarios de prueba
-- contraseña de prueba: 1234, cambiar en producción
-- Hashes BCrypt reales verificados con Spring Security BCryptPasswordEncoder
INSERT INTO usuario (nombre_usuario, contrasena_hash, id_rol, id_personal) VALUES
    ('admin',      '$2a$10$QD3zZ41a6hdJL8ULhn4Jd.lFDbh/7d7FHaZjPCwmcqhRTxsfyXj3S', 1,
        (SELECT id_personal FROM personal WHERE numero_identificacion = '12345678')),
    ('gestor',     '$2a$10$QD3zZ41a6hdJL8ULhn4Jd.lFDbh/7d7FHaZjPCwmcqhRTxsfyXj3S', 2,
        (SELECT id_personal FROM personal WHERE numero_identificacion = '87654321')),
    ('supervisor', '$2a$10$QD3zZ41a6hdJL8ULhn4Jd.lFDbh/7d7FHaZjPCwmcqhRTxsfyXj3S', 3, NULL)
ON CONFLICT (nombre_usuario) DO NOTHING;

-- Datos semilla: intentos de ingreso de ejemplo (auditoría inicial, incluyendo persona no registrada)
INSERT INTO intento_ingreso (numero_identificacion_ingresado, id_personal, id_resultado_ingreso, fecha_hora) VALUES
    ('12345678', (SELECT id_personal FROM personal WHERE numero_identificacion = '12345678'),
        (SELECT id_resultado_ingreso FROM resultado_ingreso WHERE nombre = 'Autorizado'),
        CURRENT_TIMESTAMP - INTERVAL '2 hours'),
    ('11223344', (SELECT id_personal FROM personal WHERE numero_identificacion = '11223344'),
        (SELECT id_resultado_ingreso FROM resultado_ingreso WHERE nombre = 'Denegado'),
        CURRENT_TIMESTAMP - INTERVAL '1 hour'),
    ('99999999', NULL,
        (SELECT id_resultado_ingreso FROM resultado_ingreso WHERE nombre = 'No registrado'),
        CURRENT_TIMESTAMP - INTERVAL '30 minutes')
ON CONFLICT DO NOTHING;

-- Comentarios de documentación del esquema
COMMENT ON TABLE rol IS 'Catálogo de roles internos del sistema';
COMMENT ON COLUMN rol.id_rol IS 'Identificador secuencial único del rol';
COMMENT ON COLUMN rol.nombre IS 'Nombre representativo del rol interno del sistema';

COMMENT ON TABLE departamento IS 'Departamentos del laboratorio';
COMMENT ON COLUMN departamento.id_departamento IS 'Identificador secuencial único del departamento';
COMMENT ON COLUMN departamento.nombre IS 'Nombre oficial del departamento dentro del laboratorio';
COMMENT ON COLUMN departamento.direccion IS 'Dirección física del departamento en las instalaciones';

COMMENT ON TABLE estado_autorizacion IS 'Estados de autorización de acceso para el personal';
COMMENT ON COLUMN estado_autorizacion.id_estado_autorizacion IS 'Identificador único del estado de autorización';
COMMENT ON COLUMN estado_autorizacion.nombre IS 'Nombre del estado: Otorgada, Revocada o Suspendida';

COMMENT ON TABLE resultado_ingreso IS 'Posibles resultados de un intento de ingreso físico';
COMMENT ON COLUMN resultado_ingreso.id_resultado_ingreso IS 'Identificador único del resultado';
COMMENT ON COLUMN resultado_ingreso.nombre IS 'Resultado obtenido: Autorizado, Denegado o No registrado';

COMMENT ON TABLE personal IS 'Personal autorizado para ingresar a áreas restringidas del laboratorio';
COMMENT ON COLUMN personal.id_personal IS 'Identificador secuencial único de la persona';
COMMENT ON COLUMN personal.numero_identificacion IS 'Número de identificación único interno o institucional de la persona';
COMMENT ON COLUMN personal.nombres IS 'Nombres completos de la persona';
COMMENT ON COLUMN personal.apellidos IS 'Apellidos completos de la persona';
COMMENT ON COLUMN personal.id_departamento IS 'Clave foránea hacia el departamento asignado';
COMMENT ON COLUMN personal.id_estado_autorizacion IS 'Clave foránea hacia el estado de autorización de acceso';

COMMENT ON TABLE usuario IS 'Usuarios internos con credenciales de acceso al software Zone Control';
COMMENT ON COLUMN usuario.id_usuario IS 'Identificador secuencial único del usuario';
COMMENT ON COLUMN usuario.nombre_usuario IS 'Nombre único de usuario para iniciar sesión';
COMMENT ON COLUMN usuario.contrasena_hash IS 'Hash BCrypt seguro de la contraseña del usuario';
COMMENT ON COLUMN usuario.id_rol IS 'Clave foránea hacia el rol de permisos del usuario';
COMMENT ON COLUMN usuario.id_personal IS 'UNIQUE y nullable. Relación 1:1 opcional con personal.';

COMMENT ON TABLE intento_ingreso IS 'Registro de auditoría inmutable de todos los intentos de acceso';
COMMENT ON COLUMN intento_ingreso.id_intento IS 'Identificador secuencial del intento de ingreso';
COMMENT ON COLUMN intento_ingreso.numero_identificacion_ingresado IS 'Número de identificación digitado o leído por el lector';
COMMENT ON COLUMN intento_ingreso.id_personal IS 'Clave foránea a personal (NULL si la persona no está registrada en el sistema)';
COMMENT ON COLUMN intento_ingreso.id_resultado_ingreso IS 'Clave foránea al resultado del intento (Autorizado, Denegado, No registrado)';
COMMENT ON COLUMN intento_ingreso.fecha_hora IS 'Fecha y hora exacta con zona horaria (TIMESTAMPTZ) en que ocurrió el intento';