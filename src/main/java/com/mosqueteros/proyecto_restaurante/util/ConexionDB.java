package com.mosqueteros.proyecto_restaurante.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
   
    private static String nombreBaseDatos = "restaurante_2026";  
    private static final String servidor = "localhost";
    private static final String puerto = "3306";
    private static final String usuarioDB = "root";
    private static final String claveDB = "";  
    
    private static Connection conexionActual = null;
    private static final String urlBase = "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC";

    public static void cambiarBaseDatos(String nuevoNombre) {
        nombreBaseDatos = nuevoNombre;
        System.out.println("Base de datos cambiada a: " + nombreBaseDatos);
        // Cierra conexión anterior
        try { 
            if (conexionActual != null) conexionActual.close(); 
        } catch (Exception e) {}
        conexionActual = null;
    }

    /**
     * Obtiene conexión a la base de datos configurada
     */
    public static Connection obtenerConexion() throws SQLException {
        if (conexionActual == null || conexionActual.isClosed()) {
            String urlCompleta = String.format(urlBase, servidor, puerto, nombreBaseDatos);
            conexionActual = DriverManager.getConnection(urlCompleta, usuarioDB, claveDB);
            System.out.println("Conectado a: " + urlCompleta.split("\\?")[0]);
        }
        return conexionActual;
    }

    public static void cerrarConexion() {
        try {
            if (conexionActual != null && !conexionActual.isClosed()) {
                conexionActual.close();
                System.out.println("Conexión cerrada");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
