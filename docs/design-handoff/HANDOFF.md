# Handoff: Portée — Portfolio de partitions (Android)

## Overview
Portée est une app mobile Android pour un pianiste : bibliothèque de morceaux/partitions, ajout d'un morceau (import PDF/photo), un mode "Jouer" plein écran paysage qui tourne les pages automatiquement en écoutant le jeu, un mode enregistrement avec historique des prises, et des suggestions de morceaux basées sur la progression.

## About the Design Files
The files in this bundle are **design references built in HTML** (a self-contained "Design Component" format used by the design tool) — they show intended look, layout and interaction, not production code to copy directly. The task is to **recreate this design in the target codebase's real environment** (native Android/Kotlin+Jetpack Compose is the natural fit given the brief; React Native or Flutter if that's the existing stack) using its own patterns, navigation and state management — not to embed the HTML.

`Portee.dc.html` is the whole app: one file containing every screen, driven by local component state (no backend). `android-frame.jsx` is only a preview bezel (status bar / gesture nav mockup) — skip it entirely when implementing; use real Android chrome.

## Fidelity
**High-fidelity.** Colors, type, spacing and copy are final and should be recreated pixel-for-pixel using the design tokens below (drawn from the bound "Nocturne" design system).

## Screens / Views

### 1. Bibliothèque (home, tab 1)
- **Purpose**: browse all pieces, search, open one.
- **Layout**: header row (app name "Portée" left, count "N morceaux" right, uppercase 11px), a search input (icon-left), then either a **timeline** layout (default) or a **flat list** layout (a toggle in code, `libraryLayout` prop):
  - Timeline: vertical rail — a 1px divider line offset 25px from the left edge; each row has a 9px accent dot centered on the rail, a small date label (11px, 50% opacity) above the row, then the card content.
  - List: plain rows separated by a 1px divider, no rail/dots.
  - Each row: title (16px/500 heading), composer (13px, 70% opacity), a "Niveau X/5" tag, and (list layout only) a calendar icon + date. A chevron icon sits at the row's right edge, 35% opacity.
- **Empty search state**: "Aucun morceau ne correspond à « {query} »." at 13px, 50% opacity.
- **Components**: search `.input`, `.tag.tag-accent` level badge, row divider `var(--color-divider)`.

### 2. Ajouter (tab 2)
- **Purpose**: create a new piece.
- **Layout**: vertical stack, `space-6` (~17px) gap between sections.
  1. "Importer la partition" label (12px, 70% opacity) + two side-by-side buttons (PDF / Photo, icon above label, outlined). Selecting one highlights its border/text in the accent color and shows "Importé · <filename>" below (12px, accent-300).
  2. Text field "Titre du morceau" (placeholder "Ex. Clair de lune").
  3. Text field "Compositeur" (placeholder "Ex. Claude Debussy").
  4. "Niveau de difficulté" — a 5-option segmented control (1–5), single select.
  5. Full-width primary button "Ajouter à ma bibliothèque" — disabled until title + composer + an import are all set.
- **Behavior**: on submit, prepend the new piece to the library (dated "Aujourd'hui"), reset the form, switch to the Bibliothèque tab.

### 3. Suggestions (tab 3)
- **Purpose**: propose the next pieces to learn, based on progression (one level above pieces already in the library).
- **Layout**: a vertical stack of cards (`.card`), each with a kicker line (10px uppercase, accent-300, e.g. "Étape suivante après la Gymnopédie n°1"), title (17px/500), composer (13px), and a footer row with a "Niveau X/5" tag (neutral) and an "Ajouter" button (accent outline; becomes "Ajouté ✓" and a neutral outline once tapped, and adds the piece to the library).

### 4. Fiche morceau (detail, pushed from a library row)
- **Purpose**: overview of one piece + entry points to Play/Record.
- **Layout**: back header (icon button + title). Below: level tag + "Ajouté le <date>" (11px, 50%), composer (15px, 75% opacity), a 170px score-placeholder panel (diagonal striped pattern, monospace caption "partition" + "N page(s)"), two side-by-side action buttons — "▶ Jouer" (primary) and "● S'enregistrer" (secondary) — then "Historique des interprétations": a list of past takes (date, duration, a horizontal quality bar 0–100% in accent color + numeric label) or "Aucun enregistrement pour l'instant." Ends with a ghost-styled link "Voir des suggestions →" that jumps to the Suggestions tab.

