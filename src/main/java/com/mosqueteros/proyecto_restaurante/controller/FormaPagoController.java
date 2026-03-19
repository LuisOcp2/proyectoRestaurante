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
 * Controlador para VistaFormaPago.fxml.
 * Gestiona el CRUD de formas de pago disponibles.
 * Tabla BD: forma_pago
 */
public class FormaPagoController implements Initializable {

    // ─── Filtros ──────────────────────────────────────────────────
    /** Campo de búsqueda por nombre o descripción */
    @FXML private MFXTextField txtBuscarFormaPago;
    /** ComboBox nativo filtro por estado */
    @FXML private ComboBox<String> cmbFiltroEstado;

    // ─── Tabla ────────────────────────────────────────────────────
    /** Tabla principal de formas de pago */
    @FXML private TableView<Object> tblListaFormasPago;
    @FXML private TableColumn<Object, Integer> colIdFormaPago;
    @FXML private TableColumn<Object, String>  colNombre;
    @FXML private TableColumn<Object, String>  colDescripcion;
    @FXML private TableColumn<Object, String>  colEstado;
    /** Conteo de resultados */
    @FXML private Label lblConteoFormasPago;
    /** Placeholder estado vacío */
    @FXML private VBox boxPlaceholder;

    // ─── Formulario ───────────────────────────────────────────────
    /** Campo nombre de la forma de pago */
    @FXML private MFXTextField txtFPNombre;
    /** Campo descripción de la forma de pago */
    @FXML private MFXTextField txtFPDescripcion;
    /** ComboBox estado del formulario */
    @FXML private MFXComboBox<String> cmbFPEstado;
    /** Botón eliminar del formulario */
    @FXML private MFXButton btnEliminarFormaPagoForm;
    /** Etiqueta de mensajes de validación */
    @FXML private Label lblMensajeFormaPago;

    // ─── Estado interno ───────────────────────────────────────────
    /** Lista observable que alimenta la tabla */
    private final ObservableList<Object> listaFormasPago = FXCollections.observableArrayList();

    /**
     * Inicializa combos y tabla al arrancar la vista.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbFPEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbFPEstado.setValue("Activo");
        cmbFiltroEstado.setItems(FXCollections.observableArrayList("", "Activo", "Inactivo"));
        tblListaFormasPago.setItems(listaFormasPago);
        actualizarPlaceholder();
    }

    // ─────────────────────────────────────────────────────────────
    // Acciones de filtros
    // ─────────────────────────────────────────────────────────────

    /**
     * Ejecuta búsqueda con los filtros activos.
     * Llamado por btnBuscarFormaPago del FXML.
     */
    @FXML
    private void buscarFormaPago() {
        // TODO: integrar con FormaPagoDAO.buscarConFiltros(...)
        System.out.println("[FormaPagoController] buscarFormaPago() — filtro: "
                + txtBuscarFormaPago.getText());
        actualizarPlaceholder();
    }

    /**
     * Limpia filtros y recarga la lista completa.
     * Llamado por btnLimpiarFiltroFormaPago del FXML.
     */
    @FXML
    private void limpiarFiltros() {
        txtBuscarFormaPago.clear();
        cmbFiltroEstado.setValue(null);
        listaFormasPago.clear();
        limpiarFormulario();
        actualizarPlaceholder();
    }

    // ─────────────────────────────────────────────────────────────
    // Acciones del formulario
    // ─────────────────────────────────────────────────────────────

    /**
     * Prepara el formulario para registrar una nueva forma de pago.
     * Llamado por btnNuevoFormaPago y btnNuevoFormaPagoForm del FXML.
     */
    @FXML
    private void prepararNuevoFormaPago() {
        limpiarFormulario();
    }

    /**
     * Guarda la forma de pago: INSERT si es nueva, UPDATE si hay selección.
     * Llamado por btnGuardarFormaPago del FXML.
     */
    @FXML
    private void guardarFormaPago() {
        if (txtFPNombre.getText().isBlank()) {
            mostrarMensaje("⚠ El nombre de la forma de pago es obligatorio.", true);
            return;
        }
        // TODO: integrar con FormaPagoDAO.insertar/actualizar(...)
        mostrarMensaje("✓ Forma de pago guardada correctamente.", false);
        limpiarFormulario();
    }

    /**
     * Elimina la forma de pago seleccionada.
     * Llamado por btnEliminarFormaPagoForm del FXML.
     */
    @FXML
    private void eliminarFormaPago() {
        // TODO: integrar con FormaPagoDAO.eliminar(...)
        mostrarMensaje("✓ Forma de pago eliminada.", false);
        limpiarFormulario();
    }

    // ─────────────────────────────────────────────────────────────
    // Utilidades internas
    // ─────────────────────────────────────────────────────────────

    /** Limpia el formulario y reinicia modo a INSERT. */
    private void limpiarFormulario() {
        txtFPNombre.clear();
        txtFPDescripcion.clear();
        cmbFPEstado.setValue("Activo");
        btnEliminarFormaPagoForm.setDisable(true);
        ocultarMensaje();
    }

    /** Actualiza placeholder y conteo del footer. */
    private void actualizarPlaceholder() {
        boolean vacio = listaFormasPago.isEmpty();
        boxPlaceholder.setVisible(vacio);
        boxPlaceholder.setManaged(vacio);
        lblConteoFormasPago.setText(listaFormasPago.size() + " forma" + (listaFormasPago.size() == 1 ? "" : "s") + " de pago encontrada" + (listaFormasPago.size() == 1 ? "" : "s"));
    }

    /** Muestra mensaje de éxito o error en el formulario. */
    private void mostrarMensaje(String texto, boolean esError) {
        lblMensajeFormaPago.setText(texto);
        lblMensajeFormaPago.setStyle(esError
                ? "-fx-text-fill: #C0392B; -fx-font-weight: bold;"
                : "-fx-text-fill: #27AE60; -fx-font-weight: bold;");
        lblMensajeFormaPago.setVisible(true);
        lblMensajeFormaPago.setManaged(true);
    }

    /** Oculta la etiqueta de mensajes. */
    private void ocultarMensaje() {
        lblMensajeFormaPago.setText("");
        lblMensajeFormaPago.setVisible(false);
        lblMensajeFormaPago.setManaged(false);
    }
}
