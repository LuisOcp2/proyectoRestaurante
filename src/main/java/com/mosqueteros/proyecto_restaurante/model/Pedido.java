package com.mosqueteros.proyecto_restaurante.model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Pedido {

    private long   id;
    private Date   fecha;
    private String observaciones;
    private long   mesaId;       // FK → mesa
    private int    mesaNumero;   // Número de mesa (JOIN)
    private long   usuarioId;    // FK → usuario (mesero)
    private String usuarioNombre;// Nombre del mesero (JOIN)
    private long   sedeId;       // FK → sede
    private String sedeNombre;   // Nombre de la sede (JOIN)
    private long   estId;        // FK → estado
    private String estadoDesc;   // Descripción del estado (JOIN)

    public Pedido() {}

    /** Construye un Pedido desde un ResultSet con alias estándar. */
    public static Pedido crearDesdeResultSet(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.id            = rs.getLong("ped_id");
        p.fecha         = rs.getDate("ped_fecha");
        p.observaciones = rs.getString("ped_observaciones");
        p.mesaId        = rs.getLong("mes_id");
        p.mesaNumero    = rs.getInt("mes_numero");
        p.usuarioId     = rs.getLong("usu_id");
        p.usuarioNombre = rs.getString("usu_nombre");
        p.sedeId        = rs.getLong("sede_id");
        p.sedeNombre    = rs.getString("sede_nombre");
        p.estId         = rs.getLong("est_id");
        p.estadoDesc    = rs.getString("est_descripcion");
        return p;
    }

    // ── Getters y Setters ─────────────────────────────────────────────────────

    public long   obtenerId()                         { return id; }
    public void   establecerId(long id)               { this.id = id; }

    public Date   obtenerFecha()                      { return fecha; }
    public void   establecerFecha(Date f)             { this.fecha = f; }

    public String obtenerObservaciones()              { return observaciones; }
    public void   establecerObservaciones(String o)   { this.observaciones = o; }

    public long   obtenerMesaId()                     { return mesaId; }
    public void   establecerMesaId(long m)            { this.mesaId = m; }

    public int    obtenerMesaNumero()                 { return mesaNumero; }
    public void   establecerMesaNumero(int m)         { this.mesaNumero = m; }

    public long   obtenerUsuarioId()                  { return usuarioId; }
    public void   establecerUsuarioId(long u)         { this.usuarioId = u; }

    public String obtenerUsuarioNombre()              { return usuarioNombre; }
    public void   establecerUsuarioNombre(String u)   { this.usuarioNombre = u; }

    public long   obtenerSedeId()                     { return sedeId; }
    public void   establecerSedeId(long s)            { this.sedeId = s; }

    public String obtenerSedeNombre()                 { return sedeNombre; }
    public void   establecerSedeNombre(String s)      { this.sedeNombre = s; }

    public long   obtenerEstId()                      { return estId; }
    public void   establecerEstId(long e)             { this.estId = e; }

    public String obtenerEstadoDesc()                 { return estadoDesc; }
    public void   establecerEstadoDesc(String e)      { this.estadoDesc = e; }

    @Override
    public String toString() {
        return String.format("Pedido{id=%d, mesa=%d, sede='%s', estado='%s'}",
                             id, mesaNumero, sedeNombre, estadoDesc);
    }
}
