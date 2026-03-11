package com.mosqueteros.proyecto_restaurante.model;

/**
 * Modelo de datos para Estado
 */
public class Estado {
    private Integer estid;
    private String estnombre;
    private String estdescripcion;

    // Constructores
    public Estado() {
    }

    public Estado(Integer estid, String estnombre) {
        this.estid = estid;
        this.estnombre = estnombre;
    }

    // Getters y Setters
    public Integer getEstid() {
        return estid;
    }

    public void setEstid(Integer estid) {
        this.estid = estid;
    }

    public String getEstnombre() {
        return estnombre;
    }

    public void setEstnombre(String estnombre) {
        this.estnombre = estnombre;
    }

    public String getEstdescripcion() {
        return estdescripcion;
    }

    public void setEstdescripcion(String estdescripcion) {
        this.estdescripcion = estdescripcion;
    }

    @Override
    public String toString() {
        return estnombre;
    }
}
