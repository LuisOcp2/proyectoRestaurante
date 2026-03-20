package com.mosqueteros.proyecto_restaurante.model;

public class Insumo {
    private long insId;
    private long sedeId;
    private long categoriaId;
    private long presentacionId;
    private String insNombre;
    private String categoriaNombre;
    private String presentacionAbreviatura;
    private double insStock;
    private String insEstado;

    public Insumo() {
    }

    public long getInsId() {
        return insId;
    }

    public void setInsId(long insId) {
        this.insId = insId;
    }

    public long getSedeId() {
        return sedeId;
    }

    public void setSedeId(long sedeId) {
        this.sedeId = sedeId;
    }

    public long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public long getPresentacionId() {
        return presentacionId;
    }

    public void setPresentacionId(long presentacionId) {
        this.presentacionId = presentacionId;
    }

    public String getInsNombre() {
        return insNombre;
    }

    public void setInsNombre(String insNombre) {
        this.insNombre = insNombre;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public String getPresentacionAbreviatura() {
        return presentacionAbreviatura;
    }

    public void setPresentacionAbreviatura(String presentacionAbreviatura) {
        this.presentacionAbreviatura = presentacionAbreviatura;
    }

    public double getInsStock() {
        return insStock;
    }

    public void setInsStock(double insStock) {
        this.insStock = insStock;
    }

    public String getInsEstado() {
        return insEstado;
    }

    public void setInsEstado(String insEstado) {
        this.insEstado = insEstado;
    }
}
