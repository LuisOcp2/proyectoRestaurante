package com.mosqueteros.proyecto_restaurante.dao;

import com.mosqueteros.proyecto_restaurante.model.Usuario;
import com.mosqueteros.proyecto_restaurante.util.ConexionDB;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UsuarioDAO {
    
    public static Usuario autenticar(String login, String clavePlana) {
        String query = "SELECT u.usu_id    AS id, " +
                       "       u.usu_nombre    AS nombre, " +
                       "       u.usu_apellido  AS apellido, " +
                       "       u.usu_login     AS login, " +
                       "       u.usu_correo    AS correo, " +
                       "       u.usu_pass, " +
                       "       u.usu_estado    AS estado, " +
                       "       p.perf_descripcion AS rol " +
                       "FROM usuario u " +
                       "JOIN perfil p ON u.perf_id = p.perf_id " +
                       "WHERE u.usu_login = ? AND u.usu_estado = 'Activo'";

        try (Connection connection = ConexionDB.obtenerConexion();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashDB = rs.getString("usu_pass");

                    if (hashDB == null || !hashDB.startsWith("$2")) {
                        System.err.println("ADVERTENCIA: La contraseña para el usuario '" + login + "' no es un hash BCrypt válido.");
                        return null;
                    }

                    if (BCrypt.checkpw(clavePlana, hashDB)) {
                        System.out.println("Login EXITOSO: " + login);
                        return Usuario.crearDesdeResultSet(rs);
                    } else {
                        System.out.println("Login FALLIDO (Contraseña incorrecta): " + login);
                    }
                } else {
                    System.out.println("Login FALLIDO (Usuario no encontrado o inactivo): " + login);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
        return null;
    }
}
