package com.example

import com.example.domain.model.DefaultJaypeeGreensCourse
import com.example.domain.model.GolfScoringCalculator
import com.example.domain.model.RoundPlayerInfo
import com.example.domain.model.ScoreVisualStyle
import org.junit.Assert.assertEquals
import org.junit.Test

class GolfScoringCalculatorTest {

  @Test
  fun `default course has 18 holes with OUT 36 IN 36 Total 72 and unique SI 1 to 18`() {
    val holes = DefaultJaypeeGreensCourse.holes
    assertEquals(18, holes.size)
    assertEquals(36, holes.filter { it.holeNumber in 1..9 }.sumOf { it.par })
    assertEquals(36, holes.filter { it.holeNumber in 10..18 }.sumOf { it.par })
    assertEquals(72, holes.sumOf { it.par })
    assertEquals((1..18).toSet(), holes.map { it.strokeIndex }.toSet())
  }

  @Test
  fun `handicap allocation distributes strokes by SI including above 18`() {
    // Handicap 10 -> S.I. 1..10 get 1, 11..18 get 0
    for (si in 1..18) {
      val expected = if (si <= 10) 1 else 0
      assertEquals(expected, GolfScoringCalculator.strokesAllocated(10, si))
    }

    // Handicap 22 -> S.I. 1..4 get 2, 5..18 get 1
    for (si in 1..18) {
      val expected = if (si <= 4) 2 else 1
      assertEquals(expected, GolfScoringCalculator.strokesAllocated(22, si))
    }

    assertEquals(0, GolfScoringCalculator.strokesAllocated(0, 1))
    assertEquals(3, GolfScoringCalculator.strokesAllocated(54, 18))
  }

  @Test
  fun `score classification handles Hole in One, Albatross, Eagle, Birdie, Par, Bogey, Double, Triple, and +n`() {
    assertEquals("Hole in One", GolfScoringCalculator.scoreClassificationName(par = 3, relativeToPar = -2))
    assertEquals("Hole in One", GolfScoringCalculator.scoreClassificationName(par = 4, relativeToPar = -3))
    assertEquals("Albatross", GolfScoringCalculator.scoreClassificationName(par = 5, relativeToPar = -3))
    assertEquals("Eagle", GolfScoringCalculator.scoreClassificationName(par = 4, relativeToPar = -2))
    assertEquals("Birdie", GolfScoringCalculator.scoreClassificationName(par = 4, relativeToPar = -1))
    assertEquals("Par", GolfScoringCalculator.scoreClassificationName(par = 4, relativeToPar = 0))
    assertEquals("Bogey", GolfScoringCalculator.scoreClassificationName(par = 4, relativeToPar = 1))
    assertEquals("Double Bogey", GolfScoringCalculator.scoreClassificationName(par = 4, relativeToPar = 2))
    assertEquals("Triple Bogey", GolfScoringCalculator.scoreClassificationName(par = 4, relativeToPar = 3))
    assertEquals("+4", GolfScoringCalculator.scoreClassificationName(par = 4, relativeToPar = 4))
    assertEquals("+6", GolfScoringCalculator.scoreClassificationName(par = 5, relativeToPar = 6))

    assertEquals(ScoreVisualStyle.EAGLE_OR_BETTER, GolfScoringCalculator.scoreVisualStyle(-3))
    assertEquals(ScoreVisualStyle.EAGLE_OR_BETTER, GolfScoringCalculator.scoreVisualStyle(-2))
    assertEquals(ScoreVisualStyle.BIRDIE, GolfScoringCalculator.scoreVisualStyle(-1))
    assertEquals(ScoreVisualStyle.PAR, GolfScoringCalculator.scoreVisualStyle(0))
    assertEquals(ScoreVisualStyle.BOGEY, GolfScoringCalculator.scoreVisualStyle(1))
    assertEquals(ScoreVisualStyle.DOUBLE_OR_WORSE, GolfScoringCalculator.scoreVisualStyle(2))
    assertEquals(ScoreVisualStyle.DOUBLE_OR_WORSE, GolfScoringCalculator.scoreVisualStyle(5))
  }

  @Test
  fun `mid-round and full-round Net and vs-Handicap calculations and Net ranking`() {
    val players = listOf(
      RoundPlayerInfo(id = 1L, playerOrder = 0, name = "Kim", handicap = 18),
      RoundPlayerInfo(id = 2L, playerOrder = 1, name = "Lee", handicap = 0),
    )
    // Play Holes 1 (Par 4, SI 17) and 2 (Par 4, SI 3)
    // Kim (HCP 18 -> 1 stroke per hole = 2 strokes allocated):
    //   Hole 1: +1 (5), Hole 2: 0 (4) -> Gross 9, toPar +1, vsHCP = +1 - 2 = -1 (1 UNDER), Net = 9 - 2 = 7
    // Lee (HCP 0 -> 0 strokes allocated):
    //   Hole 1: 0 (4), Hole 2: +1 (5) -> Gross 9, toPar +1, vsHCP = +1 - 0 = +1 (1 OVER), Net = 9 - 0 = 9
    val scores = mapOf(
      1L to mapOf(1 to 1, 2 to 0),
      2L to mapOf(1 to 0, 2 to 1),
    )

    val stats = GolfScoringCalculator.calculateRoundStats(
      players = players,
      holes = DefaultJaypeeGreensCourse.holes,
      scoresByPlayer = scores,
    )

    val kim = stats.first { it.player.id == 1L }
    val lee = stats.first { it.player.id == 2L }

    assertEquals(9, kim.totalGross)
    assertEquals(7, kim.netScore)
    assertEquals(-1, kim.vsHandicap)
    assertEquals(1, kim.netRank)

    assertEquals(9, lee.totalGross)
    assertEquals(9, lee.netScore)
    assertEquals(1, lee.vsHandicap)
    assertEquals(2, lee.netRank)
  }
}
