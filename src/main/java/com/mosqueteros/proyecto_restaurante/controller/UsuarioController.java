package com.mosqueteros.proyecto_restaurante.controller;

import com.mosqueteros.proyecto_restaurante.dao.UsuarioDAO;
import com.mosqueteros.proyecto_restaurante.model.Perfil;
import com.mosqueteros.proyecto_restaurante.model.Usuario;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador JavaFX para la vista VistaUsuarios.fxml.
 * Gestiona el CRUD completo de usuarios del sistema.
 *
 * CORRECCIÓN: tblListaUsuarios era MFXTableView pero el FXML usa
 * TableView estándar de JavaFX → se cambió a TableView<Usuario>.
 *
 * Flujo principal:
 *   1. initialize() → carga perfiles y lista inicial de usuarios
 *   2. buscarUsuario() → aplica filtros y recarga la tabla
 *   3. limpiarFiltros() → resetea combos y recarga todos
 *   4. guardarUsuario() → INSERT o UPDATE según modo actual
 *   5. eliminarUsuario() → DELETE con confirmación
 *   6. prepararNuevoUsuario() → limpia formulario para nuevo registro
 */
public class UsuarioController implements Initializable {

    // ─── Barra de filtros ────────────────────────────────────────
    /** Campo de búsqueda por nombre, apellido o login */
    @FXML private MFXTextField txtBuscarUsuario;
    /** ComboBox nativo para filtrar por perfil/rol */
    @FXML private ComboBox<String> cmbFiltroPerfil;
    /** ComboBox nativo para filtrar por estado */
    @FXML private ComboBox<String> cmbFiltroEstado;

    // ─── Tabla ───────────────────────────────────────────────────
    /**
     * Tabla estándar JavaFX — DEBE ser TableView, NO MFXTableView,
     * porque el FXML usa <TableView> (import javafx.scene.control.TableView).
     */
    @FXML private TableView<Usuario> tblListaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colIdUsuario;
    @FXML private TableColumn<Usuario, String>  colNombre;
    @FXML private TableColumn<Usuario, String>  colApellido;
    @FXML private TableColumn<Usuario, String>  colLogin;
    @FXML private TableColumn<Usuario, String>  colCorreo;
    @FXML private TableColumn<Usuario, String>  colPerfil;
    @FXML private TableColumn<Usuario, String>  colEstado;
    /** Etiqueta que muestra el total de usuarios encontrados */
    @FXML private Label lblConteoUsuarios;
    /** Contenedor del estado vacío (sin resultados) */
    @FXML private VBox boxPlaceholder;

    // ─── Formulario de detalle ───────────────────────────────────
    /** Campo nombre del usuario */
    @FXML private MFXTextField txtUsuNombre;
    /** Campo apellido del usuario */
    @FXML private MFXTextField txtUsuApellido;
    /** Campo login del usuario */
    @FXML private MFXTextField txtUsuLogin;
    /** Campo correo del usuario */
    @FXML private MFXTextField txtUsuCorreo;
    /** Campo teléfono del usuario */
    @FXML private MFXTextField txtUsuTelefono;
    /** Campo dirección del usuario */
    @FXML private MFXTextField txtUsuDireccion;
    /** Campo contraseña (MFXPasswordField oculta el texto) */
    @FXML private MFXPasswordField txtUsuPass;
    /** ComboBox perfil/rol del formulario */
    @FXML private MFXComboBox<String> cmbUsuPerfil;
    /** ComboBox estado del formulario */
    @FXML private MFXComboBox<String> cmbUsuEstado;
    /** Etiqueta para mensajes de validación o confirmación */
    @FXML private Label lblMensajeUsuario;
    /** Botón eliminar del formulario (se habilita con selección) */
    @FXML private MFXButton btnEliminarUsuarioForm;

    // ─── Estado interno del controlador ─────────────────────────
    /** Usuario actualmente seleccionado en la tabla (null si es nuevo) */
    private Usuario usuarioSeleccionado = null;
    /** Lista observable que alimenta la tabla */
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    /** Lista de perfiles cargados desde BD */
    private List<Perfil> listaPerfiles;

    // ─────────────────────────────────────────────────────────────
    // INITIALIZE
    // ─────────────────────────────────────────────────────────────

    /**
     * Inicializa la vista: enlaza tabla, carga perfiles y lista inicial.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Enlazar la tabla a la lista observable
        tblListaUsuarios.setItems(listaUsuarios);
        // Listener de selección en tabla → carga en formulario
        tblListaUsuarios.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        usuarioSeleccionado = seleccionado;
                        cargarUsuarioEnFormulario(seleccionado);
                    }
                }
        );
        configurarCombosFormulario();
        configurarCombosFiltroPerfil();
        cargarTodosLosUsuarios();
    }

    // ─────────────────────────────────────────────────────────────
    // Configurar ComboBoxes
    // ─────────────────────────────────────────────────────────────

    /**
     * Pobla los combos del formulario con los valores disponibles.
     */
    private void configurarCombosFormulario() {
        try {
            listaPerfiles = UsuarioDAO.listarPerfiles();
            ObservableList<String> nombresPerfiles = FXCollections.observableArrayList();
            for (Perfil p : listaPerfiles) {
                nombresPerfiles.add(p.getPerfdescripcion());
            }
            cmbUsuPerfil.setItems(nombresPerfiles);
        } catch (SQLException e) {
            mostrarMensaje("Error cargando perfiles: " + e.getMessage(), true);
        }
        cmbUsuEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbUsuEstado.setValue("Activo");
    }

