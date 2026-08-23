/*
 * Copyright (C) 2026 CatBoard contributors
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.voice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.Manifest;
import android.content.pm.PackageManager;

/** Small, non-exported bridge because an InputMethodService is not an Activity. */
public final class VoicePermissionActivity extends Activity {
    static final String EXTRA_RESULT_RECEIVER = "voice_permission_result_receiver";
    static final int REQUEST_RECORD_AUDIO = 1;

    @Override
    protected void onCreate(final Bundle state) {
        super.onCreate(state);
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            finishWithResult(true);
        } else {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions,
            final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        finishWithResult(requestCode == REQUEST_RECORD_AUDIO && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED);
    }

    private void finishWithResult(final boolean granted) {
        final ResultReceiver receiver = getIntent().getParcelableExtra(EXTRA_RESULT_RECEIVER);
        if (receiver != null) receiver.send(granted ? 1 : 0, null);
        finish();
    }
}
