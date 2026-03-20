package com.mosqueteros.proyecto_restaurante.controller;

import com.mosqueteros.proyecto_restaurante.dao.InsumoDAO;
import com.mosqueteros.proyecto_restaurante.model.Insumo;
import com.mosqueteros.proyecto_restaurante.util.FloatingFieldHelper;
import com.mosqueteros.proyecto_restaurante.util.SessionUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.text.DecimalFormat;

public class InsumoController {
    @FXML private MFXTextField txtBuscarInsumo;
    @FXML private MFXComboBox<String> cmbFiltroEstado;

    @FXML private TableView<Insumo> tblListaInsumos;
    @FXML private TableColumn<Insumo, Number> colIdInsumo;
    @FXML private TableColumn<Insumo, String> colNombre;
    @FXML private TableColumn<Insumo, String> colCategoria;
    @FXML private TableColumn<Insumo, String> colUnidad;
    @FXML private TableColumn<Insumo, String> colStock;
    @FXML private TableColumn<Insumo, String> colEstado;
    @FXML private Label lblConteoInsumos;
    @FXML private VBox boxPlaceholder;

    @FXML private MFXTextField txtInsNombre;
    @FXML private StackPane boxInsNombreField;
    @FXML private Label lblInsNombreError;
    @FXML private MFXComboBox<String> cmbInsCategoria;
    @FXML private StackPane boxInsCategoriaField;
    @FXML private MFXComboBox<String> cmbInsUnidad;
    @FXML private StackPane boxInsUnidadField;
    @FXML private MFXTextField txtInsStock;
    @FXML private MFXComboBox<String> cmbInsEstado;
    @FXML private StackPane boxInsEstadoField;
    @FXML private MFXButton btnEliminarInsumoForm;
    @FXML private Label lblMensajeInsumo;

    private final ObservableList<Insumo> listaInsumos = FXCollections.observableArrayList();
    private final DecimalFormat formatoStock = new DecimalFormat("#,##0.###");
    private Insumo insumoSeleccionado;

    @FXML
    private void initialize() {
        configurarTabla();
        cmbInsEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbInsEstado.setValue("Activo");
        cmbFiltroEstado.setItems(FXCollections.observableArrayList("", "Activo", "Inactivo"));
        cargarOpcionesCombos();
        cargarInsumos();
        configurarFloatingFields();

        tblListaInsumos.getSelectionModel().selectedItemProperty().addListener((obs, oldV, selected) -> {
            if (selected != null) cargarEnFormulario(selected);
        });
    }

    @FXML
    private void buscarInsumo() {
        try {
            listaInsumos.setAll(InsumoDAO.buscarConFiltros(txtBuscarInsumo.getText(), cmbFiltroEstado.getValue()));
            actualizarPlaceholder();
        } catch (SQLException e) {
            mostrarMensaje("Error al buscar insumos: " + e.getMessage(), true);
        }
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscarInsumo.clear();
        cmbFiltroEstado.setValue(null);
        cargarInsumos();
        limpiarFormulario();
    }

    @FXML
    private void prepararNuevoInsumo() {
        limpiarFormulario();
    }

    @FXML
    private void guardarInsumo() {
        limpiarErroresCampos();
        if (txtInsNombre.getText().isBlank()) {
            mostrarErrorNombre("El nombre es obligatorio.");
            mostrarMensaje("⚠ El nombre del insumo es obligatorio.", true);
            return;
        }
        if (cmbInsCategoria.getValue() == null || cmbInsCategoria.getValue().isBlank()) {
            marcarInvalido(boxInsCategoriaField, true);
            mostrarMensaje("⚠ La categoría es obligatoria.", true);
            return;
        }
        if (cmbInsUnidad.getValue() == null || cmbInsUnidad.getValue().isBlank()) {
            marcarInvalido(boxInsUnidadField, true);
            mostrarMensaje("⚠ La unidad/presentación es obligatoria.", true);
            return;
        }
        if (cmbInsEstado.getValue() == null || cmbInsEstado.getValue().isBlank()) {
            marcarInvalido(boxInsEstadoField, true);
            mostrarMensaje("⚠ Selecciona un estado.", true);
            return;
        }

        double stock;
        try {
            stock = txtInsStock.getText().isBlank() ? 0.0 : Double.parseDouble(txtInsStock.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarMensaje("⚠ El stock debe ser numérico.", true);
            return;
        }

        try {
            Insumo i = construirInsumoDesdeFormulario(stock);
            boolean ok;
            if (insumoSeleccionado == null) {
                ok = InsumoDAO.insertar(i);
                if (ok) mostrarMensaje("✓ Insumo creado correctamente.", false);
            } else {
                i.setInsId(insumoSeleccionado.getInsId());
                ok = InsumoDAO.actualizar(i);
                if (ok) mostrarMensaje("✓ Insumo actualizado correctamente.", false);
            }
            if (ok) {
                cargarInsumos();
                limpiarFormulario();
            }
        } catch (SQLException e) {
            mostrarMensaje("Error al guardar insumo: " + e.getMessage(), true);
        }
    }

    @FXML
    private void eliminarInsumo() {
        if (insumoSeleccionado == null) {
            mostrarMensaje("⚠ Selecciona un insumo para eliminar.", true);
            return;
        }
        try {
            boolean ok = InsumoDAO.eliminar(insumoSeleccionado.getInsId());
            if (ok) {
                mostrarMensaje("✓ Insumo eliminado.", false);
                cargarInsumos();
                limpiarFormulario();
            }
        } catch (SQLException e) {
            mostrarMensaje("Error al eliminar insumo: " + e.getMessage(), true);
        }
    }

    private void limpiarFormulario() {
        insumoSeleccionado = null;
        tblListaInsumos.getSelectionModel().clearSelection();
        txtInsNombre.clear();
        cmbInsCategoria.setValue(null);
        cmbInsUnidad.setValue(null);
        txtInsStock.clear();
        cmbInsEstado.setValue("Activo");
        btnEliminarInsumoForm.setDisable(true);
        limpiarErroresCampos();
        ocultarMensaje();
    }

    private void actualizarPlaceholder() {
        boolean vacio = listaInsumos.isEmpty();
        boxPlaceholder.setVisible(vacio);
        boxPlaceholder.setManaged(vacio);
        lblConteoInsumos.setText(listaInsumos.size() + " insumo" + (listaInsumos.size() == 1 ? "" : "s") + " encontrado" + (listaInsumos.size() == 1 ? "" : "s"));
    }

    private void configurarTabla() {
        colIdInsumo.setCellValueFactory(d -> new SimpleLongProperty(d.getValue().getInsId()));
        colNombre.setCellValueFactory(d -> new SimpleStringProperty(valorSeguro(d.getValue().getInsNombre())));
        colCategoria.setCellValueFactory(d -> new SimpleStringProperty(valorSeguro(d.getValue().getCategoriaNombre())));
        colUnidad.setCellValueFactory(d -> new SimpleStringProperty(valorSeguro(d.getValue().getPresentacionAbreviatura())));
        colStock.setCellValueFactory(d -> new SimpleStringProperty(formatoStock.format(d.getValue().getInsStock())));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(valorSeguro(d.getValue().getInsEstado())));
        tblListaInsumos.setItems(listaInsumos);
    }

