package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GolfCourse
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.GolfScoringCalculator
import com.example.domain.model.HoleConfig
import com.example.domain.model.PlayerRoundStats
import com.example.share.ScorecardImageGenerator
import com.example.ui.components.ScorecardHoleCell
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.BirdieRed
import com.example.ui.theme.BogeyBlue
import com.example.ui.theme.ChampionshipGold
import com.example.ui.theme.ChampionshipGoldDark
import com.example.ui.theme.ChampionshipGoldLight
import com.example.ui.theme.DoubleBogeyNavy
import com.example.ui.theme.EagleGoldBorder
import com.example.ui.theme.EagleGoldFill
import com.example.ui.theme.FairwayBorder
import com.example.ui.theme.FairwayCardTint
import com.example.ui.theme.FairwayDeepGreen
import com.example.ui.theme.FairwayEmerald
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
import com.example.ui.viewmodel.ActiveRoundUiModel
import com.example.ui.viewmodel.ScorecardSelectionState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ScorecardScreen(
  activeRound: ActiveRoundUiModel?,
  selection: ScorecardSelectionState,
  onSelectNineTab: (Boolean) -> Unit,
  onSelectCell: (holeNumber: Int, playerId: Long) -> Unit,
  onSelectHole: (Int) -> Unit,
  onSelectPlayer: (Long) -> Unit,
  onToggleInputPanel: (Boolean) -> Unit,
  onRecordScore: (playerId: Long, holeNumber: Int, relativeToPar: Int) -> Unit,
  onClearScore: (playerId: Long, holeNumber: Int) -> Unit,
  onNextPlayer: (Long) -> Unit,
  onNextHole: (Int) -> Unit,
  onGoToNewRound: () -> Unit,
  onGoToHistory: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current
  val context = LocalContext.current

  if (activeRound == null) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .padding(24.dp),
      contentAlignment = Alignment.Center,
    ) {
      Card(
        modifier = Modifier.widthIn(max = 460.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ScorecardWhite),
        border = BorderStroke(1.dp, FairwayBorder),
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Icon(
            imageVector = Icons.Filled.GolfCourse,
            contentDescription = null,
            tint = FairwayDeepGreen,
            modifier = Modifier.size(56.dp),
          )
          Text(
            text = strings.noActiveRoundTitle,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
          )
          Text(
            text = strings.noActiveRoundSubtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted,
            textAlign = TextAlign.Center,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            OutlinedButton(
              onClick = onGoToHistory,
              modifier = Modifier.weight(1f),
            ) {
              Icon(Icons.Filled.History, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(strings.goToHistory)
            }
            Button(
              onClick = onGoToNewRound,
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = FairwayDeepGreen),
            ) {
              Icon(Icons.Filled.AddCircleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(strings.goToNewRound)
            }
          }
        }
      }
    }
    return
  }

  val nineHoles = remember(activeRound.holes, selection.isBackNine) {
    if (selection.isBackNine) {
      activeRound.holes.filter { it.holeNumber in 10..18 }
    } else {
      activeRound.holes.filter { it.holeNumber in 1..9 }
    }
  }

  val resolvedPlayerId = selection.selectedPlayerId
    ?.takeIf { id -> activeRound.players.any { it.id == id } }
    ?: activeRound.players.firstOrNull()?.id

  val resolvedHoleNumber = selection.selectedHoleNumber
    ?.takeIf { num -> nineHoles.any { it.holeNumber == num } }
    ?: nineHoles.firstOrNull()?.holeNumber
    ?: 1

  val dateText = remember(activeRound.dateEpochMillis) {
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(activeRound.dateEpochMillis))
  }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 760.dp),
      contentPadding = PaddingValues(bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      // Top Course / Round Header Bar
      item {
        Surface(
          color = FairwayDeepGreen,
          contentColor = Color.White,
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = strings.clubName,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  Text(
                    text = dateText,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFD1E7DA),
                    fontFamily = JetBrainsMonoFontFamily,
                  )
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.14f),
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                      Box(
                        modifier = Modifier
                          .size(10.dp)
                          .clip(CircleShape)
                          .background(activeRound.teeColor.dotColor)
                          .border(1.dp, Color.White, CircleShape),
                      )
                      Text(
                        text = strings.teeName(activeRound.teeColor),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                      )
                    }
                  }
                }
              }

              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(
                  onClick = { onToggleInputPanel(!selection.isInputPanelOpen) },
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                  colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (selection.isInputPanelOpen) ChampionshipGold else Color.White.copy(alpha = 0.18f),
                    contentColor = if (selection.isInputPanelOpen) ChampionshipGoldDark else Color.White,
                  ),
                  modifier = Modifier.testTag("toggle_input_button"),
                ) {
                  Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "H$resolvedHoleNumber",
                    fontWeight = FontWeight.Bold,
                    fontFamily = JetBrainsMonoFontFamily,
                  )
                }

                FilledTonalButton(
                  onClick = { ScorecardImageGenerator.shareScorecardPng(context, activeRound, strings) },
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                  colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color.White,
                    contentColor = FairwayDeepGreen,
                  ),
                  modifier = Modifier.testTag("share_scorecard_button"),
                ) {
                  Icon(Icons.Filled.Share, contentDescription = strings.shareButton, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(strings.shareButton, fontWeight = FontWeight.Bold)
                }
              }
            }

            // OUT / IN Tabs
            val selectedTabIndex = if (selection.isBackNine) 1 else 0
            TabRow(
              selectedTabIndex = selectedTabIndex,
              containerColor = FairwayEmerald,
              contentColor = Color.White,
              indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                  modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                  height = 3.5.dp,
                  color = ChampionshipGold,
                )
              },
            ) {
              Tab(
                selected = !selection.isBackNine,
                onClick = { onSelectNineTab(false) },
                text = {
                  Text(
                    text = strings.frontNineTab,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                  )
                },
                modifier = Modifier.testTag("tab_out"),
              )
              Tab(
                selected = selection.isBackNine,
                onClick = { onSelectNineTab(true) },
                text = {
                  Text(
                    text = strings.backNineTab,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                  )
                },
                modifier = Modifier.testTag("tab_in"),
              )
            }
          }
        }
      }

      // 9-Hole Portrait-Fitted Scorecard Table (Zero horizontal scroll!)
      item {
        NineHoleScorecardTable(
          activeRound = activeRound,
          nineHoles = nineHoles,
          isBackNine = selection.isBackNine,
          selectedHoleNumber = if (selection.isInputPanelOpen) resolvedHoleNumber else null,
          selectedPlayerId = if (selection.isInputPanelOpen) resolvedPlayerId else null,
          onSelectHole = onSelectHole,
          onSelectCell = onSelectCell,
          modifier = Modifier.padding(horizontal = 8.dp),
        )
      }

      // Compact Score Markings & Handicap Dot Legend
      item {
        ScorecardLegendBar(
          modifier = Modifier.padding(horizontal = 8.dp),
        )
      }

      // One-Handed Quick Score Input Panel
      if (selection.isInputPanelOpen && resolvedPlayerId != null) {
        val currentHole = nineHoles.firstOrNull { it.holeNumber == resolvedHoleNumber }
        if (currentHole != null) {
          item {
            AnimatedVisibility(visible = true) {
              OneHandedScoreInputPanel(
                round = activeRound,
                hole = currentHole,
                selectedPlayerId = resolvedPlayerId,
                onSelectPlayer = onSelectPlayer,
                onRecordScore = { rel ->
                  onRecordScore(resolvedPlayerId, currentHole.holeNumber, rel)
                },
                onClearScore = {
                  onClearScore(resolvedPlayerId, currentHole.holeNumber)
                },
                onNextPlayer = { onNextPlayer(resolvedPlayerId) },
                onNextHole = { onNextHole(currentHole.holeNumber) },
                onClose = { onToggleInputPanel(false) },
                modifier = Modifier.padding(horizontal = 8.dp),
              )
            }
          }
        }
      } else {
        item {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = FairwayCardTint,
            border = BorderStroke(1.dp, FairwayBorder),
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 8.dp)
              .clickable { onToggleInputPanel(true) },
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null,
                tint = FairwayDeepGreen,
                modifier = Modifier.size(18.dp),
              )
              Text(
                text = strings.tapCellToScore,
                style = MaterialTheme.typography.bodySmall,
                color = FairwayDeepGreen,
                fontWeight = FontWeight.Medium,
              )
            }
          }
        }
      }

      // Player Summary Cards (1..4)
      items(activeRound.playerStats, key = { it.player.id }) { stat ->
        PlayerSummaryCard(
          stat = stat,
          isBackNineTab = selection.isBackNine,
          onClick = { onSelectPlayer(stat.player.id) },
          modifier = Modifier.padding(horizontal = 8.dp),
        )
      }
    }
  }
}

