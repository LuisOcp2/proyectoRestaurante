package com.mosqueteros.proyecto_restaurante.util;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Clase utilitaria para mostrar diálogos personalizados en la app.
 *
 * Arquitectura: overlay inyectado en el StackPane raiz del Stage principal.
 * NUNCA abre un Stage secundario, eliminando el bug GTK height>0 en Linux.
 *
 * El fondo oscuro se implementa con Region + CSS (NO con Rectangle + bind)
 * para evitar el loop de layout infinito que causaba el estiramiento del contenido.
 */
public final class Alertas {

    // ── Paleta de colores ────────────────────────────────────────────────────────────────
    private static final String COLOR_EXITO    = "#10B981";
    private static final String COLOR_ERROR    = "#EF4444";
    private static final String COLOR_AVISO    = "#F59E0B";
    private static final String COLOR_INFO     = "#6366F1";
    private static final String COLOR_FONDO    = "#FFFFFF";
    private static final String COLOR_TEXTO    = "#1E293B";
    private static final String COLOR_SUBTEXTO = "#64748B";
    private static final String COLOR_BORDE    = "#E2E8F0";

    /**
     * StackPane raiz registrado desde MainController.
     * El overlay del dialogo se inserta como hijo de este panel.
     */
    private static StackPane contenedorRaiz = null;

    private Alertas() {
        throw new UnsupportedOperationException("Clase utilitaria");
    }

    // ══ REGISTRO ══════════════════════════════════════════════════════════════════

    /**
     * Registra el StackPane raiz donde se inyectaran los overlays.
     * Llamar UNA VEZ en MainController.inicializarDashboard().
     */
    public static void registrarContenedor(StackPane contenedor) {
        contenedorRaiz = contenedor;
    }

    // ══ API PUBLICA ══════════════════════════════════════════════════════════════

