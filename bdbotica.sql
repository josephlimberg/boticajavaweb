-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 20-06-2026 a las 05:06:55
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `boticaintegrador2`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categorias`
--

CREATE TABLE `categorias` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `categorias`
--

INSERT INTO `categorias` (`id`, `nombre`, `descripcion`, `created_at`) VALUES
(1, 'Analgésicos', 'Medicinas que reducen o alivian los dolores', '2026-05-14 01:46:03'),
(2, 'Antibióticos', 'Fármacos que matan o detienen el crecimiento de bacterias', '2026-05-14 01:46:03'),
(3, 'Antiinflamatorios', 'Reducen la inflamación y el dolor', '2026-05-14 02:01:45'),
(4, 'Antihistamínicos', 'Combaten alergias y reacciones alérgicas', '2026-05-14 02:01:45'),
(5, 'Antivirales', 'Tratamiento de infecciones virales', '2026-05-14 02:01:45'),
(6, 'Antifúngicos', 'Tratamiento de infecciones por hongos', '2026-05-14 02:01:45'),
(7, 'Vitaminas y Suplementos', 'Suplementos nutricionales y vitaminas', '2026-05-14 02:01:45'),
(8, 'Cardiovasculares', 'Medicamentos para el corazón y circulación', '2026-05-14 02:01:45'),
(9, 'Gastrointestinales', 'Tratamiento del sistema digestivo', '2026-05-14 02:01:45'),
(10, 'Respiratorios', 'Medicamentos para vías respiratorias', '2026-05-14 02:01:45');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_ventas`
--

