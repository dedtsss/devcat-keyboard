/* SPDX-License-Identifier: GPL-3.0-only */
package devcat.catboard.cleaner

import android.app.Activity
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class CleanerSettingsActivity : Activity() {
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        title = "CatBoard Cleaner"
        val prefs = getSharedPreferences(CLEANER_PREFS, MODE_PRIVATE)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(32, 32, 32, 32) }
        root.addView(TextView(this).apply { text = "GigaChat Authorization Key (stored only in this companion)" })
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Authorization Key"
            setText("")
        }
        root.addView(input, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        root.addView(Button(this).apply {
            text = "Save"
            setOnClickListener { prefs.edit().putString(AUTHORIZATION_KEY, input.text.toString().trim()).apply(); input.setText("") }
        })
        root.addView(Button(this).apply {
            text = "Clear"
            setOnClickListener { prefs.edit().remove(AUTHORIZATION_KEY).apply(); input.setText("") }
        })
        setContentView(root)
    }
}
