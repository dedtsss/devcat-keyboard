/*
 * Copyright (C) 2026 CatBoard contributors
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.voice;

import android.view.inputmethod.InputConnection;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Owns the internal voice-action state boundary.
 *
 * <p>Owns capture state and retains bounded PCM for the future local recognizer. Recognition does
 * not live here.
 */
public final class VoiceController {
    public enum State {
        IDLE,
        REQUESTING_PERMISSION,
        RECORDING,
        FINALIZING_CAPTURE,
        CAPTURE_READY,
        ERROR
    }

    public interface PermissionResultCallback {
        void onResult(boolean granted);
    }

    public interface Host {
        /** Context used only to initialize the embedded offline recognizer. */
        @Nullable default Context getVoiceContext() { return null; }
        boolean hasMicrophonePermission();
        void requestMicrophonePermission(@NonNull PermissionResultCallback callback);
        @Nullable InputConnection getVoiceInputConnection();
        void onVoiceStateChanged(@NonNull State state);
        void postVoiceCallback(@NonNull Runnable callback);
    }

    private final Host host;
    private final PcmRecorder recorder;
    private final VadSegmenter segmenter;
    private State state = State.IDLE;
    private String recoverableTranscript;
    private byte[] capturedPcm;
    private int permissionRequestGeneration;
    @Nullable private final VoiceRuntime runtime;

    public VoiceController(@NonNull final Host host) {
        this(host, new AndroidPcmRecorder());
    }

    VoiceController(@NonNull final Host host, @NonNull final PcmRecorder recorder) {
        this(host, recorder, VadSegmenter.passthroughAdapter());
    }

    VoiceController(@NonNull final Host host, @NonNull final PcmRecorder recorder,
            @NonNull final VadSegmenter.Adapter vadAdapter) {
        this.host = host;
        this.recorder = recorder;
        this.segmenter = new VadSegmenter(vadAdapter, AndroidPcmRecorder.MAX_CAPTURE_BYTES, 1,
                pcm -> capturedPcm = pcm);
        final Context context = host.getVoiceContext();
        runtime = context == null ? null : new VoiceRuntime(context, new VoiceRuntime.Listener() {
            @Override public void onRecordingStarted() { }
            @Override public void onTranscribing() { setState(State.FINALIZING_CAPTURE); }
            @Override public void onTranscript(@NonNull final String text) { deliverTranscript(text); }
            @Override public void onNoSpeech() { setState(State.IDLE); }
            @Override public void onFailure() { setState(State.ERROR); }
        });
    }

    /** Handles the toolbar microphone action without switching to another IME. */
    public void onVoiceAction() {
        if (state == State.RECORDING) {
            setState(State.FINALIZING_CAPTURE);
            recorder.stop();
            return;
        }
        if (state == State.REQUESTING_PERMISSION || state == State.FINALIZING_CAPTURE) return;
        if (host.hasMicrophonePermission()) {
            startCapture();
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
            startCapture();
        } else {
            setState(State.IDLE);
        }
    }

    private void startCapture() {
        final int generation = ++permissionRequestGeneration;
        capturedPcm = null;
        final boolean started;
        try {
            started = recorder.start(result -> host.postVoiceCallback(
                    () -> onCaptureComplete(generation, result)));
        } catch (RuntimeException failure) {
            setState(State.ERROR);
            return;
        }
        setState(started ? State.RECORDING : State.ERROR);
    }

    private void onCaptureComplete(final int generation, @NonNull final PcmRecorder.Result result) {
        if (generation != permissionRequestGeneration
                || (state != State.RECORDING && state != State.FINALIZING_CAPTURE)) {
            return;
        }
        if (!result.isSuccessful()) {
            setState(State.ERROR);
            return;
        }
        capturedPcm = null;
        segmenter.reset();
        segmenter.accept(result.getPcm());
        segmenter.finish();
        if (runtime != null && capturedPcm != null) runtime.transcribe(capturedPcm);
        else setState(State.CAPTURE_READY);
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
        recorder.cancel();
        capturedPcm = null;
        segmenter.reset();
        setState(State.IDLE);
    }

    /** Releases capture and recognizer resources when the IME is destroyed. */
    public void destroy() {
        ++permissionRequestGeneration;
        recorder.release();
        capturedPcm = null;
        segmenter.reset();
        if (runtime != null) runtime.destroy();
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

    /** Returns the last bounded PCM16 capture without transferring it outside the voice module. */
    @Nullable
    public byte[] getCapturedPcm() {
        return capturedPcm == null ? null : capturedPcm.clone();
    }

    private void setState(@NonNull final State next) {
        if (state == next) return;
        state = next;
        host.onVoiceStateChanged(next);
    }
}
