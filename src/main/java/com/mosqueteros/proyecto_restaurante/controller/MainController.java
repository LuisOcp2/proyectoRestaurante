package com.mosqueteros.proyecto_restaurante.controller;

import com.mosqueteros.proyecto_restaurante.util.Alertas;
import com.mosqueteros.proyecto_restaurante.util.SessionUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
 * Gestiona la navegación entre los diferentes módulos del sistema.
 */
public class MainController {

    // ── Topbar ─────────────────────────────────────────────────────
    @FXML private Label     lblTopbarBrand;
    @FXML private Label     lblAvatar;
    @FXML private Label     lblNombreUsuario;
    @FXML private Label     lblRolUsuario;
    @FXML private MFXButton btnCerrarSesion;

    // ── Sidebar ───────────────────────────────────────────────────
    @FXML private VBox  sidebar;
    @FXML private Label lblSidebarBrand;
    @FXML private Label lblSidebarSub;

    // ── General ───────────────────────────────────────────────────
    @FXML private MFXButton btnDashboard;

    // ── Módulo: Pedidos y Comandas ─────────────────────────────────
    @FXML private Label     seccionPedidos;
    @FXML private MFXButton btnMesas;
    @FXML private MFXButton btnPlatos;
    @FXML private MFXButton btnCategoriasPlato;
    @FXML private MFXButton btnPedidos;
    @FXML private MFXButton btnComandas;
    @FXML private MFXButton btnAreasMesa;

    // ── Módulo: Egresos ───────────────────────────────────────────────
    @FXML private Label     seccionEgresos;
    @FXML private MFXButton btnEgresos;
    @FXML private MFXButton btnConceptoEgreso;
    @FXML private MFXButton btnFormaPago;

    // ── Módulo: Facturación y Roles ─────────────────────────────────
    @FXML private Label     seccionFacturacion;
    @FXML private MFXButton btnReciboCaja;
    @FXML private MFXButton btnClientes;
    @FXML private MFXButton btnPerfiles;
    @FXML private MFXButton btnUsuarios;

    // ── Módulo: Inventario y PQRS ───────────────────────────────────
    @FXML private Label     seccionInventario;
    @FXML private MFXButton btnInventarioLog;
    @FXML private MFXButton btnInsumos;
    @FXML private MFXButton btnPresentacion;
    @FXML private MFXButton btnCategoriaInsumo;
    @FXML private MFXButton btnPQRS;

    // ── Configuración General ────────────────────────────────────────
    @FXML private Label     seccionConfiguracion;
    @FXML private MFXButton btnSedes;

    // ── Contenido principal ──────────────────────────────────────────
    @FXML private StackPane contenidoPrincipal;
    @FXML private Label     lblBienvenida;
    @FXML private Label     lblRol;

    /** Estado del sidebar: expandido o colapsado */
    private boolean sidebarExpandido = true;
    private static final double SIDEBAR_ANCHO_COMPLETO = 260;
    private static final double SIDEBAR_ANCHO_MINI     = 64;

    /** Botón del menú actualmente resaltado */
    private MFXButton botonActivo = null;

    /** Caché de vistas cargadas previamente para navegación rápida */
    private final java.util.Map<String, Parent> vistaCache = new java.util.HashMap<>();
    private boolean vistasPreCargadas = false;
    private MFXProgressSpinner spinnerCarga;

    // ─────────────────────────────────────────────────────────────
    // INICIALIZAR DASHBOARD
    // ─────────────────────────────────────────────────────────────

    /**
     * Inicializa el dashboard tras el login exitoso.
     * Lee la sesión activa para mostrar nombre y rol en la topbar.
     *
     * CORRECCIÓN: usa getUsunombre() en vez del inexistente getNombre().
     */
    public void inicializarDashboard() {
        Alertas.registrarContenedor(contenidoPrincipal);
        String perfil = SessionUtil.getUserRole();
        // getUsunombre() es el getter correcto en la clase Usuario
        String nombre = SessionUtil.getUser() != null
                ? SessionUtil.getUser().getUsunombre() : "Usuario";

        lblAvatar.setText(obtenerIniciales(nombre));
        lblNombreUsuario.setText(nombre);
        lblRolUsuario.setText(perfil);

        lblBienvenida.setText("📊  Bienvenido, " + nombre + "!");
        lblRol.setText("Rol activo: " + perfil);

        aplicarVisibilidadPorRol(perfil);
        marcarBotonActivo(btnDashboard);
        inicializarSpinner();
        precargarVistasComunes(perfil);
    }

    // ─────────────────────────────────────────────────────────────
    // VISIBILIDAD POR ROL
    // ─────────────────────────────────────────────────────────────

