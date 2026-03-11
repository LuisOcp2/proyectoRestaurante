package com.mosqueteros.proyecto_restaurante.model;

/**
 * Modelo de datos para Sede
 */
public class Sede {
    private Integer sedid;
    private String sednombre;
    private String seddireccion;
    private String sedtelefono;

    // Constructores
    public Sede() {
    }

    public Sede(Integer sedid, String sednombre) {
        this.sedid = sedid;
        this.sednombre = sednombre;
    }

    // Getters y Setters
    public Integer getSedid() {
        return sedid;
    }

    public void setSedid(Integer sedid) {
        this.sedid = sedid;
    }

    public String getSednombre() {
        return sednombre;
    }

    public void setSednombre(String sednombre) {
        this.sednombre = sednombre;
    }

    public String getSeddireccion() {
        return seddireccion;
    }

    public void setSeddireccion(String seddireccion) {
        this.seddireccion = seddireccion;
    }

    public String getSedtelefono() {
        return sedtelefono;
    }

    public void setSedtelefono(String sedtelefono) {
        this.sedtelefono = sedtelefono;
    }

    @Override
    public String toString() {
        return sednombre;
    }
}
