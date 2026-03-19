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
 * Controlador para VistaConceptoEgreso.fxml.
 * Gestiona el CRUD de conceptos de egreso.
 * Tabla BD: concepto_egreso
 *
 * CORRECCIÓN: el controlador anterior referenciaba "comboEstado"
 * que no existe en el FXML nuevo. Ahora usa los fx:id correctos:
 *   cmbFiltroEstado  → ComboBox filtro en barra superior
 *   cmbConEstado     → MFXComboBox en formulario lateral
 */
public class ConceptoEgresoController implements Initializable {

    // ─── Filtros ──────────────────────────────────────────────────
    /** Campo de búsqueda por nombre o descripción */
    @FXML private MFXTextField txtBuscarConcepto;
    /** ComboBox nativo filtro por estado (fx:id="cmbFiltroEstado") */
    @FXML private ComboBox<String> cmbFiltroEstado;

    // ─── Tabla ────────────────────────────────────────────────────
    /** Tabla principal de conceptos de egreso */
    @FXML private TableView<Object> tblListaConceptos;
    @FXML private TableColumn<Object, Integer> colIdConcepto;
    @FXML private TableColumn<Object, String>  colNombre;
    @FXML private TableColumn<Object, String>  colDescripcion;
    @FXML private TableColumn<Object, String>  colEstado;
    /** Conteo de resultados */
    @FXML private Label lblConteoConceptos;
    /** Placeholder estado vacío */
    @FXML private VBox boxPlaceholder;

    // ─── Formulario ───────────────────────────────────────────────
    /** Campo nombre del concepto */
    @FXML private MFXTextField txtConNombre;
    /** Campo descripción del concepto */
    @FXML private MFXTextField txtConDescripcion;
    /** ComboBox estado del formulario (fx:id="cmbConEstado") */
    @FXML private MFXComboBox<String> cmbConEstado;
    /** Botón eliminar del formulario */
    @FXML private MFXButton btnEliminarConceptoForm;
    /** Etiqueta de mensajes de validación */
    @FXML private Label lblMensajeConcepto;

    // ─── Estado interno ───────────────────────────────────────────
    /** Lista observable que alimenta la tabla */
    private final ObservableList<Object> listaConceptos = FXCollections.observableArrayList();

    /**
     * Inicializa combos y tabla al arrancar la vista.
     * Los fx:id aquí coinciden exactamente con VistaConceptoEgreso.fxml.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Combo del formulario: estado activo/inactivo
        cmbConEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbConEstado.setValue("Activo");
        // Combo del filtro: incluye opción vacía para mostrar todos
        cmbFiltroEstado.setItems(FXCollections.observableArrayList("", "Activo", "Inactivo"));
        // Enlazar tabla a lista observable
        tblListaConceptos.setItems(listaConceptos);
        actualizarPlaceholder();
    }

    // ─────────────────────────────────────────────────────────────
    // Acciones de filtros
    // ─────────────────────────────────────────────────────────────

    /**
     * Ejecuta búsqueda con los filtros activos.
     * Llamado por btnBuscarConcepto del FXML.
     */
    @FXML
    private void buscarConcepto() {
        // TODO: integrar con ConceptoEgresoDAO.buscarConFiltros(...)
        System.out.println("[ConceptoEgresoController] buscarConcepto() — filtro: "
                + txtBuscarConcepto.getText());
        actualizarPlaceholder();
    }

    /**
     * Limpia filtros y recarga la lista completa.
     * Llamado por btnLimpiarFiltroConcepto del FXML.
     */
    @FXML
    private void limpiarFiltros() {
        txtBuscarConcepto.clear();
        cmbFiltroEstado.setValue(null);
        listaConceptos.clear();
        limpiarFormulario();
        actualizarPlaceholder();
    }

    // ─────────────────────────────────────────────────────────────
    // Acciones del formulario
    // ─────────────────────────────────────────────────────────────

    /**
     * Prepara el formulario para registrar un nuevo concepto.
     * Llamado por btnNuevoConcepto y btnNuevoConceptoForm del FXML.
     */
    @FXML
    private void prepararNuevoConcepto() {
        limpiarFormulario();
    }

    /**
     * Guarda el concepto: INSERT si es nuevo, UPDATE si hay selección.
     * Llamado por btnGuardarConcepto del FXML.
     */
    @FXML
    private void guardarConcepto() {
        if (txtConNombre.getText().isBlank()) {
            mostrarMensaje("⚠ El nombre del concepto es obligatorio.", true);
            return;
        }
        // TODO: integrar con ConceptoEgresoDAO.insertar/actualizar(...)
        mostrarMensaje("✓ Concepto guardado correctamente.", false);
        limpiarFormulario();
    }

    /**
     * Elimina el concepto seleccionado.
     * Llamado por btnEliminarConceptoForm del FXML.
     */
    @FXML
    private void eliminarConcepto() {
        // TODO: integrar con ConceptoEgresoDAO.eliminar(...)
        mostrarMensaje("✓ Concepto eliminado.", false);
        limpiarFormulario();
    }

    // ─────────────────────────────────────────────────────────────
    // Utilidades internas
    // ─────────────────────────────────────────────────────────────

    /** Limpia el formulario y reinicia modo a INSERT. */
    private void limpiarFormulario() {
        txtConNombre.clear();
        txtConDescripcion.clear();
        cmbConEstado.setValue("Activo");
        btnEliminarConceptoForm.setDisable(true);
        ocultarMensaje();
    }

    /** Actualiza placeholder y conteo del footer. */
    private void actualizarPlaceholder() {
        boolean vacio = listaConceptos.isEmpty();
        boxPlaceholder.setVisible(vacio);
        boxPlaceholder.setManaged(vacio);
        lblConteoConceptos.setText(listaConceptos.size() + " concepto" + (listaConceptos.size() == 1 ? "" : "s") + " encontrado" + (listaConceptos.size() == 1 ? "" : "s"));
    }

    /** Muestra mensaje de éxito o error en el formulario. */
    private void mostrarMensaje(String texto, boolean esError) {
        lblMensajeConcepto.setText(texto);
        lblMensajeConcepto.setStyle(esError
                ? "-fx-text-fill: #C0392B; -fx-font-weight: bold;"
                : "-fx-text-fill: #27AE60; -fx-font-weight: bold;");
        lblMensajeConcepto.setVisible(true);
        lblMensajeConcepto.setManaged(true);
    }

    /** Oculta la etiqueta de mensajes. */
    private void ocultarMensaje() {
        lblMensajeConcepto.setText("");
        lblMensajeConcepto.setVisible(false);
        lblMensajeConcepto.setManaged(false);
    }
}
