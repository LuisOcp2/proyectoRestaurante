package com.mosqueteros.proyecto_restaurante.controller;

import com.mosqueteros.proyecto_restaurante.util.SessionUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controlador principal del Dashboard y menú lateral.
 * Gestiona la navegación entre módulos de los 4 integrantes:
 *  - Luis:      Pedidos, Comandas, Platos, Mesas
 *  - Sebastián: Egresos, Forma de Pago, Concepto Egreso
 *  - Angie:     Facturación, Clientes, Perfiles/Usuarios
 *  - Nicolás:   Inventario (Insumos, Log, Presentación, Cat. Insumo) o PQRS
 */
public class MainController {

    // ── Topbar ────────────────────────────────────────────────────────────
    @FXML private MFXButton btnHamburger;
    @FXML private Label     lblTopbarBrand;
    @FXML private Label     lblAvatar;
    @FXML private Label     lblNombreUsuario;
    @FXML private Label     lblRolUsuario;
    @FXML private MFXButton btnCerrarSesion;

    // ── Sidebar — Brand ───────────────────────────────────────────────────
    @FXML private VBox  sidebar;
    @FXML private VBox  sidebarBrandBox;
    @FXML private Label lblSidebarBrand;
    @FXML private Label lblSidebarSub;

    // ── GENERAL ───────────────────────────────────────────────────────────
    @FXML private MFXButton btnDashboard;

    // ── SECCIÓN: LUIS — Pedidos y Comandas ────────────────────────────────
    @FXML private Label     seccionLuis;
    @FXML private MFXButton btnMesas;
    @FXML private MFXButton btnPlatos;
    @FXML private MFXButton btnCategoriasPlato;   // Categorías de plato
    @FXML private MFXButton btnPedidos;
    @FXML private MFXButton btnComandas;           // Vista de cocina / comanda
    @FXML private MFXButton btnAreasMesa;          // Áreas de mesa

    // ── SECCIÓN: SEBASTIÁN — Egresos y Seguridad ──────────────────────────
    @FXML private Label     seccionSebastian;
    @FXML private MFXButton btnEgresos;
    @FXML private MFXButton btnConceptoEgreso;
    @FXML private MFXButton btnFormaPago;

    // ── SECCIÓN: ANGIE — Facturación y Roles ─────────────────────────────
    @FXML private Label     seccionAngie;
    @FXML private MFXButton btnReciboCaja;
    @FXML private MFXButton btnClientes;
    @FXML private MFXButton btnPerfiles;
    @FXML private MFXButton btnUsuarios;

    // ── SECCIÓN: NICOLÁS — Inventario / PQRS ─────────────────────────────
    @FXML private Label     seccionNicolas;
    @FXML private MFXButton btnInventarioLog;
    @FXML private MFXButton btnInsumos;
    @FXML private MFXButton btnPresentacion;
    @FXML private MFXButton btnCategoriaInsumo;
    @FXML private MFXButton btnPQRS;

    // ── SECCIÓN: CONFIGURACIÓN GENERAL (solo Admin) ───────────────────────
    @FXML private Label     seccionConfiguracion;
    @FXML private MFXButton btnSedes;

    // ── Contenido principal ───────────────────────────────────────────────
    @FXML private StackPane contenidoPrincipal;
    @FXML private Label     lblBienvenida;
    @FXML private Label     lblRol;

    // Estado sidebar
    private boolean sidebarExpandido = true;
    private static final double SIDEBAR_ANCHO_COMPLETO = 260;
    private static final double SIDEBAR_ANCHO_MINI     = 64;

    // Botón activo actual
    private MFXButton botonActivo = null;

    // Cache de vistas y spinner
    private final java.util.Map<String, Parent> vistaCache = new java.util.HashMap<>();
    private boolean vistasPreCargadas = false;
    private MFXProgressSpinner spinnerCarga;

