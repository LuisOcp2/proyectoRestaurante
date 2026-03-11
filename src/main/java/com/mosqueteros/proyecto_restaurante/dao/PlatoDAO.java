package com.mosqueteros.proyecto_restaurante.dao;

import com.mosqueteros.proyecto_restaurante.model.Plato;
import com.mosqueteros.proyecto_restaurante.util.ConexionDB;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla `plato`.
 * Solo los platos en estado 'Disponible' pueden agregarse a un pedido.
 * El borrado físico NO está permitido; se usa cambio de estado a 'Inactivo'.
 */
public class PlatoDAO {

    // ── SQL base con JOIN para obtener descripciones ───────────────────────────
    private static final String SQL_SELECT =
        "SELECT p.pla_id, p.pla_descripcion, p.pla_codigo, " +
        "p.pla_precio, p.pla_costo, p.cat_id, cp.cat_nombre, " +
        "p.est_id, e.est_descripcion " +
        "FROM plato p " +
        "JOIN categoria_plato cp ON p.cat_id = cp.cat_id " +
        "JOIN estado e ON p.est_id = e.est_id ";

    // ── LISTAR TODOS ──────────────────────────────────────────────────────────

    /** Retorna todos los platos registrados con su categoría y estado. */
    public static List<Plato> listarTodos() {
        List<Plato> lista = new ArrayList<>();
        String sql = SQL_SELECT + "ORDER BY p.pla_descripcion";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(Plato.crearDesdeResultSet(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar platos: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Retorna SOLO los platos en estado 'Disponible'.
     * Usado para llenar el ComboBox al momento de crear un pedido.
     */
    public static List<Plato> listarDisponibles() {
        List<Plato> lista = new ArrayList<>();
        String sql = SQL_SELECT + "WHERE e.est_descripcion = 'Disponible' ORDER BY p.pla_descripcion";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(Plato.crearDesdeResultSet(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar platos disponibles: " + e.getMessage(), e);
        }
        return lista;
    }

    // ── BUSCAR POR ID ─────────────────────────────────────────────────────────

    /** Busca un plato por su ID. Retorna null si no existe. */
    public static Plato buscarPorId(long id) {
        String sql = SQL_SELECT + "WHERE p.pla_id = ?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Plato.crearDesdeResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar plato: " + e.getMessage(), e);
        }
        return null;
    }

    // ── INSERTAR ──────────────────────────────────────────────────────────────

    /**
     * Crea un nuevo plato.
     * Retorna true si se insertó correctamente.
     */
    public static boolean insertar(String descripcion, String codigo,
                                   BigDecimal precio, BigDecimal costo,
                                   long catId, long estId) {
        String sql = "INSERT INTO plato (pla_descripcion, pla_codigo, pla_precio, pla_costo, cat_id, est_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, descripcion);
            ps.setString(2, codigo);
            ps.setBigDecimal(3, precio);
            ps.setBigDecimal(4, costo);
            ps.setLong(5, catId);
            ps.setLong(6, estId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar plato: " + e.getMessage(), e);
        }
    }

    // ── ACTUALIZAR ────────────────────────────────────────────────────────────

    /** Actualiza todos los campos editables de un plato. */
    public static boolean actualizar(long id, String descripcion, String codigo,
                                     BigDecimal precio, BigDecimal costo,
                                     long catId, long estId) {
        String sql = "UPDATE plato SET pla_descripcion=?, pla_codigo=?, pla_precio=?, " +
                     "pla_costo=?, cat_id=?, est_id=? WHERE pla_id=?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, descripcion);
            ps.setString(2, codigo);         // null si no aplica
            ps.setBigDecimal(3, precio);
            ps.setBigDecimal(4, costo);      // null si no aplica
            ps.setLong(5, catId);
            ps.setLong(6, estId);
            ps.setLong(7, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar plato: " + e.getMessage(), e);
        }
    }

    /**
     * Cambia el estado de un plato: Disponible → Agotado → Inactivo.
     * Se pasa el est_id correspondiente al nuevo estado.
     */
    public static boolean cambiarEstado(long plaId, long estId) {
        String sql = "UPDATE plato SET est_id=? WHERE pla_id=?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, estId);
            ps.setLong(2, plaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar estado del plato: " + e.getMessage(), e);
        }
    }

    // ── OBTENER SIGUIENTE CÓDIGO ──────────────────────────────────────────────

    /**
     * Obtiene el siguiente código disponible para un nuevo plato.
     * Formato: PLT-XXX (ej. PLT-008)
     */
    public static String obtenerSiguienteCodigo() {
        String sql = "SELECT MAX(pla_codigo) AS max_codigo FROM plato WHERE pla_codigo LIKE 'PLT-%'";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            if (rs.next()) {
                String maxCodigo = rs.getString("max_codigo");
                if (maxCodigo != null && maxCodigo.startsWith("PLT-")) {
                    try {
                        int num = Integer.parseInt(maxCodigo.substring(4));
                        return String.format("PLT-%03d", num + 1);
                    } catch (NumberFormatException e) {
                        // ignore and use default
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener el siguiente código: " + e.getMessage(), e);
        }
        return "PLT-001";
    }
}
