package com.sample.jetpack2

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val usageStatsList = remember { mutableStateOf<List<UsageStats>>(emptyList()) }
            usageStatsList.value = getUsageStats()

            UsageStatsApp(usageStatsList.value)
        }
    }

    private fun getUsageStats(): List<UsageStats> {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000 * 60 * 60 * 24 // Dernières 24 heures
        return usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        ).filter { it.totalTimeInForeground >= 1000 }
    }

    override fun onResume() {
        super.onResume()
        if (!hasUsageStatsPermission()) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}

@Composable
fun UsageStatsApp(usageStatsList: List<UsageStats>) {
    Column {
        if (usageStatsList.isEmpty()) {
            Text(text = "Aucune donnée d'utilisation disponible")
        } else {
            UsageStatsList(usageStatsList)
        }
    }
}

@Composable
fun UsageStatsList(usageStatsList: List<UsageStats>) {
    LazyColumn {
        items(usageStatsList) { usageStats ->
            UsageStatsItem(usageStats)
        }
    }
}

@Composable
fun UsageStatsItem(usageStats: UsageStats) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "Package: ${usageStats.packageName}")
        Text(text = "Time in Foreground: ${usageStats.totalTimeInForeground / 1000} secondes")
        Text(text = "Last Time: ${usageStats.lastTimeUsed}")
    }
}
