package com.mosqueteros.proyecto_restaurante.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Usuario {
    private long id;
    private String nombre;
    private String apellido;
    private String login;
    private String correo;
    private String rol;     // Rol: Administrador, Mesero, etc.
    private String estado;   // Activo/Inactivo

    public Usuario() {}

    public Usuario(long id, String nombre, String apellido, String login, 
                   String correo, String rol, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.login = login;
        this.correo = correo;
        this.rol = rol;
        this.estado = estado;
    }

    public static Usuario crearDesdeResultSet(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getString("login"),
            rs.getString("correo"),
            rs.getString("rol"),
            rs.getString("estado")
        );
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return String.format("Usuario{id=%d, login='%s', rol='%s', estado='%s'}", 
                           id, login, rol, estado);
    }
}
