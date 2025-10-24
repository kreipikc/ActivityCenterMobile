package com.kreipikc.activitycenter.domain.model

import android.graphics.Bitmap

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val usageTime: Long,
    val icon: Bitmap? = null
)
