-- Listar usuarios con su rol
SELECT 
    u.id_usuario,
    u.nombre_usuario,
    r.nombre AS rol,
    p.nombres,
    p.apellidos,
    p.numero_identificacion
FROM usuario u
INNER JOIN rol r ON u.id_rol = r.id_rol
LEFT JOIN personal p ON u.id_personal = p.id_personal
ORDER BY u.nombre_usuario;

-- Validar credenciales de login (devuelve rol y datos del usuario)
SELECT 
    u.id_usuario,
    u.nombre_usuario,
    u.contrasena_hash,
    r.nombre AS rol,
    r.id_rol
FROM usuario u
INNER JOIN rol r ON u.id_rol = r.id_rol
WHERE u.nombre_usuario = 'admin_sistema';

-- Buscar usuario por nombre de usuario
SELECT id_usuario, nombre_usuario, id_rol, id_personal
FROM usuario
WHERE nombre_usuario = 'gestor_01';

-- Verificar si un nombre de usuario ya existe
SELECT COUNT(*) AS existe
FROM usuario
WHERE nombre_usuario = 'nuevo_usuario';

-- Insertar usuario vinculado a un rol (sin personal)
INSERT INTO usuario (nombre_usuario, contrasena_hash, id_rol)
VALUES ('auditor_01', 'hash_aquí', 3)
RETURNING id_usuario, nombre_usuario;

-- Insertar usuario vinculado a personal existente (subconsulta por identificación real)
INSERT INTO usuario (nombre_usuario, contrasena_hash, id_rol, id_personal)
VALUES (
    'gestor_02',
    '$2a$10$QD3zZ41a6hdJL8ULhn4Jd.lFDbh/7d7FHaZjPCwmcqhRTxsfyXj3S',
    2,
    (SELECT id_personal FROM personal WHERE numero_identificacion = '55667788')
)
ON CONFLICT (nombre_usuario) DO NOTHING
RETURNING id_usuario, nombre_usuario;

-- Actualizar contraseña (hash)
UPDATE usuario
SET contrasena_hash = 'nuevo_hash_aquí'
WHERE id_usuario = 3;

-- Cambiar rol de usuario
UPDATE usuario
SET id_rol = (SELECT id_rol FROM rol WHERE nombre = 'Administrador del sistema')
WHERE id_usuario = 3;

-- Vincular o desvincular usuario con personal
UPDATE usuario
SET id_personal = NULL  -- desvincular
WHERE id_usuario = 3;