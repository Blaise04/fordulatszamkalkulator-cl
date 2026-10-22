package hu.csiszolotarcsawebshop.fordulatszam

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)

        setContent {
            val systemDark = isSystemInDarkTheme()
            // null = a telefon beállítását követi; a gombbal választott mód megmarad.
            var darkChoice by remember {
                mutableStateOf<Boolean?>(if (prefs.contains(KEY_DARK)) prefs.getBoolean(KEY_DARK, false) else null)
            }
            val dark = darkChoice ?: systemDark

            DisposableEffect(dark) {
                val style = if (dark) {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                }
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
                onDispose { }
            }

            AppTheme(dark = dark) {
                App(
                    dark = dark,
                    onToggleDark = {
                        val next = !dark
                        darkChoice = next
                        prefs.edit().putBoolean(KEY_DARK, next).apply()
                    },
                )
            }
        }
    }

    private companion object {
        const val KEY_DARK = "dark_mode"
    }
}
