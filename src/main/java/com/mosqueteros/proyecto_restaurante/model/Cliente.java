package com.mosqueteros.proyecto_restaurante.model;

public class Cliente {
    private long cliId;
    private String cliTipoDocumento;
    private String cliDocumento;
    private String cliNombre;
    private String cliCorreo;
    private String cliTelefono;
    private String cliEstado;

    public Cliente() {
    }

    public Cliente(long cliId, String cliTipoDocumento, String cliDocumento, String cliNombre, String cliCorreo, String cliTelefono, String cliEstado) {
        this.cliId = cliId;
        this.cliTipoDocumento = cliTipoDocumento;
        this.cliDocumento = cliDocumento;
        this.cliNombre = cliNombre;
        this.cliCorreo = cliCorreo;
        this.cliTelefono = cliTelefono;
        this.cliEstado = cliEstado;
    }

    public long getCliId() {
        return cliId;
    }

    public void setCliId(long cliId) {
        this.cliId = cliId;
    }

    public String getCliDocumento() {
        return cliDocumento;
    }

    public void setCliDocumento(String cliDocumento) {
        this.cliDocumento = cliDocumento;
    }

    public String getCliTipoDocumento() {
        return cliTipoDocumento;
    }

    public void setCliTipoDocumento(String cliTipoDocumento) {
        this.cliTipoDocumento = cliTipoDocumento;
    }

    public String getCliNombre() {
        return cliNombre;
    }

    public void setCliNombre(String cliNombre) {
        this.cliNombre = cliNombre;
    }

    public String getCliCorreo() {
        return cliCorreo;
    }

    public void setCliCorreo(String cliCorreo) {
        this.cliCorreo = cliCorreo;
    }

    public String getCliTelefono() {
        return cliTelefono;
    }

    public void setCliTelefono(String cliTelefono) {
        this.cliTelefono = cliTelefono;
    }

    public String getCliEstado() {
        return cliEstado;
    }

    public void setCliEstado(String cliEstado) {
        this.cliEstado = cliEstado;
    }
}
