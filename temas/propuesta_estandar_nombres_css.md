# Propuesta de estandarizacion de nombres CSS (JavaFX)

Objetivo: unificar nombres de `styleClass` para que escalen mejor, reduzcan duplicidad y faciliten mantenimiento.

## 1. Problemas actuales detectados

- Mezcla de idiomas y semanticas (`btn-raised-primary`, `titulo-texto`, `welcome-title`).
- Mezcla de naming por componente y por contexto (`search-field` vs `input-field`).
- Uso de nombres heredados de modulo (`mesas-table`) en vistas de otros modulos.
- Duplicidad de patrones visuales con nombres distintos (ej. contenedores, encabezados, footers).

## 2. Convencion propuesta (BEM simplificado + prefijos por dominio)

Formato recomendado:

- `bloque`
- `bloque__elemento`
- `bloque--modificador`

Prefijos sugeridos:

- `layout-*` para estructura general
- `panel-*` para cards/paneles
- `table-*` para tablas
- `form-*` para formularios
- `btn-*` para botones
- `input-*` para campos
- `theme-*` para variaciones visuales/tema
- `state-*` para estados visuales (`state-error`, `state-empty`, etc.)

Ejemplos:

- `panel-card`
- `panel-card__header`
- `panel-card__title`
- `btn btn--primary` (en JavaFX puedes separar por coma/espacio en `styleClass`)
- `input input--search`

## 3. Diccionario de migracion sugerido (actual -> propuesto)

### Base de layout

- `root-container` -> `layout-root`
- `main-card` -> `panel-card`
- `filter-bar` -> `layout-toolbar`
- `table-footer` -> `layout-footer`
- `footer-bar` -> `layout-footer`

### Titulos y textos

- `titulo-texto` -> `panel-card__title`
- `welcome-title` -> `hero__title`
- `welcome-subtitle` -> `hero__subtitle`
- `welcome-hint` -> `hero__hint`
- `clientes-dev-title` -> `hero__kicker`

### Inputs y formularios

- `input-label` -> `form__label`
- `input-wrapper` -> `form__control-wrap`
- `input-field` -> `input`
- `search-field` -> `input input--search`
- `floating-label` -> `form__label--floating`
- `field-error` -> `form__error`
- `field-icon-left` -> `form__icon form__icon--left`
- `form-scroll` -> `form__body`
- `form-footer` -> `form__actions`
- `form-mensaje` -> `form__status`

### Botones

- `btn-raised-primary` -> `btn btn--primary`
- `btn-outline-primary` -> `btn btn--outline-primary`
- `btn-outline-secondary` -> `btn btn--outline-secondary`
- `btn-outline-danger` -> `btn btn--outline-danger`
- `btn-buscar-raised` -> `btn btn--primary btn--search`
- `btn-limpiar-flat` -> `btn btn--ghost`

### Tabla

- `mesas-table` -> `table table--data`
- `table-header-row` -> `table__head`
- `col-header` -> `table__col-title`
- `empty-state` -> `state-empty`
- `empty-state-container` -> `state-empty__container`
- `placeholder-text` -> `state-empty__title`
- `placeholder-subtext` -> `state-empty__subtitle`

### Shell principal

- `shell-root` -> `layout-shell`
- `topbar` -> `shell-topbar`
- `sidebar` -> `shell-sidebar`
- `content-area` -> `shell-content`
- `menu-btn` -> `shell-menu__btn`
- `menu-btn-active` -> `shell-menu__btn--active`

## 4. Reglas para nuevos modulos

1. Evitar nombres atados al modulo (ej. `mesas-table`) si el estilo es reutilizable.
2. Usar variantes (`--modificador`) en lugar de crear clases casi duplicadas.
3. Definir un set base comun:
   - `layout-root`, `panel-card`, `form__*`, `table__*`, `btn--*`.
4. Mantener estilos de tema separados en `theme-overrides.css` con tokens (`-color-*`).

## 5. Plan de migracion recomendado (sin romper vistas actuales)

### Fase 1 (segura)

- Agregar nuevas clases estandar **sin eliminar las actuales**.
- En CSS, mapear ambas temporalmente:

```css
.main-card,
.panel-card {
    /* estilos compartidos */
}
```

### Fase 2 (incremental por modulo)

- Actualizar FXML modulo por modulo para usar clases nuevas.
- Priorizar vistas de mayor reutilizacion:
  1. `VistaMesas.fxml`
  2. `VistaPedidos.fxml`
  3. `VistaPlatos.fxml`
  4. vistas CRUD que usan `styles.css`

### Fase 3 (limpieza)

- Eliminar aliases antiguos del CSS cuando ya no haya referencias en FXML.
- Verificar con un grep global de `styleClass`.

## 6. Beneficios esperados

- Menos deuda tecnica visual.
- Curva de aprendizaje mas corta para nuevos integrantes.
- Cambios de UI mas rapidos y predecibles.
- Mejor compatibilidad con temas y futuros redisenos.
