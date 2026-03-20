package com.mosqueteros.proyecto_restaurante.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Configuracion {

    private final LongProperty cfgId = new SimpleLongProperty();
    private final StringProperty cfgClave = new SimpleStringProperty();
    private final StringProperty cfgValor = new SimpleStringProperty();

    public Configuracion() {
    }

    public Configuracion(long cfgId, String cfgClave, String cfgValor) {
        setCfgId(cfgId);
        setCfgClave(cfgClave);
        setCfgValor(cfgValor);
    }

    public long getCfgId() {
        return cfgId.get();
    }

    public void setCfgId(long cfgId) {
        this.cfgId.set(cfgId);
    }

    public LongProperty cfgIdProperty() {
        return cfgId;
    }

    public String getCfgClave() {
        return cfgClave.get();
    }

    public void setCfgClave(String cfgClave) {
        this.cfgClave.set(cfgClave);
    }

    public StringProperty cfgClaveProperty() {
        return cfgClave;
    }

    public String getCfgValor() {
        return cfgValor.get();
    }

    public void setCfgValor(String cfgValor) {
        this.cfgValor.set(cfgValor);
    }

    public StringProperty cfgValorProperty() {
        return cfgValor;
    }

    @Override
    public String toString() {
        return getCfgClave();
    }
}
