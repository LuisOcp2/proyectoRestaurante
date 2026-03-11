package com.mosqueteros.proyecto_restaurante.controller;

import com.mosqueteros.proyecto_restaurante.dao.AreaMesaDAO;
import com.mosqueteros.proyecto_restaurante.dao.EstadoDAO;
import com.mosqueteros.proyecto_restaurante.dao.MesaDAO;
import com.mosqueteros.proyecto_restaurante.dao.SedeDAO;
import com.mosqueteros.proyecto_restaurante.model.AreaMesa;
import com.mosqueteros.proyecto_restaurante.model.Estado;
import com.mosqueteros.proyecto_restaurante.model.Mesa;
import com.mosqueteros.proyecto_restaurante.model.Sede;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;

import java.util.List;

public class MesaController {

    @FXML private ComboBox<Sede> cmbFiltroSede;
    @FXML private ComboBox<AreaMesa> cmbFiltroArea;
    @FXML private ComboBox<Estado> cmbFiltroEstado;
    
    @FXML private TableView<Mesa> tblListaMesas;
    @FXML private TableColumn<Mesa, Integer> colNumero;
    @FXML private TableColumn<Mesa, Integer> colCapacidad;
    @FXML private TableColumn<Mesa, String> colSede;
    @FXML private TableColumn<Mesa, String> colArea;
    @FXML private TableColumn<Mesa, String> colEstado;

    @FXML private MFXTextField txtMesNumero;
    @FXML private MFXTextField txtMesCapacidad;
    @FXML private MFXComboBox<Sede> cmbSedeMesa;
    @FXML private MFXComboBox<AreaMesa> cmbAreaMesa;
    @FXML private MFXComboBox<Estado> cmbEstadoMesa;

    @FXML private MFXButton btnGuardarMesa;
    @FXML private MFXButton btnNuevoMesa;
    @FXML private MFXButton btnEditarMesa;
    @FXML private MFXButton btnEliminarMesa;
    @FXML private MFXButton btnBuscarMesa;
    @FXML private MFXButton btnLimpiarFiltroMesa;
    @FXML private MFXTextField txtBuscarMesa;

    private final ObservableList<Mesa> mesasObservable = FXCollections.observableArrayList();
    private Mesa mesaSeleccionada;

