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
 * Controlador para VistaAreasMesa.fxml.
 * Gestiona el CRUD de las áreas de mesa del restaurante.
 * Tabla BD: areas_mesa
 */
public class AreasMesaController implements Initializable {

    // ─── Filtros ──────────────────────────────────────────────────
    /** Campo de búsqueda por nombre o descripción */
    @FXML private MFXTextField txtBuscarArea;
    /** ComboBox nativo filtro por estado */
    @FXML private ComboBox<String> cmbFiltroEstado;

    // ─── Tabla ────────────────────────────────────────────────────
    /** Tabla principal de áreas de mesa */
    @FXML private TableView<Object> tblListaAreas;
    @FXML private TableColumn<Object, Integer> colIdArea;
    @FXML private TableColumn<Object, String>  colNombre;
    @FXML private TableColumn<Object, String>  colDescripcion;
    @FXML private TableColumn<Object, String>  colEstado;
    /** Conteo de resultados en el footer */
    @FXML private Label lblConteoAreas;
    /** Placeholder estado vacío */
    @FXML private VBox boxPlaceholder;

    // ─── Formulario ───────────────────────────────────────────────
    /** Campo nombre del área */
    @FXML private MFXTextField txtAreaNombre;
    /** Campo descripción del área */
    @FXML private MFXTextField txtAreaDescripcion;
    /** ComboBox estado del formulario */
    @FXML private MFXComboBox<String> cmbAreaEstado;
    /** Botón eliminar del formulario */
    @FXML private MFXButton btnEliminarAreaForm;
    /** Etiqueta de mensajes de validación */
    @FXML private Label lblMensajeArea;

    // ─── Estado interno ───────────────────────────────────────────
    /** Lista observable que alimenta la tabla */
    private final ObservableList<Object> listaAreas = FXCollections.observableArrayList();

    /**
     * Inicializa combos y carga datos al arrancar la vista.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Poblar combo de estado del formulario
        cmbAreaEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbAreaEstado.setValue("Activo");
        // Poblar combo filtro estado
        cmbFiltroEstado.setItems(FXCollections.observableArrayList("", "Activo", "Inactivo"));
        // Enlazar tabla a la lista observable
        tblListaAreas.setItems(listaAreas);
        // Ocultar placeholder al inicio
        actualizarPlaceholder();
    }

    // ─────────────────────────────────────────────────────────────
    // Acciones de la barra de filtros
    // ─────────────────────────────────────────────────────────────

    /**
     * Ejecuta búsqueda con los filtros activos.
     * Llamado por btnBuscarArea del FXML.
     */
    @FXML
    private void buscarArea() {
        // TODO: integrar con AreasMesaDAO.buscarConFiltros(...)
        System.out.println("[AreasMesaController] buscarArea() — filtro: "
                + txtBuscarArea.getText() + " estado: " + cmbFiltroEstado.getValue());
        actualizarPlaceholder();
    }

    /**
     * Limpia todos los filtros y recarga la lista completa.
     * Llamado por btnLimpiarFiltroArea del FXML.
     */
    @FXML
    private void limpiarFiltros() {
        txtBuscarArea.clear();
        cmbFiltroEstado.setValue(null);
        listaAreas.clear();
        limpiarFormulario();
        actualizarPlaceholder();
    }

    // ─────────────────────────────────────────────────────────────
    // Acciones del formulario
    // ─────────────────────────────────────────────────────────────

    /**
     * Prepara el formulario para registrar un nuevo área.
     * Llamado por btnNuevoArea y btnNuevoAreaForm del FXML.
     */
    @FXML
    private void prepararNuevoArea() {
        limpiarFormulario();
    }

    /**
     * Guarda el área: INSERT si es nuevo, UPDATE si hay selección.
     * Llamado por btnGuardarArea del FXML.
     */
    @FXML
    private void guardarArea() {
        if (txtAreaNombre.getText().isBlank()) {
            mostrarMensaje("⚠ El nombre del área es obligatorio.", true);
            return;
        }
        // TODO: integrar con AreasMesaDAO.insertar/actualizar(...)
        mostrarMensaje("✓ Área guardada correctamente.", false);
        limpiarFormulario();
    }

    /**
     * Elimina el área seleccionada.
     * Llamado por btnEliminarAreaForm del FXML.
     */
    @FXML
    private void eliminarArea() {
        // TODO: integrar con AreasMesaDAO.eliminar(...)
        mostrarMensaje("✓ Área eliminada.", false);
        limpiarFormulario();
    }

    // ─────────────────────────────────────────────────────────────
    // Utilidades internas
    // ─────────────────────────────────────────────────────────────

    /** Limpia el formulario y reinicia modo a INSERT. */
    private void limpiarFormulario() {
        txtAreaNombre.clear();
        txtAreaDescripcion.clear();
        cmbAreaEstado.setValue("Activo");
        btnEliminarAreaForm.setDisable(true);
        ocultarMensaje();
    }

    /** Actualiza visibilidad del placeholder y conteo del footer. */
    private void actualizarPlaceholder() {
        boolean vacio = listaAreas.isEmpty();
        boxPlaceholder.setVisible(vacio);
        boxPlaceholder.setManaged(vacio);
        lblConteoAreas.setText(listaAreas.size() + " área" + (listaAreas.size() == 1 ? "" : "s") + " encontrada" + (listaAreas.size() == 1 ? "" : "s"));
    }

    /** Muestra mensaje de éxito (verde) o error (rojo) en el formulario. */
    private void mostrarMensaje(String texto, boolean esError) {
        lblMensajeArea.setText(texto);
        lblMensajeArea.setStyle(esError
                ? "-fx-text-fill: #C0392B; -fx-font-weight: bold;"
                : "-fx-text-fill: #27AE60; -fx-font-weight: bold;");
        lblMensajeArea.setVisible(true);
        lblMensajeArea.setManaged(true);
    }

    /** Oculta la etiqueta de mensajes. */
    private void ocultarMensaje() {
        lblMensajeArea.setText("");
        lblMensajeArea.setVisible(false);
        lblMensajeArea.setManaged(false);
    }
}
