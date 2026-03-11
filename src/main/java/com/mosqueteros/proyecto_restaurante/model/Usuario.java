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

    public long obtenerId() { return id; }
    public void establecerId(long id) { this.id = id; }

    public String obtenerNombre() { return nombre; }
    public void establecerNombre(String nombre) { this.nombre = nombre; }

    public String obtenerApellido() { return apellido; }
    public void establecerApellido(String apellido) { this.apellido = apellido; }

    public String obtenerLogin() { return login; }
    public void establecerLogin(String login) { this.login = login; }

    public String obtenerCorreo() { return correo; }
    public void establecerCorreo(String correo) { this.correo = correo; }

    public String obtenerRol() { return rol; }
    public void establecerRol(String rol) { this.rol = rol; }

    public String obtenerEstado() { return estado; }
    public void establecerEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return String.format("Usuario{id=%d, login='%s', rol='%s', estado='%s'}", 
                           id, login, rol, estado);
    }
}
