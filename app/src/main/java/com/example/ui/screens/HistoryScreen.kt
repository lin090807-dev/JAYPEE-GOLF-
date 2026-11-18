package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.GolfScoringCalculator
import com.example.domain.model.RoundSummaryInfo
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.FairwayBorder
import com.example.ui.theme.FairwayCardTint
import com.example.ui.theme.FairwayDeepGreen
import com.example.ui.theme.InkBlack
import com.example.ui.theme.InkMuted
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ScorecardWhite
import com.example.ui.theme.StatusEvenBg
import com.example.ui.theme.StatusEvenGray
import com.example.ui.theme.StatusOverBg
import com.example.ui.theme.StatusOverRed
import com.example.ui.theme.StatusUnderBg
import com.example.ui.theme.StatusUnderGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
  rounds: List<RoundSummaryInfo>,
  activeRoundId: Long?,
  onOpenRound: (Long) -> Unit,
  onDeleteRound: (Long) -> Unit,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current
  var pendingDeleteRoundId by remember { mutableStateOf<Long?>(null) }

  if (pendingDeleteRoundId != null) {
    AlertDialog(
      onDismissRequest = { pendingDeleteRoundId = null },
      title = { Text(strings.deleteConfirmTitle, fontWeight = FontWeight.Bold) },
      text = { Text(strings.deleteConfirmMessage) },
      confirmButton = {
        TextButton(
          onClick = {
            pendingDeleteRoundId?.let(onDeleteRound)
            pendingDeleteRoundId = null
          },
          colors = ButtonDefaults.textButtonColors(contentColor = StatusOverRed),
        ) {
          Text(strings.delete, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { pendingDeleteRoundId = null }) {
          Text(strings.cancel)
        }
      },
    )
  }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    if (rounds.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        contentAlignment = Alignment.Center,
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Icon(
            imageVector = Icons.Filled.History,
            contentDescription = null,
            tint = FairwayDeepGreen,
            modifier = Modifier.size(56.dp),
          )
          Text(
            text = strings.emptyHistoryTitle,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
          )
          Text(
            text = strings.emptyHistorySubtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted,
            textAlign = TextAlign.Center,
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 640.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        item {
          Text(
            text = strings.historyTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = FairwayDeepGreen,
          )
        }
        items(rounds, key = { it.roundId }) { round ->
          val isCurrent = round.roundId == activeRoundId
          val dateText = remember(round.dateEpochMillis) {
            SimpleDateFormat("yyyy-MM-dd (EEE)", Locale.getDefault()).format(Date(round.dateEpochMillis))
          }

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onOpenRound(round.roundId) }
              .testTag("history_round_${round.roundId}"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ScorecardWhite),
            border = BorderStroke(if (isCurrent) 2.dp else 1.dp, if (isCurrent) FairwayDeepGreen else FairwayBorder),
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  Box(
                    modifier = Modifier
                      .size(12.dp)
                      .clip(CircleShape)
                      .background(round.teeColor.dotColor)
                      .border(1.dp, Color(0xFF94A3B8), CircleShape),
                  )
                  Text(
                    text = dateText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InkBlack,
                  )
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = FairwayCardTint,
                  ) {
                    Text(
                      text = strings.holesProgress(round.completedHolesCount),
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = FairwayDeepGreen,
                      modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                    )
                  }
                }
                IconButton(
                  onClick = { pendingDeleteRoundId = round.roundId },
                  modifier = Modifier.size(36.dp),
                ) {
                  Icon(
                    imageVector = Icons.Filled.DeleteOutline,
                    contentDescription = strings.deleteRound,
                    tint = StatusOverRed,
                  )
                }
              }

              HorizontalDivider(color = FairwayBorder.copy(alpha = 0.7f))

              round.playerStats.sortedBy { it.netRank }.forEach { stat ->
                val hasScores = stat.totalPlayedCount > 0
                val (bg, fg) = when {
                  !hasScores -> StatusEvenBg to StatusEvenGray
                  stat.vsHandicap < 0 -> StatusUnderBg to StatusUnderGreen
                  stat.vsHandicap > 0 -> StatusOverBg to StatusOverRed
                  else -> StatusEvenBg to StatusEvenGray
                }
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                  ) {
                    if (hasScores) {
                      Text(
                        text = strings.rankBadge(stat.netRank),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FairwayDeepGreen,
                      )
                    }
                    Text(
                      text = stat.player.name,
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.SemiBold,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                      text = "(${strings.hcpShort} ${stat.player.handicap})",
                      style = MaterialTheme.typography.bodySmall,
                      color = InkMuted,
                    )
                  }

                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                  ) {
                    Text(
                      text = if (hasScores) {
                        "G ${stat.totalGross} (${GolfScoringCalculator.formatRelativeToPar(stat.totalToPar)}) · N ${stat.netScore}"
                      } else "-",
                      fontFamily = JetBrainsMonoFontFamily,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = InkBlack,
                    )
                    if (hasScores) {
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = bg,
                      ) {
                        Text(
                          text = strings.vsHcpBadgeText(stat.vsHandicap),
                          fontFamily = JetBrainsMonoFontFamily,
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = fg,
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
