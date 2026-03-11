package com.mosqueteros.proyecto_restaurante.model;

/**
 * Modelo para CategoriaPlato (Standardized)
 */
public class CategoriaPlato {
    private Long id;
    private String nombre;

    public CategoriaPlato() {}

    public CategoriaPlato(Long id, String nombre) {
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
