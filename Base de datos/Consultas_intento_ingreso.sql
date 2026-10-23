-- Registrar un intento de ingreso (FKs resueltas por subconsulta; valida que existan)
INSERT INTO intento_ingreso (numero_identificacion_ingresado, id_personal, id_resultado_ingreso, fecha_hora)
VALUES (
    '12345678',
    (SELECT id_personal FROM personal WHERE numero_identificacion = '12345678'),
    (SELECT id_resultado_ingreso FROM resultado_ingreso WHERE nombre = 'Autorizado'),
    CURRENT_TIMESTAMP
)
RETURNING id_intento, fecha_hora;

-- Registrar intento de persona NO registrada (id_personal NULL)
INSERT INTO intento_ingreso (numero_identificacion_ingresado, id_personal, id_resultado_ingreso, fecha_hora)
VALUES (
    '99999999',
    NULL,
    (SELECT id_resultado_ingreso FROM resultado_ingreso WHERE nombre = 'No registrado'),
    CURRENT_TIMESTAMP
)
RETURNING id_intento, fecha_hora;

-- Listar historial de intentos de ingreso con detalles
SELECT 
    ii.id_intento,
    ii.numero_identificacion_ingresado,
    p.nombres,
    p.apellidos,
    ri.nombre AS resultado,
    ii.fecha_hora
FROM intento_ingreso ii
LEFT JOIN personal p ON ii.id_personal = p.id_personal
INNER JOIN resultado_ingreso ri ON ii.id_resultado_ingreso = ri.id_resultado_ingreso
ORDER BY ii.fecha_hora DESC;

-- Historial de accesos de una persona específica (por número de identificación)
SELECT 
    ii.id_intento,
    ii.numero_identificacion_ingresado,
    ri.nombre AS resultado,
    ii.fecha_hora
FROM intento_ingreso ii
INNER JOIN resultado_ingreso ri ON ii.id_resultado_ingreso = ri.id_resultado_ingreso
WHERE ii.numero_identificacion_ingresado = '12345678'
   OR ii.id_personal = (SELECT id_personal FROM personal WHERE numero_identificacion = '12345678')
ORDER BY ii.fecha_hora DESC;

-- Historial filtrado por rango de fechas (para auditoría)
SELECT 
    ii.id_intento,
    ii.numero_identificacion_ingresado,
    p.nombres,
    p.apellidos,
    ri.nombre AS resultado,
    ii.fecha_hora
FROM intento_ingreso ii
LEFT JOIN personal p ON ii.id_personal = p.id_personal
INNER JOIN resultado_ingreso ri ON ii.id_resultado_ingreso = ri.id_resultado_ingreso
WHERE ii.fecha_hora >= '2026-10-18 13:06:56'
  AND ii.fecha_hora <= '2026-10-18 14:59:59'
ORDER BY ii.fecha_hora DESC;

-- Intentos de ingreso por resultado (ej. cuántos denegados)
SELECT 
    ri.nombre AS resultado,
    COUNT(ii.id_intento) AS total_intentos
FROM intento_ingreso ii
INNER JOIN resultado_ingreso ri ON ii.id_resultado_ingreso = ri.id_resultado_ingreso
GROUP BY ri.nombre
ORDER BY total_intentos DESC;

-- Últimos 10 intentos de ingreso
SELECT 
    ii.id_intento,
    ii.numero_identificacion_ingresado,
    ri.nombre AS resultado,
    ii.fecha_hora
FROM intento_ingreso ii
INNER JOIN resultado_ingreso ri ON ii.id_resultado_ingreso = ri.id_resultado_ingreso
ORDER BY ii.fecha_hora DESC
LIMIT 10;

-- Resumen de actividad de acceso por departamento (para reportes del Supervisor/Auditor)
SELECT 
    d.nombre AS departamento,
    COUNT(ii.id_intento) AS total_intentos,
    COUNT(*) FILTER (WHERE ri.nombre = 'Autorizado') AS autorizados,
    COUNT(*) FILTER (WHERE ri.nombre = 'Denegado') AS denegados,
    COUNT(*) FILTER (WHERE ri.nombre = 'No registrado') AS no_registrados
FROM intento_ingreso ii
LEFT JOIN personal p ON ii.id_personal = p.id_personal
LEFT JOIN departamento d ON p.id_departamento = d.id_departamento
INNER JOIN resultado_ingreso ri ON ii.id_resultado_ingreso = ri.id_resultado_ingreso
GROUP BY d.nombre
ORDER BY total_intentos DESC;