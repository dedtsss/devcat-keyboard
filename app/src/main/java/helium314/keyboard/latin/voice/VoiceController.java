/*
 * Copyright (C) 2026 CatBoard contributors
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.voice;

import android.view.inputmethod.InputConnection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Owns the internal voice-action state boundary.
 *
 * <p>Recording and recognition deliberately do not live here yet. Stage 2A only establishes the
 * permission flow and the seam through which a later local recognizer can deliver a transcript.
 */
public final class VoiceController {
    public enum State {
        IDLE,
        REQUESTING_PERMISSION,
        READY_PLACEHOLDER,
        ERROR
    }

    public interface PermissionResultCallback {
        void onResult(boolean granted);
    }

    public interface Host {
        boolean hasMicrophonePermission();
        void requestMicrophonePermission(@NonNull PermissionResultCallback callback);
        @Nullable InputConnection getVoiceInputConnection();
        void onVoiceStateChanged(@NonNull State state);
    }

    private final Host host;
    private State state = State.IDLE;
    private String recoverableTranscript;
    private int permissionRequestGeneration;

    public VoiceController(@NonNull final Host host) {
        this.host = host;
    }

    /** Handles the toolbar microphone action without switching to another IME. */
    public void onVoiceAction() {
        if (state == State.REQUESTING_PERMISSION) return;
        if (host.hasMicrophonePermission()) {
            setState(State.READY_PLACEHOLDER);
            return;
        }

        final int generation = ++permissionRequestGeneration;
        setState(State.REQUESTING_PERMISSION);
        try {
            host.requestMicrophonePermission(granted ->
                    onPermissionResult(generation, granted));
        } catch (RuntimeException failure) {
            if (generation == permissionRequestGeneration) setState(State.ERROR);
        }
    }

    private void onPermissionResult(final int generation, final boolean granted) {
        if (generation != permissionRequestGeneration || state != State.REQUESTING_PERMISSION) {
            return;
        }
        if (granted && host.hasMicrophonePermission()) {
            // This means only that the Stage 2A seam is ready. No recorder or recognizer exists.
            setState(State.READY_PLACEHOLDER);
        } else {
            setState(State.IDLE);
        }
    }

    /**
     * Future local-ASR delivery seam. Failed commits keep the transcript available for recovery.
     */
    public boolean deliverTranscript(@Nullable final CharSequence transcript) {
        if (transcript == null || transcript.toString().trim().isEmpty()) return false;
        recoverableTranscript = transcript.toString();
        final InputConnection connection = host.getVoiceInputConnection();
        if (connection == null) {
            setState(State.ERROR);
            return false;
        }
        try {
            if (!connection.commitText(recoverableTranscript, 1)) {
                setState(State.ERROR);
                return false;
            }
        } catch (RuntimeException failure) {
            setState(State.ERROR);
            return false;
        }
        recoverableTranscript = null;
        setState(State.IDLE);
        return true;
    }

    public void cancel() {
        ++permissionRequestGeneration;
        setState(State.IDLE);
    }

    @NonNull
    public State getState() {
        return state;
    }

    @Nullable
    public String getRecoverableTranscript() {
        return recoverableTranscript;
    }

    private void setState(@NonNull final State next) {
        if (state == next) return;
        state = next;
        host.onVoiceStateChanged(next);
    }
}
