package com.mosqueteros.proyecto_restaurante.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Mesa {

    private long   id;
    private int    numero;
    private int    capacidad;
    private long   areaMesaId;    // FK → areamesa
    private String areaDesc;      // Descripción del área (JOIN)
    private long   sedeId;        // FK → sede
    private String sedeNombre;    // Nombre de la sede (JOIN)
    private long   estId;         // FK → estado
    private String estadoDesc;    // Descripción del estado (JOIN)

    public Mesa() {}

    public Mesa(long id, int numero, int capacidad,
                long areaMesaId, String areaDesc,
                long sedeId, String sedeNombre,
                long estId, String estadoDesc) {
        this.id         = id;
        this.numero     = numero;
        this.capacidad  = capacidad;
        this.areaMesaId = areaMesaId;
        this.areaDesc   = areaDesc;
        this.sedeId     = sedeId;
        this.sedeNombre = sedeNombre;
        this.estId      = estId;
        this.estadoDesc = estadoDesc;
    }

    /** Construye una Mesa desde un ResultSet con alias estándar. */
    public static Mesa crearDesdeResultSet(ResultSet rs) throws SQLException {
        return new Mesa(
            rs.getLong("mes_id"),
            rs.getInt("mes_numero"),
            rs.getInt("mes_capacidad"),
            rs.getLong("arem_id"),
            rs.getString("arem_descripcion"),
            rs.getLong("sede_id"),
            rs.getString("sede_nombre"),
            rs.getLong("est_id"),
            rs.getString("est_descripcion")
        );
    }

    // ── Getters y Setters ─────────────────────────────────────────────────────

    public long   obtenerId()                      { return id; }
    public void   establecerId(long id)            { this.id = id; }

    public int    obtenerNumero()                  { return numero; }
    public void   establecerNumero(int n)          { this.numero = n; }

    public int    obtenerCapacidad()               { return capacidad; }
    public void   establecerCapacidad(int c)       { this.capacidad = c; }

    public long   obtenerAreaMesaId()              { return areaMesaId; }
    public void   establecerAreaMesaId(long a)     { this.areaMesaId = a; }

    public String obtenerAreaDesc()                { return areaDesc; }
    public void   establecerAreaDesc(String a)     { this.areaDesc = a; }

    public long   obtenerSedeId()                  { return sedeId; }
    public void   establecerSedeId(long s)         { this.sedeId = s; }

    public String obtenerSedeNombre()              { return sedeNombre; }
    public void   establecerSedeNombre(String s)   { this.sedeNombre = s; }

    public long   obtenerEstId()                   { return estId; }
    public void   establecerEstId(long e)          { this.estId = e; }

    public String obtenerEstadoDesc()              { return estadoDesc; }
    public void   establecerEstadoDesc(String e)   { this.estadoDesc = e; }

    @Override
    public String toString() {
        return String.format("Mesa{id=%d, numero=%d, sede='%s', estado='%s'}",
                             id, numero, sedeNombre, estadoDesc);
    }
}
