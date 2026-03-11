-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost
-- Tiempo de generación: 11-03-2026 a las 20:35:03
-- Versión del servidor: 10.11.16-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `restaurante_2026`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `area_mesa`
--

CREATE TABLE `area_mesa` (
  `area_id` bigint(20) UNSIGNED NOT NULL,
  `sede_id` bigint(20) UNSIGNED NOT NULL,
  `area_nombre` varchar(100) NOT NULL COMMENT 'Ej: Salón Principal, Terraza',
  `area_estado` enum('Activo','Inactivo') NOT NULL DEFAULT 'Activo',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Áreas o salones del restaurante por sede';

--
-- Volcado de datos para la tabla `area_mesa`
--

INSERT INTO `area_mesa` (`area_id`, `sede_id`, `area_nombre`, `area_estado`, `created_at`, `updated_at`) VALUES
(1, 1, 'Salón Principal', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(2, 1, 'Terraza', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(3, 2, 'Salón Norte', 'Activo', '2026-03-11 17:35:26', '2026-03-11 17:35:26');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categoria_insumo`
--

CREATE TABLE `categoria_insumo` (
  `cins_id` bigint(20) UNSIGNED NOT NULL,
  `cins_nombre` varchar(100) NOT NULL COMMENT 'Ej: Carnes, Lácteos, Granos, Bebidas',
  `cins_imagen` varchar(255) DEFAULT NULL,
  `cins_estado` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Categorías de insumos de bodega';

--
-- Volcado de datos para la tabla `categoria_insumo`
--

INSERT INTO `categoria_insumo` (`cins_id`, `cins_nombre`, `cins_imagen`, `cins_estado`, `created_at`, `updated_at`) VALUES
(1, 'Carnes', NULL, 1, '2026-03-11 17:35:57', '2026-03-11 17:35:57'),
(2, 'Lácteos', NULL, 1, '2026-03-11 17:35:57', '2026-03-11 17:35:57'),
(3, 'Granos', NULL, 1, '2026-03-11 17:35:57', '2026-03-11 17:35:57'),
(4, 'Bebidas', NULL, 1, '2026-03-11 17:35:57', '2026-03-11 17:35:57'),
(5, 'Verduras', NULL, 1, '2026-03-11 17:35:57', '2026-03-11 17:35:57');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categoria_plato`
--

CREATE TABLE `categoria_plato` (
  `cat_id` bigint(20) UNSIGNED NOT NULL,
  `cat_nombre` varchar(100) NOT NULL,
  `cat_imagen` varchar(255) DEFAULT NULL,
  `cat_estado` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Categorías del menú (Entradas, Platos Fuertes, Bebidas, etc.)';

--
-- Volcado de datos para la tabla `categoria_plato`
--

INSERT INTO `categoria_plato` (`cat_id`, `cat_nombre`, `cat_imagen`, `cat_estado`, `created_at`, `updated_at`) VALUES
(1, 'Entradas', NULL, 1, '2026-03-11 17:36:07', '2026-03-11 17:36:07'),
(2, 'Platos Fuertes', NULL, 1, '2026-03-11 17:36:07', '2026-03-11 17:36:07'),
(3, 'Bebidas', NULL, 1, '2026-03-11 17:36:07', '2026-03-11 17:36:07'),
(4, 'Postres', NULL, 1, '2026-03-11 17:36:07', '2026-03-11 17:36:07');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cliente`
--

CREATE TABLE `cliente` (
  `cli_id` bigint(20) UNSIGNED NOT NULL,
  `cli_nombre` varchar(60) NOT NULL,
  `cli_apellidos` varchar(60) NOT NULL DEFAULT '',
  `cli_tipo_documento` varchar(20) NOT NULL DEFAULT 'CC' COMMENT 'CC, NIT, CE, Pasaporte, DNI',
  `cli_num_documento` varchar(30) DEFAULT NULL,
  `cli_direccion` varchar(100) DEFAULT NULL,
  `cli_telefono` varchar(20) DEFAULT NULL,
  `cli_correo` varchar(100) DEFAULT NULL,
  `cli_estado` enum('Activo','Inactivo') NOT NULL DEFAULT 'Activo',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Clientes del restaurante';

--
-- Volcado de datos para la tabla `cliente`
--

INSERT INTO `cliente` (`cli_id`, `cli_nombre`, `cli_apellidos`, `cli_tipo_documento`, `cli_num_documento`, `cli_direccion`, `cli_telefono`, `cli_correo`, `cli_estado`, `created_at`, `updated_at`) VALUES
(1, 'Ana', 'Rodríguez', 'CC', '1098765432', 'Calle 10 #5-20', '3001112233', 'ana@correo.com', 'Activo', '2026-03-11 17:38:11', '2026-03-11 17:38:11'),
(2, 'Jorge', 'Martínez', 'CC', '1087654321', 'Cra 8 #15-40', '3114455667', 'jorge@correo.com', 'Activo', '2026-03-11 17:38:11', '2026-03-11 17:38:11'),
(3, 'Sandra', 'López', 'NIT', '900123456-1', 'Av 30 #20-10', '3209988776', 'empresa@correo.com', 'Activo', '2026-03-11 17:38:11', '2026-03-11 17:38:11');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `concepto_egreso`
--

CREATE TABLE `concepto_egreso` (
  `con_id` bigint(20) UNSIGNED NOT NULL,
  `con_descripcion` varchar(120) NOT NULL COMMENT 'Ej: Pago Nómina, Servicios Públicos',
  `con_estado` enum('Activo','Inactivo') NOT NULL DEFAULT 'Activo',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Conceptos de egresos (nómina, servicios, proveedores, etc.)';

--
-- Volcado de datos para la tabla `concepto_egreso`
--

INSERT INTO `concepto_egreso` (`con_id`, `con_descripcion`, `con_estado`, `created_at`, `updated_at`) VALUES
(1, 'Pago a Terceros', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(2, 'Pago Servicios Públicos', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(3, 'Pago Nómina', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(4, 'Compra de Insumos', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(5, 'Mantenimiento', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `configuracion`
--

CREATE TABLE `configuracion` (
  `cfg_id` bigint(20) UNSIGNED NOT NULL,
  `cfg_clave` varchar(100) NOT NULL COMMENT 'Ej: company_name, currency_symbol',
  `cfg_valor` text DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Configuración general del sistema restaurante';

--
-- Volcado de datos para la tabla `configuracion`
--

INSERT INTO `configuracion` (`cfg_id`, `cfg_clave`, `cfg_valor`, `created_at`, `updated_at`) VALUES
(1, 'company_name', 'Mi Restaurante', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(2, 'company_address', 'Calle Principal #123', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(3, 'company_phone', '300 000 0000', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(4, 'currency_symbol', '$', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(5, 'ticket_footer', '¡Gracias por su visita! Vuelva pronto.', '2026-03-05 01:44:54', '2026-03-05 01:44:54');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `encabezado_egresos`
--

CREATE TABLE `encabezado_egresos` (
  `egr_id` bigint(20) UNSIGNED NOT NULL,
  `sede_id` bigint(20) UNSIGNED NOT NULL,
  `no_egreso` bigint(20) UNSIGNED NOT NULL COMMENT 'Número consecutivo del egreso',
  `fecha_documento` date NOT NULL DEFAULT curdate(),
  `tercero_identificacion` varchar(20) NOT NULL COMMENT 'NIT o Cédula del beneficiario',
  `tercero_nombre` varchar(150) DEFAULT NULL COMMENT 'Nombre del beneficiario',
  `detalle` varchar(250) NOT NULL,
  `fp_id` bigint(20) UNSIGNED NOT NULL,
  `con_id` bigint(20) UNSIGNED NOT NULL,
  `no_documento` varchar(30) NOT NULL COMMENT 'Número de comprobante',
  `valor_egreso` decimal(14,2) NOT NULL DEFAULT 0.00,
  `usu_id` bigint(20) UNSIGNED NOT NULL COMMENT 'Usuario que registra el egreso',
  `egr_estado` enum('Activo','Anulado') NOT NULL DEFAULT 'Activo',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Registro de egresos y salidas de caja por sede';

--
-- Volcado de datos para la tabla `encabezado_egresos`
--

INSERT INTO `encabezado_egresos` (`egr_id`, `sede_id`, `no_egreso`, `fecha_documento`, `tercero_identificacion`, `tercero_nombre`, `detalle`, `fp_id`, `con_id`, `no_documento`, `valor_egreso`, `usu_id`, `egr_estado`, `created_at`, `updated_at`) VALUES
(1, 1, 1, '2026-03-11', '900111222-3', 'Servicios Públicos SA', 'Pago factura agua marzo', 1, 2, 'FAC-001', 250000.00, 2, 'Activo', '2026-03-11 17:39:38', '2026-03-11 17:39:38'),
(2, 1, 2, '2026-03-11', '10456789', 'Juan Proveedor', 'Compra de carnes semana', 2, 4, 'REC-045', 540000.00, 2, 'Activo', '2026-03-11 17:39:38', '2026-03-11 17:39:38');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estado`
--

CREATE TABLE `estado` (
  `est_id` bigint(20) UNSIGNED NOT NULL,
  `est_descripcion` varchar(60) NOT NULL,
  `tes_id` bigint(20) UNSIGNED NOT NULL,
  `est_estado` enum('Activo','Inactivo') NOT NULL DEFAULT 'Activo',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Catálogo de estados para múltiples entidades';

--
-- Volcado de datos para la tabla `estado`
--

INSERT INTO `estado` (`est_id`, `est_descripcion`, `tes_id`, `est_estado`, `created_at`, `updated_at`) VALUES
(1, 'En Preparación', 1, 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(2, 'Servido', 1, 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(3, 'Cancelado', 1, 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(4, 'Creado', 2, 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(5, 'Finalizado', 2, 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(6, 'Cancelado', 2, 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(7, 'Disponible', 3, 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(8, 'Agotado', 3, 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(9, 'Inactivo', 3, 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(10, 'Pendiente', 4, 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(11, 'Atendida', 4, 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(12, 'Cerrada', 4, 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(13, 'Disponible', 5, 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(14, 'Agotado', 5, 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(15, 'Inactivo', 5, 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `forma_pago`
--

CREATE TABLE `forma_pago` (
  `fp_id` bigint(20) UNSIGNED NOT NULL,
  `fp_descripcion` varchar(60) NOT NULL COMMENT 'Ej: Efectivo, Transferencia, Tarjeta',
  `fp_estado` enum('Activo','Inactivo') NOT NULL DEFAULT 'Activo',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Formas de pago disponibles';

--
-- Volcado de datos para la tabla `forma_pago`
--

INSERT INTO `forma_pago` (`fp_id`, `fp_descripcion`, `fp_estado`, `created_at`, `updated_at`) VALUES
(1, 'Efectivo', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(2, 'Transferencia Bancaria', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(3, 'Tarjeta Débito', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(4, 'Tarjeta Crédito', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `insumo`
--

CREATE TABLE `insumo` (
  `ins_id` bigint(20) UNSIGNED NOT NULL,
  `sede_id` bigint(20) UNSIGNED NOT NULL,
  `cins_id` bigint(20) UNSIGNED NOT NULL,
  `pres_id` bigint(20) UNSIGNED NOT NULL COMMENT 'Unidad de medida (Kg, L, Und...)',
  `ins_nombre` varchar(150) NOT NULL COMMENT 'Ej: Arroz, Carne de res, Aceite',
  `ins_codigo` varchar(30) DEFAULT NULL,
  `ins_precio_compra` decimal(10,2) DEFAULT NULL COMMENT 'Precio unitario de compra',
  `ins_stock` decimal(12,3) NOT NULL DEFAULT 0.000 COMMENT 'Stock actual en bodega',
  `ins_stock_min` decimal(12,3) NOT NULL DEFAULT 0.000 COMMENT 'Stock mínimo para alerta',
  `ins_vendible` tinyint(1) NOT NULL DEFAULT 0 COMMENT '1 = también se vende al público',
  `ins_imagen` varchar(255) DEFAULT NULL,
  `ins_estado` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Insumos de bodega por sede: materias primas y productos almacenados';

--
-- Volcado de datos para la tabla `insumo`
--

INSERT INTO `insumo` (`ins_id`, `sede_id`, `cins_id`, `pres_id`, `ins_nombre`, `ins_codigo`, `ins_precio_compra`, `ins_stock`, `ins_stock_min`, `ins_vendible`, `ins_imagen`, `ins_estado`, `created_at`, `updated_at`) VALUES
(1, 1, 3, 1, 'Arroz', 'INS-001', 2500.00, 50.000, 10.000, 0, NULL, 1, '2026-03-11 17:36:20', '2026-03-11 17:36:20'),
(2, 1, 1, 1, 'Carne de res', 'INS-002', 18000.00, 20.000, 5.000, 0, NULL, 1, '2026-03-11 17:36:20', '2026-03-11 17:36:20'),
(3, 1, 1, 1, 'Pechuga de pollo', 'INS-003', 9000.00, 15.000, 4.000, 0, NULL, 1, '2026-03-11 17:36:20', '2026-03-11 17:36:20'),
(4, 1, 2, 2, 'Leche', 'INS-004', 1800.00, 10.000, 3.000, 0, NULL, 1, '2026-03-11 17:36:20', '2026-03-11 17:36:20'),
(5, 1, 4, 2, 'Gaseosa Cola', 'INS-005', 1500.00, 48.000, 12.000, 1, NULL, 1, '2026-03-11 17:36:20', '2026-03-11 17:36:20'),
(6, 1, 5, 1, 'Tomate', 'INS-006', 3000.00, 8.000, 2.000, 0, NULL, 1, '2026-03-11 17:36:20', '2026-03-11 17:36:20');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `inventario_log`
--

CREATE TABLE `inventario_log` (
  `log_id` bigint(20) UNSIGNED NOT NULL,
  `ins_id` bigint(20) UNSIGNED NOT NULL,
  `usu_id` bigint(20) UNSIGNED NOT NULL COMMENT 'Usuario que registra el movimiento',
  `log_tipo` enum('entrada','salida','ajuste','venta','merma') NOT NULL COMMENT 'Tipo de movimiento',
  `log_cantidad` decimal(12,3) NOT NULL COMMENT 'Cantidad del movimiento',
  `log_stock_ant` decimal(12,3) DEFAULT NULL COMMENT 'Stock antes del movimiento',
  `log_stock_nvo` decimal(12,3) DEFAULT NULL COMMENT 'Stock después del movimiento',
  `log_nota` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Trazabilidad de movimientos de inventario (entradas, salidas, ajustes)';

--
-- Volcado de datos para la tabla `inventario_log`
--

INSERT INTO `inventario_log` (`log_id`, `ins_id`, `usu_id`, `log_tipo`, `log_cantidad`, `log_stock_ant`, `log_stock_nvo`, `log_nota`, `created_at`, `updated_at`) VALUES
(1, 2, 2, 'entrada', 10.000, 10.000, 20.000, 'Compra proveedor 11/03/2026', '2026-03-11 17:39:49', '2026-03-11 17:39:49'),
(2, 1, 2, 'entrada', 25.000, 25.000, 50.000, 'Abastecimiento semanal', '2026-03-11 17:39:49', '2026-03-11 17:39:49'),
(3, 2, 5, 'salida', 0.600, 20.000, 19.400, 'Consumo Bandeja Paisa pedido #3', '2026-03-11 17:39:49', '2026-03-11 17:39:49'),
(4, 1, 5, 'salida', 0.450, 50.000, 49.550, 'Consumo Arroz con Pollo pedido #3', '2026-03-11 17:39:49', '2026-03-11 17:39:49');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mesa`
--

CREATE TABLE `mesa` (
  `mesa_id` bigint(20) UNSIGNED NOT NULL,
  `sede_id` bigint(20) UNSIGNED NOT NULL,
  `area_id` bigint(20) UNSIGNED DEFAULT NULL,
  `mesa_numero` varchar(10) NOT NULL COMMENT 'Nombre o número de mesa',
  `capacidad` tinyint(3) UNSIGNED NOT NULL DEFAULT 4,
  `x_pos` int(11) NOT NULL DEFAULT 0 COMMENT 'Posición X en mapa visual',
  `y_pos` int(11) NOT NULL DEFAULT 0 COMMENT 'Posición Y en mapa visual',
  `estado` enum('Disponible','Ocupada','Reservada','Inactiva') NOT NULL DEFAULT 'Disponible',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Mesas del restaurante con posición para mapa visual y sede';

--
-- Volcado de datos para la tabla `mesa`
--

INSERT INTO `mesa` (`mesa_id`, `sede_id`, `area_id`, `mesa_numero`, `capacidad`, `x_pos`, `y_pos`, `estado`, `created_at`, `updated_at`) VALUES
(1, 1, 1, 'M01', 4, 10, 10, 'Disponible', '2026-03-11 17:35:35', '2026-03-11 17:35:35'),
(2, 1, 1, 'M02', 6, 20, 10, 'Disponible', '2026-03-11 17:35:35', '2026-03-11 17:35:35'),
(3, 1, 1, 'M03', 4, 30, 10, 'Ocupada', '2026-03-11 17:35:35', '2026-03-11 17:35:35'),
(4, 1, 2, 'T01', 2, 10, 30, 'Reservada', '2026-03-11 17:35:35', '2026-03-11 17:35:35'),
(5, 1, 2, 'T02', 8, 20, 30, 'Disponible', '2026-03-11 17:35:35', '2026-03-11 17:35:35'),
(6, 2, 3, 'N01', 4, 10, 10, 'Disponible', '2026-03-11 17:35:35', '2026-03-11 17:35:35');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedido`
--

CREATE TABLE `pedido` (
  `ped_id` bigint(20) UNSIGNED NOT NULL,
  `sede_id` bigint(20) UNSIGNED NOT NULL,
  `ped_fecha` date NOT NULL DEFAULT curdate(),
  `usu_id` bigint(20) UNSIGNED NOT NULL COMMENT 'Mesero que toma el pedido',
  `mesa_id` bigint(20) UNSIGNED NOT NULL,
  `est_id` bigint(20) UNSIGNED NOT NULL COMMENT 'Estado del pedido',
  `ped_obs` varchar(360) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Pedidos/Órdenes — cabecera por mesa, mesero y sede';

--
-- Volcado de datos para la tabla `pedido`
--

INSERT INTO `pedido` (`ped_id`, `sede_id`, `ped_fecha`, `usu_id`, `mesa_id`, `est_id`, `ped_obs`, `created_at`, `updated_at`) VALUES
(1, 1, '2026-03-11', 3, 1, 4, 'Cliente alérgico a nueces', '2026-03-11 17:38:24', '2026-03-11 17:38:24'),
(2, 1, '2026-03-11', 3, 2, 4, NULL, '2026-03-11 17:38:24', '2026-03-11 17:38:24'),
(3, 1, '2026-03-11', 3, 3, 5, 'Pedido finalizado', '2026-03-11 17:38:24', '2026-03-11 17:38:24');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedido_detalle`
--

CREATE TABLE `pedido_detalle` (
  `ped_det_id` bigint(20) UNSIGNED NOT NULL,
  `ped_id` bigint(20) UNSIGNED NOT NULL,
  `pla_id` bigint(20) UNSIGNED NOT NULL,
  `ped_det_cant` smallint(5) UNSIGNED NOT NULL DEFAULT 1,
  `ped_det_precio` decimal(10,2) NOT NULL COMMENT 'Precio al momento de la venta',
  `ped_det_obser` varchar(255) DEFAULT NULL COMMENT 'Observaciones de cocina',
  `est_id` bigint(20) UNSIGNED NOT NULL COMMENT 'Estado: En Preparación, Servido, Cancelado',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Detalle del pedido = Comanda de cocina (cada línea es un ítem)';

--
-- Volcado de datos para la tabla `pedido_detalle`
--

INSERT INTO `pedido_detalle` (`ped_det_id`, `ped_id`, `pla_id`, `ped_det_cant`, `ped_det_precio`, `ped_det_obser`, `est_id`, `created_at`, `updated_at`) VALUES
(1, 1, 2, 2, 35000.00, 'Sin frijoles', 1, '2026-03-11 17:38:39', '2026-03-11 17:38:39'),
(2, 1, 5, 2, 8000.00, NULL, 1, '2026-03-11 17:38:39', '2026-03-11 17:38:39'),
(3, 2, 3, 1, 28000.00, 'Término medio', 1, '2026-03-11 17:38:39', '2026-03-11 17:38:39'),
(4, 3, 4, 3, 25000.00, NULL, 2, '2026-03-11 17:38:39', '2026-03-11 17:38:39'),
(5, 3, 6, 3, 5000.00, NULL, 2, '2026-03-11 17:38:39', '2026-03-11 17:38:39');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `perfil`
--

CREATE TABLE `perfil` (
  `perf_id` bigint(20) UNSIGNED NOT NULL,
  `perf_descripcion` varchar(60) NOT NULL COMMENT 'Ej: Admin, Mesero, Cocina, Cajero',
  `perf_estado` enum('Activo','Inactivo') NOT NULL DEFAULT 'Activo',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Roles/Perfiles del sistema';

--
-- Volcado de datos para la tabla `perfil`
--

INSERT INTO `perfil` (`perf_id`, `perf_descripcion`, `perf_estado`, `created_at`, `updated_at`) VALUES
(1, 'Administrador', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(2, 'Mesero', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(3, 'Cocinero', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(4, 'Cajero', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `plato`
--

CREATE TABLE `plato` (
  `pla_id` bigint(20) UNSIGNED NOT NULL,
  `cat_id` bigint(20) UNSIGNED NOT NULL,
  `pla_descripcion` varchar(150) NOT NULL,
  `pla_codigo` varchar(20) DEFAULT NULL,
  `pla_precio` decimal(10,2) NOT NULL DEFAULT 0.00,
  `pla_costo` decimal(10,2) DEFAULT NULL COMMENT 'Costo de producción del plato',
  `pla_imagen` varchar(255) DEFAULT NULL,
  `est_id` bigint(20) UNSIGNED NOT NULL COMMENT 'Estado: Disponible, Agotado, Inactivo',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Carta del restaurante (platos y bebidas vendibles)';

--
-- Volcado de datos para la tabla `plato`
--

INSERT INTO `plato` (`pla_id`, `cat_id`, `pla_descripcion`, `pla_codigo`, `pla_precio`, `pla_costo`, `pla_imagen`, `est_id`, `created_at`, `updated_at`) VALUES
(1, 1, 'Sopa del Día', 'PLT-001', 15000.00, 5000.00, NULL, 7, '2026-03-11 17:37:09', '2026-03-11 17:37:09'),
(2, 2, 'Bandeja Paisa', 'PLT-002', 35000.00, 14000.00, NULL, 7, '2026-03-11 17:37:09', '2026-03-11 17:37:09'),
(3, 2, 'Pollo a la Plancha', 'PLT-003', 28000.00, 10000.00, NULL, 7, '2026-03-11 17:37:09', '2026-03-11 17:37:09'),
(4, 2, 'Arroz con Pollo', 'PLT-004', 25000.00, 9000.00, NULL, 7, '2026-03-11 17:37:09', '2026-03-11 17:37:09'),
(5, 3, 'Jugo Natural', 'PLT-005', 8000.00, 2000.00, NULL, 7, '2026-03-11 17:37:09', '2026-03-11 17:37:09'),
(6, 3, 'Gaseosa', 'PLT-006', 5000.00, 1500.00, NULL, 7, '2026-03-11 17:37:09', '2026-03-11 17:37:09'),
(7, 4, 'Postre del Día', 'PLT-007', 10000.00, 3000.00, NULL, 7, '2026-03-11 17:37:09', '2026-03-11 17:37:09');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `plato_ingrediente`
--

CREATE TABLE `plato_ingrediente` (
  `pi_id` bigint(20) UNSIGNED NOT NULL,
  `pla_id` bigint(20) UNSIGNED NOT NULL COMMENT 'Plato del menú',
  `ins_id` bigint(20) UNSIGNED NOT NULL COMMENT 'Insumo necesario',
  `pi_cantidad` decimal(10,3) NOT NULL COMMENT 'Cantidad requerida por porción',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Recetas: ingredientes (insumos) necesarios para preparar cada plato';

--
-- Volcado de datos para la tabla `plato_ingrediente`
--

INSERT INTO `plato_ingrediente` (`pi_id`, `pla_id`, `ins_id`, `pi_cantidad`, `created_at`, `updated_at`) VALUES
(1, 1, 1, 0.100, '2026-03-11 17:37:41', '2026-03-11 17:37:41'),
(2, 1, 6, 0.050, '2026-03-11 17:37:41', '2026-03-11 17:37:41'),
(3, 2, 2, 0.300, '2026-03-11 17:37:41', '2026-03-11 17:37:41'),
(4, 2, 1, 0.200, '2026-03-11 17:37:41', '2026-03-11 17:37:41'),
(5, 3, 3, 0.250, '2026-03-11 17:37:41', '2026-03-11 17:37:41'),
(6, 4, 3, 0.200, '2026-03-11 17:37:41', '2026-03-11 17:37:41'),
(7, 4, 1, 0.150, '2026-03-11 17:37:41', '2026-03-11 17:37:41');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pqrs`
--

CREATE TABLE `pqrs` (
  `pqrs_id` bigint(20) UNSIGNED NOT NULL,
  `pqrs_fecha` date NOT NULL DEFAULT curdate(),
  `pqrs_descripcion` text NOT NULL,
  `pqrs_correo` varchar(100) NOT NULL,
  `pqrs_telefono` varchar(20) NOT NULL,
  `tpqrs_id` bigint(20) UNSIGNED NOT NULL,
  `est_id` bigint(20) UNSIGNED NOT NULL COMMENT 'Estado: Pendiente, Atendida, Cerrada',
  `pqrs_respuesta` text DEFAULT NULL,
  `usu_id_responde` bigint(20) UNSIGNED DEFAULT NULL COMMENT 'Usuario que responde',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Módulo PQRS: Peticiones, Quejas, Reclamos y Sugerencias';

--
-- Volcado de datos para la tabla `pqrs`
--

INSERT INTO `pqrs` (`pqrs_id`, `pqrs_fecha`, `pqrs_descripcion`, `pqrs_correo`, `pqrs_telefono`, `tpqrs_id`, `est_id`, `pqrs_respuesta`, `usu_id_responde`, `created_at`, `updated_at`) VALUES
(1, '2026-03-10', 'La sopa llegó fría y sin sal', 'ana@correo.com', '3001112233', 2, 10, NULL, NULL, '2026-03-11 17:40:05', '2026-03-11 17:40:05'),
(2, '2026-03-11', 'Sugiero ampliar el menú vegetariano', 'jorge@correo.com', '3114455667', 4, 11, 'Gracias por su sugerencia, la tendremos en cuenta.', 2, '2026-03-11 17:40:05', '2026-03-11 17:40:05');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `presentacion`
--

CREATE TABLE `presentacion` (
  `pres_id` bigint(20) UNSIGNED NOT NULL,
  `pres_descripcion` varchar(50) NOT NULL COMMENT 'Ej: Kg, Litros, Unidad, Gramos, ml',
  `pres_abreviatura` varchar(10) NOT NULL COMMENT 'Ej: Kg, L, Und, g, ml',
  `pres_estado` enum('Activo','Inactivo') NOT NULL DEFAULT 'Activo',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Unidades de medida para insumos de bodega (Kg, Litros, Unidad, etc.)';

--
-- Volcado de datos para la tabla `presentacion`
--

INSERT INTO `presentacion` (`pres_id`, `pres_descripcion`, `pres_abreviatura`, `pres_estado`, `created_at`, `updated_at`) VALUES
(1, 'Kilogramo', 'Kg', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(2, 'Litro', 'L', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(3, 'Unidad', 'Und', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(4, 'Gramo', 'g', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(5, 'Mililitro', 'ml', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(6, 'Libra', 'Lb', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(7, 'Arroba', '@', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `recibo_caja`
--

CREATE TABLE `recibo_caja` (
  `rc_num` bigint(20) UNSIGNED NOT NULL,
  `sede_id` bigint(20) UNSIGNED NOT NULL,
  `usu_id` bigint(20) UNSIGNED NOT NULL COMMENT 'Cajero que emite el recibo',
  `rc_fecha` date NOT NULL DEFAULT curdate(),
  `ped_id` bigint(20) UNSIGNED NOT NULL,
  `cli_id` bigint(20) UNSIGNED DEFAULT NULL COMMENT 'NULL = Público General',
  `fp_id` bigint(20) UNSIGNED NOT NULL,
  `rc_subtotal` decimal(14,2) NOT NULL DEFAULT 0.00,
  `rc_descuento` decimal(14,2) NOT NULL DEFAULT 0.00,
  `rc_propina` decimal(14,2) NOT NULL DEFAULT 0.00,
  `rc_total` decimal(14,2) NOT NULL DEFAULT 0.00,
  `rc_monto_rec` decimal(14,2) DEFAULT NULL COMMENT 'Monto recibido del cliente',
  `rc_cambio` decimal(14,2) NOT NULL DEFAULT 0.00,
  `rc_observacion` varchar(360) DEFAULT NULL,
  `rc_estado` enum('Activo','Anulado') NOT NULL DEFAULT 'Activo',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Recibos de caja / Facturas de ventas por sede';

--
-- Volcado de datos para la tabla `recibo_caja`
--

INSERT INTO `recibo_caja` (`rc_num`, `sede_id`, `usu_id`, `rc_fecha`, `ped_id`, `cli_id`, `fp_id`, `rc_subtotal`, `rc_descuento`, `rc_propina`, `rc_total`, `rc_monto_rec`, `rc_cambio`, `rc_observacion`, `rc_estado`, `created_at`, `updated_at`) VALUES
(1, 1, 4, '2026-03-11', 3, 2, 1, 90000.00, 0.00, 9000.00, 99000.00, 100000.00, 1000.00, NULL, 'Activo', '2026-03-11 17:39:00', '2026-03-11 17:39:00');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `recibo_caja_detalle`
--

CREATE TABLE `recibo_caja_detalle` (
  `rcd_id` bigint(20) UNSIGNED NOT NULL,
  `rc_num` bigint(20) UNSIGNED NOT NULL,
  `pla_id` bigint(20) UNSIGNED NOT NULL,
  `rcd_cantidad` smallint(5) UNSIGNED NOT NULL DEFAULT 1,
  `rcd_precio` decimal(10,2) NOT NULL COMMENT 'Precio unitario al momento de la factura',
  `rcd_descuento` decimal(10,2) NOT NULL DEFAULT 0.00,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Detalle de líneas de cada recibo de caja';

--
-- Volcado de datos para la tabla `recibo_caja_detalle`
--

INSERT INTO `recibo_caja_detalle` (`rcd_id`, `rc_num`, `pla_id`, `rcd_cantidad`, `rcd_precio`, `rcd_descuento`, `created_at`, `updated_at`) VALUES
(1, 1, 4, 3, 25000.00, 0.00, '2026-03-11 17:39:12', '2026-03-11 17:39:12'),
(2, 1, 6, 3, 5000.00, 0.00, '2026-03-11 17:39:12', '2026-03-11 17:39:12');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reservacion`
--

CREATE TABLE `reservacion` (
  `res_id` bigint(20) UNSIGNED NOT NULL,
  `sede_id` bigint(20) UNSIGNED NOT NULL,
  `res_nombre_cli` varchar(100) NOT NULL,
  `res_telefono` varchar(20) DEFAULT NULL,
  `res_fecha_hora` datetime NOT NULL,
  `res_personas` int(11) NOT NULL DEFAULT 2,
  `mesa_id` bigint(20) UNSIGNED DEFAULT NULL,
  `res_nota` text DEFAULT NULL,
  `res_estado` enum('Pendiente','Confirmada','Cancelada') NOT NULL DEFAULT 'Pendiente',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Reservaciones de mesas por sede';

--
-- Volcado de datos para la tabla `reservacion`
--

INSERT INTO `reservacion` (`res_id`, `sede_id`, `res_nombre_cli`, `res_telefono`, `res_fecha_hora`, `res_personas`, `mesa_id`, `res_nota`, `res_estado`, `created_at`, `updated_at`) VALUES
(1, 1, 'Ana Rodríguez', '3001112233', '2026-03-12 12:30:00', 4, 4, 'Celebración cumpleaños', 'Confirmada', '2026-03-11 17:39:25', '2026-03-11 17:39:25'),
(2, 1, 'Empresa XYZ', '3209988776', '2026-03-13 19:00:00', 8, 5, NULL, 'Pendiente', '2026-03-11 17:39:25', '2026-03-11 17:39:25');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sede`
--

CREATE TABLE `sede` (
  `sede_id` bigint(20) UNSIGNED NOT NULL,
  `sede_nombre` varchar(120) NOT NULL,
  `sede_direccion` varchar(200) DEFAULT NULL,
  `sede_telefono` varchar(30) DEFAULT NULL,
  `sede_estado` enum('Activo','Inactivo') NOT NULL DEFAULT 'Activo',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Sucursales / sedes del restaurante';

--
-- Volcado de datos para la tabla `sede`
--

INSERT INTO `sede` (`sede_id`, `sede_nombre`, `sede_direccion`, `sede_telefono`, `sede_estado`, `created_at`, `updated_at`) VALUES
(1, 'Sede Principal', 'Calle Principal #123', '3000000000', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(2, 'Sede Norte', 'Carrera 5 #10-20', '3011111111', 'Activo', '2026-03-11 17:35:02', '2026-03-11 17:35:02');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tipo_estado`
--

CREATE TABLE `tipo_estado` (
  `tes_id` bigint(20) UNSIGNED NOT NULL,
  `tes_descripcion` varchar(50) NOT NULL COMMENT 'Ej: Comanda, Pedido, Plato, PQRS, Insumo',
  `tes_estado` enum('Activo','Inactivo') NOT NULL DEFAULT 'Activo',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Clasificación de grupos de estados';

--
-- Volcado de datos para la tabla `tipo_estado`
--

INSERT INTO `tipo_estado` (`tes_id`, `tes_descripcion`, `tes_estado`, `created_at`, `updated_at`) VALUES
(1, 'Comanda/Detalle Pedido', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(2, 'Pedido', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(3, 'Plato', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(4, 'PQRS', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(5, 'Insumo Bodega', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tipo_tpqrs`
--

CREATE TABLE `tipo_tpqrs` (
  `tpqrs_id` bigint(20) UNSIGNED NOT NULL,
  `tpqrs_descripcion` varchar(60) NOT NULL COMMENT 'Peticion, Queja, Reclamo, Sugerencia',
  `tpqrs_estado` enum('Activo','Inactivo') NOT NULL DEFAULT 'Activo',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Tipos de PQRS: Petición, Queja, Reclamo, Sugerencia';

--
-- Volcado de datos para la tabla `tipo_tpqrs`
--

INSERT INTO `tipo_tpqrs` (`tpqrs_id`, `tpqrs_descripcion`, `tpqrs_estado`, `created_at`, `updated_at`) VALUES
(1, 'Peticion', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(2, 'Queja', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(3, 'Reclamo', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54'),
(4, 'Sugerencia', 'Activo', '2026-03-05 01:44:54', '2026-03-05 01:44:54');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `usu_id` bigint(20) UNSIGNED NOT NULL,
  `usu_nombre` varchar(60) NOT NULL,
  `usu_apellido` varchar(60) NOT NULL,
  `usu_direccion` varchar(100) NOT NULL DEFAULT '',
  `usu_telefono` varchar(20) NOT NULL DEFAULT '',
  `usu_correo` varchar(100) NOT NULL,
  `email_verified_at` timestamp NULL DEFAULT NULL COMMENT 'Compatibilidad Laravel/frameworks',
  `perf_id` bigint(20) UNSIGNED DEFAULT NULL,
  `usu_login` varchar(30) NOT NULL,
  `usu_pass` varchar(255) NOT NULL COMMENT 'Hash bcrypt/argon2 — NUNCA texto plano',
  `remember_token` varchar(100) DEFAULT NULL COMMENT 'Token sesión persistente (frameworks)',
  `usu_estado` enum('Activo','Inactivo') NOT NULL DEFAULT 'Activo',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Usuarios del sistema (meseros, admin, cocina, cajeros)';

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`usu_id`, `usu_nombre`, `usu_apellido`, `usu_direccion`, `usu_telefono`, `usu_correo`, `email_verified_at`, `perf_id`, `usu_login`, `usu_pass`, `remember_token`, `usu_estado`, `created_at`, `updated_at`) VALUES
(2, 'Luis', 'Ocampo', 'Calle 37N#2Bn-93', '3026653614', 'lmog240@gmail.com', NULL, 1, 'luis.admin', '$2a$12$zz1j3g0pZkd676J4oyf7Z.F8IbonQ9o5ABPnklYno3XDiYPOvSIqm', NULL, 'Activo', '2026-03-11 17:33:02', '2026-03-11 18:18:11'),
(3, 'Carlos', 'Pérez', 'Cra 10 #5-30', '3101234567', 'carlos@rest.com', NULL, 2, 'carlos.mesero', '$2a$12$JsEGSpJtogyK3r5o94Re7e.CkZoROo.z.O5aT67RYutMuHzkp1trO', NULL, 'Activo', '2026-03-11 17:33:02', '2026-03-11 18:18:11'),
(4, 'María', 'Gómez', 'Av 6N #12-45', '3209876543', 'maria@rest.com', NULL, 4, 'maria.cajero', '$2a$12$7aMJFCD0O09bGMPxKsTWCepPRMWpVvFwBrq5zcTCAa2JiY3CI9Vfa', NULL, 'Activo', '2026-03-11 17:33:02', '2026-03-11 18:18:11'),
(5, 'Pedro', 'Sánchez', 'Calle 8 #3-10', '3156789012', 'pedro@rest.com', NULL, 3, 'pedro.cocina', '$2a$12$jRrHWYf4RWnIS3RmQNNKDe2GhuWWlWALgODtuI2R8yjVs3f0Lsh0K', NULL, 'Activo', '2026-03-11 17:33:02', '2026-03-11 18:18:11');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `area_mesa`
--
ALTER TABLE `area_mesa`
  ADD PRIMARY KEY (`area_id`),
  ADD KEY `idx_area_sede` (`sede_id`);

--
-- Indices de la tabla `categoria_insumo`
--
ALTER TABLE `categoria_insumo`
  ADD PRIMARY KEY (`cins_id`);

--
-- Indices de la tabla `categoria_plato`
--
ALTER TABLE `categoria_plato`
  ADD PRIMARY KEY (`cat_id`);

--
-- Indices de la tabla `cliente`
--
ALTER TABLE `cliente`
  ADD PRIMARY KEY (`cli_id`),
  ADD UNIQUE KEY `uk_cli_documento` (`cli_num_documento`),
  ADD UNIQUE KEY `uk_cli_correo` (`cli_correo`);

--
-- Indices de la tabla `concepto_egreso`
--
ALTER TABLE `concepto_egreso`
  ADD PRIMARY KEY (`con_id`);

--
-- Indices de la tabla `configuracion`
--
ALTER TABLE `configuracion`
  ADD PRIMARY KEY (`cfg_id`),
  ADD UNIQUE KEY `uk_cfg_clave` (`cfg_clave`);

--
-- Indices de la tabla `encabezado_egresos`
--
ALTER TABLE `encabezado_egresos`
  ADD PRIMARY KEY (`egr_id`),
  ADD UNIQUE KEY `uk_no_egreso` (`no_egreso`),
  ADD KEY `idx_ee_fecha` (`fecha_documento`),
  ADD KEY `idx_ee_fp` (`fp_id`),
  ADD KEY `idx_ee_concepto` (`con_id`),
  ADD KEY `idx_ee_usuario` (`usu_id`),
  ADD KEY `idx_ee_sede` (`sede_id`);

--
-- Indices de la tabla `estado`
--
ALTER TABLE `estado`
  ADD PRIMARY KEY (`est_id`),
  ADD KEY `idx_estado_tes` (`tes_id`);

--
-- Indices de la tabla `forma_pago`
--
ALTER TABLE `forma_pago`
  ADD PRIMARY KEY (`fp_id`);

--
-- Indices de la tabla `insumo`
--
ALTER TABLE `insumo`
  ADD PRIMARY KEY (`ins_id`),
  ADD KEY `idx_insumo_cat` (`cins_id`),
  ADD KEY `idx_insumo_pres` (`pres_id`),
  ADD KEY `idx_insumo_sede` (`sede_id`);

--
-- Indices de la tabla `inventario_log`
--
ALTER TABLE `inventario_log`
  ADD PRIMARY KEY (`log_id`),
  ADD KEY `idx_log_insumo` (`ins_id`),
  ADD KEY `idx_log_usuario` (`usu_id`),
  ADD KEY `idx_log_tipo` (`log_tipo`);

--
-- Indices de la tabla `mesa`
--
ALTER TABLE `mesa`
  ADD PRIMARY KEY (`mesa_id`),
  ADD UNIQUE KEY `uk_mesa_numero_sede` (`mesa_numero`,`sede_id`),
  ADD KEY `idx_mesa_sede` (`sede_id`),
  ADD KEY `idx_mesa_area` (`area_id`);

--
-- Indices de la tabla `pedido`
--
ALTER TABLE `pedido`
  ADD PRIMARY KEY (`ped_id`),
  ADD KEY `idx_pedido_fecha` (`ped_fecha`),
  ADD KEY `idx_pedido_usu` (`usu_id`),
  ADD KEY `idx_pedido_mesa` (`mesa_id`),
  ADD KEY `idx_pedido_est` (`est_id`),
  ADD KEY `idx_pedido_sede` (`sede_id`);

--
-- Indices de la tabla `pedido_detalle`
--
ALTER TABLE `pedido_detalle`
  ADD PRIMARY KEY (`ped_det_id`),
  ADD KEY `idx_pd_pedido` (`ped_id`),
  ADD KEY `idx_pd_plato` (`pla_id`),
  ADD KEY `idx_pd_estado` (`est_id`);

--
-- Indices de la tabla `perfil`
--
ALTER TABLE `perfil`
  ADD PRIMARY KEY (`perf_id`);

--
-- Indices de la tabla `plato`
--
ALTER TABLE `plato`
  ADD PRIMARY KEY (`pla_id`),
  ADD KEY `idx_plato_cat` (`cat_id`),
  ADD KEY `idx_plato_estado` (`est_id`);

--
-- Indices de la tabla `plato_ingrediente`
--
ALTER TABLE `plato_ingrediente`
  ADD PRIMARY KEY (`pi_id`),
  ADD UNIQUE KEY `uk_plato_insumo` (`pla_id`,`ins_id`),
  ADD KEY `idx_pi_plato` (`pla_id`),
  ADD KEY `idx_pi_insumo` (`ins_id`);

--
-- Indices de la tabla `pqrs`
--
ALTER TABLE `pqrs`
  ADD PRIMARY KEY (`pqrs_id`),
  ADD KEY `idx_pqrs_fecha` (`pqrs_fecha`),
  ADD KEY `idx_pqrs_tipo` (`tpqrs_id`),
  ADD KEY `idx_pqrs_estado` (`est_id`),
  ADD KEY `idx_pqrs_usu` (`usu_id_responde`);

--
-- Indices de la tabla `presentacion`
--
ALTER TABLE `presentacion`
  ADD PRIMARY KEY (`pres_id`);

--
-- Indices de la tabla `recibo_caja`
--
ALTER TABLE `recibo_caja`
  ADD PRIMARY KEY (`rc_num`),
  ADD KEY `idx_rc_fecha` (`rc_fecha`),
  ADD KEY `idx_rc_pedido` (`ped_id`),
  ADD KEY `idx_rc_cliente` (`cli_id`),
  ADD KEY `idx_rc_usuario` (`usu_id`),
  ADD KEY `idx_rc_fp` (`fp_id`),
  ADD KEY `idx_rc_sede` (`sede_id`);

--
-- Indices de la tabla `recibo_caja_detalle`
--
ALTER TABLE `recibo_caja_detalle`
  ADD PRIMARY KEY (`rcd_id`),
  ADD KEY `idx_rcd_recibo` (`rc_num`),
  ADD KEY `idx_rcd_plato` (`pla_id`);

--
-- Indices de la tabla `reservacion`
--
ALTER TABLE `reservacion`
  ADD PRIMARY KEY (`res_id`),
  ADD KEY `idx_res_mesa` (`mesa_id`),
  ADD KEY `idx_res_fecha` (`res_fecha_hora`),
  ADD KEY `idx_res_sede` (`sede_id`);

--
-- Indices de la tabla `sede`
--
ALTER TABLE `sede`
  ADD PRIMARY KEY (`sede_id`);

--
-- Indices de la tabla `tipo_estado`
--
ALTER TABLE `tipo_estado`
  ADD PRIMARY KEY (`tes_id`);

--
-- Indices de la tabla `tipo_tpqrs`
--
ALTER TABLE `tipo_tpqrs`
  ADD PRIMARY KEY (`tpqrs_id`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`usu_id`),
  ADD UNIQUE KEY `uk_usu_login` (`usu_login`),
  ADD UNIQUE KEY `uk_usu_correo` (`usu_correo`),
  ADD KEY `idx_usu_perf` (`perf_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `area_mesa`
--
ALTER TABLE `area_mesa`
  MODIFY `area_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `categoria_insumo`
--
ALTER TABLE `categoria_insumo`
  MODIFY `cins_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `categoria_plato`
--
ALTER TABLE `categoria_plato`
  MODIFY `cat_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `cliente`
--
ALTER TABLE `cliente`
  MODIFY `cli_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `concepto_egreso`
--
ALTER TABLE `concepto_egreso`
  MODIFY `con_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `configuracion`
--
ALTER TABLE `configuracion`
  MODIFY `cfg_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `encabezado_egresos`
--
ALTER TABLE `encabezado_egresos`
  MODIFY `egr_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `estado`
--
ALTER TABLE `estado`
  MODIFY `est_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT de la tabla `forma_pago`
--
ALTER TABLE `forma_pago`
  MODIFY `fp_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `insumo`
--
ALTER TABLE `insumo`
  MODIFY `ins_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `inventario_log`
--
ALTER TABLE `inventario_log`
  MODIFY `log_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `mesa`
--
ALTER TABLE `mesa`
  MODIFY `mesa_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `pedido`
--
ALTER TABLE `pedido`
  MODIFY `ped_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `pedido_detalle`
--
ALTER TABLE `pedido_detalle`
  MODIFY `ped_det_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `perfil`
--
ALTER TABLE `perfil`
  MODIFY `perf_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `plato`
--
ALTER TABLE `plato`
  MODIFY `pla_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `plato_ingrediente`
--
ALTER TABLE `plato_ingrediente`
  MODIFY `pi_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `pqrs`
--
ALTER TABLE `pqrs`
  MODIFY `pqrs_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `presentacion`
--
ALTER TABLE `presentacion`
  MODIFY `pres_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `recibo_caja`
--
ALTER TABLE `recibo_caja`
  MODIFY `rc_num` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `recibo_caja_detalle`
--
ALTER TABLE `recibo_caja_detalle`
  MODIFY `rcd_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `reservacion`
--
ALTER TABLE `reservacion`
  MODIFY `res_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `sede`
--
ALTER TABLE `sede`
  MODIFY `sede_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `tipo_estado`
--
ALTER TABLE `tipo_estado`
  MODIFY `tes_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `tipo_tpqrs`
--
ALTER TABLE `tipo_tpqrs`
  MODIFY `tpqrs_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `usu_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `area_mesa`
--
ALTER TABLE `area_mesa`
  ADD CONSTRAINT `fk_area_sede` FOREIGN KEY (`sede_id`) REFERENCES `sede` (`sede_id`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `encabezado_egresos`
--
ALTER TABLE `encabezado_egresos`
  ADD CONSTRAINT `fk_ee_concepto` FOREIGN KEY (`con_id`) REFERENCES `concepto_egreso` (`con_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ee_fp` FOREIGN KEY (`fp_id`) REFERENCES `forma_pago` (`fp_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ee_sede` FOREIGN KEY (`sede_id`) REFERENCES `sede` (`sede_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ee_usuario` FOREIGN KEY (`usu_id`) REFERENCES `usuario` (`usu_id`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `estado`
--
ALTER TABLE `estado`
  ADD CONSTRAINT `fk_estado_tipo` FOREIGN KEY (`tes_id`) REFERENCES `tipo_estado` (`tes_id`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `insumo`
--
ALTER TABLE `insumo`
  ADD CONSTRAINT `fk_insumo_categoria` FOREIGN KEY (`cins_id`) REFERENCES `categoria_insumo` (`cins_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_insumo_presentacion` FOREIGN KEY (`pres_id`) REFERENCES `presentacion` (`pres_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_insumo_sede` FOREIGN KEY (`sede_id`) REFERENCES `sede` (`sede_id`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `inventario_log`
--
ALTER TABLE `inventario_log`
  ADD CONSTRAINT `fk_log_insumo` FOREIGN KEY (`ins_id`) REFERENCES `insumo` (`ins_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_log_usuario` FOREIGN KEY (`usu_id`) REFERENCES `usuario` (`usu_id`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `mesa`
--
ALTER TABLE `mesa`
  ADD CONSTRAINT `fk_mesa_area` FOREIGN KEY (`area_id`) REFERENCES `area_mesa` (`area_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_mesa_sede` FOREIGN KEY (`sede_id`) REFERENCES `sede` (`sede_id`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `pedido`
--
ALTER TABLE `pedido`
  ADD CONSTRAINT `fk_pedido_estado` FOREIGN KEY (`est_id`) REFERENCES `estado` (`est_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_pedido_mesa` FOREIGN KEY (`mesa_id`) REFERENCES `mesa` (`mesa_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_pedido_mesero` FOREIGN KEY (`usu_id`) REFERENCES `usuario` (`usu_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_pedido_sede` FOREIGN KEY (`sede_id`) REFERENCES `sede` (`sede_id`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `pedido_detalle`
--
ALTER TABLE `pedido_detalle`
  ADD CONSTRAINT `fk_pd_estado` FOREIGN KEY (`est_id`) REFERENCES `estado` (`est_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_pd_pedido` FOREIGN KEY (`ped_id`) REFERENCES `pedido` (`ped_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_pd_plato` FOREIGN KEY (`pla_id`) REFERENCES `plato` (`pla_id`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `plato`
--
ALTER TABLE `plato`
  ADD CONSTRAINT `fk_plato_categoria` FOREIGN KEY (`cat_id`) REFERENCES `categoria_plato` (`cat_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_plato_estado` FOREIGN KEY (`est_id`) REFERENCES `estado` (`est_id`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `plato_ingrediente`
--
ALTER TABLE `plato_ingrediente`
  ADD CONSTRAINT `fk_pi_insumo` FOREIGN KEY (`ins_id`) REFERENCES `insumo` (`ins_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_pi_plato` FOREIGN KEY (`pla_id`) REFERENCES `plato` (`pla_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `pqrs`
--
ALTER TABLE `pqrs`
  ADD CONSTRAINT `fk_pqrs_estado` FOREIGN KEY (`est_id`) REFERENCES `estado` (`est_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_pqrs_tipo` FOREIGN KEY (`tpqrs_id`) REFERENCES `tipo_tpqrs` (`tpqrs_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_pqrs_usuario` FOREIGN KEY (`usu_id_responde`) REFERENCES `usuario` (`usu_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Filtros para la tabla `recibo_caja`
--
ALTER TABLE `recibo_caja`
  ADD CONSTRAINT `fk_rc_cliente` FOREIGN KEY (`cli_id`) REFERENCES `cliente` (`cli_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_rc_fp` FOREIGN KEY (`fp_id`) REFERENCES `forma_pago` (`fp_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_rc_pedido` FOREIGN KEY (`ped_id`) REFERENCES `pedido` (`ped_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_rc_sede` FOREIGN KEY (`sede_id`) REFERENCES `sede` (`sede_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_rc_usuario` FOREIGN KEY (`usu_id`) REFERENCES `usuario` (`usu_id`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `recibo_caja_detalle`
--
ALTER TABLE `recibo_caja_detalle`
  ADD CONSTRAINT `fk_rcd_plato` FOREIGN KEY (`pla_id`) REFERENCES `plato` (`pla_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_rcd_recibo` FOREIGN KEY (`rc_num`) REFERENCES `recibo_caja` (`rc_num`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `reservacion`
--
ALTER TABLE `reservacion`
  ADD CONSTRAINT `fk_reservacion_mesa` FOREIGN KEY (`mesa_id`) REFERENCES `mesa` (`mesa_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_reservacion_sede` FOREIGN KEY (`sede_id`) REFERENCES `sede` (`sede_id`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD CONSTRAINT `fk_usuario_perfil` FOREIGN KEY (`perf_id`) REFERENCES `perfil` (`perf_id`) ON DELETE SET NULL ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
