# Design System Specification: Editorial Coastal Elegance

## 1. Overview & Creative North Star
**The Creative North Star: "The Modern Mariner"**

This design system rejects the "standard dashboard" aesthetic in favor of a high-end, editorial experience that feels like a premium lifestyle magazine. We are not just building an interface; we are evoking the sensory experience of a coastal retreat. 

The system moves beyond generic grids by utilizing **intentional asymmetry** and **breathable white space**. By layering "sand" (warm neutrals) and "sea" (deep teals), we create a sense of depth that feels natural and fluid. We break the "template" look through overlapping elements, oversized typography scales, and a complete rejection of rigid structural lines.

---

## 2. Colors: Tonal Depth & The Coastal Palette

Our palette is inspired by the intersection of deep ocean water and sun-drenched sand. We use Material-inspired tonal ranges to ensure the UI feels sophisticated rather than flat.

### The Palette (Core Tokens)
- **Primary (`#006070`):** Our "Deep Sea" anchor. Used for high-impact actions and brand presence.
- **Primary Container (`#1F7A8C`):** The "Ocean Blue." Use this for hero sections or primary interactive surfaces.
- **Secondary (`#7B5800`):** The "Golden Hour." Reserved for high-contrast accents and notifications.
- **Secondary Container (`#FDC656`):** The "Sunlight." Used for cards and highlights at 50%–100% opacity.
- **Surface & Backgrounds:** We utilize a range from `surface-container-lowest` (#FFFFFF) to `surface-dim` (#D9DADB) to define space.

### The "No-Line" Rule
Standard 1px borders are strictly prohibited for sectioning. Structural boundaries must be defined solely through background color shifts. For example, a `surface-container-low` section sitting against a `surface` background creates a clear, sophisticated transition without the "boxed-in" feel of traditional UI.

### Surface Hierarchy & Nesting
Treat the UI as a series of physical layers. Use `surface-container` tiers to create "nested" depth. 
*   **Level 0 (Base):** `surface`
*   **Level 1 (Cards/Content):** `surface-container-low`
*   **Level 2 (In-set elements):** `surface-container-high`

### The "Glass & Gradient" Rule
To add "soul," use subtle linear gradients for CTAs, transitioning from `primary` to `primary-container`. For floating navigation or over-image menus, use **Glassmorphism**: semi-transparent `surface` colors with a 12px-20px backdrop-blur to allow the coastal tones to bleed through the interface.

---

## 3. Typography: Editorial Authority

We use a high-contrast scale to create an editorial rhythm. 

*   **Display & Headlines (Plus Jakarta Sans):** These are our "Hero" moments. Use `display-lg` (3.5rem) for main landing titles to create an immediate sense of premium scale. Bold weights are mandatory for headlines to anchor the "Coastal" vibe against the airy white space.
*   **Body & Titles (Be Vietnam Pro):** A clean, geometric sans-serif that ensures legibility. `body-md` (0.875rem) is our standard for long-form content, providing a modern, "tech-forward" feel that balances the traditional weight of the headlines.
*   **Tracking:** For `label-sm` and `label-md`, increase letter spacing by 2-3% to maintain a high-end, airy aesthetic.

---

## 4. Elevation & Depth: Tonal Layering

Traditional drop shadows are too heavy for a "fresh and vibrant" system. We achieve hierarchy through **Tonal Layering**.

*   **The Layering Principle:** Instead of shadows, place a `surface-container-lowest` card on a `surface-container-low` background. This creates a "soft lift" that feels architectural.
*   **Ambient Shadows:** If an element *must* float (e.g., a Modal or FAB), use a highly diffused shadow: `0px 10px 40px rgba(31, 122, 140, 0.08)`. Notice the shadow is tinted with our Primary Ocean Blue to mimic natural light refraction in a coastal environment.
*   **The "Ghost Border" Fallback:** If accessibility requires a border, use the `outline-variant` token at **20% opacity**. Never use 100% opaque borders for containers.
*   **Roundedness:** A consistent `DEFAULT: 0.5rem` (12px) radius is applied to all cards and inputs, echoing the smoothed edges of sea glass. Use `full` (9999px) for chips and buttons.

---

## 5. Components: Fluidity & Impact

### Buttons
- **Primary:** Gradient fill (`primary` to `primary_container`), `full` roundedness. No border.
- **Secondary:** `surface` background with a `secondary` (Golden Yellow) ghost border at 50% opacity.
- **Tertiary:** Pure text using `primary` color with a subtle `primary-container` background on hover.

### Input Fields
- **Styling:** White background (`surface-container-lowest`), 12px border-radius, and a `primary` border. 
- **Interaction:** On focus, the border weight remains 1px, but a soft `primary` ambient shadow (4% opacity) is added to create a "glow" effect.

### Cards & Lists
- **The Divider Ban:** Do not use line dividers between list items. Use vertical white space (Spacing `4` or `5`) or alternating `surface-container` subtle background shifts.
- **Editorial Cards:** Images should often "break" the card container (bleeding to the edge) to create a high-end magazine feel.

### Selection Chips
- Use `secondary-fixed` for the background of selected chips to provide a "sunny" pop of color that guides the eye.

---

## 6. Do’s and Don’ts

### Do:
- **Do** use asymmetrical layouts where text overlaps slightly with images.
- **Do** lean heavily on the Spacing Scale (specifically `12` and `16`) to give content room to breathe.
- **Do** use the Golden Yellow (`secondary`) sparingly as a "sparkle" color to highlight key data points.

### Don't:
- **Don't** use pure black for text. Use `on-surface` (#191C1D) to maintain softness.
- **Don't** use 100% opaque borders for anything other than input fields.
- **Don't** use sharp 0px corners; everything must feel smoothed by the tide.
- **Don't** implement a dark mode; this system is designed to live in the light of a coastal day.

---

## 7. Spacing & Rhythm
Use the provided spacing scale to maintain a strict vertical rhythm. 
- **Standard Padding:** `spacing-6` (2rem) for card internals.
- **Section Gaps:** `spacing-16` (5.5rem) to ensure a distinct separation of ideas, preventing the layout from feeling cluttered or "app-like."