    // ─────────────────────────────────────────────────────────────────────
    // Inicialización (llamada desde LoginController tras login exitoso)
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Inicializa el dashboard y ajusta la visibilidad del menú según el rol.
     * Roles: Administrador, Mesero, Cocinero, Cajero.
     */
    public void inicializarDashboard() {
        String perfil = SessionUtil.obtenerRol();
        String login  = SessionUtil.obtenerUsuario() != null
                        ? SessionUtil.obtenerUsuario().obtenerNombre() : "Usuario";

        // ── Topbar ──
        lblAvatar.setText(obtenerIniciales(login));
        lblNombreUsuario.setText(login);
        lblRolUsuario.setText(perfil);

        // ── Panel bienvenida ──
        lblBienvenida.setText("¡Bienvenido, " + login + "!");
        lblRol.setText("Rol activo: " + perfil);

        // ── Control de visibilidad por rol ──
        aplicarVisibilidadPorRol(perfil);

        // Dashboard activo por defecto
        marcarBotonActivo(btnDashboard);

        // Spinner e precarga
        inicializarSpinner();
        precargarVistasComunes(perfil);
    }

    /**
     * Aplica visibilidad de secciones y botones del menú según el rol del usuario.
     *
     * Administrador → ve TODO.
     * Mesero        → solo sección Luis (Mesas, Platos, Pedidos, Comandas).
     * Cocinero      → solo Comandas (vista de cocina).
     * Cajero        → Facturación, Clientes + Egresos.
     */
    private void aplicarVisibilidadPorRol(String perfil) {
        boolean esAdmin    = "Administrador".equalsIgnoreCase(perfil);
        boolean esMesero   = "Mesero".equalsIgnoreCase(perfil);
        boolean esCocinero = "Cocinero".equalsIgnoreCase(perfil);
        boolean esCajero   = "Cajero".equalsIgnoreCase(perfil);

        // ── Sección LUIS ──
        boolean verLuis = esAdmin || esMesero || esCocinero;
        setVisible(seccionLuis,       verLuis);
        setVisible(btnMesas,          esAdmin || esMesero);
        setVisible(btnPlatos,         esAdmin || esMesero);
        setVisible(btnCategoriasPlato,esAdmin);
        setVisible(btnPedidos,        esAdmin || esMesero);
        setVisible(btnComandas,       esAdmin || esMesero || esCocinero);
        setVisible(btnAreasMesa,      esAdmin);

        // ── Sección SEBASTIÁN ──
        boolean verSebastian = esAdmin || esCajero;
        setVisible(seccionSebastian,   verSebastian);
        setVisible(btnEgresos,         verSebastian);
        setVisible(btnConceptoEgreso,  esAdmin);
        setVisible(btnFormaPago,       esAdmin);

        // ── Sección ANGIE ──
        boolean verAngie = esAdmin || esCajero;
        setVisible(seccionAngie,  verAngie);
        setVisible(btnReciboCaja, verAngie);
        setVisible(btnClientes,   esAdmin || esCajero);
        setVisible(btnPerfiles,   esAdmin);
        setVisible(btnUsuarios,   esAdmin);

        // ── Sección NICOLÁS ──
        boolean verNicolas = esAdmin;
        setVisible(seccionNicolas,      verNicolas);
        setVisible(btnInventarioLog,    verNicolas);
        setVisible(btnInsumos,          verNicolas);
        setVisible(btnPresentacion,     verNicolas);
        setVisible(btnCategoriaInsumo,  verNicolas);
        setVisible(btnPQRS,             verNicolas);

        // ── Configuración general ──
        setVisible(seccionConfiguracion, esAdmin);
        setVisible(btnSedes,             esAdmin);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Toggle Sidebar (botón hamburger)
    // ─────────────────────────────────────────────────────────────────────

    /** Expande o colapsa el sidebar lateral con animación. */
    @FXML
    private void toggleSidebar(ActionEvent event) {
        double destino   = sidebarExpandido ? SIDEBAR_ANCHO_MINI : SIDEBAR_ANCHO_COMPLETO;
        boolean colapsando = sidebarExpandido;

        if (colapsando) ocultarTextosSidebar();

        Timeline anim = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(sidebar.prefWidthProperty(), sidebar.getPrefWidth()),
                new KeyValue(sidebar.minWidthProperty(),  sidebar.getMinWidth()),
                new KeyValue(sidebar.maxWidthProperty(),  sidebar.getMaxWidth())),
            new KeyFrame(Duration.millis(240),
                new KeyValue(sidebar.prefWidthProperty(), destino),
                new KeyValue(sidebar.minWidthProperty(),  destino),
                new KeyValue(sidebar.maxWidthProperty(),  destino))
        );

