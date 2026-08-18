# Privacy and network boundary

Date: 2026-08-18  
Status: architecture target

## Core principle

CatBoard is a keyboard. The default trust model must be stronger than “the developer promises not to upload your typing”.

Preferred design: the **keyboard APK itself has no `android.permission.INTERNET`**.

That gives Android an enforceable boundary: ordinary CatBoard code cannot open network connections.

## Local-only data path

The following should remain inside the keyboard/device by default:

- normal key presses;
- local GigaAM audio processing;
- local transcript before optional cleanup;
- clipboard history;
- personal dictionary/correction data;
- surrounding text from the editor;
- keyboard settings and usage state.

Audio from normal offline dictation is not uploaded.

## Optional online cleanup

Preferred design uses a **separate companion Android package** with network permission.

The keyboard explicitly invokes the companion only for the specific dictated text the user has configured/asked to clean.

Possible IPC:

- explicit bound service or equivalent;
- signature-level permission;
- package/component allowlisting;
- request contains only required cleanup text + mode, not arbitrary editor context.

The exact IPC must be chosen after Android lifecycle testing.

## Data minimization

Do not send implicitly:

- all keyboard keystrokes;
- clipboard history;
- selected/surrounding editor text merely because it is accessible;
- contact data;
- personal dictionary/history;
- app content outside the current dictated fragment;
- audio when only text cleanup is requested.

If a future feature needs additional data, document it and make the requirement explicit before implementation.

## Failure behavior

Network cleanup is optional enrichment, never the owner of the only transcript.

Required behavior:

1. local ASR produces a transcript;
2. local transcript is retained;
3. online cleanup runs with a bounded timeout;
4. on timeout/network/API/auth failure, return to local transcript;
5. do not silently lose user dictation.

## Secrets

API credentials belong to the network-capable component/settings storage, not source control.

Never log:

- API keys/tokens;
- full private dictated text in normal production logs;
- clipboard contents;
- surrounding editor text.

Diagnostic builds should prefer event/status/error-code logging over user-content logging.

## Incognito/sensitive fields

Before public release, define policy for password fields, incognito mode and sensitive editors.

Safe default direction:

- no clipboard capture for sensitive/password content;
- no online cleanup in password fields;
- avoid local learning/history in sensitive contexts;
- make incognito mode disable learning/history/network enrichment as appropriate.

## Verification

For privacy-sensitive PRs verify at minimum:

- merged Android manifest permissions;
- network-capable package boundaries;
- IPC export/permission configuration;
- logs do not contain secrets/private text;
- airplane-mode offline dictation;
- online-cleanup disabled behavior.

If the split-package design proves impractical, do not quietly add `INTERNET` to CatBoard. Record a superseding decision with the tradeoff and evidence first.