@Composable
private fun NineHoleScorecardTable(
  activeRound: ActiveRoundUiModel,
  nineHoles: List<HoleConfig>,
  isBackNine: Boolean,
  selectedHoleNumber: Int?,
  selectedPlayerId: Long?,
  onSelectHole: (Int) -> Unit,
  onSelectCell: (holeNumber: Int, playerId: Long) -> Unit,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current
  val statsById = remember(activeRound.playerStats) {
    activeRound.playerStats.associateBy { it.player.id }
  }

  val nameWeight = 1.95f
  val holeWeight = 1f
  val sumWeight = 1.35f

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = ScorecardWhite),
    border = BorderStroke(1.dp, FairwayBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Row 1: HOLE
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(FairwayDeepGreen)
          .height(34.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = strings.holeCol,
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier
            .weight(nameWeight)
            .padding(start = 8.dp),
          maxLines = 1,
        )
        nineHoles.forEach { hole ->
          val isHoleSelected = selectedHoleNumber == hole.holeNumber
          Box(
            modifier = Modifier
              .weight(holeWeight)
              .fillMaxSize()
              .background(if (isHoleSelected) ChampionshipGold else Color.Transparent)
              .clickable { onSelectHole(hole.holeNumber) },
            contentAlignment = Alignment.Center,
          ) {
            Text(
              text = hole.holeNumber.toString(),
              fontFamily = JetBrainsMonoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = if (isHoleSelected) ChampionshipGoldDark else Color.White,
            )
          }
        }
        Box(
          modifier = Modifier
            .weight(sumWeight)
            .fillMaxSize()
            .background(FairwayEmerald),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = if (isBackNine) strings.inCol else strings.outCol,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
          )
        }
      }

      // Row 2: PAR
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(FairwayCardTint)
          .height(26.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = strings.parCol,
          color = FairwayDeepGreen,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier
            .weight(nameWeight)
            .padding(start = 8.dp),
        )
        nineHoles.forEach { hole ->
          Text(
            text = hole.par.toString(),
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = FairwayDeepGreen,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(holeWeight),
          )
        }
        Text(
          text = nineHoles.sumOf { it.par }.toString(),
          fontFamily = JetBrainsMonoFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = FairwayDeepGreen,
          textAlign = TextAlign.Center,
          modifier = Modifier.weight(sumWeight),
        )
      }

      // Row 3: S.I.
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFFF7FAF8))
          .height(24.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = strings.siCol,
          color = InkMuted,
          fontSize = 10.sp,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier
            .weight(nameWeight)
            .padding(start = 8.dp),
        )
        nineHoles.forEach { hole ->
          Text(
            text = hole.strokeIndex.toString(),
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 10.sp,
            color = InkMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(holeWeight),
          )
        }
        Text(
          text = "-",
          fontSize = 10.sp,
          color = InkMuted,
          textAlign = TextAlign.Center,
          modifier = Modifier.weight(sumWeight),
        )
      }

      HorizontalDivider(color = FairwayBorder, thickness = 1.dp)

      // Player Rows (1..4)
      activeRound.players.forEachIndexed { rowIdx, player ->
        val stat = statsById[player.id]
        val playerScores = activeRound.scoresByPlayer[player.id].orEmpty()
        val rowBg = if (rowIdx % 2 == 0) ScorecardWhite else Color(0xFFFAFDFB)

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(rowBg),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          // Player Label Column with Net Rank + Name + HCP
          Column(
            modifier = Modifier
              .weight(nameWeight)
              .height(50.dp)
              .border(0.5.dp, FairwayBorder.copy(alpha = 0.7f))
              .clickable {
                val targetHole = selectedHoleNumber ?: nineHoles.first().holeNumber
                onSelectCell(targetHole, player.id)
              }
              .padding(horizontal = 5.dp, vertical = 3.dp),
            verticalArrangement = Arrangement.Center,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
              if (stat != null && stat.totalPlayedCount > 0) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (stat.netRank == 1) ChampionshipGoldLight else FairwayCardTint)
                    .padding(horizontal = 3.dp, vertical = 1.dp),
                ) {
                  Text(
                    text = strings.rankBadge(stat.netRank),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (stat.netRank == 1) ChampionshipGoldDark else FairwayDeepGreen,
                    maxLines = 1,
                  )
                }
              }
              Text(
                text = player.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = InkBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
            Text(
              text = "${strings.hcpShort} ${player.handicap}",
              fontSize = 9.sp,
              color = InkMuted,
              fontFamily = JetBrainsMonoFontFamily,
              maxLines = 1,
            )
          }

          // 9 Hole Cells
          nineHoles.forEach { hole ->
            val strokes = GolfScoringCalculator.strokesAllocated(player.handicap, hole.strokeIndex)
            val rel = playerScores[hole.holeNumber]
            val isCellSelected =
              selectedHoleNumber == hole.holeNumber && selectedPlayerId == player.id
            ScorecardHoleCell(
              par = hole.par,
              relativeToPar = rel,
              strokesAllocated = strokes,
              isSelected = isCellSelected,
              onClick = { onSelectCell(hole.holeNumber, player.id) },
              modifier = Modifier
                .weight(holeWeight)
                .testTag("cell_p${rowIdx}_h${hole.holeNumber}"),
            )
          }

          // Rightmost 9H Sum Column (Gross / to Par)
          val ninePlayed = if (isBackNine) stat?.backPlayedCount ?: 0 else stat?.frontPlayedCount ?: 0
          val nineGross = if (isBackNine) stat?.backGross ?: 0 else stat?.frontGross ?: 0
          val nineToPar = if (isBackNine) stat?.backToPar ?: 0 else stat?.frontToPar ?: 0

          Column(
            modifier = Modifier
              .weight(sumWeight)
              .height(50.dp)
              .background(FairwayCardTint.copy(alpha = 0.55f))
              .border(0.5.dp, FairwayBorder.copy(alpha = 0.7f)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
          ) {
            if (ninePlayed > 0) {
              Text(
                text = nineGross.toString(),
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = FairwayDeepGreen,
              )
              Text(
                text = GolfScoringCalculator.formatRelativeToPar(nineToPar),
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                color = when {
                  nineToPar < 0 -> StatusUnderGreen
                  nineToPar > 0 -> StatusOverRed
                  else -> InkMuted
                },
              )
            } else {
              Text(
                text = "-",
                fontSize = 14.sp,
                color = InkMuted,
              )
            }
          }
        }
      }

      // On Back 9 tab, also show a compact 18H Total strip right at bottom of table
      if (isBackNine) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(FairwayCardTint)
            .padding(horizontal = 10.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "${strings.total18SummaryLabel} (OUT + IN)",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = FairwayDeepGreen,
          )
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            activeRound.playerStats.forEach { stat ->
              val scoreStr = if (stat.totalPlayedCount > 0) {
                "${stat.player.name.take(5)}: ${stat.totalGross}(${GolfScoringCalculator.formatRelativeToPar(stat.totalToPar)})"
              } else {
                "${stat.player.name.take(5)}: -"
              }
              Text(
                text = scoreStr,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                color = InkBlack,
              )
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ScorecardLegendBar(modifier: Modifier = Modifier) {
  val strings = LocalAppStrings.current
  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
    color = ScorecardWhite,
    border = BorderStroke(1.dp, FairwayBorder),
  ) {
    FlowRow(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      LegendDotItem(color = EagleGoldFill, borderColor = EagleGoldBorder, isCircle = false, isDouble = false, label = strings.legendEagle)
      LegendDotItem(color = Color.Transparent, borderColor = BirdieRed, isCircle = true, isDouble = false, label = strings.legendBirdie)
      LegendDotItem(color = Color.Transparent, borderColor = Color.Transparent, isCircle = false, isDouble = false, label = strings.legendPar)
      LegendDotItem(color = Color.Transparent, borderColor = BogeyBlue, isCircle = false, isDouble = false, label = strings.legendBogey)
      LegendDotItem(color = Color.Transparent, borderColor = DoubleBogeyNavy, isCircle = false, isDouble = true, label = strings.legendDouble)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Box(
          modifier = Modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(FairwayEmerald),
        )
        Text(text = strings.hcpDotsHint.removePrefix("● "), fontSize = 10.sp, color = InkMuted)
      }
    }
  }
}

