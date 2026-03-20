# Design System Strategy: The Culinary Curator

## 1. Overview & Creative North Star
The Creative North Star for this design system is **"The Culinary Curator."** 

Unlike standard management software that feels clinical and grid-locked, this system treats the restaurant interface as a high-end editorial layout. We are moving away from "software" and toward "service." The aesthetic goal is to mirror the experience of a Michelin-starred table: warm, expansive, and meticulously composed. 

To break the "template" look, we employ **Intentional Asymmetry**. Instead of perfectly centered grids, we use the `Spacing Scale` to create generous, breathing "white space" (using the Ivory `surface` token) that guides the eye. Overlapping elements—such as a `surface_container_lowest` card slightly offset over a `surface_container` section—create a sense of physical depth and architectural intent.

## 2. Colors & Tonal Depth

The palette is rooted in organic, culinary tones: Ivory, Burgundy, and Dusty Olive. To maintain a premium feel, we prioritize **Tonal Layering** over structural lines.

### The "No-Line" Rule
Explicitly prohibit the use of 1px solid high-contrast borders for sectioning. Boundaries must be defined through background color shifts. For example, a `surface_container_low` section sitting on a `surface` background creates a clear but soft distinction. 

### Surface Hierarchy & Nesting
Treat the UI as a series of stacked fine papers.
*   **Base Layer (`surface` / #fdf9f4):** The foundational "canvas" (Ivory).
*   **Mid Layer (`surface_container` / #f1ede8):** Used for large groupings or sidebar backgrounds.
*   **Top Layer (`surface_container_lowest` / #ffffff):** Reserved for interactive cards and primary content panels. This "pure white" on Ivory creates a subtle, sophisticated lift.

### Signature Textures & Glass
*   **The Burgundy Gradient:** To provide visual "soul," primary CTAs should not be flat. Use a subtle linear gradient from `primary_container` (#6d001a) to `primary` (#45000d) at a 135-degree angle.
*   **The Glass Rule:** For floating navigation or "Quick Action" overlays, use `surface_container_lowest` at 85% opacity with a `backdrop-blur` of 12px. This ensures the warm Ivory background bleeds through, softening the edges of the UI.

## 3. Typography: Editorial Authority

The typography pairs the architectural strength of **Epilogue** with the modern readability of **Manrope**.

*   **Display & Headlines (Epilogue):** These are your "Statement Pieces." Use `display-lg` and `headline-md` with tight letter-spacing (-0.02em) to convey authority and elegance. They should feel like a menu header in a luxury bistro.
*   **Body & Labels (Manrope):** Chosen for its clean, neutral legibility. Use `body-md` for standard table data and `label-sm` for secondary metadata. 
*   **Visual Hierarchy:** Use `primary` (Burgundy) sparingly in typography—only for high-priority status or active navigation links—to maintain its impact.

## 4. Elevation & Depth

We achieve a "Sophisticated Lift" through light and shadow, mimicking natural ambient illumination.

*   **The Layering Principle:** Place a `surface_container_lowest` (#ffffff) card on a `surface_container_low` section. The 2-point shift in tonal value is enough to define the object without a single pixel of stroke.
*   **Ambient Shadows:** When a card must "float" (e.g., a draggable reservation or a modal), use a shadow with a blur of `24px` and an opacity of `6%`. The shadow color should be a tinted version of `on_surface` (#1c1c19), never pure black, to keep the "warm" feel.
*   **The Ghost Border:** If a border is required for accessibility (e.g., input fields), use the `outline_variant` token at 20% opacity. This creates a "suggestion" of a boundary rather than a hard cage.

## 5. Components

### Buttons
*   **Primary:** Burgundy gradient, `DEFAULT` (0.5rem/12px) rounded corners, white text.
*   **Secondary:** `surface_container_highest` background with `on_surface` text. No border.
*   **Tertiary:** Ghost style; `on_surface` text with a subtle `primary` underline on hover.

### Cards & Lists
*   **Forbidden:** 1px divider lines. 
*   **Replacement:** Use `Spacing Scale 4` (1.4rem) to separate list items, or alternating background shifts between `surface_container_low` and `surface_container_lowest`.

### Inputs & Fields
*   **Text Inputs:** Use `surface_container_lowest` with a `Ghost Border`. On focus, the border transitions to `primary` (Burgundy) at 40% opacity with a soft 2px outer glow.

### Specialized Restaurant Components
*   **Table Map Nodes:** Circular or `xl` (1.5rem) rounded shapes. Use `secondary_container` (Dusty Olive) for occupied tables and `surface_container_lowest` with a soft shadow for available ones.
*   **Status Chips:** Use highly desaturated versions of the palette. A "Confirmed" reservation uses a `secondary_fixed` background with `on_secondary_fixed_variant` text—keeping the olive tones sophisticated rather than "traffic-light" green.

## 6. Do’s and Don’ts

### Do
*   **Do** use asymmetrical margins. A wider left-hand gutter for a headline creates an editorial, high-end feel.
*   **Do** prioritize "Air." If in doubt, increase the spacing by one step on the `Spacing Scale`.
*   **Do** use `rounded-xl` (1.5rem) for large image containers to soften the "tech" feel.

### Don’t
*   **Don’t** use pure black (#000000) for anything. Use `on_surface` (#1c1c19) to maintain the warmth of the Ivory base.
*   **Don’t** use dark-mode panels. This system is strictly light-mode; depth is achieved through beige/ivory/grey-olive shifts, never through deep greys or blacks.
*   **Don’t** use sharp 90-degree corners. Everything must feel "touched by hand" and softened to the `DEFAULT` 12px radius.