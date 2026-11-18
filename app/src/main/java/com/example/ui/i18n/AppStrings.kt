package com.example.ui.i18n

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.domain.model.AppLanguage
import com.example.domain.model.TeeColor
import kotlin.math.abs

interface AppStrings {
  val language: AppLanguage
  val clubName: String
  val clubSubtitle: String
  val navNewRound: String
  val navScorecard: String
  val navHistory: String
  val navSettings: String

  // Round Setup
  val newRoundTitle: String
  val roundDateLabel: String
  val changeDate: String
  val teeSelectLabel: String
  val playersLabel: String
  val addPlayer: String
  val recentPlayersLabel: String
  val playerNameHint: String
  val handicapLabel: String
  val startRoundButton: String
  val playerDefaultPrefix: String
  val playerCountMaxHint: String

  // Scorecard
  val frontNineTab: String
  val backNineTab: String
  val holeCol: String
  val parCol: String
  val siCol: String
  val outCol: String
  val inCol: String
  val total18Col: String
  val shareButton: String
  val shareChooserTitle: String
  val noActiveRoundTitle: String
  val noActiveRoundSubtitle: String
  val goToNewRound: String
  val goToHistory: String
  val quickInputTitle: String
  val tapCellToScore: String
  val clearScore: String
  val nextPlayer: String
  val nextHole: String
  val doneInput: String
  val moreOverTitle: String
  val hcpDotsHint: String
  val thruHoles: (Int) -> String
  val rankBadge: (Int) -> String
  val grossLabel: String
  val netLabel: String
  val hcpShort: String
  val vsHcpHeader: String
  val frontSummaryLabel: String
  val backSummaryLabel: String
  val total18SummaryLabel: String
  val vsHcpBadgeText: (Int) -> String
  val vsHcpSentence: (Int) -> String
  val strokesReceiving: (Int) -> String
  val legendTitle: String
  val legendEagle: String
  val legendBirdie: String
  val legendPar: String
  val legendBogey: String
  val legendDouble: String

  // History
  val historyTitle: String
  val emptyHistoryTitle: String
  val emptyHistorySubtitle: String
  val openRound: String
  val deleteRound: String
  val deleteConfirmTitle: String
  val deleteConfirmMessage: String
  val cancel: String
  val delete: String
  val holesProgress: (Int) -> String

  // Settings
  val settingsTitle: String
  val languageSectionTitle: String
  val koreanLabel: String
  val englishLabel: String
  val courseEditorTitle: String
  val courseEditorSubtitle: String
  val resetDefaults: String
  val resetConfirmTitle: String
  val resetConfirmMessage: String
  val resetAction: String
  val parSumLabel: (Int, Int, Int) -> String
  val teeName: (TeeColor) -> String
}

object KoStrings : AppStrings {
  override val language = AppLanguage.KO
  override val clubName = "Jaypee Greens Golf Club"
  override val clubSubtitle = "Greg Norman 18홀 코스 · Par 72"
  override val navNewRound = "새 라운드"
  override val navScorecard = "스코어카드"
  override val navHistory = "라운드 기록"
  override val navSettings = "설정"

  override val newRoundTitle = "라운드 시작"
  override val roundDateLabel = "라운드 날짜"
  override val changeDate = "날짜 변경"
  override val teeSelectLabel = "티 선택"
  override val playersLabel = "플레이어 (최대 4명)"
  override val addPlayer = "플레이어 추가"
  override val recentPlayersLabel = "최근 플레이어 빠른 선택"
  override val playerNameHint = "이름 입력"
  override val handicapLabel = "핸디캡 (0~54)"
  override val startRoundButton = "라운드 시작하기"
  override val playerDefaultPrefix = "플레이어"
  override val playerCountMaxHint = "1~4명의 플레이어를 등록하세요"

