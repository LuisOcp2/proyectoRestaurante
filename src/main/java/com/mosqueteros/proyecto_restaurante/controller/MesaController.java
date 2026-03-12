package com.mosqueteros.proyecto_restaurante.controller;

import com.mosqueteros.proyecto_restaurante.dao.AreaMesaDAO;
import com.mosqueteros.proyecto_restaurante.dao.MesaDAO;
import com.mosqueteros.proyecto_restaurante.dao.SedeDAO;
import com.mosqueteros.proyecto_restaurante.model.AreaMesa;
import com.mosqueteros.proyecto_restaurante.model.Mesa;
import com.mosqueteros.proyecto_restaurante.model.Sede;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import com.mosqueteros.proyecto_restaurante.util.Alertas;
import javafx.scene.Node;
import java.util.List;

public class MesaController {

    // ── Filtros de listado ────────────────────────────────────────────────────
    @FXML private ComboBox<Sede>     cmbFiltroSede;
    @FXML private ComboBox<AreaMesa> cmbFiltroArea;
    @FXML private ComboBox<String>   cmbFiltroEstado;

    // ── Tabla ─────────────────────────────────────────────────────────────────
    @FXML private TableView<Mesa>            tblListaMesas;
    @FXML private TableColumn<Mesa, String>  colNumero;
    @FXML private TableColumn<Mesa, Integer> colCapacidad;
    @FXML private TableColumn<Mesa, String>  colSede;
    @FXML private TableColumn<Mesa, String>  colArea;
    @FXML private TableColumn<Mesa, String>  colEstado;

    // ── Formulario ────────────────────────────────────────────────────────────
    @FXML private MFXTextField          txtMesNumero;
    @FXML private MFXTextField          txtMesCapacidad;
    @FXML private MFXComboBox<Sede>     cmbSedeMesa;
    @FXML private MFXComboBox<AreaMesa> cmbAreaMesa;
    @FXML private MFXComboBox<String>   cmbEstadoMesa;

    // ── Botones ───────────────────────────────────────────────────────────────
    @FXML private MFXButton   btnGuardarMesa;
    @FXML private MFXButton   btnNuevoMesa;
    @FXML private MFXButton   btnEditarMesa;
    @FXML private MFXButton   btnEliminarMesaForm;
    @FXML private MFXButton   btnLimpiarFiltro;
    @FXML private MFXTextField txtBuscarMesa;
    @FXML private VBox         boxPlaceholder;

    /** Estados posibles para la mesa según el ENUM de la BD. */
    private static final ObservableList<String> ESTADOS_MESA =
        FXCollections.observableArrayList("Disponible", "Ocupada", "Reservada", "Inactiva");

    private final ObservableList<Mesa> mesasObservable = FXCollections.observableArrayList();
    private Mesa mesaSeleccionada;

