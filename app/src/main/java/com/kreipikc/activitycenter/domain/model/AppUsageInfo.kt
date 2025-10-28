package com.kreipikc.activitycenter.domain.model

import android.graphics.drawable.Drawable

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val usageTime: Long,
    val lastUsedTime: Long,
    val icon: Drawable? = null
)
