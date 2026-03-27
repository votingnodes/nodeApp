package co.empiresec.nodeapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Bitcoin Orange
val BitcoinOrange = Color(0xFFFF9900)
// Deep Sovereign Black
val SovereignBlack = Color(0xFF0A0A0A)
// Clean Block Green
val BlockGreen = Color(0xFF00FFA3)
// Chain Divergence Red
val DivergenceRed = Color(0xFFFF3D00)

// Background colors
val CardBackground = Color(0xFF1A1A1A)
val DarkBackground = Color(0xFF0A0A0A)
val Gray800 = Color(0xFF2A2A2A)
val Gray600 = Color(0xFF666666)
val Gray400 = Color(0xFFA0A0A0)

val DarkColorScheme = darkColorScheme(
    primary = BitcoinOrange,
    onPrimary = SovereignBlack,
    secondary = Gray800,
    onSecondary = Color.White,
    tertiary = BlockGreen,
    onTertiary = SovereignBlack,
    background = SovereignBlack,
    onBackground = Color.White,
    surface = CardBackground,
    onSurface = Color.White,
    error = DivergenceRed,
    onError = Color.White,
    outline = Color.White.copy(alpha = 0.1f),
    surfaceVariant = Gray800,
    onSurfaceVariant = Gray400
)

val LightColorScheme = lightColorScheme(
    primary = BitcoinOrange,
    onPrimary = Color.White,
    secondary = Gray800,
    tertiary = BlockGreen,
    background = Color.White,
    surface = Color(0xFFF5F5F5),
    error = DivergenceRed
)
