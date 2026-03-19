package com.mosqueteros.proyecto_restaurante.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class CategoriaInsumoController {

    @FXML private TextField txtBuscarCategoria;
    @FXML private Button btnBuscarCategoria;
    @FXML private Button btnLimpiarFiltroCategoria;
    @FXML private TableView<?> tablaCategorias;
    
    private ObservableList<?> listaCategorias = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cargarCategorias();
    }

    @FXML
    private void buscarCategoria() {
        String filtro = txtBuscarCategoria.getText().trim();
        if (filtro.isEmpty()) {
            cargarCategorias();
        } else {
            // Implementar lógica de búsqueda según sea necesario
            System.out.println("Buscando categoría: " + filtro);
        }
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscarCategoria.clear();
        cargarCategorias();
    }

    private void cargarCategorias() {
        // Implementar lógica para cargar categorías
        System.out.println("Cargando categorías...");
    }
}
