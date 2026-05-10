package com.saitheja.examguard.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DailyStudyEntity::class], version = 1, exportSchema = false)
abstract class ExamGuardDatabase : RoomDatabase() {
    abstract fun dailyStudyDao(): DailyStudyDao
}
