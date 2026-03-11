package com.mosqueteros.proyecto_restaurante.dao;

import com.mosqueteros.proyecto_restaurante.model.Mesa;
import com.mosqueteros.proyecto_restaurante.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MesaDAO {

    private static final String SQL_SELECT =
        "SELECT m.mes_id, m.mes_numero, m.mes_capacidad, " +
        "m.arem_id, a.arem_descripcion, " +
        "m.sede_id, s.sede_nombre, " +
        "m.est_id, e.est_descripcion " +
        "FROM mesa m " +
        "JOIN areamesa a ON m.arem_id = a.arem_id " +
        "JOIN sede s ON m.sede_id = s.sede_id " +
        "JOIN estado e ON m.est_id = e.est_id ";

    
    public static List<Mesa> listarTodas() {
        List<Mesa> lista = new ArrayList<>();
        String sql = SQL_SELECT + "ORDER BY s.sede_nombre, m.mes_numero";

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
        String sql = SQL_SELECT + "WHERE m.sede_id = ? ORDER BY m.mes_numero";

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
        String sql = SQL_SELECT + "WHERE m.mes_id = ?";

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


    public static boolean insertar(int numero, int capacidad, long areaMesaId,
                                   long sedeId, long estId) {
        String sql = "INSERT INTO mesa (mes_numero, mes_capacidad, arem_id, sede_id, est_id) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, numero);
            ps.setInt(2, capacidad);
            ps.setLong(3, areaMesaId);
            ps.setLong(4, sedeId);
            ps.setLong(5, estId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar mesa: " + e.getMessage(), e);
        }
    }


    public static boolean actualizar(long id, int numero, int capacidad, long areaMesaId, long sedeId, long estId) {
        String sql = "UPDATE mesa SET mes_numero=?, mes_capacidad=?, arem_id=?, sede_id=?, est_id=? WHERE mes_id=?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, numero);
            ps.setInt(2, capacidad);
            ps.setLong(3, areaMesaId);
            ps.setLong(4, sedeId);
            ps.setLong(5, estId);
            ps.setLong(6, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar mesa: " + e.getMessage(), e);
        }
    }

    public static boolean cambiarEstado(long mesId, long estId) {
        String sql = "UPDATE mesa SET est_id=? WHERE mes_id=?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, estId);
            ps.setLong(2, mesId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar estado de mesa: " + e.getMessage(), e);
        }
    }

  
    public static boolean tienePedidosActivos(long mesId) {
        String sql = "SELECT COUNT(*) FROM pedido p " +
                     "JOIN estado e ON p.est_id = e.est_id " +
                     "WHERE p.mes_id = ? AND e.est_descripcion IN ('Creado','En Proceso')";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, mesId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar pedidos activos: " + e.getMessage(), e);
        }
        return false;
    }
}