    /**
     * Aplica visibilidad de secciones y botones del menú según el rol.
     * Administrador → ve TODO.
     * Mesero        → Mesas, Platos, Pedidos, Comandas.
     * Cocinero      → solo Comandas.
     * Cajero        → Facturación, Clientes, Egresos.
     */
    private void aplicarVisibilidadPorRol(String perfil) {
        boolean esAdmin    = "Administrador".equalsIgnoreCase(perfil);
        boolean esMesero   = "Mesero".equalsIgnoreCase(perfil);
        boolean esCocinero = "Cocinero".equalsIgnoreCase(perfil);
        boolean esCajero   = "Cajero".equalsIgnoreCase(perfil);

        // Pedidos y Comandas
        boolean verPedidos = esAdmin || esMesero || esCocinero;
        setVisible(seccionPedidos,   verPedidos);
        setVisible(btnMesas,         esAdmin || esMesero);
        setVisible(btnPlatos,        esAdmin || esMesero);
        setVisible(btnCategoriasPlato, esAdmin);
        setVisible(btnPedidos,       esAdmin || esMesero);
        setVisible(btnComandas,      esAdmin || esMesero || esCocinero);
        setVisible(btnAreasMesa,     esAdmin);

        // Egresos
        boolean verEgresos = esAdmin || esCajero;
        setVisible(seccionEgresos,   verEgresos);
        setVisible(btnEgresos,       verEgresos);
        setVisible(btnConceptoEgreso, esAdmin);
        setVisible(btnFormaPago,     esAdmin);

        // Facturación
        boolean verFacturacion = esAdmin || esCajero;
        setVisible(seccionFacturacion, verFacturacion);
        setVisible(btnReciboCaja,    verFacturacion);
        setVisible(btnClientes,      esAdmin || esCajero);
        setVisible(btnPerfiles,      esAdmin);
        setVisible(btnUsuarios,      esAdmin);

        // Inventario
        setVisible(seccionInventario, esAdmin);
        setVisible(btnInventarioLog, esAdmin);
        setVisible(btnInsumos,       esAdmin);
        setVisible(btnPresentacion,  esAdmin);
        setVisible(btnCategoriaInsumo, esAdmin);
        setVisible(btnPQRS,          esAdmin);

        // Configuración
        setVisible(seccionConfiguracion, esAdmin);
        setVisible(btnSedes,         esAdmin);
    }

    // ─────────────────────────────────────────────────────────────
    // TOGGLE SIDEBAR
    // ─────────────────────────────────────────────────────────────

