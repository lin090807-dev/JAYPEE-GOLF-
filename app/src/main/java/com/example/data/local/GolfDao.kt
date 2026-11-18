package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface GolfDao {

  @Query("SELECT * FROM course_holes ORDER BY holeNumber ASC")
  fun observeCourseHoles(): Flow<List<CourseHoleEntity>>

  @Query("SELECT COUNT(*) FROM course_holes")
  suspend fun countCourseHoles(): Int

  @Query("SELECT * FROM course_holes ORDER BY holeNumber ASC")
  suspend fun getCourseHolesOnce(): List<CourseHoleEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertCourseHoles(holes: List<CourseHoleEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertCourseHole(hole: CourseHoleEntity)

  @Query("SELECT * FROM app_preferences WHERE id = 1")
  fun observePreferences(): Flow<AppPreferencesEntity?>

  @Query("SELECT * FROM app_preferences WHERE id = 1")
  suspend fun getPreferencesOnce(): AppPreferencesEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertPreferences(prefs: AppPreferencesEntity)

  @Query("SELECT * FROM recent_players ORDER BY lastUsedAt DESC LIMIT 16")
  fun observeRecentPlayers(): Flow<List<RecentPlayerEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertRecentPlayers(players: List<RecentPlayerEntity>)

  @Query("DELETE FROM recent_players WHERE name = :name")
  suspend fun deleteRecentPlayer(name: String)

  @Transaction
  @Query("SELECT * FROM rounds ORDER BY dateEpochMillis DESC, updatedAt DESC")
  fun observeAllRoundsWithDetails(): Flow<List<RoundWithDetails>>

  @Transaction
  @Query("SELECT * FROM rounds WHERE id = :roundId")
  fun observeRoundWithDetails(roundId: Long): Flow<RoundWithDetails?>

  @Insert
  suspend fun insertRound(round: RoundEntity): Long

  @Query("UPDATE rounds SET updatedAt = :updatedAt WHERE id = :roundId")
  suspend fun updateRoundTimestamp(roundId: Long, updatedAt: Long)

  @Insert
  suspend fun insertRoundPlayers(players: List<RoundPlayerEntity>): List<Long>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertHoleScore(score: HoleScoreEntity)

  @Query("DELETE FROM hole_scores WHERE roundId = :roundId AND playerId = :playerId AND holeNumber = :holeNumber")
  suspend fun deleteHoleScore(roundId: Long, playerId: Long, holeNumber: Int)

  @Query("DELETE FROM rounds WHERE id = :roundId")
  suspend fun deleteRoundById(roundId: Long)
}
