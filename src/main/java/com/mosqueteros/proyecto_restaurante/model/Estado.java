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

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
