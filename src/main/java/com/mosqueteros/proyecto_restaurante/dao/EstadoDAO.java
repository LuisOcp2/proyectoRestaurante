package com.mosqueteros.proyecto_restaurante.dao;

import com.mosqueteros.proyecto_restaurante.model.Estado;
import com.mosqueteros.proyecto_restaurante.util.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadoDAO {
    public static List<Estado> listarPorTipo(int tipoEstadoId) {
        List<Estado> lista = new ArrayList<>();
        String sql = "SELECT est_id, est_nombre, est_descripcion FROM estado WHERE test_id = ? ORDER BY est_nombre";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, tipoEstadoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Estado(rs.getInt("est_id"), rs.getString("est_nombre")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar estados: " + e.getMessage(), e);
        }
        return lista;
    }
}