@Composable
private fun LegendDotItem(
  color: Color,
  borderColor: Color,
  isCircle: Boolean,
  isDouble: Boolean,
  label: String,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    val shape = if (isCircle) CircleShape else RoundedCornerShape(3.dp)
    Box(
      modifier = Modifier
        .size(13.dp)
        .clip(shape)
        .background(color)
        .then(
          if (borderColor != Color.Transparent) {
            Modifier.border(1.5.dp, borderColor, shape)
          } else {
            Modifier.border(1.dp, FairwayBorder, shape)
          }
        ),
      contentAlignment = Alignment.Center,
    ) {
      if (isDouble) {
        Box(
          modifier = Modifier
            .size(7.dp)
            .border(1.dp, borderColor, RoundedCornerShape(1.dp)),
        )
      }
    }
    Text(text = label, fontSize = 10.sp, color = InkMuted, fontWeight = FontWeight.Medium)
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OneHandedScoreInputPanel(
  round: ActiveRoundUiModel,
  hole: HoleConfig,
  selectedPlayerId: Long,
  onSelectPlayer: (Long) -> Unit,
  onRecordScore: (relativeToPar: Int) -> Unit,
  onClearScore: () -> Unit,
  onNextPlayer: () -> Unit,
  onNextHole: () -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current
  val selectedPlayer = round.players.firstOrNull { it.id == selectedPlayerId } ?: round.players.first()
  val currentRel = round.scoresByPlayer[selectedPlayer.id]?.get(hole.holeNumber)
  val allocatedStrokes = GolfScoringCalculator.strokesAllocated(selectedPlayer.handicap, hole.strokeIndex)
  var showOverPicker by remember(hole.holeNumber, selectedPlayer.id) { mutableStateOf(false) }

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = ScorecardWhite),
    border = BorderStroke(2.dp, FairwayDeepGreen),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      // Header: Hole Info + Clear + Close
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = FairwayDeepGreen,
            ) {
              Text(
                text = "HOLE ${hole.holeNumber}",
                color = Color.White,
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              )
            }
            Text(
              text = "Par ${hole.par} · S.I. ${hole.strokeIndex}",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = InkBlack,
            )
          }
          Text(
            text = strings.strokesReceiving(allocatedStrokes),
            style = MaterialTheme.typography.labelSmall,
            color = if (allocatedStrokes > 0) FairwayEmerald else InkMuted,
            fontWeight = FontWeight.SemiBold,
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (currentRel != null) {
            IconButton(onClick = onClearScore) {
              Icon(
                imageVector = Icons.Filled.Backspace,
                contentDescription = strings.clearScore,
                tint = StatusOverRed,
              )
            }
          }
          IconButton(onClick = onClose) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = strings.doneInput,
              tint = InkMuted,
            )
          }
        }
      }

      // Player Switcher Pills (1..4)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        round.players.forEach { player ->
          val isActive = player.id == selectedPlayer.id
          val pRel = round.scoresByPlayer[player.id]?.get(hole.holeNumber)
          val pGross = pRel?.let { GolfScoringCalculator.grossScore(hole.par, it) }
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isActive) FairwayDeepGreen else FairwayCardTint,
            border = if (isActive) null else BorderStroke(1.dp, FairwayBorder),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .clickable { onSelectPlayer(player.id) },
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 7.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              Text(
                text = player.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) Color.White else InkBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
              Text(
                text = if (pGross != null) "$pGross (${GolfScoringCalculator.formatRelativeToPar(pRel)})" else "-",
                fontSize = 10.sp,
                fontFamily = JetBrainsMonoFontFamily,
                color = if (isActive) ChampionshipGold else InkMuted,
                maxLines = 1,
              )
            }
          }
        }
      }

      // Current Score Status Banner (shows exact classification name: Hole in One / Albatross / Eagle / Birdie / Par / Bogey / Double Bogey / Triple Bogey / +n)
      if (currentRel != null) {
        val gross = GolfScoringCalculator.grossScore(hole.par, currentRel)
        val className = GolfScoringCalculator.scoreClassificationName(hole.par, currentRel)
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = FairwayCardTint,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = "${selectedPlayer.name} · $className",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = FairwayDeepGreen,
            )
            Text(
              text = "$gross (${GolfScoringCalculator.formatRelativeToPar(currentRel)})",
              fontFamily = JetBrainsMonoFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = InkBlack,
            )
          }
        }
      }

      // One-Handed Big Score Buttons (-3/-2/-1/0 and +1/+2/+3/+4+)
      val row1RelValues = buildList {
        if (hole.par >= 5) add(-4) // Par 5 Hole in One
        if (hole.par >= 4) add(-3) // Par 4 HIO or Par 5 Albatross
        add(-2)
        add(-1)
        add(0)
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        row1RelValues.forEach { rel ->
          val gross = GolfScoringCalculator.grossScore(hole.par, rel)
          val label = GolfScoringCalculator.scoreClassificationName(hole.par, rel)
          val isChosen = currentRel == rel
          ScoreInputTile(
            relText = GolfScoringCalculator.formatRelativeToPar(rel),
            grossText = "$gross",
            subLabel = label,
            isSelected = isChosen,
            accentColor = when {
              rel <= -2 -> EagleGoldBorder
              rel == -1 -> BirdieRed
              else -> FairwayDeepGreen
            },
            onClick = { onRecordScore(rel) },
            modifier = Modifier
              .weight(1f)
              .testTag("score_btn_$rel"),
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        listOf(1, 2, 3).forEach { rel ->
          val gross = GolfScoringCalculator.grossScore(hole.par, rel)
          val label = GolfScoringCalculator.scoreClassificationName(hole.par, rel)
          val isChosen = currentRel == rel
          ScoreInputTile(
            relText = "+$rel",
            grossText = "$gross",
            subLabel = label,
            isSelected = isChosen,
            accentColor = if (rel == 1) BogeyBlue else DoubleBogeyNavy,
            onClick = { onRecordScore(rel) },
            modifier = Modifier
              .weight(1f)
              .testTag("score_btn_+$rel"),
          )
        }
        val isFourPlusChosen = currentRel != null && currentRel >= 4
        ScoreInputTile(
          relText = if (isFourPlusChosen) "+$currentRel" else "+4+",
          grossText = if (isFourPlusChosen) "${hole.par + currentRel!!}" else "${hole.par + 4}+",
          subLabel = if (isFourPlusChosen) "+$currentRel" else strings.moreOverTitle.take(8),
          isSelected = isFourPlusChosen || showOverPicker,
          accentColor = DoubleBogeyNavy,
          onClick = { showOverPicker = !showOverPicker },
          modifier = Modifier
            .weight(1f)
            .testTag("score_btn_4plus"),
        )
      }

      // Expanded +4..+9 picker
      if (showOverPicker) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFEFF4FA),
          border = BorderStroke(1.dp, DoubleBogeyNavy.copy(alpha = 0.4f)),
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Text(
              text = strings.moreOverTitle,
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = DoubleBogeyNavy,
            )
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              (4..9).forEach { rel ->
                val gross = hole.par + rel
                val chosen = currentRel == rel
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (chosen) DoubleBogeyNavy else ScorecardWhite,
                  border = BorderStroke(1.dp, DoubleBogeyNavy),
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                      showOverPicker = false
                      onRecordScore(rel)
                    }
                    .testTag("score_btn_+$rel"),
                ) {
                  Text(
                    text = "+$rel ($gross)",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = if (chosen) Color.White else DoubleBogeyNavy,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                  )
                }
              }
            }
          }
        }
      }

      // Quick Flow Navigation (Next Player / Next Hole)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        if (round.players.size > 1) {
          OutlinedButton(
            onClick = onNextPlayer,
            modifier = Modifier.weight(1f),
            border = BorderStroke(1.dp, FairwayBorder),
          ) {
            Text(strings.nextPlayer, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
          }
        }
        OutlinedButton(
          onClick = onNextHole,
          modifier = Modifier.weight(1f),
          border = BorderStroke(1.dp, FairwayBorder),
        ) {
          Text(strings.nextHole, fontWeight = FontWeight.SemiBold)
          Spacer(modifier = Modifier.width(4.dp))
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
      }
    }
  }
}

