/*
 * Copyright (C) 2026 CatBoard contributors
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.voice;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.view.inputmethod.InputConnection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import helium314.keyboard.latin.R;

/** Owns the internal voice action lifecycle and the embedded offline runtime. */
public final class VoiceController {
    public enum State { IDLE, PERMISSION_REQUIRED, READY, RECORDING, TRANSCRIBING, ERROR }

    public interface Host {
        @NonNull Context getVoiceContext();
        @Nullable InputConnection getVoiceInputConnection();
        void onVoiceStateChanged(@NonNull State state);
        void onVoiceMessage(@StringRes int messageRes);
    }

    private final Host host;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private State state = State.IDLE;
    private volatile boolean routeActive;
    private String recoverableTranscript;
    private final VoiceRuntime runtime;

    public VoiceController(@NonNull final Host host) {
        this.host = host;
        runtime = new VoiceRuntime(host.getVoiceContext(), new VoiceRuntime.Listener() {
            @Override public void onRecordingStarted() {
                if (routeActive) setState(State.RECORDING);
            }
            @Override public void onTranscribing() {
                if (routeActive) setState(State.TRANSCRIBING);
            }
            @Override public void onTranscript(final String text) {
                if (!routeActive) return;
                // Retain the local result before any optional IPC/network work.
                recoverableTranscript = text;
                if (!OnlineCleanupPreferences.isEnabled(host.getVoiceContext())) {
                    commitTranscript(text);
                    return;
                }
                new Thread(() -> {
                    final String cleaned = OnlineCleanupClient.clean(
                            host.getVoiceContext(), text, OnlineCleanupPreferences.MODE_NORMAL);
                    mainHandler.post(() -> {
                        if (routeActive) commitTranscript(cleaned == null ? text : cleaned);
                    });
                }, "catboard-online-cleanup").start();
            }
            @Override public void onNoSpeech() {
                if (!routeActive) return;
                setState(State.READY);
                host.onVoiceMessage(R.string.voice_status_no_speech);
            }
            @Override public void onFailure() {
                if (!routeActive) return;
                setState(State.ERROR);
                host.onVoiceMessage(R.string.voice_error_runtime_failed);
            }
        });
    }

    public void toggle() {
        if (state == State.IDLE || state == State.PERMISSION_REQUIRED || state == State.ERROR) startVoiceRoute();
        else if (state == State.READY) startRecording();
        else if (state == State.RECORDING) {
            runtime.stop();
        }
    }

    private void startVoiceRoute() {
        routeActive = true;
        final Context context = host.getVoiceContext();
        if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            setState(State.PERMISSION_REQUIRED);
            final Intent intent = new Intent(context, VoicePermissionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(VoicePermissionActivity.EXTRA_RESULT_RECEIVER,
                    new ResultReceiver(mainHandler) {
                        @Override protected void onReceiveResult(final int resultCode, final android.os.Bundle data) {
                            if (resultCode == 1) startVoiceRoute();
                            else {
                                routeActive = false;
                                setState(State.PERMISSION_REQUIRED);
                                host.onVoiceMessage(R.string.voice_status_permission_required);
                            }
                        }
                    });
            context.startActivity(intent);
            return;
        }
        setState(State.READY);
        startRecording();
    }

    private void startRecording() {
        // Cancellation releases AudioRecord asynchronously. Ignore a rapid re-tap until
        // that release has completed instead of starting a second capture session.
        if (runtime.isRecording()) return;
        if (!runtime.start()) setState(State.ERROR);
    }

    /** Commit a local transcript when a later recognizer supplies one. */
    public boolean commitTranscript(@Nullable final CharSequence transcript) {
        if (transcript == null || transcript.toString().trim().isEmpty()) return false;
        recoverableTranscript = transcript.toString();
        final InputConnection connection = host.getVoiceInputConnection();
        if (connection == null) {
            setState(State.ERROR);
            host.onVoiceMessage(R.string.voice_error_editor_unavailable);
            return false;
        }
        try {
            if (!connection.commitText(recoverableTranscript, 1)) {
                setState(State.ERROR);
                host.onVoiceMessage(R.string.voice_error_transcript_rejected);
                return false;
            }
            recoverableTranscript = null;
            routeActive = false;
            setState(State.IDLE);
            return true;
        } catch (RuntimeException failure) {
            setState(State.ERROR);
            host.onVoiceMessage(R.string.voice_error_transcript_commit_failed);
            return false;
        }
    }

    @Nullable public String getRecoverableTranscript() { return recoverableTranscript; }

    public void cancel() {
        routeActive = false;
        runtime.cancel();
        if (state != State.IDLE) setState(State.IDLE);
    }

    /** Called only when the IME service is being destroyed. */
    public void destroy() {
        routeActive = false;
        runtime.destroy();
        if (state != State.IDLE) setState(State.IDLE);
    }

    private void setState(@NonNull final State next) {
        state = next;
        host.onVoiceStateChanged(next);
    }
}
