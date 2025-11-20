package com.example.bio.presentation.common.component.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.core.view.WindowCompat
import com.example.bio.R


val Pro_Navy_Dark = Color(0xFF1A237E)
val Pro_Navy_Light = Color(0xFF534BAE)
val Pro_Orange = Color(0xFFFF8A65)
val Pro_White_Smoke = Color(0xFFF8F9FA)
val Pro_Dark_Gunmetal = Color(0xFF121212)
val Pro_Surface_Light = Color(0xFFFFFFFF)
val Pro_Surface_Dark = Color(0xFF1E272E)

// --- فونت ---
val vazirmatn = FontFamily(
    Font(R.font.vazirmatn_regular),
    Font(R.font.vazirmatn_bold)
)

// --- سیستم رنگ‌های سفارشی (اگر در بخش‌های دیگر استفاده کرده‌اید) ---
data class customColors(
    val appGrey: Color,
    val anotherCustomColor: Color,
    val lightBlue: Color
)

val LocalCustomColors = staticCompositionLocalOf {
    customColors(
        appGrey = Color.Gray,
        anotherCustomColor = Color.Magenta,
        lightBlue = Color(0xFF3369FF)
    )
}

// --- پالت رنگی متریال ---
private val DarkColorScheme = darkColorScheme(
    primary = Pro_Navy_Light,
    onPrimary = Color.White,
    secondary = Pro_Orange,
    onSecondary = Color.White,
    background = Pro_Dark_Gunmetal,
    surface = Pro_Surface_Dark,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Pro_Navy_Dark,
    onPrimary = Color.White,
    secondary = Pro_Orange,
    onSecondary = Color.White,
    background = Pro_White_Smoke,
    surface = Pro_Surface_Light,
    onSurface = Color.Black
)

@Composable
fun BioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // ✅ مهم: این را false کنید تا رنگ‌های شما اعمال شود، نه رنگ‌های پیش‌فرض گوشی کاربر
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val customColors = customColors(
        appGrey = Color.Gray,
        anotherCustomColor = if (darkTheme) Color.Yellow else Color.Cyan,
        lightBlue = Color(0xFF3369FF)
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // ✅✅✅ تغییر رنگ نوار وضعیت به سرمه‌ای (همیشه)
            window.statusBarColor = Pro_Navy_Dark.toArgb()

            // ✅✅✅ سفید کردن آیکون‌های نوار وضعیت (ساعت، باتری و...)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}