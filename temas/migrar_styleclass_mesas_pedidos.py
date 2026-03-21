#!/usr/bin/env python3
"""
Migracion controlada de styleClass para VistaMesas.fxml y VistaPedidos.fxml.

Modos:
- alias   : conserva clases actuales y agrega clases nuevas (recomendado)
- replace : reemplaza clases actuales por clases nuevas

Uso rapido:
  python3 temas/migrar_styleclass_mesas_pedidos.py --dry-run
  python3 temas/migrar_styleclass_mesas_pedidos.py --apply --mode alias
"""

from __future__ import annotations

import argparse
import difflib
import re
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
VIEWS_DIR = ROOT / "src/main/resources/com/mosqueteros/proyecto_restaurante/view"

TARGET_FILES = [
    VIEWS_DIR / "VistaMesas.fxml",
    VIEWS_DIR / "VistaPedidos.fxml",
]

# old_class -> [new_class_1, new_class_2, ...]
CLASS_MAP = {
    # layout
    "root-container": ["layout-root"],
    "main-card": ["panel-card"],
    "footer-bar": ["layout-footer"],
    # headers/text
    "titulo-texto": ["panel-card__title"],
    "subtitulo-seccion": ["panel-card__subtitle"],
    # form
    "input-label": ["form__label"],
    "input-wrapper": ["form__control-wrap"],
    "input-field": ["input"],
    "search-field": ["input", "input--search"],
    "form-scroll": ["form__body"],
    "form-footer": ["form__actions"],
    "form-mensaje": ["form__status"],
    "field-error": ["form__error"],
    "field-icon-left": ["form__icon", "form__icon--left"],
    # table
    "mesas-table": ["table", "table--data"],
    "table-header-row": ["table__head"],
    "col-header": ["table__col-title"],
    "empty-state": ["state-empty"],
    "empty-state-container": ["state-empty__container"],
    "placeholder-text": ["state-empty__title"],
    "placeholder-subtext": ["state-empty__subtitle"],
    # buttons
    "btn-raised-primary": ["btn", "btn--primary"],
    "btn-outline-primary": ["btn", "btn--outline-primary"],
    "btn-outline-secondary": ["btn", "btn--outline-secondary"],
    "btn-outline-danger": ["btn", "btn--outline-danger"],
    "btn-outline-primary-form": ["btn", "btn--outline-primary"],
    "btn-outline-danger-form": ["btn", "btn--outline-danger"],
    "btn-buscar-raised": ["btn", "btn--primary", "btn--search"],
    "btn-limpiar-flat": ["btn", "btn--ghost"],
}


STYLECLASS_ATTR_RE = re.compile(r'styleClass\s*=\s*"([^"]*)"')


def split_classes(raw: str) -> list[str]:
    return [t for t in re.split(r"[\s,]+", raw.strip()) if t]


def choose_separator(raw: str) -> str:
    return "," if "," in raw else " "


def dedupe_keep_order(items: list[str]) -> list[str]:
    seen = set()
    out = []
    for item in items:
        if item not in seen:
            seen.add(item)
            out.append(item)
    return out


def migrate_tokens(tokens: list[str], mode: str) -> tuple[list[str], int]:
    changes = 0
    if mode == "alias":
        out = list(tokens)
        for tok in tokens:
            mapped = CLASS_MAP.get(tok, [])
            for m in mapped:
                if m not in out:
                    out.append(m)
                    changes += 1
        return out, changes

    # replace mode
    out = []
    for tok in tokens:
        mapped = CLASS_MAP.get(tok)
        if mapped:
            out.extend(mapped)
            changes += 1
        else:
            out.append(tok)
    out = dedupe_keep_order(out)
    return out, changes


def transform_content(content: str, mode: str) -> tuple[str, int, int]:
    total_attrs = 0
    total_changes = 0

    def repl(match: re.Match[str]) -> str:
        nonlocal total_attrs, total_changes
        total_attrs += 1
        raw = match.group(1)
        sep = choose_separator(raw)
        tokens = split_classes(raw)
        if not tokens:
            return match.group(0)

        migrated, changes = migrate_tokens(tokens, mode)
        migrated = dedupe_keep_order(migrated)
        total_changes += changes

        if migrated == tokens:
            return match.group(0)

        return f'styleClass="{sep.join(migrated)}"'

    new_content = STYLECLASS_ATTR_RE.sub(repl, content)
    return new_content, total_attrs, total_changes


def main() -> int:
    parser = argparse.ArgumentParser(description="Migrar styleClass en VistaMesas/VistaPedidos")
    parser.add_argument("--mode", choices=["alias", "replace"], default="alias")
    parser.add_argument("--apply", action="store_true", help="Escribe cambios en archivos")
    parser.add_argument("--dry-run", action="store_true", help="Muestra diff sin escribir")
    args = parser.parse_args()

    do_apply = args.apply and not args.dry_run
    total_file_changes = 0

    print(f"Mode: {args.mode}")
    print(f"Apply: {do_apply}")

    for file_path in TARGET_FILES:
        if not file_path.exists():
            print(f"[WARN] No existe: {file_path}")
            continue

        old = file_path.read_text(encoding="utf-8")
        new, attrs, changes = transform_content(old, args.mode)

        if old == new:
            print(f"[OK] Sin cambios: {file_path.name} (styleClass attrs: {attrs})")
            continue

        total_file_changes += 1
        print(f"[CHANGE] {file_path.name} | attrs: {attrs} | cambios: {changes}")

        if args.dry_run or not do_apply:
            diff = difflib.unified_diff(
                old.splitlines(),
                new.splitlines(),
                fromfile=str(file_path),
                tofile=str(file_path),
                lineterm="",
            )
            print("\n".join(diff))

        if do_apply:
            file_path.write_text(new, encoding="utf-8")

    print(f"\nArchivos modificados: {total_file_changes}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
