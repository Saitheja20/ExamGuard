package com.saitheja.examguard.focus

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class DailyUsageSummary(
    val date: LocalDate,
    val whitelistedMinutes: Long,
    val blockedMinutes: Long
)

class UsageTracker(private val context: Context, private val store: FocusSettingsStore) {
    private val usageStatsManager = context.getSystemService(UsageStatsManager::class.java)

    fun collectTodaySummary(now: Instant = Instant.now()): DailyUsageSummary {
        val zone = ZoneId.systemDefault()
        val dayStart = now.atZone(zone).toLocalDate().atStartOfDay(zone).toInstant()
        val stats = usageStatsManager?.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            dayStart.toEpochMilli(),
            now.toEpochMilli()
        ).orEmpty()
        return summarize(dayStart.atZone(zone).toLocalDate(), stats, store.loadWhitelistedPackages())
    }

    internal fun summarize(date: LocalDate, stats: List<UsageStats>, whitelist: Set<String>): DailyUsageSummary {
        var whitelistMs = 0L
        var blockedMs = 0L
        stats.forEach { stat ->
            if (FocusPolicy.isAllowedPackage(stat.packageName, whitelist)) {
                whitelistMs += stat.totalTimeInForeground
            } else {
                blockedMs += stat.totalTimeInForeground
            }
        }
        return DailyUsageSummary(
            date = date,
            whitelistedMinutes = whitelistMs / 60_000,
            blockedMinutes = blockedMs / 60_000
        )
    }
}
