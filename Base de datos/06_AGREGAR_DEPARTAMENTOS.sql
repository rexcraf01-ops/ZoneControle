-- Agrega los departamentos solicitados sin duplicar los que ya existan.
-- La dirección queda como "Por definir" hasta que se proporcione la ubicación real.
INSERT INTO departamento (nombre, direccion) VALUES
    ('Operaciones', 'Por definir'),
    ('Recursos Humanos', 'Por definir'),
    ('Tecnología', 'Por definir'),
    ('Finanzas', 'Por definir'),
    ('Logística', 'Por definir'),
    ('Administración', 'Por definir')
ON CONFLICT (nombre) DO NOTHING;

-- Verifica el catálogo solicitado.
SELECT id_departamento, nombre, direccion
FROM departamento
WHERE nombre IN (
    'Operaciones',
    'Recursos Humanos',
    'Tecnología',
    'Finanzas',
    'Logística',
    'Administración'
)
ORDER BY nombre;
