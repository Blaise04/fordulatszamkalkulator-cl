package hu.csiszolotarcsawebshop.fordulatszam

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

val Brand = Color(0xFFAE0413)
val BrandDeep = Color(0xFF7A0A12)

@Immutable
data class AppColors(
    val isDark: Boolean,
    val bg: Color,
    val text: Color,
    val muted: Color,
    val label: Color,
    val surface: Color,
    val inputBg: Color,
    val headBg: Color,
    val border: Color,
    val divider: Color,
    val navBg: Color,
    val accentText: Color,
    val softBg: Color,
    val softText: Color,
    val warnBg: Color,
    val warnBorder: Color,
    val warnText: Color,
)

val LightColors = AppColors(
    isDark = false,
    bg = Color(0xFFFFFFFF),
    text = Color(0xFF1C1B1F),
    muted = Color(0xFF5C5F66),
    label = Color(0xFF3A3B40),
    surface = Color(0xFFFFFFFF),
    inputBg = Color(0xFFF7F7F8),
    headBg = Color(0xFFF4F4F5),
    border = Color(0xFFD4D4D8),
    divider = Color(0xFFE4E4E7),
    navBg = Color(0xFFFAFAFA),
    accentText = Brand,
    softBg = Color(0xFFFBEAEC),
    softText = Brand,
    warnBg = Color(0xFFFFF4D6),
    warnBorder = Color(0xFFE8B33A),
    warnText = Color(0xFF6B4300),
)

val DarkColors = AppColors(
    isDark = true,
    bg = Color(0xFF141416),
    text = Color(0xFFF2F2F4),
    muted = Color(0xFFA9ABB3),
    label = Color(0xFFD4D5DA),
    surface = Color(0xFF1F2024),
    inputBg = Color(0xFF1F2024),
    headBg = Color(0xFF26272C),
    border = Color(0xFF45464E),
    divider = Color(0xFF2C2D33),
    navBg = Color(0xFF1A1B1E),
    accentText = Color(0xFFFF7A82),
    softBg = Color(0xFF3A1519),
    softText = Color(0xFFFF9AA0),
    warnBg = Color(0xFF3A2E0F),
    warnBorder = Color(0xFFA07A1F),
    warnText = Color(0xFFFFD87A),
)

val LocalAppColors = staticCompositionLocalOf { LightColors }

/** Keskeny, félkövér rendszerbetű a címekhez és a nagy eredményszámokhoz. */
val CondensedFamily: FontFamily = FontFamily(
    android.graphics.Typeface.create("sans-serif-condensed", android.graphics.Typeface.BOLD)
)

@Composable
fun AppTheme(dark: Boolean, content: @Composable () -> Unit) {
    val colors = if (dark) DarkColors else LightColors
    val scheme = if (dark) {
        darkColorScheme(
            primary = Brand,
            onPrimary = Color.White,
            background = colors.bg,
            onBackground = colors.text,
            surface = colors.bg,
            onSurface = colors.text,
        )
    } else {
        lightColorScheme(
            primary = Brand,
            onPrimary = Color.White,
            background = colors.bg,
            onBackground = colors.text,
            surface = colors.bg,
            onSurface = colors.text,
        )
    }
    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}
