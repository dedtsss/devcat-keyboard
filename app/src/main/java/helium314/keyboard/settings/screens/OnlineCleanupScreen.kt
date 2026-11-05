// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.settings.screens

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import helium314.keyboard.latin.R
import helium314.keyboard.latin.voice.OnlineCleanupPreferences
import helium314.keyboard.latin.utils.getActivity
import helium314.keyboard.settings.SearchSettingsScreen
import helium314.keyboard.settings.SettingsActivity
import helium314.keyboard.settings.preferences.Preference

@Composable
fun OnlineCleanupScreen(onClickBack: () -> Unit) {
    val context = LocalContext.current
    val tick = (context.getActivity() as? SettingsActivity)?.prefChanged?.collectAsState()
    if ((tick?.value ?: 0) < 0) Unit
    val prefs = context.getSharedPreferences(OnlineCleanupPreferences.PREFS, 0)
    SearchSettingsScreen(onClickBack = onClickBack, title = stringResource(R.string.catboard_online_cleanup), settings = emptyList()) {
        var enabled by remember { mutableStateOf(prefs.getBoolean(OnlineCleanupPreferences.ENABLED, false)) }
        Preference(name = stringResource(R.string.catboard_online_cleanup_enabled),
            description = stringResource(R.string.catboard_online_cleanup_enabled_summary),
            onClick = { enabled = !enabled; prefs.edit().putBoolean(OnlineCleanupPreferences.ENABLED, enabled).apply() }) {
            Switch(checked = enabled, onCheckedChange = { enabled = it; prefs.edit().putBoolean(OnlineCleanupPreferences.ENABLED, it).apply() })
        }
        var mode by remember { mutableStateOf(OnlineCleanupPreferences.getMode(context)) }
        val next = when (mode) {
            OnlineCleanupPreferences.MODE_LIGHT -> OnlineCleanupPreferences.MODE_NORMAL
            OnlineCleanupPreferences.MODE_NORMAL -> OnlineCleanupPreferences.MODE_CLEAN
            else -> OnlineCleanupPreferences.MODE_LIGHT
        }
        Preference(name = stringResource(R.string.catboard_cleanup_mode), description = when (mode) {
            OnlineCleanupPreferences.MODE_LIGHT -> stringResource(R.string.catboard_cleanup_mode_light)
            OnlineCleanupPreferences.MODE_CLEAN -> stringResource(R.string.catboard_cleanup_mode_clean)
            else -> stringResource(R.string.catboard_cleanup_mode_normal)
        }, onClick = { mode = next; OnlineCleanupPreferences.setMode(context, next) })
    }
}
