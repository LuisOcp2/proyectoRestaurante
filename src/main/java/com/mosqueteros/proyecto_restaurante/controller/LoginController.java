package com.mosqueteros.proyecto_restaurante.controller;

import com.mosqueteros.proyecto_restaurante.dao.UsuarioDAO;
import com.mosqueteros.proyecto_restaurante.model.Usuario;
import com.mosqueteros.proyecto_restaurante.util.SessionUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    @FXML private MFXTextField     texUsuario;
    @FXML private MFXPasswordField texContrasena;
    @FXML private MFXTextField     texContrasenaVisible;
    @FXML private Label            etiLogo;
    @FXML private MFXCheckbox      casRecordar;
    @FXML private Label            etiErrorUsuario;
    @FXML private Label            etiErrorContrasena;
    @FXML private Label            etiErrorGeneral;
    @FXML private MFXButton        botIngreso;
    @FXML private MFXButton        botAlternarContrasena;
    @FXML private VBox             contenedorLogo;
    @FXML private VBox             contenedorFormulario;

    private boolean contrasenaVisible = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deshabilitarMenuContextual();
        ocultarOjoNativoMFX();
        animarEntrada();
        configurarListeners();
    }


    private void deshabilitarMenuContextual() {
        if (texUsuario != null) {
            texUsuario.setContextMenu(null);
        }
        if (texContrasena != null) {
            texContrasena.setContextMenu(null);
        }
        if (texContrasenaVisible != null) {
            texContrasenaVisible.setContextMenu(null);
        }
    }

    private void ocultarOjoNativoMFX() {
        texContrasena.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> aplicarOcultamientoOjo());
            }
        });
    }

    private void aplicarOcultamientoOjo() {
        System.out.println("=== Hijos de MFXPasswordField (" 
                + texContrasena.getChildrenUnmodifiable().size() + ") ===");
        for (javafx.scene.Node child : texContrasena.getChildrenUnmodifiable()) {
            System.out.println("  Tipo: " + child.getClass().getSimpleName()
                    + " | Clases CSS: " + child.getStyleClass()
                    + " | Id: " + child.getId());
        }

        String[] selectores = {
            ".password-field-toggle-button",
            ".toggle-button",
            ".mfx-icon-wrapper"
        };
        for (String sel : selectores) {
            javafx.scene.Node n = texContrasena.lookup(sel);
            if (n != null) {
                System.out.println("✅ Encontrado por selector: " + sel
                        + " → " + n.getStyleClass());
                n.setVisible(false);
                n.setManaged(false);
                return;
            }
        }

        for (javafx.scene.Node child : texContrasena.getChildrenUnmodifiable()) {
            boolean esAreaTexto = child.getStyleClass().stream()
                    .anyMatch(c -> c.contains("text") || c.contains("field") || c.contains("caret"));
            if (!esAreaTexto) {
                System.out.println("🔒 Ocultando por fallback: " + child.getClass().getSimpleName()
                        + " clases=" + child.getStyleClass());
                child.setVisible(false);
                child.setManaged(false);
            }
        }
    }

    private void animarEntrada() {
        if (contenedorFormulario == null) {
            return;
        }

        contenedorFormulario.setOpacity(0);
        contenedorFormulario.setTranslateY(30);

        FadeTransition fade = new FadeTransition(Duration.millis(600), contenedorFormulario);
        fade.setFromValue(0);
        fade.setToValue(1);

        Timeline slide = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(contenedorFormulario.translateYProperty(), 30)),
                new KeyFrame(Duration.millis(600), new KeyValue(contenedorFormulario.translateYProperty(), 0))
        );

        fade.play();
        slide.play();

        if (contenedorLogo != null) {
            contenedorLogo.setOpacity(0);
            FadeTransition logoFade = new FadeTransition(Duration.millis(400), contenedorLogo);
            logoFade.setFromValue(0);
            logoFade.setToValue(1);
            logoFade.play();
        }
    }

    private void configurarListeners() {
        texUsuario.textProperty().addListener((obs, anterior, nuevo) -> {
            if (!nuevo.isEmpty()) {
                limpiarErrorCampo(texUsuario, etiErrorUsuario);
            }
        });

        texContrasena.textProperty().addListener((obs, anterior, nuevo) -> {
            if (!nuevo.isEmpty()) {
                limpiarErrorCampo(texContrasena, etiErrorContrasena);
            }
            if (!nuevo.equals(texContrasenaVisible.getText())) {
                texContrasenaVisible.setText(nuevo);
            }
        });

        if (texContrasenaVisible != null) {
            texContrasenaVisible.textProperty().addListener((obs, anterior, nuevo) -> {
                if (!nuevo.isEmpty()) {
                    limpiarErrorCampo(texContrasena, etiErrorContrasena);
                }
                if (!nuevo.equals(texContrasena.getText())) {
                    texContrasena.setText(nuevo);
                    texContrasenaVisible.positionCaret(nuevo.length());
                }
            });
        }
    }

    @FXML
    private void iniciarSesion(ActionEvent event) {
        ocultarBannerError();

        boolean valido = true;
        if (texUsuario.getText().isBlank()) {
            mostrarErrorCampo(texUsuario, etiErrorUsuario);
            valido = false;
        }
        if (texContrasena.getText().isBlank()) {
            mostrarErrorCampo(texContrasena, etiErrorContrasena);
            valido = false;
        }
        if (!valido) {
            sacudirBoton();
            return;
        }

        activarCargando(true);

        String loginStr = texUsuario.getText().trim();
        String password = texContrasena.getText();

        Task<Boolean> tareaLogin = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                Thread.sleep(800);
                Usuario usuario = UsuarioDAO.autenticar(loginStr, password);
                if (usuario != null) {
                    SessionUtil.saveUser(usuario);
                    return true;
                }
                return false;
            }
        };

        tareaLogin.setOnSucceeded(e -> Platform.runLater(() -> {
            activarCargando(false);
            if (tareaLogin.getValue()) {
                manejarLoginExitoso(loginStr);
            } else {
                manejarLoginFallido();
            }
        }));

        tareaLogin.setOnFailed(e -> Platform.runLater(() -> {
            activarCargando(false);
            Throwable ex = tareaLogin.getException();
            String msg = ex != null ? ex.getMessage() : "Error desconocido";
            mostrarBannerError("Error: " + msg);
            System.err.println("❌ Task falló: " + msg);
        }));

        Thread hilo = new Thread(tareaLogin);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void manejarLoginExitoso(String usuarioLogin) {
        System.out.println("✅ Login exitoso: " + usuarioLogin + " - " + SessionUtil.getUserRole());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mosqueteros/proyecto_restaurante/view/main.fxml"));
            Parent root = loader.load();
            MainController mainCtrl = loader.getController();
            mainCtrl.inicializarDashboard();
            
            Stage stage = (Stage) botIngreso.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Cali Delights - " + SessionUtil.getUserRole());
            stage.show();
        } catch (Exception e) {
            mostrarBannerError("Error dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void manejarLoginFallido() {
        System.out.println("❌ Credenciales inválidas");
        mostrarBannerError("Usuario o contraseña incorrectos");
        texContrasena.clear();
        mostrarErrorCampo(texUsuario, null);
        mostrarErrorCampo(texContrasena, null);
        sacudirBoton();
    }

    @FXML
    private void toggleContrasena(ActionEvent event) {
        contrasenaVisible = !contrasenaVisible;

        if (contrasenaVisible) {
            texContrasenaVisible.setText(texContrasena.getText());
            texContrasenaVisible.setVisible(true);
            texContrasenaVisible.setManaged(true);
            texContrasena.setVisible(false);
            texContrasena.setManaged(false);
            texContrasenaVisible.requestFocus();
            texContrasenaVisible.positionCaret(texContrasenaVisible.getText().length());
            botAlternarContrasena.setText("🙈");
            if (!botAlternarContrasena.getStyleClass().contains("eye-btn-active")) {
                botAlternarContrasena.getStyleClass().add("eye-btn-active");
            }
        } else {
            texContrasena.setText(texContrasenaVisible.getText());
            texContrasena.setVisible(true);
            texContrasena.setManaged(true);
            texContrasenaVisible.setVisible(false);
            texContrasenaVisible.setManaged(false);
            texContrasena.requestFocus();
            texContrasena.positionCaret(texContrasena.getText().length());
            botAlternarContrasena.setText("👁");
            botAlternarContrasena.getStyleClass().remove("eye-btn-active");
        }

        ScaleTransition pulso = new ScaleTransition(Duration.millis(120), botAlternarContrasena);
        pulso.setByX(0.2);
        pulso.setByY(0.2);
        pulso.setAutoReverse(true);
        pulso.setCycleCount(2);
        pulso.play();
    }

    @FXML
    private void olvidoContrasena(MouseEvent event) {
        mostrarBannerError("Contacte al administrador del sistema.");
    }

    @FXML
    private void validarCampoAlEscribir(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            botIngreso.fire();
        }
    }

    private void mostrarErrorCampo(MFXTextField campo, Label etiqueta) {
        if (!campo.getStyleClass().contains("input-error")) {
            campo.getStyleClass().add("input-error");
        }
        campo.getStyleClass().remove("input-valid");
        if (etiqueta != null) {
            etiqueta.setVisible(true);
            etiqueta.setManaged(true);
            FadeTransition ft = new FadeTransition(Duration.millis(250), etiqueta);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }
    }

    private void mostrarErrorCampo(MFXPasswordField campo, Label etiqueta) {
        if (!campo.getStyleClass().contains("input-error")) {
            campo.getStyleClass().add("input-error");
        }
        campo.getStyleClass().remove("input-valid");
        if (etiqueta != null) {
            etiqueta.setVisible(true);
            etiqueta.setManaged(true);
        }
    }

    private void limpiarErrorCampo(MFXTextField campo, Label etiqueta) {
        campo.getStyleClass().remove("input-error");
        if (!campo.getStyleClass().contains("input-valid")) {
            campo.getStyleClass().add("input-valid");
        }
        if (etiqueta != null) {
            etiqueta.setVisible(false);
            etiqueta.setManaged(false);
        }
    }

    private void limpiarErrorCampo(MFXPasswordField campo, Label etiqueta) {
        campo.getStyleClass().remove("input-error");
        if (!campo.getStyleClass().contains("input-valid")) {
            campo.getStyleClass().add("input-valid");
        }
        if (etiqueta != null) {
            etiqueta.setVisible(false);
            etiqueta.setManaged(false);
        }
    }

    private void mostrarBannerError(String mensaje) {
        etiErrorGeneral.setText("⚠  " + mensaje);
        etiErrorGeneral.setVisible(true);
        etiErrorGeneral.setManaged(true);
        FadeTransition ft = new FadeTransition(Duration.millis(300), etiErrorGeneral);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void ocultarBannerError() {
        etiErrorGeneral.setVisible(false);
        etiErrorGeneral.setManaged(false);
    }

    private void activarCargando(boolean cargando) {
        botIngreso.setDisable(cargando);
        texUsuario.setDisable(cargando);
        texContrasena.setDisable(cargando);
        if (texContrasenaVisible != null) {
            texContrasenaVisible.setDisable(cargando);
        }

        botIngreso.setText(cargando ? "Verificando…" : "Ingresar  →");
        if (cargando) {
            botIngreso.getStyleClass().add("btn-loading");
        } else {
            botIngreso.getStyleClass().remove("btn-loading");
        }
    }

    private void sacudirBoton() {
        Timeline sacudida = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(botIngreso.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(60), new KeyValue(botIngreso.translateXProperty(), 8)),
                new KeyFrame(Duration.millis(120), new KeyValue(botIngreso.translateXProperty(), -8)),
                new KeyFrame(Duration.millis(180), new KeyValue(botIngreso.translateXProperty(), 6)),
                new KeyFrame(Duration.millis(240), new KeyValue(botIngreso.translateXProperty(), -6)),
                new KeyFrame(Duration.millis(300), new KeyValue(botIngreso.translateXProperty(), 3)),
                new KeyFrame(Duration.millis(360), new KeyValue(botIngreso.translateXProperty(), 0))
        );
        sacudida.play();
    }
}
