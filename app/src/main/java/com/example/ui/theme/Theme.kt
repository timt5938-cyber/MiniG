package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NeonColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = Color.Black,
  secondary = NeonPurple,
  onSecondary = Color.White,
  tertiary = NeonMagenta,
  onTertiary = Color.White,
  background = BgBlack,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  error = NeonRed,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = NeonColorScheme,
    typography = Typography,
    content = content
  )
}
