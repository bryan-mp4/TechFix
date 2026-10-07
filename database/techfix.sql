-- ==============================================================================
-- Script de Creación de Base de Datos - TechFix
-- Proyecto: TechFix (Reparación de Celulares, PCs y Tablets)
-- Motor: MySQL / MariaDB (XAMPP / phpMyAdmin)
-- Juego de caracteres: utf8mb4 (Soporta caracteres especiales y emojis)
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS `techfix`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `techfix`;

-- ------------------------------------------------------------------------------
-- 1. Tabla: usuarios
-- Almacena los clientes, técnicos y administradores del sistema.
-- Las contraseñas NUNCA se guardan en texto plano, solo su hash.
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `usuarios` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `nombre` VARCHAR(100) NOT NULL,
    `email` VARCHAR(150) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `telefono` VARCHAR(20) NOT NULL,
    `rol` ENUM('cliente', 'tecnico', 'admin') NOT NULL DEFAULT 'cliente',
    `fecha_registro` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_usuarios_email` (`email`),
    INDEX `idx_usuarios_rol` (`rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 2. Tabla: servicios
-- Catálogo de servicios ofrecidos por el taller con sus precios base.
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `servicios` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `nombre` VARCHAR(120) NOT NULL,
    `descripcion` TEXT NOT NULL,
    `tipo_dispositivo` ENUM('celular', 'pc', 'tablet', 'todos') NOT NULL DEFAULT 'todos',
    `precio_base` DECIMAL(10, 2) NOT NULL,
    `icono` VARCHAR(50) NOT NULL DEFAULT 'ic_repair',
    `activo` TINYINT(1) NOT NULL DEFAULT 1,
    INDEX `idx_servicios_activo_tipo` (`activo`, `tipo_dispositivo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 3. Tabla: dispositivos
-- Equipos registrados por los clientes para solicitar reparaciones.
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `dispositivos` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `usuario_id` INT NOT NULL,
    `tipo` ENUM('celular', 'pc', 'tablet') NOT NULL,
    `marca` VARCHAR(50) NOT NULL,
    `modelo` VARCHAR(50) NOT NULL,
    INDEX `idx_dispositivos_usuario` (`usuario_id`),
    CONSTRAINT `fk_dispositivos_usuario` FOREIGN KEY (`usuario_id`)
        REFERENCES `usuarios` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 4. Tabla: ordenes
-- Solicitudes de reparación realizadas por los clientes.
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `ordenes` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `usuario_id` INT NOT NULL,
    `dispositivo_id` INT NOT NULL,
    `servicio_id` INT NOT NULL,
    `descripcion_falla` TEXT NOT NULL,
    `estado` ENUM('recibido', 'diagnostico', 'en_reparacion', 'listo', 'entregado') NOT NULL DEFAULT 'recibido',
    `costo_final` DECIMAL(10, 2) NULL DEFAULT NULL,
    `tecnico_id` INT NULL DEFAULT NULL,
    `fecha_creacion` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_ordenes_usuario` (`usuario_id`),
    INDEX `idx_ordenes_tecnico` (`tecnico_id`),
    INDEX `idx_ordenes_estado` (`estado`),
    CONSTRAINT `fk_ordenes_usuario` FOREIGN KEY (`usuario_id`)
        REFERENCES `usuarios` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_ordenes_dispositivo` FOREIGN KEY (`dispositivo_id`)
        REFERENCES `dispositivos` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_ordenes_servicio` FOREIGN KEY (`servicio_id`)
        REFERENCES `servicios` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_ordenes_tecnico` FOREIGN KEY (`tecnico_id`)
        REFERENCES `usuarios` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 5. Tabla: historial_estados
-- Seguimiento del progreso y trazabilidad de cada orden de servicio.
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `historial_estados` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `orden_id` INT NOT NULL,
    `estado` ENUM('recibido', 'diagnostico', 'en_reparacion', 'listo', 'entregado') NOT NULL,
    `comentario` TEXT NULL,
    `fecha` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_historial_orden` (`orden_id`),
    CONSTRAINT `fk_historial_orden` FOREIGN KEY (`orden_id`)
        REFERENCES `ordenes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 6. Tabla: citas
-- Agendamiento de citas presenciales en el taller.
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `citas` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `usuario_id` INT NOT NULL,
    `orden_id` INT NULL DEFAULT NULL,
    `fecha_hora` DATETIME NOT NULL,
    `estado` ENUM('pendiente', 'confirmada', 'cancelada', 'completada') NOT NULL DEFAULT 'pendiente',
    INDEX `idx_citas_usuario` (`usuario_id`),
    INDEX `idx_citas_orden` (`orden_id`),
    CONSTRAINT `fk_citas_usuario` FOREIGN KEY (`usuario_id`)
        REFERENCES `usuarios` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_citas_orden` FOREIGN KEY (`orden_id`)
        REFERENCES `ordenes` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 7. Tabla: valoraciones
-- Calificación del servicio recibido una vez entregada la orden.
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `valoraciones` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `orden_id` INT NOT NULL UNIQUE,
    `estrellas` INT NOT NULL CHECK (`estrellas` BETWEEN 1 AND 5),
    `comentario` TEXT NULL,
    INDEX `idx_valoraciones_orden` (`orden_id`),
    CONSTRAINT `fk_valoraciones_orden` FOREIGN KEY (`orden_id`)
        REFERENCES `ordenes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- DATOS INICIALES DE PRUEBA: Servicios (Precios en Pesos Colombianos - COP)
-- Se incluyen los 8 servicios requeridos con estado activo (1).
-- ------------------------------------------------------------------------------
INSERT INTO `servicios` (`nombre`, `descripcion`, `tipo_dispositivo`, `precio_base`, `icono`, `activo`) VALUES
('Cambio de Pantalla', 'Reemplazo completo de módulo display táctil por repuesto original o de alta calidad.', 'celular', 180000.00, 'ic_screen', 1),
('Cambio de Batería', 'Sustitución de batería desgastada por una nueva con calibración de carga.', 'celular', 95000.00, 'ic_battery', 1),
('Formateo e Instalación de OS', 'Instalación limpia de sistema operativo (Windows, macOS, Android) con drivers y programas básicos.', 'pc', 120000.00, 'ic_computer', 1),
('Limpieza Interna y Mantenimiento', 'Limpieza profunda de polvo, cambio de pasta térmica de alto rendimiento y optimización de ventilación.', 'pc', 85000.00, 'ic_clean', 1),
('Recuperación de Datos', 'Diagnóstico y extracción de información de discos duros, SSDs o memorias dañadas.', 'todos', 220000.00, 'ic_data', 1),
('Eliminación de Virus y Malware', 'Análisis profundo, desinfección del sistema, eliminación de software malicioso y optimización.', 'pc', 70000.00, 'ic_security', 1),
('Cambio de Puerto de Carga', 'Reparación o sustitución del conector de carga (Pin/Type-C/Lightning).', 'celular', 75000.00, 'ic_usb', 1),
('Mantenimiento Preventivo Tablet', 'Revisión general de hardware, limpieza de puerto de carga, prueba de batería y optimización de software.', 'tablet', 65000.00, 'ic_build', 1);
