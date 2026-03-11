package com.mosqueteros.proyecto_restaurante.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Generador de hashes BCrypt reales.
 * Ejecutar UNA SOLA VEZ → copiar los UPDATE → pegarlos en phpMyAdmin.
 * Después de ejecutar, esta clase ya no se necesita.
 */
public class GeneradorHashes {

    public static void main(String[] args) {

        // Array con: { usu_id, usu_login, contraseña plana }
        String[][] usuarios = {
            {"2", "luis.admin",    "Admin2026*"},
            {"3", "carlos.mesero","Mesero2026*"},
            {"4", "maria.cajero", "Cajero2026*"},
            {"5", "pedro.cocina", "Cocina2026*"}
        };

        System.out.println("-- ==============================");
        System.out.println("-- UPDATEs con hashes BCrypt reales");
        System.out.println("-- Pegar en phpMyAdmin");
        System.out.println("-- ==============================\n");

        for (String[] u : usuarios) {
            // Genera hash real con salt aleatorio, cost factor 12
            String hash = BCrypt.hashpw(u[2], BCrypt.gensalt(12));

            System.out.println("-- Login: " + u[1] + " | Clave: " + u[2]);
            System.out.println("UPDATE `usuario` SET `usu_login` = '" + u[1] + "', "
                    + "`usu_pass` = '" + hash + "' "
                    + "WHERE `usu_id` = " + u[0] + ";");
            System.out.println();
        }
    }
}
