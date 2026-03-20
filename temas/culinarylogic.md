# Design System Document

## 1. Overview & Creative North Star: "The Culinary Curator"
This design system moves away from the rigid, boxy nature of traditional enterprise dashboards. Instead, it adopts a **"Culinary Curator"** aesthetic—a high-end editorial approach that treats data as a premium ingredient. 

The system balances the industrial precision of a modern kitchen with the warmth of a gold-standard dining experience. We achieve this by rejecting "template" layouts in favor of intentional asymmetry, layered depth, and a dramatic high-contrast palette. By using expansive white space (breathing room) and sophisticated tonal shifts, we transform a management tool into a signature digital environment.

---

## 2. Colors: Tonal Depth & The "No-Line" Rule
The palette is rooted in deep obsidian and warm gold, creating an atmosphere of authority and prestige.

### The Palette
- **Primary (CTA):** `#FFCB74` (Golden Yellow) | Text: `#111111`
- **Surface (Main BG):** `#F6F6F6` (Light Mode) | `#111111` (Dark Mode Sidebar)
- **Neutral Containers:** 
    - `surface-container-lowest`: `#FFFFFF`
    - `surface-container-low`: `#F0EDED`
    - `surface-container-high`: `#EAE7E7`
- **Accents:** Tertiary `#00677F` (Deep Teal) for analytical data points.

### The "No-Line" Rule
Prohibit the use of 1px solid borders for sectioning. Boundaries must be defined through:
1. **Background Shifts:** Place a `surface-container-lowest` card atop a `surface-container-low` background.
2. **Negative Space:** Use the **Spacing Scale (8 or 10)** to create structural separation.
3. **Ghost Borders:** If a boundary is strictly required for accessibility, use `outline-variant` at **10% opacity**. Never use 100% opaque lines.

### Glass & Gradient Rule
To prevent the UI from feeling flat, use **Glassmorphism** for floating overlays (e.g., Modals, Dropdowns). Apply a backdrop-blur of `12px` to a semi-transparent `#FFFFFF` or `#111111` (80% opacity). Main CTAs should feature a subtle linear gradient from `primary` to `primary-fixed` to provide a "metallic" sheen.

---

## 3. Typography: Editorial Authority
We use a dual-typeface system to separate "Action" from "Information."

*   **Display & Headlines (Plus Jakarta Sans):** Used for big numbers, section titles, and brand moments. The bold weight conveys the confidence of a head chef.
    *   `display-md`: 2.75rem / Bold (For hero metrics)
    *   `headline-sm`: 1.5rem / Bold (For card titles)
*   **Body & Labels (Inter):** Used for dense data, table rows, and secondary UI.
    *   `body-md`: 0.875rem / Regular (Standard reading)
    *   `label-md`: 0.75rem / Medium (Captions and status chips)

The high-contrast scale (jumping from 14px body to 32px headers) creates a clear visual hierarchy that guides the eye through complex management tasks.

---

## 4. Elevation & Depth: Tonal Layering
Traditional drop shadows are largely replaced by **Tonal Layering**.

### The Layering Principle
Think of the UI as stacked sheets of fine paper. 
*   **Level 0 (Base):** `surface` background.
*   **Level 1 (Sections):** `surface-container-low`.
*   **Level 2 (Active Cards):** `surface-container-lowest`.

### Ambient Shadows
For floating elements (Modals/Popovers), use an **Ambient Shadow**:
*   `box-shadow: 0 12px 32px rgba(17, 17, 17, 0.06);`
The shadow must be tinted with the `on-surface` color to feel like natural light passing through a high-end space, rather than a "computer-generated" grey blur.

---

## 5. Components: Precision & Grace

### Buttons
*   **Primary:** Background `#FFCB74`, Text `#111111`. Radius: `12px`. High-sheen subtle gradient.
*   **Secondary:** Ghost style. No background, `outline-variant` (20% opacity) border.
*   **Interaction:** On hover, primary buttons should "lift" via a subtle `y-minus-2px` translation rather than a color change.

### Cards & Lists
*   **Forbid Dividers:** Use `padding-bottom: 24px` (Spacing 6) to separate list items.
*   **Surface Shift:** Active list items should shift from `surface-container-low` to `surface-container-highest` rather than showing a border.

### Input Fields
*   **Style:** Minimalist. Only a bottom "Ghost Border" (10% opacity) until focused.
*   **Focus State:** The border transitions to a 2px `primary` (#FFCB74) underline with a subtle outer glow.

### Chips (Status)
*   Instead of solid blocks, use light-tinted backgrounds (e.g., `error-container`) with high-contrast text (`on-error-container`). Radius should be `full` (pill-shaped).

### Signature Component: The "Chef’s Table" Header
A sticky top-bar using Glassmorphism (`backdrop-blur: 20px`) that allows the vibrant colors of the dashboard to bleed through as the user scrolls, maintaining a sense of place.

---

## 6. Do’s and Don’ts

### Do:
*   **Use Asymmetry:** Align the main header to the far left and the primary action to the far right with significant white space between them.
*   **Embrace Large Type:** Let metrics (like "Daily Revenue") breathe at `display-sm` sizes.
*   **Nesting:** Nest `surface-container-lowest` elements inside `surface-container-low` wrappers to create organic hierarchy.

### Don't:
*   **Don't use 100% Black:** Even in dark mode, use `#111111` to allow for subtle depth and shadows to remain visible.
*   **Don't use Grid Borders:** Avoid the "Excel" look. If a table needs structure, use alternating row tints (`surface-container-low`) instead of lines.
*   **Don't Crowded:** If a screen feels busy, increase the spacing scale instead of adding more dividers.