    public static void exito(Node n, String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.EXITO, titulo, contenido, false, null);
    }
    public static void error(Node n, String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.ERROR, titulo, contenido, false, null);
    }
    public static void aviso(Node n, String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.AVISO, titulo, contenido, false, null);
    }
    public static void informacion(Node n, String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.INFO, titulo, contenido, false, null);
    }
    public static void exito(String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.EXITO, titulo, contenido, false, null);
    }
    public static void error(String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.ERROR, titulo, contenido, false, null);
    }
    public static void aviso(String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.AVISO, titulo, contenido, false, null);
    }
    public static void informacion(String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.INFO, titulo, contenido, false, null);
    }
    public static boolean confirmar(Node n, String titulo, String contenido) {
        return mostrarOverlayConfirmacion(titulo, contenido);
    }
    public static boolean confirmar(String titulo, String contenido) {
        return mostrarOverlayConfirmacion(titulo, contenido);
    }

    // ══ LOGICA INTERNA ══════════════════════════════════════════════════════════════

    private enum TipoAlerta {
        EXITO("✓", COLOR_EXITO, "Éxito"),
        ERROR("✕", COLOR_ERROR, "Error"),
        AVISO("⚠", COLOR_AVISO, "Advertencia"),
        INFO ("ℹ", COLOR_INFO,  "Información");

        final String icono;
        final String color;
        final String etiquetaDefecto;

        TipoAlerta(String icono, String color, String etiqueta) {
            this.icono           = icono;
            this.color           = color;
            this.etiquetaDefecto = etiqueta;
        }
    }

    /**
     * Muestra un dialogo informativo (1 boton) como overlay.
     *
     * CLAVE: el fondo oscuro es una Region con -fx-background-color CSS y
     * setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE). Esto hace que el StackPane
     * lo estire a su tamaño natural SIN crear un binding reactivo que dispare
     * recalculos de layout en cascada (loop infinito con Rectangle + bind).
     */
    private static void mostrarOverlay(TipoAlerta tipo,
                                       String titulo,
                                       String contenido,
                                       boolean esConfirmacion,
                                       Runnable callbackAceptar) {
        Platform.runLater(() -> {
            StackPane raiz = obtenerContenedor();
            if (raiz == null) return;

            // Fondo oscuro: Region CSS, NO Rectangle+bind
            Region fondo = crearFondoOscuro();

            VBox tarjeta = construirTarjeta(tipo, titulo, contenido);

            Button btnAceptar = crearBoton("Aceptar", tipo.color, true);

            HBox pie = new HBox(btnAceptar);
            pie.setAlignment(Pos.CENTER_RIGHT);
            pie.setPadding(new Insets(0, 28, 24, 28));
            tarjeta.getChildren().add(pie);

            // El overlay es un StackPane que ocupa todo el espacio disponible
            StackPane overlay = new StackPane(fondo, tarjeta);
            overlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            StackPane.setAlignment(tarjeta, Pos.CENTER);

            overlay.setOpacity(0);
            raiz.getChildren().add(overlay);
            overlay.toFront();

            FadeTransition entrada = new FadeTransition(Duration.millis(180), overlay);
            entrada.setFromValue(0);
            entrada.setToValue(1);
            entrada.play();

            btnAceptar.setOnAction(e -> cerrarOverlay(overlay, raiz, callbackAceptar));
        });
    }

    /**
     * Muestra un dialogo de confirmacion como overlay.
     * Bloquea el hilo llamante con Object.wait hasta que el usuario responde.
     */
    private static boolean mostrarOverlayConfirmacion(String titulo, String contenido) {
        final boolean[] resultado = {false};
        final Object bloqueo = new Object();

        Platform.runLater(() -> {
            StackPane raiz = obtenerContenedor();
            if (raiz == null) {
                synchronized (bloqueo) { bloqueo.notify(); }
                return;
            }

            Region fondo = crearFondoOscuro();

            VBox tarjeta = construirTarjeta(TipoAlerta.AVISO, titulo, contenido);

            Label lblExtra = new Label("¿Deseas continuar con esta acción?");
            lblExtra.setStyle(
                "-fx-font-size: 12px;"
                + "-fx-text-fill: " + COLOR_AVISO + ";"
                + "-fx-font-weight: 600;"
            );
            tarjeta.getChildren().add(lblExtra);

            Button btnAceptar  = crearBoton("Aceptar",  COLOR_ERROR, true);
            Button btnCancelar = crearBoton("Cancelar", COLOR_BORDE, false);

            Region espacio = new Region();
            HBox.setHgrow(espacio, Priority.ALWAYS);
            HBox pie = new HBox(10, btnCancelar, espacio, btnAceptar);
            pie.setAlignment(Pos.CENTER);
            pie.setPadding(new Insets(0, 28, 24, 28));
            tarjeta.getChildren().add(pie);

            StackPane overlay = new StackPane(fondo, tarjeta);
            overlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            StackPane.setAlignment(tarjeta, Pos.CENTER);

            overlay.setOpacity(0);
            raiz.getChildren().add(overlay);
            overlay.toFront();

            FadeTransition entrada = new FadeTransition(Duration.millis(180), overlay);
            entrada.setFromValue(0);
            entrada.setToValue(1);
            entrada.play();

            Runnable cerrar = () -> cerrarOverlay(overlay, raiz, () -> {
                synchronized (bloqueo) { bloqueo.notify(); }
            });

            btnAceptar.setOnAction(e -> {
                resultado[0] = true;
                cerrar.run();
            });
            btnCancelar.setOnAction(e -> cerrar.run());
        });

        if (!Platform.isFxApplicationThread()) {
            synchronized (bloqueo) {
                try { bloqueo.wait(30_000); }
                catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
        }
        return resultado[0];
    }

    /**
     * Crea el fondo oscuro del overlay usando Region + CSS.
     *
     * POR QUE Region y NO Rectangle + bind:
     *  - Rectangle.widthProperty().bind(raiz.widthProperty()) crea una
     *    dependencia reactiva: cada vez que el StackPane cambia de tamano,
     *    el Rectangle cambia, lo que fuerza otro layout pass del StackPane,
     *    que vuelve a cambiar el Rectangle → loop infinito que estira todo.
     *  - Region con setMaxSize(MAX, MAX) se estira pasivamente al tamano
     *    que el StackPane le asigna en el layout pass, SIN crear ninguna
     *    dependencia reactiva. No hay loop.
     */
    private static Region crearFondoOscuro() {
        Region fondo = new Region();
        fondo.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        // rgba via CSS hex no soporta alpha directamente; usamos -fx-opacity en el propio overlay
        fondo.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");
        return fondo;
    }

    /** Cierra el overlay con fade-out y lo remueve del StackPane raiz. */
    private static void cerrarOverlay(StackPane overlay, StackPane raiz, Runnable callback) {
        FadeTransition salida = new FadeTransition(Duration.millis(150), overlay);
        salida.setFromValue(1);
        salida.setToValue(0);
        salida.setOnFinished(ev -> {
            raiz.getChildren().remove(overlay);
            if (callback != null) callback.run();
        });
        salida.play();
    }

    /**
     * Construye la tarjeta visual del dialogo.
     *
     * IMPORTANTE: setMaxWidth(460) + setMinWidth(420) en la tarjeta evita
     * que el StackPane la estire al ancho completo de la pantalla.
     */
    private static VBox construirTarjeta(TipoAlerta tipo, String titulo, String contenido) {
        Region barra = new Region();
        barra.setPrefHeight(6);
        barra.setMaxWidth(Double.MAX_VALUE);
        barra.setStyle("-fx-background-color: " + tipo.color + ";"
                     + "-fx-background-radius: 14 14 0 0;");

        Label lblIcono = new Label(tipo.icono);
        lblIcono.setStyle("-fx-font-size: 22px; -fx-text-fill: white; -fx-font-weight: bold;");
        StackPane circulo = new StackPane(lblIcono);
        circulo.setPrefSize(52, 52);
        circulo.setMinSize(52, 52);
        circulo.setMaxSize(52, 52);
        circulo.setStyle("-fx-background-color: " + tipo.color + "; -fx-background-radius: 50;");

        String tituloFinal = (titulo == null || titulo.isBlank()) ? tipo.etiquetaDefecto : titulo;
        Label lblTitulo = new Label(tituloFinal);
        lblTitulo.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;"
                         + "-fx-text-fill: " + COLOR_TEXTO + "; -fx-wrap-text: true;");
        lblTitulo.setMaxWidth(310);

        Label lblContenido = new Label(contenido);
        lblContenido.setStyle("-fx-font-size: 13.5px;"
                            + "-fx-text-fill: " + COLOR_SUBTEXTO + ";"
                            + "-fx-wrap-text: true; -fx-line-spacing: 2;");
        lblContenido.setWrapText(true);
        lblContenido.setMaxWidth(380);

        HBox fila = new HBox(16, circulo, lblTitulo);
        fila.setAlignment(Pos.CENTER_LEFT);

        VBox cuerpo = new VBox(16, fila, lblContenido);
        cuerpo.setPadding(new Insets(28, 28, 20, 28));
        cuerpo.setAlignment(Pos.TOP_LEFT);

        VBox tarjeta = new VBox(barra, cuerpo);
        // Tamaño fijo de la tarjeta: impide que el StackPane la estire
        tarjeta.setMinWidth(420);
        tarjeta.setMaxWidth(460);
        tarjeta.setPrefWidth(460);
        tarjeta.setMinHeight(Region.USE_PREF_SIZE);
        tarjeta.setMaxHeight(Region.USE_PREF_SIZE);
        tarjeta.setStyle(
            "-fx-background-color: " + COLOR_FONDO + ";"
            + "-fx-background-radius: 14;"
            + "-fx-border-color: " + COLOR_BORDE + ";"
            + "-fx-border-radius: 14;"
            + "-fx-border-width: 1;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.28), 32, 0.15, 0, 10);"
        );
        return tarjeta;
    }

    /** Crea un boton estilizado del sistema. */
    private static Button crearBoton(String texto, String colorFondo, boolean esPrimario) {
        Button boton = new Button(texto);
        boton.setMinWidth(100);
        boton.setMinHeight(40);
        if (esPrimario) {
            boton.setStyle(
                "-fx-background-color: " + colorFondo + ";"
                + "-fx-text-fill: white; -fx-font-size: 13.5px; -fx-font-weight: 600;"
                + "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 22 10 22;"
            );
        } else {
            boton.setStyle(
                "-fx-background-color: transparent;"
                + "-fx-text-fill: " + COLOR_SUBTEXTO + "; -fx-font-size: 13.5px; -fx-font-weight: 600;"
                + "-fx-background-radius: 8; -fx-border-radius: 8;"
                + "-fx-border-color: " + COLOR_BORDE + "; -fx-border-width: 1.5;"
                + "-fx-cursor: hand; -fx-padding: 10 22 10 22;"
            );
        }
        return boton;
    }

    /** Obtiene el contenedor raiz registrado o busca uno como fallback. */
    private static StackPane obtenerContenedor() {
        if (contenedorRaiz != null) return contenedorRaiz;
        return javafx.stage.Stage.getWindows().stream()
            .filter(w -> w.isShowing() && w instanceof javafx.stage.Stage)
            .map(w -> ((javafx.stage.Stage) w).getScene())
            .filter(s -> s != null && s.getRoot() instanceof StackPane)
            .map(s -> (StackPane) s.getRoot())
            .findFirst()
            .orElse(null);
    }
}
