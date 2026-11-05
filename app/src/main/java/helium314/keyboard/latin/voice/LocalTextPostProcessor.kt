/* SPDX-License-Identifier: GPL-3.0-only */
package helium314.keyboard.latin.voice

/** Deterministic local cleanup; intentionally has no network or service dependency. */
object LocalTextPostProcessor {
    fun apply(text: String): String = text
        .replace(Regex("\\s+"), " ")
        .trim()
}
