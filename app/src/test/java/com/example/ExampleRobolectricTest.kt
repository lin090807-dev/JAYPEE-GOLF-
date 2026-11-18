package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.model.DefaultJaypeeGreensCourse
import com.example.domain.model.GolfScoringCalculator
import com.example.domain.model.RoundPlayerInfo
import com.example.domain.model.TeeColor
import com.example.share.ScorecardImageGenerator
import com.example.ui.i18n.KoStrings
import com.example.ui.viewmodel.ActiveRoundUiModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Jaypee Greens", appName)
  }

  @Test
  fun `render scorecard PNG bitmap produces valid non-empty image`() {
    val players = listOf(
      RoundPlayerInfo(id = 1L, playerOrder = 0, name = "김프로", handicap = 12),
      RoundPlayerInfo(id = 2L, playerOrder = 1, name = "박골퍼", handicap = 22),
    )
    val scores = mapOf(
      1L to mapOf(1 to 0, 2 to -1, 10 to 1),
      2L to mapOf(1 to 1, 2 to 2, 10 to 0),
    )
    val stats = GolfScoringCalculator.calculateRoundStats(
      players = players,
      holes = DefaultJaypeeGreensCourse.holes,
      scoresByPlayer = scores,
    )
    val round = ActiveRoundUiModel(
      roundId = 1L,
      dateEpochMillis = System.currentTimeMillis(),
      teeColor = TeeColor.BLUE,
      holes = DefaultJaypeeGreensCourse.holes,
      players = players,
      scoresByPlayer = scores,
      playerStats = stats,
    )
    val bitmap = ScorecardImageGenerator.renderScorecardBitmap(round, KoStrings)
    assertTrue(bitmap.width == 1440)
    assertTrue(bitmap.height > 1000)
    bitmap.recycle()
  }
}
