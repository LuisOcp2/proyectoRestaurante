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
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador JavaFX para la vista VistaUsuarios.fxml.
 * Gestiona el CRUD completo de usuarios del sistema.
 *
 * Flujo principal:
 *   1. initialize() → carga perfiles y lista inicial de usuarios
 *   2. buscarUsuario() → aplica filtros y recarga la tabla
 *   3. limpiarFiltros() → resetea combos y recarga todos
 *   4. seleccionarUsuario() → rellena el formulario al hacer click en tabla
 *   5. guardarUsuario() → INSERT o UPDATE según modo actual
 *   6. eliminarUsuario() → DELETE con confirmación
 *   7. prepararNuevoUsuario() → limpia formulario para nuevo registro
 */
public class UsuarioController implements Initializable {

    // ─── Barra de filtros ────────────────────────────────────────
    /** Campo de búsqueda por nombre, apellido o login */
    @FXML private MFXTextField txtBuscarUsuario;
    /** ComboBox nativo para filtrar por perfil/rol */
    @FXML private ComboBox<String> cmbFiltroPerfil;
    /** ComboBox nativo para filtrar por estado */
    @FXML private ComboBox<String> cmbFiltroEstado;

    // ─── Tabla / estado vacío ────────────────────────────────────
    /** Etiqueta que muestra el total de usuarios encontrados */
    @FXML private Label lblConteoUsuarios;
    /** Contenedor del estado vacío (sin resultados) */
    @FXML private VBox boxPlaceholder;

    // ─── Botones de acción (footer tabla) ────────────────────────
    /** Botón editar (se habilita al seleccionar fila) */
    @FXML private MFXButton btnEditarUsuario;
    /** Botón eliminar footer tabla (se habilita al seleccionar fila) */
    @FXML private MFXButton btnEliminarUsuario;

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
    // INITIALIZE: se ejecuta al cargar el FXML
    // ─────────────────────────────────────────────────────────────

    /**
     * Inicializa la vista: carga perfiles, combos de filtro
     * y la lista completa de usuarios al arrancar.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarCombosFormulario();
        configurarCombosFiltroPerfil();
        cargarTodosLosUsuarios();
    }

    // ─────────────────────────────────────────────────────────────
    // Configurar ComboBox del formulario
    // ─────────────────────────────────────────────────────────────

    /**
     * Pobla los combos del formulario con los valores disponibles.
     * Carga perfiles desde BD y define los estados fijos.
     */
    private void configurarCombosFormulario() {
        // Cargar perfiles desde BD para el combo del formulario
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

        // Estados fijos de la BD: Activo o Inactivo
        cmbUsuEstado.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        cmbUsuEstado.setValue("Activo"); // valor por defecto
    }

