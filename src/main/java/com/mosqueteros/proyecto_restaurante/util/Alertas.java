package com.mosqueteros.proyecto_restaurante.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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

import java.util.Optional;


public final class Alertas {

    // ── Paleta de colores del sistema "Cali Delights" ───────────────
    private static final String COLOR_EXITO    = "#10B981"; // emerald-500
    private static final String COLOR_ERROR    = "#EF4444"; // red-500
    private static final String COLOR_AVISO    = "#F59E0B"; // amber-500
    private static final String COLOR_INFO     = "#6366F1"; // indigo-500
    private static final String COLOR_FONDO    = "#FFFFFF";
    private static final String COLOR_TEXTO    = "#1E293B";
    private static final String COLOR_SUBTEXTO = "#64748B";
    private static final String COLOR_BORDE    = "#E2E8F0";

    // Constructor privado: clase utilitaria, no instanciable (patrón Utility Class)
    private Alertas() {
        throw new UnsupportedOperationException("Clase utilitaria, no instanciable");
    }

    // ══════════════════════════════════════════════════════════════════
    // API PÚBLICA — Métodos estáticos de conveniencia
    // ══════════════════════════════════════════════════════════════════

    /**
     * Muestra un diálogo de ÉXITO (ícono ✓ verde).
     *
     * BUENA PRÁCTICA: siempre pasar el nodo origen para obtener el Stage
     * y evitar que la ventana principal se restaure/minimice.
     *
     * @param nodoOrigen  Cualquier nodo visible de la vista actual (ej: un Button)
     * @param titulo      Título del diálogo
     * @param contenido   Mensaje descriptivo
     */
    public static void exito(Node nodoOrigen, String titulo, String contenido) {
        mostrarDialogo(obtenerStage(nodoOrigen), TipoAlerta.EXITO, titulo, contenido);
    }

    /**
     * Muestra un diálogo de ERROR (ícono ✕ rojo).
     */
    public static void error(Node nodoOrigen, String titulo, String contenido) {
        mostrarDialogo(obtenerStage(nodoOrigen), TipoAlerta.ERROR, titulo, contenido);
    }

    /**
     * Muestra un diálogo de ADVERTENCIA (ícono ⚠ ámbar).
     */
    public static void aviso(Node nodoOrigen, String titulo, String contenido) {
        mostrarDialogo(obtenerStage(nodoOrigen), TipoAlerta.AVISO, titulo, contenido);
    }

    /**
     * Muestra un diálogo de INFORMACIÓN (ícono ℹ índigo).
     */
    public static void informacion(Node nodoOrigen, String titulo, String contenido) {
        mostrarDialogo(obtenerStage(nodoOrigen), TipoAlerta.INFO, titulo, contenido);
    }

    /**
     * Muestra un diálogo de CONFIRMACIÓN con botones "Aceptar" y "Cancelar".
     *
     * @return true si el usuario presionó "Aceptar", false si canceló
     */
    public static boolean confirmar(Node nodoOrigen, String titulo, String contenido) {
        return mostrarConfirmacion(obtenerStage(nodoOrigen), titulo, contenido);
    }

    // ── Sobrecargas sin nodo (usa el stage activo — menos recomendado) ─

    /**
     * Versión sin nodo origen: busca el Stage activo automáticamente.
     * ADVERTENCIA: puede no funcionar correctamente en todas las situaciones.
     * Preferir las versiones con nodoOrigen.
     */
    public static void exito(String titulo, String contenido) {
        mostrarDialogo(obtenerStageActivo(), TipoAlerta.EXITO, titulo, contenido);
    }

    public static void error(String titulo, String contenido) {
        mostrarDialogo(obtenerStageActivo(), TipoAlerta.ERROR, titulo, contenido);
    }

    public static void aviso(String titulo, String contenido) {
        mostrarDialogo(obtenerStageActivo(), TipoAlerta.AVISO, titulo, contenido);
    }

    public static void informacion(String titulo, String contenido) {
        mostrarDialogo(obtenerStageActivo(), TipoAlerta.INFO, titulo, contenido);
    }

