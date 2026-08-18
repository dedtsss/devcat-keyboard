## Summary

What changed and why?

## Issue / spec

- Issue:
- Spec (if applicable):

## Changed areas

- [ ] Keyboard/UI
- [ ] Voice/ASR
- [ ] Online cleanup/network
- [ ] Clipboard
- [ ] Build/CI/dependencies
- [ ] Docs only
- [ ] Other

## Verification

Unified check:

```bash
./scripts/check.sh
```

Result:

- [ ] Passed
- [ ] Failed — explained below
- [ ] Not run — reason below

Additional manual/device checks:

- Device / Android:
- What was tested:
- What was not tested:

## Privacy / permissions

- [ ] No new permissions/network behavior
- [ ] Permission/network behavior changed and is described below
- [ ] No secrets/private user content added to logs or repository

If voice/network changed, state explicitly:

- local dictation in airplane mode tested: yes/no/not applicable;
- keyboard APK `INTERNET` permission changed: yes/no/not applicable;
- transcript fallback/error preservation checked: yes/no/not applicable.

## Upstream / licenses

For copied/upstream-derived code or binaries:

- source/revision:
- license/attribution checked:
- dependency/model version/hash where relevant:

## Risks / remaining boundaries

- 

## Durable handoff

Update only where facts changed:

- [ ] `docs/status.md`
- [ ] `docs/decisions.md`
- [ ] `docs/ideas.md`
- [ ] relevant spec/result
- [ ] no durable-doc update needed

## Merge / release

This PR does not authorize merge, release, publishing or visibility changes by itself.
