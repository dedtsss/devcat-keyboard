# Secrets Policy Standard

## Rule

Never commit secrets, tokens, credentials, cookies, private keys or production environment files.

## Details

- Follow the safety and public-template rules in `AGENTS.md`.
- Treat `.env`, dotenvx files, API keys and generated credentials as sensitive unless explicitly proven safe.
- Use examples or placeholder values for documentation.
- If a task requires secret setup, document where the user should configure it without exposing the value in the repository.
