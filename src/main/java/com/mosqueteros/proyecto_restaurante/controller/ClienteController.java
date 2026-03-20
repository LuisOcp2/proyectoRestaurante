package com.mosqueteros.proyecto_restaurante.controller;

import com.mosqueteros.proyecto_restaurante.dao.ClienteDAO;
import com.mosqueteros.proyecto_restaurante.model.Cliente;
import com.mosqueteros.proyecto_restaurante.util.FloatingFieldHelper;
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

public class ClienteController {
    @FXML private MFXTextField txtBuscarCliente;
    @FXML private MFXComboBox<String> cmbFiltroEstado;

    @FXML private TableView<Cliente> tblListaClientes;
    @FXML private TableColumn<Cliente, Number> colIdCliente;
    @FXML private TableColumn<Cliente, String> colDocumento;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colEstado;
    @FXML private Label lblConteoClientes;
    @FXML private VBox boxPlaceholder;

    @FXML private MFXTextField txtCliDocumento;
    @FXML private MFXComboBox<String> cmbCliTipoDocumento;
    @FXML private StackPane boxCliTipoDocumentoField;
    @FXML private StackPane boxCliDocumentoField;
    @FXML private Label lblCliDocumentoError;
    @FXML private MFXTextField txtCliNombre;
    @FXML private StackPane boxCliNombreField;
    @FXML private MFXTextField txtCliCorreo;
    @FXML private StackPane boxCliCorreoField;
    @FXML private MFXTextField txtCliTelefono;
    @FXML private StackPane boxCliTelefonoField;
    @FXML private MFXComboBox<String> cmbCliEstado;
    @FXML private StackPane boxCliEstadoField;
    @FXML private MFXButton btnEliminarClienteForm;
    @FXML private Label lblMensajeCliente;

    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private Cliente clienteSeleccionado;

