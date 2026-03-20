package com.mosqueteros.proyecto_restaurante.controller;

import com.mosqueteros.proyecto_restaurante.dao.ConfiguracionDAO;
import com.mosqueteros.proyecto_restaurante.model.Configuracion;
import com.mosqueteros.proyecto_restaurante.util.Alertas;
import com.mosqueteros.proyecto_restaurante.util.ConfiguracionUtil;
import com.mosqueteros.proyecto_restaurante.util.ThemeManager;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Parent;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import io.github.palexdev.materialfx.controls.*;

public class ConfiguracionController {

    @FXML private MFXTextField txtBuscarConfiguracion;

    @FXML private TableView<Configuracion> tblListaConfiguraciones;
    @FXML private TableColumn<Configuracion, Long> colIdConfiguracion;
    @FXML private TableColumn<Configuracion, String> colClave;
    @FXML private TableColumn<Configuracion, String> colValor;
    @FXML private Label lblConteoConfiguraciones;
    @FXML private VBox boxPlaceholder;

    @FXML private MFXTextField txtCfgClave;
    @FXML private MFXTextField txtCfgValor;
    @FXML private MFXButton btnEliminarConfiguracionForm;
    @FXML private Label lblMensajeConfiguracion;
    @FXML private MFXComboBox<String> cmbTemaUI;
    @FXML private MFXComboBox<String> cmbPerfilInicial;
    @FXML private StackPane swatchVioletaLima;
    @FXML private StackPane swatchOceanoProfesional;
    @FXML private StackPane swatchTierraCalida;
    @FXML private StackPane swatchGoldenBeach;
    @FXML private StackPane swatchSweetSundays;
    @FXML private StackPane swatchVintageReserve;
    @FXML private VBox boxThemePreview;
    @FXML private Label lblTemaActivoTitulo;
    @FXML private Label lblTemaActivoDescripcion;

