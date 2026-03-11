package com.mosqueteros.proyecto_restaurante.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

/**
 * PlatoController — controlador para VistaPlatos.fxml
 *
 * Contiene los métodos @FXML vinculados a la vista CRUD de platos.
 * Los métodos son stubs (mock); la lógica real irá en el DAO de Platos.
 */
public class PlatoController {

    // ── Tabla ────────────────────────────────────────────────────────────
    @FXML private TableView<?>     tblListaPlatos;
    @FXML private TableColumn<?,?> colPlatId;
    @FXML private TableColumn<?,?> colPlatCategoria;
    @FXML private TableColumn<?,?> colPlatNombre;
    @FXML private TableColumn<?,?> colPlatDescripcion;
    @FXML private TableColumn<?,?> colPlatPrecio;
    @FXML private TableColumn<?,?> colPlatEstado;

    // ── Filtros ──────────────────────────────────────────────────────────
    @FXML private MFXTextField   txtBuscarPlato;
    @FXML private MFXComboBox<?> cmbFiltroCategoriaPlato;
    @FXML private MFXComboBox<?> cmbFiltroEstadoPlato;

    // ── Formulario ───────────────────────────────────────────────────────
    @FXML private MFXTextField   txtPlatNombre;
    @FXML private TextArea       txtPlatDescripcion;
    @FXML private MFXTextField   txtPlatPrecio;
    @FXML private MFXComboBox<?> cmbCategoriaPlato;
    @FXML private MFXComboBox<?> cmbEstadoPlato;

    // ── Labels ───────────────────────────────────────────────────────────
    @FXML private Label lblConteoPlatos;
    @FXML private Label lblMensajePlato;
    @FXML private Label lblImagenPlato;

    // ── Botones ──────────────────────────────────────────────────────────
    @FXML private MFXButton btnBuscarPlato;
    @FXML private MFXButton btnLimpiarFiltroPlato;
    @FXML private MFXButton btnNuevoPlato;
    @FXML private MFXButton btnEditarPlato;
    @FXML private MFXButton btnEliminarPlato;
    @FXML private MFXButton btnGuardarPlato;
    @FXML private MFXButton btnNuevoPlatoForm;
    @FXML private MFXButton btnEliminarPlatoForm;
    @FXML private MFXButton btnSeleccionarImagenPlato;

    // ── Métodos mock (placeholders para la lógica del DAO) ───────────────

    /** Filtra la tabla por nombre/categoría/estado */
    @FXML
    private void buscarPlatoMock(ActionEvent event) {
        System.out.println("[Plato] buscarPlato (mock)");
    }

    /** Limpia todos los filtros y recarga la tabla */
    @FXML
    private void limpiarFiltrosPlatoMock(ActionEvent event) {
        txtBuscarPlato.clear();
        System.out.println("[Plato] limpiarFiltros (mock)");
    }

    /** Prepara el formulario para registrar un plato nuevo */
    @FXML
    private void prepararNuevoPlatoMock(ActionEvent event) {
        txtPlatNombre.clear();
        txtPlatDescripcion.clear();
        txtPlatPrecio.clear();
        System.out.println("[Plato] nuevoPlato (mock)");
    }

    /** Carga en el formulario los datos del plato seleccionado */
    @FXML
    private void editarPlatoMock(ActionEvent event) {
        System.out.println("[Plato] editarPlato (mock)");
    }

    /** Guarda (INSERT o UPDATE) el plato del formulario */
    @FXML
    private void guardarPlatoMock(ActionEvent event) {
        lblMensajePlato.setText("✅ Plato guardado (mock)");
        System.out.println("[Plato] guardarPlato (mock)");
    }

    /** Elimina el plato seleccionado en la tabla */
    @FXML
    private void eliminarPlatoMock(ActionEvent event) {
        lblMensajePlato.setText("🗑 Plato eliminado (mock)");
        System.out.println("[Plato] eliminarPlato (mock)");
    }

    /** Abre un selector de archivo para elegir imagen del plato */
    @FXML
    private void seleccionarImagenPlatoMock(ActionEvent event) {
        lblImagenPlato.setText("📷 imagen_ejemplo.png (mock)");
        System.out.println("[Plato] seleccionarImagen (mock)");
    }
}
