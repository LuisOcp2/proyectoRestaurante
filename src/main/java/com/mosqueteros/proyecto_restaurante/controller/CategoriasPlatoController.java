package com.mosqueteros.proyecto_restaurante.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para VistaCategoriasPlato.fxml.
 * Gestiona el CRUD de categorías de plato del menú.
 * Tabla BD: categoria_plato
 */
public class CategoriasPlatoController implements Initializable {

    // ─── Filtros ──────────────────────────────────────────────────
    /** Campo de búsqueda por nombre o descripción */
    @FXML private MFXTextField txtBuscarCategoria;
    /** ComboBox nativo filtro por estado */
    @FXML private ComboBox<String> cmbFiltroEstado;

    // ─── Tabla ────────────────────────────────────────────────────
    /** Tabla principal de categorías de plato */
    @FXML private TableView<Object> tblListaCategorias;
    @FXML private TableColumn<Object, Integer> colIdCategoria;
    @FXML private TableColumn<Object, String>  colNombre;
    @FXML private TableColumn<Object, String>  colDescripcion;
    @FXML private TableColumn<Object, String>  colEstado;
    /** Conteo de resultados */
    @FXML private Label lblConteoCategorias;
    /** Placeholder estado vacío */
    @FXML private VBox boxPlaceholder;

    // ─── Formulario ───────────────────────────────────────────────
    /** Campo nombre de la categoría */
    @FXML private MFXTextField txtCatNombre;
    /** Campo descripción de la categoría */
    @FXML private MFXTextField txtCatDescripcion;
    /** ComboBox estado del formulario */
    @FXML private MFXComboBox<String> cmbCatEstado;
    /** Botón eliminar del formulario */
    @FXML private MFXButton btnEliminarCategoriaForm;
    /** Etiqueta de mensajes de validación */
    @FXML private Label lblMensajeCategoria;

    // ─── Estado interno ───────────────────────────────────────────
    /** Lista observable que alimenta la tabla */
    private final ObservableList<Object> listaCategorias = FXCollections.observableArrayList();

    /**
     * Inicializa combos y tabla al arrancar la vista.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbCatEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbCatEstado.setValue("Activo");
        cmbFiltroEstado.setItems(FXCollections.observableArrayList("", "Activo", "Inactivo"));
        tblListaCategorias.setItems(listaCategorias);
        actualizarPlaceholder();
    }

    // ─────────────────────────────────────────────────────────────
    // Acciones de filtros
    // ─────────────────────────────────────────────────────────────

    /**
     * Ejecuta búsqueda con los filtros activos.
     * Llamado por btnBuscarCategoria del FXML.
     */
    @FXML
    private void buscarCategoria() {
        // TODO: integrar con CategoriasPlatoDAO.buscarConFiltros(...)
        System.out.println("[CategoriasPlatoController] buscarCategoria() — filtro: "
                + txtBuscarCategoria.getText());
        actualizarPlaceholder();
    }

    /**
     * Limpia filtros y recarga la lista completa.
     * Llamado por btnLimpiarFiltroCategoria del FXML.
     */
    @FXML
    private void limpiarFiltros() {
        txtBuscarCategoria.clear();
        cmbFiltroEstado.setValue(null);
        listaCategorias.clear();
        limpiarFormulario();
        actualizarPlaceholder();
    }

    // ─────────────────────────────────────────────────────────────
    // Acciones del formulario
    // ─────────────────────────────────────────────────────────────

    /**
     * Prepara el formulario para registrar una nueva categoría.
     * Llamado por btnNuevoCategoria y btnNuevoCategoriaForm del FXML.
     */
    @FXML
    private void prepararNuevoCategoria() {
        limpiarFormulario();
    }

    /**
     * Guarda la categoría: INSERT si es nueva, UPDATE si hay selección.
     * Llamado por btnGuardarCategoria del FXML.
     */
    @FXML
    private void guardarCategoria() {
        if (txtCatNombre.getText().isBlank()) {
            mostrarMensaje("⚠ El nombre de la categoría es obligatorio.", true);
            return;
        }
        // TODO: integrar con CategoriasPlatoDAO.insertar/actualizar(...)
        mostrarMensaje("✓ Categoría guardada correctamente.", false);
        limpiarFormulario();
    }

    /**
     * Elimina la categoría seleccionada.
     * Llamado por btnEliminarCategoriaForm del FXML.
     */
    @FXML
    private void eliminarCategoria() {
        // TODO: integrar con CategoriasPlatoDAO.eliminar(...)
        mostrarMensaje("✓ Categoría eliminada.", false);
        limpiarFormulario();
    }

    // ─────────────────────────────────────────────────────────────
    // Utilidades internas
    // ─────────────────────────────────────────────────────────────

    /** Limpia el formulario y reinicia modo a INSERT. */
    private void limpiarFormulario() {
        txtCatNombre.clear();
        txtCatDescripcion.clear();
        cmbCatEstado.setValue("Activo");
        btnEliminarCategoriaForm.setDisable(true);
        ocultarMensaje();
    }

    /** Actualiza placeholder y conteo del footer. */
    private void actualizarPlaceholder() {
        boolean vacio = listaCategorias.isEmpty();
        boxPlaceholder.setVisible(vacio);
        boxPlaceholder.setManaged(vacio);
        lblConteoCategorias.setText(listaCategorias.size() + " categoría" + (listaCategorias.size() == 1 ? "" : "s") + " encontrada" + (listaCategorias.size() == 1 ? "" : "s"));
    }

    /** Muestra mensaje de éxito o error en el formulario. */
    private void mostrarMensaje(String texto, boolean esError) {
        lblMensajeCategoria.setText(texto);
        lblMensajeCategoria.setStyle(esError
                ? "-fx-text-fill: #C0392B; -fx-font-weight: bold;"
                : "-fx-text-fill: #27AE60; -fx-font-weight: bold;");
        lblMensajeCategoria.setVisible(true);
        lblMensajeCategoria.setManaged(true);
    }

    /** Oculta la etiqueta de mensajes. */
    private void ocultarMensaje() {
        lblMensajeCategoria.setText("");
        lblMensajeCategoria.setVisible(false);
        lblMensajeCategoria.setManaged(false);
    }
}