    public static boolean confirmar(String titulo, String contenido) {
        return mostrarConfirmacion(obtenerStageActivo(), titulo, contenido);
    }

    // ══════════════════════════════════════════════════════════════════
    // LÓGICA INTERNA PRIVADA
    // ══════════════════════════════════════════════════════════════════

    /**
     * Enum interno que define cada tipo de alerta con su ícono y color.
     * Patrón: cada tipo lleva su propia configuración visual (Strategy implícito).
     */
    private enum TipoAlerta {
        EXITO ("✓",  COLOR_EXITO,  "Éxito"),
        ERROR ("✕",  COLOR_ERROR,  "Error"),
        AVISO ("⚠",  COLOR_AVISO,  "Advertencia"),
        INFO  ("ℹ",  COLOR_INFO,   "Información");

        final String icono;
        final String color;
        final String etiquetaDefecto;

        TipoAlerta(String icono, String color, String etiquetaDefecto) {
            this.icono          = icono;
            this.color          = color;
            this.etiquetaDefecto = etiquetaDefecto;
        }
    }

    /**
     * Crea y muestra un diálogo modal personalizado que NO afecta el
     * estado de maximización de la ventana padre.
     *
     * Clave técnica:
     *   - initOwner(stagePadre)      → vincula el diálogo al padre
     *   - initModality(APPLICATION_MODAL) → bloquea la app sin mover la ventana
     *   - StageStyle.UNDECORATED     → sin barra de título nativa del SO
     *   - showAndWait()              → espera a que el usuario cierre
     */
    private static void mostrarDialogo(Stage stagePadre,
                                        TipoAlerta tipo,
                                        String titulo,
                                        String contenido) {
        Stage dialogo = crearStageDialogo(stagePadre);

        // ── Construcción visual del diálogo ────────────────────────
        VBox raiz = construirLayoutDialogo(tipo, titulo, contenido, false);

        // ── Botón "Aceptar" ────────────────────────────────────────
        Button btnAceptar = crearBoton("Aceptar", tipo.color, true);
        btnAceptar.setOnAction(e -> dialogo.close());

        HBox piePagina = new HBox(btnAceptar);
        piePagina.setAlignment(Pos.CENTER_RIGHT);
        piePagina.setPadding(new Insets(0, 28, 24, 28));
        raiz.getChildren().add(piePagina);

        // ── Configurar y mostrar ───────────────────────────────────
        Scene escena = new Scene(raiz);
        escena.setFill(null); // Fondo transparente para el efecto de sombra

        dialogo.setScene(escena);
        centrarSobrePadre(dialogo, stagePadre);

        /*
         * PUNTO CRÍTICO:
         * showAndWait() bloquea el hilo de JavaFX hasta que el usuario
         * cierre el diálogo, pero como el Stage tiene un owner y modality
         * correctos, NO causa que la ventana padre cambie de tamaño.
         */
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

        // Usamos un array para capturar el resultado desde el lambda
        final boolean[] resultado = {false};

        VBox raiz = construirLayoutDialogo(TipoAlerta.AVISO, titulo, contenido, true);

        // Botón Aceptar
        Button btnAceptar = crearBoton("Aceptar", COLOR_ERROR, true);
        btnAceptar.setOnAction(e -> {
            resultado[0] = true;
            dialogo.close();
        });

        // Botón Cancelar
        Button btnCancelar = crearBoton("Cancelar", COLOR_BORDE, false);
        btnCancelar.setStyle(
            btnCancelar.getStyle()
            + "-fx-text-fill: " + COLOR_SUBTEXTO + ";"
            + "-fx-border-color: " + COLOR_BORDE + ";"
        );
        btnCancelar.setOnAction(e -> dialogo.close());

        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        HBox piePagina = new HBox(10, btnCancelar, espaciador, btnAceptar);
        piePagina.setAlignment(Pos.CENTER);
        piePagina.setPadding(new Insets(0, 28, 24, 28));
        raiz.getChildren().add(piePagina);

        Scene escena = new Scene(raiz);
        escena.setFill(null);
        dialogo.setScene(escena);
        centrarSobrePadre(dialogo, stagePadre);
        dialogo.showAndWait();

        return resultado[0];
    }

