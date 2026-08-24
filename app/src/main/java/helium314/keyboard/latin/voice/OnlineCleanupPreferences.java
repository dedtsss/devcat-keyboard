/* Copyright (C) 2026 CatBoard contributors
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.voice;

import android.content.Context;

/** Explicit local opt-in gate; disabled by default and independent of editor data. */
final class OnlineCleanupPreferences {
    static final String MODE_NORMAL = "normal";
    private static final String PREFS = "catboard_online_cleanup";
    private static final String ENABLED = "enabled";

    private OnlineCleanupPreferences() {}

    static boolean isEnabled(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(ENABLED, false);
    }

    static void setEnabled(Context context, boolean enabled) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putBoolean(ENABLED, enabled).apply();
    }
}
