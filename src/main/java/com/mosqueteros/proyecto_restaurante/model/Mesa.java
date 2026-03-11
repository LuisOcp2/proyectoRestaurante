package com.mosqueteros.proyecto_restaurante.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Modelo para la tabla `mesa`.
 * El estado es un ENUM directo en la BD: Disponible | Ocupada | Reservada | Inactiva
 */
public class Mesa {

    private long   id;
    private String numero;      // varchar(10) en BD
    private int    capacidad;
    private long   areaId;      // FK → area_mesa (nullable)
    private String areaNombre;  // Nombre del área (JOIN)
    private long   sedeId;      // FK → sede
    private String sedeNombre;  // Nombre de la sede (JOIN)
    private String estado;      // ENUM: Disponible | Ocupada | Reservada | Inactiva
    private int    xPos;
    private int    yPos;

    public Mesa() {}

    /** Construye una Mesa desde un ResultSet. Los alias vienen de MesaDAO.SQL_SELECT. */
    public static Mesa crearDesdeResultSet(ResultSet rs) throws SQLException {
        Mesa m = new Mesa();
        m.id        = rs.getLong("mesa_id");
        m.sedeId    = rs.getLong("sede_id");
        m.areaId    = rs.getLong("area_id");
        m.numero    = rs.getString("mesa_numero");
        m.capacidad = rs.getInt("capacidad");
        m.xPos      = rs.getInt("x_pos");
        m.yPos      = rs.getInt("y_pos");
        m.estado    = rs.getString("estado");
        // columnas de JOINs (pueden ser null si area es null)
        m.areaNombre = rs.getString("area_nombre");
        m.sedeNombre = rs.getString("sede_nombre");
        return m;
    }

    // ── Getters y Setters ─────────────────────────────────────────────────────

    public long   getId()                       { return id; }
    public void   setId(long id)               { this.id = id; }

    public String getNumero()                   { return numero; }
    public void   setNumero(String numero)      { this.numero = numero; }

    public int    getCapacidad()                { return capacidad; }
    public void   setCapacidad(int c)           { this.capacidad = c; }

    public long   getAreaId()                   { return areaId; }
    public void   setAreaId(long areaId)        { this.areaId = areaId; }

    public String getAreaNombre()               { return areaNombre; }
    public void   setAreaNombre(String a)       { this.areaNombre = a; }

    public long   getSedeId()                   { return sedeId; }
    public void   setSedeId(long sedeId)        { this.sedeId = sedeId; }

    public String getSedeNombre()               { return sedeNombre; }
    public void   setSedeNombre(String s)       { this.sedeNombre = s; }

    public String getEstado()                   { return estado; }
    public void   setEstado(String estado)      { this.estado = estado; }

    public int    getXPos()                     { return xPos; }
    public void   setXPos(int x)               { this.xPos = x; }

    public int    getYPos()                     { return yPos; }
    public void   setYPos(int y)               { this.yPos = y; }

    @Override
    public String toString() {
        return String.format("Mesa{id=%d, numero='%s', sede='%s', estado='%s'}",
                             id, numero, sedeNombre, estado);
    }
}
