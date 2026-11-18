package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    CourseHoleEntity::class,
    AppPreferencesEntity::class,
    RecentPlayerEntity::class,
    RoundEntity::class,
    RoundPlayerEntity::class,
    HoleScoreEntity::class,
  ],
  version = 1,
  exportSchema = false,
)
abstract class GolfDatabase : RoomDatabase() {
  abstract fun golfDao(): GolfDao

  companion object {
    @Volatile private var INSTANCE: GolfDatabase? = null

    fun getInstance(context: Context): GolfDatabase {
      return INSTANCE ?: synchronized(this) {
        INSTANCE ?: Room.databaseBuilder(
          context.applicationContext,
          GolfDatabase::class.java,
          "jaypee_greens_golf.db",
        ).build().also { INSTANCE = it }
      }
    }
  }
}
