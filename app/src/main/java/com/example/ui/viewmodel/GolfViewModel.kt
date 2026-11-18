package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.GolfDatabase
import com.example.data.local.GolfRepository
import com.example.data.local.PlayerInputDraft
import com.example.data.local.RecentPlayerEntity
import com.example.data.local.RoundWithDetails
import com.example.domain.model.AppLanguage
import com.example.domain.model.DefaultJaypeeGreensCourse
import com.example.domain.model.GolfScoringCalculator
import com.example.domain.model.HoleConfig
import com.example.domain.model.PlayerRoundStats
import com.example.domain.model.RoundPlayerInfo
import com.example.domain.model.RoundSummaryInfo
import com.example.domain.model.TeeColor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SetupPlayerForm(
  val key: Long,
  val name: String,
  val handicapText: String,
)

data class RoundSetupUiState(
  val dateEpochMillis: Long = System.currentTimeMillis(),
  val selectedTee: TeeColor = TeeColor.BLUE,
  val players: List<SetupPlayerForm> = listOf(
    SetupPlayerForm(key = 1L, name = "", handicapText = "18"),
  ),
)

data class ActiveRoundUiModel(
  val roundId: Long,
  val dateEpochMillis: Long,
  val teeColor: TeeColor,
  val holes: List<HoleConfig>,
  val players: List<RoundPlayerInfo>,
  val scoresByPlayer: Map<Long, Map<Int, Int>>,
  val playerStats: List<PlayerRoundStats>,
)

