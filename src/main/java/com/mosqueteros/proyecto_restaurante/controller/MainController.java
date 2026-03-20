package com.mosqueteros.proyecto_restaurante.controller;

import com.mosqueteros.proyecto_restaurante.App;
import com.mosqueteros.proyecto_restaurante.util.Alertas;
import com.mosqueteros.proyecto_restaurante.util.ConfiguracionUtil;
import com.mosqueteros.proyecto_restaurante.util.SessionUtil;
import com.mosqueteros.proyecto_restaurante.util.ThemeManager;
import com.mosqueteros.proyecto_restaurante.dao.SedeDAO;
import com.mosqueteros.proyecto_restaurante.dao.ConfiguracionDAO;
import com.mosqueteros.proyecto_restaurante.model.Sede;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.scene.control.Label;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import io.github.palexdev.materialfx.controls.*;
import org.kordamp.ikonli.javafx.FontIcon;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador principal del Dashboard y menú lateral.
 * Gestiona la navegación entre los diferentes módulos del sistema.
 */
public class MainController {

    // ── Topbar ─────────────────────────────────────────────────────
    @FXML private Label     lblTopbarBrand;
    @FXML private VBox      topbarBrandBlock;
    @FXML private Label     lblTopbarSubline;
    @FXML private Label     lblTopbarSede;
    @FXML private HBox      chipTopbarSede;
    @FXML private Label     lblTopbarModulo;
    @FXML private Label     lblTopbarHora;
    @FXML private Label     lblAvatar;
    @FXML private Label     lblNombreUsuario;
    @FXML private Label     lblRolUsuario;
    @FXML private MFXButton btnCerrarSesion;

    // ── Sidebar ───────────────────────────────────────────────────
    @FXML private VBox  sidebar;
    @FXML private Label lblSidebarBrand;
    @FXML private Label lblSidebarSub;
    @FXML private VBox  menuPedidos;
    @FXML private VBox  menuEgresos;
    @FXML private VBox  menuFacturacion;
    @FXML private VBox  menuInventario;
    @FXML private VBox  menuConfiguracion;

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
    @FXML private MFXButton btnConfiguracion;
    @FXML private MFXButton btnSedes;

    // ── Contenido principal ──────────────────────────────────────────
    @FXML private StackPane contenidoPrincipal;
    @FXML private Label     lblBienvenida;
    @FXML private Label     lblRol;

    /** Estado del sidebar: expandido o colapsado */
    private boolean sidebarExpandido = true;
    private static final double SIDEBAR_ANCHO_COMPLETO = 260;
    private static final double SIDEBAR_ANCHO_MINI     = 76;

    /** Botón del menú actualmente resaltado */
    private MFXButton botonActivo = null;

    /** Caché de vistas cargadas previamente para navegación rápida */
    private final java.util.Map<String, Parent> vistaCache = new java.util.HashMap<>();
    private boolean vistasPreCargadas = false;
    private MFXProgressSpinner spinnerCarga;
    private boolean listenerDensidadRegistrado = false;
    private final java.util.Map<VBox, Label> encabezadoPorGrupo = new java.util.HashMap<>();
    private final java.util.Map<Label, Label> indicadorPorEncabezado = new java.util.HashMap<>();
    private final java.util.Map<javafx.scene.Node, Timeline> hoverAnimations = new java.util.HashMap<>();
    private final java.util.Map<javafx.scene.Node, Timeline> activeAnimations = new java.util.HashMap<>();
    private final java.util.Map<MFXButton, String> textoOriginalBotones = new java.util.HashMap<>();
    private final java.util.Map<MFXButton, Region> pillPorBoton = new java.util.HashMap<>();
    private final java.util.Map<Region, Timeline> pillAnimations = new java.util.HashMap<>();
    private Timeline relojTopbar;
    private final ContextMenu menuSedesTopbar = new ContextMenu();

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
        String nombreEmpresa = ConfiguracionUtil.nombreEmpresa();
        // getUsunombre() es el getter correcto en la clase Usuario
        String nombre = SessionUtil.getUser() != null
                ? SessionUtil.getUser().getUsunombre() : "Usuario";

