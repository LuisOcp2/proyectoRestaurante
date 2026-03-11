package com.mosqueteros.proyecto_restaurante.model;

/**
 * Modelo para AreaMesa (Standardized)
 */
public class AreaMesa {
    private Long id;
    private Long sedeId;
    private String nombre;
    private String estado;

    public AreaMesa() {}

    public AreaMesa(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return nombre;
    }
}