data class ScorecardSelectionState(
  val isBackNine: Boolean = false,
  val selectedHoleNumber: Int? = 1,
  val selectedPlayerId: Long? = null,
  val isInputPanelOpen: Boolean = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
class GolfViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: GolfRepository by lazy {
    GolfRepository(GolfDatabase.getInstance(application).golfDao())
  }

  private var nextDraftKey = 2L

  private val _setupState = MutableStateFlow(RoundSetupUiState())
  val setupState: StateFlow<RoundSetupUiState> = _setupState.asStateFlow()

  private val _scorecardSelection = MutableStateFlow(ScorecardSelectionState())
  val scorecardSelection: StateFlow<ScorecardSelectionState> = _scorecardSelection.asStateFlow()

  val courseHoles: StateFlow<List<HoleConfig>> =
    repository.courseHolesFlow.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = DefaultJaypeeGreensCourse.holes,
    )

  val language: StateFlow<AppLanguage> =
    repository.preferencesFlow
      .map { AppLanguage.fromCode(it.languageCode) }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppLanguage.KO,
      )

  val recentPlayers: StateFlow<List<RecentPlayerEntity>> =
    repository.recentPlayersFlow.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList(),
    )

  val activeRoundId: StateFlow<Long?> =
    repository.preferencesFlow
      .map { it.activeRoundId }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null,
      )

  val activeRound: StateFlow<ActiveRoundUiModel?> =
    activeRoundId
      .flatMapLatest { roundId ->
        if (roundId == null) flowOf(null) else repository.observeRound(roundId)
      }
      .combine(courseHoles) { roundDetails, holes ->
        roundDetails?.toActiveRoundUiModel(holes)
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null,
      )

  val roundHistory: StateFlow<List<RoundSummaryInfo>> =
    combine(repository.allRoundsFlow, courseHoles) { rounds, holes ->
      rounds.map { it.toRoundSummaryInfo(holes) }
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList(),
    )

  init {
    viewModelScope.launch {
      repository.ensureSeeded()
    }
  }

  // --- Round Setup Actions ---

  fun setSetupDate(epochMillis: Long) {
    _setupState.update { it.copy(dateEpochMillis = epochMillis) }
  }

  fun setSetupTee(tee: TeeColor) {
    _setupState.update { it.copy(selectedTee = tee) }
  }

  fun addSetupPlayer() {
    _setupState.update { state ->
      if (state.players.size >= 4) return@update state
      val newKey = nextDraftKey++
      state.copy(
        players = state.players + SetupPlayerForm(key = newKey, name = "", handicapText = "18")
      )
    }
  }

  fun removeSetupPlayer(key: Long) {
    _setupState.update { state ->
      if (state.players.size <= 1) return@update state
      state.copy(players = state.players.filterNot { it.key == key })
    }
  }

  fun updateSetupPlayerName(key: Long, name: String) {
    _setupState.update { state ->
      state.copy(
        players = state.players.map { if (it.key == key) it.copy(name = name) else it }
      )
    }
  }

  fun updateSetupPlayerHandicap(key: Long, text: String) {
    val digitsOnly = text.filter { it.isDigit() }.take(2)
    val clampedText = if (digitsOnly.isEmpty()) {
      ""
    } else {
      digitsOnly.toInt().coerceIn(0, 54).toString()
    }
    _setupState.update { state ->
      state.copy(
        players = state.players.map { if (it.key == key) it.copy(handicapText = clampedText) else it }
      )
    }
  }

  fun stepSetupPlayerHandicap(key: Long, delta: Int) {
    _setupState.update { state ->
      state.copy(
        players = state.players.map { player ->
          if (player.key == key) {
            val current = player.handicapText.toIntOrNull() ?: 0
            player.copy(handicapText = (current + delta).coerceIn(0, 54).toString())
          } else {
            player
          }
        }
      )
    }
  }

  fun applyRecentPlayer(recent: RecentPlayerEntity) {
    _setupState.update { state ->
      // If already added, ignore or update handicap
      val existingIndex = state.players.indexOfFirst {
        it.name.trim().equals(recent.name.trim(), ignoreCase = true)
      }
      if (existingIndex >= 0) {
        val updated = state.players.toMutableList()
        updated[existingIndex] = updated[existingIndex].copy(
          name = recent.name,
          handicapText = recent.lastHandicap.coerceIn(0, 54).toString(),
        )
        return@update state.copy(players = updated)
      }
      // Fill first blank player slot if available
      val blankIndex = state.players.indexOfFirst { it.name.isBlank() }
      if (blankIndex >= 0) {
        val updated = state.players.toMutableList()
        updated[blankIndex] = updated[blankIndex].copy(
          name = recent.name,
          handicapText = recent.lastHandicap.coerceIn(0, 54).toString(),
        )
        return@update state.copy(players = updated)
      }
      // Otherwise append if < 4
      if (state.players.size < 4) {
        val newKey = nextDraftKey++
        return@update state.copy(
          players = state.players + SetupPlayerForm(
            key = newKey,
            name = recent.name,
            handicapText = recent.lastHandicap.coerceIn(0, 54).toString(),
          )
        )
      }
      state
    }
  }

  fun removeRecentPlayer(name: String) {
    viewModelScope.launch {
      repository.deleteRecentPlayer(name)
    }
  }

  fun startNewRound(defaultPrefix: String, onRoundCreated: (Long) -> Unit) {
    val snapshot = _setupState.value
    val drafts = snapshot.players.mapIndexed { idx, form ->
      val finalName = form.name.trim().ifEmpty { "$defaultPrefix ${idx + 1}" }
      val hcp = (form.handicapText.toIntOrNull() ?: 0).coerceIn(0, 54)
      PlayerInputDraft(name = finalName, handicap = hcp)
    }
    viewModelScope.launch {
      val roundId = repository.createRound(
        dateEpochMillis = snapshot.dateEpochMillis,
        teeColor = snapshot.selectedTee,
        players = drafts,
      )
      _scorecardSelection.value = ScorecardSelectionState(
        isBackNine = false,
        selectedHoleNumber = 1,
        selectedPlayerId = null,
        isInputPanelOpen = true,
      )
      onRoundCreated(roundId)
    }
  }

  // --- Scorecard Actions ---

  fun selectNineTab(isBackNine: Boolean) {
    _scorecardSelection.update { state ->
      val currentHole = state.selectedHoleNumber
      val targetHole = when {
        !isBackNine && (currentHole == null || currentHole > 9) -> 1
        isBackNine && (currentHole == null || currentHole <= 9) -> 10
        else -> currentHole
      }
      state.copy(isBackNine = isBackNine, selectedHoleNumber = targetHole)
    }
  }

  fun selectCellForInput(holeNumber: Int, playerId: Long) {
    _scorecardSelection.update {
      it.copy(
        isBackNine = holeNumber >= 10,
        selectedHoleNumber = holeNumber,
        selectedPlayerId = playerId,
        isInputPanelOpen = true,
      )
    }
  }

  fun selectHoleForInput(holeNumber: Int) {
    _scorecardSelection.update {
      it.copy(
        isBackNine = holeNumber >= 10,
        selectedHoleNumber = holeNumber,
        isInputPanelOpen = true,
      )
    }
  }

  fun selectPlayerForInput(playerId: Long) {
    _scorecardSelection.update {
      it.copy(selectedPlayerId = playerId, isInputPanelOpen = true)
    }
  }

  fun setInputPanelOpen(open: Boolean) {
    _scorecardSelection.update { it.copy(isInputPanelOpen = open) }
  }

  fun recordScoreAndAdvance(
    round: ActiveRoundUiModel,
    playerId: Long,
    holeNumber: Int,
    relativeToPar: Int,
    autoAdvancePlayer: Boolean = true,
  ) {
    viewModelScope.launch {
      repository.saveHoleScore(round.roundId, playerId, holeNumber, relativeToPar)
    }
    if (autoAdvancePlayer && round.players.size > 1) {
      val currentIdx = round.players.indexOfFirst { it.id == playerId }
      if (currentIdx in 0 until round.players.lastIndex) {
        val nextPlayer = round.players[currentIdx + 1]
        _scorecardSelection.update { it.copy(selectedPlayerId = nextPlayer.id) }
      } else if (currentIdx == round.players.lastIndex && holeNumber < 18) {
        // Optionally wrap to first player on next hole if they haven't scored it yet
        val nextHole = holeNumber + 1
        val firstPlayer = round.players.first()
        _scorecardSelection.update {
          it.copy(
            isBackNine = nextHole >= 10,
            selectedHoleNumber = nextHole,
            selectedPlayerId = firstPlayer.id,
          )
        }
      }
    } else if (round.players.size == 1 && holeNumber < 18) {
      val nextHole = holeNumber + 1
      _scorecardSelection.update {
        it.copy(
          isBackNine = nextHole >= 10,
          selectedHoleNumber = nextHole,
          selectedPlayerId = playerId,
        )
      }
    }
  }

  fun clearScore(roundId: Long, playerId: Long, holeNumber: Int) {
    viewModelScope.launch {
      repository.clearHoleScore(roundId, playerId, holeNumber)
    }
  }

  fun advanceToNextPlayer(round: ActiveRoundUiModel, currentPlayerId: Long) {
    if (round.players.isEmpty()) return
    val idx = round.players.indexOfFirst { it.id == currentPlayerId }
    val next = round.players[(idx + 1) % round.players.size]
    _scorecardSelection.update { it.copy(selectedPlayerId = next.id) }
  }

  fun advanceToNextHole(currentHoleNumber: Int) {
    val next = if (currentHoleNumber >= 18) 1 else currentHoleNumber + 1
    _scorecardSelection.update {
      it.copy(
        isBackNine = next >= 10,
        selectedHoleNumber = next,
      )
    }
  }

  // --- History Actions ---

  fun openRound(roundId: Long, onOpened: () -> Unit) {
    viewModelScope.launch {
      repository.setActiveRoundId(roundId)
      _scorecardSelection.value = ScorecardSelectionState(
        isBackNine = false,
        selectedHoleNumber = 1,
        selectedPlayerId = null,
        isInputPanelOpen = false,
      )
      onOpened()
    }
  }

  fun deleteRound(roundId: Long) {
    viewModelScope.launch {
      repository.deleteRound(roundId)
    }
  }

  // --- Settings Actions ---

  fun setLanguage(language: AppLanguage) {
    viewModelScope.launch {
      repository.setLanguage(language)
    }
  }

  fun updateHolePar(holeNumber: Int, par: Int) {
    viewModelScope.launch {
      repository.updateHolePar(holeNumber, par)
    }
  }

  fun updateHoleStrokeIndex(holeNumber: Int, si: Int) {
    viewModelScope.launch {
      repository.updateHoleStrokeIndex(holeNumber, si)
    }
  }

  fun resetCourseDefaults() {
    viewModelScope.launch {
      repository.resetCourseDefaults()
    }
  }

  // --- Mapping Helpers ---

  private fun RoundWithDetails.toActiveRoundUiModel(holes: List<HoleConfig>): ActiveRoundUiModel {
    val orderedPlayers = players
      .sortedBy { it.playerOrder }
      .map { RoundPlayerInfo(id = it.id, playerOrder = it.playerOrder, name = it.name, handicap = it.handicap) }
    val scoreMap = scores
      .groupBy { it.playerId }
      .mapValues { (_, list) -> list.associate { it.holeNumber to it.relativeToPar } }
    val stats = GolfScoringCalculator.calculateRoundStats(
      players = orderedPlayers,
      holes = holes,
      scoresByPlayer = scoreMap,
    )
    return ActiveRoundUiModel(
      roundId = round.id,
      dateEpochMillis = round.dateEpochMillis,
      teeColor = TeeColor.fromKey(round.teeColor),
      holes = holes,
      players = orderedPlayers,
      scoresByPlayer = scoreMap,
      playerStats = stats,
    )
  }

  private fun RoundWithDetails.toRoundSummaryInfo(holes: List<HoleConfig>): RoundSummaryInfo {
    val orderedPlayers = players
      .sortedBy { it.playerOrder }
      .map { RoundPlayerInfo(id = it.id, playerOrder = it.playerOrder, name = it.name, handicap = it.handicap) }
    val scoreMap = scores
      .groupBy { it.playerId }
      .mapValues { (_, list) -> list.associate { it.holeNumber to it.relativeToPar } }
    val stats = GolfScoringCalculator.calculateRoundStats(
      players = orderedPlayers,
      holes = holes,
      scoresByPlayer = scoreMap,
    )
    val maxPlayed = stats.maxOfOrNull { it.totalPlayedCount } ?: 0
    return RoundSummaryInfo(
      roundId = round.id,
      dateEpochMillis = round.dateEpochMillis,
      teeColor = TeeColor.fromKey(round.teeColor),
      playerStats = stats,
      completedHolesCount = maxPlayed,
      updatedAt = round.updatedAt,
    )
  }
}
