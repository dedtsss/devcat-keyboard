/* Copyright (C) 2026 CatBoard contributors
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin.voice;

import android.content.Context;

/** Explicit local opt-in gate; disabled by default and independent of editor data. */
public final class OnlineCleanupPreferences {
    public static final String MODE_LIGHT = "light";
    public static final String MODE_NORMAL = "normal";
    public static final String MODE_CLEAN = "clean";
    public static final String PREFS = "catboard_online_cleanup";
    public static final String ENABLED = "enabled";
    public static final String MODE = "mode";

    private OnlineCleanupPreferences() {}

    public static boolean isEnabled(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(ENABLED, false);
    }

    public static void setEnabled(Context context, boolean enabled) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putBoolean(ENABLED, enabled).apply();
    }

    public static String getMode(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(MODE, MODE_NORMAL);
    }

    public static void setMode(Context context, String mode) {
        if (MODE_LIGHT.equals(mode) || MODE_NORMAL.equals(mode) || MODE_CLEAN.equals(mode))
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(MODE, mode).apply();
    }
}
