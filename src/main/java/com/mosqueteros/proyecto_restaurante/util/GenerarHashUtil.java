package com.mosqueteros.proyecto_restaurante.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad temporal para generar hashes BCrypt.
 * Ejecutar una vez para actualizar contraseñas en la BD.
 * ELIMINAR antes de producción.
 */
public class GenerarHashUtil {

    public static String hashear(String contrasenaPlana) {
        return BCrypt.hashpw(contrasenaPlana, BCrypt.gensalt(12));
    }

    public static void main(String[] args) {
        // ── Cambia aquí las contraseñas de tus usuarios de prueba ──
        String[] usuarios = { "admin", "mesero", "cajero", "cocinero" };
        String[] claves   = { "admin123", "mesero123", "cajero123", "cocinero123" };

        System.out.println("-- SQL para actualizar contraseñas con BCrypt --");
        System.out.println("-- Ejecuta estas sentencias en tu BD restaurante_2026 --\n");

        for (int i = 0; i < usuarios.length; i++) {
            String hash = hashear(claves[i]);
            System.out.printf(
                "UPDATE usuario SET usu_pass = '%s' WHERE usu_login = '%s';%n",
                hash, usuarios[i]
            );
        }
    }
}
