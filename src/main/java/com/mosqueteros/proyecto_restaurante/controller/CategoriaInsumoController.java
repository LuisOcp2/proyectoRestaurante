package com.mosqueteros.proyecto_restaurante.controller;

import com.mosqueteros.proyecto_restaurante.dao.CategoriaInsumoDAO;
import com.mosqueteros.proyecto_restaurante.model.CategoriaInsumo;
import com.mosqueteros.proyecto_restaurante.util.Alertas;
import com.mosqueteros.proyecto_restaurante.util.FloatingFieldHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import io.github.palexdev.materialfx.controls.*;

/**
 * Controlador del módulo Categorías de Insumo.
 * Gestiona el CRUD completo: listar, buscar, guardar (crear/editar) y eliminar.
 *
 * Nota: el binding de columnas se hace con lambda en initialize() porque
 * PropertyValueFactory en FXML falla en proyectos JavaFX 21 con JPMS.
 *
 * Vinculado a: VistaCategoriaInsumo.fxml
 * Modelo:      CategoriaInsumo.java
 * DAO:         CategoriaInsumoDAO.java
 */
public class CategoriaInsumoController {

    // ── Barra de filtros ────────────────────────────────────────────
    @FXML private MFXTextField        txtBuscarCategoria;
    @FXML private MFXComboBox<String> cmbFiltroEstado;
    @FXML private MFXButton           btnBuscarCategoria;
    @FXML private MFXButton           btnLimpiarFiltroCategoria;

    // ── Tabla ──────────────────────────────────────────────────────
    @FXML private TableView<CategoriaInsumo>         tblListaCategorias;
    @FXML private TableColumn<CategoriaInsumo, Long>   colId;
    @FXML private TableColumn<CategoriaInsumo, String> colNombre;
    @FXML private TableColumn<CategoriaInsumo, String> colDescripcion;
    @FXML private TableColumn<CategoriaInsumo, String> colEstado;
    @FXML private Label lblConteoCategorias;
    @FXML private VBox  boxPlaceholder;

    // ── Formulario ──────────────────────────────────────────────────
    @FXML private MFXTextField        txtCatNombre;
    @FXML private StackPane           boxCatNombreField;
    @FXML private Label               lblCatNombreError;
    @FXML private MFXTextField        txtCatDescripcion;
    @FXML private MFXComboBox<String> cmbCatEstado;
    @FXML private StackPane           boxCatEstadoField;
    @FXML private Label            lblMensajeCategoria;

    // ── Botones del formulario ─────────────────────────────────────────
    @FXML private MFXButton btnNuevoCategoria;
    @FXML private MFXButton btnNuevoCategoriaForm;
    @FXML private MFXButton btnGuardarCategoria;
    @FXML private MFXButton btnEliminarCategoriaForm;

    /** Categoría seleccionada actualmente para edición (null = modo nuevo) */
    private CategoriaInsumo categoriaSeleccionada = null;

    /** Lista observable enlazada a la tabla */
    private final ObservableList<CategoriaInsumo> listaCategorias =
            FXCollections.observableArrayList();

    // ─────────────────────────────────────────────────────────────
    // INICIALIZACIÓN
    // ─────────────────────────────────────────────────────────────

    /**
     * JavaFX llama este método automáticamente al cargar el FXML.
     * Configura columnas, ComboBoxes, selección de tabla y carga datos iniciales.
     */
    @FXML
    public void initialize() {
        configurarColumnas();
        configurarComboBoxes();
        configurarSeleccionTabla();
        configurarFloatingFields();
        cargarCategorias();
    }

    /**
     * Enlaza cada TableColumn con la Property del modelo usando lambdas.
     *
     * RAZÓN: En JavaFX 21 con módulos JPMS, usar PropertyValueFactory dentro del
     * FXML lanza "PropertyValueFactory is not a valid type" porque el FXMLLoader
     * no puede resolver la clase desde el módulo. La solución definitiva es
     * configurar el binding aquí en Java con setCellValueFactory + lambda.
     */
    private void configurarColumnas() {
        // Enlaza la columna ID con la property idCategoriaInsumo del modelo
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleLongProperty(
                        data.getValue().getIdCategoriaInsumo()).asObject());

        // Enlaza la columna Nombre con la property nombre
        colNombre.setCellValueFactory(data ->
                data.getValue().nombreProperty());

        // Enlaza la columna Descripción con la property descripcion
        colDescripcion.setCellValueFactory(data ->
                data.getValue().descripcionProperty());

