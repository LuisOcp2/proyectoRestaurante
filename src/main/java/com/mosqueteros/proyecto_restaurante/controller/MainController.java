package com.mosqueteros.proyecto_restaurante.controller;

import com.mosqueteros.proyecto_restaurante.util.SessionUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
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

public class MainController {

    // ── Topbar ────────────────────────────────────────────────────────────
    @FXML private MFXButton btnHamburger;
    @FXML private Label     lblTopbarBrand;
    @FXML private Label     lblAvatar;
    @FXML private Label     lblNombreUsuario;
    @FXML private Label     lblRolUsuario;
    @FXML private MFXButton btnCerrarSesion;

    // ── Sidebar ───────────────────────────────────────────────────────────
    @FXML private VBox      sidebar;
    @FXML private VBox      sidebarBrandBox;
    @FXML private Label     lblSidebarBrand;
    @FXML private Label     lblSidebarSub;
    @FXML private Label     seccionAdmin;

    // Botones menú admin
    @FXML private MFXButton btnUsuarios;
    @FXML private MFXButton btnPerfiles;
    @FXML private MFXButton btnInsumos;
    @FXML private MFXButton btnEgresos;
    @FXML private MFXButton btnSedes;

    // Todos los botones de menú (para gestionar estado activo)
    @FXML private MFXButton btnDashboard;
    @FXML private MFXButton btnMesas;
    @FXML private MFXButton btnPedidos;
    @FXML private MFXButton btnPlatos;
    @FXML private MFXButton btnPQRS;

    // ── Contenido ─────────────────────────────────────────────────────────
    @FXML private StackPane contenidoPrincipal;
    @FXML private Label     lblBienvenida;
    @FXML private Label     lblRol;

    // Estado sidebar
    private boolean sidebarExpandido = true;
    private static final double SIDEBAR_ANCHO_COMPLETO = 260;
    private static final double SIDEBAR_ANCHO_MINI     = 64;

    // Botón activo actual
    private MFXButton botonActivo = null;
    
    // Flag para controlar la precarga
    private boolean vistasPreCargadas = false;
    
    // Spinner de carga
    private MFXProgressSpinner spinnerCarga;

    // ─────────────────────────────────────────────────────────────────────
    // Inicialización (llamada desde LoginController tras login exitoso)
    // ─────────────────────────────────────────────────────────────────────
    public void inicializarDashboard() {
        String perfil = SessionUtil.obtenerRol();
        String login  = SessionUtil.obtenerUsuario() != null
                        ? SessionUtil.obtenerUsuario().obtenerNombre() : "Usuario";

        // ── Topbar: avatar + nombre + rol ──
        lblAvatar.setText(obtenerIniciales(login));
        lblNombreUsuario.setText(login);
        lblRolUsuario.setText(perfil);

        // ── Panel bienvenida ──
        lblBienvenida.setText("¡Bienvenido, " + login + "!");
        lblRol.setText("Rol activo: " + perfil);

        // ── Visibilidad sección Admin ──
        boolean esAdmin = "Administrador".equals(perfil);
        setVisible(seccionAdmin, esAdmin);
        setVisible(btnUsuarios,  esAdmin);
        setVisible(btnPerfiles,  esAdmin);
        setVisible(btnInsumos,   esAdmin);
        setVisible(btnEgresos,   esAdmin);
        setVisible(btnSedes,     esAdmin);

        // Marcar Dashboard como activo
        marcarBotonActivo(btnDashboard);
        
        // Inicializar spinner de carga
        inicializarSpinner();
        
        // Precargar vistas en background para mejorar rendimiento
        precargarVistasComunes();
    }
    
    /**
     * Inicializa el spinner de carga de MaterialFX.
     */
    private void inicializarSpinner() {
        spinnerCarga = new MFXProgressSpinner();
        spinnerCarga.setRadius(40);
        
        // Crear un VBox para centrar el spinner con un label
        VBox spinnerContainer = new VBox(20);
        spinnerContainer.setAlignment(Pos.CENTER);
        spinnerContainer.getChildren().addAll(
            spinnerCarga,
            new Label("Cargando...")
        );
        
        // Wrapper para centrar el spinner con fondo semi-transparente
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
        
        // Guardar referencia al wrapper
        spinnerCarga.setUserData(spinnerWrapper);
        
        // NO agregar aquí, se agregará cuando se muestre
    }
    
