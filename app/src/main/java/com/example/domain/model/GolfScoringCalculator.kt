package com.example.domain.model

enum class ScoreVisualStyle {
  EAGLE_OR_BETTER,
  BIRDIE,
  PAR,
  BOGEY,
  DOUBLE_OR_WORSE,
}

data class RoundPlayerInfo(
  val id: Long,
  val playerOrder: Int,
  val name: String,
  val handicap: Int,
)

data class PlayerRoundStats(
  val player: RoundPlayerInfo,
  val frontGross: Int,
  val frontParPlayed: Int,
  val frontToPar: Int,
  val frontAllocatedHcp: Int,
  val frontPlayedCount: Int,
  val backGross: Int,
  val backParPlayed: Int,
  val backToPar: Int,
  val backAllocatedHcp: Int,
  val backPlayedCount: Int,
  val totalGross: Int,
  val totalParPlayed: Int,
  val totalToPar: Int,
  val totalAllocatedHcp: Int,
  val totalPlayedCount: Int,
  val netScore: Int,
  val vsHandicap: Int,
  val frontNet: Int,
  val frontVsHandicap: Int,
  val backNet: Int,
  val backVsHandicap: Int,
  val netRank: Int,
)

data class RoundSummaryInfo(
  val roundId: Long,
  val dateEpochMillis: Long,
  val teeColor: TeeColor,
  val playerStats: List<PlayerRoundStats>,
  val completedHolesCount: Int,
  val updatedAt: Long,
)

object GolfScoringCalculator {

  /**
   * Allocates handicap strokes to a specific hole according to its Stroke Index (S.I. 1..18).
   * - Handicap 10 -> S.I. 1..10 get 1 stroke each.
   * - Handicap 22 -> S.I. 1..4 get 2 strokes each, S.I. 5..18 get 1 stroke each.
   * - Handicap 0..54 supported.
   */
  fun strokesAllocated(handicap: Int, strokeIndex: Int): Int {
    val clampedHcp = handicap.coerceIn(0, 54)
    val clampedSi = strokeIndex.coerceIn(1, 18)
    val base = clampedHcp / 18
    val remainder = clampedHcp % 18
    return base + if (clampedSi <= remainder) 1 else 0
  }

  fun grossScore(par: Int, relativeToPar: Int): Int = (par + relativeToPar).coerceAtLeast(1)

  fun scoreClassificationName(par: Int, relativeToPar: Int): String {
    val gross = par + relativeToPar
    if (gross == 1) return "Hole in One"
    return when {
      relativeToPar <= -3 -> "Albatross"
      relativeToPar == -2 -> "Eagle"
      relativeToPar == -1 -> "Birdie"
      relativeToPar == 0 -> "Par"
      relativeToPar == 1 -> "Bogey"
      relativeToPar == 2 -> "Double Bogey"
      relativeToPar == 3 -> "Triple Bogey"
      else -> "+$relativeToPar"
    }
  }

  fun scoreVisualStyle(relativeToPar: Int): ScoreVisualStyle =
    when {
      relativeToPar <= -2 -> ScoreVisualStyle.EAGLE_OR_BETTER
      relativeToPar == -1 -> ScoreVisualStyle.BIRDIE
      relativeToPar == 0 -> ScoreVisualStyle.PAR
      relativeToPar == 1 -> ScoreVisualStyle.BOGEY
      else -> ScoreVisualStyle.DOUBLE_OR_WORSE
    }

  fun formatRelativeToPar(relative: Int): String =
    when {
      relative == 0 -> "E"
      relative > 0 -> "+$relative"
      else -> "$relative"
    }

