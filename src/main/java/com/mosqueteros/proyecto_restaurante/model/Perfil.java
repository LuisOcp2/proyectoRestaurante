package com.mosqueteros.proyecto_restaurante.model;

/**
 * Modelo de la entidad Perfil.
 * Mapea la tabla `perfil` de la BD restaurante_2026.
 *
 * Campos:
 *   perf_id          → PK autoincrement
 *   perf_descripcion → nombre del rol (Administrador, Mesero, Cajero, Cocinero)
 *   perf_estado      → Activo | Inactivo
 */
public class Perfil {

    /** Identificador único del perfil (PK) */
    private long perfid;

    /** Descripción del perfil: Administrador, Mesero, etc. */
    private String perfdescripcion;

    /** Estado del perfil: Activo o Inactivo */
    private String perfestado;

    // ─────────────────────────────────────────────────────────────
    // Constructores
    // ─────────────────────────────────────────────────────────────

    /** Constructor vacío requerido por DAOs y JavaFX */
    public Perfil() {}

    /**
     * Constructor completo para crear un Perfil desde BD.
     *
     * @param perfid          ID del perfil
     * @param perfdescripcion Descripción del perfil
     * @param perfestado      Estado del perfil
     */
    public Perfil(long perfid, String perfdescripcion, String perfestado) {
        this.perfid          = perfid;
        this.perfdescripcion = perfdescripcion;
        this.perfestado      = perfestado;
    }

    // ─────────────────────────────────────────────────────────────
    // Getters y Setters
    // ─────────────────────────────────────────────────────────────

    /** @return ID del perfil */
    public long getPerfid() { return perfid; }
    /** @param perfid ID del perfil */
    public void setPerfid(long perfid) { this.perfid = perfid; }

    /** Alias para compatibilidad con llamadas setPerfId (con I mayúscula) */
    public void setPerfId(long perfid) { this.perfid = perfid; }

    /** @return Descripción del perfil */
    public String getPerfdescripcion() { return perfdescripcion; }
    /** @param perfdescripcion Descripción del perfil */
    public void setPerfdescripcion(String perfdescripcion) { this.perfdescripcion = perfdescripcion; }

    /** Alias para compatibilidad con llamadas setPerfDescripcion */
    public void setPerfDescripcion(String perfdescripcion) { this.perfdescripcion = perfdescripcion; }

    /** @return Estado del perfil */
    public String getPerfestado() { return perfestado; }
    /** @param perfestado Estado del perfil */
    public void setPerfestado(String perfestado) { this.perfestado = perfestado; }

    /** Representación textual del perfil para ComboBoxes */
    @Override
    public String toString() { return perfdescripcion != null ? perfdescripcion : ""; }
}
