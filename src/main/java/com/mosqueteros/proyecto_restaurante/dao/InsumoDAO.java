package com.mosqueteros.proyecto_restaurante.dao;

import com.mosqueteros.proyecto_restaurante.model.Insumo;
import com.mosqueteros.proyecto_restaurante.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InsumoDAO {
    public static List<Insumo> listarTodos() throws SQLException {
        List<Insumo> lista = new ArrayList<>();
        String sql = """
                SELECT i.ins_id, i.sede_id, i.cins_id, i.pres_id, i.ins_nombre, i.ins_stock, i.ins_estado,
                       c.cins_nombre AS categoria_nombre,
                       p.pres_abreviatura AS presentacion_abreviatura
                FROM insumo i
                LEFT JOIN categoria_insumo c ON c.cins_id = i.cins_id
                LEFT JOIN presentacion p ON p.pres_id = i.pres_id
                ORDER BY i.ins_nombre
                """;
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearFila(rs));
            }
        }
        return lista;
    }

    public static List<Insumo> buscarConFiltros(String busqueda, String estado) throws SQLException {
        List<Insumo> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT i.ins_id, i.sede_id, i.cins_id, i.pres_id, i.ins_nombre, i.ins_stock, i.ins_estado,
                       c.cins_nombre AS categoria_nombre,
                       p.pres_abreviatura AS presentacion_abreviatura
                FROM insumo i
                LEFT JOIN categoria_insumo c ON c.cins_id = i.cins_id
                LEFT JOIN presentacion p ON p.pres_id = i.pres_id
                WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();

        if (busqueda != null && !busqueda.isBlank()) {
            sql.append(" AND (i.ins_nombre LIKE ? OR c.cins_nombre LIKE ?)");
            String like = "%" + busqueda.trim() + "%";
            params.add(like);
            params.add(like);
        }
        if (estado != null && !estado.isBlank()) {
            sql.append(" AND i.ins_estado = ?");
            params.add("Activo".equalsIgnoreCase(estado) ? 1 : 0);
        }
        sql.append(" ORDER BY i.ins_nombre");

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearFila(rs));
                }
            }
        }
        return lista;
    }

    public static boolean insertar(Insumo i) throws SQLException {
        String sql = """
                INSERT INTO insumo (sede_id, cins_id, pres_id, ins_nombre, ins_stock, ins_estado)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, i.getSedeId());
            ps.setLong(2, i.getCategoriaId());
            ps.setLong(3, i.getPresentacionId());
            ps.setString(4, i.getInsNombre());
            ps.setDouble(5, i.getInsStock());
            ps.setInt(6, "Activo".equalsIgnoreCase(i.getInsEstado()) ? 1 : 0);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean actualizar(Insumo i) throws SQLException {
        String sql = """
                UPDATE insumo
                SET cins_id = ?, pres_id = ?, ins_nombre = ?, ins_stock = ?, ins_estado = ?
                WHERE ins_id = ?
                """;
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, i.getCategoriaId());
            ps.setLong(2, i.getPresentacionId());
            ps.setString(3, i.getInsNombre());
            ps.setDouble(4, i.getInsStock());
            ps.setInt(5, "Activo".equalsIgnoreCase(i.getInsEstado()) ? 1 : 0);
            ps.setLong(6, i.getInsId());
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean eliminar(long id) throws SQLException {
        String sql = "DELETE FROM insumo WHERE ins_id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public static List<String> listarCategorias() throws SQLException {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT cins_nombre FROM categoria_insumo WHERE cins_estado = 1 ORDER BY cins_nombre";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("cins_nombre"));
            }
        }
        return lista;
    }

    public static List<String> listarPresentaciones() throws SQLException {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT pres_abreviatura FROM presentacion WHERE pres_estado = 'Activo' ORDER BY pres_abreviatura";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("pres_abreviatura"));
            }
        }
        return lista;
    }

    public static long obtenerIdCategoriaPorNombre(String nombre) throws SQLException {
        String sql = "SELECT cins_id FROM categoria_insumo WHERE cins_nombre = ? LIMIT 1";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("cins_id");
            }
        }
        throw new SQLException("Categoria de insumo no encontrada: " + nombre);
    }

    public static long obtenerIdPresentacionPorAbreviatura(String abrev) throws SQLException {
        String sql = "SELECT pres_id FROM presentacion WHERE pres_abreviatura = ? LIMIT 1";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, abrev);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("pres_id");
            }
        }
        throw new SQLException("Presentacion no encontrada: " + abrev);
    }

    private static Insumo mapearFila(ResultSet rs) throws SQLException {
        Insumo i = new Insumo();
        i.setInsId(rs.getLong("ins_id"));
        i.setSedeId(rs.getLong("sede_id"));
        i.setCategoriaId(rs.getLong("cins_id"));
        i.setPresentacionId(rs.getLong("pres_id"));
        i.setInsNombre(rs.getString("ins_nombre"));
        i.setInsStock(rs.getDouble("ins_stock"));
        i.setInsEstado(rs.getInt("ins_estado") == 1 ? "Activo" : "Inactivo");
        i.setCategoriaNombre(rs.getString("categoria_nombre"));
        i.setPresentacionAbreviatura(rs.getString("presentacion_abreviatura"));
        return i;
    }
}
