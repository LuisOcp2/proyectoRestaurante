# Informe final consolidado de migracion CSS en vistas

Estado del proceso: **completado para vistas principales `Vista*.fxml`**, con deuda legacy objetivo en **0**.

## 1) Vistas migradas por bloque

### Bloque 1

- `VistaMesas.fxml`
- `VistaPedidos.fxml`

Accion aplicada:

- migracion inicial en modo alias,
- ajuste de separadores de `styleClass` (comas),
- limpieza controlada posterior.

### Bloque 2

- `VistaPlatos.fxml`

Accion aplicada:

- alias + limpieza,
- puente CSS en `vista-platos.css` para clases nuevas.

### Bloque 3 (top deuda)

- `VistaUsuarios.fxml`
- `VistaEgresos.fxml`
- `VistaConfiguracion.fxml`

Accion aplicada:

- reemplazo controlado de clases legacy,
- compatibilidad garantizada en `styles.css`.

### Bloque 4

- `VistaInsumos.fxml`
- `VistaAreasMesa.fxml`
- `VistaCategoriasPlato.fxml`

Accion aplicada:

- migracion legacy -> estandar,
- validacion de remanentes.

### Bloque 5

- `VistaConceptoEgreso.fxml`
- `VistaFormaPago.fxml`
- `VistaClientes.fxml`

Accion aplicada:

- migracion final de vistas pendientes,
- normalizacion de `styleClass`.

### Cierre final

- `VistaCategoriaInsumo.fxml`
- refuerzo final en:
  - `VistaUsuarios.fxml`
  - `VistaEgresos.fxml`
  - `VistaConfiguracion.fxml`

Resultado de validacion global:

- `Vista*.fxml` con deuda legacy objetivo: **0 archivos**.

## 2) Clases nuevas estandar adoptadas

Conjunto detectado en vistas principales:

- `layout-root`
- `layout-footer`
- `panel-card`
- `panel-card__title`
- `panel-card__subtitle`
- `form__body`
- `form__label`
- `form__control-wrap`
- `form__actions`
- `form__status`
- `table`
- `table--data`
- `table__head`
- `table__col-title`
- `state-empty`
- `state-empty__container`
- `state-empty__title`
- `state-empty__subtitle`
- `input`
- `input--search`
- `btn`
- `btn--primary`
- `btn--outline-primary`
- `btn--outline-secondary`
- `btn--outline-danger`
- `btn--search`
- `btn--ghost`

## 3) Sugerencia de commits limpia

Propuesta para dejar historial claro y facil de revertir por bloque:

### Commit 1 - Infraestructura CSS puente

Archivos:

- `src/main/resources/com/mosqueteros/proyecto_restaurante/styles/styles.css`
- `src/main/resources/com/mosqueteros/proyecto_restaurante/styles/vista-mesas.css`
- `src/main/resources/com/mosqueteros/proyecto_restaurante/styles/vista-platos.css`

Mensaje sugerido:

`feat(css): add standardized class bridge for layout/form/table/button system`

### Commit 2 - Migracion vistas operativas

Archivos:

- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaMesas.fxml`
- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaPedidos.fxml`
- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaPlatos.fxml`

Mensaje sugerido:

`refactor(fxml): migrate Mesas Pedidos and Platos to standardized style classes`

### Commit 3 - Migracion vistas administrativas (lote 1)

Archivos:

- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaUsuarios.fxml`
- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaEgresos.fxml`
- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaConfiguracion.fxml`

Mensaje sugerido:

`refactor(fxml): migrate Usuarios Egresos and Configuracion styleClass naming`

### Commit 4 - Migracion vistas administrativas (lote 2)

Archivos:

- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaInsumos.fxml`
- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaAreasMesa.fxml`
- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaCategoriasPlato.fxml`
- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaConceptoEgreso.fxml`
- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaFormaPago.fxml`
- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaClientes.fxml`
- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaCategoriaInsumo.fxml`

Mensaje sugerido:

`refactor(fxml): complete legacy styleClass migration across remaining admin views`

### Commit 5 - Documentacion de migracion

Archivos:

- `temas/uso_estilos_css_vistas.md`
- `temas/mapa_styleclass_vistas.md`
- `temas/propuesta_estandar_nombres_css.md`
- `temas/guia_migracion_automatica_mesas_pedidos.md`
- `temas/reporte_migracion_css_mesas_pedidos_platos.md`
- `temas/informe_final_migracion_css_vistas.md`
- `temas/migrar_styleclass_mesas_pedidos.py`

Mensaje sugerido:

`docs(css): document style usage class map and migration workflow`

## 4) Nota de higiene antes de commit

Detectados archivos no relacionados en `temas/`:

- `temas/MGC_9.7.047_V2_MGC_gcamapk.io.apk`
- `temas/PLAN DE CURSO ACTUALIZADO.pdf`

Recomendacion:

- no incluirlos en commits de CSS/migracion UI.
