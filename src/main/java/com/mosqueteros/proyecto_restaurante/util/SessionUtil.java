package com.mosqueteros.proyecto_restaurante.util;

import com.mosqueteros.proyecto_restaurante.model.Usuario;

public class SessionUtil {
    private static Usuario usuarioActual;

    public static void saveUser(Usuario user) { 
        usuarioActual = user; 
        System.out.println("Sesión de Usuario: " + (usuarioActual != null ? usuarioActual.getLogin() : "null"));
    }
    
    public static Usuario getUser() { return usuarioActual; }
    
    public static String getUserRole() { 
        return usuarioActual != null ? usuarioActual.getRol() : "Invitado"; 
    }
    
    public static long getUserId() { 
        return usuarioActual != null ? usuarioActual.getId() : 0; 
    }
    
    public static void limpiarSesion() { usuarioActual = null; }
}

