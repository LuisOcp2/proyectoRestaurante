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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Clase utilitaria para mostrar diálogos personalizados en la app.
 *
 * ──────────────────────────────────────────────────────────────────
 * SOLUCIÓN DEFINITIVA: OVERLAY SIN STAGE SECUNDARIO
 * ──────────────────────────────────────────────────────────────────
 * El error "gtk_window_resize: assertion 'height > 0' failed" ocurre
 * porque CUALQUIER Stage secundario en Linux/GTK (UNDECORATED, TRANSPARENT,
 * UTILITY, incluso DECORATED) puede provocar este bug al cerrarse, ya que
 * GTK intenta redimensionar la ventana padre al recuperar el foco.
 *
 * La UNICA solucion real es NO abrir ningun Stage secundario.
 *
 * Esta implementacion inyecta el dialogo directamente como un nodo
 * hijo del StackPane raiz de la ventana principal (overlay), cubriendo
 * el contenido con un fondo oscuro semitransparente. El resultado visual
 * es identico a un dialogo modal, pero GTK nunca ve una segunda ventana.
 *
 * Uso:
 *   - Registrar el contenedor raiz una sola vez en MainController:
 *       Alertas.registrarContenedor(contenidoPrincipal);
 *   - Llamar normalmente:
 *       Alertas.exito(nodo, "Titulo", "Mensaje");
 *       boolean ok = Alertas.confirmar(nodo, "Titulo", "Mensaje");
 */
public final class Alertas {

    // ── Paleta de colores del sistema "Cali Delights" ───────────────────
    private static final String COLOR_EXITO    = "#10B981";
    private static final String COLOR_ERROR    = "#EF4444";
    private static final String COLOR_AVISO    = "#F59E0B";
    private static final String COLOR_INFO     = "#6366F1";
    private static final String COLOR_FONDO    = "#FFFFFF";
    private static final String COLOR_TEXTO    = "#1E293B";
    private static final String COLOR_SUBTEXTO = "#64748B";
    private static final String COLOR_BORDE    = "#E2E8F0";

    /**
     * Referencia al StackPane raiz de la ventana principal.
     * Se registra una vez desde MainController.inicializarDashboard().
     * El overlay del dialogo se inserta como hijo de este StackPane.
     */
    private static StackPane contenedorRaiz = null;

    /** Constructor privado: clase utilitaria, no instanciable. */
    private Alertas() {
        throw new UnsupportedOperationException("Clase utilitaria, no instanciable");
    }

    // ══════════════════════════════════════════════════════════════════════
    // REGISTRO DEL CONTENEDOR
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Registra el StackPane raiz donde se inyectaran los overlays.
     * Debe llamarse UNA SOLA VEZ desde MainController.inicializarDashboard().
     *
     * @param contenedor  El StackPane @FXML "contenidoPrincipal" del main.fxml
     */
    public static void registrarContenedor(StackPane contenedor) {
        contenedorRaiz = contenedor;
    }

    // ══════════════════════════════════════════════════════════════════════
    // API PUBLICA
    // ══════════════════════════════════════════════════════════════════════

