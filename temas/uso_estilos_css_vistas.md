# Guia de uso de estilos CSS por vistas (JavaFX)

Este documento resume como se estan aplicando los estilos `.css` en el proyecto, vista por vista, para que puedas:

- entender de donde viene el estilo visual actual,
- crear estilos personalizados sin romper lo existente,
- y saber donde modificar segun el modulo.

## 1. Arquitectura actual de estilos

En este proyecto hay dos formas de aplicar CSS:

1. **CSS global por `Scene`**
   - Se agrega en:
     - `src/main/java/com/mosqueteros/proyecto_restaurante/App.java`
     - `src/main/java/com/mosqueteros/proyecto_restaurante/util/ThemeManager.java`
     - `src/main/java/com/mosqueteros/proyecto_restaurante/controller/LoginController.java`
     - `src/main/java/com/mosqueteros/proyecto_restaurante/controller/MainController.java`
   - `ThemeManager.aplicarTemaGlobal(scene)` garantiza que existan:
     - `styles.css`
     - `theme-overrides.css`

2. **CSS por vista en FXML (`stylesheets="..."` o `<stylesheets><URL .../>`)**
   - Cada vista puede enlazar su propio archivo CSS.
   - Algunas vistas dependen solo del CSS global (no declaran `stylesheets` propio).

## 2. Archivos CSS detectados

Ubicacion: `src/main/resources/com/mosqueteros/proyecto_restaurante/styles/`

- `styles.css` (base general compartida)
- `theme-overrides.css` (tokens de tema y overrides)
- `main-shell.css` (layout principal del shell)
- `design-preview.css` (preview visual de configuracion/temas)
- `login-v5.css` (pantalla login)
- `vista-mesas.css` (modulos mesas/pedidos)
- `vista-platos.css` (modulo platos)
- `clientes.css` (vista legacy de clientes)
- `restaurante-tailwind.css` (**detectado pero no enlazado en vistas**)

## 3. Vistas con CSS explicito

Ubicacion de vistas: `src/main/resources/com/mosqueteros/proyecto_restaurante/view/`

| Vista FXML | CSS enlazado en la vista | Observaciones |
|---|---|---|
| `main.fxml` | `main-shell.css`, `styles.css`, `theme-overrides.css`, `design-preview.css` | Shell principal; concentra estilos del contenedor global. |
| `login.fxml` | `login-v5.css` | Estilo propio del login. |
| `VistaMesas.fxml` | `vista-mesas.css` | Modulo operativo de mesas. |
| `VistaPedidos.fxml` | `vista-mesas.css` | Reutiliza estilo visual de mesas. |
| `VistaPlatos.fxml` | `vista-platos.css` | Modulo platos con hoja dedicada. |
| `VistaAreasMesa.fxml` | `styles.css` | Usa base compartida. |
| `VistaCategoriaInsumo.fxml` | `styles.css` | Usa base compartida. |
| `VistaCategoriasPlato.fxml` | `styles.css` | Usa base compartida. |
| `VistaClientes.fxml` | `styles.css` | Usa base compartida. |
| `VistaConceptoEgreso.fxml` | `styles.css` | Usa base compartida. |
| `VistaConfiguracion.fxml` | `styles.css` | Usa base y clases de tema/swatch. |
| `VistaEgresos.fxml` | `styles.css` | Usa base compartida. |
| `VistaFormaPago.fxml` | `styles.css` | Usa base compartida. |
| `VistaInsumos.fxml` | `styles.css` | Usa base compartida. |
| `VistaUsuarios.fxml` | `styles.css` | Usa base compartida. |
| `cliente.fxml` | `clientes.css` | Tiene URL de `clientes.css` duplicada en `<stylesheets>`. |

## 4. Vistas sin CSS explicito (heredan CSS global de la Scene)

Estas vistas no declaran `stylesheets` en su FXML, pero al cargarse dentro de `main.fxml` heredan los CSS de la `Scene`.

