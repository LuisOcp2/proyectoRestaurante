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
 * NUNCA abre un Stage secundario (elimina el bug GTK height>0 en Linux).
 *
 * PATRON DE CONFIRMACION:
 * Se usa callback (Runnable) en lugar de retornar boolean, porque
 * confirmar() se llama desde el hilo de JavaFX y Object.wait() bloquearía
 * ese hilo causando que la UI se congele y nunca responda al usuario.
 *
 * Uso correcto:
 *   Alertas.confirmar(nodo, "Titulo", "Mensaje", () -> {
 *       // accion que se ejecuta SOLO si el usuario presiona Aceptar
 *   });
 */
public final class Alertas {

    // ── Paleta de colores ─────────────────────────────────────────────────
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

    // ── REGISTRO ─────────────────────────────────────────────────────────

    /**
     * Registra el StackPane raiz donde se inyectaran los overlays.
     * Llamar UNA VEZ en MainController.inicializarDashboard().
     */
    public static void registrarContenedor(StackPane contenedor) {
        contenedorRaiz = contenedor;
    }

    // ── API PUBLICA — INFORMATIVOS ────────────────────────────────────────

    /** Muestra un dialogo de EXITO. */
    public static void exito(Node n, String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.EXITO, titulo, contenido, null);
    }
    /** Muestra un dialogo de ERROR. */
    public static void error(Node n, String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.ERROR, titulo, contenido, null);
    }
    /** Muestra un dialogo de ADVERTENCIA. */
    public static void aviso(Node n, String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.AVISO, titulo, contenido, null);
    }
    /** Muestra un dialogo de INFORMACION. */
    public static void informacion(Node n, String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.INFO, titulo, contenido, null);
    }
    /** Muestra un dialogo de EXITO sin nodo origen. */
    public static void exito(String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.EXITO, titulo, contenido, null);
    }
    /** Muestra un dialogo de ERROR sin nodo origen. */
    public static void error(String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.ERROR, titulo, contenido, null);
    }
    /** Muestra un dialogo de ADVERTENCIA sin nodo origen. */
    public static void aviso(String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.AVISO, titulo, contenido, null);
    }
    /** Muestra un dialogo de INFORMACION sin nodo origen. */
    public static void informacion(String titulo, String contenido) {
        mostrarOverlay(TipoAlerta.INFO, titulo, contenido, null);
    }

    // ── API PUBLICA — CONFIRMACION CON CALLBACK ───────────────────────────

    /**
     * Muestra un dialogo de CONFIRMACION.
     *
     * El parametro onAceptar es un Runnable que se ejecuta SOLO si el
     * usuario presiona "Aceptar". Si presiona "Cancelar", no ocurre nada.
     *
     * IMPORTANTE: NO retorna boolean. Usa el patron callback porque este
     * metodo se llama desde el hilo de JavaFX (FX Application Thread) y
     * bloquear ese hilo con Object.wait() congelaría toda la UI.
     *
     * Ejemplo de uso:
     *   Alertas.confirmar(nodo, "Eliminar", "¿Seguro?", () -> {
     *       dao.eliminar(id);
     *       cargarListado();
     *   });
     *
     * @param nodo       Nodo de la vista (puede ser null)
     * @param titulo     Titulo del dialogo
     * @param contenido  Mensaje del dialogo
     * @param onAceptar  Accion a ejecutar si el usuario confirma
     */
    public static void confirmar(Node nodo, String titulo, String contenido, Runnable onAceptar) {
        mostrarOverlayConfirmacion(titulo, contenido, onAceptar);
    }

    /** Sobrecarga sin nodo origen. */
    public static void confirmar(String titulo, String contenido, Runnable onAceptar) {
        mostrarOverlayConfirmacion(titulo, contenido, onAceptar);
    }

    // ── LOGICA INTERNA ────────────────────────────────────────────────────

    /** Enum con icono y color por tipo de alerta. */
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
     * Muestra un dialogo informativo (1 boton Aceptar) como overlay.
     * Usa Region+CSS para el fondo oscuro, evitando el loop de layout
     * que causaba Rectangle+bind.
     */
    private static void mostrarOverlay(TipoAlerta tipo,
                                       String titulo,
                                       String contenido,
                                       Runnable callbackAceptar) {
        Platform.runLater(() -> {
            StackPane raiz = obtenerContenedor();
            if (raiz == null) return;

            Region fondo = crearFondoOscuro();
            VBox tarjeta = construirTarjeta(tipo, titulo, contenido);

            Button btnAceptar = crearBoton("Aceptar", tipo.color, true);
            HBox pie = new HBox(btnAceptar);
            pie.setAlignment(Pos.CENTER_RIGHT);
            pie.setPadding(new Insets(0, 28, 24, 28));
            tarjeta.getChildren().add(pie);

            StackPane overlay = new StackPane(fondo, tarjeta);
            overlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            StackPane.setAlignment(tarjeta, Pos.CENTER);

            overlay.setOpacity(0);
            raiz.getChildren().add(overlay);
            overlay.toFront();

            animar(overlay, 0, 1, 180, null);

            btnAceptar.setOnAction(e ->
                cerrarOverlay(overlay, raiz, callbackAceptar)
            );
        });
    }

    /**
     * Muestra un dialogo de confirmacion como overlay.
     *
     * onAceptar se ejecuta en el hilo de JavaFX cuando el usuario
     * presiona Aceptar, despues de que el overlay desaparece con fade-out.
     * Cancelar cierra el overlay sin ejecutar nada.
     */
    private static void mostrarOverlayConfirmacion(String titulo,
                                                   String contenido,
                                                   Runnable onAceptar) {
        Platform.runLater(() -> {
            StackPane raiz = obtenerContenedor();
            if (raiz == null) {
                // Sin contenedor registrado: ejecutar directamente sin confirmacion
                if (onAceptar != null) onAceptar.run();
                return;
            }

            Region fondo = crearFondoOscuro();
            VBox tarjeta = construirTarjeta(TipoAlerta.AVISO, titulo, contenido);

            // Texto adicional
            Label lblExtra = new Label("¿Deseas continuar con esta acción?");
            lblExtra.setStyle(
                "-fx-font-size: 12px;"
                + "-fx-text-fill: " + COLOR_AVISO + ";"
                + "-fx-font-weight: 600;"
            );
            tarjeta.getChildren().add(lblExtra);

            // Botones
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

            animar(overlay, 0, 1, 180, null);

            // Aceptar: cierra y ejecuta el callback
            btnAceptar.setOnAction(e ->
                cerrarOverlay(overlay, raiz, onAceptar)
            );
            // Cancelar: solo cierra, sin ejecutar nada
            btnCancelar.setOnAction(e ->
                cerrarOverlay(overlay, raiz, null)
            );
        });
    }

    /**
     * Fondo oscuro como Region + CSS.
     * NO usa Rectangle+bind para evitar el loop de layout infinito.
     */
    private static Region crearFondoOscuro() {
        Region fondo = new Region();
        fondo.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        fondo.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");
        return fondo;
    }

    /** Cierra el overlay con fade-out y ejecuta el callback tras el cierre. */
    private static void cerrarOverlay(StackPane overlay, StackPane raiz, Runnable callback) {
        animar(overlay, 1, 0, 150, () -> {
            raiz.getChildren().remove(overlay);
            if (callback != null) callback.run();
        });
    }

    /** Animacion de fade genérica. */
    private static void animar(StackPane nodo, double desde, double hasta,
                                int ms, Runnable alTerminar) {
        FadeTransition ft = new FadeTransition(Duration.millis(ms), nodo);
        ft.setFromValue(desde);
        ft.setToValue(hasta);
        if (alTerminar != null) ft.setOnFinished(e -> alTerminar.run());
        ft.play();
    }

    /**
     * Construye la tarjeta visual del dialogo.
     * Dimensiones fijas (min/max 420-460px) para que el StackPane
     * no la estire al ancho completo de la pantalla.
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

    /** Crea un boton estilizado del sistema Cali Delights. */
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

    /** Obtiene el StackPane raiz registrado o busca uno como fallback. */
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
