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

public class PlatoController {

    @FXML private MFXTextField txtBuscarPlato;
    @FXML private ComboBox<CategoriaPlato> cmbFiltroCategoriaPlato;
    @FXML private ComboBox<Estado> cmbFiltroEstadoPlato;
    
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
    @FXML private VBox boxPlaceholder;

    private final ObservableList<Plato> platosObservable = FXCollections.observableArrayList();
    private Plato platoSeleccionado;

    @FXML
    public void initialize() {
        configurarTabla();
        cargarCategorias();
        cargarEstados();

        // Registrar listener ANTES de cargar datos para detectar el estado inicial
        platosObservable.addListener((javafx.collections.ListChangeListener<Plato>) c -> {
            actualizarPlaceholder();
        });

        cargarListado();
        // Forzar estado inicial por si el listener no disparó
        actualizarPlaceholder();

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

    /** Muestra u oculta el placeholder cuando la tabla está vacía. */
    private void actualizarPlaceholder() {
        boolean vacio = platosObservable.isEmpty();
        boxPlaceholder.setVisible(vacio);
        boxPlaceholder.setManaged(vacio);
    }

    /** Configura las columnas de la tabla con sus PropertyValueFactory. */
    private void configurarTabla() {
        colPlatId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPlatNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPlatCategoria.setCellValueFactory(new PropertyValueFactory<>("categoriaDesc"));
        colPlatPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colPlatEstado.setCellValueFactory(new PropertyValueFactory<>("estadoDesc"));
        tblListaPlatos.setItems(platosObservable);
    }

    /** Carga las categorías de plato en los combos de filtro y formulario. */
    private void cargarCategorias() {
        List<CategoriaPlato> categorias = CategoriaPlatoDAO.listarTodas();
        cmbFiltroCategoriaPlato.setItems(FXCollections.observableArrayList(categorias));
        cmbCategoriaPlato.setItems(FXCollections.observableArrayList(categorias));
    }

    /** Carga los estados de tipo plato (tipo 3) en los combos. */
    private void cargarEstados() {
        List<Estado> estados = EstadoDAO.listarPorTipo(3);
        ObservableList<Estado> estadosObservable = FXCollections.observableArrayList(estados);
        cmbFiltroEstadoPlato.setItems(estadosObservable);
        cmbEstadoPlato.setItems(estadosObservable);
    }

    /** Recarga la lista completa de platos desde la base de datos. */
    private void cargarListado() {
        List<Plato> lista = PlatoDAO.listarTodos();
        platosObservable.setAll(lista);
    }

    /** Rellena el formulario con los datos del plato seleccionado. */
    private void mostrarDetallePlato(Plato plato) {
        txtPlatCodigo.setText(plato.getCodigo());
        txtPlatNombre.setText(plato.getNombre());
        txtPlatPrecio.setText(plato.getPrecio().toString());
        txtPlatCosto.setText(plato.getCosto() != null ? plato.getCosto().toString() : "0.00");
        
        for (CategoriaPlato cat : cmbCategoriaPlato.getItems()) {
            if (cat.getId().equals(plato.getCategoriaPlatoId())) {
                cmbCategoriaPlato.selectItem(cat);
                break;
            }
        }

        for (Estado est : cmbEstadoPlato.getItems()) {
            if (est.getId().equals((int) plato.getEstId())) {
                cmbEstadoPlato.selectItem(est);
                break;
            }
        }
    }

    /** Limpia el formulario y prepara los campos para crear un nuevo plato. */
    @FXML
    private void prepararNuevoPlato() {
        platoSeleccionado = null;
        txtPlatCodigo.setText(PlatoDAO.obtenerSiguienteCodigo());
        txtPlatNombre.clear();
        txtPlatPrecio.clear();
        txtPlatCosto.clear();
        cmbCategoriaPlato.getSelectionModel().clearSelection();
        cmbEstadoPlato.getSelectionModel().clearSelection();
        tblListaPlatos.getSelectionModel().clearSelection();
    }

    /** Valida los campos y guarda o actualiza el plato en la base de datos. */
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

            // 7 = Activo por defecto si no se seleccionó estado
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

    /**
     * Solicita confirmación al usuario y cambia el estado del plato a Inactivo.
     *
     * CORRECCIÓN: se reemplazó el Alert nativo de JavaFX por Alertas.confirmar()
     * pasando el nodoOrigen. Esto evita que la ventana principal se minimice,
     * ya que el diálogo queda vinculado al Stage padre mediante initOwner()
     * y Modality.APPLICATION_MODAL.
     */
    @FXML
    private void desactivarPlato() {
        if (platoSeleccionado == null) return;

        Node nodo = obtenerNodoParaAlerta();
        boolean confirmado = Alertas.confirmar(
            nodo,
            "Confirmar Inactivación",
            "¿Desea inactivar el plato \"" + platoSeleccionado.getNombre() + "\"?"
        );

        if (confirmado) {
            if (PlatoDAO.cambiarEstado(platoSeleccionado.getId(), 9)) {
                cargarListado();
                prepararNuevoPlato();
            }
        }
    }

    /** Limpia los filtros de búsqueda y recarga la lista completa. */
    @FXML
    private void limpiarFiltros() {
        txtBuscarPlato.clear();
        cmbFiltroCategoriaPlato.getSelectionModel().clearSelection();
        cmbFiltroEstadoPlato.getSelectionModel().clearSelection();
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

    /** Cancela la edición y limpia el formulario. */
    @FXML
    private void cancelarEdicion() {
        prepararNuevoPlato();
    }

    /**
     * Determina el tipo de alerta según el título y delega a la clase Alertas,
     * pasando siempre un nodo origen para que el Stage del diálogo quede
     * vinculado al Stage padre (evita minimización).
     */
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

    /**
     * Obtiene el primer nodo @FXML disponible para localizar el Stage padre.
     * El orden de prioridad va de lo más específico (botón de acción) a lo
     * más general (tabla, campo de texto). Alertas usa este nodo para
     * llamar a initOwner() y evitar la minimización de la ventana.
     */
    private Node obtenerNodoParaAlerta() {
        if (btnGuardarPlato != null) return btnGuardarPlato;
        if (tblListaPlatos  != null) return tblListaPlatos;
        if (txtPlatNombre   != null) return txtPlatNombre;
        return null; // Alertas busca el Stage activo como fallback
    }
}
