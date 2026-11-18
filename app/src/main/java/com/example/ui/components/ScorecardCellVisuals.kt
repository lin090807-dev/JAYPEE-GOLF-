package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.GolfScoringCalculator
import com.example.domain.model.ScoreVisualStyle
import com.example.ui.theme.BirdieRed
import com.example.ui.theme.BogeyBlue
import com.example.ui.theme.DoubleBogeyNavy
import com.example.ui.theme.EagleGoldBorder
import com.example.ui.theme.EagleGoldFill
import com.example.ui.theme.EagleGoldText
import com.example.ui.theme.FairwayBorder
import com.example.ui.theme.FairwayDeepGreen
import com.example.ui.theme.FairwayEmerald
import com.example.ui.theme.InkBlack
import com.example.ui.theme.InkMuted
import com.example.ui.theme.JetBrainsMonoFontFamily
import kotlin.math.min

/**
 * Compact portrait-fitted cell for the 9-hole scorecard table.
 * Displays:
 * - Corner handicap stroke dots (●)
 * - Traditional scorecard marking (Gold Eagle-, Red Circle Birdie, Plain Par, Blue Square Bogey, Navy Double Square 2+)
 * - Large actual Gross score + small relative-to-par value
 */
@Composable
fun ScorecardHoleCell(
  par: Int,
  relativeToPar: Int?,
  strokesAllocated: Int,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val bgColor = when {
    isSelected -> Color(0xFFDDF3E4)
    else -> Color.Transparent
  }

  Box(
    modifier = modifier
      .height(50.dp)
      .background(bgColor)
      .then(
        if (isSelected) {
          Modifier.border(2.dp, FairwayDeepGreen)
        } else {
          Modifier.border(0.5.dp, FairwayBorder.copy(alpha = 0.7f))
        }
      )
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center,
  ) {
    // Corner handicap stroke dots (●)
    if (strokesAllocated > 0) {
      Row(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(top = 2.dp, end = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(1.5.dp),
      ) {
        repeat(min(strokesAllocated, 3)) {
          Box(
            modifier = Modifier
              .size(4.5.dp)
              .clip(CircleShape)
              .background(FairwayEmerald),
          )
        }
      }
    }

    if (relativeToPar != null) {
      val gross = GolfScoringCalculator.grossScore(par, relativeToPar)
      val visualStyle = GolfScoringCalculator.scoreVisualStyle(relativeToPar)
      val relDisplay = if (gross == 1) "HIO" else GolfScoringCalculator.formatRelativeToPar(relativeToPar)

      Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        Box(
          modifier = Modifier.size(26.dp),
          contentAlignment = Alignment.Center,
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            when (visualStyle) {
              ScoreVisualStyle.EAGLE_OR_BETTER -> {
                drawRoundRect(
                  color = EagleGoldFill,
                  topLeft = Offset(1.dp.toPx(), 1.dp.toPx()),
                  size = Size(w - 2.dp.toPx(), h - 2.dp.toPx()),
                  cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                )
                drawRoundRect(
                  color = EagleGoldBorder,
                  topLeft = Offset(1.dp.toPx(), 1.dp.toPx()),
                  size = Size(w - 2.dp.toPx(), h - 2.dp.toPx()),
                  cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                  style = Stroke(width = 1.5.dp.toPx()),
                )
              }
              ScoreVisualStyle.BIRDIE -> {
                drawCircle(
                  color = BirdieRed,
                  radius = (min(w, h) / 2f) - 1.dp.toPx(),
                  style = Stroke(width = 1.75.dp.toPx()),
                )
              }
              ScoreVisualStyle.PAR -> Unit
              ScoreVisualStyle.BOGEY -> {
                drawRect(
                  color = BogeyBlue,
                  topLeft = Offset(1.dp.toPx(), 1.dp.toPx()),
                  size = Size(w - 2.dp.toPx(), h - 2.dp.toPx()),
                  style = Stroke(width = 1.75.dp.toPx()),
                )
              }
              ScoreVisualStyle.DOUBLE_OR_WORSE -> {
                drawRect(
                  color = DoubleBogeyNavy,
                  topLeft = Offset(0.5.dp.toPx(), 0.5.dp.toPx()),
                  size = Size(w - 1.dp.toPx(), h - 1.dp.toPx()),
                  style = Stroke(width = 1.5.dp.toPx()),
                )
                val inset = 3.5.dp.toPx()
                drawRect(
                  color = DoubleBogeyNavy,
                  topLeft = Offset(inset, inset),
                  size = Size(w - inset * 2, h - inset * 2),
                  style = Stroke(width = 1.3.dp.toPx()),
                )
              }
            }
          }

          Text(
            text = gross.toString(),
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (visualStyle == ScoreVisualStyle.EAGLE_OR_BETTER) EagleGoldText else InkBlack,
            textAlign = TextAlign.Center,
            maxLines = 1,
          )
        }

        Text(
          text = relDisplay,
          fontFamily = JetBrainsMonoFontFamily,
          fontWeight = FontWeight.Medium,
          fontSize = 9.sp,
          lineHeight = 10.sp,
          color = when (visualStyle) {
            ScoreVisualStyle.EAGLE_OR_BETTER -> EagleGoldBorder
            ScoreVisualStyle.BIRDIE -> BirdieRed
            ScoreVisualStyle.PAR -> InkMuted
            ScoreVisualStyle.BOGEY -> BogeyBlue
            ScoreVisualStyle.DOUBLE_OR_WORSE -> DoubleBogeyNavy
          },
          maxLines = 1,
        )
      }
    } else {
      Text(
        text = "·",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = if (isSelected) FairwayDeepGreen else Color(0xFFB6C5BC),
      )
    }
  }
}
