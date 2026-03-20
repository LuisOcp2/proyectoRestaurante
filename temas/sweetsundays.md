# Design System Specification: Editorial Confection

## 1. Overview & Creative North Star: "The Artisanal Patisserie"
This design system moves away from the sterile, modular grid of standard SaaS and embraces the "Artisanal Patisserie" aesthetic. The North Star is **Warmth through Intentional Asymmetry**. We treat the digital interface as a high-end dessert menu—tactile, layered, and unapologetically appetizing. 

To break the "template" look, we leverage **Editorial Layering**: elements should feel like they were placed by hand, not snapped to a rigid grid. We achieve this through overlapping containers, generous white space (the "breathing room" of a gallery), and a high-contrast typography scale that favors bold, expressive headings over small, crowded text.

---

## 2. Colors & Tonal Depth
The palette is a sophisticated blend of "Linen" warmth and "Chocolate" depth. We do not use "Generic Gray" anywhere. Every neutral is warmed with a hint of orange or brown to maintain the "Retro Modern" soul.

### The "No-Line" Rule
**Explicit Instruction:** Do not use 1px solid borders to define sections. Boundaries must be defined solely through background color shifts. For example, a `surface-container-low` section sitting on a `surface` background creates a soft, sophisticated edge that feels integrated, not "boxed in."

### Surface Hierarchy & Nesting
Treat the UI as a series of physical layers—like stacked sheets of fine parchment.
*   **Base:** `surface` (#fff8f5) for the widest page background.
*   **Structural Depth:** Use `surface-container` (#fceae0) for large sidebar areas or grouped content.
*   **The "Pop" Layer:** Use `surface-container-lowest` (#ffffff) for cards or interactive elements to make them "lift" off the warm background without needing a harsh shadow.

### The "Glass & Signature Texture" Rule
*   **Glassmorphism:** For floating overlays or navigation bars, use `surface` at 80% opacity with a `20px` backdrop-blur. This allows the dessert-inspired tones to bleed through the UI, softening the experience.
*   **Signature Gradients:** For primary CTAs, do not use a flat fill. Use a subtle linear gradient from `primary` (#a93100) to `primary_container` (#d44000) at a 45-degree angle. This adds "visual soul" and a slight three-dimensional "glaze" effect.

---

## 3. Typography: Expressive & Readable
We pair the bold, charismatic **Plus Jakarta Sans** with the functional, modern **Be Vietnam Pro**. This creates a "Headline as Hero" hierarchy.

*   **Display & Headlines (Plus Jakarta Sans):** These are your "flavor notes." Use `display-lg` (3.5rem) and `headline-lg` (2rem) with tight letter-spacing (-0.02em) to create a bold, friendly, and high-end editorial feel. Color: `on_surface` (#231a14).
*   **Body & Labels (Be Vietnam Pro):** The "recipe." `body-lg` (1rem) provides maximum readability. Ensure line-height is generous (1.6) to maintain the airy, premium feel. 
*   **Hierarchy Note:** Always use `primary` (#a93100) for small labels or category tags to draw the eye without overwhelming the layout.

---

## 4. Elevation & Depth: Tonal Layering
Traditional drop shadows are too "tech." We use **Ambient Shadows** and **Tonal Stacking**.

*   **The Layering Principle:** Instead of a shadow, place a `surface-container-lowest` card on a `surface-container-low` background. The subtle shift in hex value creates a natural, "baked-in" depth.
*   **Ambient Shadows:** If an element must float (e.g., a Modal), use a shadow with a 40px-60px blur, 0% spread, and an opacity of 6% using a tinted color like `secondary` (#7f5449). It should feel like a soft glow, not a dark smudge.
*   **The Ghost Border Fallback:** If a border is required for accessibility, use `outline_variant` at **15% opacity**. 100% opaque borders are strictly forbidden as they break the "soft" aesthetic.

---

## 5. Components: Soft & Tactile
All components follow a standard `xl` (1.5rem / 24px) or `lg` (1rem / 16px) corner radius to mimic the soft edges of a pastry or a rounded retro appliance.

*   **Primary Buttons:** High-contrast `primary` (#a93100) with `on_primary` (White) text. Use `xl` (full) roundedness. Padding: `1.2rem` (top/bottom) by `2.75rem` (left/right).
*   **Accent Chips/Tags:** Use `tertiary_fixed` (#cae7f7) for the background and `on_tertiary_fixed` (#001f2a) for text. These provide a "cool" refreshing contrast to the warm browns and oranges.
*   **Cards:** Forbid divider lines. Separate "Header" from "Body" content using a `1.4rem` (Spacing 4) vertical gap or a subtle background shift to `surface_container_highest`.
*   **Input Fields:** Use `surface_container_low` for the fill. On focus, the border should not change color; instead, the background should shift to `surface_container_lowest` with a soft ambient shadow.
*   **The "Hero" Sidebar:** Utilizing `secondary` (#7f5449) or the deep `on_secondary_fixed` (#31130b), the sidebar should feel like a solid chocolate block anchoring the airy linen page.

---

## 6. Do’s and Don’ts

### Do:
*   **Do** use asymmetrical margins. If a container has 4rem padding on the left, try 5.5rem (Spacing 16) on the right for an editorial look.
*   **Do** lean into the "Botticelli light blue" for interactive elements that aren't the main CTA—it acts as a "palate cleanser."
*   **Do** use overlapping elements. Let a product image break the container of a card to create a sense of depth and playfulness.

### Don’t:
*   **Don't** use 1px solid black or grey borders. They kill the "appetizing" warmth of the system.
*   **Don't** use standard 8px border radii. It looks like a generic bootstrap site. Stick to the `lg` (16px) and `xl` (24px) scale.
*   **Don't** crowd the content. If you think there is enough white space, add 20% more. Premium design requires "room to breathe."