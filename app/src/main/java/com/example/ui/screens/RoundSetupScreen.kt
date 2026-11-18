package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GolfCourse
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.RecentPlayerEntity
import com.example.domain.model.TeeColor
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.ChampionshipGold
import com.example.ui.theme.FairwayBorder
import com.example.ui.theme.FairwayCardTint
import com.example.ui.theme.FairwayDeepGreen
import com.example.ui.theme.FairwayEmerald
import com.example.ui.theme.InkBlack
import com.example.ui.theme.InkMuted
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ScorecardWhite
import com.example.ui.viewmodel.RoundSetupUiState
import com.example.ui.viewmodel.SetupPlayerForm
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RoundSetupScreen(
  setupState: RoundSetupUiState,
  recentPlayers: List<RecentPlayerEntity>,
  totalCoursePar: Int,
  onDateSelected: (Long) -> Unit,
  onTeeSelected: (TeeColor) -> Unit,
  onAddPlayer: () -> Unit,
  onRemovePlayer: (Long) -> Unit,
  onPlayerNameChange: (Long, String) -> Unit,
  onPlayerHandicapChange: (Long, String) -> Unit,
  onStepHandicap: (Long, Int) -> Unit,
  onSelectRecentPlayer: (RecentPlayerEntity) -> Unit,
  onDeleteRecentPlayer: (String) -> Unit,
  onStartRound: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current
  var showDatePicker by remember { mutableStateOf(false) }

  val dateText = remember(setupState.dateEpochMillis) {
    SimpleDateFormat("yyyy-MM-dd (EEE)", Locale.getDefault()).format(Date(setupState.dateEpochMillis))
  }

  if (showDatePicker) {
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = setupState.dateEpochMillis)
    DatePickerDialog(
      onDismissRequest = { showDatePicker = false },
      confirmButton = {
        TextButton(
          onClick = {
            pickerState.selectedDateMillis?.let(onDateSelected)
            showDatePicker = false
          },
        ) {
          Text("OK", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDatePicker = false }) {
          Text(strings.cancel)
        }
      },
    ) {
      DatePicker(state = pickerState)
    }
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
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // Hero Banner Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(168.dp),
          ) {
            Image(
              painter = painterResource(id = R.drawable.img_course_hero_1794973763857),
              contentDescription = strings.clubName,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize(),
            )
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    colors = listOf(
                      FairwayDeepGreen.copy(alpha = 0.25f),
                      FairwayDeepGreen.copy(alpha = 0.92f),
                    ),
                  )
                ),
            )
            Column(
              modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Icon(
                  imageVector = Icons.Filled.GolfCourse,
                  contentDescription = null,
                  tint = ChampionshipGold,
                  modifier = Modifier.size(18.dp),
                )
                Text(
                  text = "GREATER NOIDA · GREG NORMAN",
                  style = MaterialTheme.typography.labelSmall,
                  color = ChampionshipGold,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp,
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = strings.clubName,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
              )
              Text(
                text = "${strings.clubSubtitle.substringBefore("Par")}Par $totalCoursePar",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFD9EAE0),
              )
            }
          }
        }
      }

      // Date & Tee Selection Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = ScorecardWhite),
          border = androidx.compose.foundation.BorderStroke(1.dp, FairwayBorder),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            // Date Row
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(FairwayCardTint)
                .clickable { showDatePicker = true }
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .testTag("round_date_button"),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Icon(
                  imageVector = Icons.Filled.CalendarToday,
                  contentDescription = strings.roundDateLabel,
                  tint = FairwayDeepGreen,
                  modifier = Modifier.size(20.dp),
                )
                Column {
                  Text(
                    text = strings.roundDateLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = InkMuted,
                  )
                  Text(
                    text = dateText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InkBlack,
                  )
                }
              }
              Text(
                text = strings.changeDate,
                style = MaterialTheme.typography.labelLarge,
                color = FairwayDeepGreen,
              )
            }

            // Tee Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                text = strings.teeSelectLabel,
                style = MaterialTheme.typography.titleSmall,
                color = FairwayDeepGreen,
                fontWeight = FontWeight.Bold,
              )
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                TeeColor.entries.forEach { tee ->
                  val selected = setupState.selectedTee == tee
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selected) FairwayDeepGreen else ScorecardWhite,
                    border = androidx.compose.foundation.BorderStroke(
                      width = if (selected) 2.dp else 1.dp,
                      color = if (selected) FairwayDeepGreen else FairwayBorder,
                    ),
                    modifier = Modifier
                      .clip(RoundedCornerShape(12.dp))
                      .clickable { onTeeSelected(tee) }
                      .testTag("tee_${tee.storageKey.lowercase()}"),
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                      Box(
                        modifier = Modifier
                          .size(14.dp)
                          .clip(CircleShape)
                          .background(tee.dotColor)
                          .border(1.dp, Color(0xFF94A3B8), CircleShape),
                      )
                      Text(
                        text = strings.teeName(tee),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) Color.White else InkBlack,
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      // Recent Players Quick Pick
      if (recentPlayers.isNotEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ScorecardWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, FairwayBorder),
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Text(
                text = strings.recentPlayersLabel,
                style = MaterialTheme.typography.titleSmall,
                color = FairwayDeepGreen,
                fontWeight = FontWeight.Bold,
              )
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                recentPlayers.forEach { recent ->
                  FilterChip(
                    selected = setupState.players.any {
                      it.name.trim().equals(recent.name.trim(), ignoreCase = true)
                    },
                    onClick = { onSelectRecentPlayer(recent) },
                    label = {
                      Text(
                        text = "${recent.name} (${recent.lastHandicap})",
                        fontWeight = FontWeight.SemiBold,
                      )
                    },
                    trailingIcon = {
                      Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = strings.delete,
                        modifier = Modifier
                          .size(16.dp)
                          .clickable { onDeleteRecentPlayer(recent.name) },
                      )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                      selectedContainerColor = FairwayCardTint,
                      selectedLabelColor = FairwayDeepGreen,
                    ),
                  )
                }
              }
            }
          }
        }
      }

      // Players Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column {
            Text(
              text = strings.playersLabel,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = InkBlack,
            )
            Text(
              text = strings.playerCountMaxHint,
              style = MaterialTheme.typography.bodySmall,
              color = InkMuted,
            )
          }
          if (setupState.players.size < 4) {
            OutlinedButton(
              onClick = onAddPlayer,
              border = androidx.compose.foundation.BorderStroke(1.5.dp, FairwayDeepGreen),
              modifier = Modifier.testTag("add_player_button"),
            ) {
              Icon(
                imageVector = Icons.Filled.PersonAdd,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(strings.addPlayer, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // Player Input Cards (1..4)
      itemsIndexed(setupState.players, key = { _, p -> p.key }) { index, player ->
        PlayerSetupCard(
          index = index,
          player = player,
          canRemove = setupState.players.size > 1,
          onNameChange = { onPlayerNameChange(player.key, it) },
          onHandicapChange = { onPlayerHandicapChange(player.key, it) },
          onStepHandicap = { onStepHandicap(player.key, it) },
          onRemove = { onRemovePlayer(player.key) },
        )
      }

      // Start Round Button
      item {
        Button(
          onClick = onStartRound,
          modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .testTag("start_round_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = FairwayDeepGreen,
            contentColor = Color.White,
          ),
        ) {
          Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = strings.startRoundButton,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
          )
        }
        Spacer(modifier = Modifier.height(12.dp))
      }
    }
  }
}

