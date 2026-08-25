# Project origins

CatBoard product work was split out of `dedtsss/agent-dispatch` on 2026-08-18 so product decisions, specs and implementation live with the code.

Historical coordination pointers:

- `dedtsss/agent-dispatch#327` — initial integrated HeliBoard + Govorun prototype task;
- `dedtsss/agent-dispatch#328` — decision to create a dedicated product repository;
- `dedtsss/agent-dispatch/docs/memory/heliboard-govorun-keyboard.md` — pre-repository architecture note.

From repository bootstrap onward, **this repository is authoritative for CatBoard product state**.

`agent-dispatch` may keep cross-project coordination links/status, but new CatBoard implementation decisions and Issues belong here.

## Imported Android baseline

- Upstream: `HeliBorg/HeliBoard`
- Revision: `50d13c1bd6c3f4ee6d69644b3d422145cb928503`
- Boundary: imported HeliBoard Android source and its required attribution remain GPL-3.0;
  Apache-2.0 and CC-BY-SA-4.0 notices are retained for the corresponding upstream assets.
- Product adaptation: CatBoard application identity and CI/build harness are maintained in this
  repository; the normal HeliBoard keyboard foundation is preserved.
