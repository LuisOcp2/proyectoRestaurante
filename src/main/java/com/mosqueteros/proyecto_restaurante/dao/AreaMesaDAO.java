package com.mosqueteros.proyecto_restaurante.dao; 


import com.mosqueteros.proyecto_restaurante.model.AreaMesa;
import com.mosqueteros.proyecto_restaurante.util.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla areamesa (áreas o salones del restaurante por sede).
 * Nombre de tabla en BD: areamesa (confirmar con SHOW TABLES si hay error).
 */
public class AreaMesaDAO {

    // ── Nombre de tabla — cambiar a "area_mesa" si la BD lo requiere ──────
    private static final String TABLA = "area_mesa";

    /**
     * Lista todas las áreas de mesa activas de la BD.
     * Llamado desde MesaController.cargarAreas() al inicializar la vista.
     *
     * @return Lista de AreaMesa, vacía si no hay registros.
     * @throws RuntimeException si hay error de conexión o SQL.
     */
    public static List<AreaMesa> listarTodas() {
        List<AreaMesa> lista = new ArrayList<>();
        String sql = "SELECT area_id, sede_id, area_nombre, area_estado " +
                     "FROM " + TABLA + " WHERE area_estado = 'Activo' ORDER BY area_nombre";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AreaMesa a = new AreaMesa();
                a.setId(rs.getLong("area_id"));
                a.setSedeId(rs.getLong("sede_id"));
                a.setNombre(rs.getString("area_nombre"));
                a.setEstado(rs.getString("area_estado"));
                lista.add(a);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar áreas de mesa: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Lista áreas por sede específica (útil para filtrar según la sede del usuario).
     *
     * @param sedeId ID de la sede a filtrar.
     * @return Lista de áreas de esa sede en estado Activo.
     */
    public static List<AreaMesa> listarPorSede(long sedeId) {
        List<AreaMesa> lista = new ArrayList<>();
        String sql = "SELECT area_id, sede_id, area_nombre, area_estado " +
                     "FROM " + TABLA +
                     " WHERE sede_id = ? AND area_estado = 'Activo' ORDER BY area_nombre";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, sedeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AreaMesa a = new AreaMesa();
                    a.setId(rs.getLong("area_id"));
                    a.setSedeId(rs.getLong("sede_id"));
                    a.setNombre(rs.getString("area_nombre"));
                    a.setEstado(rs.getString("area_estado"));
                    lista.add(a);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar áreas por sede: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Inserta una nueva área de mesa en la BD.
     *
     * @return true si se insertó correctamente.
     */
    public static boolean insertar(long sedeId, String nombre) {
        String sql = "INSERT INTO " + TABLA + " (sede_id, area_nombre) VALUES (?, ?)";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, sedeId);
            ps.setString(2, nombre);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar área de mesa: " + e.getMessage(), e);
        }
    }

    /**
     * Desactiva un área de mesa (NO elimina físicamente).
     *
     * @param areaId ID del área a desactivar.
     * @return true si se actualizó correctamente.
     */
    public static boolean desactivar(long areaId) {
        String sql = "UPDATE " + TABLA + " SET area_estado = 'Inactivo' WHERE area_id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, areaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al desactivar área de mesa: " + e.getMessage(), e);
        }
    }
}
