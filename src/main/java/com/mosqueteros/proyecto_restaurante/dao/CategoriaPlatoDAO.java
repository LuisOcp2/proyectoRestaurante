package com.mosqueteros.proyecto_restaurante.dao;

import com.mosqueteros.proyecto_restaurante.model.CategoriaPlato;
import com.mosqueteros.proyecto_restaurante.util.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaPlatoDAO {
    public static List<CategoriaPlato> listarTodas() {
        List<CategoriaPlato> lista = new ArrayList<>();
        String sql = "SELECT cat_id, cat_nombre FROM categoriaplato ORDER BY cat_nombre";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new CategoriaPlato(rs.getLong("cat_id"), rs.getString("cat_nombre")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar categorías de platos: " + e.getMessage(), e);
        }
        return lista;
    }
}