### 5. Mode lecture / Jouer (fullscreen, landscape)
- **Purpose**: reading view that "listens" and turns pages automatically.
- **Layout**: the device rotates to a landscape frame. Main area (flex:1): the score placeholder, large page counter ("N / total", 44px monospace), and a page-progress bar (one segment per page, filled = read). Right rail (150px fixed): back button (top), a pulsing accent dot + "Écoute en cours" while listening, "Démarrer"/"Mettre en pause" primary button, "Recommencer" secondary button, and a caption "L'app écoute ton jeu et tourne la page au bon moment."
- **Behavior**: "Démarrer" starts a timer that auto-advances the page every ~2.6s (tweakable "auto turn speed") until the last page, then shows "Terminé" and disables the toggle until "Recommencer".

### 6. Enregistrement (fullscreen push from detail)
- **Purpose**: record a take, review past takes.
- **Layout**: back header. Centered: a 72px circular record button (dot when idle, square when recording) with a pulsing ring animation while active, a monospace elapsed-time readout (mm:ss), and a hint line. Below: "Reprises" — same recording-row list as the detail screen (date, duration, quality bar/label), newest first, or an empty-state line.
- **Behavior**: tapping starts/stops a 1s-tick timer; stopping appends a new take (randomized quality score for the prototype) to the top of that piece's recordings.

## Interactions & Behavior
- Bottom tab bar (Bibliothèque / Ajouter / Suggestions) is only shown on those 3 root screens; Detail/Play/Record use a back header (or, for Play, an in-place back button since that screen is chromeless/fullscreen).
- Navigation stack is effectively: root tabs → Detail → (Play | Record), with a single-level "back".
- All timers (page-turn simulation, recording clock) must be cleared on navigating away to avoid leaks.
- Search filters the library list live by title or composer (case-insensitive substring).

## State Management
- `pieces`: array of `{ id, title, composer, level (1–5), added (date label), pages, recordings: [{ id, date, duration, quality (0–100) }] }`.
- `view` / navigation stack, `selectedPieceId`.
- `addForm`: `{ title, composer, level, importKind, importName }`.
- `practice`: `{ listening: boolean, currentPage: number, done: boolean }`.
- `record`: `{ active: boolean, elapsedSeconds: number }`.
- `searchQuery`, `addedSuggestionIds`.
- No backend in the prototype — all data is in-memory mock data; a real build needs persistence (local DB) and, for the "listens and turns pages" and "record" features, real microphone/audio-analysis and audio-recording APIs.

## Design Tokens (Nocturne design system)
- **Colors**: background `#161826`, surface `#232532`, text `#e9e9ed`, accent `#9184d9` (mono-accent scheme). Tonal ramps 100–900 for neutral/accent (see the bound design system's `styles.css` for exact hex per step — e.g. `--color-accent-800: #423a6a`, `--color-accent-300: #d2cefd`, `--color-neutral-800: #3f424d`). Divider: `color-mix(in srgb, #e9e9ed 16%, transparent)`.
- **Typography**: Inter for both heading and body, heading weight 500. Scale: h1 42px, body 15px/1.55. In-app sizes used: 23px (app title), 17px (card/dialog titles), 16px (row titles), 13–15px (body/meta), 11–12px (labels/tags/kickers, often uppercase + letter-spacing .08em).
- **Spacing**: density-scaled tokens — space-1 2.8px … space-8 22.4px (0.7× base scale).
- **Radius**: sm 4px, md 8px, lg 14px.
- **Buttons**: outlined, never filled — `.btn-primary` = accent border + accent text; `.btn-secondary` = neutral border; `.btn-ghost` = no border, accent text; disabled = 45% opacity.
- **Tags**: `.tag-accent` = accent-800 bg / accent-100 text; `.tag-neutral` = neutral-800 bg / neutral-100 text.
- **Icons**: Phosphor (regular/bold), inline SVG, `currentColor`.

## Assets
- No photographs. Score previews are placeholders (diagonal-stripe pattern) — real PDF/image rendering of the imported score is a follow-up implementation task, not designed here.
- Icons are inline Phosphor SVGs (folder, image, calendar, search, sparkle, arrow/chevron) — sourced from the bound Nocturne design system's icon set.

## Files
- `Portee.dc.html` — the full design (all 6 screens, states, and tweakable props for library layout, auto-turn speed, recording score format, library density).
- `android-frame.jsx` — preview-only device bezel; not part of the design to implement.
- `screenshots/` — reference captures: `01-bibliotheque-timeline`, `02-ajouter`, `03-suggestions`, `05-fiche-morceau`, `06-mode-lecture-paysage`, `07-enregistrement`.
