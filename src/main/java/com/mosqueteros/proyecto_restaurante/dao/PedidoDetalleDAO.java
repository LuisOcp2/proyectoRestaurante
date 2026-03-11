package com.mosqueteros.proyecto_restaurante.dao;

import com.mosqueteros.proyecto_restaurante.model.PedidoDetalle;
import com.mosqueteros.proyecto_restaurante.util.ConexionDB;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PedidoDetalleDAO {

    private static final String SQL_SELECT =
        "SELECT pd.pedd_id, pd.ped_id, pd.pla_id, pl.pla_nombre, " +
        "pd.pedd_cantidad, pd.pedd_precio_momento, pd.pedd_observaciones, " +
        "pd.est_id, e.est_descripcion " +
        "FROM pedidodetalle pd " +
        "JOIN plato pl ON pd.pla_id = pl.pla_id " +
        "JOIN estado e ON pd.est_id = e.est_id ";

    public static List<PedidoDetalle> listarPorPedido(long pedidoId) {
        List<PedidoDetalle> lista = new ArrayList<>();
        String sql = SQL_SELECT + "WHERE pd.ped_id = ? ORDER BY pd.pedd_id";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, pedidoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(PedidoDetalle.crearDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar detalle de pedido: " + e.getMessage(), e);
        }
        return lista;
    }

    public static List<PedidoDetalle> listarParaCocina(long sedeId) {
        List<PedidoDetalle> lista = new ArrayList<>();
        String sql =
            "SELECT pd.pedd_id, pd.ped_id, pd.pla_id, pl.pla_nombre, " +
            "pd.pedd_cantidad, pd.pedd_precio_momento, pd.pedd_observaciones, " +
            "pd.est_id, e.est_descripcion " +
            "FROM pedidodetalle pd " +
            "JOIN plato pl ON pd.pla_id = pl.pla_id " +
            "JOIN estado e ON pd.est_id = e.est_id " +
            "JOIN pedido p ON pd.ped_id = p.ped_id " +
            "WHERE p.sede_id = ? AND e.est_descripcion = 'En Preparación' " +
            "ORDER BY pd.ped_id, pd.pedd_id";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, sedeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(PedidoDetalle.crearDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar vista de cocina: " + e.getMessage(), e);
        }
        return lista;
    }

    public static PedidoDetalle buscarPorId(long id) {
        String sql = SQL_SELECT + "WHERE pd.pedd_id = ?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return PedidoDetalle.crearDesdeResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar línea de comanda: " + e.getMessage(), e);
        }
        return null;
    }

    public static boolean pedidoEstaFacturado(long pedidoId) {
        String sql = "SELECT COUNT(*) FROM recibocaja WHERE ped_id = ? " +
                     "AND rc_estado != 'Anulado'";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, pedidoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar facturación: " + e.getMessage(), e);
        }
        return false;
    }


    public static boolean insertar(long pedidoId, long platoId, int cantidad,
                                   BigDecimal precioMomento, String observaciones,
                                   long estId) {
        String sql = "INSERT INTO pedidodetalle " +
                     "(ped_id, pla_id, pedd_cantidad, pedd_precio_momento, pedd_observaciones, est_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, pedidoId);
            ps.setLong(2, platoId);
            ps.setInt(3, cantidad);
            ps.setBigDecimal(4, precioMomento);
            ps.setString(5, observaciones);
            ps.setLong(6, estId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar línea de comanda: " + e.getMessage(), e);
        }
    }

    public static boolean actualizar(long peddId, int cantidad, String observaciones) {
        String sql = "UPDATE pedidodetalle SET pedd_cantidad=?, pedd_observaciones=? WHERE pedd_id=?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cantidad);
            ps.setString(2, observaciones);
            ps.setLong(3, peddId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar línea de comanda: " + e.getMessage(), e);
        }
    }

    public static boolean cambiarEstado(long peddId, long estId) {
        String sql = "UPDATE pedidodetalle SET est_id=? WHERE pedd_id=?";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, estId);
            ps.setLong(2, peddId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar estado de línea: " + e.getMessage(), e);
        }
    }
}
