/*
 * Copyright (C) 2026 CatBoard contributors
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.voice;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ResultReceiver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/** Non-exported runtime-permission bridge for the {@code InputMethodService}. */
public final class VoicePermissionActivity extends Activity {
    public static final String EXTRA_RESULT_RECEIVER =
            "helium314.keyboard.voice.PERMISSION_RESULT";
    public static final int RESULT_PERMISSION_DENIED = 0;
    public static final int RESULT_PERMISSION_GRANTED = 1;
    private static final int REQUEST_RECORD_AUDIO = 1;
    private boolean resultDelivered;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            finishWithResult(true);
        } else if (savedInstanceState == null) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
            @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final boolean granted = requestCode == REQUEST_RECORD_AUDIO
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        finishWithResult(granted);
    }

    @Override
    protected void onDestroy() {
        if (!resultDelivered && !isChangingConfigurations()) sendResult(false);
        super.onDestroy();
    }

    @SuppressWarnings("deprecation")
    private void finishWithResult(final boolean granted) {
        sendResult(granted);
        finish();
    }

    @SuppressWarnings("deprecation")
    private void sendResult(final boolean granted) {
        if (resultDelivered) return;
        resultDelivered = true;
        final ResultReceiver receiver = getIntent().getParcelableExtra(EXTRA_RESULT_RECEIVER);
        if (receiver != null) {
            receiver.send(granted ? RESULT_PERMISSION_GRANTED : RESULT_PERMISSION_DENIED, null);
        }
    }
}