        lblTopbarBrand.setText("🍽  " + nombreEmpresa);
        lblSidebarBrand.setText(nombreEmpresa);
        if (lblTopbarSubline != null) {
            lblTopbarSubline.setText("Control operativo multisede");
        }
        if (lblTopbarSede != null) {
            String sedeActiva = SessionUtil.getSedeActivaNombre();
            lblTopbarSede.setText((sedeActiva != null && !sedeActiva.isBlank()) ? sedeActiva : "Sede Principal");
        }
        if (lblTopbarModulo != null) {
            lblTopbarModulo.setText("Dashboard");
        }

        lblAvatar.setText(obtenerIniciales(nombre));
        lblNombreUsuario.setText(nombre);
        lblRolUsuario.setText(perfil);

        lblBienvenida.setText("📊  Bienvenido, " + nombre + "!");
        lblRol.setText("Rol activo: " + perfil);

        aplicarVisibilidadPorRol(perfil);
        prepararSidebarPremium();
        inicializarTogglesDeGruposSidebar();
        inicializarMicrointeraccionesSidebar();
        inicializarRelojTopbar();
        inicializarSelectorSedeTopbar();
        actualizarGruposSidebarPorDensidad();
        marcarBotonActivo(btnDashboard);
        inicializarSpinner();
        precargarVistasComunes(perfil);

        if (contenidoPrincipal.getScene() != null) {
            registrarListenerDensidad(contenidoPrincipal.getScene());
        } else {
            contenidoPrincipal.sceneProperty().addListener((obs, anterior, actual) -> {
                if (actual != null) {
                    registrarListenerDensidad(actual);
                    actualizarGruposSidebarPorDensidad();
                }
            });
        }
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
        setVisible(btnConfiguracion, esAdmin);
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
        if (colapsa) {
            aplicarEstadoSidebarColapsado(true);
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
        anim.setOnFinished(e -> {
            if (!colapsa) {
                mostrarTextosSidebar();
                aplicarEstadoSidebarColapsado(false);
            }
            setVisible(topbarBrandBlock, colapsa);
        });
        anim.play();
        sidebarExpandido = !sidebarExpandido;
    }

    private void prepararSidebarPremium() {
        java.util.List<MFXButton> botones = java.util.List.of(
                btnDashboard, btnMesas, btnAreasMesa, btnPlatos, btnCategoriasPlato,
                btnPedidos, btnComandas, btnEgresos, btnConceptoEgreso, btnFormaPago,
                btnReciboCaja, btnClientes, btnPerfiles, btnUsuarios, btnInventarioLog,
                btnInsumos, btnPresentacion, btnCategoriaInsumo, btnPQRS, btnConfiguracion,
                btnSedes
        );

        botones.stream().filter(java.util.Objects::nonNull).forEach(btn -> {
            textoOriginalBotones.putIfAbsent(btn, btn.getText());
            if (btn.getTooltip() == null) {
                btn.setTooltip(new Tooltip(btn.getText()));
            }
            decorarBotonConPill(btn);
        });
    }

    private void decorarBotonConPill(MFXButton boton) {
        if (pillPorBoton.containsKey(boton)) {
            return;
        }
        Node iconoOriginal = boton.getGraphic();
        if (iconoOriginal == null) {
            return;
        }

        Region pill = new Region();
        pill.getStyleClass().add("menu-btn-pill");

        HBox graphicBox = new HBox(8, pill, iconoOriginal);
        graphicBox.setAlignment(Pos.CENTER_LEFT);
        graphicBox.getStyleClass().add("menu-btn-graphic");

        boton.setGraphic(graphicBox);
        pillPorBoton.put(boton, pill);
    }

    private void aplicarEstadoSidebarColapsado(boolean colapsado) {
        if (sidebar == null) {
            return;
        }

        sidebar.getStyleClass().remove("sidebar-collapsed");
        if (colapsado) {
            sidebar.getStyleClass().add("sidebar-collapsed");
        }

        alternarSeccionesSidebar(!colapsado);

        textoOriginalBotones.forEach((boton, textoOriginal) -> {
            boton.setText(colapsado ? "" : textoOriginal);
            if (boton.getTooltip() == null) {
                boton.setTooltip(new Tooltip(textoOriginal));
            }
            boton.getTooltip().setText(textoOriginal);
        });
    }