    /**
     * Pobla los combos de filtro de la barra superior.
     */
    private void configurarCombosFiltroPerfil() {
        cmbFiltroEstado.setItems(FXCollections.observableArrayList("", "Activo", "Inactivo"));
        ObservableList<String> opcionesPerfil = FXCollections.observableArrayList("");
        if (listaPerfiles != null) {
            for (Perfil p : listaPerfiles) {
                opcionesPerfil.add(p.getPerfdescripcion());
            }
        }
        cmbFiltroPerfil.setItems(opcionesPerfil);
    }

    // ─────────────────────────────────────────────────────────────
    // BUSCAR con filtros
    // ─────────────────────────────────────────────────────────────

    /**
     * Ejecuta la búsqueda aplicando los filtros activos.
     */
    @FXML
    private void buscarUsuario() {
        String texto  = txtBuscarUsuario.getText();
        String perfil = cmbFiltroPerfil.getValue();
        String estado = cmbFiltroEstado.getValue();
        try {
            List<Usuario> resultado = UsuarioDAO.buscarConFiltros(texto, perfil, estado);
            refrescarTabla(resultado);
        } catch (SQLException e) {
            mostrarMensaje("Error en búsqueda: " + e.getMessage(), true);
        }
    }

    /**
     * Limpia todos los filtros y recarga la lista completa.
     */
    @FXML
    private void limpiarFiltros() {
        txtBuscarUsuario.clear();
        cmbFiltroPerfil.setValue(null);
        cmbFiltroEstado.setValue(null);
        cargarTodosLosUsuarios();
        limpiarFormulario();
    }

    /** Carga todos los usuarios sin filtros. */
    private void cargarTodosLosUsuarios() {
        try {
            List<Usuario> todos = UsuarioDAO.listarTodos();
            refrescarTabla(todos);
        } catch (SQLException e) {
            mostrarMensaje("Error cargando usuarios: " + e.getMessage(), true);
        }
    }

    /**
     * Actualiza la lista observable, placeholder y conteo.
     */
    private void refrescarTabla(List<Usuario> usuarios) {
        listaUsuarios.setAll(usuarios);
        boolean hayDatos = !usuarios.isEmpty();
        boxPlaceholder.setVisible(!hayDatos);
        boxPlaceholder.setManaged(!hayDatos);
        lblConteoUsuarios.setText(usuarios.size() + " usuario"
                + (usuarios.size() == 1 ? "" : "s") + " encontrado"
                + (usuarios.size() == 1 ? "" : "s"));
    }

    // ─────────────────────────────────────────────────────────────
    // GUARDAR
    // ─────────────────────────────────────────────────────────────

