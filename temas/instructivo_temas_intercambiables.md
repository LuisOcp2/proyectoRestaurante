# Instructivo: Temas intercambiables para modulos y formularios

Este instructivo explica, paso a paso, como:

1. crear un tema nuevo,
2. registrarlo en el sistema,
3. y hacer que cualquier modulo/formulario nuevo lo use automaticamente.

## 1) Como funciona hoy el sistema de temas

El proyecto ya tiene una arquitectura centralizada de temas:

- `ThemeManager` aplica clases CSS de tema al `root` de la escena.
- `ConfiguracionUtil` lee el tema activo desde la configuracion `ui_theme`.
- `theme-overrides.css` define los tokens y estilos por tema.
- La vista de configuracion permite cambiar tema en vivo y guardarlo en BD.

Archivos clave:

- `src/main/java/com/mosqueteros/proyecto_restaurante/util/ThemeManager.java`
- `src/main/java/com/mosqueteros/proyecto_restaurante/util/ConfiguracionUtil.java`
- `src/main/java/com/mosqueteros/proyecto_restaurante/controller/ConfiguracionController.java`
- `src/main/resources/com/mosqueteros/proyecto_restaurante/styles/theme-overrides.css`

## 2) Crear un tema nuevo (ejemplo: `sunsetbistro`)

### Paso 1. Agregar el nombre del tema en `ThemeManager`

En `TEMAS_SOPORTADOS`, agrega el identificador nuevo:

```java
private static final Set<String> TEMAS_SOPORTADOS = Set.of(
    "artisan",
    "culinarylogic",
    "goldenbeach",
    "sweetsundays",
    "vintagereserve",
    "violet",
    "sunsetbistro"
);
```

Tambien agrega la clase a remover en `aplicarTemaEnRoot`:

```java
root.getStyleClass().removeAll(
    "theme-artisan",
    "theme-culinarylogic",
    "theme-goldenbeach",
    "theme-sweetsundays",
    "theme-vintagereserve",
    "theme-violet",
    "theme-sunsetbistro",
    "contrast-normal",
    "contrast-high"
);
```

### Paso 2. Definir tokens CSS del tema en `theme-overrides.css`

1. Incluye el selector nuevo en el bloque base:

```css
.theme-violet,
.theme-artisan,
.theme-culinarylogic,
.theme-goldenbeach,
.theme-sweetsundays,
.theme-vintagereserve,
.theme-sunsetbistro {
    -fx-font-family: "Manrope", "Segoe UI", sans-serif;
    /* tokens compartidos */
}
```

2. Crea el bloque especifico del tema:

```css
.theme-sunsetbistro {
    -color-bg-app: #fff9f3;
    -color-surface: #ffffff;
    -color-surface-alt: #ffeede;
    -color-surface-soft: #fff4ea;
    -color-border: #e9d7c8;
    -color-text: #2a1e17;
    -color-text-muted: #7a6659;
    -color-primary: #b04a1f;
    -color-primary-strong: #8d3a18;
    -color-accent: #ffd27a;
    -color-accent-strong: #e8bb64;
    -color-accent-ink: #3a2500;
    -color-danger: #a72b1f;
    -contrast-focus-border: #b04a1f;
    -contrast-focus-width: 1.8px;
    -contrast-focus-shadow: rgba(176, 74, 31, 0.24);
    -contrast-table-hover: rgba(176, 74, 31, 0.08);
    -contrast-table-selected: rgba(176, 74, 31, 0.16);
    -contrast-table-selected-text: #4d210f;
    -topbar-start: #c25b2d;
    -topbar-end: #8d3a18;
}
```

Nota: usa los mismos nombres de tokens existentes (`-color-primary`, `-color-text`, etc.). Asi no tienes que reescribir todos los componentes.

### Paso 3. Registrar el tema en la pantalla de configuracion

En `ConfiguracionController` agrega el valor nuevo en:

- `opcionesTemaUI`
- `temaNombre`
- `temaDescripcion`

Opcional (si quieres preview completo):

- agregar swatch nuevo en `VistaConfiguracion.fxml`,
- agregar clases de preview/swatch en `theme-overrides.css`,
- y mapear el swatch en `actualizarSwatchActivo`.

### Paso 4. (Opcional) Documentar el tema en `/temas`