    @FXML
    private void initialize() {
        configurarTabla();
        cmbCliTipoDocumento.setItems(FXCollections.observableArrayList("CC", "NIT", "CE", "Pasaporte", "DNI"));
        cmbCliTipoDocumento.setValue("CC");
        cmbCliEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbCliEstado.setValue("Activo");
        cmbFiltroEstado.setItems(FXCollections.observableArrayList("", "Activo", "Inactivo"));
        cargarClientes();
        configurarFloatingFields();

        tblListaClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldV, selected) -> {
            if (selected != null) cargarEnFormulario(selected);
        });
    }

    @FXML
    private void buscarCliente() {
        try {
            listaClientes.setAll(ClienteDAO.buscarConFiltros(txtBuscarCliente.getText(), cmbFiltroEstado.getValue()));
            actualizarPlaceholder();
        } catch (SQLException e) {
            mostrarMensaje("Error al buscar clientes: " + e.getMessage(), true);
        }
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscarCliente.clear();
        cmbFiltroEstado.setValue(null);
        cargarClientes();
        limpiarFormulario();
    }

    @FXML
    private void prepararNuevoCliente() {
        limpiarFormulario();
    }

    @FXML
    private void guardarCliente() {
        limpiarErroresCampos();
        if (txtCliDocumento.getText().isBlank()) {
            mostrarErrorDocumento("El documento es obligatorio.");
            mostrarMensaje("⚠ El documento es obligatorio.", true);
            return;
        }
        if (cmbCliTipoDocumento.getValue() == null || cmbCliTipoDocumento.getValue().isBlank()) {
            marcarInvalido(boxCliTipoDocumentoField, true);
            mostrarMensaje("⚠ Selecciona el tipo de documento.", true);
            return;
        }
        if (txtCliNombre.getText().isBlank()) {
            mostrarMensaje("⚠ El nombre es obligatorio.", true);
            return;
        }
        if (cmbCliEstado.getValue() == null || cmbCliEstado.getValue().isBlank()) {
            marcarInvalido(boxCliEstadoField, true);
            mostrarMensaje("⚠ Selecciona un estado.", true);
            return;
        }
        Cliente c = construirClienteDesdeFormulario();
        try {
            boolean ok;
            if (clienteSeleccionado == null) {
                ok = ClienteDAO.insertar(c);
                if (ok) mostrarMensaje("✓ Cliente creado correctamente.", false);
            } else {
                c.setCliId(clienteSeleccionado.getCliId());
                ok = ClienteDAO.actualizar(c);
                if (ok) mostrarMensaje("✓ Cliente actualizado correctamente.", false);
            }
            if (ok) {
                cargarClientes();
                limpiarFormulario();
            }
        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("uk_cli_documento")) {
                mostrarMensaje("⚠ El documento ya existe.", true);
            } else if (msg != null && msg.contains("uk_cli_correo")) {
                mostrarMensaje("⚠ El correo ya existe.", true);
            } else {
                mostrarMensaje("Error al guardar cliente: " + msg, true);
            }
        }
    }

    @FXML
    private void eliminarCliente() {
        if (clienteSeleccionado == null) {
            mostrarMensaje("⚠ Selecciona un cliente para eliminar.", true);
            return;
        }
        try {
            boolean ok = ClienteDAO.eliminar(clienteSeleccionado.getCliId());
            if (ok) {
                mostrarMensaje("✓ Cliente eliminado.", false);
                cargarClientes();
                limpiarFormulario();
            }
        } catch (SQLException e) {
            mostrarMensaje("Error al eliminar cliente: " + e.getMessage(), true);
        }
    }

    private void limpiarFormulario() {
        clienteSeleccionado = null;
        tblListaClientes.getSelectionModel().clearSelection();
        txtCliDocumento.clear();
        cmbCliTipoDocumento.setValue("CC");
        txtCliNombre.clear();
        txtCliCorreo.clear();
        txtCliTelefono.clear();
        cmbCliEstado.setValue("Activo");
        btnEliminarClienteForm.setDisable(true);
        limpiarErroresCampos();
        ocultarMensaje();
    }

    private void actualizarPlaceholder() {
        boolean vacio = listaClientes.isEmpty();
        boxPlaceholder.setVisible(vacio);
        boxPlaceholder.setManaged(vacio);
        lblConteoClientes.setText(listaClientes.size() + " cliente" + (listaClientes.size() == 1 ? "" : "s") + " encontrado" + (listaClientes.size() == 1 ? "" : "s"));
    }

    private void configurarTabla() {
        colIdCliente.setCellValueFactory(d -> new SimpleLongProperty(d.getValue().getCliId()));
        colDocumento.setCellValueFactory(d -> new SimpleStringProperty(valorSeguro(d.getValue().getCliDocumento())));
        colNombre.setCellValueFactory(d -> new SimpleStringProperty(valorSeguro(d.getValue().getCliNombre())));
        colCorreo.setCellValueFactory(d -> new SimpleStringProperty(valorSeguro(d.getValue().getCliCorreo())));
        colTelefono.setCellValueFactory(d -> new SimpleStringProperty(valorSeguro(d.getValue().getCliTelefono())));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(valorSeguro(d.getValue().getCliEstado())));
        tblListaClientes.setItems(listaClientes);
    }

    private void cargarClientes() {
        try {
            listaClientes.setAll(ClienteDAO.listarTodos());
            actualizarPlaceholder();
        } catch (SQLException e) {
            mostrarMensaje("Error cargando clientes: " + e.getMessage(), true);
        }
    }

    private void cargarEnFormulario(Cliente c) {
        clienteSeleccionado = c;
        txtCliDocumento.setText(valorSeguro(c.getCliDocumento()));
        cmbCliTipoDocumento.setValue(valorSeguro(c.getCliTipoDocumento()).isBlank() ? "CC" : c.getCliTipoDocumento());
        txtCliNombre.setText(valorSeguro(c.getCliNombre()));
        txtCliCorreo.setText(valorSeguro(c.getCliCorreo()));
        txtCliTelefono.setText(valorSeguro(c.getCliTelefono()));
        cmbCliEstado.setValue(valorSeguro(c.getCliEstado()).isBlank() ? "Activo" : c.getCliEstado());
        btnEliminarClienteForm.setDisable(false);
        ocultarMensaje();
    }

    private Cliente construirClienteDesdeFormulario() {
        Cliente c = new Cliente();
        c.setCliDocumento(txtCliDocumento.getText().trim());
        c.setCliTipoDocumento(cmbCliTipoDocumento.getValue() != null ? cmbCliTipoDocumento.getValue() : "CC");
        c.setCliNombre(txtCliNombre.getText().trim());
        c.setCliCorreo(txtCliCorreo.getText().trim());
        c.setCliTelefono(txtCliTelefono.getText().trim());
        c.setCliEstado(cmbCliEstado.getValue() != null ? cmbCliEstado.getValue() : "Activo");
        return c;
    }

    private String valorSeguro(String valor) {
        return valor != null ? valor : "";
    }

    private void mostrarMensaje(String texto, boolean esError) {
        lblMensajeCliente.setText(texto);
        lblMensajeCliente.setStyle(esError
                ? "-fx-text-fill: #C0392B; -fx-font-weight: bold;"
                : "-fx-text-fill: #27AE60; -fx-font-weight: bold;");
        lblMensajeCliente.setVisible(true);
        lblMensajeCliente.setManaged(true);
    }

    private void ocultarMensaje() {
        lblMensajeCliente.setText("");
        lblMensajeCliente.setVisible(false);
        lblMensajeCliente.setManaged(false);
    }

    private void configurarFloatingFields() {
        FloatingFieldHelper.bindComboBox(boxCliTipoDocumentoField, cmbCliTipoDocumento);
        FloatingFieldHelper.bindTextField(boxCliDocumentoField, txtCliDocumento);
        FloatingFieldHelper.bindTextField(boxCliNombreField, txtCliNombre);
        FloatingFieldHelper.bindTextField(boxCliCorreoField, txtCliCorreo);
        FloatingFieldHelper.bindTextField(boxCliTelefonoField, txtCliTelefono);
        FloatingFieldHelper.bindComboBox(boxCliEstadoField, cmbCliEstado);
    }

    private void limpiarErroresCampos() {
        if (lblCliDocumentoError != null) {
            lblCliDocumentoError.setText("");
            lblCliDocumentoError.setVisible(false);
            lblCliDocumentoError.setManaged(false);
        }
        FloatingFieldHelper.clearInvalid(
                boxCliTipoDocumentoField,
                boxCliDocumentoField,
                boxCliNombreField,
                boxCliCorreoField,
                boxCliTelefonoField,
                boxCliEstadoField
        );
    }

    private void mostrarErrorDocumento(String mensaje) {
        if (lblCliDocumentoError != null) {
            lblCliDocumentoError.setText(mensaje);
            lblCliDocumentoError.setVisible(true);
            lblCliDocumentoError.setManaged(true);
        }
        FloatingFieldHelper.setInvalid(boxCliDocumentoField, true);
    }

    private void marcarInvalido(StackPane contenedor, boolean invalido) {
        FloatingFieldHelper.setInvalid(contenedor, invalido);
    }
}