    /**
     * Guarda el usuario del formulario (INSERT o UPDATE).
     */
    @FXML
    private void guardarUsuario() {
        if (!validarFormulario()) return;
        Usuario u = construirUsuarioDesdeFormulario();
        try {
            if (usuarioSeleccionado == null) {
                boolean ok = UsuarioDAO.insertar(u);
                if (ok) {
                    mostrarMensaje("✓ Usuario creado correctamente.", false);
                    limpiarFormulario();
                    cargarTodosLosUsuarios();
                }
            } else {
                u.setUsuid(usuarioSeleccionado.getUsuid());
                boolean ok = UsuarioDAO.actualizar(u);
                if (ok) {
                    mostrarMensaje("✓ Usuario actualizado correctamente.", false);
                    limpiarFormulario();
                    cargarTodosLosUsuarios();
                }
            }
        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("ukusulogin")) {
                mostrarMensaje("⚠ El login ya existe. Use uno diferente.", true);
            } else if (msg != null && msg.contains("ukusucorreo")) {
                mostrarMensaje("⚠ El correo ya está registrado.", true);
            } else {
                mostrarMensaje("Error al guardar: " + msg, true);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ELIMINAR
    // ─────────────────────────────────────────────────────────────

    /**
     * Elimina el usuario seleccionado.
     */
    @FXML
    private void eliminarUsuario() {
        if (usuarioSeleccionado == null) {
            mostrarMensaje("⚠ Selecciona un usuario para eliminar.", true);
            return;
        }
        try {
            boolean ok = UsuarioDAO.eliminar(usuarioSeleccionado.getUsuid());
            if (ok) {
                mostrarMensaje("✓ Usuario eliminado correctamente.", false);
                limpiarFormulario();
                cargarTodosLosUsuarios();
            }
        } catch (SQLException e) {
            mostrarMensaje("⚠ No se puede eliminar: el usuario tiene registros activos.", true);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // NUEVO
    // ─────────────────────────────────────────────────────────────

    /**
     * Prepara el formulario para un nuevo usuario (modo INSERT).
     */
    @FXML
    private void prepararNuevoUsuario() {
        limpiarFormulario();
    }

    // ─────────────────────────────────────────────────────────────
    // UTILIDADES
    // ─────────────────────────────────────────────────────────────

    /** Construye un objeto Usuario leyendo los campos del formulario. */
    private Usuario construirUsuarioDesdeFormulario() {
        Usuario u = new Usuario();
        u.setUsunombre(txtUsuNombre.getText().trim());
        u.setUsuapellido(txtUsuApellido.getText().trim());
        u.setUsulogin(txtUsuLogin.getText().trim());
        u.setUsucorreo(txtUsuCorreo.getText().trim());
        u.setUsutelefono(txtUsuTelefono.getText().trim());
        u.setUsudireccion(txtUsuDireccion.getText().trim());
        u.setUsupass(txtUsuPass.getPassword().trim());
        u.setUsuestado(cmbUsuEstado.getValue() != null ? cmbUsuEstado.getValue() : "Activo");
        String perfilSelec = cmbUsuPerfil.getValue();
        if (perfilSelec != null && listaPerfiles != null) {
            for (Perfil p : listaPerfiles) {
                if (p.getPerfdescripcion().equals(perfilSelec)) {
                    u.setPerfid(p.getPerfid());
                    u.setPerfilDescripcion(p.getPerfdescripcion());
                    break;
                }
            }
        }
        return u;
    }

    /** Valida que los campos obligatorios no estén vacíos. */
    private boolean validarFormulario() {
        if (txtUsuNombre.getText().isBlank()) {
            mostrarMensaje("⚠ El nombre es obligatorio.", true); return false;
        }
        if (txtUsuApellido.getText().isBlank()) {
            mostrarMensaje("⚠ El apellido es obligatorio.", true); return false;
        }
        if (txtUsuLogin.getText().isBlank()) {
            mostrarMensaje("⚠ El login es obligatorio.", true); return false;
        }
        if (txtUsuCorreo.getText().isBlank()) {
            mostrarMensaje("⚠ El correo es obligatorio.", true); return false;
        }
        if (usuarioSeleccionado == null && txtUsuPass.getPassword().isBlank()) {
            mostrarMensaje("⚠ La contraseña es obligatoria para nuevos usuarios.", true); return false;
        }
        if (cmbUsuEstado.getValue() == null) {
            mostrarMensaje("⚠ Selecciona un estado.", true); return false;
        }
        return true;
    }

    /** Carga los datos del usuario en el formulario para edición. */
    private void cargarUsuarioEnFormulario(Usuario u) {
        txtUsuNombre.setText(u.getUsunombre());
        txtUsuApellido.setText(u.getUsuapellido());
        txtUsuLogin.setText(u.getUsulogin());
        txtUsuCorreo.setText(u.getUsucorreo());
        txtUsuTelefono.setText(u.getUsutelefono());
        txtUsuDireccion.setText(u.getUsudireccion());
        txtUsuPass.clear();
        cmbUsuPerfil.setValue(u.getPerfilDescripcion());
        cmbUsuEstado.setValue(u.getUsuestado());
        btnEliminarUsuarioForm.setDisable(false);
        ocultarMensaje();
    }

    /** Limpia todos los campos y reinicia el modo a INSERT. */
    private void limpiarFormulario() {
        usuarioSeleccionado = null;
        tblListaUsuarios.getSelectionModel().clearSelection();
        txtUsuNombre.clear();
        txtUsuApellido.clear();
        txtUsuLogin.clear();
        txtUsuCorreo.clear();
        txtUsuTelefono.clear();
        txtUsuDireccion.clear();
        txtUsuPass.clear();
        cmbUsuPerfil.setValue(null);
        cmbUsuEstado.setValue("Activo");
        btnEliminarUsuarioForm.setDisable(true);
        ocultarMensaje();
    }

    /** Muestra mensaje de éxito (verde) o error (rojo). */
    private void mostrarMensaje(String texto, boolean esError) {
        lblMensajeUsuario.setText(texto);
        lblMensajeUsuario.setStyle(esError
                ? "-fx-text-fill: #C0392B; -fx-font-weight: bold;"
                : "-fx-text-fill: #27AE60; -fx-font-weight: bold;");
        lblMensajeUsuario.setVisible(true);
        lblMensajeUsuario.setManaged(true);
    }

    /** Oculta la etiqueta de mensajes. */
    private void ocultarMensaje() {
        lblMensajeUsuario.setText("");
        lblMensajeUsuario.setVisible(false);
        lblMensajeUsuario.setManaged(false);
    }
}
