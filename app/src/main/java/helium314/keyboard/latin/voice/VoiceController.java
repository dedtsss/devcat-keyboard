/*
 * Copyright (C) 2026 CatBoard contributors
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.voice;

import android.view.inputmethod.InputConnection;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Owns the internal voice-action state boundary.
 *
 * <p>Owns capture state and retains bounded PCM for the future local recognizer. Recognition does
 * not live here.
 */
public final class VoiceController {
    static final long CLEANUP_DEADLINE_MS = 2_500L;

    interface CleanupClient {
        @Nullable String clean(@NonNull Context context, @NonNull String transcript,
                @NonNull String mode);
    }
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
    @Nullable private final Context voiceContext;
    private final CleanupClient cleanupClient;
    private final ScheduledExecutorService cleanupExecutor;
    private long cleanupGeneration;
    private boolean cleanupSettled = true;
    private boolean destroyed;
    @Nullable private ScheduledFuture<?> cleanupFallback;

    public VoiceController(@NonNull final Host host) {
        this(host, new AndroidPcmRecorder());
    }

    VoiceController(@NonNull final Host host, @NonNull final PcmRecorder recorder) {
        this(host, recorder, VadSegmenter.passthroughAdapter());
    }

    VoiceController(@NonNull final Host host, @NonNull final PcmRecorder recorder,
            @NonNull final VadSegmenter.Adapter vadAdapter) {
        this(host, recorder, vadAdapter, OnlineCleanupClient::clean);
    }

    VoiceController(@NonNull final Host host, @NonNull final PcmRecorder recorder,
            @NonNull final VadSegmenter.Adapter vadAdapter, @NonNull final CleanupClient cleanupClient) {
        this.host = host;
        this.recorder = recorder;
        this.segmenter = new VadSegmenter(vadAdapter, AndroidPcmRecorder.MAX_CAPTURE_BYTES, 1,
                pcm -> capturedPcm = pcm);
        final Context context = host.getVoiceContext();
        voiceContext = context;
        this.cleanupClient = cleanupClient;
        cleanupExecutor = Executors.newScheduledThreadPool(2, runnable -> {
            final Thread thread = new Thread(runnable, "catboard-online-cleanup");
            thread.setDaemon(true);
            return thread;
        });
        runtime = context == null ? null : new VoiceRuntime(context, new VoiceRuntime.Listener() {
            @Override public void onRecordingStarted() { }
            @Override public void onTranscribing() { setState(State.FINALIZING_CAPTURE); }
            @Override public void onTranscript(@NonNull final String text) { onLocalTranscript(text); }
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
        invalidateCleanup();
        return commitTranscript(transcript);
    }

    void onLocalTranscript(@NonNull final String transcript) {
        if (destroyed) return;
        final Context context = voiceContext;
        if (context == null || !OnlineCleanupPreferences.isEnabled(context)) {
            commitTranscript(transcript);
            return;
        }
        final long generation = ++cleanupGeneration;
        cleanupSettled = false;
        cleanupFallback = cleanupExecutor.schedule(
                () -> host.postVoiceCallback(() -> finishCleanup(generation, transcript, null)),
                CLEANUP_DEADLINE_MS, TimeUnit.MILLISECONDS);
        cleanupExecutor.execute(() -> {
            String cleaned = null;
            try {
                cleaned = cleanupClient.clean(context, transcript,
                        OnlineCleanupPreferences.getMode(context));
            } catch (RuntimeException ignored) {
                // The local transcript is the required fallback for every cleanup failure.
            }
            final String result = cleaned;
            host.postVoiceCallback(() -> finishCleanup(generation, transcript, result));
        });
    }

    private void finishCleanup(final long generation, @NonNull final String localTranscript,
            @Nullable final String cleanedTranscript) {
        if (generation != cleanupGeneration || cleanupSettled) return;
        cleanupSettled = true;
        if (cleanupFallback != null) cleanupFallback.cancel(false);
        final String candidate = cleanedTranscript == null || cleanedTranscript.trim().isEmpty()
                ? localTranscript : cleanedTranscript;
        commitTranscript(candidate);
    }

    private boolean commitTranscript(@Nullable final CharSequence transcript) {
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
        invalidateCleanup();
        recorder.cancel();
        capturedPcm = null;
        segmenter.reset();
        setState(State.IDLE);
    }

    /** Releases capture and recognizer resources when the IME is destroyed. */
    public void destroy() {
        destroyed = true;
        ++permissionRequestGeneration;
        invalidateCleanup();
        recorder.cancel();
        capturedPcm = null;
        segmenter.reset();
        if (runtime != null) runtime.destroy();
        cleanupExecutor.shutdownNow();
        setState(State.IDLE);
    }

    private void invalidateCleanup() {
        ++cleanupGeneration;
        cleanupSettled = true;
        if (cleanupFallback != null) cleanupFallback.cancel(false);
        cleanupFallback = null;
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
