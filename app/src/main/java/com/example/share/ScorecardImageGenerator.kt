package com.example.share

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.content.FileProvider
import com.example.domain.model.DefaultJaypeeGreensCourse
import com.example.domain.model.GolfScoringCalculator
import com.example.domain.model.HoleConfig
import com.example.domain.model.PlayerRoundStats
import com.example.domain.model.ScoreVisualStyle
import com.example.ui.i18n.AppStrings
import com.example.ui.viewmodel.ActiveRoundUiModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.min

object ScorecardImageGenerator {

  fun shareScorecardPng(
    context: Context,
    round: ActiveRoundUiModel,
    strings: AppStrings,
  ) {
    val bitmap = renderScorecardBitmap(round, strings)
    val shareDir = File(context.cacheDir, "shared_scorecards").apply { mkdirs() }
    val outFile = File(shareDir, "jaypee_greens_round_${round.roundId}.png")
    FileOutputStream(outFile).use { stream ->
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    }
    bitmap.recycle()

    val uri = FileProvider.getUriForFile(
      context,
      "${context.packageName}.fileprovider",
      outFile,
    )
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
      type = "image/png"
      putExtra(Intent.EXTRA_STREAM, uri)
      putExtra(
        Intent.EXTRA_SUBJECT,
        "${DefaultJaypeeGreensCourse.COURSE_NAME}, ${DefaultJaypeeGreensCourse.COURSE_LOCATION}",
      )
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooser = Intent.createChooser(sendIntent, strings.shareChooserTitle)
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooser)
  }

  fun renderScorecardBitmap(
    round: ActiveRoundUiModel,
    strings: AppStrings,
  ): Bitmap {
    val width = 1440
    val margin = 48f
    val headerHeight = 200f
    val tableHeaderRowH = 54f
    val tableMetaRowH = 44f
    val playerRowH = 74f
    val nineTableH = tableHeaderRowH + tableMetaRowH * 2 + playerRowH * round.players.size
    val gapAfterHeader = 32f
    val gapBetweenNines = 36f
    val summaryHeaderH = 56f
    val summaryCardH = 128f
    val summaryGap = 18f
    val summarySectionH = summaryHeaderH + round.playerStats.size * (summaryCardH + summaryGap)
    val footerH = 64f

    val totalHeight = (
      headerHeight +
        gapAfterHeader +
        nineTableH +
        gapBetweenNines +
        nineTableH +
        gapBetweenNines +
        summarySectionH +
        footerH
      ).toInt()

    val bitmap = Bitmap.createBitmap(width, totalHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Background
    canvas.drawColor(Color.parseColor("#F3F7F4"))

    val greenDark = Color.parseColor("#0D3B26")
    val greenMid = Color.parseColor("#145336")
    val goldColor = Color.parseColor("#D4AF37")
    val borderColor = Color.parseColor("#C7D8CD")
    val inkBlack = Color.parseColor("#0D1B14")
    val inkMuted = Color.parseColor("#42594D")
    val white = Color.WHITE

    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // --- 1. Header Banner ---
    fillPaint.color = greenDark
    canvas.drawRect(0f, 0f, width.toFloat(), headerHeight, fillPaint)
    fillPaint.color = goldColor
    canvas.drawRect(0f, headerHeight - 8f, width.toFloat(), headerHeight, fillPaint)

    textPaint.color = white
    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    textPaint.textSize = 44f
    textPaint.textAlign = Paint.Align.LEFT
    canvas.drawText(
      "Jaypee Greens Golf Club, Greater Noida",
      margin,
      78f,
      textPaint,
    )

    textPaint.color = Color.parseColor("#D1E7DA")
    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    textPaint.textSize = 28f
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd (EEE)", Locale.getDefault())
    val formattedDate = dateFormatter.format(Date(round.dateEpochMillis))
    canvas.drawText(
      "Greg Norman Course · 18 Holes · Par ${round.holes.sumOf { it.par }}",
      margin,
      126f,
      textPaint,
    )

    // Date & Tee pill on right/bottom of header
    val teeLabel = strings.teeName(round.teeColor)
    textPaint.color = white
    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    textPaint.textSize = 28f
    canvas.drawText("$formattedDate   |   $teeLabel", margin, 168f, textPaint)

    // Tee Color Dot
    val teeDotX = margin + textPaint.measureText("$formattedDate   |   $teeLabel") + 28f
    fillPaint.color = android.graphics.Color.argb(
      255,
      (round.teeColor.dotColor.red * 255).toInt(),
      (round.teeColor.dotColor.green * 255).toInt(),
      (round.teeColor.dotColor.blue * 255).toInt(),
    )
    canvas.drawCircle(teeDotX, 158f, 14f, fillPaint)
    strokePaint.color = white
    strokePaint.strokeWidth = 3f
    canvas.drawCircle(teeDotX, 158f, 14f, strokePaint)

    // --- 2. OUT (1-9) Table ---
    var y = headerHeight + gapAfterHeader
    val frontHoles = round.holes.filter { it.holeNumber in 1..9 }
    y = drawNineHoleTable(
      canvas = canvas,
      startX = margin,
      startY = y,
      tableWidth = width - margin * 2,
      holes = frontHoles,
      isBackNine = false,
      round = round,
      strings = strings,
      tableHeaderRowH = tableHeaderRowH,
      tableMetaRowH = tableMetaRowH,
      playerRowH = playerRowH,
      greenDark = greenDark,
      greenMid = greenMid,
      borderColor = borderColor,
      inkBlack = inkBlack,
      inkMuted = inkMuted,
    )

    // --- 3. IN (10-18) Table ---
    y += gapBetweenNines
    val backHoles = round.holes.filter { it.holeNumber in 10..18 }
    y = drawNineHoleTable(
      canvas = canvas,
      startX = margin,
      startY = y,
      tableWidth = width - margin * 2,
      holes = backHoles,
      isBackNine = true,
      round = round,
      strings = strings,
      tableHeaderRowH = tableHeaderRowH,
      tableMetaRowH = tableMetaRowH,
      playerRowH = playerRowH,
      greenDark = greenDark,
      greenMid = greenMid,
      borderColor = borderColor,
      inkBlack = inkBlack,
      inkMuted = inkMuted,
    )

    // --- 4. Player Summary Cards ---
    y += gapBetweenNines
    textPaint.color = greenDark
    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    textPaint.textSize = 32f
    textPaint.textAlign = Paint.Align.LEFT
    canvas.drawText(
      "${strings.vsHcpHeader} & ${strings.total18SummaryLabel}",
      margin,
      y + 36f,
      textPaint,
    )
    y += summaryHeaderH

    val sortedStats = round.playerStats.sortedBy { it.netRank }
    for (stat in sortedStats) {
      drawSummaryCard(
        canvas = canvas,
        x = margin,
        y = y,
        cardWidth = width - margin * 2,
        cardHeight = summaryCardH,
        stat = stat,
        strings = strings,
      )
      y += summaryCardH + summaryGap
    }

    // Footer
    textPaint.color = inkMuted
    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    textPaint.textSize = 22f
    textPaint.textAlign = Paint.Align.CENTER
    canvas.drawText(
      "Jaypee Greens Golf Club · Greater Noida · ${strings.hcpDotsHint}",
      width / 2f,
      totalHeight - 24f,
      textPaint,
    )

    return bitmap
  }

  private fun drawNineHoleTable(
    canvas: Canvas,
    startX: Float,
    startY: Float,
    tableWidth: Float,
    holes: List<HoleConfig>,
    isBackNine: Boolean,
    round: ActiveRoundUiModel,
    strings: AppStrings,
    tableHeaderRowH: Float,
    tableMetaRowH: Float,
    playerRowH: Float,
    greenDark: Int,
    greenMid: Int,
    borderColor: Int,
    inkBlack: Int,
    inkMuted: Int,
  ): Float {
    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    val nameColW = 240f
    val totalColsAfterHoles = if (isBackNine) 2 else 1
    val totalColW = 115f
    val holeColW = (tableWidth - nameColW - totalColW * totalColsAfterHoles) / 9f

    var y = startY
    val tableTotalH = tableHeaderRowH + tableMetaRowH * 2 + playerRowH * round.players.size

    // Card background
    fillPaint.color = Color.WHITE
    val cardRect = RectF(startX, startY, startX + tableWidth, startY + tableTotalH)
    canvas.drawRect(cardRect, fillPaint)

    // Row 1: Hole Numbers
    fillPaint.color = greenDark
    canvas.drawRect(startX, y, startX + tableWidth, y + tableHeaderRowH, fillPaint)
    textPaint.color = Color.WHITE
    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    textPaint.textSize = 24f
    textPaint.textAlign = Paint.Align.LEFT
    canvas.drawText(strings.holeCol, startX + 16f, y + 36f, textPaint)

    textPaint.textAlign = Paint.Align.CENTER
    holes.forEachIndexed { idx, hole ->
      val cx = startX + nameColW + idx * holeColW + holeColW / 2f
      canvas.drawText(hole.holeNumber.toString(), cx, y + 36f, textPaint)
    }

    val sumColX = startX + nameColW + 9 * holeColW
    canvas.drawText(
      if (isBackNine) strings.inCol else strings.outCol,
      sumColX + totalColW / 2f,
      y + 36f,
      textPaint,
    )
    if (isBackNine) {
      canvas.drawText(
        strings.total18Col,
        sumColX + totalColW + totalColW / 2f,
        y + 36f,
        textPaint,
      )
    }
    y += tableHeaderRowH

    // Row 2: PAR
    fillPaint.color = Color.parseColor("#E5EFE8")
    canvas.drawRect(startX, y, startX + tableWidth, y + tableMetaRowH, fillPaint)
    textPaint.color = greenDark
    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    textPaint.textSize = 21f
    textPaint.textAlign = Paint.Align.LEFT
    canvas.drawText(strings.parCol, startX + 16f, y + 30f, textPaint)
    textPaint.textAlign = Paint.Align.CENTER
    holes.forEachIndexed { idx, hole ->
      val cx = startX + nameColW + idx * holeColW + holeColW / 2f
      canvas.drawText(hole.par.toString(), cx, y + 30f, textPaint)
    }
    val nineParSum = holes.sumOf { it.par }
    canvas.drawText(nineParSum.toString(), sumColX + totalColW / 2f, y + 30f, textPaint)
    if (isBackNine) {
      val totalParSum = round.holes.sumOf { it.par }
      canvas.drawText(totalParSum.toString(), sumColX + totalColW + totalColW / 2f, y + 30f, textPaint)
    }
    y += tableMetaRowH

    // Row 3: S.I.
    fillPaint.color = Color.parseColor("#F5F8F6")
    canvas.drawRect(startX, y, startX + tableWidth, y + tableMetaRowH, fillPaint)
    textPaint.color = inkMuted
    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    textPaint.textSize = 20f
    textPaint.textAlign = Paint.Align.LEFT
    canvas.drawText(strings.siCol, startX + 16f, y + 29f, textPaint)
    textPaint.textAlign = Paint.Align.CENTER
    holes.forEachIndexed { idx, hole ->
      val cx = startX + nameColW + idx * holeColW + holeColW / 2f
      canvas.drawText(hole.strokeIndex.toString(), cx, y + 29f, textPaint)
    }
    canvas.drawText("-", sumColX + totalColW / 2f, y + 29f, textPaint)
    if (isBackNine) {
      canvas.drawText("-", sumColX + totalColW + totalColW / 2f, y + 29f, textPaint)
    }
    y += tableMetaRowH

    // Player Rows
    val statsById = round.playerStats.associateBy { it.player.id }
    round.players.forEachIndexed { pIdx, player ->
      val rowTop = y
      val rowBottom = y + playerRowH
      fillPaint.color = if (pIdx % 2 == 0) Color.WHITE else Color.parseColor("#FAFDFB")
      canvas.drawRect(startX, rowTop, startX + tableWidth, rowBottom, fillPaint)

      val stat = statsById[player.id]
      val playerScores = round.scoresByPlayer[player.id].orEmpty()

      // Player Name & Rank
      textPaint.color = inkBlack
      textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      textPaint.textSize = 22f
      textPaint.textAlign = Paint.Align.LEFT
      val rankStr = if (stat != null && stat.totalPlayedCount > 0) "${strings.rankBadge(stat.netRank)} " else ""
      val displayName = (rankStr + player.name).take(14)
      canvas.drawText(displayName, startX + 14f, rowTop + 34f, textPaint)

      textPaint.color = inkMuted
      textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
      textPaint.textSize = 18f
      canvas.drawText("${strings.hcpShort} ${player.handicap}", startX + 14f, rowTop + 58f, textPaint)

      // 9 Holes
      holes.forEachIndexed { idx, hole ->
        val cellLeft = startX + nameColW + idx * holeColW
        val cellCenterX = cellLeft + holeColW / 2f
        val strokesReceived = GolfScoringCalculator.strokesAllocated(player.handicap, hole.strokeIndex)
        val relScore = playerScores[hole.holeNumber]

        // Handicap Dots in top-right corner
        if (strokesReceived > 0) {
          fillPaint.color = greenMid
          for (dotIdx in 0 until min(strokesReceived, 3)) {
            canvas.drawCircle(cellLeft + holeColW - 12f - dotIdx * 10f, rowTop + 12f, 3.8f, fillPaint)
          }
        }

        if (relScore != null) {
          val gross = GolfScoringCalculator.grossScore(hole.par, relScore)
          val style = GolfScoringCalculator.scoreVisualStyle(relScore)
          val boxSize = 40f
          val halfBox = boxSize / 2f
          val shapeCenterY = rowTop + 33f

          when (style) {
            ScoreVisualStyle.EAGLE_OR_BETTER -> {
              fillPaint.color = Color.parseColor("#F4C430")
              canvas.drawRoundRect(
                cellCenterX - halfBox,
                shapeCenterY - halfBox,
                cellCenterX + halfBox,
                shapeCenterY + halfBox,
                10f,
                10f,
                fillPaint,
              )
              strokePaint.color = Color.parseColor("#9A7200")
              strokePaint.strokeWidth = 2.5f
              canvas.drawRoundRect(
                cellCenterX - halfBox,
                shapeCenterY - halfBox,
                cellCenterX + halfBox,
                shapeCenterY + halfBox,
                10f,
                10f,
                strokePaint,
              )
            }
            ScoreVisualStyle.BIRDIE -> {
              strokePaint.color = Color.parseColor("#D32F2F")
              strokePaint.strokeWidth = 3f
              canvas.drawCircle(cellCenterX, shapeCenterY, halfBox, strokePaint)
            }
            ScoreVisualStyle.PAR -> Unit
            ScoreVisualStyle.BOGEY -> {
              strokePaint.color = Color.parseColor("#1976D2")
              strokePaint.strokeWidth = 3f
              canvas.drawRect(
                cellCenterX - halfBox,
                shapeCenterY - halfBox,
                cellCenterX + halfBox,
                shapeCenterY + halfBox,
                strokePaint,
              )
            }
            ScoreVisualStyle.DOUBLE_OR_WORSE -> {
              strokePaint.color = Color.parseColor("#0D47A1")
              strokePaint.strokeWidth = 2.5f
              canvas.drawRect(
                cellCenterX - halfBox,
                shapeCenterY - halfBox,
                cellCenterX + halfBox,
                shapeCenterY + halfBox,
                strokePaint,
              )
              val inner = halfBox - 5.5f
              canvas.drawRect(
                cellCenterX - inner,
                shapeCenterY - inner,
                cellCenterX + inner,
                shapeCenterY + inner,
                strokePaint,
              )
            }
          }

          textPaint.color = if (style == ScoreVisualStyle.EAGLE_OR_BETTER) Color.parseColor("#1E1600") else inkBlack
          textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
          textPaint.textSize = 25f
          textPaint.textAlign = Paint.Align.CENTER
          canvas.drawText(gross.toString(), cellCenterX, shapeCenterY + 9f, textPaint)

          textPaint.color = inkMuted
          textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
          textPaint.textSize = 15f
          val relStr = if (gross == 1) "HIO" else GolfScoringCalculator.formatRelativeToPar(relScore)
          canvas.drawText(relStr, cellCenterX, rowBottom - 7f, textPaint)
        } else {
          textPaint.color = Color.parseColor("#CBD5E1")
          textPaint.textSize = 22f
          textPaint.textAlign = Paint.Align.CENTER
          canvas.drawText("-", cellCenterX, rowTop + 42f, textPaint)
        }
      }

      // 9H Total Cell
      val ninePlayed = if (isBackNine) stat?.backPlayedCount ?: 0 else stat?.frontPlayedCount ?: 0
      val nineGross = if (isBackNine) stat?.backGross ?: 0 else stat?.frontGross ?: 0
      val nineToPar = if (isBackNine) stat?.backToPar ?: 0 else stat?.frontToPar ?: 0
      textPaint.textAlign = Paint.Align.CENTER
      if (ninePlayed > 0) {
        textPaint.color = inkBlack
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 25f
        canvas.drawText(nineGross.toString(), sumColX + totalColW / 2f, rowTop + 37f, textPaint)

        textPaint.color = inkMuted
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textPaint.textSize = 17f
        canvas.drawText(
          GolfScoringCalculator.formatRelativeToPar(nineToPar),
          sumColX + totalColW / 2f,
          rowBottom - 10f,
          textPaint,
        )
      } else {
        textPaint.color = Color.parseColor("#CBD5E1")
        textPaint.textSize = 22f
        canvas.drawText("-", sumColX + totalColW / 2f, rowTop + 42f, textPaint)
      }

      // 18H Total Cell (on Back 9 table)
      if (isBackNine) {
        val totX = sumColX + totalColW
        val totPlayed = stat?.totalPlayedCount ?: 0
        if (totPlayed > 0 && stat != null) {
          textPaint.color = greenDark
          textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
          textPaint.textSize = 26f
          canvas.drawText(stat.totalGross.toString(), totX + totalColW / 2f, rowTop + 37f, textPaint)

          textPaint.color = inkMuted
          textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
          textPaint.textSize = 17f
          canvas.drawText(
            GolfScoringCalculator.formatRelativeToPar(stat.totalToPar),
            totX + totalColW / 2f,
            rowBottom - 10f,
            textPaint,
          )
        } else {
          textPaint.color = Color.parseColor("#CBD5E1")
          textPaint.textSize = 22f
          canvas.drawText("-", totX + totalColW / 2f, rowTop + 42f, textPaint)
        }
      }

      y += playerRowH
    }

    // Grid Lines
    strokePaint.color = borderColor
    strokePaint.strokeWidth = 2f
    canvas.drawRect(cardRect, strokePaint)

    // Vertical dividers
    canvas.drawLine(startX + nameColW, startY, startX + nameColW, y, strokePaint)
    for (i in 1..9) {
      val vx = startX + nameColW + i * holeColW
      canvas.drawLine(vx, startY, vx, y, strokePaint)
    }
    if (isBackNine) {
      val vx = sumColX + totalColW
      canvas.drawLine(vx, startY, vx, y, strokePaint)
    }
    // Horizontal dividers
    var hy = startY + tableHeaderRowH
    canvas.drawLine(startX, hy, startX + tableWidth, hy, strokePaint)
    hy += tableMetaRowH
    canvas.drawLine(startX, hy, startX + tableWidth, hy, strokePaint)
    hy += tableMetaRowH
    canvas.drawLine(startX, hy, startX + tableWidth, hy, strokePaint)
    repeat(round.players.size - 1) {
      hy += playerRowH
      canvas.drawLine(startX, hy, startX + tableWidth, hy, strokePaint)
    }

    return y
  }

  private fun drawSummaryCard(
    canvas: Canvas,
    x: Float,
    y: Float,
    cardWidth: Float,
    cardHeight: Float,
    stat: PlayerRoundStats,
    strings: AppStrings,
  ) {
    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    val rect = RectF(x, y, x + cardWidth, y + cardHeight)
    fillPaint.color = Color.WHITE
    canvas.drawRoundRect(rect, 18f, 18f, fillPaint)
    strokePaint.color = Color.parseColor("#C7D8CD")
    strokePaint.strokeWidth = 2f
    canvas.drawRoundRect(rect, 18f, 18f, strokePaint)

    // Left: Rank + Name + HCP
    textPaint.textAlign = Paint.Align.LEFT
    textPaint.color = Color.parseColor("#0D3B26")
    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    textPaint.textSize = 30f
    val rankLabel = if (stat.totalPlayedCount > 0) "${strings.rankBadge(stat.netRank)}  " else ""
    canvas.drawText("$rankLabel${stat.player.name}", x + 26f, y + 52f, textPaint)

    textPaint.color = Color.parseColor("#42594D")
    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    textPaint.textSize = 22f
    val thru = if (stat.totalPlayedCount > 0) " · ${strings.thruHoles(stat.totalPlayedCount)}" else ""
    canvas.drawText("${strings.hcpShort} ${stat.player.handicap}$thru", x + 26f, y + 92f, textPaint)

    // Middle: OUT / IN / Gross / Net
    textPaint.textAlign = Paint.Align.LEFT
    textPaint.color = Color.parseColor("#0D1B14")
    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    textPaint.textSize = 25f
    val grossVal = if (stat.totalPlayedCount > 0) "${stat.totalGross} (${GolfScoringCalculator.formatRelativeToPar(stat.totalToPar)})" else "-"
    val netVal = if (stat.totalPlayedCount > 0) "${stat.netScore}" else "-"
    canvas.drawText(
      "OUT ${if (stat.frontPlayedCount > 0) stat.frontGross else "-"}  ·  IN ${if (stat.backPlayedCount > 0) stat.backGross else "-"}",
      x + 430f,
      y + 50f,
      textPaint,
    )
    textPaint.textSize = 24f
    canvas.drawText(
      "${strings.grossLabel} $grossVal   |   ${strings.netLabel} $netVal",
      x + 430f,
      y + 92f,
      textPaint,
    )

    // Right: Prominent vs Handicap Badge
    val badgeW = 310f
    val badgeH = 88f
    val badgeX = x + cardWidth - badgeW - 22f
    val badgeY = y + (cardHeight - badgeH) / 2f
    val (bgColor, fgColor) = when {
      stat.totalPlayedCount == 0 -> Color.parseColor("#E2E8F0") to Color.parseColor("#475569")
      stat.vsHandicap < 0 -> Color.parseColor("#DCFCE7") to Color.parseColor("#15803D")
      stat.vsHandicap > 0 -> Color.parseColor("#FEE2E2") to Color.parseColor("#C62828")
      else -> Color.parseColor("#E2E8F0") to Color.parseColor("#475569")
    }
    fillPaint.color = bgColor
    canvas.drawRoundRect(RectF(badgeX, badgeY, badgeX + badgeW, badgeY + badgeH), 14f, 14f, fillPaint)

    textPaint.textAlign = Paint.Align.CENTER
    textPaint.color = fgColor
    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    textPaint.textSize = 30f
    val headline = if (stat.totalPlayedCount > 0) strings.vsHcpBadgeText(stat.vsHandicap) else "-"
    canvas.drawText(headline, badgeX + badgeW / 2f, badgeY + 42f, textPaint)

    if (stat.totalPlayedCount > 0) {
      textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
      textPaint.textSize = 18f
      canvas.drawText(
        strings.vsHcpSentence(stat.vsHandicap),
        badgeX + badgeW / 2f,
        badgeY + 72f,
        textPaint,
      )
    }
  }
}
