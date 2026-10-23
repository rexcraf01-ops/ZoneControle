-- Listar estados de autorización
SELECT id_estado_autorizacion, nombre
FROM estado_autorizacion
ORDER BY nombre;

-- Obtener el ID de un estado por nombre (útil para inserts en personal)
SELECT id_estado_autorizacion
FROM estado_autorizacion
WHERE nombre = 'Otorgada';

-- Consulta de verificación de catálogo (los valores permitidos están restringidos por chk_estado_autorizacion_nombre)
SELECT COUNT(*) AS total_estados_autorizacion
FROM estado_autorizacion;