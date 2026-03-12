package com.mosqueteros.proyecto_restaurante.controller;

import com.mosqueteros.proyecto_restaurante.dao.CategoriaPlatoDAO;
import com.mosqueteros.proyecto_restaurante.dao.EstadoDAO;
import com.mosqueteros.proyecto_restaurante.dao.PlatoDAO;
import com.mosqueteros.proyecto_restaurante.model.CategoriaPlato;
import com.mosqueteros.proyecto_restaurante.model.Estado;
import com.mosqueteros.proyecto_restaurante.model.Plato;
import com.mosqueteros.proyecto_restaurante.util.Alertas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.math.BigDecimal;
import java.util.List;
import javafx.scene.Node;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for managing Plates (Platos) in the restaurant system.
 * Handles database interactions via PlatoDAO and UI updates.
 */
public class PlatoController implements Initializable {

    @FXML private MFXTextField txtBuscarPlato;
    @FXML private MFXComboBox<CategoriaPlato> cmbFiltroCategoriaPlato;
    @FXML private MFXComboBox<Estado> cmbFiltroEstadoPlato;
    @FXML private MFXButton btnLimpiarFiltro;
    
    @FXML private TableView<Plato> tblListaPlatos;
    @FXML private TableColumn<Plato, Integer> colPlatId;
    @FXML private TableColumn<Plato, String> colPlatNombre;
    @FXML private TableColumn<Plato, String> colPlatCategoria;
    @FXML private TableColumn<Plato, BigDecimal> colPlatPrecio;
    @FXML private TableColumn<Plato, String> colPlatEstado;

    @FXML private MFXTextField txtPlatCodigo, txtPlatNombre, txtPlatPrecio, txtPlatCosto;
    @FXML private TextArea txtPlatDescripcion;
    @FXML private MFXComboBox<CategoriaPlato> cmbCategoriaPlato;
    @FXML private MFXComboBox<Estado> cmbEstadoPlato;
    
    @FXML private MFXButton btnGuardarPlato;
    @FXML private MFXButton btnCancelarPlato;
    @FXML private MFXButton btnDesactivarPlato;
    
    @FXML private VBox boxPlaceholder;
    @FXML private Label lblConteoPlatos;
    @FXML private Label lblTituloFormulario;
    @FXML private Label lblMensajePlato;

    private final ObservableList<Plato> platosObservable = FXCollections.observableArrayList();
    private Plato platoSeleccionado;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param rb The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarCategorias();
        cargarEstados();

        platosObservable.addListener((javafx.collections.ListChangeListener.Change<? extends Plato> c) -> {
            actualizarPlaceholder();
            actualizarContador();
        });

        cargarListado();
        actualizarPlaceholder();

