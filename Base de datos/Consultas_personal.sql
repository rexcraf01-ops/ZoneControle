-- Listar personal con departamento y estado de autorización
SELECT 
    p.id_personal,
    p.numero_identificacion,
    p.nombres,
    p.apellidos,
    d.nombre AS departamento,
    ea.nombre AS estado_autorizacion
FROM personal p
INNER JOIN departamento d ON p.id_departamento = d.id_departamento
INNER JOIN estado_autorizacion ea ON p.id_estado_autorizacion = ea.id_estado_autorizacion
ORDER BY p.apellidos, p.nombres;

-- Buscar personal por número de identificación
SELECT id_personal, numero_identificacion, nombres, apellidos, id_departamento, id_estado_autorizacion
FROM personal
WHERE numero_identificacion = '12345678';

-- Buscar personal por nombre o apellido
SELECT id_personal, numero_identificacion, nombres, apellidos
FROM personal
WHERE nombres ILIKE '%juan%' OR apellidos ILIKE '%perez%'
ORDER BY apellidos, nombres;

-- Listar personal por departamento
SELECT p.id_personal, p.numero_identificacion, p.nombres, p.apellidos
FROM personal p
INNER JOIN departamento d ON p.id_departamento = d.id_departamento
WHERE d.nombre = 'Sistemas'
ORDER BY p.apellidos;

-- Listar personal por estado de autorización (ej. solo los con autorización Otorgada)
SELECT p.id_personal, p.numero_identificacion, p.nombres, p.apellidos
FROM personal p
INNER JOIN estado_autorizacion ea ON p.id_estado_autorizacion = ea.id_estado_autorizacion
WHERE ea.nombre = 'Otorgada'
ORDER BY p.apellidos, p.nombres;

-- Insertar personal (FKs resueltas por subconsulta por nombre, no por IDs asumidos)
INSERT INTO personal (numero_identificacion, nombres, apellidos, id_departamento, id_estado_autorizacion)
VALUES (
    '87654321',
    'María',
    'García López',
    (SELECT id_departamento FROM departamento WHERE nombre = 'Sistemas'),
    (SELECT id_estado_autorizacion FROM estado_autorizacion WHERE nombre = 'Otorgada')
)
ON CONFLICT (numero_identificacion) DO NOTHING
RETURNING id_personal, numero_identificacion;

-- Actualizar datos de personal
UPDATE personal
SET 
    id_departamento = 2,
    id_estado_autorizacion = 1
WHERE id_personal = 5;

-- Cambiar estado de autorización de personal (revocar/suspender)
UPDATE personal
SET id_estado_autorizacion = (SELECT id_estado_autorizacion FROM estado_autorizacion WHERE nombre = 'Revocada')
WHERE id_personal = 5;