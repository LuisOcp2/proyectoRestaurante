# 🍽️ SISTEMA RESTAURANTE 2026 — DOCUMENTACIÓN TÉCNICA COMPLETA
> **Base de datos:** `restaurante_2026` | **Lenguaje:** Java | **Patrón:** MVC | **Motor BD:** MySQL / MariaDB 10.11
> **Fecha:** Marzo 2026 | **Equipo:** Luis · Sebastián · Angie · Nicolás

## 📑 TABLA DE CONTENIDOS

1. [Visión General del Proyecto](#1-visión-general)
2. [Stack Tecnológico](#2-stack-tecnológico)
3. [Estructura Global del Proyecto](#3-estructura-global-del-proyecto)
4. [Base de Datos — Tablas y Relaciones](#4-base-de-datos)
5. [Catálogo de Estados](#5-catálogo-de-estados)
6. [Módulo Luis — Pedidos y Comandas](#6-módulo-luis--pedidos-y-comandas)
7. [Módulo Sebastián — Egresos y Seguridad](#7-módulo-sebastián--egresos-y-seguridad)
8. [Módulo Angie — Facturación y Roles](#8-módulo-angie--facturación-y-roles)
9. [Módulo Nicolás — Inventario / PQRS](#9-módulo-nicolás--inventario--pqrs)
10. [Clases Compartidas (Util)](#10-clases-compartidas-util)
11. [Vistas FXML por Integrante](#11-vistas-fxml-por-integrante)
12. [Flujos de Estados del Sistema](#12-flujos-de-estados-del-sistema)
13. [Consultas SQL de Referencia](#13-consultas-sql-de-referencia)
14. [Reglas de Negocio Globales](#14-reglas-de-negocio-globales)
15. [Entregables por Fase](#15-entregables-por-fase)
16. [Configuración y Despliegue](#16-configuración-y-despliegue)
17. [Convenciones de Código](#17-convenciones-de-código)

---

## 1. VISIÓN GENERAL

Sistema de gestión para un **restaurante multisede** que cubre pedidos, comandas, facturación,
egresos, inventario de bodega y PQRS. Desarrollado en Java con POO estricta, sin código
estructurado ni métodos `main` únicos.

### Equipo de Desarrollo

| Integrante   | Módulo                        | Tablas Principales                              |
|--------------|-------------------------------|-------------------------------------------------|
| **Luis**     | Pedidos y Comandas + App Móvil | `pedido`, `pedido_detalle`, `plato`, `mesa`    |
| **Sebastián**| Egresos y Seguridad           | `encabezado_egresos`, `usuario`, `perfil`       |
| **Angie**    | Facturación y Roles           | `recibo_caja`, `recibo_caja_detalle`, `cliente` |
| **Nicolás**  | Inventario (Bodega) o PQRS   | `inventario_log`, `insumo` / `pqrs`            |

---

## 2. STACK TECNOLÓGICO

| Capa              | Tecnología                                      |
|-------------------|-------------------------------------------------|
| Lenguaje          | Java (JDK 17+)                                  |
| Paradigma         | Programación Orientada a Objetos (POO)          |
| Patrón            | Modelo–Vista–Controlador (MVC)                  |
| Interfaz Desktop  | JavaFX + archivos `.fxml`                       |
| Base de Datos     | MySQL / MariaDB — base `restaurante_2026`       |
| Seguridad         | bcrypt o argon2 para contraseñas                |
| App Móvil (Luis)  | Android Java                                    |
| Auditoría         | `created_at` y `updated_at` en todas las tablas |

---

## 3. ESTRUCTURA GLOBAL DEL PROYECTO

```

com.restaurante
├── modelos/                  \# POJOs que mapean tablas de BD
│   ├── Pedido.java
│   ├── PedidoDetalle.java
│   ├── Plato.java
│   ├── Mesa.java
│   ├── AreaMesa.java
│   ├── CategoriaPlato.java
│   ├── EncabezadoEgresos.java
│   ├── ConceptoEgreso.java
│   ├── FormaPago.java
│   ├── Usuario.java
│   ├── Perfil.java
│   ├── ReciboCaja.java
│   ├── ReciboCajaDetalle.java
│   ├── Cliente.java
│   ├── Insumo.java
│   ├── InventarioLog.java
│   ├── CategoriaInsumo.java
│   ├── Presentacion.java
│   ├── Pqrs.java
│   ├── TipoTpqrs.java
│   ├── Estado.java
│   └── Sede.java
│
├── dao/                      \# Acceso a datos (queries MySQL)
│   ├── PedidoDAO.java
│   ├── PedidoDetalleDAO.java
│   ├── PlatoDAO.java
│   ├── MesaDAO.java
│   ├── EncabezadoEgresosDAO.java
│   ├── ConceptoEgresoDAO.java
│   ├── FormaPagoDAO.java
│   ├── UsuarioDAO.java
│   ├── PerfilDAO.java
│   ├── ReciboCajaDAO.java
│   ├── ReciboCajaDetalleDAO.java
│   ├── ClienteDAO.java
│   ├── InsumoDAO.java
│   ├── InventarioLogDAO.java
│   └── PqrsDAO.java
│
├── controladores/            \# Lógica de negocio y eventos UI
│   ├── ControladorPedido.java
│   ├── ControladorComanda.java
│   ├── ControladorPlato.java
│   ├── ControladorMesa.java
│   ├── ControladorEgresos.java
│   ├── ControladorLogin.java
│   ├── ControladorFacturacion.java
│   ├── ControladorCliente.java
│   ├── ControladorInventario.java
│   └── ControladorPqrs.java
│
├── vistas/                   \# Archivos FXML
│   ├── VistaLogin.fxml
│   ├── VistaPedidos.fxml
│   ├── VistaComandas.fxml         ← Luis (Cocina)
│   ├── VistaAreasMesa.fxml        ← Luis (Config)
│   ├── VistaCategoriasPlato.fxml  ← Luis (Config)
│   ├── VistaConceptoEgreso.fxml   ← Sebastián
│   ├── VistaFormaPago.fxml        ← Sebastián
│   ├── VistaReciboCaja.fxml       ← Angie
│   ├── VistaClientes.fxml         ← Angie
│   ├── VistaPerfiles.fxml         ← Angie
│   ├── VistaInventarioLog.fxml    ← Nicolás
│   ├── VistaPresentacion.fxml     ← Nicolás
│   ├── VistaCategoriaInsumo.fxml  ← Nicolás
│   └── VistaSedes.fxml            ← Admin general
│
└── util/
├── ConexionBD.java            \# Singleton de conexión reutilizable
├── ValidadorCampos.java       \# Validaciones centralizadas
└── HashSeguridad.java         \# Encriptación bcrypt/argon2

```

---

## 4. BASE DE DATOS

### Diagrama de Tablas por Módulo

```

[COMPARTIDAS / CONFIGURACIÓN GLOBAL]
sede ──── area_mesa ──── mesa
└─ insumo
tipo_estado ──── estado
perfil ──── usuario
forma_pago
configuracion

[MÓDULO LUIS]
categoria_plato ──── plato ──── pedido_detalle
mesa ──── pedido ──── pedido_detalle
usuario

[MÓDULO SEBASTIÁN]
concepto_egreso ──── encabezado_egresos
forma_pago      ────┘
sede            ────┘
usuario         ────┘

[MÓDULO ANGIE]
cliente ──── recibo_caja ──── recibo_caja_detalle
pedido  ────┘                └─── plato
forma_pago ─┘
usuario ────┘

[MÓDULO NICOLÁS]
categoria_insumo ──── insumo ──── inventario_log
presentacion     ────┘             └─── usuario
sede             ────┘
----- o -----
tipo_tpqrs ──── pqrs ──── estado
└─── usuario

```

---

### Esquema Completo de Tablas

#### `sede` — Sucursales del restaurante
```sql
CREATE TABLE sede (
  sede_id       BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  sede_nombre   VARCHAR(120) NOT NULL,
  sede_direccion VARCHAR(200) DEFAULT NULL,
  sede_telefono VARCHAR(30) DEFAULT NULL,
  sede_estado   ENUM('Activo','Inactivo') DEFAULT 'Activo',
  created_at    TIMESTAMP DEFAULT current_timestamp(),
  updated_at    TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
-- Dato inicial: sede_id=1, 'Sede Principal'
```


#### `tipo_estado` — Clasificación de grupos de estados

```sql
CREATE TABLE tipo_estado (
  tes_id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  tes_descripcion VARCHAR(50) NOT NULL,  -- Comanda/Detalle Pedido, Pedido, Plato, PQRS, Insumo Bodega
  tes_estado      ENUM('Activo','Inactivo') DEFAULT 'Activo',
  created_at      TIMESTAMP DEFAULT current_timestamp(),
  updated_at      TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `estado` — Catálogo unificado de estados

```sql
CREATE TABLE estado (
  est_id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  est_descripcion VARCHAR(60) NOT NULL,
  tes_id          BIGINT UNSIGNED NOT NULL,  -- FK → tipo_estado
  est_estado      ENUM('Activo','Inactivo') DEFAULT 'Activo',
  created_at      TIMESTAMP DEFAULT current_timestamp(),
  updated_at      TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `perfil` — Roles del sistema

```sql
CREATE TABLE perfil (
  perf_id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  perf_descripcion VARCHAR(60) NOT NULL,  -- Administrador, Mesero, Cocinero, Cajero
  perf_estado      ENUM('Activo','Inactivo') DEFAULT 'Activo',
  created_at       TIMESTAMP DEFAULT current_timestamp(),
  updated_at       TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `usuario` — Usuarios del sistema

```sql
CREATE TABLE usuario (
  usu_id       BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  usu_nombre   VARCHAR(60) NOT NULL,
  usu_apellido VARCHAR(60) NOT NULL,
  usu_direccion VARCHAR(100) DEFAULT '',
  usu_telefono VARCHAR(20) DEFAULT '',
  usu_correo   VARCHAR(100) NOT NULL UNIQUE,
  perf_id      BIGINT UNSIGNED DEFAULT NULL,  -- FK → perfil
  usu_login    VARCHAR(30) NOT NULL UNIQUE,
  usu_pass     VARCHAR(255) NOT NULL,          -- ⚠️ Hash bcrypt/argon2 NUNCA texto plano
  usu_estado   ENUM('Activo','Inactivo') DEFAULT 'Activo',
  created_at   TIMESTAMP DEFAULT current_timestamp(),
  updated_at   TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `forma_pago` — Formas de pago disponibles

```sql
CREATE TABLE forma_pago (
  fp_id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  fp_descripcion VARCHAR(60) NOT NULL,  -- Efectivo, Transferencia Bancaria, Tarjeta Débito, Tarjeta Crédito
  fp_estado      ENUM('Activo','Inactivo') DEFAULT 'Activo',
  created_at     TIMESTAMP DEFAULT current_timestamp(),
  updated_at     TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `configuracion` — Parámetros globales del sistema

```sql
CREATE TABLE configuracion (
  cfg_id    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  cfg_clave VARCHAR(100) NOT NULL UNIQUE,  -- company_name, currency_symbol, ticket_footer...
  cfg_valor TEXT DEFAULT NULL,
  created_at TIMESTAMP DEFAULT current_timestamp(),
  updated_at TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `area_mesa` — Salones o zonas por sede

```sql
CREATE TABLE area_mesa (
  area_id     BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  sede_id     BIGINT UNSIGNED NOT NULL,   -- FK → sede
  area_nombre VARCHAR(100) NOT NULL,      -- Salón Principal, Terraza...
  area_estado ENUM('Activo','Inactivo') DEFAULT 'Activo',
  created_at  TIMESTAMP DEFAULT current_timestamp(),
  updated_at  TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `mesa` — Mesas del restaurante

```sql
CREATE TABLE mesa (
  mesa_id    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  sede_id    BIGINT UNSIGNED NOT NULL,    -- FK → sede
  area_id    BIGINT UNSIGNED DEFAULT NULL, -- FK → area_mesa
  mesa_numero VARCHAR(10) NOT NULL,
  capacidad  TINYINT UNSIGNED DEFAULT 4,
  x_pos      INT DEFAULT 0,              -- Posición X para mapa visual
  y_pos      INT DEFAULT 0,              -- Posición Y para mapa visual
  estado     ENUM('Disponible','Ocupada','Reservada','Inactiva') DEFAULT 'Disponible',
  created_at TIMESTAMP DEFAULT current_timestamp(),
  updated_at TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  UNIQUE KEY uk_mesa_numero_sede (mesa_numero, sede_id)
);
```


#### `categoria_plato` — Categorías del menú

```sql
CREATE TABLE categoria_plato (
  cat_id     BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  cat_nombre VARCHAR(100) NOT NULL,  -- Entradas, Platos Fuertes, Bebidas...
  cat_imagen VARCHAR(255) DEFAULT NULL,
  cat_estado TINYINT(1) DEFAULT 1,
  created_at TIMESTAMP DEFAULT current_timestamp(),
  updated_at TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `plato` — Carta del restaurante

```sql
CREATE TABLE plato (
  pla_id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  cat_id          BIGINT UNSIGNED NOT NULL,  -- FK → categoria_plato
  pla_descripcion VARCHAR(150) NOT NULL,
  pla_codigo      VARCHAR(20) DEFAULT NULL,
  pla_precio      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  pla_costo       DECIMAL(10,2) DEFAULT NULL,
  pla_imagen      VARCHAR(255) DEFAULT NULL,
  est_id          BIGINT UNSIGNED NOT NULL,  -- FK → estado (Disponible/Agotado/Inactivo)
  created_at      TIMESTAMP DEFAULT current_timestamp(),
  updated_at      TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `pedido` — Cabecera de la orden

```sql
CREATE TABLE pedido (
  ped_id   BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  sede_id  BIGINT UNSIGNED NOT NULL,  -- FK → sede
  ped_fecha DATE NOT NULL DEFAULT curdate(),
  usu_id   BIGINT UNSIGNED NOT NULL,  -- FK → usuario (mesero)
  mesa_id  BIGINT UNSIGNED NOT NULL,  -- FK → mesa
  est_id   BIGINT UNSIGNED NOT NULL,  -- FK → estado (Creado/Finalizado/Cancelado)
  ped_obs  VARCHAR(360) DEFAULT NULL,
  created_at TIMESTAMP DEFAULT current_timestamp(),
  updated_at TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `pedido_detalle` — Comanda de cocina

```sql
CREATE TABLE pedido_detalle (
  ped_det_id    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  ped_id        BIGINT UNSIGNED NOT NULL,  -- FK → pedido (CASCADE DELETE)
  pla_id        BIGINT UNSIGNED NOT NULL,  -- FK → plato
  ped_det_cant  SMALLINT UNSIGNED DEFAULT 1,
  ped_det_precio DECIMAL(10,2) NOT NULL,   -- Snapshot precio al momento de la venta
  ped_det_obser VARCHAR(255) DEFAULT NULL, -- Observaciones para cocina
  est_id        BIGINT UNSIGNED NOT NULL,  -- FK → estado (En Preparación/Servido/Cancelado)
  created_at    TIMESTAMP DEFAULT current_timestamp(),
  updated_at    TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `concepto_egreso` — Conceptos de egresos

```sql
CREATE TABLE concepto_egreso (
  con_id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  con_descripcion VARCHAR(120) NOT NULL,  -- Pago Nómina, Servicios Públicos, Compra Insumos...
  con_estado      ENUM('Activo','Inactivo') DEFAULT 'Activo',
  created_at      TIMESTAMP DEFAULT current_timestamp(),
  updated_at      TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `encabezado_egresos` — Registro de salidas de caja

```sql
CREATE TABLE encabezado_egresos (
  egr_id                 BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  sede_id                BIGINT UNSIGNED NOT NULL,  -- FK → sede
  no_egreso              BIGINT UNSIGNED NOT NULL UNIQUE,  -- Consecutivo único
  fecha_documento        DATE NOT NULL DEFAULT curdate(),
  tercero_identificacion VARCHAR(20) NOT NULL,      -- NIT o Cédula del beneficiario
  tercero_nombre         VARCHAR(150) DEFAULT NULL,
  detalle                VARCHAR(250) NOT NULL,
  fp_id                  BIGINT UNSIGNED NOT NULL,  -- FK → forma_pago
  con_id                 BIGINT UNSIGNED NOT NULL,  -- FK → concepto_egreso
  no_documento           VARCHAR(30) NOT NULL,       -- Número de comprobante
  valor_egreso           DECIMAL(14,2) NOT NULL DEFAULT 0.00,
  usu_id                 BIGINT UNSIGNED NOT NULL,  -- FK → usuario
  egr_estado             ENUM('Activo','Anulado') DEFAULT 'Activo',
  created_at             TIMESTAMP DEFAULT current_timestamp(),
  updated_at             TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `cliente` — Clientes del restaurante

```sql
CREATE TABLE cliente (
  cli_id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  cli_nombre         VARCHAR(60) NOT NULL,
  cli_apellidos      VARCHAR(60) NOT NULL DEFAULT '',
  cli_tipo_documento VARCHAR(20) DEFAULT 'CC',  -- CC, NIT, CE, Pasaporte, DNI
  cli_num_documento  VARCHAR(30) DEFAULT NULL UNIQUE,
  cli_direccion      VARCHAR(100) DEFAULT NULL,
  cli_telefono       VARCHAR(20) DEFAULT NULL,
  cli_correo         VARCHAR(100) DEFAULT NULL UNIQUE,
  cli_estado         ENUM('Activo','Inactivo') DEFAULT 'Activo',
  created_at         TIMESTAMP DEFAULT current_timestamp(),
  updated_at         TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `recibo_caja` — Facturas de venta

```sql
CREATE TABLE recibo_caja (
  rc_num       BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  sede_id      BIGINT UNSIGNED NOT NULL,           -- FK → sede
  usu_id       BIGINT UNSIGNED NOT NULL,           -- FK → usuario (cajero)
  rc_fecha     DATE NOT NULL DEFAULT curdate(),
  ped_id       BIGINT UNSIGNED NOT NULL,           -- FK → pedido
  cli_id       BIGINT UNSIGNED DEFAULT NULL,       -- FK → cliente (NULL = Público General)
  fp_id        BIGINT UNSIGNED NOT NULL,           -- FK → forma_pago
  rc_subtotal  DECIMAL(14,2) DEFAULT 0.00,
  rc_descuento DECIMAL(14,2) DEFAULT 0.00,
  rc_propina   DECIMAL(14,2) DEFAULT 0.00,
  rc_total     DECIMAL(14,2) DEFAULT 0.00,
  rc_monto_rec DECIMAL(14,2) DEFAULT NULL,        -- Monto recibido del cliente
  rc_cambio    DECIMAL(14,2) DEFAULT 0.00,
  rc_observacion VARCHAR(360) DEFAULT NULL,
  rc_estado    ENUM('Activo','Anulado') DEFAULT 'Activo',
  created_at   TIMESTAMP DEFAULT current_timestamp(),
  updated_at   TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `recibo_caja_detalle` — Líneas de la factura

```sql
CREATE TABLE recibo_caja_detalle (
  rcd_id       BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  rc_num       BIGINT UNSIGNED NOT NULL,   -- FK → recibo_caja (CASCADE DELETE)
  pla_id       BIGINT UNSIGNED NOT NULL,   -- FK → plato
  rcd_cantidad SMALLINT UNSIGNED DEFAULT 1,
  rcd_precio   DECIMAL(10,2) NOT NULL,    -- Snapshot precio al momento de factura
  rcd_descuento DECIMAL(10,2) DEFAULT 0.00,
  created_at   TIMESTAMP DEFAULT current_timestamp(),
  updated_at   TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `categoria_insumo` — Categorías de bodega

```sql
CREATE TABLE categoria_insumo (
  cins_id     BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  cins_nombre VARCHAR(100) NOT NULL,  -- Carnes, Lácteos, Granos, Bebidas...
  cins_imagen VARCHAR(255) DEFAULT NULL,
  cins_estado TINYINT(1) DEFAULT 1,
  created_at  TIMESTAMP DEFAULT current_timestamp(),
  updated_at  TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `presentacion` — Unidades de medida

```sql
CREATE TABLE presentacion (
  pres_id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  pres_descripcion VARCHAR(50) NOT NULL,   -- Kilogramo, Litro, Unidad...
  pres_abreviatura VARCHAR(10) NOT NULL,   -- Kg, L, Und, g, ml, Lb, @
  pres_estado      ENUM('Activo','Inactivo') DEFAULT 'Activo',
  created_at       TIMESTAMP DEFAULT current_timestamp(),
  updated_at       TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `insumo` — Materias primas de bodega

```sql
CREATE TABLE insumo (
  ins_id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  sede_id          BIGINT UNSIGNED NOT NULL,   -- FK → sede
  cins_id          BIGINT UNSIGNED NOT NULL,   -- FK → categoria_insumo
  pres_id          BIGINT UNSIGNED NOT NULL,   -- FK → presentacion (unidad de medida)
  ins_nombre       VARCHAR(150) NOT NULL,       -- Arroz, Carne de res, Aceite...
  ins_codigo       VARCHAR(30) DEFAULT NULL,
  ins_precio_compra DECIMAL(10,2) DEFAULT NULL,
  ins_stock        DECIMAL(12,3) DEFAULT 0.000, -- Stock actual
  ins_stock_min    DECIMAL(12,3) DEFAULT 0.000, -- Stock mínimo para alerta
  ins_vendible     TINYINT(1) DEFAULT 0,         -- 1 = también se vende al público
  ins_imagen       VARCHAR(255) DEFAULT NULL,
  ins_estado       TINYINT(1) DEFAULT 1,
  created_at       TIMESTAMP DEFAULT current_timestamp(),
  updated_at       TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `inventario_log` — Movimientos de bodega

```sql
CREATE TABLE inventario_log (
  log_id       BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  ins_id       BIGINT UNSIGNED NOT NULL,  -- FK → insumo (CASCADE DELETE)
  usu_id       BIGINT UNSIGNED NOT NULL,  -- FK → usuario
  log_tipo     ENUM('entrada','salida','ajuste','venta','merma') NOT NULL,
  log_cantidad DECIMAL(12,3) NOT NULL,
  log_stock_ant DECIMAL(12,3) DEFAULT NULL,  -- Stock antes del movimiento
  log_stock_nvo DECIMAL(12,3) DEFAULT NULL,  -- Stock después del movimiento
  log_nota     VARCHAR(255) DEFAULT NULL,
  created_at   TIMESTAMP DEFAULT current_timestamp(),
  updated_at   TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `plato_ingrediente` — Recetas (relación plato–insumo)

```sql
CREATE TABLE plato_ingrediente (
  pi_id      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  pla_id     BIGINT UNSIGNED NOT NULL,   -- FK → plato (CASCADE DELETE)
  ins_id     BIGINT UNSIGNED NOT NULL,   -- FK → insumo (CASCADE DELETE)
  pi_cantidad DECIMAL(10,3) NOT NULL,    -- Cantidad requerida por porción
  created_at TIMESTAMP DEFAULT current_timestamp(),
  updated_at TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  UNIQUE KEY uk_plato_insumo (pla_id, ins_id)
);
```


#### `pqrs` — Peticiones, Quejas, Reclamos y Sugerencias

```sql
CREATE TABLE pqrs (
  pqrs_id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  pqrs_fecha       DATE NOT NULL DEFAULT curdate(),
  pqrs_descripcion TEXT NOT NULL,
  pqrs_correo      VARCHAR(100) NOT NULL,
  pqrs_telefono    VARCHAR(20) NOT NULL,
  tpqrs_id         BIGINT UNSIGNED NOT NULL,           -- FK → tipo_tpqrs
  est_id           BIGINT UNSIGNED NOT NULL,            -- FK → estado (Pendiente/Atendida/Cerrada)
  pqrs_respuesta   TEXT DEFAULT NULL,
  usu_id_responde  BIGINT UNSIGNED DEFAULT NULL,        -- FK → usuario (SET NULL al borrar)
  created_at       TIMESTAMP DEFAULT current_timestamp(),
  updated_at       TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `tipo_tpqrs` — Tipos de PQRS

```sql
CREATE TABLE tipo_tpqrs (
  tpqrs_id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  tpqrs_descripcion VARCHAR(60) NOT NULL,  -- Peticion, Queja, Reclamo, Sugerencia
  tpqrs_estado      ENUM('Activo','Inactivo') DEFAULT 'Activo',
  created_at        TIMESTAMP DEFAULT current_timestamp(),
  updated_at        TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


#### `reservacion` — Reservas de mesas

```sql
CREATE TABLE reservacion (
  res_id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  sede_id         BIGINT UNSIGNED NOT NULL,   -- FK → sede
  res_nombre_cli  VARCHAR(100) NOT NULL,
  res_telefono    VARCHAR(20) DEFAULT NULL,
  res_fecha_hora  DATETIME NOT NULL,
  res_personas    INT DEFAULT 2,
  mesa_id         BIGINT UNSIGNED DEFAULT NULL, -- FK → mesa (SET NULL al borrar)
  res_nota        TEXT DEFAULT NULL,
  res_estado      ENUM('Pendiente','Confirmada','Cancelada') DEFAULT 'Pendiente',
  created_at      TIMESTAMP DEFAULT current_timestamp(),
  updated_at      TIMESTAMP DEFAULT current_timestamp() ON UPDATE current_timestamp()
);
```


---

## 5. CATÁLOGO DE ESTADOS

### `tipo_estado` (grupos)

| tes_id | Descripción |
| :-- | :-- |
| 1 | Comanda/Detalle Pedido |
| 2 | Pedido |
| 3 | Plato |
| 4 | PQRS |
| 5 | Insumo Bodega |

### `estado` (valores)

| est_id | Descripción | Grupo (tes_id) | Usado en |
| :-- | :-- | :-- | :-- |
| 1 | En Preparación | 1 - Comanda | pedido_detalle |
| 2 | Servido | 1 - Comanda | pedido_detalle |
| 3 | Cancelado | 1 - Comanda | pedido_detalle |
| 4 | Creado | 2 - Pedido | pedido |
| 5 | Finalizado | 2 - Pedido | pedido |
| 6 | Cancelado | 2 - Pedido | pedido |
| 7 | Disponible | 3 - Plato | plato |
| 8 | Agotado | 3 - Plato | plato |
| 9 | Inactivo | 3 - Plato | plato |
| 10 | Pendiente | 4 - PQRS | pqrs |
| 11 | Atendida | 4 - PQRS | pqrs |
| 12 | Cerrada | 4 - PQRS | pqrs |
| 13 | Disponible | 5 - Insumo Bodega | insumo |
| 14 | Agotado | 5 - Insumo Bodega | insumo |
| 15 | Inactivo | 5 - Insumo Bodega | insumo |


---

## 6. MÓDULO LUIS — PEDIDOS Y COMANDAS

### Objetivo

Gestión completa de pedidos y comandas del restaurante multisede, incluyendo administración
de platos y mesas, tanto en la aplicación Java desktop como en una app móvil Android para meseros.

### Tablas bajo responsabilidad de Luis

| Tabla | Tipo | Descripción |
| :-- | :-- | :-- |
| `pedido` | Transaccional | Cabecera de cada orden por mesa |
| `pedido_detalle` | Transaccional | Líneas de comanda enviadas a cocina |
| `plato` | Configuración | Carta del restaurante |
| `mesa` | Configuración | Mesas con estado visual y posición |
| `categoria_plato` | Configuración | Grupos del menú (Entradas, Fuertes…) |
| `area_mesa` | Configuración | Salones y zonas por sede |

### Requerimientos Funcionales

#### CRUD de CategoriaPlato

- Crear, consultar, actualizar y desactivar categorías del menú.
- Validar que no existan categorías con nombre duplicado.
- Imagen opcional por categoría.


#### CRUD de AreaMesa

- Gestionar áreas/salones por sede (`sede_id`).
- Estado Activo/Inactivo.
- No eliminar físicamente si tiene mesas asociadas.


#### CRUD de Plato

- Crear plato con categoría, precio, costo e imagen.
- Asociar a `categoria_plato` y a un estado (`est_id`).
- Solo platos `Disponible` (est_id = 7) pueden añadirse a pedidos.
- Desactivar cambiando estado a `Inactivo` (est_id = 9), no borrar físicamente.


#### CRUD de Mesa

- Gestionar mesas por sede y área con número, capacidad y posición visual (x_pos, y_pos).
- Cambiar estado automáticamente:
    - `Ocupada` → al crear un pedido activo.
    - `Disponible` → al facturar el pedido.
- Impedir eliminación física de mesas con pedidos; usar estado `Inactiva`.


#### CRUD de Pedido

- Crear pedido registrando: fecha actual del sistema, mesero autenticado, mesa, estado `Creado`.
- Evitar múltiples pedidos activos (`Creado`) para la misma `(mesa_id, sede_id)`.
- Permitir cambio de estado a `Finalizado` o `Cancelado`.


#### CRUD de PedidoDetalle (Comanda)

- Añadir líneas con plato, cantidad, precio-snapshot y observaciones para cocina.
- Estado inicial de cada línea: `En Preparación` (est_id = 1).
- Vista de cocina (`VistaComandas.fxml`) lista comandas pendientes por sede.
- Restringir modificación de líneas cuando el pedido esté facturado.


### Modelos Java

```java
/**
 * Modelo que representa la cabecera de un pedido.
 * Mapea la tabla 'pedido' de restaurante_2026.
 */
public class Pedido {
    private Long pedId;           // PK autoincremental
    private Long sedeId;          // Sede del pedido
    private LocalDate pedFecha;   // Fecha (auto: fecha actual del sistema)
    private Long usuId;           // FK → usuario mesero autenticado en sesión
    private Long mesaId;          // FK → mesa seleccionada
    private Long estId;           // 4=Creado, 5=Finalizado, 6=Cancelado
    private String pedObs;        // Observaciones generales del pedido
    // Constructores, getters, setters...
}

/**
 * Modelo que representa una línea de comanda (ítem del pedido).
 * Mapea la tabla 'pedido_detalle'. Cada línea es enviada a cocina.
 */
public class PedidoDetalle {
    private Long pedDetId;            // PK
    private Long pedId;                // FK → pedido padre
    private Long plaId;                // FK → plato
    private Integer pedDetCant;        // Cantidad solicitada
    private BigDecimal pedDetPrecio;   // Precio al momento de la venta (snapshot)
    private String pedDetObser;        // Observaciones para cocina
    private Long estId;                // 1=En Preparación, 2=Servido, 3=Cancelado
    // Constructores, getters, setters...
}

/**
 * Modelo que representa un plato de la carta del restaurante.
 * Solo los platos 'Disponible' (est_id=7) pueden añadirse a pedidos.
 */
public class Plato {
    private Long plaId;
    private Long catId;              // FK → categoria_plato
    private String plaDescripcion;
    private String plaCodigo;
    private BigDecimal plaPrecio;
    private BigDecimal plaCosto;
    private String plaImagen;
    private Long estId;              // 7=Disponible, 8=Agotado, 9=Inactivo
    // Constructores, getters, setters...
}

/**
 * Modelo que representa una mesa del restaurante.
 * El estado cambia automáticamente al crear/facturar pedidos.
 */
public class Mesa {
    private Long mesaId;
    private Long sedeId;
    private Long areaId;             // FK → area_mesa (nullable)
    private String mesaNumero;
    private Integer capacidad;
    private Integer xPos;            // Posición X para mapa visual
    private Integer yPos;            // Posición Y para mapa visual
    private String estado;           // Disponible / Ocupada / Reservada / Inactiva
    // Constructores, getters, setters...
}
```


### App Móvil Android (Rol Mesero)

Funcionalidades mínimas requeridas:

1. **Login** → autenticación contra tabla `usuario` con hash bcrypt
2. **Listado visual de mesas** por sede con indicador de color por estado
3. **Crear/editar pedidos** desde el dispositivo móvil
4. **Gestión de comandas** (`pedido_detalle`) con envío a cocina
5. **Actualización de estados** de línea cuando cocina los marque como `Servido`

---

## 7. MÓDULO SEBASTIÁN — EGRESOS Y SEGURIDAD

### Objetivo

Gestionar los egresos económicos del restaurante y controlar el acceso al sistema
mediante autenticación basada en la tabla `usuario`.

### Tablas bajo responsabilidad de Sebastián

| Tabla | Tipo | Descripción |
| :-- | :-- | :-- |
| `encabezado_egresos` | Transaccional | Registro de salidas de caja por sede |
| `concepto_egreso` | Configuración | Categorías de egresos (nómina, servicios…) |
| `forma_pago` | Configuración | Métodos de pago (compartida con Angie) |
| `usuario` | Seguridad | Usuarios del sistema con hash de contraseña |
| `perfil` | Seguridad | Roles (compartida con Angie) |

### Requerimientos Funcionales

#### Autenticación y Control de Acceso

- Pantalla de login que valide `usu_login`, `usu_pass` (comparando hash) y `usu_estado`.
- Bloquear acceso si el usuario está `Inactivo`.
- Restringir módulo de egresos a perfiles autorizados (Administrador, Cajero).


#### CRUD de FormaPago

- Crear, consultar, actualizar y desactivar formas de pago.
- Respetar campo `fp_estado`.


#### CRUD de ConceptoEgreso

- Gestionar conceptos (nómina, servicios, compra de insumos, mantenimiento...).
- Estado Activo/Inactivo.


#### CRUD de EncabezadoEgresos

- Registrar egresos con todos los campos obligatorios.
- Garantizar unicidad de `no_egreso` (consecutivo).
- Anular cambiando `egr_estado` a `Anulado`, **nunca borrar físicamente**.
- Filtros de consulta: rango de fechas, sede, concepto, forma de pago, usuario.


### Modelos Java

```java
/**
 * Modelo que representa un egreso económico del restaurante.
 * Mapea la tabla 'encabezado_egresos'. Las anulaciones cambian egr_estado.
 */
public class EncabezadoEgresos {
    private Long egrId;                     // PK
    private Long sedeId;                    // FK → sede
    private Long noEgreso;                  // Consecutivo único
    private LocalDate fechaDocumento;       // Por defecto fecha actual
    private String terceroIdentificacion;   // NIT o Cédula del beneficiario
    private String terceroNombre;
    private String detalle;
    private Long fpId;                      // FK → forma_pago
    private Long conId;                     // FK → concepto_egreso
    private String noDocumento;             // Número de comprobante soporte
    private BigDecimal valorEgreso;
    private Long usuId;                     // FK → usuario que registra
    private String egrEstado;               // Activo / Anulado
    // Constructores, getters, setters...
}

/**
 * Modelo que representa un usuario del sistema.
 * La contraseña SIEMPRE se almacena como hash bcrypt/argon2.
 */
public class Usuario {
    private Long usuId;
    private String usuNombre;
    private String usuApellido;
    private String usuDireccion;
    private String usuTelefono;
    private String usuCorreo;
    private Long perfId;       // FK → perfil
    private String usuLogin;
    private String usuPass;    // Hash bcrypt/argon2 — NUNCA texto plano
    private String usuEstado;  // Activo / Inactivo
    // Constructores, getters, setters...
}
```


---

## 8. MÓDULO ANGIE — FACTURACIÓN Y ROLES

### Objetivo

Administrar la facturación de ventas (recibos de caja) y la gestión de clientes
y perfiles de usuario del sistema.

### Tablas bajo responsabilidad de Angie

| Tabla | Tipo | Descripción |
| :-- | :-- | :-- |
| `recibo_caja` | Transaccional | Factura de venta asociada a un pedido |
| `recibo_caja_detalle` | Transaccional | Líneas generadas desde el pedido_detalle |
| `cliente` | Configuración | Clientes registrados del restaurante |
| `perfil` | Configuración | Roles del sistema (compartida con Sebastián) |

### Requerimientos Funcionales

#### CRUD de Cliente

- Registrar tipo/número de documento, nombres, dirección, teléfono y correo.
- Validar unicidad de `cli_num_documento` y `cli_correo`.
- Marcar clientes como `Inactivos` sin borrarlos físicamente.


#### Gestión de Perfiles y Usuarios

- CRUD de `perfil` para roles: Administrador, Mesero, Cocinero, Cajero.
- Pantalla para asignar perfil a usuarios mediante `usuario.perf_id`.


#### CRUD de ReciboCaja (Factura de venta)

- Crear recibo asociado obligatoriamente a un `pedido` en estado `Finalizado`.
- El `cli_id` puede ser NULL (público general).
- Calcular automáticamente: subtotal, descuento, propina, total, cambio.
- No facturar pedidos ya facturados previamente.
- Anular cambiando `rc_estado` a `Anulado`, **nunca borrar físicamente**.


#### CRUD de ReciboCajaDetalle

- Generar líneas automáticamente desde el `pedido_detalle` del pedido.
- Capturar precio unitario como snapshot al momento de la factura.
- Impedir edición de líneas cuando el recibo esté `Anulado`.


#### Reportes de Facturación

- Consultas por: rango de fechas, sede, forma de pago, cajero.
- Resúmenes de ventas por día y sede.


### Modelos Java

```java
/**
 * Modelo que representa un recibo de caja (factura de venta).
 * Solo se facturan pedidos en estado 'Finalizado' no facturados previamente.
 */
public class ReciboCaja {
    private Long rcNum;              // PK autoincremental
    private Long sedeId;             // FK → sede
    private Long usuId;              // FK → usuario cajero
    private LocalDate rcFecha;
    private Long pedId;              // FK → pedido (debe estar Finalizado)
    private Long cliId;              // FK → cliente (nullable = Público General)
    private Long fpId;               // FK → forma_pago
    private BigDecimal rcSubtotal;
    private BigDecimal rcDescuento;
    private BigDecimal rcPropina;
    private BigDecimal rcTotal;
    private BigDecimal rcMontoRec;   // Monto recibido del cliente
    private BigDecimal rcCambio;
    private String rcObservacion;
    private String rcEstado;         // Activo / Anulado
    // Constructores, getters, setters...
}

/**
 * Modelo que representa una línea de la factura de venta.
 * Se genera automáticamente desde el pedido_detalle del pedido.
 */
public class ReciboCajaDetalle {
    private Long rcdId;
    private Long rcNum;              // FK → recibo_caja (CASCADE DELETE)
    private Long plaId;              // FK → plato
    private Integer rcdCantidad;
    private BigDecimal rcdPrecio;    // Snapshot precio al momento de factura
    private BigDecimal rcdDescuento;
    // Constructores, getters, setters...
}
```


---

## 9. MÓDULO NICOLÁS — INVENTARIO / PQRS

### Objetivo

Dependiendo del acuerdo con la profesora, implementar el **Módulo A: Inventario de Bodega**
o el **Módulo B: PQRS**.

---

### OPCIÓN A: Inventario de Bodega

#### Tablas involucradas

| Tabla | Tipo | Descripción |
| :-- | :-- | :-- |
| `inventario_log` | Transaccional | Movimientos: entrada/salida/ajuste/merma |
| `insumo` | Configuración | Materias primas con stock por sede |
| `categoria_insumo` | Configuración | Grupos de insumos (Carnes, Lácteos…) |
| `presentacion` | Configuración | Unidades de medida (Kg, L, Und…) |

#### Requerimientos Funcionales

- **CRUD de Presentacion**: unidades de medida con abreviatura.
- **CRUD de CategoriaInsumo**: categorías con imagen opcional.
- **CRUD de Insumo**: por sede, con categoría, unidad, precio de compra, stock actual y stock mínimo.
- **CRUD de InventarioLog**: registrar movimientos (entrada, salida, ajuste, venta, merma).
    - Actualizar automáticamente `ins_stock` en tabla `insumo` con cada movimiento.
    - Guardar snapshot: `log_stock_ant` y `log_stock_nvo`.
- **Alertas**: listar insumos con `ins_stock < ins_stock_min`.
- **Integración sugerida**: usar `plato_ingrediente` para descontar insumos al vender platos.


#### Modelos Java

```java
/**
 * Modelo que representa un insumo de bodega.
 * El stock se actualiza con cada movimiento de inventario_log.
 */
public class Insumo {
    private Long insId;
    private Long sedeId;              // FK → sede
    private Long cinsId;              // FK → categoria_insumo
    private Long presId;              // FK → presentacion (unidad de medida)
    private String insNombre;
    private String insCodigo;
    private BigDecimal insPrecioCompra;
    private BigDecimal insStock;      // Stock actual — se actualiza automáticamente
    private BigDecimal insStockMin;   // Umbral para alerta de stock bajo
    private Boolean insVendible;      // true = también se vende al público
    private String insImagen;
    private Integer insEstado;        // 1=Activo, 0=Inactivo
    // Constructores, getters, setters...
}

/**
 * Modelo que representa un movimiento de inventario.
 * Registra trazabilidad completa: stock antes y después del movimiento.
 */
public class InventarioLog {
    private Long logId;
    private Long insId;               // FK → insumo
    private Long usuId;               // FK → usuario que registra
    private String logTipo;           // entrada / salida / ajuste / venta / merma
    private BigDecimal logCantidad;
    private BigDecimal logStockAnt;   // Stock antes del movimiento (snapshot)
    private BigDecimal logStockNvo;   // Stock después del movimiento (snapshot)
    private String logNota;
    // Constructores, getters, setters...
}
```


---

### OPCIÓN B: Módulo de PQRS

#### Tablas involucradas

| Tabla | Tipo | Descripción |
| :-- | :-- | :-- |
| `pqrs` | Transaccional | Peticiones, Quejas, Reclamos, Sugerencias |
| `tipo_tpqrs` | Configuración | Tipos de PQRS |
| `estado` | Apoyo | Estados: Pendiente, Atendida, Cerrada |

#### Requerimientos Funcionales

- **CRUD de TipoTpqrs**: Petición, Queja, Reclamo, Sugerencia.
- **CRUD de Pqrs**: registrar fecha, descripción, correo, teléfono, tipo y estado.
    - Registrar respuesta y usuario que atiende la PQRS.
    - Estado inicial: `Pendiente` (est_id = 10).
- **Consultas**: por fecha, tipo, estado y usuario que atendió.


#### Modelo Java

```java
/**
 * Modelo que representa una PQRS del cliente.
 * Mapea la tabla 'pqrs'. Estado inicial siempre Pendiente (est_id=10).
 */
public class Pqrs {
    private Long pqrsId;
    private LocalDate pqrsFecha;
    private String pqrsDescripcion;
    private String pqrsCorreo;
    private String pqrsTelefono;
    private Long tpqrsId;             // FK → tipo_tpqrs
    private Long estId;               // 10=Pendiente, 11=Atendida, 12=Cerrada
    private String pqrsRespuesta;     // Respuesta del equipo
    private Long usuIdResponde;       // FK → usuario que responde (nullable)
    // Constructores, getters, setters...
}
```


---

## 10. CLASES COMPARTIDAS (UTIL)

### `ConexionBD.java` — Singleton de conexión

```java
/**
 * Clase utilitaria singleton para gestionar la conexión a MySQL.
 * Reutilizable por todos los módulos del proyecto. No instanciar directamente.
 */
public class ConexionBD {
    private static final String URL    = "jdbc:mysql://localhost:3306/restaurante_2026";
    private static final String USUARIO = "root";
    private static final String CLAVE   = "tu_password";

    private static Connection conexion;

    /**
     * Retorna la conexión activa o crea una nueva si está cerrada.
     * @return Connection objeto de conexión a restaurante_2026
     */
    public static Connection obtenerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
        }
        return conexion;
    }

    /**
     * Cierra la conexión si está abierta. Llamar al cerrar la aplicación.
     */
    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}
```


### `HashSeguridad.java` — Encriptación de contraseñas

```java
/**
 * Clase utilitaria para encriptación segura de contraseñas.
 * Implementa bcrypt. NUNCA almacenar contraseñas en texto plano.
 */
public class HashSeguridad {

    /**
     * Genera el hash bcrypt de una contraseña en texto plano.
     * @param contrasenaPlana Contraseña ingresada por el usuario
     * @return Hash bcrypt listo para almacenar en usu_pass
     */
    public static String generarHash(String contrasenaPlana) {
        // Implementar con librería BCrypt (org.mindrot.jbcrypt)
        return BCrypt.hashpw(contrasenaPlana, BCrypt.gensalt());
    }

    /**
     * Verifica si una contraseña coincide con su hash almacenado.
     * @param contrasenaPlana Contraseña ingresada en el login
     * @param hashAlmacenado  Hash almacenado en base de datos
     * @return true si la contraseña es correcta
     */
    public static boolean verificarHash(String contrasenaPlana, String hashAlmacenado) {
        return BCrypt.checkpw(contrasenaPlana, hashAlmacenado);
    }
}
```


### `ValidadorCampos.java` — Validaciones centralizadas

```java
/**
 * Clase utilitaria con métodos de validación reutilizables en todos los módulos.
 * Centraliza la lógica para evitar duplicación de código.
 */
public class ValidadorCampos {

    /**
     * Verifica que un campo de texto no sea nulo ni vacío.
     * @param valor Texto a validar
     * @param nombreCampo Nombre del campo para el mensaje de error
     */
    public static void validarNoVacio(String valor, String nombreCampo) throws Exception {
        if (valor == null || valor.trim().isEmpty()) {
            throw new Exception("El campo '" + nombreCampo + "' es obligatorio.");
        }
    }

    /**
     * Verifica que un valor decimal sea mayor que cero.
     * Usado para validar precios, cantidades y valores monetarios.
     */
    public static void validarMayorQueCero(BigDecimal valor, String nombreCampo) throws Exception {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("El campo '" + nombreCampo + "' debe ser mayor que cero.");
        }
    }

    /**
     * Verifica el formato de una dirección de correo electrónico.
     */
    public static boolean esCorreoValido(String correo) {
        return correo != null && correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }
}
```


---

## 11. VISTAS FXML POR INTEGRANTE

| Archivo FXML | Módulo | Integrante | Descripción |
| :-- | :-- | :-- | :-- |
| `VistaComandas.fxml` | Cocina | **Luis** | Lista comandas pendientes por sede |
| `VistaAreasMesa.fxml` | Config | **Luis** | CRUD de áreas/salones por sede |
| `VistaCategoriasPlato.fxml` | Config | **Luis** | CRUD de categorías del menú |
| `VistaConceptoEgreso.fxml` | Egresos | **Sebastián** | CRUD de conceptos de egreso |
| `VistaFormaPago.fxml` | Egresos | **Sebastián** | CRUD de formas de pago |
| `VistaReciboCaja.fxml` | Facturación | **Angie** | Emisión y consulta de facturas |
| `VistaClientes.fxml` | Facturación | **Angie** | CRUD de clientes |
| `VistaPerfiles.fxml` | Roles | **Angie** | CRUD de perfiles y asignación |
| `VistaInventarioLog.fxml` | Bodega | **Nicolás** | Registro de movimientos de bodega |
| `VistaPresentacion.fxml` | Bodega | **Nicolás** | CRUD de unidades de medida |
| `VistaCategoriaInsumo.fxml` | Bodega | **Nicolás** | CRUD de categorías de insumos |
| `VistaSedes.fxml` | Config general | Admin | Gestión de sedes del restaurante |


---

## 12. FLUJOS DE ESTADOS DEL SISTEMA

### Ciclo de vida del Pedido

```
  [CREADO (4)]
       │
       ├──────────────────► [FINALIZADO (5)] ──► Facturación (Angie)
       │                                              │
       └──────────────────► [CANCELADO (6)]    ◄──── (anulado luego de facturar)
```


### Ciclo de vida de la Mesa

```
  [Disponible] ──► [Ocupada] ─────────────────► [Disponible]
       │           (al crear pedido)              (al facturar)
       │
       └──────────► [Reservada] (reservación confirmada)
       └──────────► [Inactiva]  (desactivación manual, no borrar)
```


### Ciclo de vida de la Comanda (PedidoDetalle)

```
  [En Preparación (1)] ──► [Servido (2)]
            │
            └────────────► [Cancelado (3)]
```


### Ciclo de vida del Egreso

```
  [Activo] ──► [Anulado]   (solo cambio de estado, nunca borrar físicamente)
```


### Ciclo de vida del Recibo de Caja

```
  [Activo] ──► [Anulado]   (solo cambio de estado, nunca borrar físicamente)
  ⚠️ Solo se crea si el pedido está en estado [FINALIZADO]
```


### Ciclo de vida de PQRS

```
  [Pendiente (10)] ──► [Atendida (11)] ──► [Cerrada (12)]
```


---

## 13. CONSULTAS SQL DE REFERENCIA

### Pedidos activos por sede

```sql
SELECT p.ped_id, m.mesa_numero, u.usu_nombre, e.est_descripcion, p.ped_fecha
FROM pedido p
JOIN mesa    m ON p.mesa_id = m.mesa_id
JOIN usuario u ON p.usu_id  = u.usu_id
JOIN estado  e ON p.est_id  = e.est_id
WHERE p.sede_id = 1 AND p.est_id = 4  -- Creado
ORDER BY p.created_at DESC;
```


### Comandas pendientes para cocina

```sql
SELECT pd.ped_det_id, pl.pla_descripcion, pd.ped_det_cant,
       pd.ped_det_obser, m.mesa_numero, e.est_descripcion
FROM pedido_detalle pd
JOIN plato  pl  ON pd.pla_id   = pl.pla_id
JOIN pedido ped ON pd.ped_id   = ped.ped_id
JOIN mesa   m   ON ped.mesa_id = m.mesa_id
JOIN estado e   ON pd.est_id   = e.est_id
WHERE ped.sede_id = 1 AND pd.est_id = 1  -- En Preparación
ORDER BY pd.created_at ASC;
```


### Egresos por rango de fecha y sede

```sql
SELECT ee.no_egreso, ee.fecha_documento, ee.tercero_nombre,
       ce.con_descripcion, fp.fp_descripcion, ee.valor_egreso, ee.egr_estado
FROM encabezado_egresos ee
JOIN concepto_egreso ce ON ee.con_id = ce.con_id
JOIN forma_pago      fp ON ee.fp_id  = fp.fp_id
WHERE ee.sede_id = 1
  AND ee.fecha_documento BETWEEN '2026-03-01' AND '2026-03-31'
ORDER BY ee.fecha_documento DESC;
```


### Facturas emitidas en el día por sede

```sql
SELECT rc.rc_num, rc.rc_fecha, c.cli_nombre, fp.fp_descripcion,
       rc.rc_total, rc.rc_estado
FROM recibo_caja rc
LEFT JOIN cliente   c  ON rc.cli_id = c.cli_id
JOIN      forma_pago fp ON rc.fp_id = fp.fp_id
WHERE rc.sede_id = 1 AND rc.rc_fecha = curdate()
ORDER BY rc.rc_num DESC;
```


### Insumos con stock bajo mínimo

```sql
SELECT i.ins_nombre, p.pres_abreviatura,
       i.ins_stock, i.ins_stock_min,
       (i.ins_stock_min - i.ins_stock) AS faltante
FROM insumo i
JOIN presentacion p ON i.pres_id = p.pres_id
WHERE i.ins_stock < i.ins_stock_min AND i.ins_estado = 1
ORDER BY faltante DESC;
```


### Resumen de ventas por día y sede

```sql
SELECT rc.rc_fecha, s.sede_nombre,
       COUNT(*) AS total_facturas,
       SUM(rc.rc_total) AS total_ventas
FROM recibo_caja rc
JOIN sede s ON rc.sede_id = s.sede_id
WHERE rc.rc_estado = 'Activo'
GROUP BY rc.rc_fecha, rc.sede_id
ORDER BY rc.rc_fecha DESC;
```


### PQRS pendientes

```sql
SELECT pq.pqrs_id, pq.pqrs_fecha, tt.tpqrs_descripcion,
       pq.pqrs_correo, e.est_descripcion
FROM pqrs pq
JOIN tipo_tpqrs tt ON pq.tpqrs_id = tt.tpqrs_id
JOIN estado     e  ON pq.est_id   = e.est_id
WHERE pq.est_id = 10  -- Pendiente
ORDER BY pq.pqrs_fecha ASC;
```


---

## 14. REGLAS DE NEGOCIO GLOBALES

### Reglas de integridad

1. **Sin borrado físico:** Toda entidad transaccional (egresos, facturas, comandas canceladas) se anula con cambio de estado, nunca con `DELETE`.
2. **Auditoría obligatoria:** Todos los INSERT y UPDATE deben respetar `created_at` y `updated_at`.
3. **Sin código estructurado:** Todo el código debe estar organizado en clases MVC; no se acepta lógica en `main`.
4. **Restricciones de BD:** No eliminar llaves foráneas ni restricciones de integridad para forzar el funcionamiento.

### Reglas de negocio por módulo

5. **Mesa no duplicada (Luis):** No puede haber dos pedidos `Creado` para la misma `(mesa_id, sede_id)`.
6. **Precio snapshot (Luis/Angie):** `ped_det_precio` y `rcd_precio` se capturan al momento de la transacción y no se actualizan si el plato cambia.
7. **Solo platos disponibles (Luis):** Solo `plato.est_id = 7` pueden añadirse a comandas.
8. **Facturar solo pedidos finalizados (Angie):** `recibo_caja` solo puede crearse para pedidos con `est_id = 5`.
9. **No facturar dos veces (Angie):** Un pedido no puede tener más de un recibo activo.
10. **Egreso único (Sebastián):** `no_egreso` tiene restricción `UNIQUE` en BD; la app debe generarlo correctamente.
11. **Stock automático (Nicolás):** Cada `inventario_log` insertado debe actualizar `insumo.ins_stock` en la misma transacción.
12. **Hash obligatorio (Sebastián):** `usu_pass` debe almacenarse siempre como hash bcrypt/argon2, nunca en texto plano.

---

## 15. ENTREGABLES POR FASE

| Fase | Fecha | Luis | Sebastián | Angie | Nicolás |
| :-- | :-- | :-- | :-- | :-- | :-- |
| **Primer avance** | Día 25 | CRUD `pedido` + listado `pedido_detalle` | CRUD `encabezado_egresos` + login básico | CRUD `recibo_caja` (cabecera) | CRUD `inventario_log` o CRUD `pqrs` |
| **Segundo avance** | +15 días | CRUD `plato` y `mesa` integrados | CRUD `concepto_egreso` y `forma_pago` | CRUD `cliente` y gestión `perfil` | CRUD `insumo`, `presentacion`, `categoria_insumo` o `tipo_tpqrs` |
| **Fase avanzada** | Final | App móvil Android + vista cocina | Reportes y filtros avanzados | Reportes facturación + cruces pedidos | Reportes inventario/alertas o reportes PQRS |


---

## 16. CONFIGURACIÓN Y DESPLIEGUE

### Base de Datos

```sql
-- 1. Crear la base de datos
CREATE DATABASE IF NOT EXISTS restaurante_2026
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- 2. Ejecutar el script oficial completo
-- Archivo: SISTEMA_RESTAURANTE_2026_DEFINITIVO_MULTISEDE.sql
-- ⚠️ NO modificar restricciones de integridad referencial

-- 3. Verificar datos semilla
SELECT * FROM perfil;        -- 4 perfiles: Admin, Mesero, Cocinero, Cajero
SELECT * FROM forma_pago;    -- 4 formas de pago
SELECT * FROM concepto_egreso; -- 5 conceptos
SELECT * FROM presentacion;  -- 7 unidades de medida
SELECT * FROM tipo_tpqrs;    -- 4 tipos de PQRS
SELECT * FROM estado;        -- 15 estados distribuidos en 5 grupos
```


### Configuración de Conexión (`ConexionBD.java`)

```
URL:      jdbc:mysql://localhost:3306/restaurante_2026
USUARIO:  root  (o el usuario asignado)
CLAVE:    tu_password
CHARSET:  utf8mb4
```


### Usuario Administrador Inicial

```
usu_id:  2
Nombre:  Luis Ocampo
Login:   (configurar usu_login antes de producción)
Pass:    ⚠️ Hashear con bcrypt antes de insertar en producción
Perfil:  1 → Administrador
Estado:  Activo
```


---

## 17. CONVENCIONES DE CÓDIGO
| Elemento | Convención | Ejemplo |
| :-- | :-- | :-- |
| Clases | PascalCase | `ControladorPedido`, `ModeloMesa` |
| Métodos | camelCase | `crearPedido()`, `listarComandas()` |
| Variables | camelCase | `pedidoActual`, `mesaSeleccionada` |
| Constantes | SNAKE_UPPER_CASE | `ESTADO_CREADO`, `ID_SEDE_DEFAULT` |
| Paquetes | minúsculas | `com.restaurante.modelos` |
| Archivos FXML | PascalCase | `VistaComandas.fxml` |
| Comentarios | Javadoc obligatorio en clases y métodos públicos |  |
| Errores | Mensajes claros para el usuario, sin exponer stacktrace |  |


---

*Sistema Restaurante 2026 — Documentación Técnica Completa*
*Equipo: Luis · Sebastián · Angie · Nicolás | Marzo 2026*

```

