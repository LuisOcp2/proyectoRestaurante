package com.mosqueteros.proyecto_restaurante.util;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.layout.StackPane;

import java.util.function.Predicate;

public final class FloatingFieldHelper {
    private static final String CLASS_FOCUSED_WITHIN = "focused-within";
    private static final String CLASS_HAS_VALUE = "has-value";
    private static final String CLASS_INVALID = "invalid";

    private FloatingFieldHelper() {
    }

    public static void bindTextField(StackPane contenedor, MFXTextField field) {
        if (contenedor == null || field == null) return;

        field.focusedProperty().addListener((obs, oldVal, focused) ->
                sincronizarEstado(contenedor, focused, tieneTexto(field.getText())));
        field.textProperty().addListener((obs, oldVal, nuevo) ->
                sincronizarEstado(contenedor, field.isFocused(), tieneTexto(nuevo)));

        sincronizarEstado(contenedor, field.isFocused(), tieneTexto(field.getText()));
    }

    public static void bindComboBox(StackPane contenedor, MFXComboBox<?> combo) {
        bindComboBox(contenedor, combo, FloatingFieldHelper::tieneTexto);
    }

    public static void bindComboBox(StackPane contenedor, MFXComboBox<?> combo, Predicate<Object> hasValuePredicate) {
        if (contenedor == null || combo == null) return;
        Predicate<Object> predicate = hasValuePredicate != null ? hasValuePredicate : FloatingFieldHelper::tieneTexto;

        combo.focusedProperty().addListener((obs, oldVal, focused) ->
                sincronizarEstado(contenedor, focused, predicate.test(combo.getValue())));
        combo.valueProperty().addListener((obs, oldVal, nuevo) ->
                sincronizarEstado(contenedor, combo.isFocused(), predicate.test(nuevo)));

        sincronizarEstado(contenedor, combo.isFocused(), predicate.test(combo.getValue()));
    }

    public static void setInvalid(StackPane contenedor, boolean invalido) {
        if (contenedor == null) return;
        actualizarClase(contenedor, CLASS_INVALID, invalido);
    }

    public static void clearInvalid(StackPane... contenedores) {
        if (contenedores == null) return;
        for (StackPane c : contenedores) {
            setInvalid(c, false);
        }
    }

    private static void sincronizarEstado(StackPane contenedor, boolean focused, boolean hasValue) {
        actualizarClase(contenedor, CLASS_FOCUSED_WITHIN, focused);
        actualizarClase(contenedor, CLASS_HAS_VALUE, hasValue);
    }

    private static void actualizarClase(StackPane contenedor, String clase, boolean activa) {
        if (activa) {
            if (!contenedor.getStyleClass().contains(clase)) contenedor.getStyleClass().add(clase);
        } else {
            contenedor.getStyleClass().remove(clase);
        }
    }

    private static boolean tieneTexto(Object valor) {
        return valor != null && !String.valueOf(valor).isBlank();
    }
}
