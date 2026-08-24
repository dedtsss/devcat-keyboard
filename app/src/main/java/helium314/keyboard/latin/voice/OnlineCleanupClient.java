/* Copyright (C) 2026 CatBoard contributors
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.voice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/** Optional, bounded IPC client. Any failure is intentionally a local fallback. */
final class OnlineCleanupClient {
    private static final String PACKAGE = "devcat.catboard.cleaner";
    private static final String SERVICE = "devcat.catboard.cleaner.TranscriptCleanerService";
    private static final String DESCRIPTOR = "devcat.catboard.cleaner.ITranscriptCleaner";
    private static final int TRANSACTION_CLEAN = IBinder.FIRST_CALL_TRANSACTION;
    private static final long BIND_TIMEOUT_MS = 1_000L;

    private OnlineCleanupClient() {}

    static String clean(@NonNull Context context, @NonNull String transcript, @NonNull String mode) {
        if (transcript.trim().isEmpty() || transcript.length() > 6_000) return null;
        final CountDownLatch connected = new CountDownLatch(1);
        final IBinder[] binder = new IBinder[1];
        final ServiceConnection connection = new ServiceConnection() {
            @Override public void onServiceConnected(ComponentName name, IBinder service) {
                binder[0] = service;
                connected.countDown();
            }
            @Override public void onServiceDisconnected(ComponentName name) { connected.countDown(); }
        };
        final Intent intent = new Intent().setComponent(new ComponentName(PACKAGE, SERVICE));
        try {
            if (!context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
                    || !connected.await(BIND_TIMEOUT_MS, TimeUnit.MILLISECONDS)) return null;
            final IBinder service = binder[0];
            if (service == null) return null;
            final Bundle request = new Bundle();
            request.putString("transcript", transcript);
            request.putString("mode", mode);
            final Bundle response = transact(service, request);
            final String cleaned = response.getString("transcript");
            return cleaned == null || cleaned.trim().isEmpty() ? null : cleaned.trim();
        } catch (Exception ignored) {
            return null;
        } finally {
            try { context.unbindService(connection); } catch (IllegalArgumentException ignored) { }
        }
    }

    private static Bundle transact(IBinder service, Bundle request) throws RemoteException {
        final Parcel data = Parcel.obtain();
        final Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            data.writeBundle(request);
            service.transact(TRANSACTION_CLEAN, data, reply, 0);
            reply.readException();
            return reply.readBundle(OnlineCleanupClient.class.getClassLoader());
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}
