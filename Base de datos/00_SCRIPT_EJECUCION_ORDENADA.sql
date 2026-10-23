-- ============================================================================
-- SCRIPT MAESTRO DE EJECUCIÓN ORDENADA - Zone Control (Laboratorio XYZ)
-- Base de datos: PostgreSQL
-- Ubicación: Base de datos\00_SCRIPT_EJECUCION_ORDENADA.sql
-- ============================================================================
-- Este archivo ejecuta los scripts en el orden correcto para evitar errores
-- de claves foráneas y datos faltantes.
--
-- IMPORTANTE: Ejecutar con psql desde la carpeta "Base de datos".
-- ============================================================================

-- PASO 1: Crear tablas y cargar datos semilla (roles, estados, resultados, departamentos, personal, usuarios, intentos)
\i ZoneControl_BD.sql

-- PASO 2: Crear tabla de sesiones (autenticación en servidor)
\i 01_TABLA_SESION.sql

-- PASO 3: Crear tabla de exportaciones de archivos de intercambio
\i 02_TABLA_EXPORTACION.sql

-- PASO 4: Agregar destinatario al historial de exportaciones (incluye bases existentes)
\i 07_DESTINATARIO_EXPORTACION.sql

-- PASO 5: Campos y tokens para recuperación de contraseña
\i 04_RECUPERACION_CONTRASENA.sql

-- PASO 6: Eliminar tabla histórica de solicitudes de registro si existía
\i 05_ELIMINAR_SOLICITUD_REGISTRO.sql

-- PASO 7: Consultas y mantenimiento de catálogos
\i Consultas_estado_autorizacion.sql
\i Consultas_departamentos.sql
\i Consultas_rol.sql

-- PASO 8: Consultas y mantenimiento de tablas transaccionales
\i Consultas_personal.sql
\i Consultas_usuario.sql
\i Consultas_intento_ingreso.sql
\i Consultas_resultado_ingreso.sql

-- ============================================================================
-- FIN DEL SCRIPT MAESTRO
-- ============================================================================