    /**
     * Muestra el spinner de carga con animación fade-in.
     */
    private void mostrarSpinner() {
        StackPane wrapper = (StackPane) spinnerCarga.getUserData();
        if (wrapper == null) return;
        
        // Asegurar que el wrapper esté en el contenido principal
        if (!contenidoPrincipal.getChildren().contains(wrapper)) {
            contenidoPrincipal.getChildren().add(wrapper);
        }
        
        wrapper.setVisible(true);
        wrapper.setOpacity(0);
        wrapper.toFront(); // Traer al frente
        
        FadeTransition fade = new FadeTransition(Duration.millis(200), wrapper);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
    
    /**
     * Oculta el spinner de carga con animación fade-out.
     */
    private void ocultarSpinner() {
        StackPane wrapper = (StackPane) spinnerCarga.getUserData();
        if (wrapper == null) return;
        
        FadeTransition fade = new FadeTransition(Duration.millis(200), wrapper);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            wrapper.setVisible(false);
            // Remover del contenido principal para limpiar
            contenidoPrincipal.getChildren().remove(wrapper);
        });
        fade.play();
    }
    
    /**
     * Precarga las vistas más comunes en background para eliminar el lag
     * al hacer clic por primera vez en los botones del menú.
     */
    private void precargarVistasComunes() {
        if (vistasPreCargadas) return;
        vistasPreCargadas = true;
        
        Task<Void> tareaPreCarga = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Lista de vistas a precargar
                String[] vistasComunes = {
                    "/com/mosqueteros/proyecto_restaurante/view/VistaMesas.fxml",
                    "/com/mosqueteros/proyecto_restaurante/view/VistaPedidos.fxml",
                    "/com/mosqueteros/proyecto_restaurante/view/VistaPlatos.fxml",
                    "/com/mosqueteros/proyecto_restaurante/view/VistaPQRS.fxml"
                };
                
                for (String ruta : vistasComunes) {
                    try {
                        // Cargar FXML en background
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
                        Parent vista = loader.load();
                        
                        // Guardar en caché (debe hacerse en el hilo de JavaFX)
                       Platform.runLater(() -> vistaCache.put(ruta, vista));
                        
                        // Pequeña pausa para no saturar
                        Thread.sleep(50);
                    } catch (Exception e) {
                        System.err.println("No se pudo precargar: " + ruta);
                    }
                }
                return null;
            }
        };
        
        // Ejecutar en thread separado
        Thread thread = new Thread(tareaPreCarga);
        thread.setDaemon(true);
        thread.start();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Toggle Sidebar (botón hamburger)
    // ─────────────────────────────────────────────────────────────────────
    @FXML
    private void toggleSidebar(ActionEvent event) {
        double destino = sidebarExpandido ? SIDEBAR_ANCHO_MINI : SIDEBAR_ANCHO_COMPLETO;
        boolean colapsando = sidebarExpandido;

        // Si va a colapsar, ocultar textos inmediatamente para evitar recorte
        if (colapsando) {
            ocultarTextosSidebar();
        }

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

        // Cuando termina de expandir, mostrar textos
        anim.setOnFinished(e -> {
            if (!colapsando) {
                mostrarTextosSidebar();
            }
            // Mostrar/ocultar la marca en la topbar (modo mini)
            setVisible(lblTopbarBrand, colapsando);
        });

        anim.play();
        sidebarExpandido = !sidebarExpandido;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Navegación
    // ─────────────────────────────────────────────────────────────────────
    @FXML
    private void mostrarDashboard(ActionEvent event) {
        marcarBotonActivo(btnDashboard);
        lblBienvenida.setText("📊  Dashboard");
        lblRol.setText("Información general del restaurante");
        // Mostrar el welcome panel (conservar sólo el primer hijo)
        if (contenidoPrincipal.getChildren().size() > 1) {
            contenidoPrincipal.getChildren().subList(1, contenidoPrincipal.getChildren().size()).clear();
        }
    }

    @FXML
    private void mostrarVistaMesas(ActionEvent event) {
        marcarBotonActivo(btnMesas);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaMesas.fxml");
    }

    @FXML
    private void mostrarVistaPlatos(ActionEvent event) {
        marcarBotonActivo(btnPlatos);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPlatos.fxml");
    }

    @FXML
    private void mostrarVistaPedidos(ActionEvent event) {
        marcarBotonActivo(btnPedidos);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPedidos.fxml");
    }

    @FXML
    private void mostrarVistaPQRS(ActionEvent event) {
        marcarBotonActivo(btnPQRS);
        cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPQRS.fxml");
    }

    // ─────────────────────────────────────────────────────────────────────
    // Helpers privados
    // ─────────────────────────────────────────────────────────────────────

 private final java.util.Map<String, Parent> vistaCache = new java.util.HashMap<>();

    /**
     * Carga un FXML en el StackPane de contenido con spinner de carga.
     * Siempre muestra el spinner por 1 segundo para feedback visual consistente.
     */
private void cargarVista(String rutaFxml) {
    mostrarSpinner();
    long tiempoInicio = System.currentTimeMillis();
    cargarVistaAsync(rutaFxml, tiempoInicio);
}
    
    /**
     * Carga una vista de forma asíncrona para evitar bloquear el UI thread.
     * Muestra el spinner por mínimo 10 segundos para feedback visual consistente.
     */
private void cargarVistaAsync(String rutaFxml, long tiempoInicio) {
    Task<Parent> tarea = new Task<Parent>() {
        @Override
        protected Parent call() throws Exception {
            // NUEVA instancia en cada navegación → estado limpio
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            return loader.load();
        }
    };

    tarea.setOnSucceeded(event -> {
        Parent vista = tarea.getValue();

        long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;
        long tiempoRestante = Math.max(0, 300 - tiempoTranscurrido); // 300ms mínimo

        Timeline delay = new Timeline(
            new KeyFrame(Duration.millis(tiempoRestante), e -> {
                ocultarSpinner();
                mostrarVistaConAnimacion(vista);
            })
        );
        delay.play();
    });

    tarea.setOnFailed(event -> {
        System.err.println("Error al cargar vista [" + rutaFxml + "]: "
                + tarea.getException().getMessage());
        tarea.getException().printStackTrace();
        ocultarSpinner();
    });

    Thread thread = new Thread(tarea);
    thread.setDaemon(true);
    thread.start();
}
    /**
     * Muestra una vista sin animación (carga instantánea).
     */
    private void mostrarVistaConAnimacion(Parent vista) {
        contenidoPrincipal.getChildren().setAll(vista);
    }

    /**
     * Gestiona la clase CSS 'menu-btn-active' para el botón activo.
     */
    private void marcarBotonActivo(MFXButton boton) {
        if (botonActivo != null) {
            botonActivo.getStyleClass().remove("menu-btn-active");
        }
        boton.getStyleClass().add("menu-btn-active");
        botonActivo = boton;
    }

    /** Genera las iniciales del usuario (máx. 2 caracteres). */
    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) return "?";
        String[] partes = nombre.trim().split("\\s+");
        String ini = String.valueOf(partes[0].charAt(0)).toUpperCase();
        if (partes.length > 1) ini += String.valueOf(partes[1].charAt(0)).toUpperCase();
        return ini;
    }

    /** Oculta los textos del sidebar (al colapsar). */
    private void ocultarTextosSidebar() {
        setVisible(lblSidebarBrand, false);
        setVisible(lblSidebarSub,   false);
    }

    /** Muestra los textos del sidebar (al expandir). */
    private void mostrarTextosSidebar() {
        setVisible(lblSidebarBrand, true);
        setVisible(lblSidebarSub,   true);
    }

    /** Configura visibilidad y managed en un solo llamado. */
    private void setVisible(javafx.scene.Node nodo, boolean visible) {
        nodo.setVisible(visible);
        nodo.setManaged(visible);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Cerrar sesión
    // ─────────────────────────────────────────────────────────────────────
    @FXML
    private void cerrarSesion(ActionEvent event) {
        SessionUtil.limpiarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
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
