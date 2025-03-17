package com.sample.jetpack2

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasUsageStatsPermission()) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
//        enableEdgeToEdge()
        setContent {
            UsageStatsApp()
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}

@Composable
fun UsageStatsApp() {
    val context = LocalContext.current
    var usageStatsList: List<UsageStats> by remember { mutableStateOf(getUsageStats(context)) }

    Column(
        modifier = Modifier
            .padding(start = 8.dp)
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                usageStatsList = getUsageStats(context)
            }) {
                Text("Refresh")
            }
            val tt = epochMillis2HumanTime(System.currentTimeMillis(), timeAlone = true)
            Text(
                text = "\t\tCount = ${usageStatsList.size} at $tt"
            )
        }
        UsageStatsList(usageStatsList)
    }
}

@Composable
fun UsageStatsList(usageStatsList: List<UsageStats>) {
    var pkgName by remember { mutableStateOf("") }
    LazyColumn {
        items(usageStatsList) {
            val bgColor = if (it.packageName == pkgName) Color.LightGray else Color.Unspecified
            UsageStatsItem(it, bgColor) { newValue -> pkgName = newValue }
            Log.d("TAGTAG", "UsageStatsList: ${it.packageName}")
        }
    }
}

@Composable
fun UsageStatsItem(usageStats: UsageStats, bgColor: Color, onPkgSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxSize()
            .background(bgColor)
            .clickable(onClick = { onPkgSelected(usageStats.packageName) })
    ) {
        Text(text = usageStats.packageName, color = Color.Blue)
        val totalMinutes = usageStats.totalTimeInForeground / 1000 / 60
        Text(
            text = "${epochMillis2HumanTime(usageStats.lastTimeUsed)} __ "
                    + if (totalMinutes < 1) "TinF < 1 min" else "TinF = $totalMinutes min",
            color = if (totalMinutes < 1) Color.Gray else Color.Unspecified
        )
    }
}

private fun getUsageStats(context: Context): List<UsageStats> {
    val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val endTime = System.currentTimeMillis()
    val startTime = endTime - 1000 * 60 * 60 * 24 // Dernières 24 heures
    return usageStatsManager.queryUsageStats(
        UsageStatsManager.INTERVAL_DAILY, startTime, endTime
    ).filter { it.totalTimeInForeground >= 1000 }.sortedBy { it.lastTimeUsed }
}

private fun getList(appList: List<UsageStats>): List<String> {
    val list: List<String> = appList.map { it.packageName }
    return list
}

private fun epochMillis2HumanTime(epochMillis: Long, timeAlone: Boolean = false): String {
    val pattern = if (timeAlone) "HH:mm:ss" else "yyyy/MM/dd - HH:mm:ss"
    return Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern(pattern))
}
