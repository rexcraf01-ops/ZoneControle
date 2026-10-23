-- Listar resultados de ingreso
SELECT id_resultado_ingreso, nombre
FROM resultado_ingreso
ORDER BY id_resultado_ingreso;

-- Obtener ID del resultado "Autorizado"
SELECT id_resultado_ingreso
FROM resultado_ingreso
WHERE nombre = 'Autorizado';

-- Consulta de verificación de catálogo (los valores válidos están restringidos por chk_resultado_ingreso_nombre)
SELECT COUNT(*) AS total_resultados_ingreso
FROM resultado_ingreso;