    /** Expande o colapsa el sidebar con animación. */
    @FXML
    private void toggleSidebar() {
        double destino  = sidebarExpandido ? SIDEBAR_ANCHO_MINI : SIDEBAR_ANCHO_COMPLETO;
        boolean colapsa = sidebarExpandido;
        if (colapsa) ocultarTextosSidebar();

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
            if (!colapsa) mostrarTextosSidebar();
            setVisible(lblTopbarBrand, colapsa);
        });
        anim.play();
        sidebarExpandido = !sidebarExpandido;
    }

    // ─────────────────────────────────────────────────────────────
    // NAVEGACIÓN
    // ─────────────────────────────────────────────────────────────

    /** Regresa al panel de bienvenida del dashboard. */
    @FXML
    private void mostrarDashboard() {
        marcarBotonActivo(btnDashboard);
        lblBienvenida.setText("📊  Dashboard");
        lblRol.setText("Información general del restaurante");
        if (contenidoPrincipal.getChildren().size() > 1) {
            contenidoPrincipal.getChildren().subList(1, contenidoPrincipal.getChildren().size()).clear();
        }
    }

    @FXML private void mostrarVistaMesas()           { marcarBotonActivo(btnMesas);           cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaMesas.fxml"); }
    @FXML private void mostrarVistaPlatos()          { marcarBotonActivo(btnPlatos);          cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPlatos.fxml"); }
    @FXML private void mostrarVistaCategoriasPlato() { marcarBotonActivo(btnCategoriasPlato); cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaCategoriasPlato.fxml"); }
    @FXML private void mostrarVistaPedidos()         { marcarBotonActivo(btnPedidos);         cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPedidos.fxml"); }
    @FXML private void mostrarVistaComandas()        { marcarBotonActivo(btnComandas);        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaComandas.fxml"); }
    @FXML private void mostrarVistaAreasMesa()       { marcarBotonActivo(btnAreasMesa);       cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaAreasMesa.fxml"); }
    @FXML private void mostrarVistaEgresos()         { marcarBotonActivo(btnEgresos);         cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaEgresos.fxml"); }
    @FXML private void mostrarVistaConceptoEgreso()  { marcarBotonActivo(btnConceptoEgreso);  cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaConceptoEgreso.fxml"); }
    @FXML private void mostrarVistaFormaPago()       { marcarBotonActivo(btnFormaPago);       cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaFormaPago.fxml"); }
    @FXML private void mostrarVistaReciboCaja()      { marcarBotonActivo(btnReciboCaja);      cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaReciboCaja.fxml"); }
    @FXML private void mostrarVistaClientes()        { marcarBotonActivo(btnClientes);        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaClientes.fxml"); }
    @FXML private void mostrarVistaPerfiles()        { marcarBotonActivo(btnPerfiles);        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPerfiles.fxml"); }
    @FXML private void mostrarVistaUsuarios()        { marcarBotonActivo(btnUsuarios);        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaUsuarios.fxml"); }
    @FXML private void mostrarVistaInventarioLog()   { marcarBotonActivo(btnInventarioLog);   cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaInventarioLog.fxml"); }
    @FXML private void mostrarVistaInsumos()         { marcarBotonActivo(btnInsumos);         cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaInsumos.fxml"); }
    @FXML private void mostrarVistaPresentacion()    { marcarBotonActivo(btnPresentacion);    cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPresentacion.fxml"); }
    @FXML private void mostrarVistaCategoriaInsumo() { marcarBotonActivo(btnCategoriaInsumo); cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaCategoriaInsumo.fxml"); }
    @FXML private void mostrarVistaPQRS()            { marcarBotonActivo(btnPQRS);            cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPQRS.fxml"); }
    @FXML private void mostrarVistaSedes()           { marcarBotonActivo(btnSedes);           cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaSedes.fxml"); }

    // ─────────────────────────────────────────────────────────────
    // SPINNER DE CARGA
    // ─────────────────────────────────────────────────────────────

    /** Inicializa el spinner de carga con MaterialFX. */
    private void inicializarSpinner() {
        spinnerCarga = new MFXProgressSpinner();
        spinnerCarga.setRadius(40);
        VBox spinnerContainer = new VBox(20);
        spinnerContainer.setAlignment(Pos.CENTER);
        spinnerContainer.getChildren().addAll(spinnerCarga, new Label("Cargando..."));
        StackPane spinnerWrapper = new StackPane(spinnerContainer);
        spinnerWrapper.setAlignment(Pos.CENTER);
        spinnerWrapper.setStyle("-fx-background-color: rgba(248, 249, 250, 0.95);"
                + "-fx-border-color: rgba(99, 102, 241, 0.2);"
                + "-fx-border-width: 2; -fx-border-radius: 12;"
                + "-fx-background-radius: 12; -fx-padding: 40;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0.3, 0, 5);");
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
        new FadeTransition(Duration.millis(200), wrapper) {{ setFromValue(0); setToValue(1); play(); }};
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

    // ─────────────────────────────────────────────────────────────
    // PRECARGA DE VISTAS
    // ─────────────────────────────────────────────────────────────

    /**
     * Precarga las vistas más usadas en un hilo secundario
     * para eliminar el lag al navegar por primera vez.
     */
    private void precargarVistasComunes(String perfil) {
        if (vistasPreCargadas) return;
        vistasPreCargadas = true;
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
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
        Thread thread = new Thread(tareaPreCarga);
        thread.setDaemon(true);
        thread.start();
    }

    // ─────────────────────────────────────────────────────────────
    // CARGA ASÍNCRONA DE VISTAS
    // ─────────────────────────────────────────────────────────────

    /** Carga una vista FXML en el panel principal con spinner de feedback. */
    private void cargarVista(String rutaFxml) {
        mostrarSpinner();
        long tiempoInicio = System.currentTimeMillis();
        Task<Parent> tarea = new Task<Parent>() {
            @Override
            protected Parent call() throws Exception {
                return new FXMLLoader(getClass().getResource(rutaFxml)).load();
            }
        };
        tarea.setOnSucceeded(event -> {
            Parent vista = tarea.getValue();
            long restante = Math.max(0, 300 - (System.currentTimeMillis() - tiempoInicio));
            new Timeline(new KeyFrame(Duration.millis(restante), e -> {
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

    // ─────────────────────────────────────────────────────────────
    // CERRAR SESIÓN
    // ─────────────────────────────────────────────────────────────

    /** Cierra la sesión activa y vuelve al login. */
    @FXML
    private void cerrarSesion() {
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

    // ─────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────

    /** Resalta el botón activo en el menú. */
    private void marcarBotonActivo(MFXButton boton) {
        if (botonActivo != null) botonActivo.getStyleClass().remove("menu-btn-active");
        boton.getStyleClass().add("menu-btn-active");
        botonActivo = boton;
    }

    /** Genera hasta 2 iniciales del nombre para el avatar. */
    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) return "?";
        String[] partes = nombre.trim().split("\\s+");
        String ini = String.valueOf(partes[0].charAt(0)).toUpperCase();
        if (partes.length > 1) ini += String.valueOf(partes[1].charAt(0)).toUpperCase();
        return ini;
    }

    private void ocultarTextosSidebar() { setVisible(lblSidebarBrand, false); setVisible(lblSidebarSub, false); }
    private void mostrarTextosSidebar()  { setVisible(lblSidebarBrand, true);  setVisible(lblSidebarSub, true);  }

    /** Configura visibilidad y managed de un nodo en un solo llamado. */
    private void setVisible(javafx.scene.Node nodo, boolean visible) {
        nodo.setVisible(visible);
        nodo.setManaged(visible);
    }
}
