#!/usr/bin/env python3
"""Migration Thymeleaf layout — remplacements ciblés sur marqueurs exacts uniquement."""

import re
import sys
from pathlib import Path

TEMPLATES = Path(__file__).resolve().parent.parent / "src/main/resources/templates"

CDN_BLOCK = re.compile(
    r"\n  <script src=\"https://cdn\.jsdelivr\.net/npm/@tailwindcss/browser@4\"></script>\n"
    r"  <style type=\"text/tailwindcss\">.*?</style>",
    re.DOTALL,
)

AUTH_FOOTER = (
    '  <footer class="relative mt-auto border-t border-border/50 bg-bg-card/30 '
    'py-6 text-center text-xs text-muted">\n'
    "    &copy; 2026 GameStore Platform.\n"
    "  </footer>"
)

AUTH_BG_MARKER = "  <!-- Fond dégradé gaming -->"

LAYOUT_BLOCKS = [
    ('<!-- THYMELEAF: th:replace="~{_layout :: navbar}" -->', "header", '  <header th:replace="~{_layout :: navbar}"></header>'),
    ('<!-- th:replace="~{_layout :: navbar}" -->', "header", '  <header th:replace="~{_layout :: navbar}"></header>'),
    ('<!-- th:replace="~{fragments/layout :: navbar}" -->', "header", '  <header th:replace="~{_layout :: navbar}"></header>'),
    ('<!-- THYMELEAF: th:replace="~{_layout :: footer}" -->', "footer", '  <footer th:replace="~{_layout :: footer}"></footer>'),
    ('<!-- th:replace="~{_layout :: footer}" -->', "footer", '  <footer th:replace="~{_layout :: footer}"></footer>'),
    ('<!-- th:replace="~{fragments/layout :: footer}" -->', "footer", '  <footer th:replace="~{_layout :: footer}"></footer>'),
    ('<!-- THYMELEAF: th:replace="~{_layout :: auth-header}" -->', "header", '  <header th:replace="~{_layout :: auth-header}"></header>'),
    ('<!-- THYMELEAF: th:replace="~{_layout :: logo-header}" -->', "header", '  <header th:replace="~{_layout :: logo-header}"></header>'),
    ('<!-- THYMELEAF: th:replace="~{_layout :: minimal-footer}" -->', "footer", '  <footer th:replace="~{_layout :: minimal-footer}"></footer>'),
    ('<!-- THYMELEAF: th:replace="~{_layout :: toasts}" -->', "__toast__", '  <div th:replace="~{_layout :: toasts}"></div>'),
    ('<!-- th:replace="~{_layout :: toasts}" -->', "__toast__", '  <div th:replace="~{_layout :: toasts}"></div>'),
    ('<!-- th:replace="~{fragments/layout :: toasts}" -->', "__toast__", '  <div th:replace="~{_layout :: toasts}"></div>'),
]

SIDEBAR_MARKERS = [
    ('<!-- THYMELEAF: th:replace="~{admin/_sidebar :: sidebar(activePage=\'dashboard\')}" -->', "dashboard"),
    ('<!-- THYMELEAF: th:replace="~{admin/_sidebar :: sidebar(activePage=\'games\')}" -->', "games"),
]


def find_balanced_tag(html: str, start: int, tag: str) -> tuple[int, int] | None:
    open_idx = html.find(f"<{tag}", start)
    if open_idx == -1:
        return None
    close_tag = f"</{tag}>"
    depth = 0
    pos = open_idx
    while pos < len(html):
        next_open = html.find(f"<{tag}", pos + 1)
        next_close = html.find(close_tag, pos)
        if next_close == -1:
            return None
        if next_open != -1 and next_open < next_close:
            depth += 1
            pos = next_open + 1
        else:
            if depth == 0:
                return open_idx, next_close + len(close_tag)
            depth -= 1
            pos = next_close + 1
    return None


def find_toast_container(html: str, start: int) -> tuple[int, int] | None:
    open_idx = html.find('<div data-toast-container', start)
    if open_idx == -1:
        return None
    return find_balanced_tag(html, open_idx, "div")


def replace_marker_block(html: str, marker: str, tag: str, replacement: str) -> str:
    idx = html.find(marker)
    if idx == -1:
        return html
    search_from = idx + len(marker)
    if tag == "__toast__":
        bounds = find_toast_container(html, search_from)
    else:
        bounds = find_balanced_tag(html, search_from, tag)
    if not bounds:
        raise ValueError(f"Élément introuvable après le marqueur: {marker[:60]}")
    start, end = bounds
    return html[:idx] + replacement + html[end:]


