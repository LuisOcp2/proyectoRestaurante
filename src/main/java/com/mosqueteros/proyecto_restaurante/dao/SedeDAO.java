package com.mosqueteros.proyecto_restaurante.dao;

import com.mosqueteros.proyecto_restaurante.model.Sede;
import com.mosqueteros.proyecto_restaurante.util.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SedeDAO {
    public static List<Sede> listarTodas() {
        List<Sede> lista = new ArrayList<>();
        String sql = "SELECT sede_id, sede_nombre FROM sede ORDER BY sede_nombre";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Sede(rs.getLong("sede_id"), rs.getString("sede_nombre")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar sedes: " + e.getMessage(), e);
        }
        return lista;
    }

    public static Sede buscarPorId(Long sedeId) {
        if (sedeId == null) {
            return null;
        }
        String sql = "SELECT sede_id, sede_nombre FROM sede WHERE sede_id = ? LIMIT 1";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, sedeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Sede(rs.getLong("sede_id"), rs.getString("sede_nombre"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar sede por id: " + e.getMessage(), e);
        }
        return null;
    }
}
