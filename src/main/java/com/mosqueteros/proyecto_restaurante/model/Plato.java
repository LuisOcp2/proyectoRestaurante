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

    public long       getId()                       { return id; }
    public void       setId(long id)             { this.id = id; }

    public String     getNombre()                   { return nombre; }
    public void       setNombre(String n)        { this.nombre = n; }

    public String     getDescripcion()              { return descripcion; }
    public void       setDescripcion(String d)   { this.descripcion = d; }

    public String     getCodigo()                   { return codigo; }
    public void       setCodigo(String c)        { this.codigo = c; }

    public BigDecimal getPrecio()                   { return precio; }
    public void       setPrecio(BigDecimal p)    { this.precio = p; }

    public BigDecimal getCosto()                 { return costo; }
    public void       setCosto(BigDecimal c)  { this.costo = c; }

    public long       getCategoriaPlatoId()         { return categoriaPlatoId; }
    public void       setCategoriaPlatoId(long c){ this.categoriaPlatoId = c; }

    public String     getCategoriaDesc()            { return categoriaDesc; }
    public void       setCategoriaDesc(String c) { this.categoriaDesc = c; }

    public long       getEstId()                    { return estId; }
    public void       setEstId(long e)           { this.estId = e; }

    public String     getEstadoDesc()               { return estadoDesc; }
    public void       setEstadoDesc(String e)    { this.estadoDesc = e; }

    @Override
    public String toString() {
        return String.format("Plato{id=%d, nombre='%s', precio=%s, estado='%s'}",
                             id, nombre, precio, estadoDesc);
    }
}