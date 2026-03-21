# Guia de migracion automatica (VistaMesas + VistaPedidos)

Esta guia usa un script de migracion controlada para renombrar/agregar `styleClass` sin romper estilos actuales.

## 1) Script disponible

- `temas/migrar_styleclass_mesas_pedidos.py`

El script trabaja solo sobre:

- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaMesas.fxml`
- `src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaPedidos.fxml`

## 2) Modos de migracion

### `alias` (recomendado)

- Conserva clases actuales.
- Agrega clases nuevas estandarizadas.
- Riesgo minimo, ideal para transicion gradual.

### `replace`

- Reemplaza clases actuales por clases nuevas.
- Mas agresivo, usar cuando ya existan reglas CSS completas con nombres nuevos.

## 3) Flujo recomendado

### Paso A. Vista previa (sin tocar archivos)

```bash
python3 temas/migrar_styleclass_mesas_pedidos.py --dry-run --mode alias
```

### Paso B. Aplicar cambios

```bash
python3 temas/migrar_styleclass_mesas_pedidos.py --apply --mode alias
```

### Paso C. Verificar cambios git

```bash
git diff -- src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaMesas.fxml src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaPedidos.fxml
```

## 4) Reglas CSS puente (importante)

Mientras migras, define alias en CSS para que nuevas clases hereden estilos viejos.

Ejemplo en `src/main/resources/com/mosqueteros/proyecto_restaurante/styles/vista-mesas.css`:

```css
.root-container,
.layout-root {
    -fx-background-color: -color-bg-app;
}

.main-card,
.panel-card {
    -fx-background-color: -color-surface;
}

.mesas-table,
.table.table--data {
    -fx-background-color: transparent;
}
```

## 5) Control de calidad

Checklist rapido despues de aplicar:

- Carga `VistaMesas.fxml` y valida: filtros, tabla, formulario, botones.
- Carga `VistaPedidos.fxml` y valida: tabla pedidos, tabla detalle, bloque comandas.
- Verifica focus y hover en inputs/combo/buttons.
- Revisa que placeholders y footer mantengan contraste correcto por tema.

## 6) Rollback rapido

Si algo no te gusta, revierte solo las 2 vistas:

```bash
git checkout -- src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaMesas.fxml src/main/resources/com/mosqueteros/proyecto_restaurante/view/VistaPedidos.fxml
```

## 7) Siguiente etapa sugerida

Cuando valides estas dos vistas, repetir el mismo enfoque en:

1. `VistaPlatos.fxml`
2. `VistaUsuarios.fxml`
3. `VistaClientes.fxml`
