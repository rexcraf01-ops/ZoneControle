-- Listar todos los roles activos
SELECT id_rol, nombre
FROM rol
ORDER BY nombre;

-- Buscar un rol por nombre
SELECT id_rol, nombre
FROM rol
WHERE nombre ILIKE '%Administrador%';

-- Insertar un nuevo rol
INSERT INTO rol (nombre)
VALUES ('Nuevo Rol')
RETURNING id_rol, nombre;

-- Actualizar el nombre de un rol
UPDATE rol
SET nombre = 'XD'
WHERE id_rol = 4;

-- Eliminar un rol (ejemplo con ID no usado en datos semilla; 9999 no existe)
DELETE FROM rol WHERE id_rol = 9999;