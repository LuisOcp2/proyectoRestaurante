package com.mosqueteros.proyecto_restaurante.model;

/**
 * Modelo de datos para Estado (Standardized)
 */
public class Estado {
    private Integer id;
    private String nombre;

    public Estado() {}

    public Estado(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Integer obtenerId() { return id; }
    public void establecerId(Integer id) { this.id = id; }

    public String obtenerNombre() { return nombre; }
    public void establecerNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
