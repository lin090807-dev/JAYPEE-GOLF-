package com.example.data.local

import com.example.domain.model.AppLanguage
import com.example.domain.model.DefaultJaypeeGreensCourse
import com.example.domain.model.HoleConfig
import com.example.domain.model.TeeColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class PlayerInputDraft(
  val name: String,
  val handicap: Int,
)

class GolfRepository(private val dao: GolfDao) {

  val courseHolesFlow: Flow<List<HoleConfig>> =
    dao.observeCourseHoles().map { entities ->
      if (entities.size == 18) {
        entities.map { HoleConfig(it.holeNumber, it.par, it.strokeIndex) }
      } else {
        DefaultJaypeeGreensCourse.holes
      }
    }

  val preferencesFlow: Flow<AppPreferencesEntity> =
    dao.observePreferences().map { it ?: AppPreferencesEntity() }

  val recentPlayersFlow: Flow<List<RecentPlayerEntity>> = dao.observeRecentPlayers()

  val allRoundsFlow: Flow<List<RoundWithDetails>> = dao.observeAllRoundsWithDetails()

  fun observeRound(roundId: Long): Flow<RoundWithDetails?> = dao.observeRoundWithDetails(roundId)

  suspend fun ensureSeeded() {
    if (dao.countCourseHoles() < 18) {
      val entities = DefaultJaypeeGreensCourse.holes.map {
        CourseHoleEntity(it.holeNumber, it.par, it.strokeIndex)
      }
      dao.upsertCourseHoles(entities)
    }
    if (dao.getPreferencesOnce() == null) {
      dao.upsertPreferences(AppPreferencesEntity(id = 1, languageCode = AppLanguage.KO.code))
    }
  }

  suspend fun createRound(
    dateEpochMillis: Long,
    teeColor: TeeColor,
    players: List<PlayerInputDraft>,
  ): Long {
    val now = System.currentTimeMillis()
    val roundId = dao.insertRound(
      RoundEntity(
        dateEpochMillis = dateEpochMillis,
        teeColor = teeColor.storageKey,
        createdAt = now,
        updatedAt = now,
      )
    )
    val cleanedPlayers = players.mapIndexed { index, draft ->
      RoundPlayerEntity(
        roundId = roundId,
        playerOrder = index,
        name = draft.name.trim(),
        handicap = draft.handicap.coerceIn(0, 54),
      )
    }
    dao.insertRoundPlayers(cleanedPlayers)

    val recentEntities = cleanedPlayers.map {
      RecentPlayerEntity(
        name = it.name,
        lastHandicap = it.handicap,
        lastUsedAt = now,
      )
    }
    dao.upsertRecentPlayers(recentEntities)

    val currentPrefs = dao.getPreferencesOnce() ?: AppPreferencesEntity()
    dao.upsertPreferences(currentPrefs.copy(activeRoundId = roundId))
    return roundId
  }

  suspend fun setActiveRoundId(roundId: Long?) {
    val currentPrefs = dao.getPreferencesOnce() ?: AppPreferencesEntity()
    dao.upsertPreferences(currentPrefs.copy(activeRoundId = roundId))
  }

  suspend fun setLanguage(language: AppLanguage) {
    val currentPrefs = dao.getPreferencesOnce() ?: AppPreferencesEntity()
    dao.upsertPreferences(currentPrefs.copy(languageCode = language.code))
  }

  suspend fun saveHoleScore(roundId: Long, playerId: Long, holeNumber: Int, relativeToPar: Int) {
    dao.upsertHoleScore(
      HoleScoreEntity(
        roundId = roundId,
        playerId = playerId,
        holeNumber = holeNumber,
        relativeToPar = relativeToPar,
      )
    )
    dao.updateRoundTimestamp(roundId, System.currentTimeMillis())
  }

  suspend fun clearHoleScore(roundId: Long, playerId: Long, holeNumber: Int) {
    dao.deleteHoleScore(roundId, playerId, holeNumber)
    dao.updateRoundTimestamp(roundId, System.currentTimeMillis())
  }

  suspend fun deleteRound(roundId: Long) {
    val currentPrefs = dao.getPreferencesOnce()
    if (currentPrefs?.activeRoundId == roundId) {
      dao.upsertPreferences(currentPrefs.copy(activeRoundId = null))
    }
    dao.deleteRoundById(roundId)
  }

  suspend fun deleteRecentPlayer(name: String) {
    dao.deleteRecentPlayer(name)
  }

  suspend fun updateHolePar(holeNumber: Int, newPar: Int) {
    val holes = dao.getCourseHolesOnce().ifEmpty {
      DefaultJaypeeGreensCourse.holes.map { CourseHoleEntity(it.holeNumber, it.par, it.strokeIndex) }
    }
    val target = holes.firstOrNull { it.holeNumber == holeNumber } ?: return
    dao.upsertCourseHole(target.copy(par = newPar.coerceIn(3, 5)))
  }

  /**
   * Updates the Stroke Index of a hole and automatically swaps with whichever hole
   * currently holds [newStrokeIndex], keeping S.I. a valid 1..18 permutation.
   */
  suspend fun updateHoleStrokeIndex(holeNumber: Int, newStrokeIndex: Int) {
    val clampedSi = newStrokeIndex.coerceIn(1, 18)
    val holes = dao.getCourseHolesOnce().ifEmpty {
      DefaultJaypeeGreensCourse.holes.map { CourseHoleEntity(it.holeNumber, it.par, it.strokeIndex) }
    }
    val currentHole = holes.firstOrNull { it.holeNumber == holeNumber } ?: return
    if (currentHole.strokeIndex == clampedSi) return
    val otherHole = holes.firstOrNull { it.strokeIndex == clampedSi && it.holeNumber != holeNumber }
    val updated = buildList {
      add(currentHole.copy(strokeIndex = clampedSi))
      if (otherHole != null) {
        add(otherHole.copy(strokeIndex = currentHole.strokeIndex))
      }
    }
    dao.upsertCourseHoles(updated)
  }

  suspend fun resetCourseDefaults() {
    val entities = DefaultJaypeeGreensCourse.holes.map {
      CourseHoleEntity(it.holeNumber, it.par, it.strokeIndex)
    }
    dao.upsertCourseHoles(entities)
  }
}
