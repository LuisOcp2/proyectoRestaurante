package com.mosqueteros.proyecto_restaurante.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Plato {

    private long       id;
    private String     nombre;
    private String     descripcion;
    private String     codigo;       // pla_codigo (opcional)
    private BigDecimal precio;
    private BigDecimal costo;        // pla_costo  (opcional)
    private long       categoriaPlatoId;  // FK → categoriaplato
    private String     categoriaDesc;     // Descripción de la categoría (JOIN)
    private long       estId;             // FK → estado
    private String     estadoDesc;        // Descripción del estado (JOIN)

    public Plato() {}

    public Plato(long id, String nombre, String descripcion, String codigo,
                 BigDecimal precio, BigDecimal costo,
                 long categoriaPlatoId, String categoriaDesc,
                 long estId, String estadoDesc) {
        this.id               = id;
        this.nombre           = nombre;
        this.descripcion      = descripcion;
        this.codigo           = codigo;
        this.precio           = precio;
        this.costo            = costo;
        this.categoriaPlatoId = categoriaPlatoId;
        this.categoriaDesc    = categoriaDesc;
        this.estId            = estId;
        this.estadoDesc       = estadoDesc;
    }

    public static Plato crearDesdeResultSet(ResultSet rs) throws SQLException {
        return new Plato(
            rs.getLong("pla_id"),
            rs.getString("pla_descripcion"),
            null, // No hay campo nombre separado en SQL script 2026, usamos descripcion
            rs.getString("pla_codigo"),
            rs.getBigDecimal("pla_precio"),
            rs.getBigDecimal("pla_costo"),
            rs.getLong("cat_id"),
            rs.getString("cat_nombre"),
            rs.getLong("est_id"),
            rs.getString("est_descripcion")
        );
    }

    // ── Getters y Setters ─────────────────────────────────────────────────────

    public long       obtenerId()                       { return id; }
    public void       establecerId(long id)             { this.id = id; }

    public String     obtenerNombre()                   { return nombre; }
    public void       establecerNombre(String n)        { this.nombre = n; }

    public String     obtenerDescripcion()              { return descripcion; }
    public void       establecerDescripcion(String d)   { this.descripcion = d; }

    public String     obtenerCodigo()                   { return codigo; }
    public void       establecerCodigo(String c)        { this.codigo = c; }

    public BigDecimal obtenerPrecio()                   { return precio; }
    public void       establecerPrecio(BigDecimal p)    { this.precio = p; }

    public BigDecimal obtenerCosto()                 { return costo; }
    public void       establecerCosto(BigDecimal c)  { this.costo = c; }

    public long       obtenerCategoriaPlatoId()         { return categoriaPlatoId; }
    public void       establecerCategoriaPlatoId(long c){ this.categoriaPlatoId = c; }

    public String     obtenerCategoriaDesc()            { return categoriaDesc; }
    public void       establecerCategoriaDesc(String c) { this.categoriaDesc = c; }

    public long       obtenerEstId()                    { return estId; }
    public void       establecerEstId(long e)           { this.estId = e; }

    public String     obtenerEstadoDesc()               { return estadoDesc; }
    public void       establecerEstadoDesc(String e)    { this.estadoDesc = e; }

    @Override
    public String toString() {
        return String.format("Plato{id=%d, nombre='%s', precio=%s, estado='%s'}",
                             id, nombre, precio, estadoDesc);
    }
}