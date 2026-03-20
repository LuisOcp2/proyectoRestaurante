package com.mosqueteros.proyecto_restaurante.dao;

import com.mosqueteros.proyecto_restaurante.model.Cliente;
import com.mosqueteros.proyecto_restaurante.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    public static List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = """
                SELECT cli_id, cli_tipo_documento, cli_num_documento, cli_nombre, cli_correo, cli_telefono, cli_estado
                FROM cliente
                ORDER BY cli_nombre
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

    public static List<Cliente> buscarConFiltros(String busqueda, String estado) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT cli_id, cli_tipo_documento, cli_num_documento, cli_nombre, cli_correo, cli_telefono, cli_estado
                FROM cliente
                WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();

        if (busqueda != null && !busqueda.isBlank()) {
            sql.append(" AND (cli_nombre LIKE ? OR cli_num_documento LIKE ? OR cli_correo LIKE ?)");
            String like = "%" + busqueda.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (estado != null && !estado.isBlank()) {
            sql.append(" AND cli_estado = ?");
            params.add(estado);
        }
        sql.append(" ORDER BY cli_nombre");

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

    public static boolean insertar(Cliente c) throws SQLException {
        String sql = """
                INSERT INTO cliente (cli_nombre, cli_apellidos, cli_tipo_documento, cli_num_documento, cli_telefono, cli_correo, cli_estado)
                VALUES (?, '', ?, ?, ?, ?, ?)
                """;
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCliNombre());
            ps.setString(2, c.getCliTipoDocumento() != null ? c.getCliTipoDocumento() : "CC");
            ps.setString(3, c.getCliDocumento());
            ps.setString(4, c.getCliTelefono());
            ps.setString(5, c.getCliCorreo());
            ps.setString(6, c.getCliEstado() != null ? c.getCliEstado() : "Activo");
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean actualizar(Cliente c) throws SQLException {
        String sql = """
                UPDATE cliente
                SET cli_nombre = ?, cli_tipo_documento = ?, cli_num_documento = ?, cli_telefono = ?, cli_correo = ?, cli_estado = ?
                WHERE cli_id = ?
                """;
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCliNombre());
            ps.setString(2, c.getCliTipoDocumento() != null ? c.getCliTipoDocumento() : "CC");
            ps.setString(3, c.getCliDocumento());
            ps.setString(4, c.getCliTelefono());
            ps.setString(5, c.getCliCorreo());
            ps.setString(6, c.getCliEstado() != null ? c.getCliEstado() : "Activo");
            ps.setLong(7, c.getCliId());
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean eliminar(long id) throws SQLException {
        String sql = "DELETE FROM cliente WHERE cli_id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private static Cliente mapearFila(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getLong("cli_id"),
                rs.getString("cli_tipo_documento"),
                rs.getString("cli_num_documento"),
                rs.getString("cli_nombre"),
                rs.getString("cli_correo"),
                rs.getString("cli_telefono"),
                rs.getString("cli_estado")
        );
    }
}