    /** Muestra un dialogo de EXITO (icono verde). */
    public static void exito(Node nodoOrigen, String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.EXITO, titulo, contenido, false, null);
    }

    /** Muestra un dialogo de ERROR (icono rojo). */
    public static void error(Node nodoOrigen, String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.ERROR, titulo, contenido, false, null);
    }

    /** Muestra un dialogo de ADVERTENCIA (icono ambar). */
    public static void aviso(Node nodoOrigen, String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.AVISO, titulo, contenido, false, null);
    }

    /** Muestra un dialogo de INFORMACION (icono indigo). */
    public static void informacion(Node nodoOrigen, String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.INFO, titulo, contenido, false, null);
    }

    /** Muestra un dialogo de EXITO sin nodo origen. */
    public static void exito(String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.EXITO, titulo, contenido, false, null);
    }

    /** Muestra un dialogo de ERROR sin nodo origen. */
    public static void error(String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.ERROR, titulo, contenido, false, null);
    }

    /** Muestra un dialogo de ADVERTENCIA sin nodo origen. */
    public static void aviso(String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.AVISO, titulo, contenido, false, null);
    }

    /** Muestra un dialogo de INFORMACION sin nodo origen. */
    public static void informacion(String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.INFO, titulo, contenido, false, null);
    }

    /**
     * Muestra un dialogo de CONFIRMACION (Aceptar / Cancelar).
     * BLOQUEA el hilo de JavaFX con espera activa hasta que el usuario responda.
     *
     * @return true si el usuario presiono Aceptar, false si cancelo.
     */
    public static boolean confirmar(Node nodoOrigen, String titulo, String contenido) {
        return mostrarOverlayConfirmacion(titulo, contenido);
    }

    /** Muestra confirmacion sin nodo origen. */
    public static boolean confirmar(String titulo, String contenido) {
        return mostrarOverlayConfirmacion(titulo, contenido);
    }

    // ══════════════════════════════════════════════════════════════════════
    // LOGICA INTERNA
    // ══════════════════════════════════════════════════════════════════════

    /** Enum interno con icono y color por tipo de alerta. */
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
     * No bloquea el hilo — el usuario solo cierra con Aceptar.
     */
    private static void mostrarOverlay(TipoAlerta tipo,
                                       String titulo,
                                       String contenido,
                                       boolean esConfirmacion,
                                       Runnable callbackAceptar) {
        Platform.runLater(() -> {
            StackPane raiz = obtenerContenedor();
            if (raiz == null) return;

            // Fondo oscuro semitransparente que cubre todo el contenedor
            Rectangle fondo = new Rectangle();
            fondo.widthProperty().bind(raiz.widthProperty());
            fondo.heightProperty().bind(raiz.heightProperty());
            fondo.setFill(Color.rgb(0, 0, 0, 0.45));

            VBox tarjeta = construirTarjeta(tipo, titulo, contenido);

            // Boton Aceptar
            Button btnAceptar = crearBoton("Aceptar", tipo.color, true);

            HBox pie = new HBox(btnAceptar);
            pie.setAlignment(Pos.CENTER_RIGHT);
            pie.setPadding(new Insets(0, 28, 24, 28));
            tarjeta.getChildren().add(pie);

            StackPane overlay = new StackPane(fondo, tarjeta);
            StackPane.setAlignment(tarjeta, Pos.CENTER);

            // Animacion de entrada
            overlay.setOpacity(0);
            raiz.getChildren().add(overlay);
            overlay.toFront();

            FadeTransition entrada = new FadeTransition(Duration.millis(180), overlay);
            entrada.setFromValue(0);
            entrada.setToValue(1);
            entrada.play();

            // Al presionar Aceptar: fade-out y remover del StackPane
            btnAceptar.setOnAction(e -> {
                FadeTransition salida = new FadeTransition(Duration.millis(150), overlay);
                salida.setFromValue(1);
                salida.setToValue(0);
                salida.setOnFinished(ev -> {
                    raiz.getChildren().remove(overlay);
                    if (callbackAceptar != null) callbackAceptar.run();
                });
                salida.play();
            });
        });
    }

    /**
     * Muestra un dialogo de confirmacion como overlay y BLOQUEA
     * el hilo de JavaFX con espera activa hasta que el usuario responda.
     *
     * Usa un objeto de bloqueo (Object.wait/notify) para simular
     * el comportamiento de showAndWait() sin abrir ningun Stage.
     *
     * @return true si el usuario presiono Aceptar.
     */
    private static boolean mostrarOverlayConfirmacion(String titulo, String contenido) {
        // Array para capturar resultado desde el lambda
        final boolean[] resultado = {false};
        // Objeto de sincronizacion para bloquear el hilo actual
        final Object bloqueo = new Object();

        Platform.runLater(() -> {
            StackPane raiz = obtenerContenedor();
            if (raiz == null) {
                // Sin contenedor: no se puede mostrar, desbloqueamos de inmediato
                synchronized (bloqueo) { bloqueo.notify(); }
                return;
            }

            Rectangle fondo = new Rectangle();
            fondo.widthProperty().bind(raiz.widthProperty());
            fondo.heightProperty().bind(raiz.heightProperty());
            fondo.setFill(Color.rgb(0, 0, 0, 0.45));

            VBox tarjeta = construirTarjeta(TipoAlerta.AVISO, titulo, contenido);

            // Texto adicional de confirmacion
            Label lblExtra = new Label("¿Deseas continuar con esta acción?");
            lblExtra.setStyle(
                "-fx-font-size: 12px;"
                + "-fx-text-fill: " + COLOR_AVISO + ";"
                + "-fx-font-weight: 600;"
            );
            tarjeta.getChildren().add(lblExtra);

            // Botones
            Button btnAceptar  = crearBoton("Aceptar",   COLOR_ERROR, true);
            Button btnCancelar = crearBoton("Cancelar",  COLOR_BORDE, false);
            btnCancelar.setStyle(
                btnCancelar.getStyle()
                + "-fx-text-fill: " + COLOR_SUBTEXTO + ";"
                + "-fx-border-color: " + COLOR_BORDE + ";"
            );

            Region espacio = new Region();
            HBox.setHgrow(espacio, Priority.ALWAYS);
            HBox pie = new HBox(10, btnCancelar, espacio, btnAceptar);
            pie.setAlignment(Pos.CENTER);
            pie.setPadding(new Insets(0, 28, 24, 28));
            tarjeta.getChildren().add(pie);

            StackPane overlay = new StackPane(fondo, tarjeta);
            StackPane.setAlignment(tarjeta, Pos.CENTER);

            overlay.setOpacity(0);
            raiz.getChildren().add(overlay);
            overlay.toFront();

            FadeTransition entrada = new FadeTransition(Duration.millis(180), overlay);
            entrada.setFromValue(0);
            entrada.setToValue(1);
            entrada.play();

            // Accion comun: cerrar overlay y notificar al hilo bloqueado
            Runnable cerrar = () -> {
                FadeTransition salida = new FadeTransition(Duration.millis(150), overlay);
                salida.setFromValue(1);
                salida.setToValue(0);
                salida.setOnFinished(ev -> {
                    raiz.getChildren().remove(overlay);
                    synchronized (bloqueo) { bloqueo.notify(); }
                });
                salida.play();
            };

            btnAceptar.setOnAction(e -> {
                resultado[0] = true;
                cerrar.run();
            });
            btnCancelar.setOnAction(e -> cerrar.run());
        });

        // Bloquear el hilo llamante hasta que el usuario responda
        // (solo si NO estamos ya en el hilo de JavaFX — proteccion extra)
        if (!Platform.isFxApplicationThread()) {
            synchronized (bloqueo) {
                try { bloqueo.wait(30_000); } // timeout de 30s por seguridad
                catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
        }

        return resultado[0];
    }

    /**
     * Construye la tarjeta visual del dialogo (sin los botones).
     * Los botones se agregan por separado segun el tipo de dialogo.
     */
    private static VBox construirTarjeta(TipoAlerta tipo, String titulo, String contenido) {
        // Barra superior de color
        Region barra = new Region();
        barra.setPrefHeight(6);
        barra.setMaxWidth(Double.MAX_VALUE);
        barra.setStyle("-fx-background-color: " + tipo.color + ";"
                     + "-fx-background-radius: 14 14 0 0;");

        // Icono circular
        Label lblIcono = new Label(tipo.icono);
        lblIcono.setStyle("-fx-font-size: 22px; -fx-text-fill: white; -fx-font-weight: bold;");
        StackPane circulo = new StackPane(lblIcono);
        circulo.setPrefSize(52, 52);
        circulo.setMinSize(52, 52);
        circulo.setMaxSize(52, 52);
        circulo.setStyle("-fx-background-color: " + tipo.color + "; -fx-background-radius: 50;");

        // Titulo
        String tituloFinal = (titulo == null || titulo.isBlank()) ? tipo.etiquetaDefecto : titulo;
        Label lblTitulo = new Label(tituloFinal);
        lblTitulo.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;"
                         + "-fx-text-fill: " + COLOR_TEXTO + "; -fx-wrap-text: true;");

        // Contenido
        Label lblContenido = new Label(contenido);
        lblContenido.setStyle("-fx-font-size: 13.5px;"
                            + "-fx-text-fill: " + COLOR_SUBTEXTO + ";"
                            + "-fx-wrap-text: true; -fx-line-spacing: 2;");
        lblContenido.setWrapText(true);
        lblContenido.setMaxWidth(320);

        HBox fila = new HBox(16, circulo, lblTitulo);
        fila.setAlignment(Pos.CENTER_LEFT);

        VBox cuerpo = new VBox(16, fila, lblContenido);
        cuerpo.setPadding(new Insets(28, 28, 20, 28));
        cuerpo.setAlignment(Pos.TOP_LEFT);

        // Tarjeta completa
        VBox tarjeta = new VBox(barra, cuerpo);
        tarjeta.setMinWidth(420);
        tarjeta.setMaxWidth(460);
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

    /** Crea un boton estilizado del sistema Cali Delights. */
    private static Button crearBoton(String texto, String colorFondo, boolean esPrimario) {
        Button boton = new Button(texto);
        boton.setMinWidth(100);
        boton.setMinHeight(40);
        if (esPrimario) {
            boton.setStyle(
                "-fx-background-color: " + colorFondo + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 13.5px; -fx-font-weight: 600;"
                + "-fx-background-radius: 8; -fx-cursor: hand;"
                + "-fx-padding: 10 22 10 22;"
            );
        } else {
            boton.setStyle(
                "-fx-background-color: transparent;"
                + "-fx-text-fill: " + COLOR_SUBTEXTO + ";"
                + "-fx-font-size: 13.5px; -fx-font-weight: 600;"
                + "-fx-background-radius: 8; -fx-border-radius: 8;"
                + "-fx-border-color: " + COLOR_BORDE + "; -fx-border-width: 1.5;"
                + "-fx-cursor: hand; -fx-padding: 10 22 10 22;"
            );
        }
        return boton;
    }

    /**
     * Obtiene el StackPane raiz registrado.
     * Si no hay ninguno registrado intenta obtener el StackPane raiz
     * de la escena activa como fallback.
     */
    private static StackPane obtenerContenedor() {
        if (contenedorRaiz != null) return contenedorRaiz;
        // Fallback: buscar en las ventanas visibles
        return javafx.stage.Stage.getWindows().stream()
            .filter(w -> w.isShowing() && w instanceof javafx.stage.Stage)
            .map(w -> ((javafx.stage.Stage) w).getScene())
            .filter(s -> s != null && s.getRoot() instanceof StackPane)
            .map(s -> (StackPane) s.getRoot())
            .findFirst()
            .orElse(null);
    }
}