@Composable
private fun PlayerSetupCard(
  index: Int,
  player: SetupPlayerForm,
  canRemove: Boolean,
  onNameChange: (String) -> Unit,
  onHandicapChange: (String) -> Unit,
  onStepHandicap: (Int) -> Unit,
  onRemove: () -> Unit,
) {
  val strings = LocalAppStrings.current
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = ScorecardWhite),
    border = androidx.compose.foundation.BorderStroke(1.dp, FairwayBorder),
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
              .size(28.dp)
              .clip(CircleShape)
              .background(FairwayDeepGreen),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              text = "${index + 1}",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontFamily = JetBrainsMonoFontFamily,
              fontSize = 14.sp,
            )
          }
          Text(
            text = "${strings.playerDefaultPrefix} ${index + 1}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = FairwayDeepGreen,
          )
        }
        if (canRemove) {
          IconButton(
            onClick = onRemove,
            modifier = Modifier.size(40.dp),
          ) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = strings.delete,
              tint = InkMuted,
            )
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        OutlinedTextField(
          value = player.name,
          onValueChange = onNameChange,
          placeholder = { Text(strings.playerNameHint) },
          singleLine = true,
          modifier = Modifier
            .weight(1.3f)
            .testTag("player_name_input_$index"),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FairwayDeepGreen,
            unfocusedBorderColor = FairwayBorder,
          ),
        )

        // Handicap Stepper + Input (0..54)
        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Text(
            text = strings.handicapLabel,
            style = MaterialTheme.typography.labelSmall,
            color = InkMuted,
            maxLines = 1,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = FairwayCardTint,
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { onStepHandicap(-1) },
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Filled.Remove,
                  contentDescription = "-1",
                  tint = FairwayDeepGreen,
                  modifier = Modifier.size(18.dp),
                )
              }
            }

            OutlinedTextField(
              value = player.handicapText,
              onValueChange = onHandicapChange,
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              textStyle = MaterialTheme.typography.titleMedium.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
              ),
              modifier = Modifier
                .width(58.dp)
                .testTag("player_hcp_input_$index"),
              shape = RoundedCornerShape(10.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FairwayDeepGreen,
                unfocusedBorderColor = FairwayBorder,
              ),
            )

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = FairwayCardTint,
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { onStepHandicap(1) },
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Filled.Add,
                  contentDescription = "+1",
                  tint = FairwayDeepGreen,
                  modifier = Modifier.size(18.dp),
                )
              }
            }
          }
        }
      }
    }
  }
}
