package com.mosqueteros.proyecto_restaurante.dao;

import com.mosqueteros.proyecto_restaurante.model.Configuracion;
import com.mosqueteros.proyecto_restaurante.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConfiguracionDAO {

    public static List<Configuracion> listarTodas() throws SQLException {
        String sql = """
                SELECT cfg_id, cfg_clave, cfg_valor
                FROM configuracion
                ORDER BY cfg_clave
                """;

        List<Configuracion> lista = new ArrayList<>();
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public static List<Configuracion> buscarConFiltros(String textoBusqueda) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT cfg_id, cfg_clave, cfg_valor
                FROM configuracion
                WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();

        if (textoBusqueda != null && !textoBusqueda.isBlank()) {
            sql.append(" AND (cfg_clave LIKE ? OR cfg_valor LIKE ?)");
            String like = "%" + textoBusqueda.trim() + "%";
            params.add(like);
            params.add(like);
        }
        sql.append(" ORDER BY cfg_clave");

        List<Configuracion> lista = new ArrayList<>();
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public static boolean insertar(Configuracion configuracion) throws SQLException {
        String sql = """
                INSERT INTO configuracion (cfg_clave, cfg_valor)
                VALUES (?, ?)
                """;

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, configuracion.getCfgClave());
            ps.setString(2, normalizarValor(configuracion.getCfgValor()));
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean actualizar(Configuracion configuracion) throws SQLException {
        String sql = """
                UPDATE configuracion
                SET cfg_clave = ?, cfg_valor = ?
                WHERE cfg_id = ?
                """;

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, configuracion.getCfgClave());
            ps.setString(2, normalizarValor(configuracion.getCfgValor()));
            ps.setLong(3, configuracion.getCfgId());
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean eliminar(long cfgId) throws SQLException {
        String sql = "DELETE FROM configuracion WHERE cfg_id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, cfgId);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean existeClave(String cfgClave, Long excluirCfgId) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM configuracion WHERE cfg_clave = ?");
        if (excluirCfgId != null) {
            sql.append(" AND cfg_id <> ?");
        }

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setString(1, cfgClave);
            if (excluirCfgId != null) {
                ps.setLong(2, excluirCfgId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public static String obtenerValorPorClave(String cfgClave) throws SQLException {
        String sql = "SELECT cfg_valor FROM configuracion WHERE cfg_clave = ? LIMIT 1";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cfgClave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("cfg_valor");
                }
            }
        }
        return null;
    }

    public static boolean guardarPorClave(String cfgClave, String cfgValor) throws SQLException {
        String sqlActualizar = "UPDATE configuracion SET cfg_valor = ? WHERE cfg_clave = ?";
        String sqlInsertar = "INSERT INTO configuracion (cfg_clave, cfg_valor) VALUES (?, ?)";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement psActualizar = con.prepareStatement(sqlActualizar)) {
            String valorNormalizado = normalizarValor(cfgValor);
            psActualizar.setString(1, valorNormalizado);
            psActualizar.setString(2, cfgClave);
            int filasActualizadas = psActualizar.executeUpdate();
            if (filasActualizadas > 0) {
                return true;
            }

            try (PreparedStatement psInsertar = con.prepareStatement(sqlInsertar)) {
                psInsertar.setString(1, cfgClave);
                psInsertar.setString(2, valorNormalizado);
                return psInsertar.executeUpdate() > 0;
            }
        }
    }

    private static Configuracion mapear(ResultSet rs) throws SQLException {
        return new Configuracion(
                rs.getLong("cfg_id"),
                rs.getString("cfg_clave"),
                rs.getString("cfg_valor")
        );
    }

    private static String normalizarValor(String valor) {
        return valor == null ? null : valor.trim();
    }
}
