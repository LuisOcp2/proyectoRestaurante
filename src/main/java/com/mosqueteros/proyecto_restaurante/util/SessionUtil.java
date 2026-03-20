package com.mosqueteros.proyecto_restaurante.util;

import com.mosqueteros.proyecto_restaurante.model.Usuario;

/**
 * Utilidad estática para gestionar la sesión del usuario autenticado.
 * Guarda el objeto Usuario durante todo el ciclo de vida de la aplicación.
 */
public class SessionUtil {

    /** Usuario que tiene la sesión activa actualmente */
    private static Usuario usuarioActual;
    private static Long sedeActivaId;
    private static String sedeActivaNombre;

    /**
     * Guarda el usuario autenticado en la sesión.
     * Imprime en consola el login para confirmar la sesión.
     *
     * @param user Objeto {@link Usuario} autenticado
     */
    public static void saveUser(Usuario user) {
        usuarioActual = user;
        System.out.println("Sesión de Usuario: "
                + (usuarioActual != null ? usuarioActual.getUsulogin() : "null"));
    }

    /**
     * Retorna el usuario de la sesión activa.
     *
     * @return {@link Usuario} actual o null si no hay sesión
     */
    public static Usuario getUser() { return usuarioActual; }

    /**
     * Retorna la descripción del perfil/rol del usuario activo.
     * Si no hay sesión devuelve "Invitado".
     *
     * @return Rol como String: "Administrador", "Mesero", etc.
     */
    public static String getUserRole() {
        return usuarioActual != null
                ? usuarioActual.getPerfilDescripcion()
                : "Invitado";
    }

    /**
     * Retorna el ID numérico del usuario activo.
     * Si no hay sesión devuelve 0.
     *
     * @return ID del usuario o 0
     */
    public static long getUserId() {
        return usuarioActual != null ? usuarioActual.getUsuid() : 0;
    }

    public static void setSedeActiva(Long sedeId, String sedeNombre) {
        sedeActivaId = sedeId;
        sedeActivaNombre = sedeNombre;
    }

    public static Long getSedeActivaId() {
        return sedeActivaId;
    }

    public static String getSedeActivaNombre() {
        return sedeActivaNombre;
    }

    /**
     * Elimina la sesión activa (logout).
     * Pone usuarioActual a null.
     */
    public static void limpiarSesion() {
        usuarioActual = null;
        sedeActivaId = null;
        sedeActivaNombre = null;
    }
}
