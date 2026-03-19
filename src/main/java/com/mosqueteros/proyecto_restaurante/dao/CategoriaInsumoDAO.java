package com.mosqueteros.proyecto_restaurante.dao;

import com.mosqueteros.proyecto_restaurante.model.CategoriaInsumo;
import com.mosqueteros.proyecto_restaurante.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad CategoriaInsumo.
 * Contiene todas las operaciones CRUD contra la tabla `categoria_insumo`.
 *
 * Patrón: DAO estático (métodos estáticos, sin instancia).
 * Conexión obtenida de {@link ConexionDB#obtenerConexion()}.
 */
public class CategoriaInsumoDAO {

    // ─────────────────────────────────────────────────────────────
    // READ: listar todas las categorías
    // ─────────────────────────────────────────────────────────────

    /**
     * Obtiene todas las categorías de insumo de la BD.
     *
     * @return Lista de {@link CategoriaInsumo} con todos los registros
     * @throws SQLException si hay error de conexión o consulta
     */
    public static List<CategoriaInsumo> listarTodas() throws SQLException {
        List<CategoriaInsumo> lista = new ArrayList<>();
        String sql = """
                SELECT cat_ins_id, cat_ins_nombre, cat_ins_descripcion, cat_ins_estado
                FROM categoria_insumo
                ORDER BY cat_ins_nombre
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

    // ─────────────────────────────────────────────────────────────
    // READ: buscar con filtros dinámicos
    // ─────────────────────────────────────────────────────────────

    /**
     * Busca categorías aplicando filtros opcionales.
     * Si un parámetro es null o vacío, no se aplica ese filtro.
     *
     * @param busqueda Texto libre para buscar en nombre o descripción
     * @param estado   Estado a filtrar: "Activo" o "Inactivo" (puede ser null)
     * @return Lista filtrada de {@link CategoriaInsumo}
     * @throws SQLException si hay error de BD
     */
    public static List<CategoriaInsumo> buscarConFiltros(String busqueda,
                                                          String estado) throws SQLException {
        List<CategoriaInsumo> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT cat_ins_id, cat_ins_nombre, cat_ins_descripcion, cat_ins_estado
                FROM categoria_insumo
                WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();

        // Filtro por texto libre en nombre o descripción
        if (busqueda != null && !busqueda.isBlank()) {
            sql.append(" AND (cat_ins_nombre LIKE ? OR cat_ins_descripcion LIKE ?)");
            String like = "%" + busqueda.trim() + "%";
            params.add(like); params.add(like);
        }
        // Filtro por estado
        if (estado != null && !estado.isBlank()) {
            sql.append(" AND cat_ins_estado = ?");
            params.add(estado);
        }
        sql.append(" ORDER BY cat_ins_nombre");

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearFila(rs));
            }
        }
        return lista;
    }

    // ─────────────────────────────────────────────────────────────
    // CREATE: insertar nueva categoría
    // ─────────────────────────────────────────────────────────────

    /**
     * Inserta una nueva categoría en la BD.
     *
     * @param c Objeto {@link CategoriaInsumo} con los datos a insertar
     * @return true si la inserción fue exitosa
     * @throws SQLException si hay error de BD (ej. nombre duplicado)
     */
    public static boolean insertar(CategoriaInsumo c) throws SQLException {
        String sql = """
                INSERT INTO categoria_insumo (cat_ins_nombre, cat_ins_descripcion, cat_ins_estado)
                VALUES (?, ?, ?)
                """;
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setString(3, c.getEstado());
            return ps.executeUpdate() > 0;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // UPDATE: actualizar categoría existente
    // ─────────────────────────────────────────────────────────────

    /**
     * Actualiza los datos de una categoría existente.
     *
     * @param c Objeto {@link CategoriaInsumo} con los datos actualizados
     * @return true si la actualización fue exitosa
     * @throws SQLException si hay error de BD
     */
    public static boolean actualizar(CategoriaInsumo c) throws SQLException {
        String sql = """
                UPDATE categoria_insumo
                SET cat_ins_nombre=?, cat_ins_descripcion=?, cat_ins_estado=?
                WHERE cat_ins_id=?
                """;
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setString(3, c.getEstado());
            ps.setLong(4, c.getIdCategoriaInsumo());
            return ps.executeUpdate() > 0;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // DELETE: eliminar categoría por ID
    // ─────────────────────────────────────────────────────────────

    /**
     * Elimina una categoría por su ID.
     * Precaución: verificar relaciones FK con tabla insumo antes de eliminar.
     *
     * @param id ID de la categoría a eliminar
     * @return true si la eliminación fue exitosa
     * @throws SQLException si hay error de BD o restricción FK
     */
    public static boolean eliminar(long id) throws SQLException {
        String sql = "DELETE FROM categoria_insumo WHERE cat_ins_id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // UTIL PRIVADO: mapear ResultSet a objeto CategoriaInsumo
    // ─────────────────────────────────────────────────────────────

    /**
     * Convierte una fila del {@link ResultSet} en un objeto {@link CategoriaInsumo}.
     *
     * @param rs ResultSet posicionado en la fila a leer
     * @return Objeto {@link CategoriaInsumo} con los datos de la fila
     * @throws SQLException si hay error al leer columnas
     */
    private static CategoriaInsumo mapearFila(ResultSet rs) throws SQLException {
        return new CategoriaInsumo(
                rs.getLong("cat_ins_id"),
                rs.getString("cat_ins_nombre"),
                rs.getString("cat_ins_descripcion"),
                rs.getString("cat_ins_estado")
        );
    }
}
