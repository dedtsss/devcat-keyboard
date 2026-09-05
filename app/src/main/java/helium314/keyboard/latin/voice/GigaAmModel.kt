/* SPDX-License-Identifier: GPL-3.0-only */
package helium314.keyboard.latin.voice

import android.content.Context
import java.io.File

/** Pinned GigaAM v3 E2E RNNT assets, copied from APK assets for sherpa-onnx. */
object GigaAmModel {
    const val ENCODER = "gigaam_v3_e2e_rnnt_encoder_int8.onnx"
    const val DECODER = "gigaam_v3_e2e_rnnt_decoder.onnx"
    const val JOINER = "gigaam_v3_e2e_rnnt_joint.onnx"
    const val TOKENS = "gigaam_v3_e2e_rnnt_tokens.txt"
    private const val ASSET_DIR = "models/gigaam-v3"

    private val files = arrayOf(ENCODER, DECODER, JOINER, TOKENS)

    fun directory(context: Context): File =
        File(context.filesDir, "models/gigaam-v3").also { it.mkdirs() }

    fun ensureInstalled(context: Context): File {
        val directory = directory(context)
        for (name in files) {
            val target = File(directory, name)
            if (target.exists() && target.length() > 0L) continue
            context.assets.open("$ASSET_DIR/$name").use { input ->
                target.outputStream().use { output -> input.copyTo(output) }
            }
        }
        return directory
    }
}
