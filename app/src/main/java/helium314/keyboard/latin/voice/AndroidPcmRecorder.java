/*
 * Copyright (C) 2026 CatBoard contributors
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.voice;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/** Local microphone capture for the future offline speech pipeline. */
final class AndroidPcmRecorder implements PcmRecorder {
    static final int SAMPLE_RATE_HZ = 16_000;
    static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    static final int MAX_CAPTURE_BYTES = SAMPLE_RATE_HZ * 2 * 60;

    private static final String TAG = "CatBoardPcmRecorder";
    private static final int READ_BUFFER_BYTES = 4096;
    private static final long CANCEL_JOIN_MILLIS = 500;

    private final Object lock = new Object();
    private Session activeSession;

    @Override
    @SuppressLint("MissingPermission") // VoiceController starts us only after its permission check.
    public boolean start(@NonNull final Callback callback) {
        synchronized (lock) {
            if (activeSession != null) return false;

            AudioRecord audioRecord = null;
            boolean recordingStarted = false;
            try {
                final int minimum = AudioRecord.getMinBufferSize(
                        SAMPLE_RATE_HZ, CHANNEL_CONFIG, AUDIO_FORMAT);
                if (minimum <= 0) return false;
                final int bufferSize = Math.max(minimum, READ_BUFFER_BYTES * 2);
                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_HZ,
                        CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);
                if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                    audioRecord.release();
                    return false;
                }
                audioRecord.startRecording();
                recordingStarted = true;
                if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                    audioRecord.stop();
                    audioRecord.release();
                    return false;
                }

                final Session session = new Session(audioRecord, callback);
                activeSession = session;
                session.thread = new Thread(() -> capture(session), "CatBoardPcmCapture");
                try {
                    session.thread.start();
                } catch (RuntimeException failure) {
                    activeSession = null;
                    session.stopAndRelease();
                    Log.e(TAG, "Unable to create microphone capture thread", failure);
                    return false;
                }
                return true;
            } catch (RuntimeException failure) {
                Log.e(TAG, "Unable to start microphone capture", failure);
                if (audioRecord != null) {
                    if (recordingStarted) {
                        try {
                            audioRecord.stop();
                        } catch (RuntimeException stopFailure) {
                            Log.w(TAG, "Unable to stop failed microphone startup", stopFailure);
                        }
                    }
                    audioRecord.release();
                }
                return false;
            }
        }
    }

    private void capture(@NonNull final Session session) {
        final ByteArrayOutputStream pcm = new ByteArrayOutputStream();
        boolean failed = false;
        try {
            final byte[] buffer = new byte[READ_BUFFER_BYTES];
            while (!session.stopRequested.get() && !session.cancelled.get()
                    && pcm.size() < MAX_CAPTURE_BYTES) {
                final int read = session.audioRecord.read(buffer, 0, buffer.length);
                if (read > 0) {
                    pcm.write(buffer, 0, Math.min(read, MAX_CAPTURE_BYTES - pcm.size()));
                } else if (!session.stopRequested.get() && !session.cancelled.get()) {
                    failed = true;
                    break;
                }
            }
        } catch (RuntimeException failure) {
            if (!session.stopRequested.get() && !session.cancelled.get()) {
                failed = true;
                Log.e(TAG, "Microphone capture failed", failure);
            }
        } finally {
            session.stopAndRelease();
            synchronized (lock) {
                if (activeSession == session) activeSession = null;
            }
        }

        if (!session.cancelled.get()) {
            session.callback.onCaptureComplete(failed
                    ? Result.error() : Result.captured(pcm.toByteArray()));
        }
    }

    @Override
    public void stop() {
        final Session session;
        synchronized (lock) {
            session = activeSession;
        }
        if (session == null) return;
        session.stopRequested.set(true);
        session.stopRecording();
    }

    @Override
    public void cancel() {
        final Session session;
        synchronized (lock) {
            session = activeSession;
            if (session == null) return;
            session.cancelled.set(true);
        }
        session.stopAndRelease();
        final Thread thread = session.thread;
        if (thread != null && thread != Thread.currentThread()) {
            thread.interrupt();
            try {
                thread.join(CANCEL_JOIN_MILLIS);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static final class Session {
        private final AudioRecord audioRecord;
        private final Callback callback;
        private final AtomicBoolean stopRequested = new AtomicBoolean();
        private final AtomicBoolean cancelled = new AtomicBoolean();
        private final AtomicBoolean stopped = new AtomicBoolean();
        private final AtomicBoolean released = new AtomicBoolean();
        private Thread thread;

        private Session(@NonNull final AudioRecord audioRecord,
                @NonNull final Callback callback) {
            this.audioRecord = audioRecord;
            this.callback = callback;
        }

        private void stopRecording() {
            if (!stopped.compareAndSet(false, true)) return;
            try {
                audioRecord.stop();
            } catch (RuntimeException failure) {
                Log.w(TAG, "Unable to stop microphone capture", failure);
            }
        }

        private void stopAndRelease() {
            stopRequested.set(true);
            stopRecording();
            if (!released.compareAndSet(false, true)) return;
            audioRecord.release();
        }
    }
}
