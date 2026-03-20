package com.mosqueteros.proyecto_restaurante.dao;

import com.mosqueteros.proyecto_restaurante.model.Usuario;
import com.mosqueteros.proyecto_restaurante.model.Perfil;
import com.mosqueteros.proyecto_restaurante.util.ConexionDB;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import io.github.palexdev.materialfx.controls.*;

/**
 * DAO (Data Access Object) para la entidad Usuario.
 * Contiene todas las operaciones CRUD contra la tabla `usuario`.
 *
 * Patrón usado: DAO estático (métodos estáticos, sin instancia).
 * La conexión se obtiene de {@link ConexionDB#obtenerConexion()}.
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
        String sql = """
                SELECT u.usu_id, u.usu_nombre, u.usu_apellido, u.usu_direccion,
                       u.usu_telefono, u.usu_correo, u.perf_id,
                       COALESCE(p.perf_descripcion, 'Sin perfil') AS perfilDescripcion,
                       u.usu_login, u.usu_pass, u.usu_estado
                FROM usuario u
                LEFT JOIN perfil p ON u.perf_id = p.perf_id
                ORDER BY u.usu_nombre, u.usu_apellido
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
        StringBuilder sql = new StringBuilder("""
                SELECT u.usu_id, u.usu_nombre, u.usu_apellido, u.usu_direccion,
                       u.usu_telefono, u.usu_correo, u.perf_id,
                       COALESCE(p.perf_descripcion, 'Sin perfil') AS perfilDescripcion,
                       u.usu_login, u.usu_pass, u.usu_estado
                FROM usuario u
                LEFT JOIN perfil p ON u.perf_id = p.perf_id
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if (busqueda != null && !busqueda.isBlank()) {
            sql.append(" AND (u.usu_nombre LIKE ? OR u.usu_apellido LIKE ? OR u.usu_login LIKE ?)");
            String like = "%" + busqueda.trim() + "%";
            params.add(like); params.add(like); params.add(like);
        }
        if (perfil != null && !perfil.isBlank()) {
            sql.append(" AND p.perf_descripcion = ?");
            params.add(perfil);
        }
        if (estado != null && !estado.isBlank()) {
            sql.append(" AND u.usu_estado = ?");
            params.add(estado);
        }
        sql.append(" ORDER BY u.usu_nombre, u.usu_apellido");

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

    // ─────────────────────────────────────────────────────────────
    // CREATE: insertar nuevo usuario
    // ─────────────────────────────────────────────────────────────

    /**
     * Inserta un nuevo usuario en la BD.
     * Hashea la contraseña con BCrypt antes de guardar.
     *
     * @param usuario Objeto {@link Usuario} con los datos a insertar
     * @return true si la inserción fue exitosa
     * @throws SQLException si hay error de BD (ej. login/correo duplicado)
     */
    public static boolean insertar(Usuario usuario) throws SQLException {
        String sql = """
                INSERT INTO usuario
                (usu_nombre, usu_apellido, usu_direccion, usu_telefono,
                 usu_correo, perf_id, usu_login, usu_pass, usu_estado)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        // Hashear la contraseña antes de guardar
        String passHash = BCrypt.hashpw(usuario.getUsupass(), BCrypt.gensalt());

        try (Connection con = ConexionDB.obtenerConexion();
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
            ps.setString(8, passHash); // hash bcrypt
            ps.setString(9, usuario.getUsuestado());
            return ps.executeUpdate() > 0;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // UPDATE: actualizar usuario existente
    // ─────────────────────────────────────────────────────────────

    /**
     * Actualiza los datos de un usuario existente.
     * Si usupass NO está vacío, hashea y actualiza la contraseña.
     * Si usupass está vacío, conserva la contraseña actual.
     *
     * @param usuario Objeto {@link Usuario} con los datos actualizados
     * @return true si la actualización fue exitosa
     * @throws SQLException si hay error de BD
     */
    public static boolean actualizar(Usuario usuario) throws SQLException {
        boolean actualizarPass = usuario.getUsupass() != null && !usuario.getUsupass().isBlank();

        String sql = actualizarPass
                ? """
                  UPDATE usuario SET
                    usu_nombre=?, usu_apellido=?, usu_direccion=?, usu_telefono=?,
                    usu_correo=?, perf_id=?, usu_login=?, usu_pass=?, usu_estado=?
                  WHERE usu_id=?
                  """
                : """
                  UPDATE usuario SET
                    usu_nombre=?, usu_apellido=?, usu_direccion=?, usu_telefono=?,
                    usu_correo=?, perf_id=?, usu_login=?, usu_estado=?
                  WHERE usu_id=?
                  """;

        try (Connection con = ConexionDB.obtenerConexion();
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
                // Hashear antes de guardar
                String passHash = BCrypt.hashpw(usuario.getUsupass(), BCrypt.gensalt());
                ps.setString(8, passHash);
                ps.setString(9, usuario.getUsuestado());
                ps.setLong(10, usuario.getUsuid());
            } else {
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
     *
     * @param usuid ID del usuario a eliminar
     * @return true si la eliminación fue exitosa
     * @throws SQLException si hay error de BD o restricción FK
     */
    public static boolean eliminar(long usuid) throws SQLException {
        String sql = "DELETE FROM usuario WHERE usu_id = ?";
        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, usuid);
            return ps.executeUpdate() > 0;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // UTIL: listar perfiles disponibles para MFXComboBox
    // ─────────────────────────────────────────────────────────────

    /**
     * Carga todos los perfiles activos desde la tabla `perfil`.
     * Se usa para poblar el MFXComboBox de perfiles en el formulario de usuarios.
     *
     * @return Lista de objetos {@link Perfil}
     * @throws SQLException si hay error de BD
     */
    public static List<Perfil> listarPerfiles() throws SQLException {
        List<Perfil> perfiles = new ArrayList<>();
        String sql = "SELECT perf_id, perf_descripcion FROM perfil WHERE perf_estado='Activo' ORDER BY perf_descripcion";

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Perfil p = new Perfil();
                p.setPerfid(rs.getLong("perf_id"));              // usa setter correcto
                p.setPerfdescripcion(rs.getString("perf_descripcion")); // usa setter correcto
                perfiles.add(p);
            }
        }
        return perfiles;
    }

    // ─────────────────────────────────────────────────────────────
    // AUTENTICAR: login con verificación bcrypt
    // ─────────────────────────────────────────────────────────────

    /**
     * Autentica un usuario verificando login + contraseña con BCrypt.
     *
     * Flujo:
     *   1. Busca el usuario activo por login en BD
     *   2. Verifica que el password ingresado coincide con el hash almacenado
     *   3. Retorna el objeto Usuario si todo es correcto, null si no
     *
     * @param login    Login ingresado en el formulario
     * @param password Contraseña en texto plano ingresada en el formulario
     * @return Objeto {@link Usuario} autenticado, o null si credenciales inválidas
     * @throws SQLException si hay error de BD
     */
    public static Usuario autenticar(String login, String password) throws SQLException {
        String sql = """
                SELECT u.usu_id, u.usu_nombre, u.usu_apellido, u.usu_direccion,
                       u.usu_telefono, u.usu_correo, u.perf_id,
                       COALESCE(p.perf_descripcion, 'Sin perfil') AS perfilDescripcion,
                       u.usu_login, u.usu_pass, u.usu_estado
                FROM usuario u
                LEFT JOIN perfil p ON u.perf_id = p.perf_id
                WHERE u.usu_login = ? AND u.usu_estado = 'Activo'
                """;

        try (Connection con = ConexionDB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashAlmacenado = rs.getString("usu_pass");
                    // Verificar contraseña con BCrypt
                    if (BCrypt.checkpw(password, hashAlmacenado)) {
                        return mapearFila(rs);
                    }
                }
            }
        }
        // Credenciales inválidas o usuario inactivo
        return null;
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
        u.setUsuid(rs.getLong("usu_id"));
        u.setUsunombre(rs.getString("usu_nombre"));
        u.setUsuapellido(rs.getString("usu_apellido"));
        u.setUsudireccion(rs.getString("usu_direccion"));
        u.setUsutelefono(rs.getString("usu_telefono"));
        u.setUsucorreo(rs.getString("usu_correo"));
        long perfid = rs.getLong("perf_id");
        u.setPerfid(rs.wasNull() ? null : perfid);
        u.setPerfilDescripcion(rs.getString("perfilDescripcion"));
        u.setUsulogin(rs.getString("usu_login"));
        u.setUsupass(rs.getString("usu_pass"));
        u.setUsuestado(rs.getString("usu_estado"));
        return u;
    }
}
