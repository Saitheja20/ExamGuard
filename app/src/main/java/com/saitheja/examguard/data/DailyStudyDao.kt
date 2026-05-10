package com.saitheja.examguard.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DailyStudyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: DailyStudyEntity)

    @Query("SELECT * FROM daily_study ORDER BY date DESC LIMIT 7")
    suspend fun lastWeek(): List<DailyStudyEntity>
}
