package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

// Sunlight-optimized high-contrast green & white palette
private val GolfLightColorScheme = lightColorScheme(
  primary = FairwayDeepGreen,
  onPrimary = Color.White,
  primaryContainer = FairwayCardTint,
  onPrimaryContainer = FairwayDeepGreen,
  secondary = FairwayBrightGreen,
  onSecondary = Color.White,
  secondaryContainer = ChampionshipGoldLight,
  onSecondaryContainer = ChampionshipGoldDark,
  tertiary = ChampionshipGold,
  onTertiary = ChampionshipGoldDark,
  background = FairwayMintSurface,
  onBackground = InkBlack,
  surface = ScorecardWhite,
  onSurface = InkBlack,
  surfaceVariant = FairwayCardTint,
  onSurfaceVariant = InkMuted,
  outline = FairwayBorder,
  outlineVariant = Color(0xFFDDE7E0),
  error = StatusOverRed,
  onError = Color.White,
)

private val GolfShapes = Shapes(
  small = RoundedCornerShape(10.dp),
  medium = RoundedCornerShape(14.dp),
  large = RoundedCornerShape(18.dp),
  extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun JaypeeGreensTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = GolfLightColorScheme,
    typography = Typography,
    shapes = GolfShapes,
    content = content,
  )
}
