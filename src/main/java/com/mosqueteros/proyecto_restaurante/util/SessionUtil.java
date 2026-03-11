package com.mosqueteros.proyecto_restaurante.util;

import com.mosqueteros.proyecto_restaurante.model.Usuario;

public class SessionUtil {
    private static Usuario usuarioActual;

    public static void guardarUsuario(Usuario usuario) { 
        usuarioActual = usuario; 
        System.out.println("Sesión de Usuario: " + usuarioActual.obtenerLogin());
    }
    
    public static Usuario obtenerUsuario() { return usuarioActual; }
    
    public static String obtenerRol() { 
        return usuarioActual != null ? usuarioActual.obtenerRol() : "Invitado"; 
    }
    
    public static long obtenerIdUsuario() { 
        return usuarioActual != null ? usuarioActual.obtenerId() : 0; 
    }
    
    public static void limpiarSesion() { usuarioActual = null; }
}

