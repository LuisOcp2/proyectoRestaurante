package com.mosqueteros.proyecto_restaurante.model;

/**
 * Modelo para AreaMesa (Standardized)
 */
public class AreaMesa {
    private Long id;
    private String nombre;

    public AreaMesa() {}

    public AreaMesa(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long obtenerId() { return id; }
    public void establecerId(Long id) { this.id = id; }

    public String obtenerNombre() { return nombre; }
    public void establecerNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
