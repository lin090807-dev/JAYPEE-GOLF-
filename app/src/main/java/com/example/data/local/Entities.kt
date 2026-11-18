package com.example.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "course_holes")
data class CourseHoleEntity(
  @PrimaryKey val holeNumber: Int,
  val par: Int,
  val strokeIndex: Int,
)

@Entity(tableName = "app_preferences")
data class AppPreferencesEntity(
  @PrimaryKey val id: Int = 1,
  val languageCode: String = "ko",
  val activeRoundId: Long? = null,
)

@Entity(tableName = "recent_players")
data class RecentPlayerEntity(
  @PrimaryKey val name: String,
  val lastHandicap: Int,
  val lastUsedAt: Long,
)

@Entity(tableName = "rounds")
data class RoundEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val dateEpochMillis: Long,
  val teeColor: String,
  val createdAt: Long,
  val updatedAt: Long,
)

@Entity(
  tableName = "round_players",
  foreignKeys = [
    ForeignKey(
      entity = RoundEntity::class,
      parentColumns = ["id"],
      childColumns = ["roundId"],
      onDelete = ForeignKey.CASCADE,
    )
  ],
  indices = [Index(value = ["roundId"])],
)
data class RoundPlayerEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val roundId: Long,
  val playerOrder: Int,
  val name: String,
  val handicap: Int,
)

@Entity(
  tableName = "hole_scores",
  primaryKeys = ["roundId", "playerId", "holeNumber"],
  foreignKeys = [
    ForeignKey(
      entity = RoundEntity::class,
      parentColumns = ["id"],
      childColumns = ["roundId"],
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = RoundPlayerEntity::class,
      parentColumns = ["id"],
      childColumns = ["playerId"],
      onDelete = ForeignKey.CASCADE,
    ),
  ],
  indices = [Index(value = ["roundId"]), Index(value = ["playerId"])],
)
data class HoleScoreEntity(
  val roundId: Long,
  val playerId: Long,
  val holeNumber: Int,
  val relativeToPar: Int,
)

data class RoundWithDetails(
  @Embedded val round: RoundEntity,
  @Relation(parentColumn = "id", entityColumn = "roundId")
  val players: List<RoundPlayerEntity>,
  @Relation(parentColumn = "id", entityColumn = "roundId")
  val scores: List<HoleScoreEntity>,
)
