package com.mosqueteros.proyecto_restaurante.model;

/**
 * Modelo de la entidad Usuario.
 * Mapea la tabla `usuario` de la BD restaurante_2026.
 *
 * Campos:
 *   usuid        → PK autoincrement
 *   usunombre    → nombre del usuario
 *   usuapellido  → apellido del usuario
 *   usudireccion → dirección del usuario
 *   usutelefono  → teléfono de contacto
 *   usucorreo    → correo electrónico (único)
 *   perfid       → FK a tabla perfil (rol: Admin, Mesero, etc.)
 *   usulogin     → login de acceso (único)
 *   usupass      → contraseña hasheada (bcrypt)
 *   usuestado    → Activo | Inactivo
 */
public class Usuario {

    /** Identificador único del usuario (PK) */
    private long usuid;

    /** Nombre del usuario */
    private String usunombre;

    /** Apellido del usuario */
    private String usuapellido;

    /** Dirección del usuario */
    private String usudireccion;

    /** Teléfono de contacto */
    private String usutelefono;

    /** Correo electrónico (único en BD) */
    private String usucorreo;

    /** FK al perfil/rol del usuario (puede ser null) */
    private Long perfid;

    /** Descripción del perfil (viene del JOIN con tabla perfil) */
    private String perfilDescripcion;

    /** Login de acceso al sistema (único en BD) */
    private String usulogin;

    /** Contraseña hasheada con bcrypt */
    private String usupass;

    /** Estado del usuario: Activo o Inactivo */
    private String usuestado;

    // ─────────────────────────────────────────────────────────────
    // Constructor vacío requerido por JavaFX y DAOs
    // ─────────────────────────────────────────────────────────────

    /** Constructor vacío */
    public Usuario() {}

    /**
     * Constructor completo para crear un usuario desde BD.
     *
     * @param usuid           ID del usuario
     * @param usunombre       Nombre
     * @param usuapellido     Apellido
     * @param usudireccion    Dirección
     * @param usutelefono     Teléfono
     * @param usucorreo       Correo
     * @param perfid          FK perfil
     * @param perfilDescripcion Descripción del perfil
     * @param usulogin        Login
     * @param usupass         Password hash
     * @param usuestado       Estado
     */
    public Usuario(long usuid, String usunombre, String usuapellido,
                   String usudireccion, String usutelefono, String usucorreo,
                   Long perfid, String perfilDescripcion,
                   String usulogin, String usupass, String usuestado) {
        this.usuid            = usuid;
        this.usunombre        = usunombre;
        this.usuapellido      = usuapellido;
        this.usudireccion     = usudireccion;
        this.usutelefono      = usutelefono;
        this.usucorreo        = usucorreo;
        this.perfid           = perfid;
        this.perfilDescripcion = perfilDescripcion;
        this.usulogin         = usulogin;
        this.usupass          = usupass;
        this.usuestado        = usuestado;
    }

    // ─────────────────────────────────────────────────────────────
    // Getters y Setters
    // ─────────────────────────────────────────────────────────────

    /** @return ID del usuario */
    public long getUsuid() { return usuid; }
    /** @param usuid ID del usuario */
    public void setUsuid(long usuid) { this.usuid = usuid; }

    /** @return Nombre del usuario */
    public String getUsunombre() { return usunombre; }
    /** @param usunombre Nombre del usuario */
    public void setUsunombre(String usunombre) { this.usunombre = usunombre; }

    /** @return Apellido del usuario */
    public String getUsuapellido() { return usuapellido; }
    /** @param usuapellido Apellido del usuario */
    public void setUsuapellido(String usuapellido) { this.usuapellido = usuapellido; }

    /** @return Dirección del usuario */
    public String getUsudireccion() { return usudireccion; }
    /** @param usudireccion Dirección del usuario */
    public void setUsudireccion(String usudireccion) { this.usudireccion = usudireccion; }

    /** @return Teléfono del usuario */
    public String getUsutelefono() { return usutelefono; }
    /** @param usutelefono Teléfono del usuario */
    public void setUsutelefono(String usutelefono) { this.usutelefono = usutelefono; }

    /** @return Correo del usuario */
    public String getUsucorreo() { return usucorreo; }
    /** @param usucorreo Correo del usuario */
    public void setUsucorreo(String usucorreo) { this.usucorreo = usucorreo; }

    /** @return FK perfil (puede ser null) */
    public Long getPerfid() { return perfid; }
    /** @param perfid FK perfil */
    public void setPerfid(Long perfid) { this.perfid = perfid; }

    /** @return Descripción legible del perfil */
    public String getPerfilDescripcion() { return perfilDescripcion; }
    /** @param perfilDescripcion Descripción del perfil */
    public void setPerfilDescripcion(String perfilDescripcion) { this.perfilDescripcion = perfilDescripcion; }

    /** @return Login del usuario */
    public String getUsulogin() { return usulogin; }
    /** @param usulogin Login del usuario */
    public void setUsulogin(String usulogin) { this.usulogin = usulogin; }

    /** @return Password hash del usuario */
    public String getUsupass() { return usupass; }
    /** @param usupass Password hash del usuario */
    public void setUsupass(String usupass) { this.usupass = usupass; }

    /** @return Estado del usuario: Activo o Inactivo */
    public String getUsuestado() { return usuestado; }
    /** @param usuestado Estado del usuario */
    public void setUsuestado(String usuestado) { this.usuestado = usuestado; }

    /**
     * Retorna nombre completo para mostrar en tabla.
     * @return "Nombre Apellido"
     */
    @Override
    public String toString() {
        return usunombre + " " + usuapellido;
    }
}
