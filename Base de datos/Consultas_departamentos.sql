-- Listar todos los departamentos
SELECT id_departamento, nombre, direccion
FROM departamento
ORDER BY nombre;

-- Buscar departamento por nombre exacto
SELECT id_departamento, nombre, direccion
FROM departamento
WHERE nombre = 'Sistemas';

-- Insertar departamento
INSERT INTO departamento (nombre, direccion)
VALUES ('Seguridad Física', 'Av. Laboratorio 456, Piso 1, Bloque B')
RETURNING id_departamento, nombre, direccion;

-- Actualizar nombre y dirección de departamento
UPDATE departamento
SET nombre = 'Tecnologías de la Información',
    direccion = 'Av. Innovación 100, Piso 3, Edificio Central'
WHERE id_departamento = 2;