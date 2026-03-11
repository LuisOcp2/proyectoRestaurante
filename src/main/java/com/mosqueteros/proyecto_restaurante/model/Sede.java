package com.mosqueteros.proyecto_restaurante.model;

/**
 * Modelo para Sede (Standardized)
 */
public class Sede {
    private Long id;
    private String nombre;

    public Sede() {}

    public Sede(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
