// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin.voice

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VadSegmenterTest {
    @Test fun speechStartsBufferAndTrailingSilenceHandsOffOnce() {
        val utterances = mutableListOf<ByteArray>()
        val segmenter = VadSegmenter(FakeVad(true, false), 32, 2, utterances::add)

        segmenter.accept(byteArrayOf(1, 2))
        segmenter.accept(byteArrayOf(3, 4))
        segmenter.accept(byteArrayOf(0, 6))
        segmenter.accept(byteArrayOf(0, 8))

        assertEquals(1, utterances.size)
        assertTrue(byteArrayOf(1, 2, 3, 4, 0, 6).contentEquals(utterances.single()))
    }

    @Test fun utteranceIsCompletedAtByteLimit() {
        val utterances = mutableListOf<ByteArray>()
        val segmenter = VadSegmenter(FakeVad(true), 4, 3, utterances::add)

        segmenter.accept(byteArrayOf(1, 2, 3))
        segmenter.accept(byteArrayOf(4, 5, 6))

        assertEquals(1, utterances.size)
        assertTrue(byteArrayOf(1, 2, 3, 4).contentEquals(utterances.single()))
    }

    @Test fun resetDiscardsIncompletePcmBeforeNextUtterance() {
        val utterances = mutableListOf<ByteArray>()
        val segmenter = VadSegmenter(FakeVad(true), 32, 2, utterances::add)

        segmenter.accept(byteArrayOf(9, 9))
        segmenter.reset()
        segmenter.accept(byteArrayOf(1, 2))
        segmenter.finish()

        assertEquals(1, utterances.size)
        assertTrue(byteArrayOf(1, 2).contentEquals(utterances.single()))
    }

    @Test fun finishHandsOffAndLeavesSegmenterEmpty() {
        val utterances = mutableListOf<ByteArray>()
        val segmenter = VadSegmenter(FakeVad(true), 32, 2, utterances::add)

        segmenter.accept(byteArrayOf(1, 2, 3))
        segmenter.finish()
        segmenter.finish()

        assertEquals(1, utterances.size)
        assertTrue(byteArrayOf(1, 2, 3).contentEquals(utterances.single()))
    }

    private class FakeVad(private val speech: Boolean, private val silence: Boolean = speech) :
        VadSegmenter.Adapter {
        override fun isSpeech(pcm: ByteArray, offset: Int, length: Int) =
            if (pcm[offset].toInt() == 0) silence else speech
    }
}
