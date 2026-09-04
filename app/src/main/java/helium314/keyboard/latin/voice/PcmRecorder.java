/*
 * Copyright (C) 2026 CatBoard contributors
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.voice;

import androidx.annotation.NonNull;

/** Records one bounded 16 kHz mono PCM16 microphone session. */
interface PcmRecorder {
    interface Callback {
        void onCaptureComplete(@NonNull Result result);
    }

    final class Result {
        private final byte[] pcm;
        private final boolean successful;

        private Result(@NonNull final byte[] pcm, final boolean successful) {
            this.pcm = pcm;
            this.successful = successful;
        }

        static Result captured(@NonNull final byte[] pcm) {
            return new Result(pcm, true);
        }

        static Result error() {
            return new Result(new byte[0], false);
        }

        @NonNull byte[] getPcm() {
            return pcm;
        }

        boolean isSuccessful() {
            return successful;
        }
    }

    /** Starts a session, returning false if initialization/start failed or one is already active. */
    boolean start(@NonNull Callback callback);

    /** Stops an active session and completes it with the PCM captured so far. */
    void stop();

    /** Abandons an active session without delivering its PCM. */
    void cancel();
}
