package com.mosqueteros.proyecto_restaurante.dao;

import com.mosqueteros.proyecto_restaurante.model.Usuario;
import com.mosqueteros.proyecto_restaurante.util.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Usuario.
 * Contiene todas las operaciones CRUD contra la tabla `usuario`.
 *
 * Patrón usado: DAO estático (métodos estáticos, sin instancia).
 * La conexión se obtiene de {@link Conexion#getConexion()}.
 */
public class UsuarioDAO {

    // ─────────────────────────────────────────────────────────────
    // READ: listar todos los usuarios con JOIN a perfil
    // ─────────────────────────────────────────────────────────────

    /**
     * Obtiene todos los usuarios de la BD con su perfil.
     * Hace JOIN con la tabla `perfil` para traer la descripción del rol.
     *
     * @return Lista de {@link Usuario} con todos los registros
     * @throws SQLException si hay error de conexión o consulta
     */
    public static List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();

        // Consulta con JOIN para obtener el nombre del perfil
        String sql = """
                SELECT u.usuid, u.usunombre, u.usuapellido, u.usudireccion,
                       u.usutelefono, u.usucorreo, u.perfid,
                       COALESCE(p.perfdescripcion, 'Sin perfil') AS perfilDescripcion,
                       u.usulogin, u.usupass, u.usuestado
                FROM usuario u
                LEFT JOIN perfil p ON u.perfid = p.perfid
                ORDER BY u.usunombre, u.usuapellido
                """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearFila(rs)); // convierte cada fila en un objeto Usuario
            }
        }
        return lista;
    }

    // ─────────────────────────────────────────────────────────────
    // READ: buscar con filtros dinámicos
    // ─────────────────────────────────────────────────────────────

    /**
     * Busca usuarios aplicando filtros opcionales.
     * Si un parámetro es null o vacío, no se aplica ese filtro.
     *
     * @param busqueda   Texto libre para buscar en nombre, apellido o login
     * @param perfil     Descripción del perfil a filtrar (puede ser null)
     * @param estado     Estado a filtrar: "Activo" o "Inactivo" (puede ser null)
     * @return Lista filtrada de {@link Usuario}
     * @throws SQLException si hay error de conexión o consulta
     */
    public static List<Usuario> buscarConFiltros(String busqueda,
                                                  String perfil,
                                                  String estado) throws SQLException {
        List<Usuario> lista = new ArrayList<>();

        // Construcción dinámica del WHERE con parámetros opcionales
        StringBuilder sql = new StringBuilder("""
                SELECT u.usuid, u.usunombre, u.usuapellido, u.usudireccion,
                       u.usutelefono, u.usucorreo, u.perfid,
                       COALESCE(p.perfdescripcion, 'Sin perfil') AS perfilDescripcion,
                       u.usulogin, u.usupass, u.usuestado
                FROM usuario u
                LEFT JOIN perfil p ON u.perfid = p.perfid
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        // Filtro por texto libre (nombre, apellido o login)
        if (busqueda != null && !busqueda.isBlank()) {
            sql.append(" AND (u.usunombre LIKE ? OR u.usuapellido LIKE ? OR u.usulogin LIKE ?)");
            String like = "%" + busqueda.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }

        // Filtro por perfil
        if (perfil != null && !perfil.isBlank()) {
            sql.append(" AND p.perfdescripcion = ?");
            params.add(perfil);
        }

        // Filtro por estado
        if (estado != null && !estado.isBlank()) {
            sql.append(" AND u.usuestado = ?");
            params.add(estado);
        }

        sql.append(" ORDER BY u.usunombre, u.usuapellido");

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            // Asignar parámetros dinámicamente
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

    // ─────────────────────────────────────────────────────────────
    // CREATE: insertar nuevo usuario
    // ─────────────────────────────────────────────────────────────

    /**
     * Inserta un nuevo usuario en la BD.
     * El campo usupass debe llegar ya hasheado desde el controlador.
     *
     * @param usuario Objeto {@link Usuario} con los datos a insertar
     * @return true si la inserción fue exitosa
     * @throws SQLException si hay error de BD (ej. login/correo duplicado)
     */
    public static boolean insertar(Usuario usuario) throws SQLException {
        String sql = """
                INSERT INTO usuario
                (usunombre, usuapellido, usudireccion, usutelefono,
                 usucorreo, perfid, usulogin, usupass, usuestado)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getUsunombre());
            ps.setString(2, usuario.getUsuapellido());
            ps.setString(3, usuario.getUsudireccion());
            ps.setString(4, usuario.getUsutelefono());
            ps.setString(5, usuario.getUsucorreo());

            // perfid puede ser null si el usuario no tiene perfil asignado
            if (usuario.getPerfid() != null) {
                ps.setLong(6, usuario.getPerfid());
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            ps.setString(7, usuario.getUsulogin());
            ps.setString(8, usuario.getUsupass()); // ya debe venir hasheado
            ps.setString(9, usuario.getUsuestado());

            return ps.executeUpdate() > 0;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // UPDATE: actualizar usuario existente
    // ─────────────────────────────────────────────────────────────

    /**
     * Actualiza los datos de un usuario existente.
     * Si usupass está vacío, NO actualiza la contraseña (conserva la actual).
     *
     * @param usuario Objeto {@link Usuario} con los datos actualizados
     * @return true si la actualización fue exitosa
     * @throws SQLException si hay error de BD
     */
    public static boolean actualizar(Usuario usuario) throws SQLException {
        boolean actualizarPass = usuario.getUsupass() != null && !usuario.getUsupass().isBlank();

        // SQL condicional: incluye o excluye usupass según corresponda
        String sql = actualizarPass
                ? """
                  UPDATE usuario SET
                    usunombre=?, usuapellido=?, usudireccion=?, usutelefono=?,
                    usucorreo=?, perfid=?, usulogin=?, usupass=?, usuestado=?
                  WHERE usuid=?
                  """
                : """
                  UPDATE usuario SET
                    usunombre=?, usuapellido=?, usudireccion=?, usutelefono=?,
                    usucorreo=?, perfid=?, usulogin=?, usuestado=?
                  WHERE usuid=?
                  """;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getUsunombre());
            ps.setString(2, usuario.getUsuapellido());
            ps.setString(3, usuario.getUsudireccion());
            ps.setString(4, usuario.getUsutelefono());
            ps.setString(5, usuario.getUsucorreo());

            if (usuario.getPerfid() != null) {
                ps.setLong(6, usuario.getPerfid());
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            ps.setString(7, usuario.getUsulogin());

            if (actualizarPass) {
                // Con contraseña: índices 8=pass, 9=estado, 10=id
                ps.setString(8, usuario.getUsupass());
                ps.setString(9, usuario.getUsuestado());
                ps.setLong(10, usuario.getUsuid());
            } else {
                // Sin contraseña: índices 8=estado, 9=id
                ps.setString(8, usuario.getUsuestado());
                ps.setLong(9, usuario.getUsuid());
            }

            return ps.executeUpdate() > 0;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // DELETE: eliminar usuario por ID
    // ─────────────────────────────────────────────────────────────

    /**
     * Elimina un usuario por su ID (usuid).
     * Precaución: verificar relaciones FK antes de eliminar
     * (pedido, recibocaja, inventariolog, etc.)
     *
     * @param usuid ID del usuario a eliminar
     * @return true si la eliminación fue exitosa
     * @throws SQLException si hay error de BD o restricción FK
     */
    public static boolean eliminar(long usuid) throws SQLException {
        String sql = "DELETE FROM usuario WHERE usuid = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, usuid);
            return ps.executeUpdate() > 0;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // UTIL: obtener lista de perfiles disponibles para ComboBox
    // ─────────────────────────────────────────────────────────────

    /**
     * Carga todos los perfiles activos desde la tabla `perfil`.
     * Se usa para poblar el ComboBox de perfiles en el formulario.
     *
     * @return Lista de objetos {@link Perfil}
     * @throws SQLException si hay error de BD
     */
    public static List<Perfil> listarPerfiles() throws SQLException {
        List<Perfil> perfiles = new ArrayList<>();
        String sql = "SELECT perfid, perfdescripcion FROM perfil WHERE perfestado='Activo' ORDER BY perfdescripcion";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Perfil p = new Perfil();
                p.setPerfid(rs.getLong("perfid"));
                p.setPerfdescripcion(rs.getString("perfdescripcion"));
                perfiles.add(p);
            }
        }
        return perfiles;
    }

    // ─────────────────────────────────────────────────────────────
    // UTIL PRIVADO: mapear ResultSet a objeto Usuario
    // ─────────────────────────────────────────────────────────────

    /**
     * Convierte una fila del {@link ResultSet} en un objeto {@link Usuario}.
     * Método privado de uso interno para evitar repetición de código.
     *
     * @param rs ResultSet posicionado en la fila a leer
     * @return Objeto {@link Usuario} con los datos de la fila
     * @throws SQLException si hay error al leer columnas
     */
    private static Usuario mapearFila(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setUsuid(rs.getLong("usuid"));
        u.setUsunombre(rs.getString("usunombre"));
        u.setUsuapellido(rs.getString("usuapellido"));
        u.setUsudireccion(rs.getString("usudireccion"));
        u.setUsutelefono(rs.getString("usutelefono"));
        u.setUsucorreo(rs.getString("usucorreo"));

        // perfid puede ser null (LEFT JOIN)
        long perfid = rs.getLong("perfid");
        u.setPerfid(rs.wasNull() ? null : perfid);

        u.setPerfilDescripcion(rs.getString("perfilDescripcion"));
        u.setUsulogin(rs.getString("usulogin"));
        u.setUsupass(rs.getString("usupass"));
        u.setUsuestado(rs.getString("usuestado"));
        return u;
    }
}
