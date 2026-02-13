-- Crear tabla saldos
CREATE TABLE IF NOT EXISTS saldos (
    id SERIAL PRIMARY KEY,
    numero_cuenta VARCHAR(50) UNIQUE NOT NULL,
    monto DECIMAL(15, 2) NOT NULL
);

-- Insertar datos de prueba
INSERT INTO saldos (numero_cuenta, monto)
VALUES
    ('123456', 1000.00),
    ('789012', 2500.50),
    ('456789', 5000.00)
ON CONFLICT (numero_cuenta) DO NOTHING;

-- Verificar
SELECT * FROM saldos;

