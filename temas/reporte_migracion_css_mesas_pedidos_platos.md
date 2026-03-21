# Reporte final de migracion CSS (Mesas, Pedidos, Platos)

Estado: migracion de `styleClass` aplicada y validada en:

- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaMesas.fxml`
- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaPedidos.fxml`
- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaPlatos.fxml`

## 1) Clases legacy eliminadas por vista

### `VistaMesas.fxml`

Eliminadas:

- `root-container`
- `main-card`
- `titulo-texto`
- `form-scroll`
- `input-label`
- `input-wrapper`
- `input-field`
- `search-field`
- `table-header-row`
- `col-header`
- `mesas-table`
- `empty-state-container`
- `placeholder-text`
- `footer-bar`
- `form-footer`
- `form-mensaje`

### `VistaPedidos.fxml`

Eliminadas:

- `root-container`
- `main-card`
- `titulo-texto`
- `subtitulo-seccion`
- `form-scroll`
- `input-label`
- `input-wrapper`
- `input-field`
- `table-header-row`
- `col-header`
- `mesas-table`
- `placeholder-subtext`
- `footer-bar`
- `form-footer`
- `form-mensaje`

### `VistaPlatos.fxml`

Eliminadas:

- `root-container`
- `main-card`
- `view-title`
- `form-scroll`
- `input-label`
- `input-wrapper`
- `search-field`
- `transparent-field`
- `table-header-row`
- `table-header-label`
- `mesas-table`
- `empty-state-container`
- `placeholder-text`
- `placeholder-subtext`
- `footer-bar`
- `form-mensaje`

## 2) Clases nuevas activas por vista

### `VistaMesas.fxml`

- `layout-root`
- `panel-card`
- `panel-card__title`
- `form__body`
- `form__label`
- `form__control-wrap`
- `form__actions`
- `form__status`
- `input`
- `input--search`
- `table`
- `table--data`
- `table__head`
- `table__col-title`
- `state-empty__container`
- `state-empty__title`
- `layout-footer`
- `btn`
- `btn--primary`
- `btn--outline-primary`
- `btn--outline-secondary`
- `btn--outline-danger`
- `btn--search`
- `btn--ghost`

### `VistaPedidos.fxml`

- `layout-root`
- `panel-card`
- `panel-card__title`
- `panel-card__subtitle`
- `form__body`
- `form__label`
- `form__control-wrap`
- `form__actions`
- `form__status`
- `input`
- `table`
- `table--data`
- `state-empty__subtitle`
- `layout-footer`
- `btn`
- `btn--primary`
- `btn--outline-primary`
- `btn--outline-secondary`
- `btn--outline-danger`
- `btn--search`
- `btn--ghost`

### `VistaPlatos.fxml`

- `layout-root`
- `panel-card`
- `panel-card__title`
- `form__body`
- `form__label`
- `form__control-wrap`
- `form__status`
- `input`
- `input--search`
- `table`
- `table--data`
- `table__head`
- `table__col-title`
- `state-empty__container`
- `state-empty__title`
- `state-empty__subtitle`
- `layout-footer`
- `btn`
- `btn--primary`
- `btn--outline-primary`
- `btn--outline-danger`

## 3) Checklist para pasar a `replace` total en todo el proyecto

### Pre-condiciones tecnicas

- [ ] Confirmar que `styles.css`, `theme-overrides.css`, `vista-mesas.css`, `vista-platos.css` tienen reglas para todas las clases nuevas base (`layout-*`, `panel-*`, `form__*`, `table-*`, `btn--*`, `state-empty*`).
- [ ] Verificar que no haya `styleClass` con separador por espacio en FXML (usar solo comas en este proyecto).
- [ ] Mantener temporalmente alias CSS (viejo + nuevo) hasta completar todas las vistas.

### Validacion funcional por modulo

- [ ] Mesas: filtros, tabla, placeholder, formulario, acciones CRUD.
- [ ] Pedidos: tabla principal, detalle, comandas, flujo de estado.
- [ ] Platos: filtros, tabla, formulario completo y botones.
- [ ] Configuracion y login: confirmar que no hubo regresiones visuales globales.

### Barrido global antes de eliminar clases viejas

- [ ] Ejecutar grep global de legacy en `src/main/resources/.../view/*.fxml`.
- [ ] Asegurar que las clases legacy objetivo tengan **0 referencias**.
- [ ] Solo entonces remover alias viejos de CSS en un commit separado.

### Orden recomendado para el `replace` total

1. `VistaUsuarios.fxml`
2. `VistaClientes.fxml`
3. `VistaAreasMesa.fxml`
4. `VistaInsumos.fxml`
5. `VistaCategoriasPlato.fxml`
6. `VistaCategoriaInsumo.fxml`
7. `VistaConceptoEgreso.fxml`
8. `VistaEgresos.fxml`
9. `VistaFormaPago.fxml`
10. `VistaConfiguracion.fxml`

### Cierre

- [ ] Crear commit de migracion por bloques (vista + css puente).
- [ ] Crear commit final de limpieza (eliminar aliases legacy CSS).
- [ ] Actualizar documentacion de convencion CSS para el equipo.