    private void inicializarRelojTopbar() {
        if (lblTopbarHora == null) {
            return;
        }
        actualizarRelojTopbar();
        if (relojTopbar != null) {
            relojTopbar.stop();
        }
        relojTopbar = new Timeline(new KeyFrame(Duration.seconds(1), e -> actualizarRelojTopbar()));
        relojTopbar.setCycleCount(Timeline.INDEFINITE);
        relojTopbar.play();
    }

    private void inicializarSelectorSedeTopbar() {
        if (chipTopbarSede == null) {
            return;
        }
        chipTopbarSede.setOnMouseClicked(e -> {
            construirMenuSedesTopbar();
            if (!menuSedesTopbar.getItems().isEmpty()) {
                menuSedesTopbar.show(chipTopbarSede, e.getScreenX(), e.getScreenY() + 8);
            }
        });
    }

    private void construirMenuSedesTopbar() {
        menuSedesTopbar.getItems().clear();
        try {
            List<Sede> sedes = SedeDAO.listarTodas();
            Long sedeActivaId = SessionUtil.getSedeActivaId();
            for (Sede sede : sedes) {
                MenuItem item = new MenuItem(sede.getNombre());
                boolean activa = sedeActivaId != null && sedeActivaId.equals(sede.getId());
                if (activa) {
                    item.setText("✓ " + sede.getNombre());
                    item.setDisable(true);
                } else {
                    item.setOnAction(evt -> cambiarSedeActiva(sede));
                }
                menuSedesTopbar.getItems().add(item);
            }
        } catch (Exception e) {
            MenuItem error = new MenuItem("No se pudieron cargar sedes");
            error.setDisable(true);
            menuSedesTopbar.getItems().add(error);
        }
    }

    private void cambiarSedeActiva(Sede sede) {
        if (sede == null) {
            return;
        }
        SessionUtil.setSedeActiva(sede.getId(), sede.getNombre());
        persistirPreferenciaSedeActiva(sede.getId());
        if (lblTopbarSede != null) {
            lblTopbarSede.setText(sede.getNombre());
        }
        refrescarVistaActual();
    }

    private void persistirPreferenciaSedeActiva(Long sedeId) {
        long usuId = SessionUtil.getUserId();
        if (usuId <= 0 || sedeId == null) {
            return;
        }
        String clave = ConfiguracionUtil.claveSedeActivaPorUsuario(usuId);
        try {
            ConfiguracionDAO.guardarPorClave(clave, String.valueOf(sedeId));
        } catch (Exception e) {
            System.err.println("No se pudo guardar preferencia de sede: " + e.getMessage());
        }
    }

    private void refrescarVistaActual() {
        if (botonActivo == null) {
            mostrarDashboard();
            return;
        }
        if (botonActivo == btnDashboard) {
            mostrarDashboard();
        } else if (botonActivo == btnMesas) {
            mostrarVistaMesas();
        } else if (botonActivo == btnAreasMesa) {
            mostrarVistaAreasMesa();
        } else if (botonActivo == btnPlatos) {
            mostrarVistaPlatos();
        } else if (botonActivo == btnCategoriasPlato) {
            mostrarVistaCategoriasPlato();
        } else if (botonActivo == btnPedidos) {
            mostrarVistaPedidos();
        } else if (botonActivo == btnComandas) {
            mostrarVistaComandas();
        } else if (botonActivo == btnEgresos) {
            mostrarVistaEgresos();
        } else if (botonActivo == btnConceptoEgreso) {
            mostrarVistaConceptoEgreso();
        } else if (botonActivo == btnFormaPago) {
            mostrarVistaFormaPago();
        } else if (botonActivo == btnReciboCaja) {
            mostrarVistaReciboCaja();
        } else if (botonActivo == btnClientes) {
            mostrarVistaClientes();
        } else if (botonActivo == btnPerfiles) {
            mostrarVistaPerfiles();
        } else if (botonActivo == btnUsuarios) {
            mostrarVistaUsuarios();
        } else if (botonActivo == btnInventarioLog) {
            mostrarVistaInventarioLog();
        } else if (botonActivo == btnInsumos) {
            mostrarVistaInsumos();
        } else if (botonActivo == btnPresentacion) {
            mostrarVistaPresentacion();
        } else if (botonActivo == btnCategoriaInsumo) {
            mostrarVistaCategoriaInsumo();
        } else if (botonActivo == btnPQRS) {
            mostrarVistaPQRS();
        } else if (botonActivo == btnConfiguracion) {
            mostrarVistaConfiguracion();
        } else if (botonActivo == btnSedes) {
            mostrarVistaSedes();
        }
    }

