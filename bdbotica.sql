-- 1. CREACIÓN DE LA BASE DE DATOS
CREATE DATABASE IF NOT EXISTS boticaintegrador2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE boticaintegrador2;

-- 2. TABLA DE USUARIOS (HU-01, HU-13)
-- Soporta los roles (Admin, Farmacéutico) y el control de acceso
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(150) NOT NULL,
    rol VARCHAR(50) NOT NULL, -- Ej: 'ADMIN', 'PHARMACIST'
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. TABLA DE CATEGORÍAS (HU-06)
-- Para clasificar los medicamentos (Analgésicos, Antibióticos, etc.)
CREATE TABLE categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. TABLA DE MEDICAMENTOS (HU-02, HU-03, HU-04, HU-05, HU-07, HU-08)
CREATE TABLE medicamentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    categoria_id BIGINT NOT NULL,
    forma_farmaceutica VARCHAR(100), -- Ej: Tabletas 500mg
    descripcion TEXT,
    proveedor VARCHAR(150),
    stock INT NOT NULL DEFAULT 0,
    nivel_minimo INT NOT NULL DEFAULT 20, -- Fundamental para alerta de Stock Bajo (HU-07)
    precio DECIMAL(10,2) NOT NULL DEFAULT 0.00, -- Necesario para las ventas
    ubicacion_estante VARCHAR(50),
    fecha_vencimiento DATE NOT NULL,      -- Fundamental para alerta de Caducidad (HU-08)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT
);

-- 5. TABLA DE PACIENTES / CLIENTES 
CREATE TABLE pacientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni VARCHAR(20) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    fecha_nacimiento DATE,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    genero VARCHAR(20),
    nombre_madre VARCHAR(100),
    nombre_padre VARCHAR(100),
    dni_madre VARCHAR(20),
    dni_padre VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 6. TABLA DE VENTAS (HU-09, HU-10, HU-14)
-- Registra la cabecera de la venta, quién la hizo y a quién
CREATE TABLE ventas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL, -- Quien registró la venta (HU-18)
    paciente_id BIGINT NULL,    -- Opcional, si el cliente está registrado
    cliente_anonimo_nombre VARCHAR(150), -- Por si es una venta rápida sin registrar cliente
    metodo_pago VARCHAR(50),    -- Efectivo, Tarjeta, etc.
    total DECIMAL(10,2) NOT NULL,
    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id)
);

-- 7. TABLA DE DETALLE DE VENTAS (HU-09, HU-11)
-- Registra qué medicamentos exactos se llevaron en cada venta
CREATE TABLE detalle_ventas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id BIGINT NOT NULL,
    medicamento_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE,
    FOREIGN KEY (medicamento_id) REFERENCES medicamentos(id) ON DELETE RESTRICT
);

-- 8. INSERCIÓN DE DATOS INICIALES
-- El password insertado es el hash BCrypt del primer script que compartiste.
INSERT INTO usuarios (username, password, nombre_completo, rol) 
VALUES ('admin', '$2a$10$B8c1tXJAKxvCvbBG7ImJu.gyCKPtzNkehMDnBjyn19NhWRj5pls5m', 'Administrador', 'ADMIN');

INSERT INTO categorias (nombre, descripcion) VALUES 
('Analgésicos', 'Medicinas que reducen o alivian los dolores'),
('Antibióticos', 'Fármacos que matan o detienen el crecimiento de bacterias');