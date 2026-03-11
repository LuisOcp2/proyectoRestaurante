package com.mosqueteros.proyecto_restaurante.dao;

import com.mosqueteros.proyecto_restaurante.model.Pedido;
import com.mosqueteros.proyecto_restaurante.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla `pedido` (cabecera de la orden).
 *
 * Reglas de negocio críticas:
 *  1. Al crear pedido → la mesa pasa automáticamente a estado 'Ocupada'.
 *  2. No se permiten dos pedidos activos para la misma mesa/sede.
 *  3. Al cerrar/cancelar el pedido → la mesa vuelve a 'Disponible'.
 */
public class PedidoDAO {

    private static final String SQL_SELECT =
        "SELECT p.ped_id, p.ped_fecha, p.ped_observaciones, " +
        "p.mes_id, m.mes_numero, " +
        "p.usu_id, u.usu_nombre, " +
        "p.sede_id, s.sede_nombre, " +
        "p.est_id, e.est_descripcion " +
        "FROM pedido p " +
        "JOIN mesa m ON p.mes_id = m.mes_id " +
        "JOIN usuario u ON p.usu_id = u.usu_id " +
        "JOIN sede s ON p.sede_id = s.sede_id " +
        "JOIN estado e ON p.est_id = e.est_id ";

    // ── LISTAR ────────────────────────────────────────────────────────────────

    /** Lista todos los pedidos, el más reciente primero. */
    public static List<Pedido> listarTodos() {
        List<Pedido> lista = new ArrayList<>();
        String sql = SQL_SELECT + "ORDER BY p.ped_fecha DESC, p.ped_id DESC";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(Pedido.crearDesdeResultSet(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar pedidos: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Lista pedidos activos (Creado / En Proceso) de una sede específica.
     * Útil para la vista de cocina y el panel de meseros.
     */
    public static List<Pedido> listarActivosPorSede(long sedeId) {
        List<Pedido> lista = new ArrayList<>();
        String sql = SQL_SELECT +
                     "WHERE p.sede_id = ? AND e.est_descripcion IN ('Creado','En Proceso') " +
                     "ORDER BY p.ped_fecha";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, sedeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(Pedido.crearDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar pedidos activos: " + e.getMessage(), e);
        }
        return lista;
    }

    /** Busca un pedido por su ID. Retorna null si no existe. */
    public static Pedido buscarPorId(long id) {
        String sql = SQL_SELECT + "WHERE p.ped_id = ?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Pedido.crearDesdeResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar pedido: " + e.getMessage(), e);
        }
        return null;
    }

    // ── VALIDAR MESA LIBRE ────────────────────────────────────────────────────

    /**
     * Verifica si una mesa tiene pedidos activos en una sede.
     * Retorna true si ya tiene pedido activo → NO se puede crear otro.
     */
    public static boolean mesaTienePedidoActivo(long mesId, long sedeId) {
        String sql = "SELECT COUNT(*) FROM pedido p " +
                     "JOIN estado e ON p.est_id = e.est_id " +
                     "WHERE p.mes_id = ? AND p.sede_id = ? " +
                     "AND e.est_descripcion IN ('Creado','En Proceso')";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, mesId);
            ps.setLong(2, sedeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al validar mesa: " + e.getMessage(), e);
        }
        return false;
    }

    // ── INSERTAR CON TRANSACCIÓN ──────────────────────────────────────────────

    /**
     * Crea un nuevo pedido y actualiza el estado de la mesa a 'Ocupada' en
     * una sola transacción atómica.
     *
     * @param mesId          ID de la mesa seleccionada
     * @param usuId          ID del mesero autenticado
     * @param sedeId         ID de la sede
     * @param observaciones  Observaciones generales del pedido
     * @param estIdPedido    est_id del estado 'Creado'
     * @param estIdOcupada   est_id del estado 'Ocupada' para la mesa
     * @return ID del nuevo pedido, o -1 si falló
     */
    public static long insertar(long mesId, long usuId, long sedeId,
                                String observaciones,
                                long estIdPedido, long estIdOcupada) {

        String sqlPedido = "INSERT INTO pedido (ped_fecha, ped_observaciones, mes_id, " +
                           "usu_id, sede_id, est_id) VALUES (CURDATE(), ?, ?, ?, ?, ?)";
        String sqlMesa   = "UPDATE mesa SET est_id=? WHERE mes_id=?";

        Connection con = null;
        try {
            con = ConexionDB.obtenerConexion();
            con.setAutoCommit(false); // Iniciar transacción

            long nuevoPedidoId = -1;

            // 1. Insertar pedido
            try (PreparedStatement ps = con.prepareStatement(sqlPedido,
                     Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, observaciones);
                ps.setLong(2, mesId);
                ps.setLong(3, usuId);
                ps.setLong(4, sedeId);
                ps.setLong(5, estIdPedido);
                ps.executeUpdate();

                try (ResultSet generadas = ps.getGeneratedKeys()) {
                    if (generadas.next()) nuevoPedidoId = generadas.getLong(1);
                }
            }

            // 2. Marcar mesa como Ocupada
            try (PreparedStatement ps = con.prepareStatement(sqlMesa)) {
                ps.setLong(1, estIdOcupada);
                ps.setLong(2, mesId);
                ps.executeUpdate();
            }

            con.commit(); // Confirmar transacción
            return nuevoPedidoId;

        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { /* ignorar */ }
            }
            throw new RuntimeException("Error al crear pedido: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ex) { /* ignorar */ }
            }
        }
    }

    // ── ACTUALIZAR OBSERVACIONES ──────────────────────────────────────────────

    /** Actualiza solo las observaciones de un pedido activo. */
    public static boolean actualizarObservaciones(long pedId, String observaciones) {
        String sql = "UPDATE pedido SET ped_observaciones=? WHERE ped_id=?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, observaciones);
            ps.setLong(2, pedId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar observaciones: " + e.getMessage(), e);
        }
    }

    // ── CAMBIAR ESTADO CON TRANSACCIÓN ────────────────────────────────────────

    /**
     * Cambia el estado de un pedido y, si el nuevo estado es 'Finalizado' o
     * 'Cancelado', también libera la mesa (→ 'Disponible') en la misma transacción.
     *
     * @param pedId          ID del pedido
     * @param mesId          ID de la mesa asociada
     * @param nuevoEstPedido est_id del nuevo estado del pedido
     * @param liberarMesa    true si hay que liberar la mesa (est_id 'Disponible')
     * @param estIdDisponible est_id del estado 'Disponible' para la mesa
     */
    public static boolean cambiarEstado(long pedId, long mesId,
                                        long nuevoEstPedido,
                                        boolean liberarMesa, long estIdDisponible) {
        String sqlPedido = "UPDATE pedido SET est_id=? WHERE ped_id=?";
        String sqlMesa   = "UPDATE mesa SET est_id=? WHERE mes_id=?";

        Connection con = null;
        try {
            con = ConexionDB.obtenerConexion();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sqlPedido)) {
                ps.setLong(1, nuevoEstPedido);
                ps.setLong(2, pedId);
                ps.executeUpdate();
            }

            if (liberarMesa) {
                try (PreparedStatement ps = con.prepareStatement(sqlMesa)) {
                    ps.setLong(1, estIdDisponible);
                    ps.setLong(2, mesId);
                    ps.executeUpdate();
                }
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { /* ignorar */ }
            }
            throw new RuntimeException("Error al cambiar estado del pedido: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ex) { /* ignorar */ }
            }
        }
    }
}
