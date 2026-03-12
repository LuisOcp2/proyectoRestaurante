package com.mosqueteros.proyecto_restaurante.dao;

import com.mosqueteros.proyecto_restaurante.model.Plato;
import com.mosqueteros.proyecto_restaurante.util.ConexionDB;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlatoDAO {

    private static final String SQL_SELECT
            = "SELECT p.pla_id, p.pla_descripcion, p.pla_codigo, "
            + "p.pla_precio, p.pla_costo, p.cat_id, cp.cat_nombre, "
            + "p.est_id, e.est_descripcion "
            + "FROM plato p "
            + "JOIN categoria_plato cp ON p.cat_id = cp.cat_id "
            + "JOIN estado e ON p.est_id = e.est_id ";

    public static List<Plato> listarTodos() {
        List<Plato> lista = new ArrayList<>();
        String sql = SQL_SELECT + "ORDER BY p.pla_descripcion";

        try (Connection con = ConexionDB.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(Plato.crearDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar platos: " + e.getMessage(), e);
        }
        return lista;
    }

    public static List<Plato> listarDisponibles() {
        List<Plato> lista = new ArrayList<>();
        String sql = SQL_SELECT + "WHERE e.est_descripcion = 'Disponible' ORDER BY p.pla_descripcion";

        try (Connection con = ConexionDB.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(Plato.crearDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar platos disponibles: " + e.getMessage(), e);
        }
        return lista;
    }

    public static Plato buscarPorId(long id) {
        String sql = SQL_SELECT + "WHERE p.pla_id = ?";

        try (Connection con = ConexionDB.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Plato.crearDesdeResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar plato: " + e.getMessage(), e);
        }
        return null;
    }

    public static boolean insertar(String descripcion, String codigo,
            BigDecimal precio, BigDecimal costo,
            long catId, long estId) {
        String sql = "INSERT INTO plato (pla_descripcion, pla_codigo, pla_precio, pla_costo, cat_id, est_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

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

    public static boolean actualizar(long id, String descripcion, String codigo,
            BigDecimal precio, BigDecimal costo,
            long catId, long estId) {
        String sql = "UPDATE plato SET pla_descripcion=?, pla_codigo=?, pla_precio=?, "
                + "pla_costo=?, cat_id=?, est_id=? WHERE pla_id=?";
        try (Connection con = ConexionDB.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
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

    public static boolean cambiarEstado(long plaId, long estId) {
        String sql = "UPDATE plato SET est_id=? WHERE pla_id=?";

        try (Connection con = ConexionDB.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, estId);
            ps.setLong(2, plaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar estado del plato: " + e.getMessage(), e);
        }
    }

    public static String obtenerSiguienteCodigo() {
        String sql = "SELECT MAX(pla_codigo) AS max_codigo FROM plato WHERE pla_codigo LIKE 'PLT-%'";
        try (Connection con = ConexionDB.obtenerConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

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
