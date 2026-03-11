package com.mosqueteros.proyecto_restaurante.model;

/**
 * Modelo de datos para Área de Mesa
 */
public class AreaMesa {
    private Integer armid;
    private String armnombre;
    private String armdescripcion;

    // Constructores
    public AreaMesa() {
    }

    public AreaMesa(Integer armid, String armnombre) {
        this.armid = armid;
        this.armnombre = armnombre;
    }

    // Getters y Setters
    public Integer getArmid() {
        return armid;
    }

    public void setArmid(Integer armid) {
        this.armid = armid;
    }

    public String getArmnombre() {
        return armnombre;
    }

    public void setArmnombre(String armnombre) {
        this.armnombre = armnombre;
    }

    public String getArmdescripcion() {
        return armdescripcion;
    }

    public void setArmdescripcion(String armdescripcion) {
        this.armdescripcion = armdescripcion;
    }

    @Override
    public String toString() {
        return armnombre;
    }
}
