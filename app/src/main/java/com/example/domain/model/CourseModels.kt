package com.example.domain.model

import androidx.compose.ui.graphics.Color

enum class TeeColor(
  val storageKey: String,
  val labelKo: String,
  val labelEn: String,
  val dotColor: Color,
  val onDotColor: Color,
) {
  BLACK("BLACK", "블랙 티", "Black", Color(0xFF18181B), Color.White),
  GOLD("GOLD", "골드 티", "Gold", Color(0xFFD4AF37), Color(0xFF1A1400)),
  BLUE("BLUE", "블루 티", "Blue", Color(0xFF1D4ED8), Color.White),
  WHITE("WHITE", "화이트 티", "White", Color(0xFFF8FAFC), Color(0xFF0F172A)),
  RED("RED", "레드 티", "Red", Color(0xFFDC2626), Color.White);

  companion object {
    fun fromKey(key: String): TeeColor =
      entries.firstOrNull { it.storageKey.equals(key, ignoreCase = true) } ?: BLUE
  }
}

enum class AppLanguage(val code: String) {
  KO("ko"),
  EN("en");

  companion object {
    fun fromCode(code: String): AppLanguage =
      entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: KO
  }
}

data class HoleConfig(
  val holeNumber: Int,
  val par: Int,
  val strokeIndex: Int,
)

object DefaultJaypeeGreensCourse {
  const val COURSE_NAME = "Jaypee Greens Golf Club"
  const val COURSE_LOCATION = "Greater Noida"
  const val COURSE_DESIGNER = "Greg Norman Course"

  val holes: List<HoleConfig> = listOf(
    // OUT (1~9) - Par 36
    HoleConfig(holeNumber = 1, par = 4, strokeIndex = 17),
    HoleConfig(holeNumber = 2, par = 4, strokeIndex = 3),
    HoleConfig(holeNumber = 3, par = 5, strokeIndex = 9),
    HoleConfig(holeNumber = 4, par = 4, strokeIndex = 5),
    HoleConfig(holeNumber = 5, par = 3, strokeIndex = 13),
    HoleConfig(holeNumber = 6, par = 5, strokeIndex = 11),
    HoleConfig(holeNumber = 7, par = 4, strokeIndex = 1),
    HoleConfig(holeNumber = 8, par = 3, strokeIndex = 15),
    HoleConfig(holeNumber = 9, par = 4, strokeIndex = 7),
    // IN (10~18) - Par 36
    HoleConfig(holeNumber = 10, par = 4, strokeIndex = 12),
    HoleConfig(holeNumber = 11, par = 5, strokeIndex = 8),
    HoleConfig(holeNumber = 12, par = 3, strokeIndex = 14),
    HoleConfig(holeNumber = 13, par = 5, strokeIndex = 6),
    HoleConfig(holeNumber = 14, par = 4, strokeIndex = 16),
    HoleConfig(holeNumber = 15, par = 4, strokeIndex = 2),
    HoleConfig(holeNumber = 16, par = 3, strokeIndex = 18),
    HoleConfig(holeNumber = 17, par = 4, strokeIndex = 4),
    HoleConfig(holeNumber = 18, par = 4, strokeIndex = 10),
  )
}
