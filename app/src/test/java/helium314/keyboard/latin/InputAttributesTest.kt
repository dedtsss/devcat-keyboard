// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin

import android.text.InputType
import android.view.inputmethod.EditorInfo
import helium314.keyboard.latin.common.Constants.ImeOption.NO_MICROPHONE
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class InputAttributesTest {
    @Test fun ordinaryTextShowsInternalVoiceWithoutShortcutIme() {
        assertTrue(attributes(InputType.TYPE_CLASS_TEXT).mShouldShowVoiceInputKey)
    }

    @Test fun passwordHidesVoice() {
        assertFalse(attributes(
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
        ).mShouldShowVoiceInputKey)
    }

    @Test fun emailHidesVoice() {
        assertFalse(attributes(
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
        ).mShouldShowVoiceInputKey)
    }

    @Test fun privateNoMicrophoneOptionHidesVoice() {
        assertFalse(attributes(
            InputType.TYPE_CLASS_TEXT,
            privateImeOptions = NO_MICROPHONE,
        ).mShouldShowVoiceInputKey)
    }

    private fun attributes(inputType: Int, privateImeOptions: String? = null): InputAttributes {
        val editorInfo = EditorInfo().apply {
            this.inputType = inputType
            this.privateImeOptions = privateImeOptions
        }
        return InputAttributes(editorInfo, false, null)
    }
}
