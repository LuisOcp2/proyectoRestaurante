package com.mosqueteros.proyecto_restaurante.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.StringFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;   // ← NATIVO, no MFX
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  MesaController  v4                                              ║
 * ║                                                                  ║
 * ║  CAMBIO CLAVE v4:                                                ║
 * ║  cmbFiltroSede / cmbFiltroArea / cmbFiltroEstado                 ║
 * ║  → ahora son javafx.scene.control.ComboBox<String>  (NATIVOS)   ║
 * ║  La API es idéntica: getValue(), getItems(), setValue(null)      ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
public class MesaController implements Initializable {

    // ── Filtros superiores — ComboBox NATIVO (no MFX) ────────────────────
    @FXML private MFXTextField        txtBuscarMesa;
    @FXML private ComboBox<String>    cmbFiltroSede;    // ← ComboBox nativo
    @FXML private ComboBox<String>    cmbFiltroArea;    // ← ComboBox nativo
    @FXML private ComboBox<String>    cmbFiltroEstado;  // ← ComboBox nativo
    @FXML private MFXButton           btnBuscarMesa;
    @FXML private MFXButton           btnLimpiarFiltroMesa;

    // ── Tabla y placeholder ──────────────────────────────────────────────
    @FXML private MFXTableView<MesaFila> tblListaMesas;
    @FXML private VBox                   boxPlaceholder;
    @FXML private ImageView              imgEmptyState;

    // ── Footer de la tabla ───────────────────────────────────────────────
    @FXML private Label     lblConteoMesas;
    @FXML private MFXButton btnNuevoMesa;
    @FXML private MFXButton btnEditarMesa;
    @FXML private MFXButton btnEliminarMesa;

    // ── Formulario lateral (MFXComboBox se conserva aquí) ────────────────
    @FXML private MFXTextField          txtMesNumero;
    @FXML private MFXTextField          txtMesCapacidad;
    @FXML private MFXComboBox<String>   cmbAreaMesa;
    @FXML private MFXComboBox<String>   cmbSedeMesa;
    @FXML private MFXComboBox<String>   cmbEstadoMesa;
    @FXML private MFXButton             btnGuardarMesa;
    @FXML private MFXButton             btnNuevoMesaForm;
    @FXML private MFXButton             btnEliminarMesaForm;
    @FXML private Label                 lblMensajeMesa;

    // ── Estado interno ───────────────────────────────────────────────────
    private final ObservableList<MesaFila> listaMesas = FXCollections.observableArrayList();
    private MesaFila mesaSeleccionada = null;

