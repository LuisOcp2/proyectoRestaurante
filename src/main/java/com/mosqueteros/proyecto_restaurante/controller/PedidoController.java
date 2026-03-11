package com.mosqueteros.proyecto_restaurante.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

/**
 * PedidoController — controlador para VistaPedidos.fxml
 *
 * Gestiona la vista maestro-detalle de pedidos y el panel de cocina.
 * Los métodos son stubs (mock); la lógica real irá en el DAO de Pedidos.
 */
public class PedidoController {

    // ── Tabla cabecera pedido ────────────────────────────────────────────
    @FXML private TableView<?>     tblListaPedidos;
    @FXML private TableColumn<?,?> colPedId;
    @FXML private TableColumn<?,?> colPedMesero;
    @FXML private TableColumn<?,?> colPedMesa;
    @FXML private TableColumn<?,?> colPedFecha;
    @FXML private TableColumn<?,?> colPedObs;
    @FXML private TableColumn<?,?> colPedEstado;

    // ── Tabla detalle pedido (pedidetalle) ───────────────────────────────
    @FXML private TableView<?>     tblDetallePedido;
    @FXML private TableColumn<?,?> colDtPlato;
    @FXML private TableColumn<?,?> colDtCantidad;
    @FXML private TableColumn<?,?> colDtPrecio;
    @FXML private TableColumn<?,?> colDtObs;
    @FXML private TableColumn<?,?> colDtEstado;

    // ── Tabla cocina / comandas ──────────────────────────────────────────
    @FXML private TableView<?>     tblComandas;
    @FXML private TableColumn<?,?> colComMesa;
    @FXML private TableColumn<?,?> colComPlato;
    @FXML private TableColumn<?,?> colComCant;

    // ── Filtros ──────────────────────────────────────────────────────────
    @FXML private DatePicker     dpFechaPedido;
    @FXML private ComboBox<String> cmbFiltroMesaPedido;
    @FXML private ComboBox<String> cmbFiltroEstadoPedido;
    @FXML private ComboBox<String> cmbFiltroSedePedido;

    // ── Formulario rápido (panel cocina) ─────────────────────────────────
    @FXML private ComboBox<String> cmbPlatoDetalle;
    @FXML private MFXTextField   txtCantidadDetalle;
    @FXML private TextArea       txtObservacionDetalle;
    @FXML private ComboBox<String> cmbEstadoDetalle;

    // ── Labels ───────────────────────────────────────────────────────────
    @FXML private Label lblConteoPedidos;
    @FXML private Label lblTotalPedido;
    @FXML private Label lblMensajePedido;

    // ── Botones ──────────────────────────────────────────────────────────
    @FXML private MFXButton btnBuscarPedido;
    @FXML private MFXButton btnLimpiarFiltroPedido;
    @FXML private MFXButton btnNuevoPedido;
    @FXML private MFXButton btnCambiarEstadoPedido;
    @FXML private MFXButton btnCancelarPedido;
    @FXML private MFXButton btnAgregarDetalle;
    @FXML private MFXButton btnQuitarDetalle;
    @FXML private MFXButton btnConfirmarAgregarDetalle;
    @FXML private MFXButton btnMarcarEstadoDetalle;

    // ── Métodos mock (placeholders para la lógica del DAO) ───────────────

    /** Filtra la tabla de pedidos por los criterios ingresados */
    @FXML
    private void buscarPedidoMock(ActionEvent event) {
        System.out.println("[Pedido] buscarPedido (mock)");
    }

    /** Limpia todos los filtros y recarga la tabla */
    @FXML
    private void limpiarFiltrosPedidoMock(ActionEvent event) {
        dpFechaPedido.setValue(null);
        System.out.println("[Pedido] limpiarFiltros (mock)");
    }

    /** Prepara el formulario para registrar un pedido nuevo */
    @FXML
    private void prepararNuevoPedidoMock(ActionEvent event) {
        lblTotalPedido.setText("$ 0.00");
        System.out.println("[Pedido] nuevoPedido (mock)");
    }

    /** Abre diálogo para cambiar el estado del pedido seleccionado */
    @FXML
    private void cambiarEstadoPedidoMock(ActionEvent event) {
        System.out.println("[Pedido] cambiarEstado (mock)");
    }

    /** Cancela el pedido seleccionado en la tabla */
    @FXML
    private void cancelarPedidoMock(ActionEvent event) {
        lblMensajePedido.setText("❌ Pedido cancelado (mock)");
        System.out.println("[Pedido] cancelarPedido (mock)");
    }

    /** Habilita el formulario de detalle para agregar un plato */
    @FXML
    private void agregarDetalleMock(ActionEvent event) {
        System.out.println("[Pedido] agregarDetalle (mock)");
    }

    /** Quita la línea de detalle seleccionada del pedido */
    @FXML
    private void quitarDetalleMock(ActionEvent event) {
        System.out.println("[Pedido] quitarDetalle (mock)");
    }

    /** Confirma el agregado de un plato al pedido actual */
    @FXML
    private void confirmarAgregarDetalleMock(ActionEvent event) {
        lblMensajePedido.setText("✅ Plato agregado al pedido (mock)");
        System.out.println("[Pedido] confirmarAgregarDetalle (mock)");
    }

    /** Cambia el estado de la línea de detalle seleccionada (ej: → Servido) */
    @FXML
    private void marcarEstadoDetalleMock(ActionEvent event) {
        System.out.println("[Pedido] marcarEstadoDetalle (mock)");
    }
}
