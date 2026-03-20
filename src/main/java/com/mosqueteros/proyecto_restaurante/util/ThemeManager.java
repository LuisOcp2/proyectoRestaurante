package com.mosqueteros.proyecto_restaurante.util;

import javafx.scene.Scene;
import javafx.scene.Parent;

import java.util.Set;

public final class ThemeManager {

    private static final String DENSITY_COMPACT = "density-compact";
    private static final String DENSITY_ULTRA_COMPACT = "density-ultra-compact";
    private static final String DENSITY_COMFORTABLE = "density-comfortable";
    private static final String DENSITY_LISTENER_KEY = "theme-density-listener-installed";

    private static final Set<String> TEMAS_SOPORTADOS = Set.of(
            "artisan",
            "culinarylogic",
            "goldenbeach",
            "sweetsundays",
            "vintagereserve",
            "violet"
    );

    private ThemeManager() {
        throw new UnsupportedOperationException("Clase utilitaria");
    }

    public static String normalizarTema(String tema) {
        if (tema == null) {
            return ConfiguracionUtil.TEMA_UI_PREDETERMINADO;
        }
        String valor = tema.trim().toLowerCase();

        if ("violeta-lima".equals(valor) || "oceano-profesional".equals(valor) || "tierra-calida".equals(valor)) {
            return "violet";
        }

        if (TEMAS_SOPORTADOS.contains(valor)) {
            return valor;
        }
        return ConfiguracionUtil.TEMA_UI_PREDETERMINADO;
    }

    public static void aplicarTemaGlobal(Scene scene) {
        if (scene == null || scene.getRoot() == null) {
            return;
        }
        asegurarStylesheetsBase(scene);
        aplicarTemaEnRoot(scene.getRoot(), ConfiguracionUtil.temaUI());
        aplicarDensidadResponsive(scene);
    }

    public static void aplicarTemaEnRoot(Parent root, String tema) {
        if (root == null) {
            return;
        }

        root.getStyleClass().removeAll(
                "theme-artisan",
                "theme-culinarylogic",
                "theme-goldenbeach",
                "theme-sweetsundays",
                "theme-vintagereserve",
                "theme-violet",
                "theme-violeta-lima",
                "theme-oceano-profesional",
                "theme-tierra-calida",
                "contrast-normal",
                "contrast-high"
        );

        String temaNormalizado = normalizarTema(tema);
        root.getStyleClass().add("theme-" + temaNormalizado);
        root.getStyleClass().add(requiereContrasteAlto(temaNormalizado) ? "contrast-high" : "contrast-normal");
    }

    private static boolean requiereContrasteAlto(String temaNormalizado) {
        return "culinarylogic".equals(temaNormalizado) || "vintagereserve".equals(temaNormalizado);
    }

    private static void asegurarStylesheetsBase(Scene scene) {
        String stylesBase = ThemeManager.class
                .getResource("/com/mosqueteros/proyecto_restaurante/styles/styles.css")
                .toExternalForm();
        String stylesOverrides = ThemeManager.class
                .getResource("/com/mosqueteros/proyecto_restaurante/styles/theme-overrides.css")
                .toExternalForm();

        if (!scene.getStylesheets().contains(stylesBase)) {
            scene.getStylesheets().add(stylesBase);
        }
        if (!scene.getStylesheets().contains(stylesOverrides)) {
            scene.getStylesheets().add(stylesOverrides);
        }
    }

    private static void aplicarDensidadResponsive(Scene scene) {
        if (scene == null || scene.getRoot() == null) {
            return;
        }

        actualizarDensidad(scene);

        if (Boolean.TRUE.equals(scene.getProperties().get(DENSITY_LISTENER_KEY))) {
            return;
        }

        scene.widthProperty().addListener((obs, anterior, actual) -> actualizarDensidad(scene));
        scene.heightProperty().addListener((obs, anterior, actual) -> actualizarDensidad(scene));
        scene.getProperties().put(DENSITY_LISTENER_KEY, Boolean.TRUE);
    }

    private static void actualizarDensidad(Scene scene) {
        Parent root = scene.getRoot();
        if (root == null) {
            return;
        }

        double width = scene.getWidth();
        double height = scene.getHeight();
        boolean ultraCompacta = (width > 0 && width < 1120) || (height > 0 && height < 700);
        boolean compacta = !ultraCompacta && ((width > 0 && width < 1360) || (height > 0 && height < 820));

        root.getStyleClass().removeAll(DENSITY_ULTRA_COMPACT, DENSITY_COMPACT, DENSITY_COMFORTABLE);
        root.getStyleClass().add(ultraCompacta ? DENSITY_ULTRA_COMPACT : (compacta ? DENSITY_COMPACT : DENSITY_COMFORTABLE));
    }
}
