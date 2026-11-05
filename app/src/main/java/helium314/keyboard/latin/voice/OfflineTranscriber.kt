/* SPDX-License-Identifier: GPL-3.0-only */
package helium314.keyboard.latin.voice

import android.content.Context
import com.k2fsa.sherpa.onnx.FeatureConfig
import com.k2fsa.sherpa.onnx.OfflineModelConfig
import com.k2fsa.sherpa.onnx.OfflineRecognizer
import com.k2fsa.sherpa.onnx.OfflineRecognizerConfig
import com.k2fsa.sherpa.onnx.OfflineTransducerModelConfig
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** Offline GigaAM v3 RNNT adapter. The native recognizer is process-local. */
class OfflineTranscriber private constructor(private val recognizer: OfflineRecognizer) {
    companion object {
        @Volatile private var instance: OfflineTranscriber? = null

        @JvmStatic
        @Synchronized
        fun get(context: Context): OfflineTranscriber {
            instance?.let { return it }
            val dir = GigaAmModel.ensureInstalled(context)
            val model = OfflineModelConfig(
                transducer = OfflineTransducerModelConfig(
                    encoder = FilePath(dir, GigaAmModel.ENCODER),
                    decoder = FilePath(dir, GigaAmModel.DECODER),
                    joiner = FilePath(dir, GigaAmModel.JOINER)
                ),
                tokens = FilePath(dir, GigaAmModel.TOKENS),
                numThreads = 2,
                provider = "cpu",
                modelType = "nemo_transducer"
            )
            val config = OfflineRecognizerConfig(
                featConfig = FeatureConfig(sampleRate = 16000, featureDim = 80),
                modelConfig = model
            )
            return OfflineTranscriber(OfflineRecognizer(null, config)).also { instance = it }
        }

        @JvmStatic
        @Synchronized
        fun release() {
            instance?.recognizer?.release()
            instance = null
        }

        private fun FilePath(dir: java.io.File, name: String): String =
            java.io.File(dir, name).absolutePath
    }

    @Synchronized
    fun transcribe(pcm: ByteArray): String {
        if (pcm.isEmpty()) return ""
        val samples = ByteBuffer.wrap(pcm).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
        val waveform = FloatArray(samples.remaining()) { samples.get().toFloat() / 32768f }
        val stream = recognizer.createStream()
        return try {
            stream.acceptWaveform(waveform, sampleRate = 16000)
            recognizer.decode(stream)
            recognizer.getResult(stream).text.trim()
        } finally {
            stream.release()
        }
    }
}