    private final ObservableList<Configuracion> listaConfiguraciones = FXCollections.observableArrayList();
    private final ObservableList<String> opcionesTemaUI = FXCollections.observableArrayList(
            "violet",
            "artisan",
            "culinarylogic",
            "goldenbeach",
            "sweetsundays",
            "vintagereserve"
    );
    private final ObservableList<String> opcionesPerfilInicial = FXCollections.observableArrayList(
            "Restaurante tradicional",
            "Cafeteria",
            "Food truck"
    );
    private final Map<String, String> temaNombre = Map.of(
            "violet", "Violet",
            "artisan", "Artisan",
            "culinarylogic", "Culinary Logic",
            "goldenbeach", "Golden Beach",
            "sweetsundays", "Sweet Sundays",
            "vintagereserve", "Vintage Reserve"
    );
    private final Map<String, String> temaDescripcion = Map.of(
            "violet", "Imagen premium para destacar promociones, platos estrella y llamadas a la accion en horas pico.",
            "artisan", "Ambiente calido y artesanal para conectar con experiencias de cocina casera y servicio cercano.",
            "culinarylogic", "Estetica sobria para operacion eficiente en cocina, caja y seguimiento de comandas sin distracciones.",
            "goldenbeach", "Look fresco para sedes de alto flujo con menus visuales, combos del dia y atencion dinamica.",
            "sweetsundays", "Tono amigable ideal para postres, cafeteria y momentos familiares con foco en recompra.",
            "vintagereserve", "Estilo elegante para cartas especiales, reservas y una percepcion de marca mas exclusiva."
    );
    private Configuracion configuracionSeleccionada;
    private String temaActual;
    private boolean inicializandoTema = true;
    private boolean cambioInternoTema = false;

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarSeleccionTabla();
        inicializarSelectorTema();
        cargarConfiguraciones();
        inicializandoTema = false;
    }

    @FXML
    private void restaurarTemaPredeterminado() {
        if (ConfiguracionUtil.TEMA_UI_PREDETERMINADO.equals(cmbTemaUI.getValue())) {
            mostrarMensaje("El tema predeterminado ya esta activo.", false);
            return;
        }
        cmbTemaUI.setValue(ConfiguracionUtil.TEMA_UI_PREDETERMINADO);
    }

    @FXML
    private void seleccionarTemaVioletaLima() {
        cmbTemaUI.setValue("violet");
    }

    @FXML
    private void seleccionarTemaOceanoProfesional() {
        cmbTemaUI.setValue("artisan");
    }

    @FXML
    private void seleccionarTemaTierraCalida() {
        cmbTemaUI.setValue("culinarylogic");
    }

    @FXML
    private void seleccionarTemaGoldenBeach() {
        cmbTemaUI.setValue("goldenbeach");
    }

    @FXML
    private void seleccionarTemaSweetSundays() {
        cmbTemaUI.setValue("sweetsundays");
    }

    @FXML
    private void seleccionarTemaVintageReserve() {
        cmbTemaUI.setValue("vintagereserve");
    }

    @FXML
    private void seleccionarTemaAnterior() {
        cambiarTemaPorPaso(-1);
    }

    @FXML
    private void seleccionarTemaSiguiente() {
        cambiarTemaPorPaso(1);
    }

    @FXML
    private void usarPresetEmpresaNombre() {
        aplicarPreset(ConfiguracionUtil.CLAVE_COMPANY_NAME, "Mi Restaurante", "Preset cargado: nombre de empresa.");
    }

    @FXML
    private void usarPresetMoneda() {
        aplicarPreset(ConfiguracionUtil.CLAVE_CURRENCY_SYMBOL, "$", "Preset cargado: simbolo de moneda.");
    }

    @FXML
    private void usarPresetImpuesto() {
        aplicarPreset(ConfiguracionUtil.CLAVE_TAX_PERCENTAGE, "19", "Preset cargado: porcentaje de impuesto.");
    }

    @FXML
    private void usarPresetPieTicket() {
        aplicarPreset(ConfiguracionUtil.CLAVE_TICKET_FOOTER, "Gracias por su compra.", "Preset cargado: pie de ticket.");
    }

    @FXML
    private void usarPresetCodigoMoneda() {
        aplicarPreset(ConfiguracionUtil.CLAVE_CURRENCY_CODE, "COP", "Preset cargado: codigo de moneda.");
    }

    @FXML
    private void usarPresetTelefonoEmpresa() {
        aplicarPreset(ConfiguracionUtil.CLAVE_COMPANY_PHONE, "+57 300 123 4567", "Preset cargado: telefono de empresa.");
    }

    @FXML
    private void usarPresetZonaHoraria() {
        aplicarPreset(ConfiguracionUtil.CLAVE_TIMEZONE, "America/Bogota", "Preset cargado: zona horaria.");
    }

    @FXML
    private void usarPresetCorreoEmpresa() {
        aplicarPreset(ConfiguracionUtil.CLAVE_COMPANY_EMAIL, "contacto@mi-restaurante.com", "Preset cargado: correo de empresa.");
    }

    @FXML
    private void usarPresetPrefijoFactura() {
        aplicarPreset(ConfiguracionUtil.CLAVE_INVOICE_PREFIX, "FAC", "Preset cargado: prefijo de factura.");
    }

    @FXML
    private void usarPresetDecimalesMoneda() {
        aplicarPreset(ConfiguracionUtil.CLAVE_CURRENCY_DECIMALS, "2", "Preset cargado: decimales de moneda.");
    }

    @FXML
    private void cargarConfiguracionInicial() {
        String perfil = obtenerPerfilSeleccionado();
        Alertas.confirmar(
                "Cargar configuracion inicial",
                "Se cargara el perfil '" + perfil + "' con valores base de empresa, moneda, impuestos y facturacion.",
                () -> {
                    try {
                        LinkedHashMap<String, String> presets = construirPresetSegunPerfil(perfil);

                        List<String> clavesExistentes = ConfiguracionDAO.listarTodas().stream()
                                .map(Configuracion::getCfgClave)
                                .toList();

                        int creadas = 0;
                        int actualizadas = 0;
                        for (Map.Entry<String, String> entry : presets.entrySet()) {
                            boolean existe = clavesExistentes.contains(entry.getKey());
                            if (ConfiguracionDAO.guardarPorClave(entry.getKey(), entry.getValue())) {
                                if (existe) {
                                    actualizadas++;
                                } else {
                                    creadas++;
                                }
                            }
                        }

                        cargarConfiguraciones();
                        prepararNuevaConfiguracion();
                        mostrarMensaje("Perfil '" + perfil + "' aplicado: " + creadas + " creadas y " + actualizadas + " actualizadas.", false);
                    } catch (SQLException e) {
                        mostrarMensaje("No se pudo cargar la configuracion inicial: " + e.getMessage(), true);
                    }
                }
        );
    }

    private String obtenerPerfilSeleccionado() {
        if (cmbPerfilInicial == null || cmbPerfilInicial.getValue() == null || cmbPerfilInicial.getValue().isBlank()) {
            return "Restaurante tradicional";
        }
        return cmbPerfilInicial.getValue();
    }

    private LinkedHashMap<String, String> construirPresetSegunPerfil(String perfil) {
        LinkedHashMap<String, String> presets = new LinkedHashMap<>();

        presets.put(ConfiguracionUtil.CLAVE_COMPANY_NAME, "Mi Restaurante");
        presets.put(ConfiguracionUtil.CLAVE_COMPANY_ADDRESS, "Calle 123 #45-67");
        presets.put(ConfiguracionUtil.CLAVE_COMPANY_PHONE, "+57 300 123 4567");
        presets.put(ConfiguracionUtil.CLAVE_COMPANY_EMAIL, "contacto@mi-restaurante.com");
        presets.put(ConfiguracionUtil.CLAVE_CURRENCY_SYMBOL, "$");
        presets.put(ConfiguracionUtil.CLAVE_CURRENCY_CODE, "COP");
        presets.put(ConfiguracionUtil.CLAVE_CURRENCY_DECIMALS, "2");
        presets.put(ConfiguracionUtil.CLAVE_TIMEZONE, "America/Bogota");
        presets.put(ConfiguracionUtil.CLAVE_UI_THEME, temaActual != null ? temaActual : ConfiguracionUtil.TEMA_UI_PREDETERMINADO);
        presets.put(ConfiguracionUtil.CLAVE_UI_QUICK_PROFILE, perfil);

        if ("Cafeteria".equals(perfil)) {
            presets.put(ConfiguracionUtil.CLAVE_COMPANY_NAME, "Cafe de la Esquina");
            presets.put(ConfiguracionUtil.CLAVE_DEFAULT_TIP_PERCENTAGE, "8");
            presets.put(ConfiguracionUtil.CLAVE_TAX_PERCENTAGE, "19");
            presets.put(ConfiguracionUtil.CLAVE_TICKET_FOOTER, "Gracias por visitarnos. Tu cafe favorito te espera.");
            presets.put(ConfiguracionUtil.CLAVE_INVOICE_PREFIX, "CAF");
            return presets;
        }

        if ("Food truck".equals(perfil)) {
            presets.put(ConfiguracionUtil.CLAVE_COMPANY_NAME, "Ruta Street Food");
            presets.put(ConfiguracionUtil.CLAVE_DEFAULT_TIP_PERCENTAGE, "5");
            presets.put(ConfiguracionUtil.CLAVE_TAX_PERCENTAGE, "8");
            presets.put(ConfiguracionUtil.CLAVE_TICKET_FOOTER, "Gracias por rodar con nosotros. Nos vemos en la proxima parada.");
            presets.put(ConfiguracionUtil.CLAVE_INVOICE_PREFIX, "TRK");
            return presets;
        }

        presets.put(ConfiguracionUtil.CLAVE_DEFAULT_TIP_PERCENTAGE, "10");
        presets.put(ConfiguracionUtil.CLAVE_TAX_PERCENTAGE, "19");
        presets.put(ConfiguracionUtil.CLAVE_TICKET_FOOTER, "Gracias por su compra. Vuelva pronto.");
        presets.put(ConfiguracionUtil.CLAVE_INVOICE_PREFIX, "FAC");
        return presets;
    }

    @FXML
    private void buscarConfiguracion() {
        try {
            listaConfiguraciones.setAll(
                    ConfiguracionDAO.buscarConFiltros(txtBuscarConfiguracion.getText())
            );
            actualizarConteo();
            actualizarPlaceholder();
        } catch (Exception e) {
            Alertas.error("Error al buscar", e.getMessage());
        }
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscarConfiguracion.clear();
        cargarConfiguraciones();
    }

    @FXML
    private void prepararNuevaConfiguracion() {
        configuracionSeleccionada = null;
        txtCfgClave.clear();
        txtCfgValor.clear();
        ocultarMensaje();
        btnEliminarConfiguracionForm.setDisable(true);
        tblListaConfiguraciones.getSelectionModel().clearSelection();
        txtCfgClave.requestFocus();
    }

    @FXML
    private void guardarConfiguracion() {
        String clave = txtCfgClave.getText() != null ? txtCfgClave.getText().trim() : "";
        String valor = txtCfgValor.getText() != null ? txtCfgValor.getText().trim() : "";

        if (clave.isBlank()) {
            mostrarMensaje("La clave es obligatoria.", true);
            return;
        }

        try {
            Long excluirId = configuracionSeleccionada != null ? configuracionSeleccionada.getCfgId() : null;
            if (ConfiguracionDAO.existeClave(clave, excluirId)) {
                mostrarMensaje("La clave ya existe. Usa una clave diferente.", true);
                return;
            }

            boolean guardado;
            if (configuracionSeleccionada == null) {
                guardado = ConfiguracionDAO.insertar(new Configuracion(0L, clave, valor));
                if (guardado) {
                    mostrarMensaje("Configuracion creada correctamente.", false);
                }
            } else {
                Configuracion actualizada = new Configuracion(
                        configuracionSeleccionada.getCfgId(),
                        clave,
                        valor
                );
                guardado = ConfiguracionDAO.actualizar(actualizada);
                if (guardado) {
                    mostrarMensaje("Configuracion actualizada correctamente.", false);
                }
            }

            if (guardado) {
                cargarConfiguraciones();
                prepararNuevaConfiguracion();
            }
        } catch (Exception e) {
            mostrarMensaje("No se pudo guardar: " + e.getMessage(), true);
        }
    }

    @FXML
    private void eliminarConfiguracion() {
        if (configuracionSeleccionada == null) {
            return;
        }

        Alertas.confirmar(
                "Eliminar configuracion",
                "Se eliminara la clave '" + configuracionSeleccionada.getCfgClave() + "'.",
                () -> {
                    try {
                        if (ConfiguracionDAO.eliminar(configuracionSeleccionada.getCfgId())) {
                            Alertas.exito("Configuracion eliminada", "Registro eliminado correctamente.");
                            prepararNuevaConfiguracion();
                            cargarConfiguraciones();
                        }
                    } catch (Exception e) {
                        Alertas.error("Error al eliminar", e.getMessage());
                    }
                }
        );
    }

    private void configurarColumnas() {
        colIdConfiguracion.setCellValueFactory(data ->
                new SimpleLongProperty(data.getValue().getCfgId()).asObject()
        );
        colClave.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCfgClave())
        );
        colValor.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCfgValor())
        );

        tblListaConfiguraciones.setItems(listaConfiguraciones);
        tblListaConfiguraciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void configurarSeleccionTabla() {
        tblListaConfiguraciones.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, actual) -> {
                    if (actual != null) {
                        cargarFormulario(actual);
                    }
                });
    }

    private void inicializarSelectorTema() {
        cmbTemaUI.setItems(opcionesTemaUI);
        temaActual = ThemeManager.normalizarTema(ConfiguracionUtil.temaUI());
        establecerTemaEnCombo(temaActual);
        actualizarVistaTema(temaActual);
        aplicarTemaEnEscena(temaActual);

        cmbTemaUI.valueProperty().addListener((obs, anterior, actual) -> {
            if (cambioInternoTema || actual == null || actual.isBlank()) {
                return;
            }
            manejarCambioTema(actual);
        });

        if (cmbPerfilInicial != null) {
            cmbPerfilInicial.setItems(opcionesPerfilInicial);
            String perfilGuardado = ConfiguracionUtil.obtener(ConfiguracionUtil.CLAVE_UI_QUICK_PROFILE, "Restaurante tradicional");
            if (!opcionesPerfilInicial.contains(perfilGuardado)) {
                perfilGuardado = "Restaurante tradicional";
            }
            cmbPerfilInicial.setValue(perfilGuardado);
        }
    }

    private void cargarFormulario(Configuracion configuracion) {
        configuracionSeleccionada = configuracion;
        txtCfgClave.setText(configuracion.getCfgClave() != null ? configuracion.getCfgClave() : "");
        txtCfgValor.setText(configuracion.getCfgValor() != null ? configuracion.getCfgValor() : "");
        ocultarMensaje();
        btnEliminarConfiguracionForm.setDisable(false);
    }

    private void cargarConfiguraciones() {
        try {
            listaConfiguraciones.setAll(ConfiguracionDAO.listarTodas());
            actualizarConteo();
            actualizarPlaceholder();
        } catch (Exception e) {
            Alertas.error("Error al cargar configuracion", e.getMessage());
        }
    }

    private void actualizarConteo() {
        int total = listaConfiguraciones.size();
        lblConteoConfiguraciones.setText(total + (total == 1
                ? " configuracion encontrada"
                : " configuraciones encontradas"));
    }

    private void actualizarPlaceholder() {
        boolean hayDatos = !listaConfiguraciones.isEmpty();
        tblListaConfiguraciones.setVisible(hayDatos);
        tblListaConfiguraciones.setManaged(hayDatos);
        boxPlaceholder.setVisible(!hayDatos);
        boxPlaceholder.setManaged(!hayDatos);
    }

    private void mostrarMensaje(String mensaje, boolean error) {
        lblMensajeConfiguracion.setText(mensaje);
        lblMensajeConfiguracion.getStyleClass().removeAll("form-mensaje-ok", "form-mensaje-error");
        lblMensajeConfiguracion.getStyleClass().add(error ? "form-mensaje-error" : "form-mensaje-ok");
        lblMensajeConfiguracion.setVisible(true);
        lblMensajeConfiguracion.setManaged(true);
    }

    private void ocultarMensaje() {
        lblMensajeConfiguracion.setText("");
        lblMensajeConfiguracion.setVisible(false);
        lblMensajeConfiguracion.setManaged(false);
    }

    private void aplicarTemaEnEscena(String tema) {
        if (tblListaConfiguraciones.getScene() == null) {
            return;
        }
        Parent root = tblListaConfiguraciones.getScene().getRoot();
        ThemeManager.aplicarTemaEnRoot(root, tema);
    }

    private void manejarCambioTema(String temaSeleccionado) {
        String temaNormalizado = ThemeManager.normalizarTema(temaSeleccionado);

        aplicarTemaEnEscena(temaNormalizado);
        actualizarVistaTema(temaNormalizado);

        if (inicializandoTema || temaNormalizado.equals(temaActual)) {
            return;
        }

        try {
            if (ConfiguracionDAO.guardarPorClave(ConfiguracionUtil.CLAVE_UI_THEME, temaNormalizado)) {
                temaActual = temaNormalizado;
                mostrarMensaje("Tema aplicado en vivo: " + temaNombre.getOrDefault(temaNormalizado, temaNormalizado) + ".", false);
                cargarConfiguraciones();
            }
        } catch (Exception e) {
            aplicarTemaEnEscena(temaActual);
            actualizarVistaTema(temaActual);
            establecerTemaEnCombo(temaActual);
            mostrarMensaje("No se pudo guardar el tema: " + e.getMessage(), true);
        }
    }

    private void cambiarTemaPorPaso(int paso) {
        if (opcionesTemaUI.isEmpty()) {
            return;
        }
        String valorActual = ThemeManager.normalizarTema(cmbTemaUI.getValue());
        int indiceActual = opcionesTemaUI.indexOf(valorActual);
        if (indiceActual < 0) {
            indiceActual = 0;
        }
        int nuevoIndice = (indiceActual + paso + opcionesTemaUI.size()) % opcionesTemaUI.size();
        cmbTemaUI.setValue(opcionesTemaUI.get(nuevoIndice));
    }

    private void aplicarPreset(String clave, String valorSugerido, String mensaje) {
        txtCfgClave.setText(clave);
        txtCfgValor.setText(valorSugerido);
        txtCfgValor.requestFocus();
        txtCfgValor.positionCaret(txtCfgValor.getText().length());
        mostrarMensaje(mensaje, false);
    }

    private void establecerTemaEnCombo(String tema) {
        cambioInternoTema = true;
        cmbTemaUI.setValue(tema);
        cambioInternoTema = false;
    }

    private void actualizarSwatchActivo(String tema) {
        swatchVioletaLima.getStyleClass().remove("theme-swatch-active");
        swatchOceanoProfesional.getStyleClass().remove("theme-swatch-active");
        swatchTierraCalida.getStyleClass().remove("theme-swatch-active");
        swatchGoldenBeach.getStyleClass().remove("theme-swatch-active");
        swatchSweetSundays.getStyleClass().remove("theme-swatch-active");
        swatchVintageReserve.getStyleClass().remove("theme-swatch-active");

        if ("artisan".equals(tema)) {
            swatchOceanoProfesional.getStyleClass().add("theme-swatch-active");
            return;
        }
        if ("culinarylogic".equals(tema)) {
            swatchTierraCalida.getStyleClass().add("theme-swatch-active");
            return;
        }
        if ("goldenbeach".equals(tema)) {
            swatchGoldenBeach.getStyleClass().add("theme-swatch-active");
            return;
        }
        if ("sweetsundays".equals(tema)) {
            swatchSweetSundays.getStyleClass().add("theme-swatch-active");
            return;
        }
        if ("vintagereserve".equals(tema)) {
            swatchVintageReserve.getStyleClass().add("theme-swatch-active");
            return;
        }
        swatchVioletaLima.getStyleClass().add("theme-swatch-active");
    }

    private void actualizarVistaTema(String tema) {
        actualizarSwatchActivo(tema);
        if (lblTemaActivoTitulo != null) {
            lblTemaActivoTitulo.setText(temaNombre.getOrDefault(tema, "Violet"));
        }
        if (lblTemaActivoDescripcion != null) {
            lblTemaActivoDescripcion.setText(temaDescripcion.getOrDefault(tema,
                    "Tema optimizado para una experiencia visual consistente en todo el sistema."));
        }
        if (boxThemePreview == null) {
            return;
        }
        boxThemePreview.getStyleClass().removeAll(
                "theme-preview-violet",
                "theme-preview-artisan",
                "theme-preview-culinarylogic",
                "theme-preview-goldenbeach",
                "theme-preview-sweetsundays",
                "theme-preview-vintagereserve"
        );
        boxThemePreview.getStyleClass().add("theme-preview-" + ThemeManager.normalizarTema(tema));
    }
}
