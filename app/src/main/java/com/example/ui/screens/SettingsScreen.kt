package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AppLanguage
import com.example.domain.model.HoleConfig
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.FairwayBorder
import com.example.ui.theme.FairwayCardTint
import com.example.ui.theme.FairwayDeepGreen
import com.example.ui.theme.InkBlack
import com.example.ui.theme.InkMuted
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ScorecardWhite
import com.example.ui.theme.StatusOverRed

@Composable
fun SettingsScreen(
  language: AppLanguage,
  holes: List<HoleConfig>,
  onLanguageChange: (AppLanguage) -> Unit,
  onUpdatePar: (holeNumber: Int, par: Int) -> Unit,
  onUpdateStrokeIndex: (holeNumber: Int, si: Int) -> Unit,
  onResetDefaults: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current
  var showResetConfirm by remember { mutableStateOf(false) }

  val outPar = remember(holes) { holes.filter { it.holeNumber in 1..9 }.sumOf { it.par } }
  val inPar = remember(holes) { holes.filter { it.holeNumber in 10..18 }.sumOf { it.par } }
  val totalPar = outPar + inPar

  if (showResetConfirm) {
    AlertDialog(
      onDismissRequest = { showResetConfirm = false },
      title = { Text(strings.resetConfirmTitle, fontWeight = FontWeight.Bold) },
      text = { Text(strings.resetConfirmMessage) },
      confirmButton = {
        TextButton(
          onClick = {
            onResetDefaults()
            showResetConfirm = false
          },
          colors = ButtonDefaults.textButtonColors(contentColor = FairwayDeepGreen),
        ) {
          Text(strings.resetAction, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirm = false }) {
          Text(strings.cancel)
        }
      },
    )
  }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 640.dp),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      item {
        Text(
          text = strings.settingsTitle,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color = FairwayDeepGreen,
        )
      }

      // Language Switcher Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = ScorecardWhite),
          border = BorderStroke(1.dp, FairwayBorder),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.Language,
                contentDescription = null,
                tint = FairwayDeepGreen,
              )
              Text(
                text = strings.languageSectionTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              LanguageOptionChip(
                label = strings.koreanLabel,
                selected = language == AppLanguage.KO,
                onClick = { onLanguageChange(AppLanguage.KO) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("lang_ko_button"),
              )
              LanguageOptionChip(
                label = strings.englishLabel,
                selected = language == AppLanguage.EN,
                onClick = { onLanguageChange(AppLanguage.EN) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("lang_en_button"),
              )
            }
          }
        }
      }

      // Course Par & S.I. Header
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = ScorecardWhite),
          border = BorderStroke(1.dp, FairwayBorder),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Text(
              text = strings.courseEditorTitle,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = FairwayDeepGreen,
            )
            Text(
              text = strings.courseEditorSubtitle,
              style = MaterialTheme.typography.bodySmall,
              color = InkMuted,
            )
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = FairwayCardTint,
              modifier = Modifier.fillMaxWidth(),
            ) {
              Text(
                text = strings.parSumLabel(outPar, inPar, totalPar),
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = FairwayDeepGreen,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              )
            }
            OutlinedButton(
              onClick = { showResetConfirm = true },
              border = BorderStroke(1.dp, StatusOverRed.copy(alpha = 0.6f)),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusOverRed),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Icon(Icons.Filled.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(strings.resetDefaults, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // 18 Holes Par & S.I. Editor List
      items(holes, key = { it.holeNumber }) { hole ->
        HoleEditorRow(
          hole = hole,
          onUpdatePar = { par -> onUpdatePar(hole.holeNumber, par) },
          onUpdateSi = { si -> onUpdateStrokeIndex(hole.holeNumber, si) },
        )
      }
    }
  }
}

@Composable
private fun LanguageOptionChip(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier
      .height(48.dp)
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(12.dp),
    color = if (selected) FairwayDeepGreen else FairwayCardTint,
    border = BorderStroke(1.dp, if (selected) FairwayDeepGreen else FairwayBorder),
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text = label,
        fontWeight = FontWeight.Bold,
        color = if (selected) Color.White else InkBlack,
      )
    }
  }
}

@Composable
private fun HoleEditorRow(
  hole: HoleConfig,
  onUpdatePar: (Int) -> Unit,
  onUpdateSi: (Int) -> Unit,
) {
  var siMenuExpanded by remember { mutableStateOf(false) }

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = ScorecardWhite),
    border = BorderStroke(1.dp, FairwayBorder),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      // Hole badge
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(FairwayDeepGreen),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = "${hole.holeNumber}",
            fontFamily = JetBrainsMonoFontFamily,
            fontWeight = FontWeight.Bold,
            color = Color.White,
          )
        }
        Text(
          text = if (hole.holeNumber <= 9) "OUT" else "IN",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = InkMuted,
        )
      }

      // Par 3 / 4 / 5 chips
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        listOf(3, 4, 5).forEach { parVal ->
          val selected = hole.par == parVal
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (selected) FairwayDeepGreen else FairwayCardTint,
            border = BorderStroke(1.dp, if (selected) FairwayDeepGreen else FairwayBorder),
            modifier = Modifier
              .size(width = 40.dp, height = 38.dp)
              .clip(RoundedCornerShape(8.dp))
              .clickable { onUpdatePar(parVal) }
              .testTag("hole_${hole.holeNumber}_par_$parVal"),
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "P$parVal",
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else InkBlack,
              )
            }
          }
        }
      }

      // S.I. Selector Dropdown (1..18)
      Box {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = FairwayCardTint,
          border = BorderStroke(1.dp, FairwayBorder),
          modifier = Modifier
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { siMenuExpanded = true }
            .testTag("hole_${hole.holeNumber}_si_picker"),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
          ) {
            Text(
              text = "S.I. ${hole.strokeIndex}",
              fontFamily = JetBrainsMonoFontFamily,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = FairwayDeepGreen,
            )
            Icon(
              imageVector = Icons.Filled.ArrowDropDown,
              contentDescription = "Select S.I.",
              tint = FairwayDeepGreen,
              modifier = Modifier.size(18.dp),
            )
          }
        }
        DropdownMenu(
          expanded = siMenuExpanded,
          onDismissRequest = { siMenuExpanded = false },
        ) {
          (1..18).forEach { siOption ->
            DropdownMenuItem(
              text = {
                Text(
                  text = "S.I. $siOption",
                  fontFamily = JetBrainsMonoFontFamily,
                  fontWeight = if (siOption == hole.strokeIndex) FontWeight.Bold else FontWeight.Normal,
                )
              },
              onClick = {
                siMenuExpanded = false
                onUpdateSi(siOption)
              },
            )
            if (siOption == 9) {
              HorizontalDivider()
            }
          }
        }
      }
    }
  }
}
