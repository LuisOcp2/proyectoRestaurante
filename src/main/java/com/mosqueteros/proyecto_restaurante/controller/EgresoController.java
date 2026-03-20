package com.mosqueteros.proyecto_restaurante.controller;

import com.mosqueteros.proyecto_restaurante.util.FloatingFieldHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import io.github.palexdev.materialfx.controls.*;

/**
 * Controlador para VistaEgresos.fxml.
 * Gestiona el CRUD de egresos del restaurante.
 * Tabla BD: egresos
 *
 * CORRECCIÓN: el controlador anterior referenciaba "comboTipoEgreso"
 * que no existe en el FXML nuevo. Ahora usa los fx:id correctos:
 *   cmbFiltroConcepto → MFXComboBox filtro concepto en barra superior
 *   cmbFiltroEstado   → MFXComboBox filtro estado en barra superior
 *   cmbEgresoEstado   → MFXComboBox estado en formulario lateral
 */
public class EgresoController implements Initializable {

    // ─── Filtros ──────────────────────────────────────────────────
    /** Campo de búsqueda por concepto o número */
    @FXML private MFXTextField txtBuscarEgreso;
    /** MFXComboBox nativo filtro por concepto (fx:id="cmbFiltroConcepto") */
    @FXML private MFXComboBox<String> cmbFiltroConcepto;
    /** MFXComboBox nativo filtro por estado (fx:id="cmbFiltroEstado") */
    @FXML private MFXComboBox<String> cmbFiltroEstado;

    // ─── Tabla ────────────────────────────────────────────────────
    /** Tabla principal de egresos */
    @FXML private TableView<Object> tblListaEgresos;
    @FXML private TableColumn<Object, Integer> colIdEgreso;
    @FXML private TableColumn<Object, String>  colNumero;
    @FXML private TableColumn<Object, String>  colFecha;
    @FXML private TableColumn<Object, String>  colConcepto;
    @FXML private TableColumn<Object, String>  colDetalle;
    @FXML private TableColumn<Object, Double>  colValor;
    @FXML private TableColumn<Object, String>  colEstado;
    /** Conteo de resultados */
    @FXML private Label lblConteoEgresos;
    /** Placeholder estado vacío */
    @FXML private VBox boxPlaceholder;

    // ─── Formulario ───────────────────────────────────────────────
    /** Campo número del egreso */
    @FXML private MFXTextField txtEgresoNumero;
    /** Contenedor visual del campo numero */
    @FXML private StackPane boxEgresoNumeroField;
    /** Error de validacion del numero */
    @FXML private Label lblEgresoNumeroError;
    /** Campo fecha del egreso */
    @FXML private MFXTextField dtEgresoFecha;
    /** Campo concepto del egreso */
    @FXML private MFXTextField txtEgresoConcepto;
    /** Campo detalle del egreso */
    @FXML private MFXTextField txtEgresoDetalle;
    /** Campo valor del egreso */
    @FXML private MFXTextField txtEgresoValor;
    /** MFXComboBox estado del formulario (fx:id="cmbEgresoEstado") */
    @FXML private MFXComboBox<String> cmbEgresoEstado;
    /** Contenedor visual del combo estado */
    @FXML private StackPane boxEgresoEstadoField;
    /** Botón eliminar del formulario */
    @FXML private MFXButton btnEliminarEgresoForm;
    /** Etiqueta de mensajes de validación */
    @FXML private Label lblMensajeEgreso;

    // ─── Estado interno ───────────────────────────────────────────
    /** Lista observable que alimenta la tabla */
    private final ObservableList<Object> listaEgresos = FXCollections.observableArrayList();

    /**
     * Inicializa combos y tabla al arrancar la vista.
     * Los fx:id aquí coinciden exactamente con VistaEgresos.fxml.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Combo estado del formulario
        cmbEgresoEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbEgresoEstado.setValue("Activo");
        // Combos de filtro: incluyen opción vacía para mostrar todos
        cmbFiltroEstado.setItems(FXCollections.observableArrayList("", "Activo", "Inactivo"));
        cmbFiltroConcepto.setItems(FXCollections.observableArrayList(""));
        // TODO: cargar conceptos desde ConceptoEgresoDAO para poblar cmbFiltroConcepto
        // Enlazar tabla a lista observable
        tblListaEgresos.setItems(listaEgresos);
        actualizarPlaceholder();
        configurarFloatingFields();
    }

    // ─────────────────────────────────────────────────────────────
    // Acciones de filtros
    // ─────────────────────────────────────────────────────────────

    /**
     * Ejecuta búsqueda con los filtros activos.
     * Llamado por btnBuscarEgreso del FXML.
     */
    @FXML
    private void buscarEgreso() {
        // TODO: integrar con EgresoDAO.buscarConFiltros(...)
        System.out.println("[EgresoController] buscarEgreso() — filtro: "
                + txtBuscarEgreso.getText());
        actualizarPlaceholder();
    }

