package com.sample.jetpack2.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.epochMillis2HumanTime(timeAlone: Boolean = false): String {
    val pattern = if (timeAlone) "HH:mm:ss" else "yyyy/MM/dd - HH:mm:ss"
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern(pattern))
}