  /**
   * Calculates full and nine-hole statistics for all players in a round,
   * including tie-aware Net rankings (1..4).
   *
   * @param players Ordered list of players in the round
   * @param holes 18 holes of the course
   * @param scoresByPlayer Map of playerId -> Map of holeNumber -> relativeToPar
   */
  fun calculateRoundStats(
    players: List<RoundPlayerInfo>,
    holes: List<HoleConfig>,
    scoresByPlayer: Map<Long, Map<Int, Int>>,
  ): List<PlayerRoundStats> {
    val holeByNumber = holes.associateBy { it.holeNumber }

    val rawStats = players.map { player ->
      val playerScores = scoresByPlayer[player.id].orEmpty()

      var frontGross = 0
      var frontParPlayed = 0
      var frontToPar = 0
      var frontAllocatedHcp = 0
      var frontPlayedCount = 0

      var backGross = 0
      var backParPlayed = 0
      var backToPar = 0
      var backAllocatedHcp = 0
      var backPlayedCount = 0

      for ((holeNum, rel) in playerScores) {
        val hole = holeByNumber[holeNum] ?: continue
        val gross = grossScore(hole.par, rel)
        val effectiveRel = gross - hole.par
        val strokes = strokesAllocated(player.handicap, hole.strokeIndex)

        if (holeNum in 1..9) {
          frontGross += gross
          frontParPlayed += hole.par
          frontToPar += effectiveRel
          frontAllocatedHcp += strokes
          frontPlayedCount++
        } else if (holeNum in 10..18) {
          backGross += gross
          backParPlayed += hole.par
          backToPar += effectiveRel
          backAllocatedHcp += strokes
          backPlayedCount++
        }
      }

      val totalGross = frontGross + backGross
      val totalParPlayed = frontParPlayed + backParPlayed
      val totalToPar = frontToPar + backToPar
      val totalAllocatedHcp = frontAllocatedHcp + backAllocatedHcp
      val totalPlayedCount = frontPlayedCount + backPlayedCount

      val netScore = totalGross - totalAllocatedHcp
      val vsHandicap = totalToPar - totalAllocatedHcp
      val frontNet = frontGross - frontAllocatedHcp
      val frontVsHandicap = frontToPar - frontAllocatedHcp
      val backNet = backGross - backAllocatedHcp
      val backVsHandicap = backToPar - backAllocatedHcp

      PlayerRoundStats(
        player = player,
        frontGross = frontGross,
        frontParPlayed = frontParPlayed,
        frontToPar = frontToPar,
        frontAllocatedHcp = frontAllocatedHcp,
        frontPlayedCount = frontPlayedCount,
        backGross = backGross,
        backParPlayed = backParPlayed,
        backToPar = backToPar,
        backAllocatedHcp = backAllocatedHcp,
        backPlayedCount = backPlayedCount,
        totalGross = totalGross,
        totalParPlayed = totalParPlayed,
        totalToPar = totalToPar,
        totalAllocatedHcp = totalAllocatedHcp,
        totalPlayedCount = totalPlayedCount,
        netScore = netScore,
        vsHandicap = vsHandicap,
        frontNet = frontNet,
        frontVsHandicap = frontVsHandicap,
        backNet = backNet,
        backVsHandicap = backVsHandicap,
        netRank = 1,
      )
    }

    // Assign Net ranking (1..4). Players with 0 holes played rank after those with scores;
    // among players with scores, lower vsHandicap (or Net score when equal holes) ranks higher.
    val rankedIds = rawStats
      .sortedWith(
        compareBy<PlayerRoundStats> { it.totalPlayedCount == 0 }
          .thenBy { it.vsHandicap }
          .thenBy { it.netScore }
          .thenBy { it.totalGross }
          .thenBy { it.player.playerOrder }
      )

    val rankByPlayerId = mutableMapOf<Long, Int>()
    var currentRank = 1
    rankedIds.forEachIndexed { index, stats ->
      if (index > 0) {
        val prev = rankedIds[index - 1]
        val sameScore =
          (prev.totalPlayedCount == 0 && stats.totalPlayedCount == 0) ||
            (prev.totalPlayedCount > 0 &&
              stats.totalPlayedCount > 0 &&
              prev.vsHandicap == stats.vsHandicap &&
              prev.netScore == stats.netScore)
        if (!sameScore) {
          currentRank = index + 1
        }
      }
      rankByPlayerId[stats.player.id] = currentRank
    }

    return rawStats.map { stats ->
      stats.copy(netRank = rankByPlayerId[stats.player.id] ?: 1)
    }
  }
}