        // Enlaza la columna Estado con la property estado
        colEstado.setCellValueFactory(data ->
                data.getValue().estadoProperty());

        // Asigna la lista observable a la tabla y activa el auto-resize de columnas
        tblListaCategorias.setItems(listaCategorias);
        tblListaCategorias.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Carga las opciones fijas en los ComboBoxes de filtro y formulario.
     */
    private void configurarComboBoxes() {
        cmbFiltroEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbCatEstado   .setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbCatEstado   .setValue("Activo"); // valor por defecto al crear
    }

    /**
     * Al hacer clic en una fila de la tabla, carga los datos en el formulario
     * para editar y habilita el botón Eliminar.
     */
    private void configurarSeleccionTabla() {
        tblListaCategorias.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, seleccionada) -> {
                    if (seleccionada != null) cargarEnFormulario(seleccionada);
                });
    }

    // ─────────────────────────────────────────────────────────────
    // ACCIONES FXML
    // ─────────────────────────────────────────────────────────────

    /**
     * Ejecuta búsqueda con los filtros activos (texto libre + estado).
     * Llamado por el botón "Buscar" del FXML.
     */
    @FXML
    private void buscarCategoria() {
        try {
            String texto  = txtBuscarCategoria.getText();
            String estado = cmbFiltroEstado.getValue();
            listaCategorias.setAll(CategoriaInsumoDAO.buscarConFiltros(texto, estado));
            actualizarConteo();
            actualizarPlaceholder();
        } catch (Exception e) {
            Alertas.error("Error al buscar", e.getMessage());
        }
    }

    /**
     * Limpia los filtros y recarga todas las categorías.
     * Llamado por el botón "Limpiar" del FXML.
     */
    @FXML
    private void limpiarFiltros() {
        txtBuscarCategoria.clear();
        cmbFiltroEstado.setValue(null);
        cargarCategorias();
    }

    /**
     * Limpia el formulario y deja listo para registrar una nueva categoría.
     * Llamado por los botones "Nueva Categoría" del FXML.
     */
    @FXML
    private void prepararNuevoCategoria() {
        categoriaSeleccionada = null;
        txtCatNombre.clear();
        txtCatDescripcion.clear();
        cmbCatEstado.setValue("Activo");
        limpiarErroresCampos();
        ocultarMensaje();
        btnEliminarCategoriaForm.setDisable(true);
        tblListaCategorias.getSelectionModel().clearSelection();
        txtCatNombre.requestFocus();
    }

    /**
     * Guarda la categoría: inserta si es nueva, actualiza si hay una seleccionada.
     * Llamado por el botón "Guardar Categoría" del FXML.
     */
    @FXML
    private void guardarCategoria() {
        limpiarErroresCampos();
        if (txtCatNombre.getText().isBlank()) {
            mostrarErrorNombre("El nombre es obligatorio.");
            mostrarMensaje("⚠️ El nombre es obligatorio.", "form-mensaje-error");
            return;
        }
        if (cmbCatEstado.getValue() == null || cmbCatEstado.getValue().isBlank()) {
            marcarInvalido(boxCatEstadoField, true);
            mostrarMensaje("⚠️ Selecciona un estado.", "form-mensaje-error");
            return;
        }
        CategoriaInsumo c = construirDesdeFormulario();
        try {
            boolean ok;
            if (categoriaSeleccionada == null) {
                ok = CategoriaInsumoDAO.insertar(c);
                if (ok) mostrarMensaje("✅ Categoría creada correctamente.", "form-mensaje-ok");
            } else {
                c.setIdCategoriaInsumo(categoriaSeleccionada.getIdCategoriaInsumo());
                ok = CategoriaInsumoDAO.actualizar(c);
                if (ok) mostrarMensaje("✅ Categoría actualizada correctamente.", "form-mensaje-ok");
            }
            if (ok) cargarCategorias();
        } catch (Exception e) {
            mostrarMensaje("❌ Error: " + e.getMessage(), "form-mensaje-error");
        }
    }

    /**
     * Elimina la categoría seleccionada tras confirmación del usuario.
     * Llamado por el botón "Eliminar" del FXML.
     */
    @FXML
    private void eliminarCategoria() {
        if (categoriaSeleccionada == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar categoría");
        confirm.setHeaderText("Esta acción no se puede deshacer.");
        confirm.setContentText("¿Eliminar la categoría \""
                + categoriaSeleccionada.getNombre() + "\"?");
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    boolean ok = CategoriaInsumoDAO.eliminar(
                            categoriaSeleccionada.getIdCategoriaInsumo());
                    if (ok) {
                        mostrarMensaje("✅ Categoría eliminada.", "form-mensaje-ok");
                        prepararNuevoCategoria();
                        cargarCategorias();
                    }
                } catch (Exception e) {
                    Alertas.error("Error al eliminar", e.getMessage());
                }
            }
        });
    }

    // ─────────────────────────────────────────────────────────────
    // HELPERS PRIVADOS
    // ─────────────────────────────────────────────────────────────

    /**
     * Carga todas las categorías desde la BD y refresca la tabla y el conteo.
     */
    private void cargarCategorias() {
        try {
            listaCategorias.setAll(CategoriaInsumoDAO.listarTodas());
            actualizarConteo();
            actualizarPlaceholder();
        } catch (Exception e) {
            Alertas.error("Error al cargar categorías", e.getMessage());
        }
    }

    /**
     * Carga los datos de una categoría en el formulario para editarla.
     *
     * @param c Categoría seleccionada en la tabla
     */
    private void cargarEnFormulario(CategoriaInsumo c) {
        categoriaSeleccionada = c;
        txtCatNombre.setText(c.getNombre());
        txtCatDescripcion.setText(c.getDescripcion() != null ? c.getDescripcion() : "");
        cmbCatEstado.setValue(c.getEstado());
        limpiarErroresCampos();
        ocultarMensaje();
        btnEliminarCategoriaForm.setDisable(false);
    }

    /**
     * Construye un objeto CategoriaInsumo con los valores actuales del formulario.
     *
     * @return CategoriaInsumo listo para insertar o actualizar
     */
    private CategoriaInsumo construirDesdeFormulario() {
        return new CategoriaInsumo(
                0L,
                txtCatNombre.getText().trim(),
                txtCatDescripcion.getText().trim(),
                cmbCatEstado.getValue() != null ? cmbCatEstado.getValue() : "Activo"
        );
    }

    /** Actualiza la etiqueta de conteo de resultados debajo de la tabla. */
    private void actualizarConteo() {
        int total = listaCategorias.size();
        lblConteoCategorias.setText(total +
                (total == 1 ? " categoría encontrada" : " categorías encontradas"));
    }

    /** Muestra u oculta el placeholder vacío según si la lista tiene datos. */
    private void actualizarPlaceholder() {
        boolean hayDatos = !listaCategorias.isEmpty();
        tblListaCategorias.setVisible(hayDatos);
        tblListaCategorias.setManaged(hayDatos);
        boxPlaceholder.setVisible(!hayDatos);
        boxPlaceholder.setManaged(!hayDatos);
    }

    /** Muestra el label de feedback con el texto y estilo CSS dados. */
    private void mostrarMensaje(String texto, String estilo) {
        lblMensajeCategoria.setText(texto);
        lblMensajeCategoria.getStyleClass()
                .removeAll("form-mensaje-ok", "form-mensaje-error");
        lblMensajeCategoria.getStyleClass().add(estilo);
        lblMensajeCategoria.setVisible(true);
        lblMensajeCategoria.setManaged(true);
    }

    /** Oculta el label de mensajes del formulario. */
    private void ocultarMensaje() {
        lblMensajeCategoria.setVisible(false);
        lblMensajeCategoria.setManaged(false);
    }

    private void configurarFloatingFields() {
        FloatingFieldHelper.bindTextField(boxCatNombreField, txtCatNombre);
        FloatingFieldHelper.bindComboBox(boxCatEstadoField, cmbCatEstado);
    }

    private void limpiarErroresCampos() {
        if (lblCatNombreError != null) {
            lblCatNombreError.setText("");
            lblCatNombreError.setVisible(false);
            lblCatNombreError.setManaged(false);
        }
        FloatingFieldHelper.clearInvalid(boxCatNombreField, boxCatEstadoField);
    }

    private void mostrarErrorNombre(String mensaje) {
        if (lblCatNombreError != null) {
            lblCatNombreError.setText(mensaje);
            lblCatNombreError.setVisible(true);
            lblCatNombreError.setManaged(true);
        }
        FloatingFieldHelper.setInvalid(boxCatNombreField, true);
    }

    private void marcarInvalido(StackPane contenedor, boolean invalido) {
        FloatingFieldHelper.setInvalid(contenedor, invalido);
    }
}
