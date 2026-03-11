package com.mosqueteros.proyecto_restaurante.dao;

import com.mosqueteros.proyecto_restaurante.model.AreaMesa;
import com.mosqueteros.proyecto_restaurante.util.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AreaMesaDAO {
    public static List<AreaMesa> listarTodas() {
        List<AreaMesa> lista = new ArrayList<>();
        String sql = "SELECT arem_id, arem_descripcion FROM areamesa ORDER BY arem_descripcion";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new AreaMesa(rs.getLong("arem_id"), rs.getString("arem_descripcion")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar áreas de mesa: " + e.getMessage(), e);
        }
        return lista;
    }
}