    // ─────────────────────────────────────────────────────────────────────
    // INICIALIZACIÓN
    // ─────────────────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarSvgPlaceholder();
        configurarColumnas();
        configurarFiltrosMFX();
        cargarOpcionesCombos();
        registrarListenerSeleccion();
        cargarMesasMock();
    }

    @SuppressWarnings("unchecked")
    private void configurarColumnas() {
        MFXTableColumn<MesaFila> colId = new MFXTableColumn<>("ID", true,
                Comparator.comparingInt(MesaFila::getId));
        colId.setRowCellFactory(m -> new MFXTableRowCell<>(MesaFila::getId));
        colId.setPrefWidth(60);

        MFXTableColumn<MesaFila> colSede = new MFXTableColumn<>("Sede", true,
                Comparator.comparing(MesaFila::getSede));
        colSede.setRowCellFactory(m -> new MFXTableRowCell<>(MesaFila::getSede));

        MFXTableColumn<MesaFila> colArea = new MFXTableColumn<>("Área", true,
                Comparator.comparing(MesaFila::getArea));
        colArea.setRowCellFactory(m -> new MFXTableRowCell<>(MesaFila::getArea));

        MFXTableColumn<MesaFila> colNumero = new MFXTableColumn<>("Nº Mesa", true,
                Comparator.comparingInt(MesaFila::getNumero));
        colNumero.setRowCellFactory(m -> new MFXTableRowCell<>(MesaFila::getNumero));
        colNumero.setPrefWidth(90);

        MFXTableColumn<MesaFila> colCapacidad = new MFXTableColumn<>("Capacidad", true,
                Comparator.comparingInt(MesaFila::getCapacidad));
        colCapacidad.setRowCellFactory(m -> new MFXTableRowCell<>(MesaFila::getCapacidad));
        colCapacidad.setPrefWidth(100);

        MFXTableColumn<MesaFila> colEstado = new MFXTableColumn<>("Estado", true,
                Comparator.comparing(MesaFila::getEstado));
        colEstado.setRowCellFactory(m -> new MFXTableRowCell<>(MesaFila::getEstado));
        colEstado.setPrefWidth(90);

        tblListaMesas.getTableColumns().addAll(
                colId, colSede, colArea, colNumero, colCapacidad, colEstado);
    }

    private void cargarSvgPlaceholder() {
        try {
            String path = "/com/mosqueteros/proyecto_restaurante/images/empty_state_mesas.png";
            Image image = new Image(getClass().getResourceAsStream(path));
            if (imgEmptyState != null && !image.isError()) {
                imgEmptyState.setImage(image);
                imgEmptyState.setFitWidth(230);
                imgEmptyState.setPreserveRatio(true);
                imgEmptyState.setSmooth(true);
            }
        } catch (Exception e) {
            System.out.println("ℹ️ Imagen placeholder no cargada: " + e.getMessage());
        }
    }

    private void configurarFiltrosMFX() {
        tblListaMesas.getFilters().addAll(
                new StringFilter<>("Sede",   MesaFila::getSede),
                new StringFilter<>("Área",   MesaFila::getArea),
                new StringFilter<>("Estado", MesaFila::getEstado)
        );
    }

    /**
     * Carga opciones en los combos de filtro (nativos) y formulario (MFX).
     * API idéntica para ambos: getItems().addAll(...)
     */
    private void cargarOpcionesCombos() {
        // Filtros superiores — ComboBox nativo
        cmbFiltroSede.getItems().addAll("Sede Norte", "Sede Sur", "Sede Centro");
        cmbFiltroArea.getItems().addAll("Interior", "Terraza", "VIP", "Bar");
        cmbFiltroEstado.getItems().addAll("Disponible", "Ocupada", "Reservada", "Mantenimiento");

        // Formulario lateral — MFXComboBox
        cmbSedeMesa.getItems().addAll("Sede Norte", "Sede Sur", "Sede Centro");
        cmbAreaMesa.getItems().addAll("Interior", "Terraza", "VIP", "Bar");
        cmbEstadoMesa.getItems().addAll("Disponible", "Ocupada", "Reservada", "Mantenimiento");
    }

    private void registrarListenerSeleccion() {
        tblListaMesas.getSelectionModel().selectionProperty().addListener(
            (obs, oldSel, newSel) -> {
                boolean haySeleccion = newSel != null && !newSel.isEmpty();
                if (haySeleccion) {
                    mesaSeleccionada = newSel.values().iterator().next();
                    poblarFormulario(mesaSeleccionada);
                } else {
                    mesaSeleccionada = null;
                }
                btnEditarMesa.setDisable(!haySeleccion);
                btnEliminarMesa.setDisable(!haySeleccion);
                btnEliminarMesaForm.setDisable(!haySeleccion);
            }
        );
    }

    // ─────────────────────────────────────────────────────────────────────
    // GESTIÓN DEL EMPTY STATE
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Alterna tabla ↔ empty state cambiando visible+managed juntos.
     * Esto evita que MFXTableView renderice su cabecera interna
     * cuando no hay datos (managed=false la excluye del layout tree).
     */
    private void actualizarEstadoVista(int totalFiltrado) {
        boolean estaVacio = totalFiltrado == 0;

        boxPlaceholder.setVisible(estaVacio);
        boxPlaceholder.setManaged(estaVacio);

        tblListaMesas.setVisible(!estaVacio);
        tblListaMesas.setManaged(!estaVacio);

        String texto = totalFiltrado == 1
                ? "1 mesa encontrada"
                : totalFiltrado + " mesas encontradas";
        lblConteoMesas.setText(texto);
    }

    // ─────────────────────────────────────────────────────────────────────
    // ACCIONES FXML
    // ─────────────────────────────────────────────────────────────────────

    @FXML
    private void buscarMesaMock() {
        String texto  = txtBuscarMesa.getText().trim().toLowerCase();
        String sede   = cmbFiltroSede.getValue();
        String area   = cmbFiltroArea.getValue();
        String estado = cmbFiltroEstado.getValue();

        List<MesaFila> filtradas = listaMesas.stream()
                .filter(m -> texto.isEmpty()
                        || String.valueOf(m.getNumero()).contains(texto)
                        || m.getSede().toLowerCase().contains(texto)
                        || m.getArea().toLowerCase().contains(texto))
                .filter(m -> sede   == null || sede.isEmpty()   || m.getSede().equals(sede))
                .filter(m -> area   == null || area.isEmpty()   || m.getArea().equals(area))
                .filter(m -> estado == null || estado.isEmpty() || m.getEstado().equals(estado))
                .collect(Collectors.toList());

        tblListaMesas.setItems(FXCollections.observableArrayList(filtradas));
        actualizarEstadoVista(filtradas.size());
    }

    @FXML
    private void limpiarFiltrosMesaMock() {
        txtBuscarMesa.clear();

        // ComboBox nativo: setValue(null) limpia la selección
        cmbFiltroSede.setValue(null);
        cmbFiltroArea.setValue(null);
        cmbFiltroEstado.setValue(null);

        tblListaMesas.setItems(listaMesas);
        actualizarEstadoVista(listaMesas.size());
    }

    @FXML
    private void prepararNuevaMesaMock() {
        mesaSeleccionada = null;
        limpiarFormulario();
        txtMesNumero.requestFocus();
        mostrarMensaje("", false);
    }

    @FXML
    private void guardarMesaMock() {
        if (!validarFormulario()) return;

        if (mesaSeleccionada == null) {
            listaMesas.add(construirMesaDesdeFormulario());
            mostrarMensaje("✓ Mesa registrada correctamente", true);
        } else {
            actualizarMesaDesdeFormulario(mesaSeleccionada);
            tblListaMesas.update();
            mostrarMensaje("✓ Mesa actualizada correctamente", true);
        }

        tblListaMesas.setItems(listaMesas);
        actualizarEstadoVista(listaMesas.size());
        limpiarFormulario();
        mesaSeleccionada = null;
    }

    @FXML
    private void editarMesaMock() {
        if (mesaSeleccionada == null) {
            mostrarMensaje("⚠ Selecciona una mesa para editar", false);
            return;
        }
        poblarFormulario(mesaSeleccionada);
        txtMesNumero.requestFocus();
    }

    @FXML
    private void eliminarMesaMock() {
        if (mesaSeleccionada == null) {
            mostrarMensaje("⚠ Selecciona una mesa para eliminar", false);
            return;
        }
        listaMesas.remove(mesaSeleccionada);
        tblListaMesas.setItems(listaMesas);
        actualizarEstadoVista(listaMesas.size());
        limpiarFormulario();
        mesaSeleccionada = null;
        mostrarMensaje("Mesa eliminada", false);
    }

    // ─────────────────────────────────────────────────────────────────────
    // AUXILIARES PRIVADOS
    // ─────────────────────────────────────────────────────────────────────

    private void cargarMesasMock() {
        // Sin datos → empty state visible al arrancar.
        // Para probar con datos, descomenta:
        // listaMesas.addAll(
        //     new MesaFila(1, "Sede Norte", "Interior",  1, 4, "Disponible"),
        //     new MesaFila(2, "Sede Norte", "Terraza",   2, 6, "Ocupada"),
        //     new MesaFila(3, "Sede Sur",   "VIP",       1, 2, "Reservada")
        // );
        tblListaMesas.setItems(listaMesas);
        actualizarEstadoVista(listaMesas.size());
    }

    private void poblarFormulario(MesaFila mesa) {
        txtMesNumero.setText(String.valueOf(mesa.getNumero()));
        txtMesCapacidad.setText(String.valueOf(mesa.getCapacidad()));
        cmbAreaMesa.setValue(mesa.getArea());
        cmbSedeMesa.setValue(mesa.getSede());
        cmbEstadoMesa.setValue(mesa.getEstado());
    }

    private void limpiarFormulario() {
        txtMesNumero.clear();
        txtMesCapacidad.clear();
        cmbAreaMesa.clearSelection();
        cmbSedeMesa.clearSelection();
        cmbEstadoMesa.clearSelection();
    }

    private boolean validarFormulario() {
        String numero    = txtMesNumero.getText().trim();
        String capacidad = txtMesCapacidad.getText().trim();

        if (numero.isEmpty()) {
            mostrarMensaje("⚠ El número de mesa es obligatorio", false);
            txtMesNumero.requestFocus(); return false;
        }
        if (!numero.matches("\\d+")) {
            mostrarMensaje("⚠ El número debe ser un entero positivo", false);
            txtMesNumero.requestFocus(); return false;
        }
        if (capacidad.isEmpty()) {
            mostrarMensaje("⚠ La capacidad es obligatoria", false);
            txtMesCapacidad.requestFocus(); return false;
        }
        if (!capacidad.matches("\\d+") || Integer.parseInt(capacidad) < 1) {
            mostrarMensaje("⚠ Capacidad debe ser un número mayor a 0", false);
            txtMesCapacidad.requestFocus(); return false;
        }
        if (cmbAreaMesa.getValue()   == null) { mostrarMensaje("⚠ Selecciona el área",  false); return false; }
        if (cmbSedeMesa.getValue()   == null) { mostrarMensaje("⚠ Selecciona la sede",  false); return false; }
        if (cmbEstadoMesa.getValue() == null) { mostrarMensaje("⚠ Selecciona el estado",false); return false; }
        return true;
    }

    private MesaFila construirMesaDesdeFormulario() {
        int nuevoId = listaMesas.stream().mapToInt(MesaFila::getId).max().orElse(0) + 1;
        return new MesaFila(
                nuevoId,
                cmbSedeMesa.getValue(),
                cmbAreaMesa.getValue(),
                Integer.parseInt(txtMesNumero.getText().trim()),
                Integer.parseInt(txtMesCapacidad.getText().trim()),
                cmbEstadoMesa.getValue()
        );
    }

    private void actualizarMesaDesdeFormulario(MesaFila mesa) {
        mesa.setSede(cmbSedeMesa.getValue());
        mesa.setArea(cmbAreaMesa.getValue());
        mesa.setNumero(Integer.parseInt(txtMesNumero.getText().trim()));
        mesa.setCapacidad(Integer.parseInt(txtMesCapacidad.getText().trim()));
        mesa.setEstado(cmbEstadoMesa.getValue());
    }

    private void mostrarMensaje(String texto, boolean esExito) {
        if (texto.isEmpty()) {
            lblMensajeMesa.setVisible(false);
            lblMensajeMesa.setManaged(false);
            return;
        }
        lblMensajeMesa.setText(texto);
        lblMensajeMesa.setVisible(true);
        lblMensajeMesa.setManaged(true);
        lblMensajeMesa.setStyle("-fx-text-fill: " + (esExito ? "#27AE60" : "#E67E22") + ";");
    }

    // ─────────────────────────────────────────────────────────────────────
    // DTO DE PRESENTACIÓN
    // ─────────────────────────────────────────────────────────────────────
    public static class MesaFila {
        private int id; private String sede, area, estado;
        private int numero, capacidad;

        public MesaFila(int id, String sede, String area, int numero, int capacidad, String estado) {
            this.id = id; this.sede = sede; this.area = area;
            this.numero = numero; this.capacidad = capacidad; this.estado = estado;
        }

        public int    getId()        { return id; }
        public String getSede()      { return sede; }
        public String getArea()      { return area; }
        public int    getNumero()    { return numero; }
        public int    getCapacidad() { return capacidad; }
        public String getEstado()    { return estado; }

        public void setSede(String s)      { this.sede = s; }
        public void setArea(String a)      { this.area = a; }
        public void setNumero(int n)       { this.numero = n; }
        public void setCapacidad(int c)    { this.capacidad = c; }
        public void setEstado(String e)    { this.estado = e; }

        @Override public String toString() { return "Mesa#" + numero + " (" + sede + " - " + area + ")"; }
    }
}