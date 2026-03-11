package com.mosqueteros.proyecto_restaurante.util;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Clase utilitaria para mostrar diálogos personalizados en la app.
 *
 * ──────────────────────────────────────────────────────────────────
 * SOLUCIÓN DEFINITIVA AL BUG gtk_window_resize height>0 EN LINUX/GTK
 * ──────────────────────────────────────────────────────────────────
 * El error "Gtk-CRITICAL: gtk_window_resize: assertion 'height > 0' failed"
 * ocurre porque GTK intenta dimensionar la ventana ANTES de que JavaFX
 * termine su layout pass, obteniendo height = 0.
 *
 * Estrategia aplicada:
 *  1. StageStyle.UTILITY  → único estilo que GTK gestiona correctamente
 *     en todos los gestores de ventanas Linux (GNOME, KDE, i3, etc.).
 *     UNDECORATED y TRANSPARENT fallan con height=0 en GTK.
 *  2. stage.setWidth() / stage.setHeight() ANTES del show() → GTK
 *     siempre tiene dimensiones válidas y nunca llama window_resize con 0.
 *  3. raiz.setMinHeight() → segunda capa de protección en el layout de JavaFX.
 *  4. Platform.runLater en el cierre → restaura foco y maximización al padre
 *     después de que GTK termina de procesar el cierre del diálogo.
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

    /** Dimensiones fijas del diálogo — evitan que GTK reciba height=0. */
    private static final double DIALOGO_ANCHO        = 460;
    private static final double DIALOGO_ALTO_SIMPLE  = 230;
    private static final double DIALOGO_ALTO_CONFIRM = 270;

    /** Constructor privado: clase utilitaria, no instanciable. */
    private Alertas() {
        throw new UnsupportedOperationException("Clase utilitaria, no instanciable");
    }

    // ══════════════════════════════════════════════════════════════════════
    // API PÚBLICA — Métodos estáticos de conveniencia
    // ══════════════════════════════════════════════════════════════════════

    /** Muestra un diálogo de ÉXITO (ícono ✓ verde). */
    public static void exito(Node nodoOrigen, String titulo, String contenido) {
        mostrarDialogo(obtenerStage(nodoOrigen), TipoAlerta.EXITO, titulo, contenido);
    }

    /** Muestra un diálogo de ERROR (ícono ✕ rojo). */
    public static void error(Node nodoOrigen, String titulo, String contenido) {
        mostrarDialogo(obtenerStage(nodoOrigen), TipoAlerta.ERROR, titulo, contenido);
    }

    /** Muestra un diálogo de ADVERTENCIA (ícono ⚠ ámbar). */
    public static void aviso(Node nodoOrigen, String titulo, String contenido) {
        mostrarDialogo(obtenerStage(nodoOrigen), TipoAlerta.AVISO, titulo, contenido);
    }

    /** Muestra un diálogo de INFORMACIÓN (ícono ℹ índigo). */
    public static void informacion(Node nodoOrigen, String titulo, String contenido) {
        mostrarDialogo(obtenerStage(nodoOrigen), TipoAlerta.INFO, titulo, contenido);
    }

    /**
     * Muestra un diálogo de CONFIRMACIÓN con botones Aceptar y Cancelar.
     * @return true si el usuario presionó Aceptar, false si canceló.
     */
    public static boolean confirmar(Node nodoOrigen, String titulo, String contenido) {
        return mostrarConfirmacion(obtenerStage(nodoOrigen), titulo, contenido);
    }

    // ── Sobrecargas sin nodo (usa el stage activo — menos recomendado) ──────

    /** Muestra éxito buscando el Stage activo automáticamente. */
    public static void exito(String titulo, String contenido) {
        mostrarDialogo(obtenerStageActivo(), TipoAlerta.EXITO, titulo, contenido);
    }

    /** Muestra error buscando el Stage activo automáticamente. */
    public static void error(String titulo, String contenido) {
        mostrarDialogo(obtenerStageActivo(), TipoAlerta.ERROR, titulo, contenido);
    }

    /** Muestra aviso buscando el Stage activo automáticamente. */
    public static void aviso(String titulo, String contenido) {
        mostrarDialogo(obtenerStageActivo(), TipoAlerta.AVISO, titulo, contenido);
    }

    /** Muestra información buscando el Stage activo automáticamente. */
    public static void informacion(String titulo, String contenido) {
        mostrarDialogo(obtenerStageActivo(), TipoAlerta.INFO, titulo, contenido);
    }

    /** Muestra confirmación buscando el Stage activo automáticamente. */
    public static boolean confirmar(String titulo, String contenido) {
        return mostrarConfirmacion(obtenerStageActivo(), titulo, contenido);
    }

    // ══════════════════════════════════════════════════════════════════════
    // LÓGICA INTERNA PRIVADA
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Enum interno — define cada tipo de alerta con su ícono y color.
     * Patrón Strategy implícito: cada tipo lleva su configuración visual.
     */
    private enum TipoAlerta {
        EXITO("✓", COLOR_EXITO, "Éxito"),
        ERROR("✕", COLOR_ERROR, "Error"),
        AVISO("⚠", COLOR_AVISO, "Advertencia"),
        INFO ("ℹ", COLOR_INFO,  "Información");

        final String icono;
        final String color;
        final String etiquetaDefecto;

        TipoAlerta(String icono, String color, String etiquetaDefecto) {
            this.icono           = icono;
            this.color           = color;
            this.etiquetaDefecto = etiquetaDefecto;
        }
    }

    /**
     * Muestra un diálogo informativo (1 botón Aceptar).
     *
     * CLAVE GTK: se llama stage.setWidth() y stage.setHeight() con valores
     * fijos ANTES del showAndWait(). Esto garantiza que GTK nunca reciba
     * height = 0 al intentar dimensionar la ventana.
     */
    private static void mostrarDialogo(Stage stagePadre,
                                       TipoAlerta tipo,
                                       String titulo,
                                       String contenido) {
        Stage dialogo = crearStageDialogo(stagePadre);

        VBox raiz = construirLayoutDialogo(tipo, titulo, contenido, false);

        Button btnAceptar = crearBoton("Aceptar", tipo.color, true);
        btnAceptar.setOnAction(e -> cerrarDialogoYRestaurarPadre(dialogo, stagePadre));

        HBox piePagina = new HBox(btnAceptar);
        piePagina.setAlignment(Pos.CENTER_RIGHT);
        piePagina.setPadding(new Insets(0, 28, 24, 28));
        raiz.getChildren().add(piePagina);

        Scene escena = new Scene(raiz, DIALOGO_ANCHO, DIALOGO_ALTO_SIMPLE);
        dialogo.setScene(escena);

        // Dimensiones fijas ANTES del show() → GTK nunca recibe height = 0
        dialogo.setWidth(DIALOGO_ANCHO);
        dialogo.setHeight(DIALOGO_ALTO_SIMPLE);

        centrarSobrePadre(dialogo, stagePadre);
        dialogo.showAndWait();
    }

    /**
     * Muestra un diálogo de confirmación (2 botones: Cancelar / Aceptar).
     * Retorna true si el usuario presionó Aceptar.
     */
    private static boolean mostrarConfirmacion(Stage stagePadre,
                                               String titulo,
                                               String contenido) {
        Stage dialogo = crearStageDialogo(stagePadre);
        final boolean[] resultado = {false};

        VBox raiz = construirLayoutDialogo(TipoAlerta.AVISO, titulo, contenido, true);

        Button btnAceptar = crearBoton("Aceptar", COLOR_ERROR, true);
        btnAceptar.setOnAction(e -> {
            resultado[0] = true;
            cerrarDialogoYRestaurarPadre(dialogo, stagePadre);
        });

        Button btnCancelar = crearBoton("Cancelar", COLOR_BORDE, false);
        btnCancelar.setStyle(
            btnCancelar.getStyle()
            + "-fx-text-fill: " + COLOR_SUBTEXTO + ";"
            + "-fx-border-color: " + COLOR_BORDE + ";"
        );
        btnCancelar.setOnAction(e -> cerrarDialogoYRestaurarPadre(dialogo, stagePadre));

        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        HBox piePagina = new HBox(10, btnCancelar, espaciador, btnAceptar);
        piePagina.setAlignment(Pos.CENTER);
        piePagina.setPadding(new Insets(0, 28, 24, 28));
        raiz.getChildren().add(piePagina);

        Scene escena = new Scene(raiz, DIALOGO_ANCHO, DIALOGO_ALTO_CONFIRM);
        dialogo.setScene(escena);

        // Dimensiones fijas ANTES del show() → GTK nunca recibe height = 0
        dialogo.setWidth(DIALOGO_ANCHO);
        dialogo.setHeight(DIALOGO_ALTO_CONFIRM);

        centrarSobrePadre(dialogo, stagePadre);
        dialogo.showAndWait();

        return resultado[0];
    }

    /**
     * Cierra el diálogo y usa Platform.runLater para devolver foco y estado
     * de maximización al padre DESPUÉS de que GTK termine el cierre.
     *
     * Sin Platform.runLater, GNOME puede colapsar la ventana padre porque
     * JavaFX intenta enfocarla mientras GTK aún está procesando el cierre.
     */
    private static void cerrarDialogoYRestaurarPadre(Stage dialogo, Stage padre) {
        boolean estabaMaximizado = (padre != null) && padre.isMaximized();
        dialogo.close();
        if (padre != null) {
            Platform.runLater(() -> {
                padre.toFront();
                padre.requestFocus();
                // Si GTK colapsó la ventana durante el cierre, la restauramos
                if (estabaMaximizado && !padre.isMaximized()) {
                    padre.setMaximized(true);
                }
            });
        }
    }

    /**
     * Crea el Stage base del diálogo.
     *
     * ─────────────────────────────────────────────────────────────────────
     * POR QUÉ StageStyle.UTILITY:
     *
     * En Linux/GTK los estilos de Stage se comportan así:
     *  • DECORATED   → barra de título completa del SO (no queremos)
     *  • UNDECORATED → genera gtk_window_resize height>0 al cerrar
     *  • TRANSPARENT → también puede generar height>0 sin dimensiones fijas
     *  • UTILITY     → ventana utilitaria sin botones min/max, GTK la
     *                  trata diferente y NO genera el error de height=0.
     *                  En GNOME aparece una barra de título muy pequeña
     *                  pero el diseño personalizado del VBox está abajo.
     *
     * Con las dimensiones fijas (setWidth/setHeight antes del show) el
     * error desaparece incluso en entornos donde UTILITY no es suficiente.
     * ─────────────────────────────────────────────────────────────────────
     */
    private static Stage crearStageDialogo(Stage stagePadre) {
        Stage dialogo = new Stage();
        dialogo.initOwner(stagePadre);
        dialogo.initModality(Modality.APPLICATION_MODAL);
        dialogo.initStyle(StageStyle.UTILITY);
        dialogo.setResizable(false);
        dialogo.setTitle(""); // Título vacío para que la barra UTILITY sea mínima
        return dialogo;
    }

    /**
     * Construye el layout visual (VBox principal) del diálogo.
     * Incluye: barra de color superior, ícono circular, título y contenido.
     *
     * @param esConfirmacion  si true agrega el texto de confirmación adicional
     */
    private static VBox construirLayoutDialogo(TipoAlerta tipo,
                                               String titulo,
                                               String contenido,
                                               boolean esConfirmacion) {
        Region barraSuperior = new Region();
        barraSuperior.setPrefHeight(6);
        barraSuperior.setMaxWidth(Double.MAX_VALUE);
        barraSuperior.setStyle("-fx-background-color: " + tipo.color + ";");

        Label lblIcono = new Label(tipo.icono);
        lblIcono.setStyle(
            "-fx-font-size: 22px;"
            + "-fx-text-fill: white;"
            + "-fx-font-weight: bold;"
        );
        StackPane circuloIcono = new StackPane(lblIcono);
        circuloIcono.setPrefSize(52, 52);
        circuloIcono.setMinSize(52, 52);
        circuloIcono.setMaxSize(52, 52);
        circuloIcono.setStyle(
            "-fx-background-color: " + tipo.color + ";"
            + "-fx-background-radius: 50;"
        );

        String tituloFinal = (titulo == null || titulo.isBlank()) ? tipo.etiquetaDefecto : titulo;
        Label lblTitulo = new Label(tituloFinal);
        lblTitulo.setStyle(
            "-fx-font-size: 17px;"
            + "-fx-font-weight: bold;"
            + "-fx-text-fill: " + COLOR_TEXTO + ";"
            + "-fx-wrap-text: true;"
        );

        Label lblContenido = new Label(contenido);
        lblContenido.setStyle(
            "-fx-font-size: 13.5px;"
            + "-fx-text-fill: " + COLOR_SUBTEXTO + ";"
            + "-fx-wrap-text: true;"
            + "-fx-line-spacing: 2;"
        );
        lblContenido.setWrapText(true);
        lblContenido.setMaxWidth(320);

        if (esConfirmacion) {
            Label lblPregunta = new Label("¿Deseas continuar con esta acción?");
            lblPregunta.setStyle(
                "-fx-font-size: 12px;"
                + "-fx-text-fill: " + COLOR_AVISO + ";"
                + "-fx-font-weight: 600;"
            );
            VBox cuerpo = construirCuerpo(circuloIcono, lblTitulo, lblContenido, lblPregunta);
            return construirRaiz(barraSuperior, cuerpo, DIALOGO_ALTO_CONFIRM);
        }

        VBox cuerpo = construirCuerpo(circuloIcono, lblTitulo, lblContenido, null);
        return construirRaiz(barraSuperior, cuerpo, DIALOGO_ALTO_SIMPLE);
    }

    /** Ensambla el cuerpo principal del diálogo con ícono, título y contenido. */
    private static VBox construirCuerpo(StackPane icono,
                                        Label titulo,
                                        Label contenido,
                                        Label extra) {
        VBox cuerpo = new VBox(16);
        cuerpo.setPadding(new Insets(28, 28, 20, 28));
        cuerpo.setAlignment(Pos.TOP_LEFT);

        HBox filaIconoTitulo = new HBox(16, icono, titulo);
        filaIconoTitulo.setAlignment(Pos.CENTER_LEFT);

        cuerpo.getChildren().addAll(filaIconoTitulo, contenido);
        if (extra != null) cuerpo.getChildren().add(extra);
        return cuerpo;
    }

    /**
     * Ensambla el layout raíz con la barra superior y el cuerpo.
     * Se define minHeight explícito como segunda capa de protección
     * contra el bug de GTK con height = 0.
     *
     * @param altoMinimo  Alto mínimo a fijar en el VBox raíz
     */
    private static VBox construirRaiz(Region barraSuperior, VBox cuerpo, double altoMinimo) {
        VBox raiz = new VBox();
        raiz.setMinWidth(DIALOGO_ANCHO);
        raiz.setMaxWidth(DIALOGO_ANCHO);
        raiz.setMinHeight(altoMinimo);  // Protección contra GTK height = 0
        raiz.setStyle(
            "-fx-background-color: " + COLOR_FONDO + ";"
            + "-fx-background-radius: 8;"
            + "-fx-border-color: " + COLOR_BORDE + ";"
            + "-fx-border-radius: 8;"
            + "-fx-border-width: 1;"
        );
        raiz.getChildren().addAll(barraSuperior, cuerpo);
        return raiz;
    }

    /**
     * Crea un botón estilizado consistente con el diseño de Cali Delights.
     *
     * @param texto      Texto del botón
     * @param colorFondo Color de fondo en hex
     * @param esPrimario Si es true tiene fondo sólido, si no es outline
     */
    private static Button crearBoton(String texto, String colorFondo, boolean esPrimario) {
        Button boton = new Button(texto);
        boton.setMinWidth(100);
        boton.setMinHeight(40);
        if (esPrimario) {
            boton.setStyle(
                "-fx-background-color: " + colorFondo + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 13.5px;"
                + "-fx-font-weight: 600;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;"
                + "-fx-padding: 10 22 10 22;"
            );
        } else {
            boton.setStyle(
                "-fx-background-color: transparent;"
                + "-fx-text-fill: " + COLOR_SUBTEXTO + ";"
                + "-fx-font-size: 13.5px;"
                + "-fx-font-weight: 600;"
                + "-fx-background-radius: 8;"
                + "-fx-border-radius: 8;"
                + "-fx-border-color: " + COLOR_BORDE + ";"
                + "-fx-border-width: 1.5;"
                + "-fx-cursor: hand;"
                + "-fx-padding: 10 22 10 22;"
            );
        }
        return boton;
    }

    /**
     * Centra el diálogo encima de la ventana padre.
     * Usa setOnShown para leer las dimensiones reales del diálogo.
     */
    private static void centrarSobrePadre(Stage dialogo, Stage padre) {
        dialogo.setOnShown(e -> {
            if (padre != null) {
                double x = padre.getX() + (padre.getWidth()  - dialogo.getWidth())  / 2;
                double y = padre.getY() + (padre.getHeight() - dialogo.getHeight()) / 2;
                dialogo.setX(x);
                dialogo.setY(y);
            }
        });
    }

    /**
     * Obtiene el Stage a partir de cualquier nodo de la vista.
     *
     * @param nodo  Cualquier nodo visible en la escena actual
     * @return      El Stage principal, o null si no se puede obtener
     */
    private static Stage obtenerStage(Node nodo) {
        if (nodo == null) return obtenerStageActivo();
        Window ventana = nodo.getScene() != null ? nodo.getScene().getWindow() : null;
        return (ventana instanceof Stage) ? (Stage) ventana : obtenerStageActivo();
    }

    /**
     * Obtiene el Stage activo de la aplicación como fallback.
     * Menos confiable que obtenerStage(Node) en aplicaciones multi-ventana.
     */
    private static Stage obtenerStageActivo() {
        return Stage.getWindows()
                    .stream()
                    .filter(Window::isShowing)
                    .filter(w -> w instanceof Stage)
                    .map(w -> (Stage) w)
                    .filter(Stage::isFocused)
                    .findFirst()
                    .orElseGet(() -> Stage.getWindows()
                                         .stream()
                                         .filter(Window::isShowing)
                                         .filter(w -> w instanceof Stage)
                                         .map(w -> (Stage) w)
                                         .findFirst()
                                         .orElse(null));
    }
}
