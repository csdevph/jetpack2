package com.sample.jetpack2.utils

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

fun getMostUsedApps(context: Context, startTime: Long, endTime: Long): List<UsageStats> {
    val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val usageStatsList =
        usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
    return usageStatsList.sortedByDescending { it.totalTimeInForeground }
}

@Composable
fun MostUsedAppsScreen(usageStatsList: List<UsageStats>) {
    LazyColumn {
        items(usageStatsList) { usageStats ->
            Text(text = "Package: ${usageStats.packageName}, Time: ${usageStats.totalTimeInForeground}")
        }
    }
}

fun getAppUsageTime(context: Context, packageName: String): Long {
    val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val endTime = System.currentTimeMillis()
    val startTime = endTime - 1000 * 60 * 60 * 24 // Dernières 24 heures
    val usageStatsList =
        usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
    return usageStatsList.firstOrNull { it.packageName == packageName }?.totalTimeInForeground ?: 0L
}

@Composable
fun AppUsageTimeScreen(packageName: String, usageTime: Long) {
    Column {
        Text(text = "Package: $packageName")
        Text(text = "Time: $usageTime ms")
    }
}

fun getForegroundApp(context: Context): String? {
    val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val endTime = System.currentTimeMillis()
    val startTime = endTime - 1000 * 60 // Dernière minute
    val usageStatsList =
        usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
    return usageStatsList
        .filter { it.lastTimeUsed >= startTime }
        .maxByOrNull { it.lastTimeUsed }?.packageName
}

@Composable
fun ForegroundAppScreen(packageName: String?) {
    Text(text = "Foreground App: ${packageName ?: "None"}")
}
