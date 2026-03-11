package com.mosqueteros.proyecto_restaurante.controller;

import com.mosqueteros.proyecto_restaurante.dao.CategoriaPlatoDAO;
import com.mosqueteros.proyecto_restaurante.dao.EstadoDAO;
import com.mosqueteros.proyecto_restaurante.dao.PlatoDAO;
import com.mosqueteros.proyecto_restaurante.model.CategoriaPlato;
import com.mosqueteros.proyecto_restaurante.model.Estado;
import com.mosqueteros.proyecto_restaurante.model.Plato;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.math.BigDecimal;
import java.util.List;

public class PlatoController {

    @FXML private MFXTextField txtBuscarPlato;
    @FXML private MFXComboBox<CategoriaPlato> cmbFiltroCategoriaPlato;
    @FXML private MFXComboBox<Estado> cmbFiltroEstadoPlato;
    
    @FXML private TableView<Plato> tblListaPlatos;
    @FXML private TableColumn<Plato, Integer> colPlatId;
    @FXML private TableColumn<Plato, String> colPlatNombre;
    @FXML private TableColumn<Plato, String> colPlatCategoria;
    @FXML private TableColumn<Plato, BigDecimal> colPlatPrecio;
    @FXML private TableColumn<Plato, String> colPlatEstado;

    @FXML private MFXTextField txtPlatCodigo;
    @FXML private MFXTextField txtPlatNombre;
    @FXML private MFXTextField txtPlatPrecio;
    @FXML private MFXTextField txtPlatCosto;
    @FXML private MFXComboBox<CategoriaPlato> cmbCategoriaPlato;
    @FXML private MFXComboBox<Estado> cmbEstadoPlato;
    
    @FXML private MFXButton btnGuardarPlato;
    @FXML private MFXButton btnCancelarPlato;
    @FXML private MFXButton btnDesactivarPlato;

    private final ObservableList<Plato> platosObservable = FXCollections.observableArrayList();
    private Plato platoSeleccionado;

    @FXML
    public void initialize() {
        configurarTabla();
        cargarCategorias();
        cargarEstados();
        cargarListado();

        tblListaPlatos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                platoSeleccionado = newVal;
                mostrarDetallePlato(newVal);
            }
        });

        // Filtros en tiempo real
        txtBuscarPlato.textProperty().addListener((obs, oldVal, newVal) -> filtrar());
        cmbFiltroCategoriaPlato.valueProperty().addListener((obs, oldVal, newVal) -> filtrar());
        cmbFiltroEstadoPlato.valueProperty().addListener((obs, oldVal, newVal) -> filtrar());
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
        cmbFiltroEstadoPlato.setItems(FXCollections.observableArrayList(estados));
    }

    private void cargarListado() {
        List<Plato> lista = PlatoDAO.listarTodos();
        platosObservable.setAll(lista);
    }

    private void mostrarDetallePlato(Plato plato) {
        txtPlatCodigo.setText(plato.obtenerCodigo());
        txtPlatNombre.setText(plato.obtenerNombre());
        txtPlatPrecio.setText(plato.obtenerPrecio().toString());
        txtPlatCosto.setText(plato.obtenerCosto() != null ? plato.obtenerCosto().toString() : "0.00");
        
        for (CategoriaPlato cat : cmbCategoriaPlato.getItems()) {
            if (cat.obtenerId().equals(plato.obtenerCategoriaPlatoId())) {
                cmbCategoriaPlato.selectItem(cat);
                break;
            }
        }

        for (Estado est : cmbEstadoPlato.getItems()) {
            if (est.obtenerId().equals(plato.obtenerEstId())) {
                cmbEstadoPlato.selectItem(est);
                break;
            }
        }
    }

    @FXML
    private void prepararNuevoPlato() {
        platoSeleccionado = null;
        txtPlatCodigo.clear();
        txtPlatNombre.clear();
        txtPlatPrecio.clear();
        txtPlatCosto.clear();
        cmbCategoriaPlato.getSelectionModel().clearSelection();
        cmbEstadoPlato.getSelectionModel().clearSelection();
        tblListaPlatos.getSelectionModel().clearSelection();
    }

    @FXML
    private void guardarPlato() {
        try {
            String codigo = txtPlatCodigo.getText();
            String nombre = txtPlatNombre.getText();
            BigDecimal precio = new BigDecimal(txtPlatPrecio.getText());
            BigDecimal costo = txtPlatCosto.getText().isEmpty() ? BigDecimal.ZERO : new BigDecimal(txtPlatCosto.getText());
            CategoriaPlato cat = cmbCategoriaPlato.getValue();
            Estado est = cmbEstadoPlato.getValue();

            if (nombre.isEmpty() || cat == null) {
                mostrarAlerta("Error", "El nombre y la categoría son obligatorios.");
                return;
            }

            Integer estadoId = (est != null) ? est.obtenerId() : 7; // 7 = Activo por defecto

            boolean exito;
            if (platoSeleccionado == null) {
                exito = PlatoDAO.insertar(nombre, codigo, precio, costo, cat.obtenerId(), estadoId);
            } else {
                exito = PlatoDAO.actualizar(platoSeleccionado.obtenerId(), nombre, codigo, precio, costo, cat.obtenerId(), estadoId);
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

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Inactivación");
        alert.setHeaderText("¿Desea inactivar el plato " + platoSeleccionado.obtenerNombre() + "?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (PlatoDAO.cambiarEstado(platoSeleccionado.obtenerId(), 9)) {
                cargarListado();
                prepararNuevoPlato();
            }
        }
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscarPlato.clear();
        cmbFiltroCategoriaPlato.getSelectionModel().clearSelection();
        cmbFiltroEstadoPlato.getSelectionModel().clearSelection();
        filtrar();
    }

    private void filtrar() {
        String busqueda = txtBuscarPlato.getText().toLowerCase();
        CategoriaPlato catFiltro = cmbFiltroCategoriaPlato.getValue();
        Estado estFiltro = cmbFiltroEstadoPlato.getValue();

        List<Plato> listaFiltrada = PlatoDAO.listarTodos().stream().filter(p -> {
            boolean coincideBusqueda = p.obtenerNombre().toLowerCase().contains(busqueda) || 
                                     p.obtenerCodigo().toLowerCase().contains(busqueda);
            boolean coincideCat = (catFiltro == null) || (p.obtenerCategoriaPlatoId() == catFiltro.obtenerId());
            boolean coincideEst = (estFiltro == null) || (p.obtenerEstId() == estFiltro.obtenerId());
            
            return coincideBusqueda && coincideCat && coincideEst;
        }).toList();

        platosObservable.setAll(listaFiltrada);
    }

    @FXML
    private void cancelarEdicion() {
        prepararNuevoPlato();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
