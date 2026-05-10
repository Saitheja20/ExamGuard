package com.saitheja.examguard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_study")
data class DailyStudyEntity(
    @PrimaryKey val date: String,
    val whitelistedMinutes: Long,
    val blockedMinutes: Long
)