CREATE TABLE `detalle_ventas` (
  `id` bigint(20) NOT NULL,
  `venta_id` bigint(20) NOT NULL,
  `medicamento_id` bigint(20) NOT NULL,
  `lote_id` bigint(20) DEFAULT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `detalle_ventas`
--

INSERT INTO `detalle_ventas` (`id`, `venta_id`, `medicamento_id`, `lote_id`, `cantidad`, `precio_unitario`, `subtotal`) VALUES
(1, 1, 17, NULL, 3, 5.00, 15.00),
(2, 1, 2, NULL, 2, 28.90, 57.80),
(3, 2, 17, NULL, 1, 5.00, 5.00),
(4, 2, 2, NULL, 1, 28.90, 28.90),
(5, 3, 16, NULL, 1, 2.50, 2.50),
(6, 3, 17, NULL, 1, 5.00, 5.00),
(7, 3, 2, NULL, 1, 28.90, 28.90),
(8, 4, 17, NULL, 1, 5.00, 5.00),
(9, 5, 17, NULL, 1, 5.00, 5.00),
(10, 6, 17, NULL, 1, 5.00, 5.00),
(11, 7, 17, NULL, 1, 5.00, 5.00),
(12, 8, 17, NULL, 1, 5.00, 5.00),
(13, 9, 17, NULL, 1, 5.00, 5.00),
(14, 10, 17, NULL, 1, 5.00, 5.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `historial_acciones`
--

CREATE TABLE `historial_acciones` (
  `id` bigint(20) NOT NULL,
  `usuario_id` bigint(20) NOT NULL,
  `tipo_accion` varchar(50) NOT NULL,
  `fecha_hora` datetime NOT NULL,
  `descripcion` text DEFAULT NULL,
  `modulo` varchar(50) DEFAULT NULL,
  `detalles` text DEFAULT NULL,
  `ip_origen` varchar(50) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `historial_acciones`
--

INSERT INTO `historial_acciones` (`id`, `usuario_id`, `tipo_accion`, `fecha_hora`, `descripcion`, `modulo`, `detalles`, `ip_origen`, `created_at`) VALUES
(1, 1, 'SISTEMA', '2026-06-19 20:24:31', 'Inicio de sesión exitoso desde terminal ADMIN-PC-01', 'Seguridad', 'IP: 127.0.0.1', NULL, '2026-06-20 01:24:31'),
(2, 1, 'CREACIÓN', '2026-06-19 19:24:31', 'Registró nuevo medicamento: Paracetamol 500mg', 'Inventario', 'Lote: PAR-001-24, Cantidad: 100', NULL, '2026-06-20 01:24:31'),
(3, 1, 'MODIFICACIÓN', '2026-06-19 18:24:31', 'Actualizó precio de Amoxicilina 500mg de S/ 35.00 a S/ 38.50', 'Precios', 'ID Medicamento: 3', NULL, '2026-06-20 01:24:31'),
(4, 1, 'ELIMINACIÓN', '2026-06-19 17:24:31', 'Eliminó 12 unidades de Paracetamol 500mg (Lote: PX-902)', 'Inventario', 'Motivo: Vencimiento próximo', NULL, '2026-06-20 01:24:31'),
(5, 1, 'SISTEMA', '2026-06-19 21:25:30', 'Visualizó el panel de inicio', 'Dashboard', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:25:30'),
(6, 1, 'SISTEMA', '2026-06-19 21:25:32', 'Cerró sesión del sistema', 'Seguridad', 'Logout exitoso', '0:0:0:0:0:0:0:1', '2026-06-20 02:25:32'),
(7, 1, 'SISTEMA', '2026-06-19 21:25:39', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:25:39'),
(8, 1, 'SISTEMA', '2026-06-19 21:25:48', 'Visualizó lista de pacientes', 'Pacientes', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:25:48'),
(9, 1, 'SISTEMA', '2026-06-19 21:25:49', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:25:49'),
(10, 1, 'SISTEMA', '2026-06-19 21:25:57', 'Visualizó lista de usuarios', 'Usuarios', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:25:57'),
(11, 1, 'SISTEMA', '2026-06-19 21:25:59', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null', '0:0:0:0:0:0:0:1', '2026-06-20 02:25:59'),
(12, 1, 'SISTEMA', '2026-06-19 21:25:59', 'Visualizó lista de usuarios', 'Usuarios', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:25:59'),
(13, 1, 'SISTEMA', '2026-06-19 21:26:02', 'Visualizó lista de pacientes', 'Pacientes', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:26:02'),
(14, 1, 'SISTEMA', '2026-06-19 21:26:16', 'Visualizó lista de pacientes', 'Pacientes', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:26:16'),
(15, 1, 'CREACIÓN', '2026-06-19 21:26:41', 'Creó nuevo paciente: Alejandro Paredes', 'Pacientes', 'DNI: 12332121, Teléfono: 981232123', '0:0:0:0:0:0:0:1', '2026-06-20 02:26:41'),
(16, 1, 'SISTEMA', '2026-06-19 21:26:41', 'Visualizó lista de pacientes', 'Pacientes', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:26:41'),
(17, 1, 'SISTEMA', '2026-06-19 21:26:46', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:26:46'),
(18, 1, 'SISTEMA', '2026-06-19 21:28:46', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:28:46'),
(19, 1, 'SISTEMA', '2026-06-19 21:29:02', 'Visualizó el panel de inicio', 'Dashboard', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:29:02'),
(20, 1, 'SISTEMA', '2026-06-19 21:29:08', 'Visualizó el panel de inicio', 'Dashboard', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:29:08'),
(21, 1, 'SISTEMA', '2026-06-19 21:29:11', 'Visualizó pantalla de ventas', 'Ventas', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:29:11'),
(22, 1, 'SISTEMA', '2026-06-19 21:29:16', 'Visualizó pantalla de ventas', 'Ventas', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:29:16'),
(23, 1, 'SISTEMA', '2026-06-19 21:29:19', 'Visualizó ventas diarias', 'Ventas', 'Fecha: null', '0:0:0:0:0:0:0:1', '2026-06-20 02:29:19'),
(24, 1, 'SISTEMA', '2026-06-19 21:29:54', 'Visualizó pantalla de ventas', 'Ventas', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:29:54'),
(25, 1, 'SISTEMA', '2026-06-19 21:29:56', 'Visualizó el panel de inicio', 'Dashboard', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:29:56'),
(26, 1, 'SISTEMA', '2026-06-19 21:31:11', 'Visualizó pantalla de ventas', 'Ventas', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:31:11'),
(27, 1, 'SISTEMA', '2026-06-19 21:31:22', 'Visualizó pantalla de ventas', 'Ventas', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:31:22'),
(28, 1, 'SISTEMA', '2026-06-19 21:31:23', 'Visualizó ventas diarias', 'Ventas', 'Fecha: null', '0:0:0:0:0:0:0:1', '2026-06-20 02:31:23'),
(29, 1, 'SISTEMA', '2026-06-19 21:31:37', 'Visualizó ventas diarias', 'Ventas', 'Fecha: null', '0:0:0:0:0:0:0:1', '2026-06-20 02:31:37'),
(30, 1, 'SISTEMA', '2026-06-19 21:31:42', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null', '0:0:0:0:0:0:0:1', '2026-06-20 02:31:42'),
(31, 1, 'SISTEMA', '2026-06-19 21:31:48', 'Visualizó pantalla de ventas', 'Ventas', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:31:48'),
(32, 1, 'SISTEMA', '2026-06-19 21:31:49', 'Visualizó ventas diarias', 'Ventas', 'Fecha: null', '0:0:0:0:0:0:0:1', '2026-06-20 02:31:49'),
(33, 1, 'SISTEMA', '2026-06-19 21:31:50', 'Visualizó ventas diarias', 'Ventas', 'Fecha: null', '0:0:0:0:0:0:0:1', '2026-06-20 02:31:50'),
(34, 1, 'SISTEMA', '2026-06-19 21:31:51', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null', '0:0:0:0:0:0:0:1', '2026-06-20 02:31:51'),
(35, 1, 'SISTEMA', '2026-06-19 21:32:02', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null', '0:0:0:0:0:0:0:1', '2026-06-20 02:32:02'),
(36, 1, 'SISTEMA', '2026-06-19 21:32:12', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null', '0:0:0:0:0:0:0:1', '2026-06-20 02:32:12'),
(37, 1, 'SISTEMA', '2026-06-19 21:32:16', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null', '0:0:0:0:0:0:0:1', '2026-06-20 02:32:16'),
(38, 1, 'SISTEMA', '2026-06-19 21:32:57', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null', '0:0:0:0:0:0:0:1', '2026-06-20 02:32:57'),
(39, 1, 'SISTEMA', '2026-06-19 21:33:52', 'Visualizó lista de usuarios', 'Usuarios', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:33:52'),
(40, 1, 'SISTEMA', '2026-06-19 21:34:30', 'Visualizó lista de usuarios', 'Usuarios', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:34:30'),
(41, 1, 'SISTEMA', '2026-06-19 21:34:32', 'Visualizó lista de usuarios', 'Usuarios', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:34:32'),
(42, 1, 'SISTEMA', '2026-06-19 21:34:35', 'Visualizó lista de usuarios', 'Usuarios', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:34:35'),
(43, 1, 'SISTEMA', '2026-06-19 21:34:37', 'Visualizó lista de pacientes', 'Pacientes', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:34:37'),
(44, 1, 'SISTEMA', '2026-06-19 21:35:05', 'Visualizó lista de pacientes', 'Pacientes', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:35:05'),
(45, 1, 'SISTEMA', '2026-06-19 21:35:06', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:35:06'),
(46, 1, 'SISTEMA', '2026-06-19 21:35:19', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:35:19'),
(47, 1, 'SISTEMA', '2026-06-19 21:35:37', 'Visualizó el panel de inicio', 'Dashboard', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:35:37'),
(48, 1, 'SISTEMA', '2026-06-19 21:35:39', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:35:39'),
(49, 1, 'SISTEMA', '2026-06-19 21:35:40', 'Visualizó lista de pacientes', 'Pacientes', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:35:40'),
(50, 1, 'SISTEMA', '2026-06-19 21:35:41', 'Visualizó lista de usuarios', 'Usuarios', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:35:41'),
(51, 1, 'SISTEMA', '2026-06-19 21:35:42', 'Visualizó lista de pacientes', 'Pacientes', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:35:42'),
(52, 1, 'SISTEMA', '2026-06-19 21:35:43', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:35:43'),
(53, 1, 'SISTEMA', '2026-06-19 21:35:59', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:35:59'),
(54, 1, 'SISTEMA', '2026-06-19 21:36:00', 'Visualizó lista de pacientes', 'Pacientes', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:36:00'),
(55, 1, 'SISTEMA', '2026-06-19 21:36:00', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:36:00'),
(56, 1, 'SISTEMA', '2026-06-19 21:36:15', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:36:15'),
(57, 1, 'SISTEMA', '2026-06-19 21:36:16', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:36:16'),
(58, 1, 'SISTEMA', '2026-06-19 21:36:17', 'Visualizó lista de pacientes', 'Pacientes', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:36:17'),
(59, 1, 'SISTEMA', '2026-06-19 21:36:18', 'Visualizó lista de usuarios', 'Usuarios', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:36:18'),
(60, 1, 'SISTEMA', '2026-06-19 21:36:19', 'Visualizó lista de pacientes', 'Pacientes', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:36:19'),
(61, 1, 'SISTEMA', '2026-06-19 21:36:20', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:36:20'),
(62, 1, 'SISTEMA', '2026-06-19 21:36:25', 'Visualizó el panel de inicio', 'Dashboard', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:36:25'),
(63, 1, 'SISTEMA', '2026-06-19 21:36:33', 'Visualizó el panel de inicio', 'Dashboard', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:36:33'),
(64, 1, 'SISTEMA', '2026-06-19 21:36:41', 'Visualizó el panel de inicio', 'Dashboard', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:36:41'),
(65, 1, 'SISTEMA', '2026-06-19 21:42:47', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:42:47'),
(66, 1, 'SISTEMA', '2026-06-19 21:43:00', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:43:00'),
(67, 1, 'SISTEMA', '2026-06-19 21:43:30', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:43:30'),
(68, 1, 'SISTEMA', '2026-06-19 21:43:47', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:43:47'),
(69, 1, 'SISTEMA', '2026-06-19 21:43:55', 'Visualizó el panel de inicio', 'Dashboard', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:43:55'),
(70, 1, 'SISTEMA', '2026-06-19 21:43:56', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:43:56'),
(71, 1, 'SISTEMA', '2026-06-19 21:45:21', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:45:21'),
(72, 1, 'SISTEMA', '2026-06-19 21:45:22', 'Visualizó el panel de inicio', 'Dashboard', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:45:22'),
(73, 1, 'SISTEMA', '2026-06-19 21:45:29', 'Visualizó el panel de inicio', 'Dashboard', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:45:29'),
(74, 1, 'SISTEMA', '2026-06-19 21:46:49', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:46:49'),
(75, 1, 'SISTEMA', '2026-06-19 21:46:51', 'Visualizó el panel de inicio', 'Dashboard', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:46:51'),
(76, 1, 'SISTEMA', '2026-06-19 21:46:54', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:46:54'),
(77, 1, 'SISTEMA', '2026-06-19 21:46:56', 'Visualizó el panel de inicio', 'Dashboard', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:46:56'),
(78, 1, 'SISTEMA', '2026-06-19 21:46:57', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:46:57'),
(79, 1, 'SISTEMA', '2026-06-19 21:46:59', 'Visualizó el panel de inicio', 'Dashboard', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:46:59'),
(80, 1, 'SISTEMA', '2026-06-19 21:47:00', 'Visualizó lista de pacientes', 'Pacientes', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:47:00'),
(81, 1, 'SISTEMA', '2026-06-19 21:47:37', 'Visualizó pantalla de ventas', 'Ventas', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:47:37'),
(82, 1, 'SISTEMA', '2026-06-19 21:49:06', 'Visualizó lista de pacientes', 'Pacientes', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:49:06'),
(83, 1, 'SISTEMA', '2026-06-19 21:52:52', 'Visualizó pantalla de ventas', 'Ventas', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:52:52'),
(84, 1, 'SISTEMA', '2026-06-19 21:52:53', 'Visualizó el inventario', 'Inventario', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:52:53'),
(85, 1, 'SISTEMA', '2026-06-19 21:52:55', 'Visualizó el panel de inicio', 'Dashboard', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:52:55'),
(86, 1, 'SISTEMA', '2026-06-19 21:52:56', 'Visualizó el inventario', 'Inventario', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:52:56'),
(87, 1, 'SISTEMA', '2026-06-19 21:52:57', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:52:57'),
(88, 1, 'SISTEMA', '2026-06-19 21:53:00', 'Visualizó pantalla de ventas', 'Ventas', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:53:00'),
(89, 1, 'SISTEMA', '2026-06-19 21:53:34', 'Visualizó pantalla de ventas', 'Ventas', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:53:34'),
(90, 1, 'SISTEMA', '2026-06-19 21:53:36', 'Visualizó ventas diarias', 'Ventas', 'Fecha: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:53:36'),
(91, 1, 'SISTEMA', '2026-06-19 21:53:52', 'Visualizó ventas diarias', 'Ventas', 'Fecha: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:53:52'),
(92, 1, 'SISTEMA', '2026-06-19 21:54:00', 'Visualizó ventas diarias', 'Ventas', 'Fecha: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:54:00'),
(93, 1, 'SISTEMA', '2026-06-19 21:54:02', 'Visualizó pantalla de ventas', 'Ventas', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:54:02'),
(94, 1, 'SISTEMA', '2026-06-19 21:54:03', 'Visualizó el inventario', 'Inventario', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:54:03'),
(95, 1, 'SISTEMA', '2026-06-19 21:54:04', 'Visualizó el panel de inicio', 'Dashboard', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:54:04'),
(96, 1, 'SISTEMA', '2026-06-19 21:54:06', 'Visualizó ventas diarias', 'Ventas', 'Fecha: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:54:06'),
(97, 1, 'SISTEMA', '2026-06-19 21:54:07', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:54:07'),
(98, 1, 'SISTEMA', '2026-06-19 21:54:37', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:54:37'),
(99, 1, 'SISTEMA', '2026-06-19 21:54:44', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:54:44'),
(100, 1, 'SISTEMA', '2026-06-19 21:54:46', 'Visualizó ventas diarias', 'Ventas', 'Fecha: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:54:46'),
(101, 1, 'SISTEMA', '2026-06-19 21:54:46', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:54:46'),
(102, 1, 'SISTEMA', '2026-06-19 21:55:05', 'Visualizó el inventario', 'Inventario', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:55:05'),
(103, 1, 'SISTEMA', '2026-06-19 21:55:09', 'Visualizó el inventario', 'Inventario', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:55:09'),
(104, 1, 'SISTEMA', '2026-06-19 21:55:23', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:55:23'),
(105, 1, 'SISTEMA', '2026-06-19 21:55:27', 'Visualizó lista de usuarios', 'Usuarios', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:55:27'),
(106, 1, 'SISTEMA', '2026-06-19 21:55:38', 'Visualizó lista de usuarios', 'Usuarios', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:55:38'),
(107, 1, 'SISTEMA', '2026-06-19 21:55:40', 'Visualizó lista de pacientes', 'Pacientes', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:55:40'),
(108, 1, 'SISTEMA', '2026-06-19 21:55:41', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:55:41'),
(109, 1, 'SISTEMA', '2026-06-19 21:55:42', 'Visualizó lista de pacientes', 'Pacientes', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:55:42'),
(110, 1, 'SISTEMA', '2026-06-19 21:55:51', 'Visualizó lista de pacientes', 'Pacientes', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:55:51'),
(111, 1, 'SISTEMA', '2026-06-19 21:55:52', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:55:52'),
(112, 1, 'SISTEMA', '2026-06-19 21:56:02', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:56:02'),
(113, 1, 'SISTEMA', '2026-06-19 21:56:08', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:56:08'),
(114, 1, 'SISTEMA', '2026-06-19 21:56:51', 'Visualizó el panel de inicio', 'Dashboard', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:56:51'),
(115, 1, 'SISTEMA', '2026-06-19 21:56:52', 'Visualizó el inventario', 'Inventario', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:56:52'),
(116, 1, 'SISTEMA', '2026-06-19 21:56:53', 'Visualizó pantalla de ventas', 'Ventas', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:56:53'),
(117, 1, 'SISTEMA', '2026-06-19 21:56:54', 'Visualizó ventas diarias', 'Ventas', 'Fecha: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:56:54'),
(118, 1, 'SISTEMA', '2026-06-19 21:56:55', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:56:55'),
(119, 1, 'SISTEMA', '2026-06-19 21:56:55', 'Visualizó lista de usuarios', 'Usuarios', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:56:55'),
(120, 1, 'SISTEMA', '2026-06-19 21:56:56', 'Visualizó lista de pacientes', 'Pacientes', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:56:56'),
(121, 1, 'SISTEMA', '2026-06-19 21:56:57', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:56:57'),
(122, 1, 'SISTEMA', '2026-06-19 21:56:59', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:56:59'),
(123, 1, 'SISTEMA', '2026-06-19 21:57:00', 'Visualizó pantalla de ventas', 'Ventas', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:57:00'),
(124, 1, 'SISTEMA', '2026-06-19 21:57:05', 'Visualizó ventas diarias', 'Ventas', 'Fecha: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:57:05'),
(125, 1, 'SISTEMA', '2026-06-19 21:57:07', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:57:07'),
(126, 1, 'SISTEMA', '2026-06-19 21:57:08', 'Visualizó lista de usuarios', 'Usuarios', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:57:08'),
(127, 1, 'SISTEMA', '2026-06-19 21:57:09', 'Visualizó lista de pacientes', 'Pacientes', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:57:09'),
(128, 1, 'SISTEMA', '2026-06-19 21:57:09', 'Visualizó lista de usuarios', 'Usuarios', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 02:57:09'),
(129, 1, 'SISTEMA', '2026-06-19 21:57:11', 'Visualizó el inventario', 'Inventario', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 02:57:11'),
(130, 1, 'SISTEMA', '2026-06-19 22:00:41', 'Visualizó pantalla de ventas', 'Ventas', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:00:41'),
(131, 1, 'SISTEMA', '2026-06-19 22:00:43', 'Visualizó ventas diarias', 'Ventas', 'Fecha: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:00:43'),
(132, 1, 'SISTEMA', '2026-06-19 22:00:52', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:00:52'),
(133, 1, 'SISTEMA', '2026-06-19 22:01:00', 'Visualizó lista de usuarios', 'Usuarios', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:01:00'),
(134, 1, 'SISTEMA', '2026-06-19 22:01:02', 'Visualizó lista de usuarios', 'Usuarios', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:01:02'),
(135, 1, 'SISTEMA', '2026-06-19 22:01:03', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:01:03'),
(136, 1, 'SISTEMA', '2026-06-19 22:01:03', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:01:03'),
(137, 1, 'SISTEMA', '2026-06-19 22:01:05', 'Visualizó lista de usuarios', 'Usuarios', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:01:05'),
(138, 1, 'SISTEMA', '2026-06-19 22:01:11', 'Visualizó lista de pacientes', 'Pacientes', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:01:11'),
(139, 1, 'SISTEMA', '2026-06-19 22:03:02', 'Visualizó lista de pacientes', 'Pacientes', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:03:02'),
(140, 1, 'SISTEMA', '2026-06-19 22:03:03', 'Visualizó pantalla de ventas', 'Ventas', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:03:03'),
(141, 1, 'SISTEMA', '2026-06-19 22:03:04', 'Visualizó ventas diarias', 'Ventas', 'Fecha: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:03:04'),
(142, 1, 'SISTEMA', '2026-06-19 22:03:06', 'Visualizó el inventario', 'Inventario', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 03:03:06'),
(143, 1, 'SISTEMA', '2026-06-19 22:03:48', 'Visualizó pantalla de ventas', 'Ventas', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:03:48'),
(144, 1, 'SISTEMA', '2026-06-19 22:03:49', 'Visualizó ventas diarias', 'Ventas', 'Fecha: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:03:49'),
(145, 1, 'SISTEMA', '2026-06-19 22:03:51', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:03:51'),
(146, 1, 'SISTEMA', '2026-06-19 22:03:52', 'Visualizó lista de usuarios', 'Usuarios', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:03:52'),
(147, 1, 'SISTEMA', '2026-06-19 22:03:53', 'Visualizó lista de pacientes', 'Pacientes', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:03:53'),
(148, 1, 'SISTEMA', '2026-06-19 22:03:54', 'Visualizó reportes', 'Reportes', 'Mes: null, Año: null, Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:03:54'),
(149, 1, 'SISTEMA', '2026-06-19 22:03:55', 'Visualizó lista de usuarios', 'Usuarios', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:03:55'),
(150, 1, 'SISTEMA', '2026-06-19 22:04:01', 'Visualizó lista de pacientes', 'Pacientes', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:04:01'),
(151, 1, 'SISTEMA', '2026-06-19 22:05:10', 'Visualizó lista de pacientes', 'Pacientes', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:05:10'),
(152, 1, 'CREACIÓN', '2026-06-19 22:05:19', 'Creó nuevo paciente: 3213 123123', 'Pacientes', 'DNI: 123213123, Teléfono: 32132131, Realizado por: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:05:19'),
(153, 1, 'SISTEMA', '2026-06-19 22:05:19', 'Visualizó lista de pacientes', 'Pacientes', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:05:19'),
(154, 1, 'SISTEMA', '2026-06-19 22:05:50', 'Visualizó lista de usuarios', 'Usuarios', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:05:50'),
(155, 1, 'SISTEMA', '2026-06-19 22:05:51', 'Visualizó historial de acciones', 'Historial', NULL, '0:0:0:0:0:0:0:1', '2026-06-20 03:05:51'),
(156, 1, 'SISTEMA', '2026-06-19 22:05:54', 'Visualizó lista de usuarios', 'Usuarios', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:05:54'),
(157, 1, 'SISTEMA', '2026-06-19 22:06:07', 'Visualizó el panel de inicio', 'Dashboard', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:06:07'),
(158, 1, 'SISTEMA', '2026-06-19 22:06:08', 'Visualizó pantalla de ventas', 'Ventas', 'Usuario: Administrador', '0:0:0:0:0:0:0:1', '2026-06-20 03:06:08');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `lotes`
--

CREATE TABLE `lotes` (
  `id` bigint(20) NOT NULL,
  `medicamento_id` bigint(20) NOT NULL,
  `codigo_lote` varchar(100) NOT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `fecha_vencimiento` date NOT NULL,
  `fecha_fabricacion` date DEFAULT NULL,
  `precio_compra` decimal(10,2) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `lotes`
--

INSERT INTO `lotes` (`id`, `medicamento_id`, `codigo_lote`, `stock`, `fecha_vencimiento`, `fecha_fabricacion`, `precio_compra`, `created_at`, `updated_at`) VALUES
(1, 1, 'PAR-001-24', 80, '2026-06-30', '2024-06-01', 10.00, '2026-05-14 02:01:45', '2026-05-14 02:56:48'),
(3, 2, 'IBU-001-24', 50, '2026-06-30', '2024-05-10', 18.00, '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(5, 3, 'AMX-001-24', 120, '2026-06-30', '2024-04-15', 22.00, '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(7, 4, 'LOR-001-24', 70, '2026-06-30', '2024-06-10', 14.00, '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(9, 5, 'OME-001-24', 55, '2026-06-30', '2024-05-20', 28.00, '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(11, 6, 'LOS-001-24', 35, '2026-06-30', '2024-06-25', 38.00, '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(13, 7, 'AZI-001-24', 25, '2026-06-30', '2024-07-05', 32.00, '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(15, 8, 'DIC-001-24', 60, '2026-06-30', '2024-06-18', 11.00, '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(17, 9, 'CET-001-24', 45, '2026-06-30', '2024-05-30', 16.00, '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(19, 10, 'MET-001-24', 80, '2026-06-30', '2024-07-12', 25.00, '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(21, 11, 'SAL-001-24', 25, '2026-06-30', '2024-06-08', 42.00, '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(23, 12, 'VIT-001-24', 110, '2026-06-30', '2024-05-22', 20.00, '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(25, 13, 'COM-001-24', 100, '2026-06-30', '2024-06-14', 18.00, '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(27, 14, 'NAP-001-24', 50, '2026-06-30', '2024-07-08', 20.00, '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(29, 15, 'CLO-001-24', 40, '2026-06-30', '2024-06-20', 15.00, '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(31, 16, 'PAR-002-21', 150, '2026-05-31', '2026-03-30', 2.00, '2026-05-14 03:14:01', '2026-05-14 03:32:27'),
(34, 17, 'PAR-002-28', 100, '2026-06-30', '2026-02-01', 3.00, '2026-05-14 05:23:09', '2026-05-14 05:23:09');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `medicamentos`
--

CREATE TABLE `medicamentos` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `categoria_id` bigint(20) NOT NULL,
  `forma_farmaceutica` varchar(100) DEFAULT NULL,
  `descripcion` text DEFAULT NULL,
  `proveedor` varchar(150) DEFAULT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `nivel_minimo` int(11) NOT NULL DEFAULT 20,
  `precio` decimal(10,2) NOT NULL DEFAULT 0.00,
  `ubicacion_estante` varchar(50) DEFAULT NULL,
  `fecha_vencimiento` date NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `medicamentos`
--

INSERT INTO `medicamentos` (`id`, `nombre`, `categoria_id`, `forma_farmaceutica`, `descripcion`, `proveedor`, `stock`, `nivel_minimo`, `precio`, `ubicacion_estante`, `fecha_vencimiento`, `created_at`, `updated_at`) VALUES
(1, 'Paracetamol 500mg', 1, 'Tabletas x 30', 'Analgésico y antipirético para el dolor y fiebre', 'Farmindustria SA', 150, 30, 15.50, 'A-01', '2026-04-30', '2026-05-14 02:01:45', '2026-05-14 02:56:54'),
(2, 'Ibuprofeno 400mg', 3, 'Cápsulas x 20', 'Antiinflamatorio no esteroideo para dolor e inflamación', 'Laboratorios Pfizer', 81, 25, 28.90, 'A-02', '2026-06-30', '2026-05-14 02:01:45', '2026-05-18 23:05:05'),
(3, 'Amoxicilina 500mg', 2, 'Cápsulas x 15', 'Antibiótico de amplio espectro', 'Antibióticos Perú', 200, 40, 35.00, 'B-01', '2026-06-30', '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(4, 'Loratadina 10mg', 4, 'Tabletas x 10', 'Antihistamínico para alergias', 'Alergofarma', 120, 20, 22.30, 'C-01', '2026-06-30', '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(5, 'Omeprazol 20mg', 7, 'Cápsulas x 28', 'Inhibidor de bomba de protones para acidez estomacal', 'GastroHealth', 95, 25, 42.00, 'D-01', '2026-06-30', '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(6, 'Losartán 50mg', 6, 'Tabletas x 30', 'Antihipertensivo para presión arterial', 'CardioPerú', 60, 15, 55.00, 'E-01', '2026-06-30', '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(7, 'Azitromicina 500mg', 2, 'Tabletas x 3', 'Antibiótico macrólido para infecciones respiratorias', 'Antibióticos Perú', 45, 10, 48.50, 'B-02', '2026-06-30', '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(8, 'Diclofenaco 50mg', 3, 'Tabletas x 20', 'Antiinflamatorio para dolores musculares y articulares', 'Farmindustria SA', 110, 30, 18.00, 'A-03', '2026-06-30', '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(9, 'Cetirizina 10mg', 4, 'Tabletas x 10', 'Antihistamínico para rinitis y urticaria', 'Alergofarma', 75, 20, 25.00, 'C-02', '2026-06-30', '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(10, 'Metformina 850mg', 6, 'Tabletas x 60', 'Antidiabético para control de glucosa', 'CardioPerú', 130, 35, 38.00, 'E-02', '2026-06-30', '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(11, 'Salbutamol 100mcg', 8, 'Inhalador dosis medida', 'Broncodilatador para asma y EPOC', 'RespiraFarma', 40, 10, 65.00, 'F-01', '2026-06-30', '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(12, 'Vitamina C 1000mg', 5, 'Tabletas efervescentes x 20', 'Suplemento vitamínico para defensas', 'NutriVida', 200, 50, 32.00, 'G-01', '2026-06-30', '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(13, 'Complejo B', 5, 'Tabletas x 30', 'Complejo vitamínico B1, B6, B12', 'NutriVida', 180, 40, 28.00, 'G-02', '2026-06-30', '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(14, 'Naproxeno 250mg', 3, 'Tabletas x 24', 'Antiinflamatorio para dolor menstrual y articular', 'Farmindustria SA', 90, 25, 31.00, 'A-04', '2026-06-30', '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(15, 'Clotrimazol 1%', 5, 'Crema tópica 20g', 'Antifúngico para infecciones dérmicas', 'DermaCare', 70, 15, 24.50, 'H-01', '2026-06-30', '2026-05-14 02:01:45', '2026-05-14 02:01:45'),
(16, 'Paracetamol 500mg', 2, 'Tabletas x 30', 'asdas', 'Farmalabs', 20, 20, 2.50, 'A-01', '2026-05-31', '2026-05-14 03:14:01', '2026-05-18 23:05:05'),
(17, 'Paracetamol 500mg', 1, '100 tabletas', 'NO', 'Farmalabs', 7, 20, 5.00, 'A-01', '2026-06-30', '2026-05-14 05:23:09', '2026-06-19 20:18:10');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pacientes`
--

CREATE TABLE `pacientes` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `dni` varchar(20) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `estado` varchar(20) DEFAULT 'ACTIVO',
  `genero` varchar(20) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `compras_realizadas` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `pacientes`
--

INSERT INTO `pacientes` (`id`, `nombre`, `apellido`, `dni`, `telefono`, `estado`, `genero`, `created_at`, `updated_at`, `compras_realizadas`) VALUES
(1, 'Alejandro', 'Paredes', '12345678', '987412563', 'ACTIVO', 'Masculino', '2026-06-19 19:43:59', '2026-06-19 20:14:11', 6),
(2, 'Mari', 'Pila', '12332131', '123213213321', 'ACTIVO', 'Femenino', '2026-06-20 01:28:48', '2026-06-20 01:28:48', 0),
(3, 'Mari', 'Pilar', '12332112', '981321123', 'ACTIVO', 'Femenino', '2026-06-20 01:29:14', '2026-06-20 01:29:14', 0),
(4, 'Alejandro', 'Paredes', '12332121', '981232123', 'ACTIVO', 'Masculino', '2026-06-20 02:26:41', '2026-06-20 02:26:41', 0),
(5, '3213', '123123', '123213123', '32132131', 'ACTIVO', 'Masculino', '2026-06-20 03:05:19', '2026-06-20 03:05:19', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` bigint(20) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nombre_completo` varchar(150) NOT NULL,
  `rol` varchar(50) NOT NULL,
  `estado` varchar(20) DEFAULT 'ACTIVO',
  `last_login` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `username`, `password`, `nombre_completo`, `rol`, `estado`, `last_login`, `created_at`, `updated_at`) VALUES
(1, 'admin', '$2a$10$B8c1tXJAKxvCvbBG7ImJu.gyCKPtzNkehMDnBjyn19NhWRj5pls5m', 'Administrador', 'ADMIN', 'ACTIVO', NULL, '2026-05-05 05:21:20', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ventas`
--

CREATE TABLE `ventas` (
  `id` bigint(20) NOT NULL,
  `usuario_id` bigint(20) NOT NULL,
  `paciente_id` bigint(20) DEFAULT NULL,
  `cliente_anonimo_nombre` varchar(150) DEFAULT NULL,
  `metodo_pago` varchar(50) DEFAULT NULL,
  `total` decimal(10,2) NOT NULL,
  `fecha_venta` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `ventas`
--

INSERT INTO `ventas` (`id`, `usuario_id`, `paciente_id`, `cliente_anonimo_nombre`, `metodo_pago`, `total`, `fecha_venta`) VALUES
(1, 1, NULL, NULL, 'EFECTIVO', 72.80, '2026-05-18'),
(2, 1, NULL, 'juan', 'YAPE', 33.90, '2026-05-18'),
(3, 1, NULL, 'Anónimo', 'EFECTIVO', 36.40, '2026-05-18'),
(4, 1, NULL, 'Alejandro Paredes', 'EFECTIVO', 5.00, '2026-06-19'),
(5, 1, NULL, 'Alejandro Paredes', 'EFECTIVO', 5.00, '2026-06-19'),
(6, 1, NULL, 'Alejandro Paredes', 'EFECTIVO', 5.00, '2026-06-19'),
(7, 1, NULL, 'Alejandro Paredes', 'EFECTIVO', 5.00, '2026-06-19'),
(8, 1, NULL, 'Alejandro Paredes', 'EFECTIVO', 5.00, '2026-06-19'),
(9, 1, NULL, 'Alejandro Paredes', 'EFECTIVO', 4.75, '2026-06-19'),
(10, 1, NULL, 'Anónimo', 'EFECTIVO', 5.00, '2026-06-19');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `categorias`
--
ALTER TABLE `categorias`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `detalle_ventas`
--
ALTER TABLE `detalle_ventas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `venta_id` (`venta_id`),
  ADD KEY `medicamento_id` (`medicamento_id`),
  ADD KEY `lote_id` (`lote_id`);

--
-- Indices de la tabla `historial_acciones`
--
ALTER TABLE `historial_acciones`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- Indices de la tabla `lotes`
--
ALTER TABLE `lotes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_medicamento_lote` (`medicamento_id`,`codigo_lote`);

--
-- Indices de la tabla `medicamentos`
--
ALTER TABLE `medicamentos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `categoria_id` (`categoria_id`);

--
-- Indices de la tabla `pacientes`
--
ALTER TABLE `pacientes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `dni` (`dni`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indices de la tabla `ventas`
--
ALTER TABLE `ventas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`),
  ADD KEY `paciente_id` (`paciente_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `categorias`
--
ALTER TABLE `categorias`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `detalle_ventas`
--
ALTER TABLE `detalle_ventas`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `historial_acciones`
--
ALTER TABLE `historial_acciones`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=159;

--
-- AUTO_INCREMENT de la tabla `lotes`
--
ALTER TABLE `lotes`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

--
-- AUTO_INCREMENT de la tabla `medicamentos`
--
ALTER TABLE `medicamentos`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT de la tabla `pacientes`
--
ALTER TABLE `pacientes`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `ventas`
--
ALTER TABLE `ventas`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `detalle_ventas`
--
ALTER TABLE `detalle_ventas`
  ADD CONSTRAINT `detalle_ventas_ibfk_1` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `detalle_ventas_ibfk_2` FOREIGN KEY (`medicamento_id`) REFERENCES `medicamentos` (`id`),
  ADD CONSTRAINT `detalle_ventas_ibfk_3` FOREIGN KEY (`lote_id`) REFERENCES `lotes` (`id`);

--
-- Filtros para la tabla `historial_acciones`
--
ALTER TABLE `historial_acciones`
  ADD CONSTRAINT `historial_acciones_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `lotes`
--
ALTER TABLE `lotes`
  ADD CONSTRAINT `lotes_ibfk_1` FOREIGN KEY (`medicamento_id`) REFERENCES `medicamentos` (`id`);

--
-- Filtros para la tabla `medicamentos`
--
ALTER TABLE `medicamentos`
  ADD CONSTRAINT `medicamentos_ibfk_1` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`id`);

--
-- Filtros para la tabla `ventas`
--
ALTER TABLE `ventas`
  ADD CONSTRAINT `ventas_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `ventas_ibfk_2` FOREIGN KEY (`paciente_id`) REFERENCES `pacientes` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
