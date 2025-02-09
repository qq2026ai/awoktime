package cn.jianyun.worktime.ui.theme


import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryColor,
    secondary = DarkSecondColor,
    tertiary = DarkThirdColor,
    background = DarkBg0Color,
    surface = DarkBgColor,
    surfaceVariant = DarkSelectColor,
    error = DarkDeleteColor,
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimaryColor,
    secondary = LightSecondColor,
    tertiary = LightThirdColor,
    background = LightBgColor,
    onBackground= LightPrimaryColor,
    surface = LightBg0Color,
    surfaceVariant = LightSelectColor,
    error = LightDeleteColor,


    /* Other default colors to override
background = Color(0xFFFFFBFE),
surface = Color(0xFFFFFBFE),
onPrimary = Color.White,
onSecondary = Color.White,
onTertiary = Color.White,
onBackground = Color(0xFF1C1B1F),
onSurface = Color(0xFF1C1B1F),
*/
)


@Composable
fun WorktimeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) DarkColorScheme else LightColorScheme
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    Log.d("primary", colorScheme.primary.toString())

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}