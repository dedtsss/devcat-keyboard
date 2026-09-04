// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin.voice

import android.media.AudioFormat
import android.view.inputmethod.InputConnection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class VoiceControllerTest {
    @Test fun permissionDenialReturnsToIdle() {
        val host = FakeHost()
        val controller = VoiceController(host)

        controller.onVoiceAction()
        assertEquals(VoiceController.State.REQUESTING_PERMISSION, controller.state)
        host.permissionCallback!!.onResult(false)

        assertEquals(VoiceController.State.IDLE, controller.state)
    }

    @Test fun permissionGrantStartsCapture() {
        val host = FakeHost()
        val recorder = FakeRecorder()
        val controller = VoiceController(host, recorder)

        controller.onVoiceAction()
        host.permissionGranted = true
        host.permissionCallback!!.onResult(true)

        assertEquals(VoiceController.State.RECORDING, controller.state)
        assertEquals(1, recorder.startCount)
    }

    @Test fun secondTapStopsAndReturnsCapturedPcm() {
        val host = FakeHost(permissionGranted = true)
        val recorder = FakeRecorder()
        val controller = VoiceController(host, recorder)

        controller.onVoiceAction()
        controller.onVoiceAction()
        assertEquals(VoiceController.State.FINALIZING_CAPTURE, controller.state)
        assertEquals(1, recorder.stopCount)

        recorder.complete(byteArrayOf(1, 2, 3))
        assertEquals(VoiceController.State.CAPTURE_READY, controller.state)
        assertTrue(byteArrayOf(1, 2, 3).contentEquals(controller.capturedPcm))
    }

    @Test fun repeatedTapWhileFinalizingCannotStartOverlappingSession() {
        val host = FakeHost(permissionGranted = true)
        val recorder = FakeRecorder()
        val controller = VoiceController(host, recorder)

        controller.onVoiceAction()
        controller.onVoiceAction()
        controller.onVoiceAction()

        assertEquals(1, recorder.startCount)
        assertEquals(1, recorder.stopCount)
    }

    @Test fun captureStartFailureMovesToError() {
        val host = FakeHost(permissionGranted = true)
        val controller = VoiceController(host, FakeRecorder(startSucceeds = false))

        controller.onVoiceAction()

        assertEquals(VoiceController.State.ERROR, controller.state)
    }

    @Test fun captureFailureMovesToError() {
        val host = FakeHost(permissionGranted = true)
        val recorder = FakeRecorder()
        val controller = VoiceController(host, recorder)

        controller.onVoiceAction()
        recorder.fail()

        assertEquals(VoiceController.State.ERROR, controller.state)
    }

    @Test fun cancelReleasesCaptureAndIgnoresLateResult() {
        val host = FakeHost(permissionGranted = true)
        val recorder = FakeRecorder()
        val controller = VoiceController(host, recorder)

        controller.onVoiceAction()
        val callback = recorder.callback!!
        controller.cancel()
        callback.onCaptureComplete(PcmRecorder.Result.captured(byteArrayOf(7)))

        assertEquals(1, recorder.cancelCount)
        assertEquals(VoiceController.State.IDLE, controller.state)
        assertEquals(null, controller.capturedPcm)
    }

    @Test fun audioFormatAndCaptureAreBoundedForOfflinePipeline() {
        assertEquals(16_000, AndroidPcmRecorder.SAMPLE_RATE_HZ)
        assertEquals(AudioFormat.CHANNEL_IN_MONO, AndroidPcmRecorder.CHANNEL_CONFIG)
        assertEquals(AudioFormat.ENCODING_PCM_16BIT, AndroidPcmRecorder.AUDIO_FORMAT)
        assertEquals(1_920_000, AndroidPcmRecorder.MAX_CAPTURE_BYTES)
    }

    @Test fun cancelledPermissionResultCannotReactivateController() {
        val host = FakeHost()
        val controller = VoiceController(host)

        controller.onVoiceAction()
        val callback = host.permissionCallback!!
        controller.cancel()
        host.permissionGranted = true
        callback.onResult(true)

        assertEquals(VoiceController.State.IDLE, controller.state)
    }

    @Test fun permissionLaunchFailureMovesToError() {
        val host = FakeHost(permissionRequestFailure = true)
        val controller = VoiceController(host)

        controller.onVoiceAction()

        assertEquals(VoiceController.State.ERROR, controller.state)
    }

    @Test fun repeatedTapWhileRequestingDoesNotLaunchAgain() {
        val host = FakeHost()
        val controller = VoiceController(host)

        controller.onVoiceAction()
        controller.onVoiceAction()

        assertEquals(1, host.permissionRequestCount)
        assertEquals(VoiceController.State.REQUESTING_PERMISSION, controller.state)
    }

    @Test fun transcriptIsRetainedWhenEditorRejectsIt() {
        val connection = mock(InputConnection::class.java)
        `when`(connection.commitText("local transcript", 1)).thenReturn(false)
        val host = FakeHost(connection = connection)
        val controller = VoiceController(host)

        assertFalse(controller.deliverTranscript("local transcript"))
        assertEquals("local transcript", controller.recoverableTranscript)
        assertEquals(VoiceController.State.ERROR, controller.state)
    }

    @Test fun transcriptCommitReturnsControllerToIdle() {
        val connection = mock(InputConnection::class.java)
        `when`(connection.commitText("local transcript", 1)).thenReturn(true)
        val host = FakeHost(connection = connection)
        val controller = VoiceController(host)

        assertTrue(controller.deliverTranscript("local transcript"))
        assertEquals(null, controller.recoverableTranscript)
        assertEquals(VoiceController.State.IDLE, controller.state)
    }

    private class FakeHost(
        var permissionGranted: Boolean = false,
        private val connection: InputConnection? = null,
        private val permissionRequestFailure: Boolean = false,
    ) : VoiceController.Host {
        var permissionCallback: VoiceController.PermissionResultCallback? = null
        var permissionRequestCount = 0
        val states = mutableListOf<VoiceController.State>()

        override fun hasMicrophonePermission() = permissionGranted

        override fun requestMicrophonePermission(callback: VoiceController.PermissionResultCallback) {
            permissionRequestCount++
            if (permissionRequestFailure) throw IllegalStateException("permission bridge unavailable")
            permissionCallback = callback
        }

        override fun getVoiceInputConnection() = connection

        override fun onVoiceStateChanged(state: VoiceController.State) {
            states += state
        }

        override fun postVoiceCallback(callback: Runnable) = callback.run()
    }

    private class FakeRecorder(private val startSucceeds: Boolean = true) : PcmRecorder {
        var callback: PcmRecorder.Callback? = null
        var startCount = 0
        var stopCount = 0
        var cancelCount = 0

        override fun start(callback: PcmRecorder.Callback): Boolean {
            startCount++
            this.callback = callback
            return startSucceeds
        }

        override fun stop() { stopCount++ }

        override fun cancel() { cancelCount++ }

        fun complete(pcm: ByteArray) = callback!!.onCaptureComplete(PcmRecorder.Result.captured(pcm))

        fun fail() = callback!!.onCaptureComplete(PcmRecorder.Result.error())
    }
}