  override val frontNineTab = "전반 OUT 1~9홀"
  override val backNineTab = "후반 IN 10~18홀"
  override val holeCol = "HOLE"
  override val parCol = "PAR"
  override val siCol = "S.I."
  override val outCol = "OUT"
  override val inCol = "IN"
  override val total18Col = "TOTAL"
  override val shareButton = "공유"
  override val shareChooserTitle = "스코어카드 이미지 공유"
  override val noActiveRoundTitle = "진행 중인 라운드가 없습니다"
  override val noActiveRoundSubtitle = "새 라운드를 시작하거나 저장된 라운드 기록을 열어보세요."
  override val goToNewRound = "새 라운드 시작"
  override val goToHistory = "라운드 기록 보기"
  override val quickInputTitle = "빠른 스코어 입력"
  override val tapCellToScore = "표의 칸을 누르면 한 손으로 바로 스코어를 입력할 수 있습니다."
  override val clearScore = "지우기"
  override val nextPlayer = "다음 플레이어"
  override val nextHole = "다음 홀"
  override val doneInput = "닫기"
  override val moreOverTitle = "+4 이상 직접 선택"
  override val hcpDotsHint = "● 핸디캡 1타 배분 홀"
  override val thruHoles: (Int) -> String = { n -> "${n}홀 완료" }
  override val rankBadge: (Int) -> String = { r -> "${r}위" }
  override val grossLabel = "Gross"
  override val netLabel = "Net"
  override val hcpShort = "핸디"
  override val vsHcpHeader = "핸디 대비"
  override val frontSummaryLabel = "전반 OUT"
  override val backSummaryLabel = "후반 IN"
  override val total18SummaryLabel = "18홀 합계"
  override val vsHcpBadgeText: (Int) -> String = { diff ->
    when {
      diff < 0 -> "▼ ${abs(diff)} UNDER"
      diff > 0 -> "▲ $diff OVER"
      else -> "EVEN"
    }
  }
  override val vsHcpSentence: (Int) -> String = { diff ->
    when {
      diff < 0 -> "핸디 대비 ${abs(diff)}개 언더"
      diff > 0 -> "핸디 대비 ${diff}개 오버"
      else -> "핸디 이븐"
    }
  }
  override val strokesReceiving: (Int) -> String = { s ->
    if (s > 0) "핸디캡 +${s}타 홀" else "핸디캡 배분 없음"
  }
  override val legendTitle = "스코어 표기"
  override val legendEagle = "이글 이하"
  override val legendBirdie = "버디"
  override val legendPar = "파"
  override val legendBogey = "보기"
  override val legendDouble = "더블+"

  override val historyTitle = "라운드 기록"
  override val emptyHistoryTitle = "저장된 라운드가 없습니다"
  override val emptyHistorySubtitle = "새 라운드를 시작하면 자동으로 기기에 저장됩니다."
  override val openRound = "열기 / 수정"
  override val deleteRound = "삭제"
  override val deleteConfirmTitle = "라운드 삭제"
  override val deleteConfirmMessage = "이 라운드 기록을 삭제하시겠습니까? 삭제 후 복구할 수 없습니다."
  override val cancel = "취소"
  override val delete = "삭제"
  override val holesProgress: (Int) -> String = { n -> "$n / 18홀" }

  override val settingsTitle = "설정"
  override val languageSectionTitle = "언어 설정"
  override val koreanLabel = "한국어 (기본)"
  override val englishLabel = "English"
  override val courseEditorTitle = "코스 Par / S.I. 편집"
  override val courseEditorSubtitle = "S.I. 변경 시 중복 방지를 위해 기존 홀과 자동 교환됩니다."
  override val resetDefaults = "Jaypee Greens 기본값 복원"
  override val resetConfirmTitle = "기본 코스 데이터 복원"
  override val resetConfirmMessage = "1~18번 홀의 Par와 S.I.를 Jaypee Greens Greg Norman 기본값(Par 72)으로 되돌릴까요?"
  override val resetAction = "복원"
  override val parSumLabel: (Int, Int, Int) -> String = { outP, inP, totP ->
    "OUT Par $outP · IN Par $inP · 합계 Par $totP"
  }
  override val teeName: (TeeColor) -> String = { it.labelKo }
}

object EnStrings : AppStrings {
  override val language = AppLanguage.EN
  override val clubName = "Jaypee Greens Golf Club"
  override val clubSubtitle = "Greg Norman 18-Hole · Par 72"
  override val navNewRound = "New Round"
  override val navScorecard = "Scorecard"
  override val navHistory = "History"
  override val navSettings = "Settings"

  override val newRoundTitle = "Start New Round"
  override val roundDateLabel = "Round Date"
  override val changeDate = "Change"
  override val teeSelectLabel = "Select Tee"
  override val playersLabel = "Players (Up to 4)"
  override val addPlayer = "Add Player"
  override val recentPlayersLabel = "Recent Players"
  override val playerNameHint = "Player name"
  override val handicapLabel = "Handicap (0–54)"
  override val startRoundButton = "Start Round"
  override val playerDefaultPrefix = "Player"
  override val playerCountMaxHint = "Add 1 to 4 players"

