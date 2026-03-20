# Design System Strategy: The Electric Epicurean

### 1. Overview & Creative North Star
This design system is built upon the concept of **"Electric Editorial."** It rejects the sterile, "safe" layouts of typical SaaS platforms in favor of the high-contrast tension found in luxury fashion magazines and avant-garde culinary journals. 

The Creative North Star is the **Digital Sommelier**: authoritative, sophisticated, and surprisingly vibrant. We achieve this by clashing the timeless elegance of **Noto Serif** and **Pure Black (#000000)** against the futuristic energy of **Violet (#7E3BED)** and **Lime (#C6FF34)**. The layout must feel intentional and curated, utilizing white space not just as "breathing room," but as a structural element that guides the eye toward high-impact imagery and bold typography.

---

### 2. Colors: High-Contrast Vibrancy
Our palette is designed for maximum visual impact. It relies on the tension between deep blacks, crisp whites, and neon-adjacent accents.

*   **The Foundation:** Use `surface` (#f9f9f9) for main canvas areas and `on_surface` (#1b1b1b) for readability. The sidebar is anchored in **Pure Black (#000000)** to provide a permanent architectural frame for the content.
*   **The Accents:** `primary` (#6510d4) is your tool for high-level brand moments. `secondary` (#4c6700) and its vibrant container (#baf224) act as the "electric" spark—use these for highlights, status indicators, and call-to-actions that must not be missed.
*   **The "No-Line" Rule:** We do not use 1px solid borders to separate sections. Structure is defined by background shifts. Place a `surface_container_low` (#f3f3f3) block against a `surface` background to define a zone. 
*   **Surface Hierarchy:** Use the `surface_container` tiers (Lowest to Highest) to "stack" information. A card should be `surface_container_lowest` (#ffffff) sitting on a `surface_container` (#eeeeee) section. This creates a subtle "paper-on-table" depth without heavy shadows.
*   **Signature Textures:** For high-end hero sections, use a subtle linear gradient transitioning from `primary` (#6510d4) to `primary_container` (#7e3bed). 
*   **Glassmorphism:** For floating overlays (like mobile navigation or quick-action modals), use semi-transparent `surface_container_lowest` with a `backdrop-blur` of 20px. This ensures the vibrant food photography beneath feels integrated, not obscured.

---

### 3. Typography: Editorial Authority
The type system is a dialogue between the traditional and the modern.

*   **Display & Headline (Noto Serif):** These are your "Signature" moments. Use `display-lg` for hero titles. The serif weight conveys luxury and culinary heritage. Ensure headings use `on_surface` (#1b1b1b) to maintain an "ink-on-paper" feel.
*   **Body & Utility (Manrope):** Use `body-lg` for descriptions. Manrope’s geometric clarity balances the serif's complexity. 
*   **The "Editorial Scale":** Create drama by placing `label-sm` (Manrope, Uppercase, Tracking +10%) directly above a `display-md` (Noto Serif) headline. The extreme size difference is a hallmark of high-end design.

---

### 4. Elevation & Depth: Tonal Layering
In this system, depth is a matter of light and tone, not structural lines.

*   **The Layering Principle:** Avoid shadows for static elements. Instead, elevate a card by moving from `surface_container_low` to `surface_container_lowest`. 
*   **Ambient Shadows:** If an element must float (e.g., a "Book a Table" FAB), use a highly diffused shadow: `box-shadow: 0 20px 40px rgba(126, 59, 237, 0.08)`. Note the slight tint of `primary` violet in the shadow to make it feel "electric" rather than muddy.
*   **The "Ghost Border" Fallback:** If a container requires definition against a similar background, use a "Ghost Border": 1px solid `outline_variant` (#ccc3d8) at **20% opacity**. 
*   **Signature Lime Accent:** Use the `secondary_fixed` (#bdf528) token for ultra-thin 2px top-borders on "Featured" cards to provide that "vibrant" upscale pop.

---

### 5. Components: Modern Primitives
All components utilize the **12px (0.75rem)** `md` roundedness scale to feel approachable yet modern.

*   **Buttons:**
    *   *Primary:* `primary` (#6510d4) background with `on_primary` (#ffffff) text.
    *   *Secondary (Vibrant):* `secondary_container` (#baf224) background with `on_secondary_fixed` (#151f00) text. Use this for "Order Now" or "Bookings."
*   **Input Fields:** Use `surface_container_high` (#e8e8e8) for the field background. Labels should be `label-md` in `on_surface_variant`. On focus, the border should transition to a 2px `primary` (#6510d4) stroke.
*   **Chips:** Use `secondary_fixed_dim` (#a3d800) for active filters to create a high-visibility "highlighter" effect.
*   **Cards & Lists:** **Strictly no dividers.** Use `3.5rem` (`10`) vertical spacing between list items. For cards, rely on the shift between `surface` and `surface_container_low` to define the boundary.
*   **Sidebar:** The pure black sidebar should use `primary` (#7e3bed) for active state icons and `on_primary_fixed` for inactive states to maintain a sophisticated noir aesthetic.

---

### 6. Do's and Don'ts

**Do:**
*   **Do** use asymmetrical layouts. A large image on the left with a small, perfectly typeset block of text on the right creates "Editorial Tension."
*   **Do** use the `16` (5.5rem) spacing token for major section breaks. Luxury needs space.
*   **Do** use the Lime (#C6FF34) sparingly. It is a spice, not the main course.

**Don't:**
*   **Don't** use 1px solid black borders. It cheapens the "High-End" feel.
*   **Don't** use standard "Drop Shadows." They create a "Windows 95" depth that conflicts with our modern aesthetic.
*   **Don't** center-align long blocks of text. Keep typography left-aligned for a crisp, professional editorial look.
*   **Don't** mix multiple serif fonts. Stick strictly to Noto Serif for headlines and Manrope for everything else.