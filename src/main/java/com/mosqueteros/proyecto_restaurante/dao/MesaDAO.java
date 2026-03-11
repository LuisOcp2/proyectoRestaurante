package com.mosqueteros.proyecto_restaurante.dao;

import com.mosqueteros.proyecto_restaurante.model.Mesa;
import com.mosqueteros.proyecto_restaurante.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla `mesa`.
 *
 * Esquema real de la BD:
 *   mesa(mesa_id, sede_id, area_id, mesa_numero, capacidad, x_pos, y_pos,
 *        estado ENUM('Disponible','Ocupada','Reservada','Inactiva'), ...)
 *
 * NOTA: el estado de mesa es un ENUM directo, NO una FK a la tabla `estado`.
 */
public class MesaDAO {

    private static final String SQL_SELECT =
        "SELECT m.mesa_id, m.sede_id, m.area_id, m.mesa_numero, m.capacidad, " +
        "m.x_pos, m.y_pos, m.estado, " +
        "s.sede_nombre, " +
        "a.area_nombre " +
        "FROM mesa m " +
        "JOIN sede s ON m.sede_id = s.sede_id " +
        "LEFT JOIN area_mesa a ON m.area_id = a.area_id ";

    // ── LISTAR ─────────────────────────────────────────────────────────────────

    public static List<Mesa> listarTodas() {
        List<Mesa> lista = new ArrayList<>();
        String sql = SQL_SELECT + "ORDER BY s.sede_nombre, m.mesa_numero";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(Mesa.crearDesdeResultSet(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar mesas: " + e.getMessage(), e);
        }
        return lista;
    }

    public static List<Mesa> listarPorSede(long sedeId) {
        List<Mesa> lista = new ArrayList<>();
        String sql = SQL_SELECT + "WHERE m.sede_id = ? ORDER BY m.mesa_numero";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, sedeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(Mesa.crearDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar mesas por sede: " + e.getMessage(), e);
        }
        return lista;
    }

    public static Mesa buscarPorId(long id) {
        String sql = SQL_SELECT + "WHERE m.mesa_id = ?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Mesa.crearDesdeResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar mesa: " + e.getMessage(), e);
        }
        return null;
    }

    // ── INSERTAR ───────────────────────────────────────────────────────────────

    /**
     * Inserta una nueva mesa. El estado inicial es siempre 'Disponible'.
     * @param numero    Nombre/número de mesa (String: "1", "A1", etc.)
     * @param capacidad Capacidad de personas
     * @param areaId    ID del área (puede ser 0 si no aplica)
     * @param sedeId    ID de la sede
     */
    public static boolean insertar(String numero, int capacidad, long areaId, long sedeId) {
        String sql = "INSERT INTO mesa (sede_id, area_id, mesa_numero, capacidad, estado) " +
                     "VALUES (?, ?, ?, ?, 'Disponible')";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, sedeId);
            if (areaId > 0) ps.setLong(2, areaId);
            else            ps.setNull(2, Types.BIGINT);
            ps.setString(3, numero);
            ps.setInt(4, capacidad);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar mesa: " + e.getMessage(), e);
        }
    }

    // ── ACTUALIZAR ─────────────────────────────────────────────────────────────

    public static boolean actualizar(long id, String numero, int capacidad,
                                     long areaId, long sedeId, String estado) {
        String sql = "UPDATE mesa SET mesa_numero=?, capacidad=?, area_id=?, " +
                     "sede_id=?, estado=? WHERE mesa_id=?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, numero);
            ps.setInt(2, capacidad);
            if (areaId > 0) ps.setLong(3, areaId);
            else            ps.setNull(3, Types.BIGINT);
            ps.setLong(4, sedeId);
            ps.setString(5, estado);
            ps.setLong(6, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar mesa: " + e.getMessage(), e);
        }
    }

    // ── CAMBIAR ESTADO ─────────────────────────────────────────────────────────

    /**
     * Cambia el estado de la mesa usando el ENUM directamente.
     * @param mesaId ID de la mesa
     * @param estado Uno de: 'Disponible', 'Ocupada', 'Reservada', 'Inactiva'
     */
    public static boolean cambiarEstado(long mesaId, String estado) {
        String sql = "UPDATE mesa SET estado=? WHERE mesa_id=?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setLong(2, mesaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar estado de mesa: " + e.getMessage(), e);
        }
    }

    // ── VALIDACIONES ───────────────────────────────────────────────────────────

    public static boolean tienePedidosActivos(long mesaId) {
        String sql = "SELECT COUNT(*) FROM pedido p " +
                     "JOIN estado e ON p.est_id = e.est_id " +
                     "WHERE p.mesa_id = ? AND e.est_descripcion IN ('Creado','En Proceso')";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, mesaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar pedidos activos: " + e.getMessage(), e);
        }
        return false;
    }

    public static boolean mesaTienePedidoActivo(long mesaId, long sedeId) {
        String sql = "SELECT COUNT(*) FROM pedido p " +
                     "JOIN estado e ON p.est_id = e.est_id " +
                     "WHERE p.mesa_id = ? AND p.sede_id = ? " +
                     "AND e.est_descripcion IN ('Creado','En Proceso')";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, mesaId);
            ps.setLong(2, sedeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al validar mesa: " + e.getMessage(), e);
        }
        return false;
    }
}
