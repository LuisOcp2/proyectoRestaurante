package com.mosqueteros.proyecto_restaurante.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Modelo de la entidad CategoriaInsumo.
 * Mapea la tabla `categoria_insumo` de la BD restaurante_2026.
 *
 * Usa JavaFX Properties para que TableView (PropertyValueFactory)
 * pueda enlazar las columnas correctamente.
 *
 * Campos BD:
 *   cat_ins_id          → PK autoincrement
 *   cat_ins_nombre      → nombre de la categoría
 *   cat_ins_descripcion → descripción detallada
 *   cat_ins_estado      → Activo | Inactivo
 */
public class CategoriaInsumo {

    /** Identificador único de la categoría (PK) */
    private final LongProperty idCategoriaInsumo = new SimpleLongProperty();

    /** Nombre de la categoría: Lácteos, Carnes, Verduras, etc. */
    private final StringProperty nombre = new SimpleStringProperty();

    /** Descripción detallada de la categoría */
    private final StringProperty descripcion = new SimpleStringProperty();

    /** Estado: Activo | Inactivo */
    private final StringProperty estado = new SimpleStringProperty();

    // ─────────────────────────────────────────────────────────────
    // Constructores
    // ─────────────────────────────────────────────────────────────

    /** Constructor vacío requerido por JavaFX y DAOs */
    public CategoriaInsumo() {}

    /**
     * Constructor completo para crear desde BD.
     *
     * @param id          ID de la categoría
     * @param nombre      Nombre de la categoría
     * @param descripcion Descripción detallada
     * @param estado      Estado: Activo | Inactivo
     */
    public CategoriaInsumo(long id, String nombre, String descripcion, String estado) {
        setIdCategoriaInsumo(id);
        setNombre(nombre);
        setDescripcion(descripcion);
        setEstado(estado);
    }

    // ─────────────────────────────────────────────────────────────
    // Properties (requeridas por PropertyValueFactory en FXML)
    // ─────────────────────────────────────────────────────────────

    /** @return Property del ID — requerida por TableColumn(PropertyValueFactory) */
    public LongProperty idCategoriaInsumoProperty() { return idCategoriaInsumo; }

    /** @return Property del nombre */
    public StringProperty nombreProperty() { return nombre; }

    /** @return Property de la descripción */
    public StringProperty descripcionProperty() { return descripcion; }

    /** @return Property del estado */
    public StringProperty estadoProperty() { return estado; }

    // ─────────────────────────────────────────────────────────────
    // Getters y Setters
    // ─────────────────────────────────────────────────────────────

    /** @return ID de la categoría */
    public long getIdCategoriaInsumo() { return idCategoriaInsumo.get(); }
    /** @param id ID de la categoría */
    public void setIdCategoriaInsumo(long id) { idCategoriaInsumo.set(id); }

    /** @return Nombre de la categoría */
    public String getNombre() { return nombre.get(); }
    /** @param nombre Nombre de la categoría */
    public void setNombre(String nombre) { this.nombre.set(nombre); }

    /** @return Descripción de la categoría */
    public String getDescripcion() { return descripcion.get(); }
    /** @param descripcion Descripción de la categoría */
    public void setDescripcion(String descripcion) { this.descripcion.set(descripcion); }

    /** @return Estado de la categoría */
    public String getEstado() { return estado.get(); }
    /** @param estado Estado: Activo | Inactivo */
    public void setEstado(String estado) { this.estado.set(estado); }

    /** Representación textual para ComboBoxes */
    @Override
    public String toString() { return nombre.get() != null ? nombre.get() : ""; }
}
