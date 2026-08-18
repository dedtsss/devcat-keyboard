# Clipboard direction

Date: 2026-08-18

## Decision boundary

CatBoard does not build a new clipboard subsystem for MVP.

HeliBoard already provides the useful foundation: clipboard history, persistence, pinning/retention, dedicated history UI and current support for non-text content. Reuse it first.

## Product goal

After voice input is stable, improve the clipboard only where daily usage shows real friction.

Likely useful improvements:

- fast search/filter;
- easier pinned/favorites handling;
- quicker item actions;
- better long-text preview;
- optional snippets/templates;
- grouping only if real usage justifies the extra UI.

## Privacy

Clipboard data is sensitive.

Rules:

- never send clipboard history to online cleanup implicitly;
- respect Android sensitive-clipboard metadata/password fields;
- online features operate on explicitly selected/created content only;
- avoid verbose clipboard-content logging;
- keep cleanup/retention controls visible and predictable.

## Engineering approach

Before implementing changes:

1. use stock HeliBoard clipboard in the first CatBoard builds;
2. record concrete pain points;
3. inspect upstream Issues/PRs before adding custom behavior;
4. make the smallest change that fixes the observed problem;
5. prefer upstreamable generic fixes when practical.

Clipboard work must not block integrated voice MVP.
