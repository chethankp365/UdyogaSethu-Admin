# UdyogaSethu Admin — "SETU" Design System & Motion Framework

The design concept **"SETU" (The Bridge)** expresses connectivity between job seekers, employers, farmers, and consumers.

---

## 1. Color Palette Tokens

- **Deep Ink Navy** (`--us-bg-root`: `#0A1220`): Main workspace backdrop in dark mode.
- **Layered Surfaces** (`--us-surface-1`: `#0F1A2E`, `--us-surface-2`: `#14233D`): Elevated cards and sidebar layers.
- **Primary Accent** (`--us-primary`: `#22C55E`): Emerald green from the logo, paired with `--us-primary-glow` (`rgba(34, 197, 94, 0.25)`).
- **Secondary Accent** (`--us-setu-blue`: `#0284C7`): Vibrant suspension bridge blue from the logo.
- **Warning Amber** (`--us-amber`: `#FF8A00`): Udyoga warm amber, used in DEMO ribbons and attention chips.
- **Light Workspace Neutral** (`#F6F8FB`): Soft neutral background with white cards for light mode.

---

## 2. Typography

- **Headings**: *Plus Jakarta Sans* / *Sora* (Geometric display font).
- **Body & Data Grids**: *Inter* (High-legibility interface font).
- **Identifiers & Currency**: *JetBrains Mono* (Fixed-width numeric alignment).

---

## 3. Motion System & Tokens

- **Micro Transitions** (`--us-duration-micro`: `140ms`): Button clicks, row hover highlights, icon bounce.
- **Standard Transitions** (`--us-duration-std`: `260ms`): Drawer slide-overs, card hover lifts, modal appearances.
- **Large Transitions** (`--us-duration-lg`: `450ms`): Full page route shifts, living bridge node animations.
- **Easing Curve**: `cubic-bezier(0.16, 1, 0.3, 1)` (Decelerating smooth curve).
- **Reduced Motion Support**:
  - Respects `@media (prefers-reduced-motion: reduce)`.
  - In-app toggle on `SettingsView` immediately disables animations by setting `.reduced-motion` on `document.body`.

---

## 4. Signature Visual Components

1. **Living Bridge Dynamic Background**: HTML5 Canvas rendering drifting connected nodes and arcs on the login screen.
2. **Glassmorphism Cards**: Translucent surface cards with subtle border highlight and hover glow.
3. **Command Palette (`Ctrl+K`)**: Modal search index with keyboard jump to any module.
4. **Striped DEMO Ribbon**: Amber striped badge clearly distinguishing simulation data.
5. **Apache ECharts Dynamic Visualization**: Custom Java wrapper for dark-themed, responsive charts.