    /**
     * Crea el Stage base del diálogo con la configuración correcta para
     * evitar el problema de minimización de la ventana padre.
     */
    private static Stage crearStageDialogo(Stage stagePadre) {
        Stage dialogo = new Stage();

        /*
         * SOLUCIÓN AL PROBLEMA DE MINIMIZACIÓN:
         *
         * 1. initOwner() → El diálogo queda "vinculado" al Stage padre.
         *    Esto garantiza que la ventana padre mantenga su estado.
         *
         * 2. initModality(APPLICATION_MODAL) → Bloquea TODA la aplicación
         *    mientras el diálogo está abierto, sin afectar la posición
         *    o tamaño de ninguna ventana.
         *
         * 3. StageStyle.UTILITY → Ventana pequeña sin botones de min/max.
         *    Alternativa: UNDECORATED para control total del diseño.
         */
        dialogo.initOwner(stagePadre);
        dialogo.initModality(Modality.APPLICATION_MODAL);
        dialogo.initStyle(StageStyle.UNDECORATED);  // Sin decoración nativa del SO
        dialogo.setResizable(false);

        return dialogo;
    }

    /**
     * Construye el layout visual (VBox principal) del diálogo.
     * Incluye: barra de color superior, ícono, título y contenido.
     *
     * @param esConfirmacion  si es true, agrega texto más formal
     */
    private static VBox construirLayoutDialogo(TipoAlerta tipo,
                                                String titulo,
                                                String contenido,
                                                boolean esConfirmacion) {
        // ── Barra superior de color ─────────────────────────────────
        Region barraSuperior = new Region();
        barraSuperior.setPrefHeight(6);
        barraSuperior.setMaxWidth(Double.MAX_VALUE);
        barraSuperior.setStyle("-fx-background-color: " + tipo.color + ";");

        // ── Ícono circular ──────────────────────────────────────────
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

        // ── Título ──────────────────────────────────────────────────
        String tituloFinal = (titulo == null || titulo.isBlank())
                             ? tipo.etiquetaDefecto : titulo;
        Label lblTitulo = new Label(tituloFinal);
        lblTitulo.setStyle(
            "-fx-font-size: 17px;"
            + "-fx-font-weight: bold;"
            + "-fx-text-fill: " + COLOR_TEXTO + ";"
            + "-fx-wrap-text: true;"
        );

        // ── Contenido ───────────────────────────────────────────────
        Label lblContenido = new Label(contenido);
        lblContenido.setStyle(
            "-fx-font-size: 13.5px;"
            + "-fx-text-fill: " + COLOR_SUBTEXTO + ";"
            + "-fx-wrap-text: true;"
            + "-fx-line-spacing: 2;"
        );
        lblContenido.setWrapText(true);
        lblContenido.setMaxWidth(320);

        // ── Texto extra para confirmación ───────────────────────────
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

    /** Ensambla el cuerpo principal del diálogo. */
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
        if (extra != null) {
            cuerpo.getChildren().add(extra);
        }
        return cuerpo;
    }

    /** Ensambla el layout raíz del diálogo con la barra superior. */
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
     * @param colorFondo Color de fondo (hex)
     * @param esPrimario Si es primario tiene fondo sólido, si no, outline
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
     * Si el padre está maximizado, lo centra en la pantalla.
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
     * BUENA PRÁCTICA: usar el nodo origen es más confiable que buscar
     * el stage activo, especialmente en aplicaciones multi-ventana.
     *
     * @param nodo  Cualquier nodo visible en la escena actual
     * @return      El Stage principal, o null si no se puede obtener
     */
    private static Stage obtenerStage(Node nodo) {
        if (nodo == null) return obtenerStageActivo();
        Window ventana = nodo.getScene() != null
                         ? nodo.getScene().getWindow() : null;
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
                    // Fallback: primer stage visible aunque no tenga foco
                    .orElseGet(() -> Stage.getWindows()
                                         .stream()
                                         .filter(Window::isShowing)
                                         .filter(w -> w instanceof Stage)
                                         .map(w -> (Stage) w)
                                         .findFirst()
                                         .orElse(null));
    }
}