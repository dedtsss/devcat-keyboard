// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin.voice

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

    @Test fun permissionGrantExposesPlaceholderReadinessOnly() {
        val host = FakeHost()
        val controller = VoiceController(host)

        controller.onVoiceAction()
        host.permissionGranted = true
        host.permissionCallback!!.onResult(true)

        assertEquals(VoiceController.State.READY_PLACEHOLDER, controller.state)
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
    }
}