        tblListaPlatos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                platoSeleccionado = newVal;
                mostrarDetallePlato(newVal);
            }
        });

        txtBuscarPlato.textProperty().addListener((obs, oldVal, newVal) -> filtrar());
        cmbFiltroCategoriaPlato.valueProperty().addListener((obs, oldVal, newVal) -> filtrar());
        cmbFiltroEstadoPlato.valueProperty().addListener((obs, oldVal, newVal) -> filtrar());
    }
    private void actualizarPlaceholder() {
        boolean vacio = platosObservable.isEmpty();
        boxPlaceholder.setVisible(vacio);
        boxPlaceholder.setManaged(vacio);
    }

    private void actualizarContador() {
        lblConteoPlatos.setText("Mostrando " + platosObservable.size() + " platos");
    }

    private void configurarTabla() {
        colPlatId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPlatNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPlatCategoria.setCellValueFactory(new PropertyValueFactory<>("categoriaDesc"));
        colPlatPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colPlatEstado.setCellValueFactory(new PropertyValueFactory<>("estadoDesc"));
        tblListaPlatos.setItems(platosObservable);
    }

    private void cargarCategorias() {
        List<CategoriaPlato> categorias = CategoriaPlatoDAO.listarTodas();
        cmbFiltroCategoriaPlato.setItems(FXCollections.observableArrayList(categorias));
        cmbCategoriaPlato.setItems(FXCollections.observableArrayList(categorias));
    }
    private void cargarEstados() {
        List<Estado> estados = EstadoDAO.listarPorTipo(3);
        ObservableList<Estado> estadosObservable = FXCollections.observableArrayList(estados);
        cmbFiltroEstadoPlato.setItems(estadosObservable);
        cmbEstadoPlato.setItems(estadosObservable);
    }

    private void cargarListado() {
        List<Plato> lista = PlatoDAO.listarTodos();
        platosObservable.setAll(lista);
    }

    private void mostrarDetallePlato(Plato plato) {
        lblTituloFormulario.setText("Editar Plato");
        txtPlatCodigo.setText(plato.getCodigo());
        txtPlatNombre.setText(plato.getNombre());
        txtPlatPrecio.setText(plato.getPrecio().toString());
        txtPlatCosto.setText(plato.getCosto() != null ? plato.getCosto().toString() : "0.00");
        txtPlatDescripcion.setText(plato.getDescripcion());
        
        for (CategoriaPlato cat : cmbCategoriaPlato.getItems()) {
            if (cat.getId() != null && cat.getId().equals(plato.getCategoriaPlatoId())) {
                cmbCategoriaPlato.selectItem(cat);
                break;
            }
        }

        for (Estado est : cmbEstadoPlato.getItems()) {
            if (est.getId() != null && est.getId().longValue() == plato.getEstId()) {
                cmbEstadoPlato.selectItem(est);
                break;
            }
        }
    }

    @FXML
    private void prepararNuevoPlato() {
        lblTituloFormulario.setText("Nuevo Plato");
        platoSeleccionado = null;
        txtPlatCodigo.setText(PlatoDAO.obtenerSiguienteCodigo());
        txtPlatNombre.clear();
        txtPlatPrecio.clear();
        txtPlatCosto.clear();
        txtPlatDescripcion.clear();
        cmbCategoriaPlato.clearSelection();
        cmbEstadoPlato.clearSelection();
        tblListaPlatos.getSelectionModel().clearSelection();
    }

    /**
     * Saves a new plate or updates an existing one.
     */
    @FXML
    private void guardarPlato() {
        try {
            String codigo = txtPlatCodigo.getText();
            String nombre = txtPlatNombre.getText();
            BigDecimal precio = new BigDecimal(txtPlatPrecio.getText());
            BigDecimal costo  = txtPlatCosto.getText().isEmpty()
                                ? BigDecimal.ZERO
                                : new BigDecimal(txtPlatCosto.getText());
            CategoriaPlato cat = cmbCategoriaPlato.getValue();
            Estado est         = cmbEstadoPlato.getValue();

            if (nombre.isEmpty() || cat == null) {
                mostrarAlerta("Error", "El nombre y la categoría son obligatorias.");
                return;
            }

            Integer estadoId = (est != null) ? est.getId() : 7;

            boolean exito;
            if (platoSeleccionado == null) {
                exito = PlatoDAO.insertar(nombre, codigo, precio, costo, cat.getId(), (long) estadoId);
            } else {
                exito = PlatoDAO.actualizar(
                    platoSeleccionado.getId(), nombre, codigo,
                    precio, costo, cat.getId(), (long) estadoId
                );
            }

            if (exito) {
                cargarListado();
                prepararNuevoPlato();
                mostrarAlerta("Éxito", "Plato guardado correctamente.");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El precio debe ser un número válido.");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void desactivarPlato() {
        if (platoSeleccionado == null) return;

        // Capturamos el plato en una variable final para usarla en el lambda
        final Plato plato = platoSeleccionado;

        Alertas.confirmar(
            obtenerNodoParaAlerta(),
            "Confirmar Inactivación",
            "¿Desea inactivar el plato \"" + plato.getNombre() + "\"?",
            () -> {
                // Este bloque se ejecuta SOLO si el usuario presiona Aceptar
                if (PlatoDAO.cambiarEstado(plato.getId(), 9)) {
                    cargarListado();
                    prepararNuevoPlato();
                    mostrarAlerta("Éxito", "Plato inactivado correctamente.");
                } else {
                    mostrarAlerta("Error", "No se pudo inactivar el plato.");
                }
            }
        );
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscarPlato.clear();
        cmbFiltroCategoriaPlato.clearSelection();
        cmbFiltroEstadoPlato.clearSelection();
        filtrar();
    }

    /** Filtra los platos en tiempo real según texto, categoría y estado. */
    private void filtrar() {
        String busqueda       = txtBuscarPlato.getText().toLowerCase();
        CategoriaPlato catFiltro = cmbFiltroCategoriaPlato.getValue();
        Estado estFiltro      = cmbFiltroEstadoPlato.getValue();

        List<Plato> listaFiltrada = PlatoDAO.listarTodos().stream().filter(p -> {
            boolean coincideBusqueda = p.getNombre().toLowerCase().contains(busqueda)
                                    || p.getCodigo().toLowerCase().contains(busqueda);
            boolean coincideCat  = (catFiltro == null) || (p.getCategoriaPlatoId() == (long) catFiltro.getId());
            boolean coincideEst  = (estFiltro == null) || (p.getEstId() == (long) estFiltro.getId());
            return coincideBusqueda && coincideCat && coincideEst;
        }).toList();

        platosObservable.setAll(listaFiltrada);
    }

    @FXML
    private void cancelarEdicion() {
        prepararNuevoPlato();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Node nodo = obtenerNodoParaAlerta();
        String t  = titulo.toLowerCase();

        if (t.contains("éxito") || t.contains("correcto") || t.contains("guardado")) {
            Alertas.exito(nodo, titulo, contenido);
        } else if (t.contains("error") || t.contains("fallo") || t.contains("falló")) {
            Alertas.error(nodo, titulo, contenido);
        } else if (t.contains("advertencia") || t.contains("aviso") || t.contains("atención")) {
            Alertas.aviso(nodo, titulo, contenido);
        } else {
            Alertas.informacion(nodo, titulo, contenido);
        }
    }

    private Node obtenerNodoParaAlerta() {
        if (btnGuardarPlato != null) return btnGuardarPlato;
        if (tblListaPlatos  != null) return tblListaPlatos;
        if (txtPlatNombre   != null) return txtPlatNombre;
        return null;
    }
}