def extract_meta(html: str) -> tuple[str, str]:
    title_m = re.search(r"<title>([^<]+)</title>", html)
    title = title_m.group(1).strip() if title_m else "GameStore Platform"
    desc_m = re.search(r'<meta name="description" content="([^"]*)"', html)
    desc = desc_m.group(1) if desc_m else ""
    return title, desc


def strip_cdn(html: str) -> str:
    html = CDN_BLOCK.sub("", html)
    html = html.replace('href="css/', 'href="/css/')
    html = html.replace('href="../css/', 'href="/css/')
    html = re.sub(r'\n  <link rel="stylesheet" href="/css/styles.css">\n  <link rel="stylesheet" href="/css/theme.css">', "", html, count=1)
    return html


def apply_head_thymeleaf(html: str) -> str:
    title, desc = extract_meta(html)
    title_esc = title.replace("'", "''")
    desc_esc = desc.replace("'", "''")
    head_m = re.search(r"<head>.*?</head>", html, re.DOTALL)
    if not head_m:
        raise ValueError("Balise <head> introuvable")
    inner = head_m.group(0)
    inner = strip_cdn(inner)
    inner = re.sub(r"<head>", f'<head th:replace="~{{_layout :: head(\'{title_esc}\', \'{desc_esc}\')}}">', inner, count=1)
    if "/css/styles.css" not in inner:
        inner = inner.replace(
            "</head>",
            '\n  <link rel="stylesheet" href="/css/styles.css">\n  <link rel="stylesheet" href="/css/theme.css">\n</head>',
        )
    html = html[: head_m.start()] + inner + html[head_m.end() :]
    return html


def replace_main_js(html: str) -> str:
    for pattern in (
        '  <script src="js/main.js"></script>\n',
        '  <script src="../js/main.js"></script>\n',
    ):
        if pattern in html:
            return html.replace(pattern, '  <div th:replace="~{_layout :: scripts}"></div>\n', 1)
    return html


def migrate_file(path: Path) -> None:
    original = path.read_text(encoding="utf-8")
    original_lines = original.count("\n") + 1
    html = original

    if 'xmlns:th="http://www.thymeleaf.org"' not in html:
        html = html.replace('<html lang="fr">', '<html lang="fr" xmlns:th="http://www.thymeleaf.org">', 1)

    html = apply_head_thymeleaf(html)

    for marker, tag, replacement in LAYOUT_BLOCKS:
        while marker in html:
            html = replace_marker_block(html, marker, tag, replacement)

    for marker, active in SIDEBAR_MARKERS:
        replacement = f'    <aside th:replace="~{{admin/_sidebar :: sidebar(activePage=\'{active}\')}}"></aside>'
        while marker in html:
            html = replace_marker_block(html, marker, "aside", replacement)

    if AUTH_BG_MARKER in html:
        bounds = find_balanced_tag(html, html.find(AUTH_BG_MARKER) + len(AUTH_BG_MARKER), "div")
        if bounds:
            start, end = bounds
            html = html[: html.find(AUTH_BG_MARKER)] + '  <div th:replace="~{_layout :: auth-background}"></div>\n' + html[end:]

    if AUTH_FOOTER in html:
        html = html.replace(AUTH_FOOTER, '  <footer th:replace="~{_layout :: auth-footer}"></footer>')

    html = replace_main_js(html)

    new_lines = html.count("\n") + 1
    min_lines = max(20, int(original_lines * 0.45))
    if new_lines < min_lines:
        raise ValueError(f"{path.name}: {original_lines} → {new_lines} lignes (perte de contenu suspecte)")
    if "<main" not in html and path.name not in ("admin/index.html", "_layout.html", "admin/_sidebar.html"):
        raise ValueError(f"{path.name}: balise <main> absente après migration")

    path.write_text(html, encoding="utf-8")
    print(f"OK  {path.relative_to(TEMPLATES)}  ({original_lines} -> {new_lines} lignes)")


def main() -> int:
    skip = {"_layout.html", "admin/_sidebar.html", "admin/index.html"}
    files = sorted(TEMPLATES.rglob("*.html"))
    errors = []
    for f in files:
        rel = f.relative_to(TEMPLATES).as_posix()
        if rel in skip:
            continue
        try:
            migrate_file(f)
        except Exception as exc:
            errors.append(f"{rel}: {exc}")
            print(f"ERR {rel}: {exc}", file=sys.stderr)
    if errors:
        print(f"\n{len(errors)} erreur(s).", file=sys.stderr)
        return 1
    print("\nMigration terminée sans perte de contenu détectée.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
