/*
 * Copyright (C) 2026 CatBoard contributors
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.voice;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;

/**
 * Turns PCM frames into bounded utterances without depending on a VAD runtime.
 *
 * <p>The adapter receives one complete PCM frame at a time. Once speech starts, frames are
 * retained until the configured trailing-silence threshold is reached or the byte limit is hit.
 * Calling {@link #reset()} discards any incomplete utterance and makes the segmenter reusable.
 */
final class VadSegmenter {
    interface Adapter {
        boolean isSpeech(@NonNull byte[] pcm, int offset, int length);
    }

    interface Callback {
        void onUtterance(@NonNull byte[] pcm);
    }

    private final Adapter adapter;
    private final int maxUtteranceBytes;
    private final int endSilenceFrames;
    private final ByteArrayOutputStream utterance = new ByteArrayOutputStream();
    private Callback callback;
    private int silentFrames;
    private boolean inSpeech;

    VadSegmenter(@NonNull final Adapter adapter, final int maxUtteranceBytes,
            final int endSilenceFrames, @NonNull final Callback callback) {
        if (maxUtteranceBytes <= 0) throw new IllegalArgumentException("maxUtteranceBytes");
        if (endSilenceFrames <= 0) throw new IllegalArgumentException("endSilenceFrames");
        this.adapter = adapter;
        this.maxUtteranceBytes = maxUtteranceBytes;
        this.endSilenceFrames = endSilenceFrames;
        this.callback = callback;
    }

    void accept(@NonNull final byte[] pcm, final int offset, final int length) {
        if (offset < 0 || length < 0 || offset > pcm.length - length) {
            throw new IndexOutOfBoundsException("PCM frame outside buffer");
        }
        if (length == 0) return;

        if (adapter.isSpeech(pcm, offset, length)) {
            if (!inSpeech) {
                inSpeech = true;
                silentFrames = 0;
            }
            append(pcm, offset, length);
            silentFrames = 0;
            return;
        }
        if (!inSpeech) return;

        silentFrames++;
        if (silentFrames >= endSilenceFrames) {
            finishUtterance();
        } else {
            append(pcm, offset, length);
        }
    }

    void accept(@NonNull final byte[] pcm) {
        accept(pcm, 0, pcm.length);
    }

    /** Completes the current utterance, if any, without retaining it for the next session. */
    void finish() {
        if (inSpeech) finishUtterance();
    }

    /** Discards all buffered PCM, including an utterance that has not reached speech end. */
    void reset() {
        utterance.reset();
        silentFrames = 0;
        inSpeech = false;
    }

    private void append(@NonNull final byte[] pcm, final int offset, final int length) {
        final int remaining = maxUtteranceBytes - utterance.size();
        if (remaining <= 0) {
            finishUtterance();
            return;
        }
        utterance.write(pcm, offset, Math.min(length, remaining));
        if (utterance.size() == maxUtteranceBytes) finishUtterance();
    }

    private void finishUtterance() {
        final byte[] completed = utterance.toByteArray();
        reset();
        if (completed.length > 0) callback.onUtterance(completed);
    }

    static Adapter passthroughAdapter() {
        return (pcm, offset, length) -> true;
    }
}