    /**
     * Pobla el ComboBox de filtro de perfil (nativo JavaFX).
     * Agrega opción vacía para mostrar todos.
     */
    private void configurarCombosFiltroPerfil() {
        // Filtro de estado fijo
        cmbFiltroEstado.setItems(FXCollections.observableArrayList("", "Activo", "Inactivo"));

        // Filtro de perfil: depende de los perfiles ya cargados
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
     * Llamado por el botón "Buscar" del FXML.
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
     * Llamado por el botón "Limpiar" del FXML.
     */
    @FXML
    private void limpiarFiltros() {
        txtBuscarUsuario.clear();
        cmbFiltroPerfil.setValue(null);
        cmbFiltroEstado.setValue(null);
        cargarTodosLosUsuarios();
        limpiarFormulario();
    }

    /**
     * Carga todos los usuarios sin filtros al inicio o al limpiar.
     */
    private void cargarTodosLosUsuarios() {
        try {
            List<Usuario> todos = UsuarioDAO.listarTodos();
            refrescarTabla(todos);
        } catch (SQLException e) {
            mostrarMensaje("Error cargando usuarios: " + e.getMessage(), true);
        }
    }

    /**
     * Actualiza la lista observable y el estado del placeholder.
     * Muestra el estado vacío si no hay resultados.
     *
     * @param usuarios Lista de usuarios a mostrar
     */
    private void refrescarTabla(List<Usuario> usuarios) {
        listaUsuarios.setAll(usuarios);
        boolean hayDatos = !usuarios.isEmpty();

        // Mostrar/ocultar placeholder de "sin datos"
        boxPlaceholder.setVisible(!hayDatos);
        boxPlaceholder.setManaged(!hayDatos);

        // Actualizar conteo en el footer
        lblConteoUsuarios.setText(usuarios.size() + " usuario" + (usuarios.size() == 1 ? "" : "s") + " encontrado" + (usuarios.size() == 1 ? "" : "s"));
    }

    // ─────────────────────────────────────────────────────────────
    // GUARDAR: INSERT o UPDATE según modo
    // ─────────────────────────────────────────────────────────────

    /**
     * Guarda el usuario del formulario.
     * Si {@code usuarioSeleccionado} es null → INSERT (nuevo).
     * Si {@code usuarioSeleccionado} tiene ID → UPDATE (edición).
     * Llamado por los botones "Guardar Usuario" del FXML.
     */
    @FXML
    private void guardarUsuario() {
        // Validar campos obligatorios antes de intentar guardar
        if (!validarFormulario()) return;

        // Construir el objeto Usuario desde los campos del formulario
        Usuario u = construirUsuarioDesdeFormulario();

        try {
            if (usuarioSeleccionado == null) {
                // Modo INSERT: nuevo usuario
                boolean ok = UsuarioDAO.insertar(u);
                if (ok) {
                    mostrarMensaje("✓ Usuario creado correctamente.", false);
                    limpiarFormulario();
                    cargarTodosLosUsuarios();
                }
            } else {
                // Modo UPDATE: editar usuario existente
                u.setUsuid(usuarioSeleccionado.getUsuid());
                boolean ok = UsuarioDAO.actualizar(u);
                if (ok) {
                    mostrarMensaje("✓ Usuario actualizado correctamente.", false);
                    limpiarFormulario();
                    cargarTodosLosUsuarios();
                }
            }
        } catch (SQLException e) {
            // Detectar errores comunes de BD (duplicados)
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
    // EDITAR: cargar datos del usuario seleccionado en formulario
    // ─────────────────────────────────────────────────────────────

    /**
     * Carga los datos del usuario seleccionado en el formulario.
     * Llamado por el botón "Editar" del FXML.
     * En implementación real se obtiene el usuario de la fila seleccionada.
     */
    @FXML
    private void editarUsuario() {
        if (usuarioSeleccionado == null) {
            mostrarMensaje("⚠ Selecciona un usuario de la tabla primero.", true);
            return;
        }
        cargarUsuarioEnFormulario(usuarioSeleccionado);
    }

    /**
     * Rellena todos los campos del formulario con los datos del usuario.
     * La contraseña NO se precarga por seguridad.
     *
     * @param u Usuario cuyos datos se cargarán en el formulario
     */
    private void cargarUsuarioEnFormulario(Usuario u) {
        txtUsuNombre.setText(u.getUsunombre());
        txtUsuApellido.setText(u.getUsuapellido());
        txtUsuLogin.setText(u.getUsulogin());
        txtUsuCorreo.setText(u.getUsucorreo());
        txtUsuTelefono.setText(u.getUsutelefono());
        txtUsuDireccion.setText(u.getUsudireccion());
        txtUsuPass.clear(); // no se precarga la contraseña por seguridad
        cmbUsuPerfil.setValue(u.getPerfilDescripcion());
        cmbUsuEstado.setValue(u.getUsuestado());

        // Habilitar botones de eliminar
        btnEliminarUsuario.setDisable(false);
        btnEliminarUsuarioForm.setDisable(false);
        ocultarMensaje();
    }

    // ─────────────────────────────────────────────────────────────
    // ELIMINAR usuario
    // ─────────────────────────────────────────────────────────────

    /**
     * Elimina el usuario seleccionado.
     * Llamado por los botones "Eliminar" del FXML.
     * Precaución: verifica FK antes de eliminar desde el DAO.
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
            // Error FK: el usuario tiene registros relacionados
            mostrarMensaje("⚠ No se puede eliminar: el usuario tiene registros activos.", true);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // NUEVO: preparar formulario vacío
    // ─────────────────────────────────────────────────────────────

    /**
     * Prepara el formulario para registrar un nuevo usuario.
     * Limpia todos los campos y reinicia el modo a INSERT.
     * Llamado por los botones "Nuevo Usuario" del FXML.
     */
    @FXML
    private void prepararNuevoUsuario() {
        limpiarFormulario();
    }

    // ─────────────────────────────────────────────────────────────
    // UTILIDADES internas del controlador
    // ─────────────────────────────────────────────────────────────

    /**
     * Construye un objeto {@link Usuario} leyendo los valores del formulario.
     * La contraseña se almacena tal cual (debe hashearse antes de llamar esto).
     *
     * @return Objeto {@link Usuario} con los datos del formulario
     */
    private Usuario construirUsuarioDesdeFormulario() {
        Usuario u = new Usuario();
        u.setUsunombre(txtUsuNombre.getText().trim());
        u.setUsuapellido(txtUsuApellido.getText().trim());
        u.setUsulogin(txtUsuLogin.getText().trim());
        u.setUsucorreo(txtUsuCorreo.getText().trim());
        u.setUsutelefono(txtUsuTelefono.getText().trim());
        u.setUsudireccion(txtUsuDireccion.getText().trim());
        u.setUsupass(txtUsuPass.getPassword().trim()); // se pasa el texto plano (hash en capa real)
        u.setUsuestado(cmbUsuEstado.getValue() != null ? cmbUsuEstado.getValue() : "Activo");

        // Buscar el perfid correspondiente al nombre de perfil seleccionado
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

    /**
     * Valida que los campos obligatorios del formulario no estén vacíos.
     *
     * @return true si el formulario es válido, false si hay errores
     */
    private boolean validarFormulario() {
        if (txtUsuNombre.getText().isBlank()) {
            mostrarMensaje("⚠ El nombre es obligatorio.", true);
            return false;
        }
        if (txtUsuApellido.getText().isBlank()) {
            mostrarMensaje("⚠ El apellido es obligatorio.", true);
            return false;
        }
        if (txtUsuLogin.getText().isBlank()) {
            mostrarMensaje("⚠ El login es obligatorio.", true);
            return false;
        }
        if (txtUsuCorreo.getText().isBlank()) {
            mostrarMensaje("⚠ El correo es obligatorio.", true);
            return false;
        }
        // La contraseña solo es obligatoria si es un usuario nuevo
        if (usuarioSeleccionado == null && txtUsuPass.getPassword().isBlank()) {
            mostrarMensaje("⚠ La contraseña es obligatoria para nuevos usuarios.", true);
            return false;
        }
        if (cmbUsuEstado.getValue() == null) {
            mostrarMensaje("⚠ Selecciona un estado.", true);
            return false;
        }
        return true;
    }

    /**
     * Limpia todos los campos del formulario y resetea el modo a INSERT.
     * También deshabilita los botones de edición/eliminación.
     */
    private void limpiarFormulario() {
        usuarioSeleccionado = null;
        txtUsuNombre.clear();
        txtUsuApellido.clear();
        txtUsuLogin.clear();
        txtUsuCorreo.clear();
        txtUsuTelefono.clear();
        txtUsuDireccion.clear();
        txtUsuPass.clear();
        cmbUsuPerfil.setValue(null);
        cmbUsuEstado.setValue("Activo");
        btnEditarUsuario.setDisable(true);
        btnEliminarUsuario.setDisable(true);
        btnEliminarUsuarioForm.setDisable(true);
        ocultarMensaje();
    }

    /**
     * Muestra un mensaje informativo o de error al usuario.
     *
     * @param texto  Texto del mensaje a mostrar
     * @param esError true si es error (rojo), false si es éxito (verde)
     */
    private void mostrarMensaje(String texto, boolean esError) {
        lblMensajeUsuario.setText(texto);
        lblMensajeUsuario.setStyle(esError
                ? "-fx-text-fill: #C0392B; -fx-font-weight: bold;"
                : "-fx-text-fill: #27AE60; -fx-font-weight: bold;");
        lblMensajeUsuario.setVisible(true);
        lblMensajeUsuario.setManaged(true);
    }

    /**
     * Oculta la etiqueta de mensajes del formulario.
     */
    private void ocultarMensaje() {
        lblMensajeUsuario.setText("");
        lblMensajeUsuario.setVisible(false);
        lblMensajeUsuario.setManaged(false);
    }
}
