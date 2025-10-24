package com.kreipikc.activitycenter.domain.utils

import java.util.concurrent.TimeUnit

object TimeFormatter {
    fun formatLastUsed(lastUsed: Long): String {
        val diff = System.currentTimeMillis() - lastUsed
        val hours = TimeUnit.MILLISECONDS.toHours(diff)

        lateinit var lastTimeUsed: String
        if (hours < 1) {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - lastUsed)
            lastTimeUsed = "$minutes мин. назад"
        }
        else if (hours < 24) {
            lastTimeUsed = "$hours часов назад"
        }
        else {
            lastTimeUsed = "Более суток назад"
        }
        return lastTimeUsed
    }

    fun formatDetailedTime(milliseconds: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60

        return when {
            hours > 0 -> "${hours}ч ${minutes}м"
            minutes > 0 -> "${minutes}м"
            else -> "${seconds}с"
        }
    }
}