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

    // ── Getters y Setters ─────────────────────────────────────────────────────

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public long getMesaId() { return mesaId; }
    public void setMesaId(long mesaId) { this.mesaId = mesaId; }

    public int getMesaNumero() { return mesaNumero; }
    public void setMesaNumero(int mesaNumero) { this.mesaNumero = mesaNumero; }

    public long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(long usuarioId) { this.usuarioId = usuarioId; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public long getSedeId() { return sedeId; }
    public void setSedeId(long sedeId) { this.sedeId = sedeId; }

    public String getSedeNombre() { return sedeNombre; }
    public void setSedeNombre(String sedeNombre) { this.sedeNombre = sedeNombre; }

    public long getEstId() { return estId; }
    public void setEstId(long estId) { this.estId = estId; }

    public String getEstadoDesc() { return estadoDesc; }
    public void setEstadoDesc(String estadoDesc) { this.estadoDesc = estadoDesc; }

    @Override
    public String toString() {
        return String.format("Pedido{id=%d, mesa=%d, sede='%s', estado='%s'}",
                             id, mesaNumero, sedeNombre, estadoDesc);
    }
}