Como buena practica de equipo, crea un archivo de guia visual:

- `temas/sunsetbistro.md`

Recomendado incluir:

- objetivo del tema,
- paleta,
- tipografia,
- reglas de botones, inputs, tablas,
- do/dont para mantener consistencia.

## 3) Como hacer que un modulo nuevo use temas intercambiables

### Escenario A: modulo dentro del `main` (recomendado)

Si tu modulo se carga dentro de `MainController` (contenido principal), normalmente ya hereda el tema activo. Solo debes:

1. usar clases CSS semanticas ya estandarizadas,
2. evitar colores hardcodeados en FXML/CSS,
3. usar tokens definidos en `theme-overrides.css`.

Ejemplo en FXML:

```xml
<StackPane styleClass="root-container" xmlns:fx="http://javafx.com/fxml/1">
    <VBox styleClass="main-card">
        <Label text="MODULO X" styleClass="input-label"/>
        <Label text="Gestion de datos" styleClass="view-title"/>
    </VBox>
</StackPane>
```

### Escenario B: modulo abre una ventana (`Stage`) nueva

Si creas una escena nueva, SI debes aplicar tema global explicitamente:

```java
FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mosqueteros/proyecto_restaurante/view/mi_modulo.fxml"));
Parent root = loader.load();
Scene scene = new Scene(root);
ThemeManager.aplicarTemaGlobal(scene);
stage.setScene(scene);
stage.show();
```

Sin esto, la ventana puede verse sin tokens del tema activo.

## 4) Checklist para formularios nuevos

Cada formulario nuevo debe cumplir esto para ser theme-ready:

- Contenedor raiz con `styleClass="root-container"`.
- Tarjetas con `main-card` o clase equivalente ya tematizada.
- Titulos y labels con clases reutilizadas (`view-title`, `input-label`, etc.).
- Inputs/botones/tablas usando clases existentes (`input-field`, `btn-raised-primary`, `custom-table`, etc.).
- No usar valores fijos de color como `-fx-background-color: #...` dentro del FXML.
- Si requiere CSS propio del modulo, referenciar tokens (`-color-primary`, `-color-text`, etc.) en lugar de hex fijos.

## 5) Patrones recomendados para CSS de modulo nuevo

Archivo ejemplo: `src/main/resources/com/mosqueteros/proyecto_restaurante/styles/mi-modulo.css`

```css
.mi-modulo-panel {
    -fx-background-color: -color-surface;
    -fx-border-color: -color-border;
    -fx-background-radius: 12px;
    -fx-border-radius: 12px;
}

.mi-modulo-titulo {
    -fx-text-fill: -color-text;
}

.mi-modulo-etiqueta {
    -fx-text-fill: -color-primary;
}
```

Con este enfoque, tu modulo responde automaticamente al cambio de tema guardado en `ui_theme`.

## 6) Flujo de persistencia del tema

El tema activo se guarda y se restaura asi:

1. Usuario cambia tema en Configuracion.
2. `ConfiguracionController` guarda en `configuracion.cfg_clave = ui_theme`.
3. `ConfiguracionUtil.temaUI()` lee el valor.
4. `ThemeManager` normaliza y aplica clase `theme-<tema>` al root.
5. Todos los formularios que usan tokens/estilos compartidos se actualizan visualmente.

## 7) Errores comunes y como evitarlos

- Error: agregar tema en CSS pero no en `ThemeManager`.
  - Resultado: el sistema lo normaliza al tema por defecto.

- Error: modulo nuevo con colores fijos.
  - Resultado: no cambia al intercambiar tema.

- Error: abrir `Stage` nuevo sin `ThemeManager.aplicarTemaGlobal(scene)`.
  - Resultado: ventana fuera del sistema de temas.

- Error: inventar nombres de token nuevos por modulo.
  - Resultado: inconsistencia entre modulos.

## 8) Convencion recomendada de nombres

- ID tecnico de tema: minuscula, sin espacios, ejemplo `sunsetbistro`.
- Clase CSS de tema: `theme-sunsetbistro`.
- Preview CSS: `theme-preview-sunsetbistro`.
- Documentacion de tema: `temas/sunsetbistro.md`.

Con estas reglas, cualquier modulo nuevo quedara integrado al sistema de temas intercambiables con minimo esfuerzo y sin duplicar estilos.