  override val frontNineTab = "OUT (Holes 1–9)"
  override val backNineTab = "IN (Holes 10–18)"
  override val holeCol = "HOLE"
  override val parCol = "PAR"
  override val siCol = "S.I."
  override val outCol = "OUT"
  override val inCol = "IN"
  override val total18Col = "TOTAL"
  override val shareButton = "Share"
  override val shareChooserTitle = "Share Scorecard Image"
  override val noActiveRoundTitle = "No Active Round"
  override val noActiveRoundSubtitle = "Start a new round or open a saved round from History."
  override val goToNewRound = "New Round"
  override val goToHistory = "View History"
  override val quickInputTitle = "Quick Score Entry"
  override val tapCellToScore = "Tap any cell in the scorecard to enter scores with one hand."
  override val clearScore = "Clear"
  override val nextPlayer = "Next Player"
  override val nextHole = "Next Hole"
  override val doneInput = "Done"
  override val moreOverTitle = "Select +4 or Higher"
  override val hcpDotsHint = "● Stroke hole"
  override val thruHoles: (Int) -> String = { n -> "Thru $n" }
  override val rankBadge: (Int) -> String = { r ->
    when (r) {
      1 -> "1st"
      2 -> "2nd"
      3 -> "3rd"
      else -> "${r}th"
    }
  }
  override val grossLabel = "Gross"
  override val netLabel = "Net"
  override val hcpShort = "HCP"
  override val vsHcpHeader = "vs HCP"
  override val frontSummaryLabel = "OUT (1–9)"
  override val backSummaryLabel = "IN (10–18)"
  override val total18SummaryLabel = "18H Total"
  override val vsHcpBadgeText: (Int) -> String = { diff ->
    when {
      diff < 0 -> "▼ ${abs(diff)} UNDER"
      diff > 0 -> "▲ $diff OVER"
      else -> "EVEN"
    }
  }
  override val vsHcpSentence: (Int) -> String = { diff ->
    when {
      diff < 0 -> "${abs(diff)} under handicap"
      diff > 0 -> "$diff over handicap"
      else -> "Even with handicap"
    }
  }
  override val strokesReceiving: (Int) -> String = { s ->
    if (s > 0) "+$s HCP stroke(s)" else "No HCP stroke"
  }
  override val legendTitle = "Legend"
  override val legendEagle = "Eagle-"
  override val legendBirdie = "Birdie"
  override val legendPar = "Par"
  override val legendBogey = "Bogey"
  override val legendDouble = "Double+"

  override val historyTitle = "Round History"
  override val emptyHistoryTitle = "No Saved Rounds"
  override val emptyHistorySubtitle = "Rounds are saved automatically to your device offline."
  override val openRound = "Open / Edit"
  override val deleteRound = "Delete"
  override val deleteConfirmTitle = "Delete Round"
  override val deleteConfirmMessage = "Delete this saved round? This action cannot be undone."
  override val cancel = "Cancel"
  override val delete = "Delete"
  override val holesProgress: (Int) -> String = { n -> "$n / 18 holes" }

  override val settingsTitle = "Settings"
  override val languageSectionTitle = "Language"
  override val koreanLabel = "한국어 (Korean)"
  override val englishLabel = "English"
  override val courseEditorTitle = "Edit Course Par & S.I."
  override val courseEditorSubtitle = "Changing S.I. automatically swaps with the previous holder to keep 1–18 unique."
  override val resetDefaults = "Reset to Jaypee Greens Default"
  override val resetConfirmTitle = "Reset Course Data"
  override val resetConfirmMessage = "Restore all 18 holes to Jaypee Greens Greg Norman defaults (Par 72)?"
  override val resetAction = "Reset"
  override val parSumLabel: (Int, Int, Int) -> String = { outP, inP, totP ->
    "OUT Par $outP · IN Par $inP · Total Par $totP"
  }
  override val teeName: (TeeColor) -> String = { it.labelEn }
}

fun stringsFor(language: AppLanguage): AppStrings =
  when (language) {
    AppLanguage.KO -> KoStrings
    AppLanguage.EN -> EnStrings
  }

val LocalAppStrings = staticCompositionLocalOf<AppStrings> { KoStrings }
