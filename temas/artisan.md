# Design System Documentation: The Artisanal Interface

## 1. Overview & Creative North Star
**Creative North Star: "The Digital Boulangerie"**

This design system rejects the sterile, cold nature of standard POS systems in favor of a "Digital Boulangerie" aesthetic. The goal is to evoke the tactile warmth of a high-end bakery—the smell of toasted grain, the texture of heavy cardstock menus, and the precision of a master pastry chef. 

We achieve an "Upscale Rustic" feel by breaking the standard digital grid. Instead of rigid boxes, we utilize **intentional asymmetry** and **tonal layering**. Elements should feel "placed" on a surface rather than "rendered" on a screen. High-contrast typography scales (pairing an authoritative serif with a modern sans-serif) create an editorial rhythm that feels premium and intentional.

---

## 2. Colors & Surface Philosophy
The palette is built on the contrast between deep, roasted tones and warm, glowing highlights.

### The Palette (Material Design Tokens)
*   **Primary (#8D4F00):** The "Roasted" tone. Used for active states and deep emphasis.
*   **Primary Container (#F5A454):** The "Warm Orange." This is your signature CTA color, evoking a glowing hearth.
*   **Surface (#FFF8F7):** The "Light Beige" base. A creamy, off-white that reduces eye strain compared to pure hex white.
*   **On-Surface (#24191A):** The "Dark Coffee." Used for primary legibility to maintain high contrast without the harshness of pure black.

### The "No-Line" Rule
To maintain a high-end feel, **1px solid borders for sectioning are strictly prohibited.** Do not use lines to separate a sidebar from a main content area. Instead:
*   **Background Shifts:** Use `surface-container-low` (#FFF0F0) against a `surface` (#FFF8F7) background to define zones.
*   **Tonal Transitions:** Use a subtle shift in saturation to denote where one functional area ends and another begins.

### Signature Textures & Glass
*   **The Golden Glow:** For main CTAs, use a subtle linear gradient transitioning from `primary` to `primary-container`. This adds a "soul" to the button that flat color cannot replicate.
*   **Glassmorphism:** Floating modals or "Order Summary" drawers should use `surface_container_lowest` (#FFFFFF) at 85% opacity with a `20px` backdrop-blur. This makes the UI feel integrated and layered, like flour dusted on a marble counter.

---

## 3. Typography
We use a high-contrast pairing to balance "Rustic" and "Modern."

*   **Display & Headlines (Noto Serif):** Use these for product names, category headers, and total amounts. The bold serif weight conveys heritage and craft.
    *   *Headline-LG (2rem):* For the main bakery name or page title.
*   **Body & Titles (Manrope):** A clean, geometric sans-serif for high-speed readability in a busy POS environment.
    *   *Body-MD (0.875rem):* The workhorse for ingredient lists and descriptions.
*   **Label-SM (0.6875rem):** Used exclusively for technical metadata (timestamps, SKU numbers) in `on-surface-variant` (#534438).

---

## 4. Elevation & Depth
In this system, depth is a physical property, not a drop-shadow effect.

*   **The Layering Principle:** Stack your surfaces. A `surface-container-lowest` (#FFFFFF) card should sit on a `surface-container-low` (#FFF0F0) background. This creates a soft, natural lift.
*   **Ambient Shadows:** If an element must "float" (like a notification or a picked item), use a shadow tinted with the `on-surface` color:
    *   `box-shadow: 0 12px 32px rgba(36, 25, 26, 0.08);`
    *   *Note: Never use pure black shadows.*
*   **The Ghost Border:** If accessibility requires a container boundary, use the `outline-variant` (#D8C3B2) at **15% opacity**. It should be felt, not seen.

---

## 5. Components

### Buttons (The "Hearth" Elements)
*   **Primary:** Rounded (12px), `primary-container` background, `on-primary-container` text. Apply a subtle 2px inner-glow (lighter orange) on the top edge to mimic a 3D tactile button.
*   **Secondary:** Ghost style. No background, `primary` text, and a "Ghost Border" that only appears on hover.

### Cards & Lists (The "Menu" Style)
*   **The Rule of White Space:** Forbid the use of divider lines between list items. Use the **Spacing Scale (Step 3: 1rem)** to separate items.
*   **Product Cards:** Use `surface_container_lowest` (#FFFFFF) with a `DEFAULT` (12px) corner radius. The image should be slightly inset rather than bleed-to-edge to feel like a framed photograph.

### Input Fields
*   **States:** Background should be `surface-container-high`. On focus, the border transitions to a `primary-container` (#F5A454) "Ghost Border" (20% opacity) with a soft outer glow.

### Specialized Bakery Components
*   **The "Freshness" Chip:** A small, rounded-pill using `tertiary-container` with `notoSerif` italic text for "Daily Special" or "Just Out of Oven" tags.
*   **Quantity Stepper:** A minimal, horizontal component. Instead of a box, use a single `surface-variant` pill where the numbers sit directly on the surface.

---

## 6. Do’s and Don’ts

### Do:
*   **Do** use asymmetrical layouts. Place a large Serif headline on the left with significant white space to its right to create an editorial, high-end feel.
*   **Do** use the Spacing Scale religiously. Consistent gaps (Step 4: 1.4rem) between cards are more effective than any border.
*   **Do** prioritize typography over icons. Let the beautiful Serif typeface do the talking.

### Don't:
*   **Don't** use 100% black (#000000). It kills the "Warm Bakery" vibe. Always use `on-surface` (#24191A).
*   **Don't** use sharp 90-degree corners. Everything in a bakery is soft—from the loaves to the flour sacks. Stick to the **12px (DEFAULT)** or **16px (lg)** radius.
*   **Don't** use "Alert Red" for errors if possible. Use the `error` token (#BA1A1A) which is slightly desaturated to fit the organic palette.