    private void actualizarRelojTopbar() {
        if (lblTopbarHora == null) {
            return;
        }
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        lblTopbarHora.setText(hora);
    }

    private void alternarSeccionesSidebar(boolean visibles) {
        setVisible(seccionPedidos, visibles);
        setVisible(seccionEgresos, visibles);
        setVisible(seccionFacturacion, visibles);
        setVisible(seccionInventario, visibles);
        setVisible(seccionConfiguracion, visibles);
    }

    private void registrarListenerDensidad(Scene scene) {
        if (listenerDensidadRegistrado || scene == null) {
            return;
        }
        scene.widthProperty().addListener((obs, oldV, newV) -> actualizarGruposSidebarPorDensidad());
        scene.heightProperty().addListener((obs, oldV, newV) -> actualizarGruposSidebarPorDensidad());
        listenerDensidadRegistrado = true;
    }

    private void actualizarGruposSidebarPorDensidad() {
        if (sidebar == null || sidebar.getScene() == null) {
            return;
        }
        // Mantener menús visibles por defecto en cualquier densidad.
        // El colapso por grupo se hace de forma manual al hacer clic en el label.
        expandirGrupoSinAnimacion(menuPedidos);
        expandirGrupoSinAnimacion(menuEgresos);
        expandirGrupoSinAnimacion(menuFacturacion);
        expandirGrupoSinAnimacion(menuInventario);
        expandirGrupoSinAnimacion(menuConfiguracion);
    }

    private void inicializarTogglesDeGruposSidebar() {
        configurarToggleGrupo(seccionPedidos, menuPedidos, "fas-utensils");
        configurarToggleGrupo(seccionEgresos, menuEgresos, "fas-shield-alt");
        configurarToggleGrupo(seccionFacturacion, menuFacturacion, "fas-receipt");
        configurarToggleGrupo(seccionInventario, menuInventario, "fas-boxes");
        configurarToggleGrupo(seccionConfiguracion, menuConfiguracion, "fas-cogs");
    }

    private void inicializarMicrointeraccionesSidebar() {
        java.util.List<MFXButton> botones = java.util.List.of(
                btnDashboard, btnMesas, btnAreasMesa, btnPlatos, btnCategoriasPlato,
                btnPedidos, btnComandas, btnEgresos, btnConceptoEgreso, btnFormaPago,
                btnReciboCaja, btnClientes, btnPerfiles, btnUsuarios, btnInventarioLog,
                btnInsumos, btnPresentacion, btnCategoriaInsumo, btnPQRS, btnConfiguracion,
                btnSedes
        );

        botones.stream().filter(java.util.Objects::nonNull).forEach(this::aplicarHoverPremium);
    }

    private void aplicarHoverPremium(MFXButton boton) {
        boton.setOnMouseEntered(event -> animarHoverBoton(boton, true));
        boton.setOnMouseExited(event -> animarHoverBoton(boton, false));
    }

