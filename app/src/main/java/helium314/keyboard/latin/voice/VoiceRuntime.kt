/* SPDX-License-Identifier: GPL-3.0-only */
package helium314.keyboard.latin.voice

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Coordinates one real recorder/transcription session; all work remains on-device. */
class VoiceRuntime(private val context: Context, private val listener: Listener) {
    interface Listener {
        fun onRecordingStarted()
        fun onTranscribing()
        fun onTranscript(text: String)
        fun onNoSpeech()
        fun onFailure()
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val recorder = VadRecorder(context)

    fun start(): Boolean {
        val started = recorder.start(scope) { pcm, speechDetected ->
            if (!speechDetected || pcm.isEmpty()) {
                withContext(Dispatchers.Main.immediate) { listener.onNoSpeech() }
            } else {
                try {
                    val transcript = withContext(Dispatchers.Default) {
                        LocalTextPostProcessor.apply(
                            OfflineTranscriber.get(context).transcribe(pcm)
                        )
                    }
                    withContext(Dispatchers.Main.immediate) {
                        if (transcript.isBlank()) listener.onNoSpeech()
                        else listener.onTranscript(transcript)
                    }
                } catch (_: Exception) {
                    withContext(Dispatchers.Main.immediate) { listener.onFailure() }
                }
            }
        }
        if (started) listener.onRecordingStarted()
        else listener.onFailure()
        return started
    }

    fun stop() {
        if (recorder.active) {
            listener.onTranscribing()
            recorder.stop()
        }
    }

    fun cancel() {
        recorder.cancel()
    }
}