    private void cargarInsumos() {
        try {
            listaInsumos.setAll(InsumoDAO.listarTodos());
            actualizarPlaceholder();
        } catch (SQLException e) {
            mostrarMensaje("Error cargando insumos: " + e.getMessage(), true);
        }
    }

    private void cargarEnFormulario(Insumo i) {
        insumoSeleccionado = i;
        txtInsNombre.setText(valorSeguro(i.getInsNombre()));
        cmbInsCategoria.setValue(valorSeguro(i.getCategoriaNombre()));
        cmbInsUnidad.setValue(valorSeguro(i.getPresentacionAbreviatura()));
        txtInsStock.setText(formatoStock.format(i.getInsStock()));
        cmbInsEstado.setValue(valorSeguro(i.getInsEstado()).isBlank() ? "Activo" : i.getInsEstado());
        btnEliminarInsumoForm.setDisable(false);
        ocultarMensaje();
    }

    private Insumo construirInsumoDesdeFormulario(double stock) throws SQLException {
        Insumo i = new Insumo();
        Long sedeActiva = SessionUtil.getSedeActivaId();
        i.setSedeId(sedeActiva != null ? sedeActiva : 1L);
        i.setCategoriaId(InsumoDAO.obtenerIdCategoriaPorNombre(cmbInsCategoria.getValue().trim()));
        i.setPresentacionId(InsumoDAO.obtenerIdPresentacionPorAbreviatura(cmbInsUnidad.getValue().trim()));
        i.setInsNombre(txtInsNombre.getText().trim());
        i.setInsStock(stock);
        i.setInsEstado(cmbInsEstado.getValue() != null ? cmbInsEstado.getValue() : "Activo");
        return i;
    }

    private void cargarOpcionesCombos() {
        try {
            cmbInsCategoria.setItems(FXCollections.observableArrayList(InsumoDAO.listarCategorias()));
            cmbInsUnidad.setItems(FXCollections.observableArrayList(InsumoDAO.listarPresentaciones()));
        } catch (SQLException e) {
            mostrarMensaje("Error cargando categorias/presentaciones: " + e.getMessage(), true);
        }
    }

    private String valorSeguro(String valor) {
        return valor != null ? valor : "";
    }

    private void mostrarMensaje(String texto, boolean esError) {
        lblMensajeInsumo.setText(texto);
        lblMensajeInsumo.setStyle(esError
                ? "-fx-text-fill: #C0392B; -fx-font-weight: bold;"
                : "-fx-text-fill: #27AE60; -fx-font-weight: bold;");
        lblMensajeInsumo.setVisible(true);
        lblMensajeInsumo.setManaged(true);
    }

    private void ocultarMensaje() {
        lblMensajeInsumo.setText("");
        lblMensajeInsumo.setVisible(false);
        lblMensajeInsumo.setManaged(false);
    }

    private void configurarFloatingFields() {
        FloatingFieldHelper.bindTextField(boxInsNombreField, txtInsNombre);
        FloatingFieldHelper.bindComboBox(boxInsCategoriaField, cmbInsCategoria);
        FloatingFieldHelper.bindComboBox(boxInsUnidadField, cmbInsUnidad);
        FloatingFieldHelper.bindComboBox(boxInsEstadoField, cmbInsEstado);
    }

    private void limpiarErroresCampos() {
        if (lblInsNombreError != null) {
            lblInsNombreError.setText("");
            lblInsNombreError.setVisible(false);
            lblInsNombreError.setManaged(false);
        }
        FloatingFieldHelper.clearInvalid(
                boxInsNombreField,
                boxInsCategoriaField,
                boxInsUnidadField,
                boxInsEstadoField
        );
    }

    private void mostrarErrorNombre(String mensaje) {
        if (lblInsNombreError != null) {
            lblInsNombreError.setText(mensaje);
            lblInsNombreError.setVisible(true);
            lblInsNombreError.setManaged(true);
        }
        FloatingFieldHelper.setInvalid(boxInsNombreField, true);
    }

    private void marcarInvalido(StackPane contenedor, boolean invalido) {
        FloatingFieldHelper.setInvalid(contenedor, invalido);
    }
}
