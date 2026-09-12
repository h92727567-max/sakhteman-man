package com.buildingmy.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

val Teal = Color(0xFF2A6B5F)
val TealDark = Color(0xFF1E4F46)
val Gold = Color(0xFFC4A35A)
val Cream = Color(0xFFF4F1EA)
val Ink = Color(0xFF1C1917)
val Mute = Color(0xFF57534E)
val Danger = Color(0xFFB42318)
val Ok = Color(0xFF027A48)

private val Colors = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD5EBE6),
    onPrimaryContainer = TealDark,
    secondary = Gold,
    onSecondary = Ink,
    secondaryContainer = Color(0xFFF3E6C8),
    background = Cream,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFE8E4DC),
    onSurfaceVariant = Mute,
    error = Danger,
    outline = Color(0xFFD6D3D1)
)

@Composable
fun SakhtemanTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(colorScheme = Colors, content = content)
    }
}