        anim.setOnFinished(e -> {
            if (!colapsando) mostrarTextosSidebar();
            setVisible(lblTopbarBrand, colapsando);
        });

        anim.play();
        sidebarExpandido = !sidebarExpandido;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Navegación — GENERAL
    // ─────────────────────────────────────────────────────────────────────

    /** Regresa al panel de bienvenida del dashboard. */
    @FXML
    private void mostrarDashboard(ActionEvent event) {
        marcarBotonActivo(btnDashboard);
        lblBienvenida.setText("📊  Dashboard");
        lblRol.setText("Información general del restaurante");
        if (contenidoPrincipal.getChildren().size() > 1) {
            contenidoPrincipal.getChildren()
                .subList(1, contenidoPrincipal.getChildren().size()).clear();
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Navegación — MÓDULO LUIS (Pedidos y Comandas)
    // ─────────────────────────────────────────────────────────────────────

    /** Muestra el CRUD de mesas por sede y área. */
    @FXML
    private void mostrarVistaMesas(ActionEvent event) {
        marcarBotonActivo(btnMesas);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaMesas.fxml");
    }

    /** Muestra el CRUD de platos del menú. */
    @FXML
    private void mostrarVistaPlatos(ActionEvent event) {
        marcarBotonActivo(btnPlatos);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPlatos.fxml");
    }

    /** Muestra el CRUD de categorías de plato. */
    @FXML
    private void mostrarVistaCategoriasPlato(ActionEvent event) {
        marcarBotonActivo(btnCategoriasPlato);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaCategoriasPlato.fxml");
    }

    /** Muestra el CRUD de pedidos (cabecera de la orden). */
    @FXML
    private void mostrarVistaPedidos(ActionEvent event) {
        marcarBotonActivo(btnPedidos);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPedidos.fxml");
    }

    /** Muestra la vista de cocina: comandas pendientes por sede. */
    @FXML
    private void mostrarVistaComandas(ActionEvent event) {
        marcarBotonActivo(btnComandas);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaComandas.fxml");
    }

    /** Muestra el CRUD de áreas de mesa. */
    @FXML
    private void mostrarVistaAreasMesa(ActionEvent event) {
        marcarBotonActivo(btnAreasMesa);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaAreasMesa.fxml");
    }

    // ─────────────────────────────────────────────────────────────────────
    // Navegación — MÓDULO SEBASTIÁN (Egresos y Seguridad)
    // ─────────────────────────────────────────────────────────────────────

    /** Muestra el CRUD de egresos (encabezadoegresos). */
    @FXML
    private void mostrarVistaEgresos(ActionEvent event) {
        marcarBotonActivo(btnEgresos);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaEgresos.fxml");
    }

    /** Muestra el CRUD de conceptos de egreso. */
    @FXML
    private void mostrarVistaConceptoEgreso(ActionEvent event) {
        marcarBotonActivo(btnConceptoEgreso);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaConceptoEgreso.fxml");
    }

    /** Muestra el CRUD de formas de pago. */
    @FXML
    private void mostrarVistaFormaPago(ActionEvent event) {
        marcarBotonActivo(btnFormaPago);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaFormaPago.fxml");
    }

    // ─────────────────────────────────────────────────────────────────────
    // Navegación — MÓDULO ANGIE (Facturación y Roles)
    // ─────────────────────────────────────────────────────────────────────

    /** Muestra el CRUD de recibos de caja (facturación). */
    @FXML
    private void mostrarVistaReciboCaja(ActionEvent event) {
        marcarBotonActivo(btnReciboCaja);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaReciboCaja.fxml");
    }

    /** Muestra el CRUD de clientes. */
    @FXML
    private void mostrarVistaClientes(ActionEvent event) {
        marcarBotonActivo(btnClientes);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaClientes.fxml");
    }

    /** Muestra el CRUD de perfiles/roles. */
    @FXML
    private void mostrarVistaPerfiles(ActionEvent event) {
        marcarBotonActivo(btnPerfiles);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPerfiles.fxml");
    }

    /** Muestra el CRUD de usuarios del sistema. */
    @FXML
    private void mostrarVistaUsuarios(ActionEvent event) {
        marcarBotonActivo(btnUsuarios);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaUsuarios.fxml");
    }

    // ─────────────────────────────────────────────────────────────────────
    // Navegación — MÓDULO NICOLÁS (Inventario / PQRS)
    // ─────────────────────────────────────────────────────────────────────

    /** Muestra el CRUD de movimientos de inventario (inventariolog). */
    @FXML
    private void mostrarVistaInventarioLog(ActionEvent event) {
        marcarBotonActivo(btnInventarioLog);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaInventarioLog.fxml");
    }

    /** Muestra el CRUD de insumos de bodega. */
    @FXML
    private void mostrarVistaInsumos(ActionEvent event) {
        marcarBotonActivo(btnInsumos);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaInsumos.fxml");
    }

    /** Muestra el CRUD de presentaciones (unidades de medida). */
    @FXML
    private void mostrarVistaPresentacion(ActionEvent event) {
        marcarBotonActivo(btnPresentacion);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPresentacion.fxml");
    }

    /** Muestra el CRUD de categorías de insumo. */
    @FXML
    private void mostrarVistaCategoriaInsumo(ActionEvent event) {
        marcarBotonActivo(btnCategoriaInsumo);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaCategoriaInsumo.fxml");
    }

    /** Muestra el CRUD de PQRS (Peticiones, Quejas, Reclamos, Sugerencias). */
    @FXML
    private void mostrarVistaPQRS(ActionEvent event) {
        marcarBotonActivo(btnPQRS);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPQRS.fxml");
    }

    // ─────────────────────────────────────────────────────────────────────
    // Navegación — CONFIGURACIÓN GENERAL
    // ─────────────────────────────────────────────────────────────────────

    /** Muestra el CRUD de sedes del restaurante. */
    @FXML
    private void mostrarVistaSedes(ActionEvent event) {
        marcarBotonActivo(btnSedes);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaSedes.fxml");
    }

    // ─────────────────────────────────────────────────────────────────────
    // Spinner de carga
    // ─────────────────────────────────────────────────────────────────────

    /** Inicializa el spinner de carga con MaterialFX. */
    private void inicializarSpinner() {
        spinnerCarga = new MFXProgressSpinner();
        spinnerCarga.setRadius(40);

        VBox spinnerContainer = new VBox(20);
        spinnerContainer.setAlignment(Pos.CENTER);
        spinnerContainer.getChildren().addAll(spinnerCarga, new Label("Cargando..."));

        StackPane spinnerWrapper = new StackPane(spinnerContainer);
        spinnerWrapper.setAlignment(Pos.CENTER);
        spinnerWrapper.setStyle(
            "-fx-background-color: rgba(248, 249, 250, 0.95);" +
            "-fx-border-color: rgba(99, 102, 241, 0.2);" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 40;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 20, 0.3, 0, 5);"
        );
        spinnerWrapper.setMaxWidth(300);
        spinnerWrapper.setMaxHeight(200);
        spinnerWrapper.setVisible(false);
        spinnerCarga.setUserData(spinnerWrapper);
    }

    /** Muestra el spinner con fade-in. */
    private void mostrarSpinner() {
        StackPane wrapper = (StackPane) spinnerCarga.getUserData();
        if (wrapper == null) return;
        if (!contenidoPrincipal.getChildren().contains(wrapper)) {
            contenidoPrincipal.getChildren().add(wrapper);
        }
        wrapper.setVisible(true);
        wrapper.setOpacity(0);
        wrapper.toFront();
        FadeTransition fade = new FadeTransition(Duration.millis(200), wrapper);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    /** Oculta el spinner con fade-out. */
    private void ocultarSpinner() {
        StackPane wrapper = (StackPane) spinnerCarga.getUserData();
        if (wrapper == null) return;
        FadeTransition fade = new FadeTransition(Duration.millis(200), wrapper);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            wrapper.setVisible(false);
            contenidoPrincipal.getChildren().remove(wrapper);
        });
        fade.play();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Precarga de vistas por rol
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Precarga las vistas más usadas según el rol en un hilo secundario
     * para eliminar el lag al navegar por primera vez.
     */
    private void precargarVistasComunes(String perfil) {
        if (vistasPreCargadas) return;
        vistasPreCargadas = true;

        // Vistas a precargar según el rol
        java.util.List<String> vistas = new java.util.ArrayList<>();
        vistas.add("/com/mosqueteros/proyecto_restaurante/view/VistaMesas.fxml");
        vistas.add("/com/mosqueteros/proyecto_restaurante/view/VistaPedidos.fxml");
        vistas.add("/com/mosqueteros/proyecto_restaurante/view/VistaComandas.fxml");

        if ("Administrador".equalsIgnoreCase(perfil)) {
            vistas.add("/com/mosqueteros/proyecto_restaurante/view/VistaPlatos.fxml");
            vistas.add("/com/mosqueteros/proyecto_restaurante/view/VistaEgresos.fxml");
            vistas.add("/com/mosqueteros/proyecto_restaurante/view/VistaReciboCaja.fxml");
            vistas.add("/com/mosqueteros/proyecto_restaurante/view/VistaInventarioLog.fxml");
            vistas.add("/com/mosqueteros/proyecto_restaurante/view/VistaPQRS.fxml");
        }

        Task<Void> tareaPreCarga = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (String ruta : vistas) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
                        Parent vista = loader.load();
                        Platform.runLater(() -> vistaCache.put(ruta, vista));
                        Thread.sleep(50);
                    } catch (Exception e) {
                        System.err.println("No se pudo precargar: " + ruta);
                    }
                }
                return null;
            }
        };

        Thread thread = new Thread(tareaPreCarga);
        thread.setDaemon(true);
        thread.start();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Carga de vistas asíncrona
    // ─────────────────────────────────────────────────────────────────────

    /** Inicia la carga de una vista con spinner de feedback. */
    private void cargarVista(String rutaFxml) {
        mostrarSpinner();
        long tiempoInicio = System.currentTimeMillis();

        Task<Parent> tarea = new Task<Parent>() {
            @Override
            protected Parent call() throws Exception {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
                return loader.load();
            }
        };

        tarea.setOnSucceeded(event -> {
            Parent vista = tarea.getValue();
            long tiempoRestante = Math.max(0, 300 - (System.currentTimeMillis() - tiempoInicio));
            new Timeline(new KeyFrame(Duration.millis(tiempoRestante), e -> {
                ocultarSpinner();
                contenidoPrincipal.getChildren().setAll(vista);
            })).play();
        });

        tarea.setOnFailed(event -> {
            System.err.println("Error al cargar: " + rutaFxml + " → "
                    + tarea.getException().getMessage());
            tarea.getException().printStackTrace();
            ocultarSpinner();
        });

        Thread t = new Thread(tarea);
        t.setDaemon(true);
        t.start();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────

    /** Gestiona la clase CSS 'menu-btn-active' para resaltar el botón activo. */
    private void marcarBotonActivo(MFXButton boton) {
        if (botonActivo != null) botonActivo.getStyleClass().remove("menu-btn-active");
        boton.getStyleClass().add("menu-btn-active");
        botonActivo = boton;
    }

    /** Genera las iniciales del usuario para el avatar (máx. 2 letras). */
    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) return "?";
        String[] partes = nombre.trim().split("\\s+");
        String ini = String.valueOf(partes[0].charAt(0)).toUpperCase();
        if (partes.length > 1) ini += String.valueOf(partes[1].charAt(0)).toUpperCase();
        return ini;
    }

    private void ocultarTextosSidebar() {
        setVisible(lblSidebarBrand, false);
        setVisible(lblSidebarSub,   false);
    }

    private void mostrarTextosSidebar() {
        setVisible(lblSidebarBrand, true);
        setVisible(lblSidebarSub,   true);
    }

    /** Configura visibilidad y managed de un nodo en un solo llamado. */
    private void setVisible(javafx.scene.Node nodo, boolean visible) {
        nodo.setVisible(visible);
        nodo.setManaged(visible);
    }
   
    @FXML
    private void cerrarSesion(ActionEvent event) {
        SessionUtil.limpiarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/mosqueteros/proyecto_restaurante/view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Restaurante 2026 — Iniciar Sesión");
            stage.show();
        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
        }
    }
}