    /**
     * Limpia filtros y recarga la lista completa.
     * Llamado por btnLimpiarFiltroEgreso del FXML.
     */
    @FXML
    private void limpiarFiltros() {
        txtBuscarEgreso.clear();
        cmbFiltroConcepto.setValue(null);
        cmbFiltroEstado.setValue(null);
        listaEgresos.clear();
        limpiarFormulario();
        actualizarPlaceholder();
    }

    // ─────────────────────────────────────────────────────────────
    // Acciones del formulario
    // ─────────────────────────────────────────────────────────────

    /**
     * Prepara el formulario para registrar un nuevo egreso.
     * Llamado por btnNuevoEgreso y btnNuevoEgresoForm del FXML.
     */
    @FXML
    private void prepararNuevoEgreso() {
        limpiarFormulario();
    }

    /**
     * Guarda el egreso: INSERT si es nuevo, UPDATE si hay selección.
     * Llamado por btnGuardarEgreso del FXML.
     */
    @FXML
    private void guardarEgreso() {
        limpiarErroresCampos();
        if (txtEgresoNumero.getText().isBlank()) {
            mostrarErrorNumero("El numero de egreso es obligatorio.");
            mostrarMensaje("⚠ El número de egreso es obligatorio.", true);
            return;
        }
        if (txtEgresoValor.getText().isBlank()) {
            mostrarMensaje("⚠ El valor del egreso es obligatorio.", true);
            return;
        }
        if (cmbEgresoEstado.getValue() == null || cmbEgresoEstado.getValue().isBlank()) {
            marcarInvalido(boxEgresoEstadoField, true);
            mostrarMensaje("⚠ Selecciona un estado.", true);
            return;
        }
        // TODO: integrar con EgresoDAO.insertar/actualizar(...)
        mostrarMensaje("✓ Egreso guardado correctamente.", false);
        limpiarFormulario();
    }

    /**
     * Elimina el egreso seleccionado.
     * Llamado por btnEliminarEgresoForm del FXML.
     */
    @FXML
    private void eliminarEgreso() {
        // TODO: integrar con EgresoDAO.eliminar(...)
        mostrarMensaje("✓ Egreso eliminado.", false);
        limpiarFormulario();
    }

    // ─────────────────────────────────────────────────────────────
    // Utilidades internas
    // ─────────────────────────────────────────────────────────────

    /** Limpia el formulario y reinicia modo a INSERT. */
    private void limpiarFormulario() {
        txtEgresoNumero.clear();
        dtEgresoFecha.clear();
        txtEgresoConcepto.clear();
        txtEgresoDetalle.clear();
        txtEgresoValor.clear();
        cmbEgresoEstado.setValue("Activo");
        btnEliminarEgresoForm.setDisable(true);
        limpiarErroresCampos();
        ocultarMensaje();
    }

    /** Actualiza placeholder y conteo del footer. */
    private void actualizarPlaceholder() {
        boolean vacio = listaEgresos.isEmpty();
        boxPlaceholder.setVisible(vacio);
        boxPlaceholder.setManaged(vacio);
        lblConteoEgresos.setText(listaEgresos.size() + " egreso" + (listaEgresos.size() == 1 ? "" : "s") + " encontrado" + (listaEgresos.size() == 1 ? "" : "s"));
    }

    /** Muestra mensaje de éxito o error en el formulario. */
    private void mostrarMensaje(String texto, boolean esError) {
        lblMensajeEgreso.setText(texto);
        lblMensajeEgreso.setStyle(esError
                ? "-fx-text-fill: #C0392B; -fx-font-weight: bold;"
                : "-fx-text-fill: #27AE60; -fx-font-weight: bold;");
        lblMensajeEgreso.setVisible(true);
        lblMensajeEgreso.setManaged(true);
    }

    /** Oculta la etiqueta de mensajes. */
    private void ocultarMensaje() {
        lblMensajeEgreso.setText("");
        lblMensajeEgreso.setVisible(false);
        lblMensajeEgreso.setManaged(false);
    }

    private void configurarFloatingFields() {
        FloatingFieldHelper.bindTextField(boxEgresoNumeroField, txtEgresoNumero);
        FloatingFieldHelper.bindComboBox(boxEgresoEstadoField, cmbEgresoEstado);
    }

    private void limpiarErroresCampos() {
        if (lblEgresoNumeroError != null) {
            lblEgresoNumeroError.setText("");
            lblEgresoNumeroError.setVisible(false);
            lblEgresoNumeroError.setManaged(false);
        }
        FloatingFieldHelper.clearInvalid(boxEgresoNumeroField, boxEgresoEstadoField);
    }

    private void mostrarErrorNumero(String mensaje) {
        if (lblEgresoNumeroError != null) {
            lblEgresoNumeroError.setText(mensaje);
            lblEgresoNumeroError.setVisible(true);
            lblEgresoNumeroError.setManaged(true);
        }
        FloatingFieldHelper.setInvalid(boxEgresoNumeroField, true);
    }

    private void marcarInvalido(StackPane contenedor, boolean invalido) {
        FloatingFieldHelper.setInvalid(contenedor, invalido);
    }
}