    @FXML
    public void initialize() {
        configurarTabla();
        cargarSedes();
        cargarAreas();
        cargarEstados();

        // Registrar listener ANTES de cargar datos para detectar el estado inicial
        mesasObservable.addListener((javafx.collections.ListChangeListener<Mesa>) c -> {
            actualizarPlaceholder();
        });

        cargarListado();
        // Forzar estado inicial por si el listener no disparó
        actualizarPlaceholder();

        tblListaMesas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                mesaSeleccionada = newVal;
                mostrarDetalleMesa(newVal);
            }
        });

        txtBuscarMesa.textProperty().addListener((obs, oldVal, newVal) -> filtrar());
        cmbFiltroSede.valueProperty().addListener((obs, oldVal, newVal) -> filtrar());
        cmbFiltroArea.valueProperty().addListener((obs, oldVal, newVal) -> filtrar());
        cmbFiltroEstado.valueProperty().addListener((obs, oldVal, newVal) -> filtrar());
    }

    /** Muestra u oculta el placeholder cuando la tabla está vacía. */
    private void actualizarPlaceholder() {
        boolean vacio = mesasObservable.isEmpty();
        boxPlaceholder.setVisible(vacio);
        boxPlaceholder.setManaged(vacio);
    }

    /** Configura las columnas de la tabla con sus PropertyValueFactory. */
    private void configurarTabla() {
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colSede.setCellValueFactory(new PropertyValueFactory<>("sedeNombre"));
        colArea.setCellValueFactory(new PropertyValueFactory<>("areaNombre"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        tblListaMesas.setItems(mesasObservable);
    }

    /** Carga las sedes en los combos de filtro y formulario. */
    private void cargarSedes() {
        List<Sede> sedes = SedeDAO.listarTodas();
        cmbFiltroSede.setItems(FXCollections.observableArrayList(sedes));
        cmbSedeMesa.setItems(FXCollections.observableArrayList(sedes));
    }

    /** Carga las áreas de mesa en los combos de filtro y formulario. */
    private void cargarAreas() {
        List<AreaMesa> areas = AreaMesaDAO.listarTodas();
        cmbFiltroArea.setItems(FXCollections.observableArrayList(areas));
        cmbAreaMesa.setItems(FXCollections.observableArrayList(areas));
    }

    /** Carga los estados posibles de mesa en los combos. */
    private void cargarEstados() {
        cmbFiltroEstado.setItems(ESTADOS_MESA);
        if (cmbEstadoMesa != null) {
            cmbEstadoMesa.setItems(ESTADOS_MESA);
        }
    }

    /** Recarga la lista completa de mesas desde la base de datos. */
    private void cargarListado() {
        List<Mesa> lista = MesaDAO.listarTodas();
        mesasObservable.setAll(lista);
    }

    /** Rellena el formulario con los datos de la mesa seleccionada. */
    private void mostrarDetalleMesa(Mesa mesa) {
        txtMesNumero.setText(mesa.getNumero());
        txtMesCapacidad.setText(String.valueOf(mesa.getCapacidad()));

        for (Sede s : cmbSedeMesa.getItems()) {
            if (s.getId() == mesa.getSedeId()) {
                cmbSedeMesa.selectItem(s);
                break;
            }
        }

        for (AreaMesa a : cmbAreaMesa.getItems()) {
            if (a.getId() == mesa.getAreaId()) {
                cmbAreaMesa.selectItem(a);
                break;
            }
        }

        if (cmbEstadoMesa != null) {
            cmbEstadoMesa.selectItem(mesa.getEstado());
        }

        btnEliminarMesaForm.setDisable(false);
    }

    /** Limpia el formulario y prepara los campos para crear una nueva mesa. */
    @FXML
    private void prepararNuevaMesa() {
        mesaSeleccionada = null;
        txtMesNumero.clear();
        txtMesCapacidad.clear();
        cmbSedeMesa.getSelectionModel().clearSelection();
        cmbAreaMesa.getSelectionModel().clearSelection();
        if (cmbEstadoMesa != null) cmbEstadoMesa.getSelectionModel().clearSelection();
        tblListaMesas.getSelectionModel().clearSelection();
        btnEliminarMesaForm.setDisable(true);
    }

    /** Valida los campos y guarda o actualiza la mesa en la base de datos. */
    @FXML
    private void guardarMesa() {
        try {
            String   numero   = txtMesNumero.getText().trim();
            int      capacidad = Integer.parseInt(txtMesCapacidad.getText().trim());
            Sede     sede     = cmbSedeMesa.getValue();
            AreaMesa area     = cmbAreaMesa.getValue();
            String   estado   = (cmbEstadoMesa != null) ? cmbEstadoMesa.getValue() : "Disponible";

            if (numero.isEmpty() || sede == null) {
                mostrarAlerta("Error", "El número y la sede son obligatorios.");
                return;
            }

            long sedeId = sede.getId();
            long areaId = (area != null && area.getId() != null) ? area.getId() : 0L;
            if (estado == null) estado = "Disponible";

            boolean exito;
            if (mesaSeleccionada == null) {
                exito = MesaDAO.insertar(numero, capacidad, areaId, sedeId);
            } else {
                exito = MesaDAO.actualizar(mesaSeleccionada.getId(), numero, capacidad, areaId, sedeId, estado);
            }

            if (exito) {
                mostrarAlerta("Éxito", "Mesa guardada correctamente.");
                cargarListado();
                prepararNuevaMesa();
            } else {
                mostrarAlerta("Error", "No se pudo guardar la mesa.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "La capacidad debe ser un número válido.");
        }
    }

    /**
     * Verifica si la mesa tiene pedidos activos, solicita confirmación
     * y cambia el estado de la mesa a Inactiva.
     *
     * CORRECCIÓN: se reemplazó el Alert nativo de JavaFX por Alertas.confirmar()
     * pasando el nodoOrigen. Esto evita que la ventana principal se minimice,
     * ya que el diálogo queda vinculado al Stage padre mediante initOwner()
     * y Modality.APPLICATION_MODAL.
     */
    @FXML
    private void desactivarMesa() {
        if (mesaSeleccionada == null) return;

        // Verificar si la mesa tiene pedidos activos antes de intentar inactivar
        if (MesaDAO.tienePedidosActivos(mesaSeleccionada.getId())) {
            mostrarAlerta("Acción denegada", "No se puede inactivar una mesa con pedidos activos.");
            return;
        }

        Node nodo = obtenerNodoParaAlerta();
        Alertas.confirmar(
            nodo,
            "Confirmar Inactivación",
            "¿Desea inactivar la mesa #" + mesaSeleccionada.getNumero() + "?",
            () -> {
                if (MesaDAO.cambiarEstado(mesaSeleccionada.getId(), "Inactiva")) {
                    cargarListado();
                    prepararNuevaMesa();
                }
            }
        );
    }

    /** Carga los datos de la mesa seleccionada en el formulario para editar. */
    @FXML
    private void editarMesa() {
        if (mesaSeleccionada != null) {
            mostrarDetalleMesa(mesaSeleccionada);
        }
    }

    /** Delega al método desactivarMesa (botón eliminar del formulario). */
    @FXML
    private void eliminarMesa() {
        desactivarMesa();
    }

    /** Ejecuta el filtro manualmente (botón buscar). */
    @FXML
    private void buscarMesa() {
        filtrar();
    }

    /** Limpia los filtros y recarga la lista completa. */
    @FXML
    private void limpiarFiltrosMesa() {
        cmbFiltroSede.getSelectionModel().clearSelection();
        cmbFiltroArea.getSelectionModel().clearSelection();
        cmbFiltroEstado.getSelectionModel().clearSelection();
        filtrar();
    }

    /** Filtra las mesas según sede, área, estado y texto de búsqueda. */
    private void filtrar() {
        Sede     sedeFiltro = cmbFiltroSede.getValue();
        AreaMesa areaFiltro = cmbFiltroArea.getValue();
        String   estFiltro  = cmbFiltroEstado.getValue();
        String   txtFiltro  = txtBuscarMesa.getText().trim().toLowerCase();

        List<Mesa> listaFiltrada = MesaDAO.listarTodas().stream().filter(m -> {
            boolean coincideSede  = (sedeFiltro == null) || (m.getSedeId() == sedeFiltro.getId());
            boolean coincideArea  = (areaFiltro == null) || (m.getAreaId() == areaFiltro.getId());
            boolean coincideEst   = (estFiltro  == null) || estFiltro.equalsIgnoreCase(m.getEstado());
            boolean coincideTexto = txtFiltro.isEmpty() || m.getNumero().toLowerCase().contains(txtFiltro);
            return coincideSede && coincideArea && coincideEst && coincideTexto;
        }).toList();

        mesasObservable.setAll(listaFiltrada);
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
        if (btnGuardarMesa != null) return btnGuardarMesa;
        if (tblListaMesas  != null) return tblListaMesas;
        if (txtMesNumero   != null) return txtMesNumero;
        return null; // Alertas busca el Stage activo como fallback
    }
}