    @FXML
    public void initialize() {
        configurarTabla();
        cargarSedes();
        cargarAreas();
        cargarEstados();
        cargarListado();

        tblListaMesas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                mesaSeleccionada = newVal;
                mostrarDetalleMesa(newVal);
            }
        });

        cmbFiltroSede.valueProperty().addListener((obs, oldVal, newVal) -> filtrar());
        cmbFiltroArea.valueProperty().addListener((obs, oldVal, newVal) -> filtrar());
        cmbFiltroEstado.valueProperty().addListener((obs, oldVal, newVal) -> filtrar());
    }

    private void configurarTabla() {
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colSede.setCellValueFactory(new PropertyValueFactory<>("sedeNombre"));
        colArea.setCellValueFactory(new PropertyValueFactory<>("areaDesc"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoDesc"));
        tblListaMesas.setItems(mesasObservable);
    }

    private void cargarSedes() {
        List<Sede> sedes = SedeDAO.listarTodas();
        cmbFiltroSede.setItems(FXCollections.observableArrayList(sedes));
        cmbSedeMesa.setItems(FXCollections.observableArrayList(sedes));
    }

    private void cargarAreas() {
        List<AreaMesa> areas = AreaMesaDAO.listarTodas();
        cmbFiltroArea.setItems(FXCollections.observableArrayList(areas));
        cmbAreaMesa.setItems(FXCollections.observableArrayList(areas));
    }

    private void cargarEstados() {
        List<Estado> estados = EstadoDAO.listarPorTipo(5); 
        cmbFiltroEstado.setItems(FXCollections.observableArrayList(estados));
        if (cmbEstadoMesa != null) {
            cmbEstadoMesa.setItems(FXCollections.observableArrayList(estados));
        }
    }

    private void cargarListado() {
        List<Mesa> lista = MesaDAO.listarTodas();
        mesasObservable.setAll(lista);
    }

    private void mostrarDetalleMesa(Mesa mesa) {
        txtMesNumero.setText(String.valueOf(mesa.obtenerNumero()));
        txtMesCapacidad.setText(String.valueOf(mesa.obtenerCapacidad()));
        
        for (Sede s : cmbSedeMesa.getItems()) {
            if (s.obtenerId().equals(mesa.obtenerSedeId())) {
                cmbSedeMesa.selectItem(s);
                break;
            }
        }
        
        for (AreaMesa a : cmbAreaMesa.getItems()) {
            if (a.obtenerId().equals(mesa.obtenerAreaMesaId())) {
                cmbAreaMesa.selectItem(a);
                break;
            }
        }

        if (cmbEstadoMesa != null) {
            for (Estado e : cmbEstadoMesa.getItems()) {
                if (e.obtenerId().equals(mesa.obtenerEstId())) {
                    cmbEstadoMesa.selectItem(e);
                    break;
                }
            }
        }
        btnEliminarMesa.setDisable(false);
    }

    @FXML
    private void prepararNuevaMesa() {
        mesaSeleccionada = null;
        txtMesNumero.clear();
        txtMesCapacidad.clear();
        cmbSedeMesa.getSelectionModel().clearSelection();
        cmbAreaMesa.getSelectionModel().clearSelection();
        if (cmbEstadoMesa != null) cmbEstadoMesa.getSelectionModel().clearSelection();
        tblListaMesas.getSelectionModel().clearSelection();
        btnEliminarMesa.setDisable(true);
    }

    @FXML
    private void guardarMesa() {
        try {
            int numero = Integer.parseInt(txtMesNumero.getText());
            int capacidad = Integer.parseInt(txtMesCapacidad.getText());
            Sede sede = cmbSedeMesa.getValue();
            AreaMesa area = cmbAreaMesa.getValue();
            Estado est = (cmbEstadoMesa != null) ? cmbEstadoMesa.getValue() : null;

            if (area == null || sede == null) {
                mostrarAlerta("Error", "El área y la sede son obligatorias.");
                return;
            }

            long sedeId = sede.obtenerId();
            long areaMesaId = area.obtenerId();
            long estadoId = (est != null) ? est.obtenerId() : 15; // 15 = Libre

            boolean exito;
            if (mesaSeleccionada == null) {
                exito = MesaDAO.insertar(numero, capacidad, areaMesaId, sedeId, estadoId);
            } else {
                exito = MesaDAO.actualizar(mesaSeleccionada.obtenerId(), numero, capacidad, areaMesaId, sedeId, estadoId);
            }

            if (exito) {
                mostrarAlerta("Éxito", "Mesa guardada correctamente.");
                cargarListado();
                prepararNuevaMesa();
            } else {
                mostrarAlerta("Error", "No se pudo guardar la mesa.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Formato numérico inválido.");
        }
    }

    @FXML
    private void desactivarMesa() {
        if (mesaSeleccionada == null) return;

        if (MesaDAO.tienePedidosActivos(mesaSeleccionada.obtenerId())) {
            mostrarAlerta("Acción denegada", "No se puede inactivar una mesa con pedidos activos.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Inactivación");
        alert.setHeaderText("¿Desea inactivar la mesa #" + mesaSeleccionada.obtenerNumero() + "?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            if (MesaDAO.cambiarEstado(mesaSeleccionada.obtenerId(), 15)) {
                cargarListado();
                prepararNuevaMesa();
            }
        }
    }

    @FXML
    private void editarMesa() {
        if (mesaSeleccionada != null) {
            mostrarDetalleMesa(mesaSeleccionada);
        }
    }

    @FXML
    private void eliminarMesa() {
        desactivarMesa();
    }

    @FXML
    private void buscarMesa() {
        filtrar();
    }

    @FXML
    private void limpiarFiltrosMesa() {
        cmbFiltroSede.getSelectionModel().clearSelection();
        cmbFiltroArea.getSelectionModel().clearSelection();
        cmbFiltroEstado.getSelectionModel().clearSelection();
        filtrar();
    }

    private void filtrar() {
        Sede sedeFiltro = cmbFiltroSede.getValue();
        AreaMesa areaFiltro = cmbFiltroArea.getValue();
        Estado estFiltro = cmbFiltroEstado.getValue();

        List<Mesa> listaFiltrada = MesaDAO.listarTodas().stream().filter(m -> {
            boolean coincideSede = (sedeFiltro == null) || (m.obtenerSedeId() == sedeFiltro.obtenerId());
            boolean coincideArea = (areaFiltro == null) || (m.obtenerAreaMesaId() == areaFiltro.obtenerId());
            boolean coincideEst = (estFiltro == null) || (m.obtenerEstId() == estFiltro.obtenerId());
            
            return coincideSede && coincideArea && coincideEst;
        }).toList();

        mesasObservable.setAll(listaFiltrada);
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}