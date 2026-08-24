/* SPDX-License-Identifier: GPL-3.0-only */
package helium314.keyboard.latin.voice

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.k2fsa.sherpa.onnx.SileroVadModelConfig
import com.k2fsa.sherpa.onnx.TenVadModelConfig
import com.k2fsa.sherpa.onnx.Vad
import com.k2fsa.sherpa.onnx.VadModelConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** AudioRecord capture with Silero VAD speech detection and safe release. */
class VadRecorder(private val context: Context) {
    companion object {
        private const val TAG = "CatBoardVad"
        private const val RATE = 16_000
        private const val WINDOW = 512
        private const val MIN_AUDIO_BYTES = RATE * 2 / 4
        private const val VAD_ASSET = "silero_vad.onnx"

        private fun vad(context: Context): Vad {
            val dir = File(context.filesDir, "models/vad").also { it.mkdirs() }
            val model = File(dir, VAD_ASSET)
            if (!model.exists() || model.length() < 100_000L) {
                context.assets.open(VAD_ASSET).use { input ->
                    model.outputStream().use { output -> input.copyTo(output) }
                }
            }
            return Vad(null, VadModelConfig(
                sileroVadModelConfig = SileroVadModelConfig(
                    model = model.absolutePath,
                    threshold = 0.5f,
                    minSilenceDuration = 0.5f,
                    minSpeechDuration = 0.25f,
                    windowSize = WINDOW,
                    maxSpeechDuration = 30.0f
                ),
                tenVadModelConfig = TenVadModelConfig(),
                sampleRate = RATE,
                numThreads = 1,
                provider = "cpu",
                debug = false
            ))
        }
    }

    @Volatile var active = false
        private set
    @Volatile private var stopRequested = false
    private var recorder: AudioRecord? = null
    private var job: Job? = null

    @SuppressLint("MissingPermission")
    fun start(scope: CoroutineScope, onComplete: suspend (ByteArray, Boolean) -> Unit): Boolean {
        if (active) return false
        val bytes = WINDOW * 2
        val bufferSize = AudioRecord.getMinBufferSize(
            RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(bytes * 8)
        val candidate = AudioRecord(
            MediaRecorder.AudioSource.MIC, RATE, AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT, bufferSize
        )
        if (candidate.state != AudioRecord.STATE_INITIALIZED) {
            candidate.release()
            return false
        }
        try {
            candidate.startRecording()
        } catch (failure: RuntimeException) {
            candidate.release()
            return false
        }
        recorder = candidate
        stopRequested = false
        active = true
        job = scope.launch(Dispatchers.IO) {
            val pcm = ByteArrayOutputStream()
            var speechDetected = false
            val vad = try { vad(context) } catch (failure: Exception) {
                Log.e(TAG, "VAD initialization failed", failure)
                null
            }
            try {
                vad?.reset()
                val window = ByteArray(bytes)
                while (isActive && !stopRequested) {
                    val read = candidate.read(window, 0, window.size)
                    if (read <= 0) break
                    pcm.write(window, 0, read)
                    if (read == bytes && vad != null) {
                        val input = ByteBuffer.wrap(window).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
                        val samples = FloatArray(WINDOW) { input.get().toFloat() / 32768f }
                        vad.acceptWaveform(samples)
                        if (!vad.empty()) speechDetected = true
                    }
                }
                vad?.flush()
                if (vad != null && !vad.empty()) speechDetected = true
                if (pcm.size() >= MIN_AUDIO_BYTES) onComplete(pcm.toByteArray(), speechDetected)
                else onComplete(ByteArray(0), false)
            } catch (failure: Exception) {
                Log.e(TAG, "Audio capture failed", failure)
                onComplete(ByteArray(0), false)
            } finally {
                try { vad?.release() } catch (_: Exception) { }
                try { candidate.stop() } catch (_: Exception) { }
                candidate.release()
                recorder = null
                active = false
            }
        }
        return true
    }

    fun stop() {
        if (active) stopRequested = true
    }

    fun cancel() {
        stopRequested = true
        job?.cancel()
        job = null
        recorder?.let {
            try { it.stop() } catch (_: Exception) { }
            it.release()
        }
        recorder = null
        active = false
    }
}