@Composable
private fun ScoreInputTile(
  relText: String,
  grossText: String,
  subLabel: String,
  isSelected: Boolean,
  accentColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = if (isSelected) accentColor else ScorecardWhite,
    border = BorderStroke(if (isSelected) 2.dp else 1.2.dp, accentColor.copy(alpha = 0.7f)),
    modifier = modifier
      .height(72.dp)
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick),
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 4.dp, vertical = 5.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
        text = if (relText == "E") "0" else relText,
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        color = if (isSelected) Color.White else accentColor,
      )
      val strings = LocalAppStrings.current
      Text(
        text = if (strings.language == com.example.domain.model.AppLanguage.KO) {
          "${grossText}타"
        } else {
          "= $grossText"
        },
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        color = if (isSelected) Color.White.copy(alpha = 0.9f) else InkBlack,
      )
      Text(
        text = subLabel,
        fontSize = 8.5.sp,
        lineHeight = 9.5.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        color = if (isSelected) Color.White.copy(alpha = 0.9f) else InkMuted,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Composable
private fun PlayerSummaryCard(
  stat: PlayerRoundStats,
  isBackNineTab: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current
  val hasScores = stat.totalPlayedCount > 0

  val (badgeBg, badgeFg) = when {
    !hasScores -> StatusEvenBg to StatusEvenGray
    stat.vsHandicap < 0 -> StatusUnderBg to StatusUnderGreen
    stat.vsHandicap > 0 -> StatusOverBg to StatusOverRed
    else -> StatusEvenBg to StatusEvenGray
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("summary_card_p${stat.player.playerOrder}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = ScorecardWhite),
    border = BorderStroke(1.2.dp, FairwayBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      // Left: Rank, Name, Handicap, Gross & Net metrics
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          if (hasScores) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (stat.netRank == 1) ChampionshipGoldLight else FairwayCardTint,
              border = BorderStroke(1.dp, if (stat.netRank == 1) ChampionshipGold else FairwayBorder),
            ) {
              Text(
                text = strings.rankBadge(stat.netRank),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (stat.netRank == 1) ChampionshipGoldDark else FairwayDeepGreen,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
              )
            }
          }
          Text(
            text = stat.player.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = InkBlack,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Text(
            text = "${strings.hcpShort} ${stat.player.handicap}",
            style = MaterialTheme.typography.labelMedium,
            fontFamily = JetBrainsMonoFontFamily,
            color = InkMuted,
          )
        }

        // Metrics row
        if (isBackNineTab) {
          Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricPill(
              label = strings.backSummaryLabel,
              value = if (stat.backPlayedCount > 0) {
                "${stat.backGross} (${GolfScoringCalculator.formatRelativeToPar(stat.backToPar)})"
              } else "-",
            )
            MetricPill(
              label = strings.total18SummaryLabel,
              value = if (hasScores) {
                "${stat.totalGross} (${GolfScoringCalculator.formatRelativeToPar(stat.totalToPar)})"
              } else "-",
            )
            MetricPill(
              label = strings.netLabel,
              value = if (hasScores) "${stat.netScore}" else "-",
            )
          }
        } else {
          Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricPill(
              label = strings.grossLabel,
              value = if (hasScores) {
                "${stat.totalGross} (${GolfScoringCalculator.formatRelativeToPar(stat.totalToPar)})"
              } else "-",
            )
            MetricPill(
              label = strings.netLabel,
              value = if (hasScores) "${stat.netScore}" else "-",
            )
            if (hasScores) {
              MetricPill(
                label = strings.thruHoles(stat.totalPlayedCount),
                value = "OUT ${if (stat.frontPlayedCount > 0) stat.frontGross else "-"}",
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.width(10.dp))

      // Right: Prominent vs Handicap Display (▼ 2 UNDER / ▲ 3 OVER / EVEN)
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = badgeBg,
        modifier = Modifier.widthIn(min = 116.dp),
      ) {
        Column(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Text(
            text = if (hasScores) strings.vsHcpBadgeText(stat.vsHandicap) else "-",
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = badgeFg,
            maxLines = 1,
          )
          if (hasScores) {
            Text(
              text = strings.vsHcpSentence(stat.vsHandicap),
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = badgeFg,
              textAlign = TextAlign.Center,
              maxLines = 1,
            )
          } else {
            Text(
              text = strings.vsHcpHeader,
              fontSize = 10.sp,
              color = badgeFg,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun MetricPill(
  label: String,
  value: String,
) {
  Column {
    Text(
      text = label,
      fontSize = 10.sp,
      color = InkMuted,
      fontWeight = FontWeight.Medium,
    )
    Text(
      text = value,
      fontFamily = JetBrainsMonoFontFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 13.sp,
      color = InkBlack,
    )
  }
}
