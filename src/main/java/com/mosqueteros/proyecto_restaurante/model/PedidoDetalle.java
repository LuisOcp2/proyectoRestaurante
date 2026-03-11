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

    // ── Getters y Setters ─────────────────────────────────────────────────────

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getPedidoId() { return pedidoId; }
    public void setPedidoId(long pedidoId) { this.pedidoId = pedidoId; }

    public long getPlatoId() { return platoId; }
    public void setPlatoId(long platoId) { this.platoId = platoId; }

    public String getPlatoNombre() { return platoNombre; }
    public void setPlatoNombre(String platoNombre) { this.platoNombre = platoNombre; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioMomento() { return precioMomento; }
    public void setPrecioMomento(BigDecimal precioMomento) { this.precioMomento = precioMomento; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public long getEstId() { return estId; }
    public void setEstId(long estId) { this.estId = estId; }

    public String getEstadoDesc() { return estadoDesc; }
    public void setEstadoDesc(String estadoDesc) { this.estadoDesc = estadoDesc; }

    @Override
    public String toString() {
        return String.format("PedidoDetalle{id=%d, plato='%s', cant=%d, estado='%s'}",
                             id, platoNombre, cantidad, estadoDesc);
    }
}
