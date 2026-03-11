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
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Clase utilitaria para mostrar diálogos personalizados en la app.
 *
 * SOLUCIÓN AL BUG DE MINIMIZACIÓN EN LINUX/GNOME:
 * - Se usa StageStyle.TRANSPARENT en lugar de UNDECORATED.
 * - UNDECORATED en GTK genera "gtk_window_resize: assertion 'height > 0' failed"
 *   que hace que GNOME colapse la ventana padre al cerrar el diálogo.
 * - Se guarda/restaura el estado maximizado del padre explícitamente.
 * - Se usa Platform.runLater para devolver el foco al padre tras cerrar.
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

    /** Constructor privado: clase utilitaria, no instanciable. */
    private Alertas() {
        throw new UnsupportedOperationException("Clase utilitaria, no instanciable");
    }

    // ════════════════════════════════════════════════════════════════
    // API PÚblica — Métodos estáticos de conveniencia
    // ════════════════════════════════════════════════════════════════

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

    // ── Sobrecargas sin nodo (usa el stage activo — menos recomendado) ──

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

    // ════════════════════════════════════════════════════════════════
    // LÓGICA INTERNA PRIVADA
    // ════════════════════════════════════════════════════════════════

    /**
     * Enum interno que define cada tipo de alerta con su ícono y color.
     * Patrón Strategy implícito: cada tipo lleva su propia configuración visual.
     */
    private enum TipoAlerta {
        EXITO("✓",  COLOR_EXITO, "Éxito"),
        ERROR("✕",  COLOR_ERROR, "Error"),
        AVISO("⚠",  COLOR_AVISO, "Advertencia"),
        INFO ("ℹ",  COLOR_INFO,  "Información");

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
     * Crea y muestra un diálogo modal personalizado sin afectar el estado
     * de maximización de la ventana padre.
     *
     * CLAVES TÉCNICAS (Linux/GTK):
     * - StageStyle.TRANSPARENT: evita el bug gtk_window_resize height>0
     *   que StageStyle.UNDECORATED provoca en GNOME/GTK al cerrar el diálogo.
     * - Se guarda si el padre estaba maximizado antes de mostrar el diálogo.
     * - Con Platform.runLater se devuelve el foco y maximización al padre
     *   después de que GTK termine de procesar el cierre del diálogo.
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

        // Scene con fondo transparente: necesario para StageStyle.TRANSPARENT
        Scene escena = new Scene(raiz);
        escena.setFill(Color.TRANSPARENT);

        dialogo.setScene(escena);
        centrarSobrePadre(dialogo, stagePadre);
        dialogo.showAndWait();
    }

    /**
     * Versión con dos botones para confirmaciones (Aceptar / Cancelar).
     * Retorna true si el usuario confirmó.
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

        Scene escena = new Scene(raiz);
        escena.setFill(Color.TRANSPARENT);
        dialogo.setScene(escena);
        centrarSobrePadre(dialogo, stagePadre);
        dialogo.showAndWait();

        return resultado[0];
    }

    /**
     * Cierra el diálogo y usa Platform.runLater para devolver el foco
     * y estado de maximización al padre DESPUÉS de que GTK termine de
     * procesar el cierre. Esto evita que GNOME colapse la ventana.
     *
     * Sin este método, en Linux el orden de eventos de GTK puede hacer
     * que la ventana padre quede desfocada o colapsada.
     */
    private static void cerrarDialogoYRestaurarPadre(Stage dialogo, Stage padre) {
        // Guardar estado antes de cerrar
        boolean estabaMaximizado = (padre != null) && padre.isMaximized();

        dialogo.close();

        if (padre != null) {
            // Platform.runLater garantiza que el cierre del diálogo se procese
            // completamente en GTK antes de intentar restaurar el padre
            Platform.runLater(() -> {
                padre.toFront();
                padre.requestFocus();
                // Si estaba maximizado y GTK lo colapsó, lo restauramos
                if (estabaMaximizado && !padre.isMaximized()) {
                    padre.setMaximized(true);
                }
            });
        }
    }

    /**
     * Crea el Stage base del diálogo con la configuración correcta.
     *
     * CAMBIO CLAVE: StageStyle.TRANSPARENT en lugar de UNDECORATED.
     * En Linux/GTK, UNDECORATED provoca gtk_window_resize height>0
     * al cerrar, lo que hace que GNOME colapse la ventana padre.
     * TRANSPARENT no tiene ese bug y mantiene el diseño personalizado.
     */
    private static Stage crearStageDialogo(Stage stagePadre) {
        Stage dialogo = new Stage();

        // initOwner: vincula el diálogo al Stage padre
        dialogo.initOwner(stagePadre);
        // APPLICATION_MODAL: bloquea toda la app sin mover ventanas
        dialogo.initModality(Modality.APPLICATION_MODAL);
        // TRANSPARENT: compatble con Linux/GTK, no genera el bug de height>0
        dialogo.initStyle(StageStyle.TRANSPARENT);
        dialogo.setResizable(false);

        return dialogo;
    }

    /**
     * Construye el layout visual (VBox principal) del diálogo.
     * Incluye: barra de color superior, ícono circular, título y contenido.
     *
     * @param esConfirmacion  si es true agrega texto de confirmación adicional
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
            return construirRaiz(barraSuperior, cuerpo);
        }

        VBox cuerpo = construirCuerpo(circuloIcono, lblTitulo, lblContenido, null);
        return construirRaiz(barraSuperior, cuerpo);
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
     * Ensambla el layout raíz del diálogo con la barra superior.
     * Nota: el fondo blanco sólido se define aquí en CSS para que
     * el área transparente del Stage solo rodee el borde redondeado.
     */
    private static VBox construirRaiz(Region barraSuperior, VBox cuerpo) {
        VBox raiz = new VBox();
        raiz.setMinWidth(400);
        raiz.setMaxWidth(460);
        raiz.setStyle(
            "-fx-background-color: " + COLOR_FONDO + ";"
            + "-fx-background-radius: 14;"
            + "-fx-border-color: " + COLOR_BORDE + ";"
            + "-fx-border-radius: 14;"
            + "-fx-border-width: 1;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.22), 24, 0.12, 0, 8);"
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
     * Si el padre está maximizado, calcula el centro de la pantalla.
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
     * Más confiable que buscar el stage activo, especialmente en
     * aplicaciones multi-ventana.
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
     * Menos confiable que obtenerStage(Node), pero útil cuando no
     * tenemos acceso a un nodo de la vista.
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
