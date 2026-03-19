package com.mosqueteros.proyecto_restaurante.controller;

import com.mosqueteros.proyecto_restaurante.dao.CategoriaInsumoDAO;
import com.mosqueteros.proyecto_restaurante.model.CategoriaInsumo;
import com.mosqueteros.proyecto_restaurante.util.Alertas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

/**
 * Controlador del módulo Categorías de Insumo.
 * Gestiona el CRUD completo: listar, buscar, guardar (crear/editar) y eliminar.
 *
 * Vinculado a: VistaCategoriaInsumo.fxml
 * Modelo:      CategoriaInsumo.java
 * DAO:         CategoriaInsumoDAO.java
 */
public class CategoriaInsumoController {

    // ── Barra de filtros ──────────────────────────────────────────────
    @FXML private TextField               txtBuscarCategoria;
    @FXML private ComboBox<String>        cmbFiltroEstado;
    @FXML private Button                  btnBuscarCategoria;
    @FXML private Button                  btnLimpiarFiltroCategoria;

    // ── Tabla ────────────────────────────────────────────────────────
    @FXML private TableView<CategoriaInsumo>         tblListaCategorias;
    @FXML private TableColumn<CategoriaInsumo, Long>   colId;
    @FXML private TableColumn<CategoriaInsumo, String> colNombre;
    @FXML private TableColumn<CategoriaInsumo, String> colDescripcion;
    @FXML private TableColumn<CategoriaInsumo, String> colEstado;
    @FXML private Label                               lblConteoCategorias;
    @FXML private VBox                                boxPlaceholder;

    // ── Formulario ──────────────────────────────────────────────────
    @FXML private TextField    txtCatNombre;
    @FXML private TextField    txtCatDescripcion;
    @FXML private ComboBox<String> cmbCatEstado;
    @FXML private Label        lblMensajeCategoria;

    // ── Botones ─────────────────────────────────────────────────────
    @FXML private Button btnNuevoCategoria;
    @FXML private Button btnNuevoCategoriaForm;
    @FXML private Button btnGuardarCategoria;
    @FXML private Button btnEliminarCategoriaForm;

    /** Categoría actualmente seleccionada en la tabla para editar */
    private CategoriaInsumo categoriaSeleccionada = null;

    /** Lista observable vinculada a la tabla */
    private final ObservableList<CategoriaInsumo> listaCategorias =
            FXCollections.observableArrayList();

    // ─────────────────────────────────────────────────────────────
    // INICIALIZACIÓN
    // ─────────────────────────────────────────────────────────────

    /**
     * JavaFX llama este método automáticamente al cargar el FXML.
     * Configura columnas de la tabla, pobla ComboBoxes y carga datos iniciales.
     */
    @FXML
    public void initialize() {
        configurarColumnas();
        configurarComboBoxes();
        configurarSeleccionTabla();
        cargarCategorias();
    }

    /**
     * Enlaza cada columna de la tabla con la Property correspondiente del modelo.
     * PropertyValueFactory busca: get + NombreCampo() en el modelo.
     */
    private void configurarColumnas() {
        colId          .setCellValueFactory(new PropertyValueFactory<>("idCategoriaInsumo"));
        colNombre      .setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion .setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colEstado      .setCellValueFactory(new PropertyValueFactory<>("estado"));

        tblListaCategorias.setItems(listaCategorias);
        tblListaCategorias.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Carga los valores estáticos en los ComboBoxes de filtro y formulario.
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
                    if (seleccionada != null) {
                        cargarEnFormulario(seleccionada);
                    }
                });
    }

    // ─────────────────────────────────────────────────────────────
    // ACCIONES DE TABLA
    // ─────────────────────────────────────────────────────────────

    /**
     * Ejecuta búsqueda con los filtros activos (texto y estado).
     * Llamado por el botón "Buscar" del FXML.
     */
    @FXML
    private void buscarCategoria() {
        try {
            String texto = txtBuscarCategoria.getText();
            String estado = cmbFiltroEstado.getValue();
            var resultados = CategoriaInsumoDAO.buscarConFiltros(texto, estado);
            listaCategorias.setAll(resultados);
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

    // ─────────────────────────────────────────────────────────────
    // ACCIONES DE FORMULARIO
    // ─────────────────────────────────────────────────────────────

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
        // Validar campos obligatorios
        if (txtCatNombre.getText().isBlank()) {
            mostrarMensaje("⚠️ El nombre es obligatorio.", "form-mensaje-error");
            return;
        }

        CategoriaInsumo c = construirDesdeFormulario();
        try {
            boolean ok;
            if (categoriaSeleccionada == null) {
                // Modo creación
                ok = CategoriaInsumoDAO.insertar(c);
                if (ok) mostrarMensaje("✅ Categoría creada correctamente.", "form-mensaje-ok");
            } else {
                // Modo edición
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

        // Confirmación antes de eliminar
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar categoría");
        confirm.setHeaderText("Esta acción no se puede deshacer.");
        confirm.setContentText("¿Eliminar la categoría \""
                + categoriaSeleccionada.getNombre() + "\"?");

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    boolean ok = CategoriaInsumoDAO.eliminar(categoriaSeleccionada.getIdCategoriaInsumo());
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
     * Carga todas las categorías desde la BD y refresca la tabla.
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
     * Carga los datos de una categoría en los campos del formulario para editarla.
     *
     * @param c Categoría seleccionada en la tabla
     */
    private void cargarEnFormulario(CategoriaInsumo c) {
        categoriaSeleccionada = c;
        txtCatNombre.setText(c.getNombre());
        txtCatDescripcion.setText(c.getDescripcion() != null ? c.getDescripcion() : "");
        cmbCatEstado.setValue(c.getEstado());
        ocultarMensaje();
        btnEliminarCategoriaForm.setDisable(false);
    }

    /**
     * Construye un objeto {@link CategoriaInsumo} con los valores del formulario.
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

    /** Actualiza la etiqueta de conteo de resultados en la tabla. */
    private void actualizarConteo() {
        int total = listaCategorias.size();
        lblConteoCategorias.setText(total + (total == 1 ? " categoría encontrada" : " categorías encontradas"));
    }

    /** Muestra u oculta el placeholder vacío según haya datos en la tabla. */
    private void actualizarPlaceholder() {
        boolean hayDatos = !listaCategorias.isEmpty();
        tblListaCategorias.setVisible(hayDatos);
        tblListaCategorias.setManaged(hayDatos);
        boxPlaceholder.setVisible(!hayDatos);
        boxPlaceholder.setManaged(!hayDatos);
    }

    /** Muestra el mensaje de feedback en el formulario con el estilo dado. */
    private void mostrarMensaje(String texto, String estilo) {
        lblMensajeCategoria.setText(texto);
        lblMensajeCategoria.getStyleClass().removeAll("form-mensaje-ok", "form-mensaje-error");
        lblMensajeCategoria.getStyleClass().add(estilo);
        lblMensajeCategoria.setVisible(true);
        lblMensajeCategoria.setManaged(true);
    }

    /** Oculta el label de mensajes del formulario. */
    private void ocultarMensaje() {
        lblMensajeCategoria.setVisible(false);
        lblMensajeCategoria.setManaged(false);
    }
}
