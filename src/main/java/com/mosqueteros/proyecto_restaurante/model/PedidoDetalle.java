package com.mosqueteros.proyecto_restaurante.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Modelo para la tabla `pedidodetalle` (comanda / línea del pedido).
 * Estado posible de línea: En Preparación, Servido, Cancelado.
 */
public class PedidoDetalle {

    private long       id;
    private long       pedidoId;       // FK → pedido
    private long       platoId;        // FK → plato
    private String     platoNombre;    // Nombre del plato (JOIN)
    private int        cantidad;
    private BigDecimal precioMomento;  // Precio al momento de agregar la línea
    private String     observaciones;  // Observaciones para cocina
    private long       estId;          // FK → estado de la línea
    private String     estadoDesc;     // Descripción del estado (JOIN)

    public PedidoDetalle() {}

    /** Construye un PedidoDetalle desde un ResultSet con alias estándar. */
    public static PedidoDetalle crearDesdeResultSet(ResultSet rs) throws SQLException {
        PedidoDetalle d = new PedidoDetalle();
        d.id            = rs.getLong("pedd_id");
        d.pedidoId      = rs.getLong("ped_id");
        d.platoId       = rs.getLong("pla_id");
        d.platoNombre   = rs.getString("pla_nombre");
        d.cantidad      = rs.getInt("pedd_cantidad");
        d.precioMomento = rs.getBigDecimal("pedd_precio_momento");
        d.observaciones = rs.getString("pedd_observaciones");
        d.estId         = rs.getLong("est_id");
        d.estadoDesc    = rs.getString("est_descripcion");
        return d;
    }

    // ── Getters y Setters ─────────────────────────────────────────────────────

    public long       obtenerId()                         { return id; }
    public void       establecerId(long id)               { this.id = id; }

    public long       obtenerPedidoId()                   { return pedidoId; }
    public void       establecerPedidoId(long p)          { this.pedidoId = p; }

    public long       obtenerPlatoId()                    { return platoId; }
    public void       establecerPlatoId(long p)           { this.platoId = p; }

    public String     obtenerPlatoNombre()                { return platoNombre; }
    public void       establecerPlatoNombre(String n)     { this.platoNombre = n; }

    public int        obtenerCantidad()                   { return cantidad; }
    public void       establecerCantidad(int c)           { this.cantidad = c; }

    public BigDecimal obtenerPrecioMomento()              { return precioMomento; }
    public void       establecerPrecioMomento(BigDecimal p){ this.precioMomento = p; }

    public String     obtenerObservaciones()              { return observaciones; }
    public void       establecerObservaciones(String o)   { this.observaciones = o; }

    public long       obtenerEstId()                      { return estId; }
    public void       establecerEstId(long e)             { this.estId = e; }

    public String     obtenerEstadoDesc()                 { return estadoDesc; }
    public void       establecerEstadoDesc(String e)      { this.estadoDesc = e; }

    @Override
    public String toString() {
        return String.format("PedidoDetalle{id=%d, plato='%s', cant=%d, estado='%s'}",
                             id, platoNombre, cantidad, estadoDesc);
    }
}