| Vista FXML | Observaciones |
|---|---|
| `VistaComandas.fxml` | Usa `styleClass` (ej. `welcome-panel`, `welcome-title`). |
| `VistaInventarioLog.fxml` | Usa `styleClass` de panel de bienvenida. |
| `VistaPQRS.fxml` | Usa `styleClass` de panel de bienvenida. |
| `VistaPerfiles.fxml` | Usa `styleClass` de panel de bienvenida. |
| `VistaPresentacion.fxml` | Usa `styleClass` de panel de bienvenida. |
| `VistaReciboCaja.fxml` | Usa `styleClass` de panel de bienvenida. |
| `VistaSedes.fxml` | Usa `styleClass` de panel de bienvenida. |

## 5. Vistas placeholder/incompletas

Estas vistas estan practicamente vacias (1 linea import) y hoy no tienen estructura visual ni CSS aplicado:

- `areamesa.fxml`
- `categoriainsumo.fxml`
- `categoriaplato.fxml`
- `conceptoegreso.fxml`
- `egreso.fxml`
- `formapago.fxml`
- `insumo.fxml`
- `inventariolog.fxml`
- `mesa.fxml`
- `pedido.fxml`
- `pedidodetalle.fxml`
- `perfil.fxml`
- `plato.fxml`
- `platoingrediente.fxml`
- `pqrs.fxml`
- `presentacion.fxml`
- `recibocaja.fxml`
- `recibocajadetalle.fxml`
- `usuario.fxml`

## 6. Como se decide que estilo gana (cascada real en tu app)

Orden general efectivo (de menor a mayor prioridad):

1. Estilos por defecto de JavaFX
2. `styles.css` (base)
3. `theme-overrides.css` (tokens y overrides por tema)
4. CSS especifico de vista (si existe)
5. `styleClass` y `id` concretos del nodo
6. `style="..."` inline en FXML (si se usa)

Nota: en `main.fxml` el orden de `stylesheets` influye directamente en que regla sobreescribe a otra.

## 7. Guia practica para crear o modificar estilos personalizados

### Caso A: cambiar look global de casi todos los modulos

- Edita `styles.css` para componentes base reutilizados.
- Si el cambio depende del tema (artisan, goldenbeach, etc.), edita `theme-overrides.css`.

### Caso B: cambiar un modulo puntual

- Si el modulo ya tiene CSS propio, edita ese archivo:
  - `vista-mesas.css`
  - `vista-platos.css`
  - `login-v5.css`
  - `clientes.css`
- Si no tiene CSS propio, tienes dos opciones:
  - agregar clases nuevas en `styles.css`, o
  - crear `mi-modulo.css` y enlazarlo en la vista FXML.

### Caso C: crear una vista nueva con estilo limpio y mantenible

1. Definir `styleClass` semanticas en FXML (ej. `modulo-card`, `modulo-title`).
2. Crear `styles/mi-modulo.css`.
3. Enlazarlo en la raiz FXML con `stylesheets="@../styles/mi-modulo.css"`.
4. Evitar estilos inline para mantener consistencia.
5. Reusar tokens de `theme-overrides.css` para que soporte cambio de tema.

## 8. Hallazgos utiles para mantenimiento

- `cliente.fxml` enlaza dos veces `clientes.css` (puede limpiarse para evitar redundancia).
- `restaurante-tailwind.css` existe pero no se esta usando en las vistas actuales.
- Muchas vistas modernas comparten clases base (`root-container`, `main-card`, `mesas-table`, `btn-raised-primary`), por lo que conviene mantener esos estilos en `styles.css`.
- Vistas de dashboard tipo "welcome" (`VistaComandas`, `VistaPQRS`, etc.) dependen de clases comunes; cambios en esas clases impactan varios modulos al tiempo.

## 9. Checklist rapido antes de tocar estilos

- Identificar si el cambio es global (`styles.css`) o local (CSS de vista).
- Verificar si la clase CSS se usa en varias vistas antes de modificarla.
- Si el cambio es de color/contraste, revisar tambien `theme-overrides.css`.
- Probar login + shell principal + al menos un modulo con CSS propio (`VistaMesas` o `VistaPlatos`).
