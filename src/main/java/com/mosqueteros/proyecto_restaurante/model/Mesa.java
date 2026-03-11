package com.mosqueteros.proyecto_restaurante.model;

/**
 * Modelo de datos para Mesa
 */
public class Mesa {
    private Integer mesid;
    private Integer mesnumero;
    private Integer mescapacidad;
    private AreaMesa areaMesa;
    private Sede sede;
    private Estado estado;

    // Constructores
    public Mesa() {
    }

    public Mesa(Integer mesid, Integer mesnumero, Integer mescapacidad) {
        this.mesid = mesid;
        this.mesnumero = mesnumero;
        this.mescapacidad = mescapacidad;
    }

    // Getters y Setters
    public Integer getMesid() {
        return mesid;
    }

    public void setMesid(Integer mesid) {
        this.mesid = mesid;
    }

    public Integer getMesnumero() {
        return mesnumero;
    }

    public void setMesnumero(Integer mesnumero) {
        this.mesnumero = mesnumero;
    }

    public Integer getMescapacidad() {
        return mescapacidad;
    }

    public void setMescapacidad(Integer mescapacidad) {
        this.mescapacidad = mescapacidad;
    }

    public AreaMesa getAreaMesa() {
        return areaMesa;
    }

    public void setAreaMesa(AreaMesa areaMesa) {
        this.areaMesa = areaMesa;
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Mesa{" +
                "mesid=" + mesid +
                ", mesnumero=" + mesnumero +
                ", mescapacidad=" + mescapacidad +
                ", areaMesa=" + (areaMesa != null ? areaMesa.getArmnombre() : "null") +
                ", sede=" + (sede != null ? sede.getSednombre() : "null") +
                ", estado=" + (estado != null ? estado.getEstnombre() : "null") +
                '}';
    }
}