    private void animarHoverBoton(MFXButton boton, boolean hover) {
        Timeline activa = hoverAnimations.get(boton);
        if (activa != null) {
            activa.stop();
        }

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(180),
                        new KeyValue(boton.scaleXProperty(), hover ? 1.012 : 1.0, Interpolator.EASE_BOTH),
                        new KeyValue(boton.scaleYProperty(), hover ? 1.012 : 1.0, Interpolator.EASE_BOTH),
                        new KeyValue(boton.translateXProperty(), hover ? 1.8 : 0.0, Interpolator.EASE_BOTH)
                )
        );
        hoverAnimations.put(boton, timeline);
        timeline.play();
    }

    private void configurarToggleGrupo(Label encabezado, VBox grupo, String iconLiteral) {
        if (encabezado == null || grupo == null) {
            return;
        }
        encabezadoPorGrupo.put(grupo, encabezado);
        if (!indicadorPorEncabezado.containsKey(encabezado)) {
            String tituloBase = encabezado.getText();
            Label indicador = new Label("-");
            indicador.getStyleClass().add("sidebar-group-indicator");

            FontIcon icono = new FontIcon(iconLiteral);
            icono.setIconSize(12);
            icono.getStyleClass().add("sidebar-group-icon");

            Label titulo = new Label(tituloBase);
            titulo.getStyleClass().add("sidebar-group-title");

            Region separador = new Region();
            javafx.scene.layout.HBox.setHgrow(separador, javafx.scene.layout.Priority.ALWAYS);

            javafx.scene.layout.HBox prefijo = new javafx.scene.layout.HBox(8, icono, titulo, separador, indicador);
            prefijo.setAlignment(Pos.CENTER_LEFT);
            prefijo.getStyleClass().add("sidebar-group-prefix");

            encabezado.setText("");
            encabezado.setGraphic(prefijo);
            encabezado.setContentDisplay(ContentDisplay.LEFT);
            encabezado.setGraphicTextGap(0);
            indicadorPorEncabezado.put(encabezado, indicador);
        }
        actualizarIndicadorGrupo(encabezado, true);
        encabezado.setOnMouseClicked(event -> alternarGrupoSidebar(grupo, !grupo.isManaged()));
    }

    private void expandirGrupoSinAnimacion(VBox grupo) {
        if (grupo == null) {
            return;
        }
        grupo.setVisible(true);
        grupo.setManaged(true);
        grupo.setOpacity(1.0);
        grupo.setMaxHeight(Region.USE_COMPUTED_SIZE);
        Label encabezado = encabezadoPorGrupo.get(grupo);
        actualizarIndicadorGrupo(encabezado, true);
    }

    private void alternarGrupoSidebar(VBox grupo, boolean expandir) {
        if (grupo == null) {
            return;
        }
        Label encabezado = encabezadoPorGrupo.get(grupo);

        double alturaObjetivo = Math.max(grupo.prefHeight(-1), grupo.getBoundsInLocal().getHeight());
        if (alturaObjetivo <= 0) {
            alturaObjetivo = 180;
        }

        if (expandir) {
            grupo.setManaged(true);
            grupo.setVisible(true);
            grupo.setMaxHeight(0);
            grupo.setOpacity(0.0);
            actualizarIndicadorGrupo(encabezado, true);
        }

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(220),
                        new KeyValue(grupo.maxHeightProperty(), expandir ? alturaObjetivo : 0, Interpolator.EASE_BOTH),
                        new KeyValue(grupo.opacityProperty(), expandir ? 1.0 : 0.0, Interpolator.EASE_BOTH)
                )
        );

        if (expandir) {
            timeline.setOnFinished(e -> grupo.setMaxHeight(Region.USE_COMPUTED_SIZE));
        } else {
            actualizarIndicadorGrupo(encabezado, false);
            timeline.setOnFinished(e -> {
                grupo.setVisible(false);
                grupo.setManaged(false);
            });
        }

        timeline.play();
    }

    private void actualizarIndicadorGrupo(Label encabezado, boolean expandido) {
        if (encabezado == null) {
            return;
        }
        Label indicador = indicadorPorEncabezado.get(encabezado);
        if (indicador != null) {
            animarIndicador(indicador, expandido ? "-" : "+");
        }
    }

    private void animarIndicador(Label indicador, String objetivo) {
        if (indicador == null) {
            return;
        }
        if (objetivo.equals(indicador.getText())) {
            return;
        }

        Object animacionActiva = indicador.getProperties().get("sidebar-indicator-animation");
        if (animacionActiva instanceof Timeline timelineActiva) {
            timelineActiva.stop();
        }

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(indicador.opacityProperty(), 1.0, Interpolator.EASE_BOTH),
                        new KeyValue(indicador.rotateProperty(), 0.0, Interpolator.EASE_BOTH),
                        new KeyValue(indicador.scaleXProperty(), 1.0, Interpolator.EASE_BOTH),
                        new KeyValue(indicador.scaleYProperty(), 1.0, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(Duration.millis(90),
                        event -> indicador.setText(objetivo),
                        new KeyValue(indicador.opacityProperty(), 0.2, Interpolator.EASE_BOTH),
                        new KeyValue(indicador.rotateProperty(), "+".equals(objetivo) ? -90.0 : 90.0, Interpolator.EASE_BOTH),
                        new KeyValue(indicador.scaleXProperty(), 0.72, Interpolator.EASE_BOTH),
                        new KeyValue(indicador.scaleYProperty(), 0.72, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(Duration.millis(180),
                        new KeyValue(indicador.opacityProperty(), 1.0, Interpolator.EASE_BOTH),
                        new KeyValue(indicador.rotateProperty(), 0.0, Interpolator.EASE_BOTH),
                        new KeyValue(indicador.scaleXProperty(), 1.0, Interpolator.EASE_BOTH),
                        new KeyValue(indicador.scaleYProperty(), 1.0, Interpolator.EASE_BOTH)
                )
        );

        indicador.getProperties().put("sidebar-indicator-animation", timeline);
        timeline.play();
    }

    // ─────────────────────────────────────────────────────────────
    // NAVEGACIÓN
    // ─────────────────────────────────────────────────────────────

    /** Regresa al panel de bienvenida del dashboard. */
    @FXML
    private void mostrarDashboard() {
        marcarBotonActivo(btnDashboard);
        actualizarModuloTopbar("Dashboard");
        lblBienvenida.setText("📊  Dashboard");
        lblRol.setText("Información general del restaurante");
        if (contenidoPrincipal.getChildren().size() > 1) {
            contenidoPrincipal.getChildren().subList(1, contenidoPrincipal.getChildren().size()).clear();
        }
    }

    @FXML private void mostrarVistaMesas()           { marcarBotonActivo(btnMesas);           actualizarModuloTopbar("Mesas");             cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaMesas.fxml"); }
    @FXML private void mostrarVistaPlatos()          { marcarBotonActivo(btnPlatos);          actualizarModuloTopbar("Platos");            cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPlatos.fxml"); }
    @FXML private void mostrarVistaCategoriasPlato() { marcarBotonActivo(btnCategoriasPlato); actualizarModuloTopbar("Categorias Plato");  cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaCategoriasPlato.fxml"); }
    @FXML private void mostrarVistaPedidos()         { marcarBotonActivo(btnPedidos);         actualizarModuloTopbar("Pedidos");           cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPedidos.fxml"); }
    @FXML private void mostrarVistaComandas()        { marcarBotonActivo(btnComandas);        actualizarModuloTopbar("Cocina / Comandas"); cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaComandas.fxml"); }
    @FXML private void mostrarVistaAreasMesa()       { marcarBotonActivo(btnAreasMesa);       actualizarModuloTopbar("Areas de Mesa");     cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaAreasMesa.fxml"); }
    @FXML private void mostrarVistaEgresos()         { marcarBotonActivo(btnEgresos);         actualizarModuloTopbar("Egresos");           cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaEgresos.fxml"); }
    @FXML private void mostrarVistaConceptoEgreso()  { marcarBotonActivo(btnConceptoEgreso);  actualizarModuloTopbar("Concepto Egreso");   cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaConceptoEgreso.fxml"); }
    @FXML private void mostrarVistaFormaPago()       { marcarBotonActivo(btnFormaPago);       actualizarModuloTopbar("Forma de Pago");     cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaFormaPago.fxml"); }
    @FXML private void mostrarVistaReciboCaja()      { marcarBotonActivo(btnReciboCaja);      actualizarModuloTopbar("Recibos de Caja");   cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaReciboCaja.fxml"); }
    @FXML private void mostrarVistaClientes()        { marcarBotonActivo(btnClientes);        actualizarModuloTopbar("Clientes");          cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaClientes.fxml"); }
    @FXML private void mostrarVistaPerfiles()        { marcarBotonActivo(btnPerfiles);        actualizarModuloTopbar("Perfiles / Roles");  cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPerfiles.fxml"); }
    @FXML private void mostrarVistaUsuarios()        { marcarBotonActivo(btnUsuarios);        actualizarModuloTopbar("Usuarios");          cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaUsuarios.fxml"); }
    @FXML private void mostrarVistaInventarioLog()   { marcarBotonActivo(btnInventarioLog);   actualizarModuloTopbar("Movimientos Bodega");cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaInventarioLog.fxml"); }
    @FXML private void mostrarVistaInsumos()         { marcarBotonActivo(btnInsumos);         actualizarModuloTopbar("Insumos");           cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaInsumos.fxml"); }
    @FXML private void mostrarVistaPresentacion()    { marcarBotonActivo(btnPresentacion);    actualizarModuloTopbar("Presentaciones");    cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPresentacion.fxml"); }
    @FXML private void mostrarVistaCategoriaInsumo() { marcarBotonActivo(btnCategoriaInsumo); actualizarModuloTopbar("Categoria Insumo");  cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaCategoriaInsumo.fxml"); }
    @FXML private void mostrarVistaPQRS()            { marcarBotonActivo(btnPQRS);            actualizarModuloTopbar("PQRS");              cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaPQRS.fxml"); }
    @FXML private void mostrarVistaConfiguracion()   { marcarBotonActivo(btnConfiguracion);   actualizarModuloTopbar("Configuracion");     cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaConfiguracion.fxml"); }
    @FXML private void mostrarVistaSedes()           { marcarBotonActivo(btnSedes);           actualizarModuloTopbar("Sedes");             cargarVista("/com/mosqueteros/proyecto_restaurante/view/VistaSedes.fxml"); }

    private void actualizarModuloTopbar(String modulo) {
        if (lblTopbarModulo != null && modulo != null && !modulo.isBlank()) {
            lblTopbarModulo.setText(modulo);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // SPINNER DE CARGA
    // ─────────────────────────────────────────────────────────────

    /** Inicializa el spinner de carga con JavaFX. */
    private void inicializarSpinner() {
        spinnerCarga = new MFXProgressSpinner();
        spinnerCarga.setPrefSize(80, 80);
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
       // new FadeTransition(Duration.millis(200), wrapper) {{ setFromValue(0); setToValue(1); play(); }};
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
            vistas.add("/com/mosqueteros/proyecto_restaurante/view/VistaConfiguracion.fxml");
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
            Scene scene = new Scene(root);
            scene.getStylesheets().add(App.class.getResource("styles/styles.css").toExternalForm());
            ThemeManager.aplicarTemaGlobal(scene);
            stage.setScene(scene);
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
        MFXButton anterior = botonActivo;
        if (anterior != null) {
            anterior.getStyleClass().remove("menu-btn-active");
            animarPill(anterior, false);
        }
        boton.getStyleClass().add("menu-btn-active");
        botonActivo = boton;
        animarPill(boton, true);
        animarEntradaBotonActivo(boton);
    }

    private void animarPill(MFXButton boton, boolean activo) {
        Region pill = pillPorBoton.get(boton);
        if (pill == null) {
            return;
        }

        Timeline animActiva = pillAnimations.get(pill);
        if (animActiva != null) {
            animActiva.stop();
        }

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(170),
                        new KeyValue(pill.opacityProperty(), activo ? 1.0 : 0.0, Interpolator.EASE_BOTH),
                        new KeyValue(pill.scaleYProperty(), activo ? 1.0 : 0.35, Interpolator.EASE_BOTH),
                        new KeyValue(pill.translateXProperty(), activo ? 0.0 : -2.0, Interpolator.EASE_BOTH)
                )
        );
        pillAnimations.put(pill, timeline);
        timeline.play();
    }

    private void animarEntradaBotonActivo(MFXButton boton) {
        if (boton == null) {
            return;
        }

        Timeline activa = activeAnimations.get(boton);
        if (activa != null) {
            activa.stop();
        }

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(boton.scaleXProperty(), 0.986, Interpolator.EASE_BOTH),
                        new KeyValue(boton.scaleYProperty(), 0.986, Interpolator.EASE_BOTH),
                        new KeyValue(boton.translateXProperty(), 0.0, Interpolator.EASE_BOTH),
                        new KeyValue(boton.opacityProperty(), 0.92, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(Duration.millis(180),
                        new KeyValue(boton.scaleXProperty(), 1.012, Interpolator.EASE_BOTH),
                        new KeyValue(boton.scaleYProperty(), 1.012, Interpolator.EASE_BOTH),
                        new KeyValue(boton.translateXProperty(), 1.8, Interpolator.EASE_BOTH),
                        new KeyValue(boton.opacityProperty(), 1.0, Interpolator.EASE_BOTH)
                )
        );

        activeAnimations.put(boton, timeline);
        timeline.play();
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
